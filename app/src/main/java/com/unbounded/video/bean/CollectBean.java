package com.unbounded.video.bean;

/**
 * Created by zjf on 2017/7/31 0031.
 * 收藏javaBean
 */

public class CollectBean {
    String userImg;
    String collectorTime;
    String timeLong;
    String uploadImg;
    String name;
    String likeCount;
    String id;//视频或者图片的id
    String openCount;
    String userName;
    String type;   //image/video
    String uuid;//收藏id
    String introduction;

    public CollectBean(String userImg, String collectorTime, String timeLong, String uploadImg, String name, String likeCount, String id, String openCount, String userName, String type, String uuid, String introduction) {
        this.userImg = userImg;
        this.collectorTime = collectorTime;
        this.timeLong = timeLong;
        this.uploadImg = uploadImg;
        this.name = name;
        this.likeCount = likeCount;
        this.id = id;
        this.openCount = openCount;
        this.userName = userName;
        this.type = type;
        this.uuid = uuid;
        this.introduction = introduction;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public String getCollectorTime() {
        return collectorTime;
    }

    public void setCollectorTime(String collectorTime) {
        this.collectorTime = collectorTime;
    }

    public String getTimeLong() {
        return timeLong;
    }

    public void setTimeLong(String timeLong) {
        this.timeLong = timeLong;
    }

    public String getUploadImg() {
        return uploadImg;
    }

    public void setUploadImg(String uploadImg) {
        this.uploadImg = uploadImg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOpenCount() {
        return openCount;
    }

    public void setOpenCount(String openCount) {
        this.openCount = openCount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
}
