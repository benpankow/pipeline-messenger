package com.benpankow.pipeline.data;

import android.content.Context;
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

    /**
     * Gets the most recent message sent to the given user
     *
     * @param uid The user whose preview message instance to get
     * @return The most recent message in this conversation for the given user
     */
    public Message getRecentMessage(String uid) {
        if (recentMessages == null) {
            recentMessages = new HashMap<>();
        }
        if (recentMessages.containsKey(uid)) {
            return recentMessages.get(uid);
        }
        return null;
    }

    /**
     * Adds a list of participants to this conversation
     *
     * @param uids A list of uids to add
     */
    public void addParticipants(String... uids) {
        if (participants == null) {
            participants = new ArrayList<>();
        }
        participants.addAll(Arrays.asList(uids));
    }

    /**
     * Gets the title for this conversation - either auto-generated or one that was user-set
     *
     * @param callback A callback that returns the title
     */
    public void getTitle(Consumer<String> callback) {
        if (title != null) {
            callback.accept(title);
        } else {
            generateTitle(callback);
        }
    }

    /**
     * Generates a default title for a conversation - the user's names separated w/ commas and &
     * i.e., Ben, Joe, Eric & Brian
     *
     * @param callback A callback that takes the generated title
     */
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

    /**
     * Get the preview text for this conversatin, automatically decrypting it
     *
     * @param uid The current user's uid
     * @param context The current application context
     * @return A plaintext, decrypted version of the most recent message
     */
    public String getPreviewMessage(String uid, Context context) {
        Message previewMessage = getRecentMessage(uid);
        if (previewMessage != null) {
            return previewMessage.decrypt(context);
        } else {
            return "";
        }
    }

    /**
     * Get a string version of this conversation's timestamp in the format HH:MM AM/PM
     *
     * @param uid The current user's uid
     * @return A formatted timestamp
     */
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

    /**
     * Get the Date this conversation was last updated
     *
     * @return The last updated Date
     */
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
