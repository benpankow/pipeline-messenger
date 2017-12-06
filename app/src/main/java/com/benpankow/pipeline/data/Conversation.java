package com.benpankow.pipeline.data;

import com.benpankow.pipeline.helper.DatabaseHelper;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import java8.util.function.Consumer;

/**
 * Created by Ben Pankow on 12/5/17.
 */

public class Conversation {

    public List<String> participants;
    public String title;

    public void addParticipants(String... uids) {
        if (participants == null) {
            participants = new ArrayList<>();
        }
        participants.addAll(Arrays.asList(uids));
    }

    public void getTitle(final Consumer<String> callback) {
        if (title != null) {
            callback.accept(title);
            return;
        }
        DatabaseHelper.getUserData(getOtherUid(), new Consumer<User>() {
            @Override
            public void accept(User u) {
                callback.accept(u.nickname);
            }
        });
    }

    private String getOtherUid() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            for (String participantUid : participants) {
                if (!participantUid.equals(uid)) {
                    return participantUid;
                }
            }
        }
        return uid;
    }
}
