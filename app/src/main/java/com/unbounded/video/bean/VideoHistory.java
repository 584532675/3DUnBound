package com.unbounded.video.bean;

import java.io.Serializable;

/**
 * Created by zjf on 2017/7/27 0027.
 * 在线视频观看记录javabean
 */

public class VideoHistory implements Serializable{
    public final static int View_Over = -1;
    public final static int Select = 1;
    public final static int UnSelect = 0;
    public final static int CanView= 3;
    public final static int UnView = 4;

    String id;//视频id
    String headImg;//作者头像
    String authorName;//作者名称
    String videoImg;//视频图
    String videoName;//视频名
    String playTime;//最近播放时间
    String videoUrl;//播放地址
    int playPosition;
    int isselect;
    int ischeckVisible;//checkbox是否可见

    public VideoHistory(String id, String headImg, String authorName, String videoImg, String videoName, String playTime, String videoUrl, int isselect, int ischeckVisible, int playPosition) {
        this.id = id;
        this.headImg = headImg;
        this.authorName = authorName;
        this.videoImg = videoImg;
        this.videoName = videoName;
        this.playTime = playTime;
        this.videoUrl = videoUrl;
        this.isselect = isselect;
        this.ischeckVisible = ischeckVisible;
        this.playPosition = playPosition;
    }

    public int getIscheckVisible() {
        return ischeckVisible;
    }

    public void setIscheckVisible(int ischeckVisible) {
        this.ischeckVisible = ischeckVisible;
    }

    public int getIsselect() {
        return isselect;
    }

    public void setIsselect(int isselect) {
        this.isselect = isselect;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getVideoImg() {
        return videoImg;
    }

    public void setVideoImg(String videoImg) {
        this.videoImg = videoImg;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getPlayTime() {
        return playTime;
    }

    public void setPlayTime(String playTime) {
        this.playTime = playTime;
    }

    public int getPlayPosition() {
        return playPosition;
    }

    public void setPlayPosition(int playPosition) {
        this.playPosition = playPosition;
    }
}
