package com.benpankow.pipeline.activity.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Ben Pankow on 12/2/17.
 *
 * A basic activity that handles common functionality
 */
public abstract class BaseActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        authListener = createAuthStateListener();
    }

    /**
     * Creates the AuthStateListener that will be triggered on the authentication state changing
     *
     * @return The AuthStateListener to bind
     */
    protected abstract FirebaseAuth.AuthStateListener createAuthStateListener();

    public FirebaseAuth getAuth() {
        return auth;
    }

    @Override
    protected void onStart() {
        // Start listening for changes in authentication state
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authListener != null) {
            // Stop listening for changes in authentication state
            auth.removeAuthStateListener(authListener);
        }
    }
}
