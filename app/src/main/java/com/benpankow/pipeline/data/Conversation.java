package com.benpankow.pipeline.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ben Pankow on 12/5/17.
 */

public class Conversation {
    List<String> participants;

    public void addParticipants(String... uids) {
        if (participants == null) {
            participants = new ArrayList<>();
        }
        for (String uid : uids) {
            participants.add(uid);
        }
    }

    public String getConversationName() {
        return "";
    }
}
