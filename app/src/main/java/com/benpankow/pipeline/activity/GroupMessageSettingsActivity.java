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
import com.benpankow.pipeline.data.Message;
import com.benpankow.pipeline.data.MessageType;
import com.benpankow.pipeline.data.User;
import com.benpankow.pipeline.helper.DatabaseHelper;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ServerValue;

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

        final String uid = getAuth().getUid();

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
                    if (!intendedTitle.equals(conversation.title)) {

                        // Set conversation title + send notification to users
                        String infoText = getString(R.string.info_change_convo_title);
                        if (intendedTitle.length() == 0) {
                            intendedTitle = "";
                            infoText = getString(R.string.info_remove_convo_title);
                        }
                        conversation.setTitle(intendedTitle);

                        Message message = new Message(
                                uid,
                                String.format(
                                        infoText,
                                        userData.nickname,
                                        intendedTitle
                                ),
                                ServerValue.TIMESTAMP,
                                MessageType.INFORMATION
                        );
                        if (message.getText().length() > 0) {
                            DatabaseHelper.addMessageToConversation(
                                    convoid,
                                    message,
                                    userData,
                                    GroupMessageSettingsActivity.this
                            );
                        }
                    }
                    DatabaseHelper.updateConversation(convoid, conversation);
                }
            }
        });

        DatabaseHelper.bindConversation(convoid, new Consumer<Conversation>() {
            @Override
            public void accept(Conversation convo) {
                conversation = convo;
                if (convo != null) {
                    if (conversation.getTitle() != null) {
                        etConversationTitle.setText(conversation.getTitle());
                    }
                    // Set title hint
                    conversation.generateTitle(new Consumer<String>() {
                        @Override
                        public void accept(String generatedTitle) {
                            etConversationTitle.setHint(generatedTitle);
                        }
                    });

                    // Return to conversation list if removed from conversation
                    if (!convo.getParticipants().containsKey(uid)) {
                        Intent convoListActivity = new Intent(
                                GroupMessageSettingsActivity.this,
                                ConversationListActivity.class
                        );
                        GroupMessageSettingsActivity.this.startActivity(convoListActivity);
                    }
                }
            }
        });

        rvConvoMembers = findViewById(R.id.rv_convo_members);
        rvConvoMembers.setLayoutManager(new LinearLayoutManager(this));

        // Load all members of this conversation to allow the user to remove people
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
                        holder.bindUser(userData);
                        holder.bindTargetUser(model);
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
