package com.benpankow.pipeline.activity.component;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.benpankow.pipeline.R;
import com.benpankow.pipeline.data.Conversation;
import com.benpankow.pipeline.data.User;
import com.benpankow.pipeline.helper.AuthenticationHelper;
import com.benpankow.pipeline.helper.DatabaseHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Ben Pankow on 12/2/17.
 */

public class ConversationItem extends RecyclerView.ViewHolder {

    private final View ivMain;
    private final TextView tvName;
    private Conversation conversation;

    public ConversationItem(View itemView) {
        super(itemView);
        this.ivMain = itemView;
        this.tvName = itemView.findViewById(R.id.search_result_name);
    }

    public void bindConversation(Conversation model) {
        conversation = model;
        tvName.setText("EE");
    }
}
