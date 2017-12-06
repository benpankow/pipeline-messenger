package com.benpankow.pipeline.helper;

import com.benpankow.pipeline.data.Conversation;
import com.benpankow.pipeline.data.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseReference.CompletionListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ben Pankow on 12/2/17.
 */

public class DatabaseHelper {

    private static final String USERS_KEY = "users";
    private static final String USER_CONVERSATIONS_KEY = "user_conversations";
    private static final String CONVERSATIONS_KEY = "conversations";
    private static final String MESSAGES_KEY = "messages";

    public static void updateUser(String uid, User data, CompletionListener listener) {
        if (uid == null) {
            return;
        }
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(USERS_KEY)
                .child(uid)
                .setValue(data, listener);
    }

    public static void updateUser(String uid, User data) {
        updateUser(uid, data, null);
    }

    public static void addUserListener(String uid, ValueEventListener listener) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(USERS_KEY)
                .child(uid)
                .addValueEventListener(listener);
    }

    public static DatabaseReference getConversationLocation() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        return database.child(CONVERSATIONS_KEY).getRef();
    }

    public static Query getConversationsForUser(String uid) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        return database.child(USER_CONVERSATIONS_KEY).child(uid);
    }

    public static Query getUserByUsername(String username) {
        if (username != null) {
            username = username.toLowerCase();
        }
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        return database.child(USERS_KEY)
                .orderByChild("usernameLower")
                .equalTo(username);
    }

    public static void createConversationBetween(String uid1, String uid2) {
        if (uid1 == null || uid2 == null) {
            return;
        }
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        DatabaseReference conversationRef = database.child(CONVERSATIONS_KEY).push();
        Conversation conversation = new Conversation();
        conversation.addParticipants(uid1, uid2);
        conversationRef.setValue(conversation);

        String conversationKey = conversationRef.getKey();
        addConversationToUser(uid1, conversationKey);
        addConversationToUser(uid2, conversationKey);
    }

    public static void addConversationToUser(final String uid, final String convoid) {
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(USER_CONVERSATIONS_KEY)
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HashMap<String, Boolean> conversationList = dataSnapshot.getValue
                                (new GenericTypeIndicator<HashMap<String, Boolean>>() {});
                        if (conversationList == null) {
                            conversationList = new HashMap<>();
                        }
                        conversationList.put(convoid, true);
                        database.child(USER_CONVERSATIONS_KEY)
                                .child(uid)
                                .setValue(conversationList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
