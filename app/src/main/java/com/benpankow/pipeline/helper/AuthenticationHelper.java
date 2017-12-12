package com.benpankow.pipeline.helper;

import android.support.annotation.NonNull;
import android.widget.Toast;

import com.benpankow.pipeline.R;
import com.benpankow.pipeline.activity.base.BaseActivity;
import com.benpankow.pipeline.data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import java8.util.function.Consumer;

/**
 * Created by Ben Pankow on 12/2/17.
 *
 * Helps with authenticating or registering users
 */
public class AuthenticationHelper {

    /**
     * Attempts to log in a new user to the app
     *
     * @param activity The Activity that the app currently has open
     * @param email The user's email
     * @param password The user's password
     */
    public static void login(final BaseActivity activity, String email, String password) {
        if (email == null || password == null || email.length() == 0 || password.length() == 0) {
            Toast.makeText(
                    activity,
                    R.string.err_blank,
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }
        // Pass info to FirebaseAuth instance
        activity.getAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        task.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(
                                        activity,
                                        R.string.err_no_credentials,
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        });
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
        if (email == null || password == null || email.length() == 0 || password.length() == 0) {
            Toast.makeText(
                    activity,
                    R.string.err_blank,
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }
        // Pass info to FirebaseAuth instance
        activity.getAuth().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        task.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if (e instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(
                                            activity,
                                            R.string.err_email_in_use,
                                            Toast.LENGTH_SHORT
                                    ).show();
                                } else if (e instanceof FirebaseAuthWeakPasswordException) {
                                    Toast.makeText(
                                            activity,
                                            ((FirebaseAuthWeakPasswordException) e).getReason(),
                                            Toast.LENGTH_SHORT
                                    ).show();
                                } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                    Toast.makeText(
                                            activity,
                                            R.string.err_invalid_email,
                                            Toast.LENGTH_SHORT
                                    ).show();
                                } else {
                                    Toast.makeText(
                                            activity,
                                            R.string.err_unknown,
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }
                        });
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
            DatabaseHelper.bindUser(user.getUid(), callback);
        } else {
            callback.accept(null);
        }
    }

}
