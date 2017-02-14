package com.mss.myfirebasedemo.model;

import java.io.Serializable;

/**
 * Created by deepakgupta on 13/2/17.
 */

public class ProfileModel implements Serializable {
    String name, desc, profilePic;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
