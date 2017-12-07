package com.benpankow.pipeline.data;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;

/**
 * Created by Ben Pankow on 12/5/17.
 *
 * Stores data relating to a specific message
 */

public class Message {
    public String senderUid;
    public String text;
    public Object timestamp;

    public boolean sentByCurrentUser() {
        return FirebaseAuth.getInstance().getUid().equals(senderUid);
    }

    public Date getDate(){
        if (timestamp == null) {
            return null;
        }
        if (timestamp instanceof Long) {
            return new Date((long) timestamp);
        } else {
            return null;
        }
    }


}
