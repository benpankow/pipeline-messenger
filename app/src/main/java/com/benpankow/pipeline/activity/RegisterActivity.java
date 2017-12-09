package com.benpankow.pipeline.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.benpankow.pipeline.R;
import com.benpankow.pipeline.activity.base.UnauthenticatedActivity;
import com.benpankow.pipeline.data.User;
import com.benpankow.pipeline.helper.AuthenticationHelper;
import com.benpankow.pipeline.helper.DatabaseHelper;
import com.benpankow.pipeline.helper.EncryptionHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import org.w3c.dom.Text;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

/**
 * Created by Ben Pankow on 12/2/17.
 */
public class RegisterActivity extends UnauthenticatedActivity {

    private Button btnRegister;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etUsername;

    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tvLogin = findViewById(R.id.tv_login);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etUsername = findViewById(R.id.et_username);

        btnRegister = findViewById(R.id.btn_register);

        // Attempt to login on button press
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                AuthenticationHelper.register(RegisterActivity.this, email, password);
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent =
                        new Intent(RegisterActivity.this, LoginActivity.class);
                RegisterActivity.this.startActivity(loginIntent);
            }
        });
    }

    @Override
    protected FirebaseAuth.AuthStateListener createAuthStateListener() {
        return new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // Listen for changes in auth state - if logged in, go to authenticated page
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // On register, set up user's device token
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

                    // Set up user object
                    User userObj = new User();
                    userObj.deviceToken = deviceToken;
                    userObj.uid = user.getUid();
                    userObj.email = user.getEmail();
                    userObj.username = etUsername.getText().toString();
                    userObj.usernameLower = userObj.username.toLowerCase();
                    userObj.nickname = userObj.username;

                    DatabaseHelper.updateUser(userObj.uid, userObj);
                    try {
                        KeyStore keyStore = EncryptionHelper.getKeystore();
                        EncryptionHelper.generateKeyPair(keyStore, RegisterActivity.this, user);
                    } catch (IOException | CertificateException | InvalidAlgorithmParameterException
                            | NoSuchProviderException | NoSuchAlgorithmException
                            | KeyStoreException e) {
                        e.printStackTrace();
                    }


                    Intent authenticatedIntent = new Intent(
                            RegisterActivity.this,
                            getLoggedInDestination()
                    );
                    RegisterActivity.this.startActivity(authenticatedIntent);
                }
            }
        };
    }
}
