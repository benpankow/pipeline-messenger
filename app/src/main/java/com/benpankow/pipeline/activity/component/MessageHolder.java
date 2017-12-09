package com.benpankow.pipeline.activity.component;

import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
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

/**
 * Created by Ben Pankow on 12/2/17.
 *
 * A RecyclerView ViewHolder corresponding to a Message
 */

public class MessageHolder extends RecyclerView.ViewHolder {

    private final View ivMain;
    private final TextView tvMessageText;

    private Message targetMessage;

    public MessageHolder(final View itemView) {
        super(itemView);
        this.ivMain = itemView;
        this.tvMessageText = itemView.findViewById(R.id.tv_message_text);

    }

    public void bindMessage(Message model) {
        targetMessage = model;

        tvMessageText.setText(model.decrypt(itemView.getContext()));
    }
}
