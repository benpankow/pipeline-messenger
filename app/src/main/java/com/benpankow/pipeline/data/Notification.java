package com.benpankow.pipeline.data;

/**
 * Created by Ben Pankow on 11/15/17.
 *
 * Holds data related to a notification, which should be delivered to another user or users.
 */
public class Notification {

    public String recipient;
    public String message;
    public String sender;
    public String convoid;

    public Notification() {}

    public Notification(String recipient, String message, String sender, String convoid) {
        this.recipient = recipient;
        this.message = message;
        this.sender = sender;
        this.convoid = convoid;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public String getConvoid() {
        return convoid;
    }
}
