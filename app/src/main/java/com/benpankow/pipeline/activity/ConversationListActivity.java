package com.benpankow.pipeline.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.benpankow.pipeline.R;
import com.benpankow.pipeline.activity.base.AuthenticatedActivity;
import com.benpankow.pipeline.activity.component.ConversationHolder;
import com.benpankow.pipeline.data.Conversation;
import com.benpankow.pipeline.helper.DatabaseHelper;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

/**
 * Created by Ben Pankow on 12/2/17.
 */
public class ConversationListActivity extends AuthenticatedActivity {

    private RecyclerView rvConversations;
    private FirebaseRecyclerAdapter<Conversation, ConversationHolder> conversationAdapter;
    private FloatingActionButton fabAddConversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_list);

        String uid = getAuth().getUid();

        fabAddConversation = findViewById(R.id.fab_add_conversation);
        fabAddConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingsIntent =
                        new Intent(ConversationListActivity.this, SearchActivity.class);
                ConversationListActivity.this.startActivity(settingsIntent);
            }
        });

        rvConversations = findViewById(R.id.rv_conversations);
        rvConversations.setHasFixedSize(true);
        rvConversations.setLayoutManager(new LinearLayoutManager(this));

        // Load all conversations that this user is in

        FirebaseRecyclerOptions<Conversation> conversationOptions =
                new FirebaseRecyclerOptions.Builder<Conversation>()
                .setIndexedQuery(DatabaseHelper.queryConversationsForUser(uid),
                        DatabaseHelper.getRefConversationLocation(), Conversation.class)
                .build();

        conversationAdapter =
                new FirebaseRecyclerAdapter<Conversation, ConversationHolder>(conversationOptions) {

            @Override
            public ConversationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_conversation, parent, false);

                return new ConversationHolder(view);
            }

            @Override
            protected void onBindViewHolder(ConversationHolder holder, int position, Conversation model) {
                holder.bindConversation(model);
            }
        };
        rvConversations.setAdapter(conversationAdapter);
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
                    new Intent(ConversationListActivity.this, SettingsActivity.class);
            ConversationListActivity.this.startActivity(settingsIntent);
            return true;
        }
        /*else if (item.getItemId() == R.id.item_add_conversation) {
            Intent settingsIntent =
                    new Intent(ConversationListActivity.this, SearchActivity.class);
            ConversationListActivity.this.startActivity(settingsIntent);
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        conversationAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        conversationAdapter.stopListening();
    }
}
