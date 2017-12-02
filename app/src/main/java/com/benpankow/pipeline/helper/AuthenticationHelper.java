package com.benpankow.pipeline.helper;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.benpankow.pipeline.activity.LoginActivity;
import com.benpankow.pipeline.activity.base.BaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

/**
 * Created by Ben Pankow on 12/2/17.
 */
public class AuthenticationHelper {

    public static void login(BaseActivity activity, String email, String password) {
        // Pass info to FirebaseAuth instance
        activity.getAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                    }
                });
    }

    public static void register(BaseActivity activity, String email, String password) {
        // Pass info to FirebaseAuth instance
        activity.getAuth().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                    }
                });
    }

}
