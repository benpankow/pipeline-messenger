package com.benpankow.pipeline.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.benpankow.pipeline.R;
import com.benpankow.pipeline.activity.base.AuthenticatedActivity;
import com.benpankow.pipeline.activity.component.UserHolderRemovable;
import com.benpankow.pipeline.data.Conversation;
import com.benpankow.pipeline.data.User;
import com.benpankow.pipeline.helper.DatabaseHelper;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java8.util.function.Consumer;

/**
 * Created by Ben Pankow on 12/3/17.
 *
 * Handles changing settings related to each conversation.
 */
public class GroupMessageSettingsActivity extends AuthenticatedActivity {

    private EditText etConversationTitle;
    private Button btnSaveSettings;
    private Conversation conversation;
    private FirebaseRecyclerAdapter<User, UserHolderRemovable> membersAdapter;
    private RecyclerView rvConvoMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_message_settings);

        Intent intent = getIntent();
        final String convoid = intent.getStringExtra("convoid");

        etConversationTitle = findViewById(R.id.et_conversation_title);

        // Update conversation name when button pressed
        btnSaveSettings = findViewById(R.id.btn_save_settings);
        btnSaveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (conversation != null) {
                    String intendedTitle = etConversationTitle.getText().toString().trim();
                    if (intendedTitle.length() > 0) {
                        conversation.title = intendedTitle;
                    } else {
                        conversation.title = null;
                    }
                    DatabaseHelper.updateConversation(convoid, conversation);
                }
            }
        });

        DatabaseHelper.bindConversation(convoid, new Consumer<Conversation>() {
            @Override
            public void accept(Conversation convo) {
                conversation = convo;
                if (conversation.title != null) {
                    etConversationTitle.setText(conversation.title);
                }
                conversation.generateTitle(new Consumer<String>() {
                    @Override
                    public void accept(String generatedTitle) {
                        etConversationTitle.setHint(generatedTitle);
                    }
                });

            }
        });

        rvConvoMembers = findViewById(R.id.rv_convo_members);
        rvConvoMembers.setLayoutManager(new LinearLayoutManager(this));

        // Load all members of this conversation
        FirebaseRecyclerOptions<User> conversationOptions =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setIndexedQuery(DatabaseHelper.queryUsersInConversation(convoid),
                                DatabaseHelper.getRefUserLocation(), User.class)
                        .build();

        membersAdapter =
                new FirebaseRecyclerAdapter<User, UserHolderRemovable>(conversationOptions) {

                    @Override
                    public UserHolderRemovable onCreateViewHolder(ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_user_removable, parent, false);

                        return new UserHolderRemovable(view);
                    }

                    @Override
                    protected void onBindViewHolder(UserHolderRemovable holder, int position, User model) {
                        holder.bindUser(model);
                        holder.bindConversationId(convoid);
                    }
                };
        rvConvoMembers.setAdapter(membersAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        membersAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        membersAdapter.stopListening();
    }
}
