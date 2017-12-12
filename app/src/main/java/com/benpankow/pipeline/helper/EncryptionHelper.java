package com.benpankow.pipeline.helper;

import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;


/**
 * Created by Ben Pankow on 11/15/17.
 *
 * Utilities for generating keys, encrypting bytes of data
 */
public class EncryptionHelper {

    private static final String TAG = EncryptionHelper.class.getSimpleName();

    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String MSG_KEY_ALIAS = "MsgKey";
    private static final String RSA = "RSA/ECB/PKCS1Padding";
    private static final String AES = "AES";
    private static final String MD5 = "MD5";

    /**
     * Generates a RSA keypair for the given user, updating the user's public key in the
     * Firebase database and placing the private key in the Android KeyStore
     *
     * @param keyStore Instance of Android's keystore to store the private key in
     * @param context Application context to use in order to build the KeyPairGeneratorSpec
     * @param user Firebase user, used to update Firebase database
     */
    public static void generateKeyPair(KeyStore keyStore, Context context, FirebaseUser user)
            throws NoSuchAlgorithmException, NoSuchProviderException, IOException,
            CertificateException, KeyStoreException, InvalidAlgorithmParameterException {

        String alias = getAlias(user);

        if (!keyStore.containsAlias(alias)) {
            KeyPairGenerator rsaKeyGen = KeyPairGenerator.getInstance(
                    "RSA",
                    ANDROID_KEY_STORE);

            // Snippet of below code used to set up RSA key generator on Android API version 18
            // https://www.programcreek.com/java-api-examples/index.php?api=android.security.KeyPairGeneratorSpec

            Calendar cal = Calendar.getInstance();
            Date now = cal.getTime();
            cal.add(Calendar.YEAR, 1);
            Date end = cal.getTime();

            // Not sure why API versions <= 23 need all this extra stuff but oh well
            rsaKeyGen.initialize(
                    new KeyPairGeneratorSpec.Builder(context)
                            .setAlias(alias)
                            .setStartDate(now)
                            .setEndDate(end)
                            .setSerialNumber(BigInteger.valueOf(1))
                            .setSubject(new X500Principal("CN=test1"))
                            .build()
            );

            // Generate RSA keypair, encode public key with Base64 and update in DB
            // https://stackoverflow.com/a/12039611
            KeyPair rsaKey = rsaKeyGen.generateKeyPair();
            String stringPublicKey
                    = Base64.encodeToString(rsaKey.getPublic().getEncoded(), Base64.DEFAULT);
            DatabaseHelper.updatePublicKey(user.getUid(), stringPublicKey);
        }
    }

    /**
     * Gets the Android keystore for this device
     */
    public static KeyStore getKeystore() throws KeyStoreException, CertificateException,
            NoSuchAlgorithmException, IOException {
        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        keyStore.load(null);
        return keyStore;
    }

