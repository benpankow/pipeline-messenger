package com.benpankow.pipeline.helper;

import com.benpankow.pipeline.data.Conversation;
import com.benpankow.pipeline.data.Message;
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

    /**
     * Updates data associated with a given user
     *
     * @param uid The uid of the user to update
     * @param data A User object holding that user's data
     */
    public static void updateUser(String uid, User data, final Consumer<DatabaseError> listener) {
        if (uid == null) {
            return;
        }
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(USERS_KEY)
                .child(uid)
                .setValue(data);
    }

    /**
     * Updates data associated with a given user
     *
     * @param uid The uid of the user to update
     * @param data A User object holding that user's data
     */
    public static void updateUser(String uid, User data) {
        updateUser(uid, data, null);
    }

    /**
     * Gets a User object associated with a given uid each time it updates
     *
     * @param uid The uid whose data to fetch
     * @param callback A callback that will be called with the User object, and will be called
     *                 when the state of the User object changes on the database
     */
    public static void bindUserData(String uid, final Consumer<User> callback) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(USERS_KEY)
                .child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        callback.accept(dataSnapshot.getValue(User.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
    }

    /**
     * Gets a User object associated with a given uid
     *
     * @param uid The uid whose data to fetch
     * @param callback A callback that will be called with the User object
     */
    public static void getUserData(String uid, final Consumer<User> callback) {
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(USERS_KEY)
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        callback.accept(dataSnapshot.getValue(User.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
    }

    /**
     * Gets a DatabaseReference for the location where all conversations are stored
     *
     * @return A DatabaseReference for where conversations are stored
     */
    public static DatabaseReference getRefConversationLocation() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        return database.child(CONVERSATIONS_KEY).getRef();
    }

    /**
     * Gets a Query that lists all conversations for a specific user
     *
     * @param uid The uid whose conversations to get
     * @return A Query that lists all the user's conversations
     */
    public static Query queryConversationsForUser(String uid) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        return database.child(USER_CONVERSATIONS_KEY).child(uid);
    }

    /**
     * Gets a Query that gets a specific user by their username
     *
     * @param username The username to search for
     * @return A Query getting the given user
     */
    public static Query queryUserByUsername(String username) {
        if (username != null) {
            username = username.toLowerCase();
        }
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        return database.child(USERS_KEY)
                .orderByChild("usernameLower")
                .equalTo(username);
    }

    /**
     * Gets a Query that queries all messages for a given conversation and user
     *
     * @param convoid The conversation whose messages to get
     * @param uid The uid of the user whose messages to get
     * @return A Query that would get the specified messages
     */
    public static Query queryMessagesForConversation(String convoid, String uid) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        return database.child(MESSAGES_KEY)
                .child(convoid)
                .child(uid);
    }

    /**
     * Creates a new conversation between two users, adding it to both of their conversation lists
     *
     * @param uid1 The uid of the first user
     * @param uid2 The uid of the second user
     */
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

    /**
     * Add a specific convoid to the list of a user's conversations
     *
     * @param uid The user whose list of conversations to update
     * @param convoid The convoid of the conversation to add
     */
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

    /**
     * Adds a message to an existing conversation for all users in that conversation
     *
     * @param convoid The convoid of the conversation to send the message to
     * @param message The Message object to send
     */
    public static void addMessageToConversation(final String convoid, final Message message) {
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        getUsersInConversation(convoid, new Consumer<List<String>>() {
            @Override
            public void accept(List<String> uids) {
                for (String uid : uids) {
                    DatabaseReference ref =
                            database.child(MESSAGES_KEY)
                                    .child(convoid)
                                    .child(uid)
                                    .push();
                    ref.setValue(message);
                }

            }
        });
    }

    /**
     * Obtains a list of all users involved in the conversation in the form of uids, calls given
     * callback
     *
     * @param convoid The convoid of the conversation that we're interested in
     * @param callback A callback Consumer that will be called with a List of Strings
     */
    private static void getUsersInConversation(String convoid, final Consumer<List<String>> callback) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        database.child(CONVERSATIONS_KEY)
                .child(convoid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Conversation conversation = dataSnapshot.getValue(Conversation.class);
                        if (conversation == null) {
                            callback.accept(new ArrayList<String>());
                        } else {
                            callback.accept(conversation.participants);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
