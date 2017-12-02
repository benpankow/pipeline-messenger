package com.benpankow.pipeline;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.benpankow.pipeline.activity.base.UnauthenticatedActivity;

/**
 * Created by Ben Pankow on 12/2/17.
 */
public class LoginActivity extends UnauthenticatedActivity {

    private Button btnLogin;
    private EditText etEmail;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = (EditText) findViewById(R.id.et_email);
        etPassword = (EditText) findViewById(R.id.et_password);

        btnLogin = (Button) findViewById(R.id.btn_login);
    }
}
