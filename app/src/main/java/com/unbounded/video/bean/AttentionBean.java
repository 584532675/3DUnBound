package com.unbounded.video.bean;

/**
 * Created by zjf on 2017/7/26 0026.
 */

public class AttentionBean {
    String followid;
    String followtime;
    String password;
    String userimg;
    String id;
    String userid;
    String userCase;
    String userfans;
    String account;
    String username;

    public AttentionBean(String id, String userimg, String username) {
        this.id = id;
        this.userimg = userimg;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserimg() {
        return userimg;
    }

    public void setUserimg(String userimg) {
        this.userimg = userimg;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
