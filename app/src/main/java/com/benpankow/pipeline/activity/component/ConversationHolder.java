package com.benpankow.pipeline.activity.component;

import android.content.Intent;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.benpankow.pipeline.R;
import com.benpankow.pipeline.activity.ConversationListActivity;
import com.benpankow.pipeline.activity.MessageActivity;
import com.benpankow.pipeline.data.Conversation;
import com.benpankow.pipeline.data.User;
import com.benpankow.pipeline.helper.AuthenticationHelper;
import com.benpankow.pipeline.helper.DatabaseHelper;

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
        this.tvTitle = itemView.findViewById(R.id.tv_user_nickname);

        // On click open messages
        this.ivMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View itemView) {
                Intent convoListActivity =
                        new Intent(itemView.getContext(), MessageActivity.class);
                convoListActivity.putExtra("convoid", conversation.convoid);
                itemView.getContext().startActivity(convoListActivity);
            }
        });
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
