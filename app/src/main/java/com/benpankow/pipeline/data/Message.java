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

    public boolean sentByCurrentUser() {
        return FirebaseAuth.getInstance().getUid().equals(senderUid);
    }

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

    public Message clone() {
        Message clone = new Message();
        clone.senderUid = senderUid;
        clone.text = text;
        clone.timestamp = timestamp;
        clone.signature = signature;
        return clone;
    }


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

    public void sign(Context context) throws CertificateException, NoSuchAlgorithmException,
            KeyStoreException, UnrecoverableEntryException, NoSuchProviderException,
            InvalidAlgorithmParameterException, IOException, IllegalBlockSizeException,
            InvalidKeyException, NoSuchPaddingException, BadPaddingException {
        byte[] signature =
                EncryptionHelper.sign(text.getBytes(), EncryptionHelper.getPrivateKey(context));
        Log.w("TAG", "sign: signed!" + Base64.encodeToString(signature, Base64.DEFAULT));
        this.signature = Base64.encodeToString(signature, Base64.DEFAULT);
    }

    public void checkSignature(final String decryptedMessage, final Consumer<Boolean> callback) {
        DatabaseHelper.getUser(senderUid, new Consumer<User>() {
            @Override
            public void accept(User user) {
                byte[] encodedKey = Base64.decode(user.publicKey, Base64.DEFAULT);
                X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(encodedKey);
                try {
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
}
