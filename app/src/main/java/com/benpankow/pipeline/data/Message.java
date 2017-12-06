package com.benpankow.pipeline.data;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Ben Pankow on 12/5/17.
 */

public class Message {
    public String senderUid;
    public String text;

    public boolean sentByCurrentUser() {
        return FirebaseAuth.getInstance().getUid().equals(senderUid);
    }
}
