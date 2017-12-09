package com.benpankow.pipeline.data;

import android.content.Context;
import android.util.Base64;

import com.benpankow.pipeline.helper.EncryptionHelper;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

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
        return clone;
    }


    public String decrypt(Context context) {
        byte[][] encryptedMessageData = new byte[][] {
                Base64.decode(text, Base64.DEFAULT),
                Base64.decode(key, Base64.DEFAULT)
        };
        try {
            byte[] decryptedMessage = EncryptionHelper.decrypt(
                    encryptedMessageData,
                    EncryptionHelper.getPrivateKey(context)
            );
            return new String(decryptedMessage);

        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException
                | BadPaddingException | IllegalBlockSizeException | KeyStoreException
                | CertificateException | NoSuchProviderException | IOException
                | InvalidAlgorithmParameterException | UnrecoverableEntryException e) {
            e.printStackTrace();
        }

        return null;
    }
}
