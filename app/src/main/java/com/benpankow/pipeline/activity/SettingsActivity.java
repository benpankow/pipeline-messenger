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
    private Button btnSaveSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        etNickname = findViewById(R.id.et_nickname);

        btnSaveSettings = findViewById(R.id.btn_save_settings);
        btnSaveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String intendedNickname = etNickname.getText().toString().trim();;
                if (intendedNickname.length() > 0) {
                    userData.nickname = intendedNickname;
                }
                DatabaseHelper.updateUser(userData.uid, userData);
                finish();
            }
        });

        btnLogOut = findViewById(R.id.btn_log_out);
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
