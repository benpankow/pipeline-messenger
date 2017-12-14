package com.benpankow.pipeline.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.benpankow.pipeline.R;
import com.benpankow.pipeline.activity.base.AuthenticatedActivity;
import com.benpankow.pipeline.data.Conversation;
import com.benpankow.pipeline.data.Message;
import com.benpankow.pipeline.data.MessageType;
import com.benpankow.pipeline.data.User;
import com.benpankow.pipeline.helper.DatabaseHelper;
import com.google.firebase.database.ServerValue;

import java8.util.function.Consumer;

/**
 * Created by Ben Pankow on 12/3/17.
 *
 * Handles changing settings related to a direct message.
 */
public class DirectMessageSettingsActivity extends AuthenticatedActivity {

    private TextView tvUsername;
    private TextView tvNickname;
    private EditText etConversationTitle;
    private Button btnSaveSettings;
    private Conversation conversation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_message_settings);

        final String uid = getAuth().getUid();

        Intent intent = getIntent();
        final String convoid = intent.getStringExtra("convoid");

        tvUsername = findViewById(R.id.tv_username);
        tvNickname = findViewById(R.id.tv_nickname);

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
                            intendedTitle = null;
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
                                    DirectMessageSettingsActivity.this
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
                    // Set other user's information
                    conversation.getOtherUser(new Consumer<User>() {
                        @Override
                        public void accept(User user) {
                            if (user != null) {
                                tvNickname.setText(user.getNickname());
                                tvUsername.setText(user.getUsername());
                            }
                        }
                    });

                    // Return to conversation list if removed from conversation
                    if (!convo.getParticipants().containsKey(uid)) {
                        Intent convoListActivity = new Intent(
                                DirectMessageSettingsActivity.this,
                                ConversationListActivity.class
                        );
                        DirectMessageSettingsActivity.this.startActivity(convoListActivity);
                    }
                }
            }
        });
    }
}
