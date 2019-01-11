package com.unbounded.video.bean;

/**
 * 评论实体类
 * Created by zjf on 2017/7/24 0024.
 */

public class Discuss {
    String userImg;//用户头像
    String comment;//评论内容
    String id;//评论ID
    String userName;//用户名称
    String userId;//用户ID
    String commentTime;//评论时间

    public Discuss(String userImg, String comment, String id, String userName, String userId, String commentTime) {
        this.userImg = userImg;
        this.comment = comment;
        this.id = id;
        this.userName = userName;
        this.userId = userId;
        this.commentTime = commentTime;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }
}
