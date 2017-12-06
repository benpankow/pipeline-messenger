package com.benpankow.pipeline.activity.component;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.benpankow.pipeline.R;
import com.benpankow.pipeline.data.Conversation;

import java8.util.function.Consumer;

/**
 * Created by Ben Pankow on 12/2/17.
 *
 * A RecyclerView ViewHolder corresponding to a Conversation
 */

public class ConversationHolder extends RecyclerView.ViewHolder {

    private final View ivMain;
    private final TextView tvTitle;
    private Conversation conversation;

    public ConversationHolder(View itemView) {
        super(itemView);
        this.ivMain = itemView;
        this.tvTitle = itemView.findViewById(R.id.tv_conversation_title);
    }

    public void bindConversation(Conversation model) {
        conversation = model;
        model.getTitle(new Consumer<String>() {
               @Override
               public void accept(String s) {
                   tvTitle.setText(s);
               }
        });
    }
}
