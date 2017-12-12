package com.benpankow.pipeline.activity.base;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.benpankow.pipeline.activity.ConversationListActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Ben Pankow on 12/2/17.
 *
 * A basic activity that is extended by any activity used while unauthenticated
 */
public abstract class UnauthenticatedActivity extends BaseActivity {

    @Override
    protected FirebaseAuth.AuthStateListener createAuthStateListener() {
        return new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // Listen for changes in auth state - if logged in, go to authenticated page
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent authenticatedIntent = new Intent(
                            UnauthenticatedActivity.this,
                            getLoggedInDestination()
                    );
                    UnauthenticatedActivity.this.startActivity(authenticatedIntent);
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
        return ConversationListActivity.class;
    }
}
