package com.benpankow.pipeline.data;

/**
 * Created by Ben Pankow on 12/2/17.
 *
 * Stores data relating to a specific user
 */
public class User {
    public String email;
    public String username;
    public String usernameLower;
    public String uid;
    public String nickname;
    public String deviceToken;
    public String publicKey;

    public User() {}

    public User(String email, String username, String usernameLower, String uid, String nickname, String deviceToken, String publicKey) {
        this.email = email;
        this.username = username;
        this.usernameLower = usernameLower;
        this.uid = uid;
        this.nickname = nickname;
        this.deviceToken = deviceToken;
        this.publicKey = publicKey;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getUsernameLower() {
        return usernameLower;
    }

    public String getUid() {
        return uid;
    }

    public String getNickname() {
        return nickname;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
