package com.benpankow.pipeline.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.benpankow.pipeline.R;
import com.benpankow.pipeline.activity.base.UnauthenticatedActivity;
import com.benpankow.pipeline.helper.AuthenticationHelper;

/**
 * Created by Ben Pankow on 12/2/17.
 *
 * Handles logging in users.
 */
public class LoginActivity extends UnauthenticatedActivity {

    private Button btnLogin;
    private EditText etEmail;
    private EditText etPassword;
    private TextView tvSignUp;
    private String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            if (intent.getExtras().containsKey("convoid")) {
                Intent convoIntent =
                        new Intent(LoginActivity.this, ConversationActivity.class);
                convoIntent.putExtra("convoid", intent.getExtras().getString("convoid"));
                LoginActivity.this.startActivity(convoIntent);
                return;
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvSignUp = findViewById(R.id.tv_sign_up);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);

        btnLogin = findViewById(R.id.btn_login);

        // Attempt to login on button press
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                AuthenticationHelper.login(LoginActivity.this, email, password);
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUpIntent =
                        new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(signUpIntent);
            }
        });
    }
}
