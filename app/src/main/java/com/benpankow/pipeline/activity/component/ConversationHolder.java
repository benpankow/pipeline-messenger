package com.benpankow.pipeline.activity.component;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.benpankow.pipeline.R;
import com.benpankow.pipeline.activity.ConversationListActivity;
import com.benpankow.pipeline.activity.MessageActivity;
import com.benpankow.pipeline.data.Conversation;
import com.benpankow.pipeline.data.Message;
import com.benpankow.pipeline.data.User;
import com.benpankow.pipeline.helper.AuthenticationHelper;
import com.benpankow.pipeline.helper.DatabaseHelper;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;

import java8.util.function.Consumer;

/**
 * Created by Ben Pankow on 12/2/17.
 *
 * A RecyclerView ViewHolder corresponding to a Conversation
 */

public class ConversationHolder extends RecyclerView.ViewHolder {

    private final View ivMain;
    private final TextView tvTitle;
    private final TextView tvPreview;
    private final TextView tvTimestamp;
    private Conversation conversation;

    public ConversationHolder(View itemView) {
        super(itemView);
        this.ivMain = itemView;
        this.tvTitle = itemView.findViewById(R.id.tv_user_nickname);
        this.tvPreview = itemView.findViewById(R.id.tv_conversation_preview);
        this.tvTimestamp = itemView.findViewById(R.id.tv_conversation_timestamp);

        // On click open messages
        this.ivMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View itemView) {
                Intent conversationActivity =
                        new Intent(itemView.getContext(), MessageActivity.class);
                conversationActivity.putExtra("convoid", conversation.convoid);
                itemView.getContext().startActivity(conversationActivity);
            }
        });
    }

    public void bindConversation(Conversation model) {
        conversation = model;
        conversation.getTitle(new Consumer<String>() {
               @Override
               public void accept(String s) {
                   tvTitle.setText(s);
               }
        });

        String uid = FirebaseAuth.getInstance().getUid();

        tvPreview.setText(conversation.getPreviewMessage(uid));

        tvTimestamp.setText(conversation.getTimestamp(uid));

    }
}
