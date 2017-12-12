package com.benpankow.pipeline.activity.component;

import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.benpankow.pipeline.R;
import com.benpankow.pipeline.data.Message;
import com.benpankow.pipeline.helper.EncryptionHelper;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java8.util.function.Consumer;

/**
 * Created by Ben Pankow on 12/2/17.
 *
 * A RecyclerView ViewHolder corresponding to a Message
 */
public class MessageHolder extends RecyclerView.ViewHolder {

    private final View ivMain;
    private final TextView tvMessageText;
    private final ImageView ivVerified;

    private Message targetMessage;

    public MessageHolder(final View itemView) {
        super(itemView);
        this.ivMain = itemView;
        this.tvMessageText = itemView.findViewById(R.id.tv_message_text);
        this.ivVerified = itemView.findViewById(R.id.iv_verified);
    }

    public void bindMessage(Message model) {
        targetMessage = model;

        // Decrypt given message
        final String decryptedMessage = model.decrypt(itemView.getContext());
        tvMessageText.setText(decryptedMessage);

        // If message is from another user, attempt to verify the message's hash
        if (ivVerified != null) {
            ivVerified.setVisibility(View.INVISIBLE);
            if (decryptedMessage != null) {
                model.checkSignature(decryptedMessage, new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean accepted) {
                        if (accepted) {
                            ivVerified.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        }



    }
}
