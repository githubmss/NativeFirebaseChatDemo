package com.mss.myfirebasedemo.model;

import java.io.Serializable;

/**
 * Created by deepakgupta on 9/2/17.
 */

public class FromModel implements Serializable {
    String name;
    String email;
    String uid;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
