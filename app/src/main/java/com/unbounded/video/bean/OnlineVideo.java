package com.unbounded.video.bean;

import java.io.Serializable;

/**
 * Created by zjf on 2017/7/21 0021.
 */

public class OnlineVideo implements Serializable{
    String userImg;//作者头像
    String timeLong;//视频时长
    String opencount;//观看次数
    String name;//视频名称
    String uploadimg;
    String id;//视频ID
    String userName;//用户名称
    String userId;//作者ID
    String introduction;//视频简介
    String report;//1和FREE是免费    0和NOT_FREE是收费
    String uploadUrl;
    public OnlineVideo(String name, String uploadimg, String id,  String userId, String introduction) {
        this.name = name;
        this.uploadimg = uploadimg;
        this.id = id;
        this.userId = userId;
        this.introduction = introduction;
    }
    public OnlineVideo(String name, String uploadimg, String id,  String userId, String introduction,String uploadUrl) {
        this.name = name;
        this.uploadimg = uploadimg;
        this.id = id;
        this.userId = userId;
        this.introduction = introduction;
        this.uploadUrl = uploadUrl;
    }

    public OnlineVideo(String userImg, String timeLong, String opencount, String name, String uploadimg, String id, String userName, String userId, String introduction, String report) {
        this.userImg = userImg;
        this.timeLong = timeLong;
        this.opencount = opencount;
        this.name = name;
        this.uploadimg = uploadimg;
        this.id = id;
        this.userName = userName;
        this.userId = userId;
        this.introduction = introduction;
        this.report = report;
    }
    public OnlineVideo(String userImg, String timeLong, String opencount, String name, String uploadimg, String id, String userName, String userId, String introduction) {
        this.userImg = userImg;
        this.timeLong = timeLong;
        this.opencount = opencount;
        this.name = name;
        this.uploadimg = uploadimg;
        this.id = id;
        this.userName = userName;
        this.userId = userId;
        this.introduction = introduction;
    }


    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public String getTimeLong() {
        return timeLong;
    }

    public void setTimeLong(String timeLong) {
        this.timeLong = timeLong;
    }

    public String getOpencount() {
        return opencount;
    }

    public void setOpencount(String opencount) {
        this.opencount = opencount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUploadimg() {
        return uploadimg;
    }

    public void setUploadimg(String uploadimg) {
        this.uploadimg = uploadimg;
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

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }
}
