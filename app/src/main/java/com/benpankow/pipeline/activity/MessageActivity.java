package com.benpankow.pipeline.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.benpankow.pipeline.R;
import com.benpankow.pipeline.activity.base.AuthenticatedActivity;
import com.benpankow.pipeline.activity.component.MessageHolder;
import com.benpankow.pipeline.data.Message;
import com.benpankow.pipeline.helper.DatabaseHelper;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class MessageActivity extends AuthenticatedActivity {

    private RecyclerView rvMessages;
    private FirebaseRecyclerAdapter<Message, MessageHolder> messageAdapter;
    private EditText etMessage;
    private ImageButton btnSendMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Intent intent = getIntent();
        final String convoid = intent.getStringExtra("convoid");

        rvMessages = findViewById(R.id.rv_messages);
        rvMessages.setHasFixedSize(true);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));

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

        btnSendMessage = findViewById(R.id.btn_send_message);
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create and send message
                Message message = new Message();
                message.text = etMessage.getText().toString().trim();
                message.senderUid = uid;
                if (message.text.length() > 0) {
                    DatabaseHelper.addMessageToConversation(convoid, message);
                }
                etMessage.setText("");
            }
        });
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
}
