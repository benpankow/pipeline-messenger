package com.benpankow.pipeline.activity;

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
import com.benpankow.pipeline.activity.component.UserHolderCheckbox;
import com.benpankow.pipeline.data.User;
import com.benpankow.pipeline.helper.ConversationHelper;
import com.benpankow.pipeline.helper.DatabaseHelper;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Ben Pankow on 12/2/17.
 *
 * Handles creation of groups between many users.
 */
public class CreateGroupActivity extends AuthenticatedActivity {

    private RecyclerView rvFriends;
    private FirebaseRecyclerAdapter<User, UserHolderCheckbox> friendsAdapter;
    private FloatingActionButton fabAddConversation;
    private FloatingActionButton fabAddUser;
    private FloatingActionButton fabAddGroup;
    private Set<String> addedUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        String uid = getAuth().getUid();

        rvFriends = findViewById(R.id.rv_friends);
        rvFriends.setHasFixedSize(true);
        rvFriends.setLayoutManager(new LinearLayoutManager(this));

        // Load all friends of this user, people they can add to groups
        FirebaseRecyclerOptions<User> conversationOptions =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setIndexedQuery(DatabaseHelper.queryFriendsForUser(uid),
                                DatabaseHelper.getRefUserLocation(), User.class)
                        .build();

        addedUsers = new HashSet<>();
        addedUsers.add(uid);

        friendsAdapter =
                new FirebaseRecyclerAdapter<User, UserHolderCheckbox>(conversationOptions) {

                    @Override
                    public UserHolderCheckbox onCreateViewHolder(ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_user_checkbox, parent, false);

                        return new UserHolderCheckbox(view);
                    }

                    @Override
                    protected void onBindViewHolder(UserHolderCheckbox holder, int position, User model) {
                        holder.bindTargetUser(model);
                        holder.bindSet(addedUsers);
                    }
                };
        rvFriends.setAdapter(friendsAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add next button to menu bar
        getMenuInflater().inflate(R.menu.create_group_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Next button, creates group
        if (item.getItemId() == R.id.item_next && addedUsers.size() > 1) {
            ConversationHelper.createAndOpenGroup(
                    CreateGroupActivity.this,
                    addedUsers.toArray(new String[0])
            );
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        friendsAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        friendsAdapter.stopListening();
    }
}
