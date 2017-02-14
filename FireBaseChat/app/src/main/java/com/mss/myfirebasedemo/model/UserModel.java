package com.mss.myfirebasedemo.model;

import java.io.Serializable;

/**
 * Created by deepakgupta on 9/2/17.
 */

public class UserModel implements Serializable {
    String email;
    String uid;
    ProfileModel profile;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ProfileModel getProfile() {
        return profile;
    }

    public void setProfile(ProfileModel profile) {
        this.profile = profile;
    }
}
