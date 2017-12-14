package com.benpankow.pipeline.data;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.benpankow.pipeline.helper.DatabaseHelper;
import com.benpankow.pipeline.helper.EncryptionHelper;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java8.util.function.Consumer;

/**
 * Created by Ben Pankow on 12/5/17.
 *
 * Stores data relating to a specific message
 */

public class Message {
    public String senderUid;
    public String text;
    public Object timestamp;
    public String key;
    public String signature;

    public Message() {}

    public Message(String senderUid, String text, Object timestamp, String key, String signature) {
        this.senderUid = senderUid;
        this.text = text;
        this.timestamp = timestamp;
        this.key = key;
        this.signature = signature;
    }

    public Message(String senderUid, String text, Object timestamp) {
        this(senderUid, text, timestamp, null, null);
    }


    /**
     * Whether or not this message was sent by the current user
     *
     * @return Whether the user sent this message
     */
    public boolean sentByCurrentUser() {
        String uid = FirebaseAuth.getInstance().getUid();
        return uid != null && uid.equals(senderUid);
    }

    /**
     * Get the Date this conversation was sent
     *
     * @return The last updated Date
     */
    public Date getDate(){
        if (timestamp == null) {
            return null;
        }
        if (timestamp instanceof Long) {
            return new Date((long) timestamp);
        } else {
            return null;
        }
    }

    /**
     * Clones this message
     *
     * @return A copy of this message
     */
    public Message clone() {
        return new Message(senderUid, text, timestamp, key, signature);
    }

    /**
     * Attempts to decrypt this message - heavy lifting done in EncryptionHelper
     *
     * @param context The context this is being executed from
     * @return The plaintext of the message, or null if decryption fails
     */
    public String decrypt(Context context) {
        try {
            byte[][] encryptedMessageData = new byte[][] {
                    Base64.decode(text, Base64.DEFAULT),
                    Base64.decode(key, Base64.DEFAULT)
            };
            byte[] decryptedMessage = EncryptionHelper.decrypt(
                    encryptedMessageData,
                    EncryptionHelper.getPrivateKey(context)
            );
            return new String(decryptedMessage);

        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException
                | BadPaddingException | IllegalBlockSizeException | KeyStoreException
                | CertificateException | NoSuchProviderException | IOException
                | InvalidAlgorithmParameterException | UnrecoverableEntryException
                | IllegalArgumentException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Sets the internal signature of this message, signing it with the user's private key
     *
     * @param context The context this is being executed from
     */
    public void sign(Context context) throws CertificateException, NoSuchAlgorithmException,
            KeyStoreException, UnrecoverableEntryException, NoSuchProviderException,
            InvalidAlgorithmParameterException, IOException, IllegalBlockSizeException,
            InvalidKeyException, NoSuchPaddingException, BadPaddingException {
        byte[] signature =
                EncryptionHelper.sign(text.getBytes(), EncryptionHelper.getPrivateKey(context));
        this.signature = Base64.encodeToString(signature, Base64.DEFAULT);
    }


    /**
     * Verifies the signature on this message is valid
     *
     * @param decryptedMessage The decrypted version of this message
     * @param callback A callback that takes a boolean, whether the signature was valid
     */
    public void checkSignature(final String decryptedMessage, final Consumer<Boolean> callback) {
        DatabaseHelper.getUser(senderUid, new Consumer<User>() {
            @Override
            public void accept(User user) {
                try {
                    // Decodes the sender's public key
                    byte[] encodedKey = Base64.decode(user.getPublicKey(), Base64.DEFAULT);
                    X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(encodedKey);
                    KeyFactory rsaFactory = KeyFactory.getInstance("RSA");
                    PublicKey publicKey = rsaFactory.generatePublic(X509publicKey);

                    boolean result = EncryptionHelper.verifySignature(
                            decryptedMessage.getBytes(),
                            Base64.decode(signature, Base64.DEFAULT),
                            publicKey
                    );
                    callback.accept(result);
                } catch (InvalidKeySpecException | NoSuchAlgorithmException
                        | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException
                        | InvalidKeyException e) {
                    e.printStackTrace();
                    callback.accept(false);
                }
            }
        });
    }

    public String getSenderUid() {
        return senderUid;
    }

    public String getText() {
        return text;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public String getKey() {
        return key;
    }

    public String getSignature() {
        return signature;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
