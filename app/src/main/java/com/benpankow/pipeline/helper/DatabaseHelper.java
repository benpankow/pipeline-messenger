package com.benpankow.pipeline.helper;

import android.view.inputmethod.CompletionInfo;

import com.benpankow.pipeline.data.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseReference.CompletionListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(USERS_KEY)
                .child(uid)
                .setValue(data, listener);
    }

    public static void updateUser(String uid, User data) {
        updateUser(uid, data, null);
    }

    public static void addUserListener(String uid, ValueEventListener listener) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(USERS_KEY)
                .child(uid)
                .addValueEventListener(listener);
    }
}
