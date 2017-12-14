package com.benpankow.pipeline.helper;

import android.content.Context;
import android.util.Base64;

import com.benpankow.pipeline.R;
import com.benpankow.pipeline.activity.DirectMessageSettingsActivity;
import com.benpankow.pipeline.data.Conversation;
import com.benpankow.pipeline.data.ConversationType;
import com.benpankow.pipeline.data.Message;
import com.benpankow.pipeline.data.MessageType;
import com.benpankow.pipeline.data.Notification;
import com.benpankow.pipeline.data.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java8.util.function.Consumer;

/**
 * Created by Ben Pankow on 12/2/17.
 *
 * Helps with interactions with the Firebase database
 */
public class DatabaseHelper {

    private static final String USERS_KEY = "users";
    private static final String USER_CONVERSATIONS_KEY = "user_conversations";
    private static final String USER_FRIENDS_KEY = "user_friends";
    private static final String CONVERSATIONS_KEY = "conversations";
    private static final String MESSAGES_KEY = "messages";
    private static final String NOTIFICATIONS_KEY = "notifications";

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
     * Updates data associated with a given conversation
     *
     * @param convoid The convoid of the conversation to update
     * @param data A Conversation object holding that conversation's data
     */
    public static void updateConversation(String convoid,
                                          Conversation data,
                                          final Consumer<DatabaseError> listener) {
        if (convoid == null) {
            return;
        }
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(CONVERSATIONS_KEY)
                .child(convoid)
                .setValue(data);
    }

    /**
     * Updates data associated with a given conversation
     *
     * @param convoid The convoid of the conversation to update
     * @param data A Conversation object holding that conversation's data
     */
    public static void updateConversation(String convoid, Conversation data) {
        updateConversation(convoid, data, null);
    }

