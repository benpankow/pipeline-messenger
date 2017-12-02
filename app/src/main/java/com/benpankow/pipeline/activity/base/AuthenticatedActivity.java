package com.benpankow.pipeline.activity.base;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.benpankow.pipeline.ConversationListActivity;
import com.benpankow.pipeline.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Ben Pankow on 12/2/17.
 */
public abstract class AuthenticatedActivity extends BaseActivity {

    @Override
    protected FirebaseAuth.AuthStateListener createAuthStateListener() {
        return new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // Listen for changes in auth state - if logged in, go to authenticated page
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Intent unauthenticatedIntent = new Intent(
                            AuthenticatedActivity.this,
                            getLoggedOutDestination()
                    );
                    AuthenticatedActivity.this.startActivity(unauthenticatedIntent);
                }
            }
        };
    }

    /**
     * Gets the Activity that will be activated when the user is logged out
     *
     * @return The Activity to activate
     */
    protected Class getLoggedOutDestination() {
        return LoginActivity.class;
    }
}
