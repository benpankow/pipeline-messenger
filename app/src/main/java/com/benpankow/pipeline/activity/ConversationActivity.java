package com.benpankow.pipeline.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.benpankow.pipeline.R;
import com.benpankow.pipeline.activity.base.AuthenticatedActivity;
import com.benpankow.pipeline.activity.component.MessageHolder;
import com.benpankow.pipeline.data.Conversation;
import com.benpankow.pipeline.data.Message;
import com.benpankow.pipeline.helper.DatabaseHelper;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ServerValue;

import java8.util.function.Consumer;

public class ConversationActivity extends AuthenticatedActivity {

    private RecyclerView rvMessages;
    private FirebaseRecyclerAdapter<Message, MessageHolder> messageAdapter;
    private EditText etMessage;
    private ImageButton btnSendMessage;
    private Conversation conversation;
    private LinearLayoutManager llmMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        Intent intent = getIntent();
        final String convoid = intent.getStringExtra("convoid");

        rvMessages = findViewById(R.id.rv_messages);
        rvMessages.setHasFixedSize(true);
        llmMessages = new LinearLayoutManager(this);
        rvMessages.setLayoutManager(llmMessages);

        final String uid = getAuth().getUid();

        FirebaseRecyclerOptions<Message> messageOptions =
                new FirebaseRecyclerOptions.Builder<Message>()
                        .setQuery(
                                DatabaseHelper.queryMessagesForConversation(convoid, uid),
                                Message.class
                        ).build();

        messageAdapter =
                new FirebaseRecyclerAdapter<Message, MessageHolder>(messageOptions) {

                    @Override
                    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(viewType, parent, false);

                        return new MessageHolder(view);
                    }

                    @Override
                    public int getItemViewType(int position) {
                        // Switch between sent/received button layout
                        if (getItem(position).sentByCurrentUser()) {
                            return R.layout.item_message_sent;
                        } else {
                            return R.layout.item_message_received;
                        }
                    }

                    @Override
                    protected void onBindViewHolder(MessageHolder holder, int position, Message model) {
                        holder.bindMessage(model);
                    }
                };
        rvMessages.setAdapter(messageAdapter);

        etMessage = findViewById(R.id.et_message);
        etMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                //https://stackoverflow.com/a/19217624
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btnSendMessage.performClick();
                    return true;
                }
                return false;
            }
        });

        btnSendMessage = findViewById(R.id.btn_send_message);
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create and send message
                Message message = new Message();
                message.text = etMessage.getText().toString().trim();
                message.senderUid = uid;
                message.timestamp = ServerValue.TIMESTAMP;
                if (message.text.length() > 0) {
                    DatabaseHelper.addMessageToConversation(
                            convoid,
                            message,
                            userData,
                            ConversationActivity.this
                    );
                }
                etMessage.setText("");
                llmMessages.scrollToPosition(messageAdapter.getItemCount() - 1);
            }
        });

        DatabaseHelper.bindConversation(convoid, new Consumer<Conversation>() {
            @Override
            public void accept(Conversation convo) {
                conversation = convo;
                if (convo != null) {
                    convo.getTitle(new Consumer<String>() {
                        @Override
                        public void accept(String s) {
                            ActionBar actionBar = ConversationActivity.this.getSupportActionBar();
                            if (actionBar != null) {
                                actionBar.setTitle(s);
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add settings button to menu bar
        getMenuInflater().inflate(R.menu.conversation_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_settings) {
            Intent settingsIntent =
                    new Intent(ConversationActivity.this, ConversationSettingsActivity.class);
            settingsIntent.putExtra("convoid", conversation.convoid);
            ConversationActivity.this.startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();
        messageAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        messageAdapter.stopListening();
    }


    @Override
    public void onBackPressed() {
        Intent conversationListActivity =
                new Intent(ConversationActivity.this, ConversationListActivity.class);
        ConversationActivity.this.startActivity(conversationListActivity);
    }
}