    /**
     * Gets a User object associated with a given uid each time it updates
     *
     * @param uid The uid whose data to fetch
     * @param callback A callback that will be called with the User object, and will be called
     *                 when the state of the User object changes on the database
     */
    public static void bindUser(String uid, final Consumer<User> callback) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(USERS_KEY)
                .child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        callback.accept(dataSnapshot.getValue(User.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.accept(null);
                    }
                });
    }


    /**
     * Gets a User object associated with a given uid
     *
     * @param uid The uid whose data to fetch
     * @param callback A callback that will be called with the User object
     */
    public static void getUser(String uid, final Consumer<User> callback) {
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(USERS_KEY)
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        callback.accept(dataSnapshot.getValue(User.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.accept(null);
                    }
                });
    }


    /**
     * Gets a Conversation object associated with a given convoid each time it updates
     *
     * @param convoid The convoid whose data to fetch
     * @param callback A callback that will be called with the Conversation object, and will be called
     *                 when the state of the Conversation object changes on the database
     */
    public static void bindConversation(String convoid, final Consumer<Conversation> callback) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(CONVERSATIONS_KEY)
                .child(convoid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        callback.accept(dataSnapshot.getValue(Conversation.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.accept(null);
                    }
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
        return database.child(USER_CONVERSATIONS_KEY).child(uid).orderByValue();
    }

    /**
     * Gets a DatabaseReference for the location where all users are stored
     *
     * @return A DatabaseReference for where users are stored
     */
    public static DatabaseReference getRefUserLocation() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        return database.child(USERS_KEY).getRef();
    }

    /**
     * Gets a Query that lists all friends for a specific user
     *
     * @param uid The uid whose friends to get
     * @return A Query that lists all the user's friends
     */
    public static Query queryFriendsForUser(String uid) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        return database.child(USER_FRIENDS_KEY).child(uid);
    }

    /**
     * Gets a Query that lists all users for a specific conversation
     *
     * @param convoid The convoid whose users to get
     * @return A Query that lists all the users in the conversation
     */
    public static Query queryUsersInConversation(String convoid) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        return database.child(CONVERSATIONS_KEY).child(convoid).child("participants");
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
     * Calls back whether a user exists with the given username
     *
     * @param username The username to search for
     * @param callback A Callback taking a Boolean, whether the user exists
     */
    public static void doesUserExistWithUsername(String username, final Consumer<Boolean> callback) {
        queryUserByUsername(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean userExists = dataSnapshot.hasChildren();
                callback.accept(userExists);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.accept(false);
            }
        });
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
     * Gets a conversation between two users, if it exists, passing it to the given callback. If
     * it does not exist, return null
     *
     * @param uid1 The uid of the first user
     * @param uid2 The uid of the second user
     * @param callback A Callback taking a String of the convoid of the users' conversation
     */
    public static void getConversationBetween(final String uid1,
                                              final String uid2,
                                              final Consumer<String> callback) {
        if (uid1 == null || uid2 == null) {
            callback.accept(null);
            return;
        }
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        database.child(USER_FRIENDS_KEY)
                .child(uid1)
                .child(uid2)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String convoid = dataSnapshot.getValue(String.class);
                        if (convoid != null) {
                            addConversationToUser(uid1, convoid);
                            addConversationToUser(uid2, convoid);
                        }
                        callback.accept(convoid);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.accept(null);
                    }
                });
    }

    /**
     * Creates a new direct conversation between two users, adding it to both of their conversation
     * lists
     *
     * @param uid1 The uid of the first user
     * @param uid2 The uid of the second user
     * @param callback A Callback taking a String of the convoid of the users' conversation
     */
    public static void createConversationBetween(final String uid1,
                                                 final String uid2,
                                                 final Consumer<String> callback) {

        createConversation(new String[]{uid1, uid2}, new Consumer<String>() {
            @Override
            public void accept(String conversationKey) {
                if (conversationKey == null) {
                    callback.accept(null);
                    return;
                }
                addFriendToUser(uid1, uid2, conversationKey);
                addFriendToUser(uid2, uid1, conversationKey);

                callback.accept(conversationKey);
            }
        }, false);
    }


    /**
     * Creates a conversation between a number of users, and adds it to each of their
     * conversation lists
     *
     * @param uids A list of uids to add to the conversation
     * @param callback A callback called with the convoid of the produced conversation
     * @param groupMessage Whether or not to make a group message
     */
    public static void createConversation(String[] uids, Consumer<String> callback, boolean groupMessage) {
        if (uids == null) {
            callback.accept(null);
            return;
        }
        for (String uid : uids) {
            if (uid == null) {
                callback.accept(null);
                return;
            }
        }
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        DatabaseReference conversationRef = database.child(CONVERSATIONS_KEY).push();

        ConversationType type = groupMessage ? ConversationType.GROUP_MESSAGE :
                ConversationType.DIRECT_MESSAGE;
        Conversation conversation = new Conversation(ServerValue.TIMESTAMP, type);
        conversation.addParticipants(uids);
        String conversationKey = conversationRef.getKey();
        conversation.setConvoid(conversationKey);
        conversationRef.setValue(conversation);

        for (String uid : uids) {
            addConversationToUser(uid, conversationKey);
        }

        callback.accept(conversationKey);
    }

    /**
     * Add a specific uid and convoid pair to the list of a user's friends
     *
     * @param uid The user whose list of friends to update
     * @param friendUid The user who is becoming a friend and whose conversation is being added
     * @param convoid The convoid of the conversation to add
     */
    public static void addFriendToUser(final String uid,
                                       final String friendUid,
                                       final String convoid) {
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(USER_FRIENDS_KEY)
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HashMap<String, String> friendList = dataSnapshot.getValue
                                (new GenericTypeIndicator<HashMap<String, String>>() {});
                        if (friendList == null) {
                            friendList = new HashMap<>();
                        }
                        friendList.put(friendUid, convoid);
                        database.child(USER_FRIENDS_KEY)
                                .child(uid)
                                .setValue(friendList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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
                        HashMap<String, Object> conversationList = null;
                        if (dataSnapshot != null) {
                            conversationList = dataSnapshot.getValue
                                    (new GenericTypeIndicator<HashMap<String, Object>>() {});
                        }
                        if (conversationList == null) {
                            conversationList = new HashMap<>();
                        }
                        if (!conversationList.containsKey(convoid)) {
                            conversationList.put(convoid, ServerValue.TIMESTAMP);
                        }
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
     * Removes a specific convoid from the list of a user's conversations
     *
     * @param user The user who is currently logged in
     * @param targetUser The user whose list of conversations to update
     * @param convoid The convoid of the conversation to remove
     */
    public static void removeConversationFromUser(final User user, final User targetUser,
                                                  final String convoid, final Context context) {
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(CONVERSATIONS_KEY)
                .child(convoid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Conversation conversation = dataSnapshot.getValue(Conversation.class);
                        if (conversation != null
                                && conversation.getConversationType() == ConversationType.GROUP_MESSAGE
                                && conversation.getParticipants() != null) {
                            conversation.getParticipants().remove(targetUser.getUid());

                            // Send notification in conversation
                            String infoText = context.getString(R.string.info_removed);
                            if (user.uid.equals(targetUser.uid)) {
                                infoText = context.getString(R.string.info_left_convo);
                            }
                            Message message = new Message(
                                    user.getUid(),
                                    String.format(
                                            infoText,
                                            user.nickname,
                                            targetUser.nickname
                                    ),
                                    ServerValue.TIMESTAMP,
                                    MessageType.INFORMATION
                            );
                            if (message.getText().length() > 0) {
                                DatabaseHelper.addMessageToConversation(
                                        convoid,
                                        message,
                                        user,
                                        context
                                );
                            }
                        }
                        database.child(CONVERSATIONS_KEY)
                                .child(convoid)
                                .setValue(conversation);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        // Remove from list of user conversations
        database.child(USER_CONVERSATIONS_KEY)
                .child(targetUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HashMap<String, Object> conversationList = dataSnapshot.getValue
                                (new GenericTypeIndicator<HashMap<String, Object>>() {});
                        if (conversationList == null) {
                            conversationList = new HashMap<>();
                        }
                        conversationList.remove(convoid);
                        database.child(USER_CONVERSATIONS_KEY)
                                .child(targetUser.getUid())
                                .setValue(conversationList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    /**
     * Adds a specific user to a group, after that group was created
     *
     * @param user The user who is currently logged in
     * @param targetUser The user who to add to the group
     * @param convoid The convoid of the conversation to add to
     */
    public static void addUserToGroup(final User user, final User targetUser,
                                      final String convoid, final Context context) {
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(CONVERSATIONS_KEY)
                .child(convoid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Conversation conversation = dataSnapshot.getValue(Conversation.class);
                        if (conversation != null
                                && conversation.getConversationType() == ConversationType.GROUP_MESSAGE
                                && conversation.getParticipants() != null) {
                            conversation.getParticipants().put(targetUser.getUid(), true);

                            // Send notification in conversation
                            String infoText = context.getString(R.string.info_added);
                            Message message = new Message(
                                    user.getUid(),
                                    String.format(
                                            infoText,
                                            user.nickname,
                                            targetUser.nickname
                                    ),
                                    ServerValue.TIMESTAMP,
                                    MessageType.INFORMATION
                            );
                            if (message.getText().length() > 0) {
                                DatabaseHelper.addMessageToConversation(
                                        convoid,
                                        message,
                                        user,
                                        context
                                );
                            }
                        }
                        database.child(CONVERSATIONS_KEY)
                                .child(convoid)
                                .setValue(conversation);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        DatabaseHelper.addConversationToUser(targetUser.uid, convoid);
    }

    /**
     * Adds a message to an existing conversation for all users in that conversation
     *
     * @param convoid The convoid of the conversation to send the message to
     * @param message The Message object to send
     * @param sender The User who sent this message
     * @param context The context that this message is sent from
     */
    public static void addMessageToConversation(final String convoid,
                                                final Message message,
                                                final User sender,
                                                final Context context) {
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        try {
            // First, attempt to sign the message
            message.sign(context);

            getUsersInConversation(convoid, new Consumer<List<String>>() {
                @Override
                public void accept(List<String> uids) {
                    // Update the timestamp for this conversation
                    database.child(CONVERSATIONS_KEY)
                            .child(convoid)
                            .child("timestamp")
                            .setValue(ServerValue.TIMESTAMP);
                    for (final String uid : uids) {
                       getUser(uid, new Consumer<User>() {
                            @Override
                            public void accept(User user) {
                                try {
                                    // Decode the target user's public key
                                    byte[] encodedKey = Base64.decode(user.getPublicKey(), Base64.DEFAULT);
                                    X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(encodedKey);
                                    KeyFactory rsaFactory = KeyFactory.getInstance("RSA");
                                    PublicKey publicKey = rsaFactory.generatePublic(X509publicKey);

                                    // Create a copy of the message for the target user & encrypt
                                    Message messageForUser = message.clone();
                                    byte[][] encryptedMessage = EncryptionHelper.encrypt(
                                            messageForUser.getText().getBytes(),
                                            publicKey
                                    );
                                    messageForUser.setText(Base64.encodeToString(
                                            encryptedMessage[0],
                                            Base64.DEFAULT
                                    ));
                                    messageForUser.setKey(Base64.encodeToString(
                                            encryptedMessage[1],
                                            Base64.DEFAULT
                                    ));

                                    // Adds the message to the list of messages
                                    database.child(MESSAGES_KEY)
                                            .child(convoid)
                                            .child(uid)
                                            .push()
                                            .setValue(messageForUser);

                                    // Add the message as a preview for this conversation
                                    database.child(CONVERSATIONS_KEY)
                                            .child(convoid)
                                            .child("recentMessages")
                                            .child(uid)
                                            .setValue(messageForUser);

                                    // Update the user-specific timestamp for this conversation
                                    // (for sorting purposes on home screen)
                                    database.child(USER_CONVERSATIONS_KEY)
                                            .child(uid)
                                            .child(convoid)
                                            .setValue(ServerValue.TIMESTAMP);


                                    if (!uid.equals(message.getSenderUid())) {
                                        String messageText = context.getString(R.string.new_msg);
                                        Notification notification = new Notification(
                                                uid,
                                                String.format(messageText, sender.getNickname()),
                                                sender.getNickname(),
                                                convoid
                                        );

                                        database.child(NOTIFICATIONS_KEY)
                                                .push()
                                                .setValue(notification);
                                    }
                                } catch (NoSuchAlgorithmException | InvalidKeySpecException
                                        | BadPaddingException | IllegalBlockSizeException
                                        | InvalidKeyException | NoSuchPaddingException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                }
            });
        } catch (CertificateException | NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException | NoSuchProviderException | IOException | InvalidAlgorithmParameterException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
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
                            callback.accept(new ArrayList<>(conversation.getParticipants().keySet()));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    /**
     * Update's a user's device token on the database, used when a new token is generated.
     *
     * @param uid The uid of the user whose deviceToken to update
     * @param deviceToken The user's new device token
     */
    public static void updateDeviceToken(String uid, String deviceToken) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(USERS_KEY).child(uid).child("deviceToken").setValue(deviceToken);
    }

    /**
     * Update's a user's public key on the database
     *
     * @param uid The uid of the user whose deviceToken to update
     * @param stringPublicKey The user's new public key
     */
    public static void updatePublicKey(String uid, String stringPublicKey) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(USERS_KEY).child(uid).child("publicKey").setValue(stringPublicKey);
    }
}
