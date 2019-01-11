package com.unbounded.video.bean;

/**
 * Created by zjf on 2017/7/31 0031.
 */

public class OnlinePic {
    String thumbnail;
    String timelong;
    String userimg;
    String opencount;
    String type3d;
    String name;
    String report;
    String uploadimg;
    String id;
    String likecount;
    String introduction;
    String username;
    String uploadtime;

    /**
     * 首页列表
     */
    public OnlinePic(String thumbnail, String timelong, String userimg, String opencount, String type3d, String name, String report, String uploadimg, String id, String likecount, String introduction, String username, String uploadtime) {
        this.thumbnail = thumbnail;
        this.timelong = timelong;
        this.userimg = userimg;
        this.opencount = opencount;
        this.type3d = type3d;
        this.name = name;
        this.report = report;
        this.uploadimg = uploadimg;
        this.id = id;
        this.likecount = likecount;
        this.introduction = introduction;
        this.username = username;
        this.uploadtime = uploadtime;
    }

    /**
     * 搜索时的构造方法
     */
    public OnlinePic(String thumbnail ,String userimg, String opencount, String type3d, String name, String report, String uploadimg, String id, String likecount, String introduction, String username, String uploadtime) {
        this.thumbnail = thumbnail;
        this.userimg = userimg;
        this.opencount = opencount;
        this.type3d = type3d;
        this.name = name;
        this.report = report;
        this.uploadimg = uploadimg;
        this.id = id;
        this.likecount = likecount;
        this.introduction = introduction;
        this.username = username;
        this.uploadtime = uploadtime;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTimelong() {
        return timelong;
    }

    public void setTimelong(String timelong) {
        this.timelong = timelong;
    }

    public String getUserimg() {
        return userimg;
    }

    public void setUserimg(String userimg) {
        this.userimg = userimg;
    }

    public String getOpencount() {
        return opencount;
    }

    public void setOpencount(String opencount) {
        this.opencount = opencount;
    }

    public String getType3d() {
        return type3d;
    }

    public void setType3d(String type3d) {
        this.type3d = type3d;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
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

    public String getLikecount() {
        return likecount;
    }

    public void setLikecount(String likecount) {
        this.likecount = likecount;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUploadtime() {
        return uploadtime;
    }

    public void setUploadtime(String uploadtime) {
        this.uploadtime = uploadtime;
    }
}
