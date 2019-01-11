package com.unbounded.video.bean;

/**
 * Created by zjf on 2017/8/6.
 */

public class Mymsg {
    String msg;
    String msgtitle;
    String extras;
    String id;
    String title;
    String sendtime;
    String type;

    public Mymsg(String msg, String msgtitle, String extras, String id,String title, String sendtime, String type) {
        this.msg = msg;
        this.msgtitle = msgtitle;
        this.extras = extras;
        this.title = title;
        this.id = id;
        this.sendtime = sendtime;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsgtitle() {
        return msgtitle;
    }

    public void setMsgtitle(String msgtitle) {
        this.msgtitle = msgtitle;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSendtime() {
        return sendtime;
    }

    public void setSendtime(String sendtime) {
        this.sendtime = sendtime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
