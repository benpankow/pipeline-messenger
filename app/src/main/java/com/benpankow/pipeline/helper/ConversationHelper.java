package com.benpankow.pipeline.helper;

import android.content.Context;
import android.content.Intent;

import com.benpankow.pipeline.activity.ConversationActivity;

import java8.util.function.Consumer;

/**
 * Created by Ben Pankow on 12/7/17.
 *
 * Utilities for opening or operating on existing conversations
 */
public class ConversationHelper {


    /**
     * Given two users, will open a conversation between them, creating it if neccesary.
     *
     * @param context A Context instance to create intents from
     * @param currentUid The uid of the logged in user
     * @param targetUid The uid of the intended user to open a conversation with
     */
    public static void openConversationBetween(final Context context,
                                               final String currentUid,
                                               final String targetUid) {
        DatabaseHelper.getConversationBetween(
                currentUid,
                targetUid,
                new Consumer<String>() {
                    @Override
                    public void accept(String convoid) {
                        if (convoid == null) {
                            DatabaseHelper.createConversationBetween(
                                    currentUid,
                                    targetUid,
                                    new Consumer<String>() {
                                        @Override
                                        public void accept(String convoid) {
                                            Intent conversationActivity =
                                                    new Intent(context, ConversationActivity.class);
                                            conversationActivity.putExtra("convoid", convoid);
                                            context.startActivity(conversationActivity);
                                        }
                                    }
                            );
                        } else {
                            Intent conversationActivity =
                                    new Intent(context, ConversationActivity.class);
                            conversationActivity.putExtra("convoid", convoid);
                            context.startActivity(conversationActivity);
                        }
                    }
                });
    }

    /**
     * Creates a group between the given users, and then opens it
     *
     * @param context The context that this action is triggered in
     * @param uids A list of uids of the users to add to the group
     */
    public static void createAndOpenGroup(final Context context,
                                          final String[] uids) {
        DatabaseHelper.createConversation(
                uids,
                new Consumer<String>() {
                    @Override
                    public void accept(String convoid) {
                        Intent conversationActivity =
                                new Intent(context, ConversationActivity.class);
                        conversationActivity.putExtra("convoid", convoid);
                        context.startActivity(conversationActivity);
                    }
                },
                true
        );
    }
}