    /**
     * Gets the private RSA key for the logged in user, or generates one if none has been created.
     * Returns null if the user is not logged in.
     *
     * @param context Application context to use in order to build the KeyPairGeneratorSpec
     */
    public static PrivateKey getPrivateKey(Context context) throws KeyStoreException,
            CertificateException, NoSuchAlgorithmException, IOException,
            NoSuchProviderException, InvalidAlgorithmParameterException,
            UnrecoverableEntryException {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            KeyStore keyStore = getKeystore();

            String alias = getAlias(user);

            if (!keyStore.containsAlias(alias)) {
                generateKeyPair(keyStore, context, user);
            }
            KeyStore.Entry entry = keyStore.getEntry(alias, null);
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) entry;
            return privateKeyEntry.getPrivateKey();
        }
        return null;
    }

    /**
     * Produces the alias that a user's private key will be stored under
     *
     * @param user The user whose key we're storing
     * @return A string name for the alias that a private key will be stored under
     */
    private static String getAlias(FirebaseUser user) {
        return MSG_KEY_ALIAS + ":" + user.getUid();
    }

    /**
     * Generates a symmetric AES key, useful for encrypting long strings of bytes
     *
     * @return The AES key
     */
    private static SecretKey generateSymmetricKey() throws NoSuchAlgorithmException {
        KeyGenerator aesKeyGen = KeyGenerator.getInstance("AES");
        aesKeyGen.init(128);
        return aesKeyGen.generateKey();
    }

    /**
     * Encrypts a series of bytes with the intention of sending to another user. Only that user
     * can unencrypt the message with their own private key. Uses a combination of RSA + AES in a
     * similar fashion to OpenPGP.
     *
     * @param message The message to encrypt
     * @param recipientPublicKey The public key of the recipient of the message
     * @return A two-dimensional byte array with two subarrays, the first is the encrypted
     * symmetric key, the second the encrypted message
     */
    public static byte[][] encrypt(byte[] message, PublicKey recipientPublicKey)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        // Generates a fresh symmetric key for each message. This is used to encrypt the message
        // itself.
        SecretKey symmetricKey = generateSymmetricKey();
        Cipher aesCipher = Cipher.getInstance(AES);
        aesCipher.init(Cipher.ENCRYPT_MODE, symmetricKey);
        byte[] encryptedMessage = aesCipher.doFinal(message);

        // We then encrypt that AES key with the recipient's public key, since RSA is best suited
        // for encrypting smaller things.
        Cipher rsaCipher = Cipher.getInstance(RSA);
        rsaCipher.init(Cipher.ENCRYPT_MODE, recipientPublicKey);
        byte[] encryptedSymmetricKey = rsaCipher.doFinal(symmetricKey.getEncoded());

        // Return a pair of byte arrays, representing the encrypted symmetric key and encrypted
        // message
        return new byte[][] { encryptedSymmetricKey, encryptedMessage };
    }

    /**
     * Decrypts a series of bytes encrypted using the encrypt method using a private key. See
     * encrypt for details.
     *
     * @param encryptedMessage The message to decrypt
     * @param myPrivateKey The user's private key, to decrypt the message
     * @return A byte array representing the decrypted message
     */
    public static byte[] decrypt(byte[][] encryptedMessage, PrivateKey myPrivateKey)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher rsaCipher = Cipher.getInstance(RSA);
        rsaCipher.init(Cipher.DECRYPT_MODE, myPrivateKey);
        byte[] symmetricKeyBytes = rsaCipher.doFinal(encryptedMessage[0]);

        // Load symmetric key from bytes
        SecretKey symmetricKey = new SecretKeySpec(
                symmetricKeyBytes,
                0,
                symmetricKeyBytes.length,
                AES
        );

        // Decrypt message with symmetric key
        Cipher aesCipher = Cipher.getInstance(AES);
        aesCipher.init(Cipher.DECRYPT_MODE, symmetricKey);
        return aesCipher.doFinal(encryptedMessage[1]);
    }

    /**
     * Signs a series of bytes using the user's private key
     *
     * @param message The message to sign
     * @param myPrivateKey The user's private key, to sign the message
     * @return A byte array representing the signed message
     */
    public static byte[] sign(byte[] message, PrivateKey myPrivateKey) throws
            NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException,
            NoSuchPaddingException, InvalidKeyException {
        MessageDigest md = MessageDigest.getInstance(MD5);
        byte[] messageDigest = md.digest(message);

        Cipher rsaCipher = Cipher.getInstance(RSA);
        rsaCipher.init(Cipher.ENCRYPT_MODE, myPrivateKey);
        byte[] signedDigest = rsaCipher.doFinal(messageDigest);
        return signedDigest;
    }

    /**
     * Verifies a message signature with a received message.
     *
     * @param message The message data to check the signature of
     * @param signedDigest The signed message digest provided alongside the message
     * @param senderPublicKey The public key of the sender
     * @return A boolean representing whether the signature was verified or not
     */
    public static boolean verifySignature(byte[] message, byte[] signedDigest, PublicKey senderPublicKey)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        MessageDigest md = MessageDigest.getInstance(MD5);
        byte[] messageDigest = md.digest(message);

        Cipher rsaCipher = Cipher.getInstance(RSA);
        rsaCipher.init(Cipher.DECRYPT_MODE, senderPublicKey);
        byte[] decryptedDigest = rsaCipher.doFinal(signedDigest);

        return Arrays.equals(messageDigest, decryptedDigest);
    }
}
