package com.unbounded.video.bean;

import java.io.Serializable;

/**
 * Created by zjf on 2017/7/21 0021.
 */

public class VideoDownBean implements Serializable{
    public final static int UNDOWNLOAD = 0;//没下载
    public final static int Downloading = 1;//正在下载
    public final static int DownloadError= 2;//下载错误
    public final static int DownloadPaused = 3;//暂停
    public final static int DownloadPending = 4;//等待
    public final static int DownloadComplated = 5;//完成

    String id;//视频ID
    String imgurl;//视频图片地址
    String name;//视频名称
    String uploadUrl;//下载地址
    int speed;//下载速度
    int progress;//进度
    int flag;//当前状态
    int downTag;//下载的标记
    String totalsize;

    public VideoDownBean(String id, String imgurl, String name,String uploadUrl , int speed, int progress, int flag, String totalsize) {
        this.id = id;
        this.imgurl = imgurl;
        this.name = name;
        this.uploadUrl = uploadUrl;
        this.speed = speed;
        this.progress = progress;
        this.flag = flag;
        this.totalsize = totalsize;
    }

    public String getTotalsize() {
        return totalsize;
    }

    public void setTotalsize(String totalsize) {
        this.totalsize = totalsize;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public int getDownTag() {
        return downTag;
    }

    public void setDownTag(int downTag) {
        this.downTag = downTag;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
