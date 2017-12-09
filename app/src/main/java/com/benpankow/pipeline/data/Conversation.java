package com.benpankow.pipeline.data;

import android.widget.TextView;

import com.benpankow.pipeline.helper.DatabaseHelper;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import java8.util.function.Consumer;

/**
 * Created by Ben Pankow on 12/5/17.
 *
 * Stores data relating to a specific conversation
 */

public class Conversation {

    public List<String> participants;
    public String title;
    public String convoid;
    public HashMap<String, Message> recentMessages;
    public Object timestamp;

    public Message getRecentMessage(String uid) {
        if (recentMessages == null) {
            recentMessages = new HashMap<>();
        }
        if (recentMessages.containsKey(uid)) {
            return recentMessages.get(uid);
        }
        return null;
    }

    public void addParticipants(String... uids) {
        if (participants == null) {
            participants = new ArrayList<>();
        }
        participants.addAll(Arrays.asList(uids));
    }

    public void getTitle(Consumer<String> callback) {
        if (title != null) {
            callback.accept(title);
        } else {
            generateTitle(callback);
        }
    }

    public void generateTitle(final Consumer<String> callback) {
        String uid = FirebaseAuth.getInstance().getUid();

        final List<String> otherParticipants = new ArrayList<>(participants);
        otherParticipants.remove(uid);

        final int[] counter = { otherParticipants.size() - 1 };
        final String[] title = { "" };

        for (String participantUid : otherParticipants) {
            DatabaseHelper.getUser(participantUid, new Consumer<User>() {
                @Override
                public void accept(User u) {
                    if (title[0].length() > 0) {
                        if (otherParticipants.size() > 2) {
                            title[0] += ", ";
                        } else {
                            title[0] += " ";
                        }
                    }
                    if (counter[0] == 0 && otherParticipants.size() > 1) {
                        title[0] += "and ";
                    }
                    title[0] += u.nickname;
                    if (counter[0] == 0) {
                        callback.accept(title[0]);
                    }
                    counter[0]--;
                }
            });
        }
    }

    public String getPreviewMessage(String uid) {
        Message previewMessage = getRecentMessage(uid);
        if (previewMessage != null) {
            return previewMessage.text;
        } else {
            return "";
        }
    }

    public String getTimestamp(String uid) {
        Date date = getDate();
        if (date == null) {
            return "";
        }

        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();

        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.US);
        sdf.setTimeZone(tz);
        return sdf.format(date);
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
