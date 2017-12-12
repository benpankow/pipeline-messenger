package com.benpankow.pipeline.activity.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.benpankow.pipeline.activity.LoginActivity;
import com.benpankow.pipeline.data.User;
import com.benpankow.pipeline.helper.DatabaseHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java8.util.function.Consumer;

/**
 * Created by Ben Pankow on 12/2/17.
 *
 * A basic activity that is extended by any activity used while authenticated
 */
public abstract class AuthenticatedActivity extends BaseActivity {

    public static User userData = new User();
    public boolean first;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseUser user = getAuth().getCurrentUser();

        first = true;
        if (user != null) {
            DatabaseHelper.bindUser(user.getUid(), new Consumer<User>() {
                @Override
                public void accept(User user) {
                    userData = user;
                    if (userData == null) {
                        getAuth().signOut();
                        return;
                    }
                    onUserDataUpdate();
                    if (first) {
                        onUserDataObtained();
                        first = false;
                    }
                }
            });
        }
    }

    protected void onUserDataObtained() {};

    protected void onUserDataUpdate() {}

    @Override
    protected FirebaseAuth.AuthStateListener createAuthStateListener() {
        return new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // Listen for changes in auth state - if logged in, go to authenticated page
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Intent unauthenticatedActivity = new Intent(
                            AuthenticatedActivity.this,
                            getLoggedInDestination()
                    );
                    AuthenticatedActivity.this.startActivity(unauthenticatedActivity);
                }
            }
        };
    }

    /**
     * Gets the Activity that will be activated when the user is logged in
     *
     * @return The Activity to activate
     */
    protected Class getLoggedInDestination() {
        return LoginActivity.class;
    }
}
