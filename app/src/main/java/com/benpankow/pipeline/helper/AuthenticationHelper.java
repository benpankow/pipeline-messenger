package com.benpankow.pipeline.helper;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.benpankow.pipeline.R;
import com.benpankow.pipeline.activity.LoginActivity;
import com.benpankow.pipeline.activity.base.BaseActivity;
import com.benpankow.pipeline.data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java8.util.function.Consumer;

/**
 * Created by Ben Pankow on 12/2/17.
 */
public class AuthenticationHelper {

    /**
     * Attempts to log in a new user to the app
     *
     * @param activity The Activity that the app currently has open
     * @param email The user's email
     * @param password The user's password
     */
    public static void login(BaseActivity activity, String email, String password) {
        // Pass info to FirebaseAuth instance
        activity.getAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                    }
                });
    }

    /**
     * Registers a new user with the app
     *
     * @param activity The Activity that the app currently has open
     * @param email The user's email
     * @param password The user's password
     */
    public static void register(final BaseActivity activity, String email, String password) {
        // Pass info to FirebaseAuth instance
        activity.getAuth().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            // Notify user if login failed, otherwise listener in onCreate does the
                            // work for us
                            Toast.makeText(activity, "BAD!", Toast
                                    .LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
    }

    /**
     * Gets a User object associated with a given uid each time it updates
     *
     * @param callback The callback which will be called with the User object, and will be called
     *                 when the state of the User object changes on the database
     */
    public static void bindLoggedInUserInfo(Consumer<User> callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            DatabaseHelper.bindUserData(user.getUid(), callback);
        } else {
            callback.accept(null);
        }
    }

}
