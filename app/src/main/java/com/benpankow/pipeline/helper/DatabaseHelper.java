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

import java8.util.function.Consumer;

/**
 * Created by Ben Pankow on 12/2/17.
 */

public class DatabaseHelper {

    private static final String USERS_KEY = "users";
    private static final String USER_CONVERSATIONS_KEY = "user_conversations";
    private static final String CONVERSATIONS_KEY = "conversations";
    private static final String MESSAGES_KEY = "messages";

    public static void updateUser(String uid, User data, final Consumer<DatabaseError> listener) {
        if (uid == null) {
            return;
        }
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(USERS_KEY)
                .child(uid)
                .setValue(data);
    }

    public static void updateUser(String uid, User data) {
        updateUser(uid, data, null);
    }

    public static void bindUserData(String uid, final Consumer<User> listener) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(USERS_KEY)
                .child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        listener.accept(dataSnapshot.getValue(User.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
    }

    public static void getUserData(String uid, final Consumer<User> listener) {
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(USERS_KEY)
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        listener.accept(dataSnapshot.getValue(User.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
    }

    public static DatabaseReference getRefConversationLocation() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        return database.child(CONVERSATIONS_KEY).getRef();
    }

    public static Query queryConversationsForUser(String uid) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        return database.child(USER_CONVERSATIONS_KEY).child(uid);
    }

    public static Query queryUserByUsername(String username) {
        if (username != null) {
            username = username.toLowerCase();
        }
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        return database.child(USERS_KEY)
                .orderByChild("usernameLower")
                .equalTo(username);
    }

    public static Query queryMessagesForConversation(String convoid, String uid) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        return database.child(MESSAGES_KEY)
                .child(convoid)
                .child(uid);
    }

    public static void createConversationBetween(String uid1, String uid2) {
        if (uid1 == null || uid2 == null) {
            return;
        }
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        DatabaseReference conversationRef = database.child(CONVERSATIONS_KEY).push();
        Conversation conversation = new Conversation();
        conversation.addParticipants(uid1, uid2);
        String conversationKey = conversationRef.getKey();
        conversation.convoid = conversationKey;
        conversationRef.setValue(conversation);

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
