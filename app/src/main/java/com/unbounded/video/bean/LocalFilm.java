package com.unbounded.video.bean;

import java.io.Serializable;

/**
 * Created by zjf on 2017/5/24 0024.
 */

public class LocalFilm implements Serializable{
    public static final int UNUPLOAD = 0;//没上传
    public static final int UPLOADING = 1;//正在上传
    public static final int UPLOADED = 2;//上传完成
    public static final int UPLOADERROR = 3;//上传失败
    public static final int UPLOAD = 4;//已上传

    int id;
    String filmTitle;//电影名称  (不带后缀名)
    String album;//专辑
    String artist;//作者
    String displayName;//显示名称   (带后缀名)
    String mimeType;//格式，类型
    String path;//路径
    long duration;//时长
    long size;//尺寸
    int progress;//上传进度
    int flag;//

    //本地电影构造方法
    public LocalFilm(int id, String filmTitle, String album, String artist, String displayName, String mimeType, String path, long duration, long size, int progress, int flag) {
        this.id = id;
        this.filmTitle = filmTitle;
        this.album = album;
        this.artist = artist;
        this.displayName = displayName;
        this.mimeType = mimeType;
        this.path = path;
        this.duration = duration;
        this.size = size;
        this.progress = progress;
        this.flag = flag;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilmTitle() {
        return filmTitle;
    }

    public void setFilmTitle(String filmTitle) {
        this.filmTitle = filmTitle;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
