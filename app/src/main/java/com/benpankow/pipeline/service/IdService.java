package com.benpankow.pipeline.service;

import android.util.Log;

import com.benpankow.pipeline.helper.DatabaseHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Ben Pankow on 11/15/17.
 *
 * Keeps track of the client ID for each user
 *
 * Based on https://firebase.google.com/docs/cloud-messaging/android/client#sample-register
 */
public class IdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated device token, which identifies the device the user is on
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // Update token in database
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseHelper.updateDeviceToken(user.getUid(), refreshedToken);
        }
    }
}
