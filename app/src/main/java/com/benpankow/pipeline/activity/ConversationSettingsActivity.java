package com.benpankow.pipeline.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.benpankow.pipeline.R;
import com.benpankow.pipeline.activity.base.AuthenticatedActivity;
import com.benpankow.pipeline.data.Conversation;
import com.benpankow.pipeline.helper.DatabaseHelper;

import java8.util.function.Consumer;

public class ConversationSettingsActivity extends AuthenticatedActivity {

    private EditText etConversationTitle;
    private Button btnSaveSettings;
    private Conversation conversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_settings);

        Intent intent = getIntent();
        final String convoid = intent.getStringExtra("convoid");

        etConversationTitle = (EditText) findViewById(R.id.et_conversation_title);

        btnSaveSettings = (Button) findViewById(R.id.btn_save_settings);
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
    }
}
