package com.benpankow.pipeline.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.benpankow.pipeline.R;
import com.benpankow.pipeline.activity.base.AuthenticatedActivity;
import com.benpankow.pipeline.data.User;
import com.benpankow.pipeline.helper.DatabaseHelper;

public class SettingsActivity extends AuthenticatedActivity {

    private Button btnLogOut;
    private EditText etNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        etNickname = (EditText) findViewById(R.id.et_nickname);

        etNickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                userData.nickname = etNickname.getText().toString();
                DatabaseHelper.updateUser(userData.uid, userData);
            }
        });

        btnLogOut = (Button) findViewById(R.id.btn_log_out);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Code used from
                // https://stackoverflow.com/questions/42571618/how-to-make-a-user-sign-out-in-firebase
                getAuth().signOut();

                // Return to login screen
                Intent loginActivity =
                        new Intent(SettingsActivity.this, LoginActivity.class);
                SettingsActivity.this.startActivity(loginActivity);
            }
        });
    }

    @Override
    protected void onUserDataObtained() {
        super.onUserDataUpdate();
        etNickname.setText(userData.nickname);
    }
}
