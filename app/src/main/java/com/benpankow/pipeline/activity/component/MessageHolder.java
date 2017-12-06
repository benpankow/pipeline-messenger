package com.benpankow.pipeline.activity.component;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.benpankow.pipeline.R;
import com.benpankow.pipeline.data.Message;

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
        tvMessageText.setText(model.text);
    }
}
