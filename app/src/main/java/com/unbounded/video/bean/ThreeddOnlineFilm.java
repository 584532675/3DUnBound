package com.unbounded.video.bean;

/**
 * Created by zjf on 2017/9/1 0001.
 */

public class ThreeddOnlineFilm {
    int connerMark;//角标
    int duration;//时长
    String fid;//视频FID
    int is3d;//1只允许3D  2只允许2D  3：3D和2D都允许 4只允许VR 5：3D和VR 6：2D和VR 7：3D和2D和VR 8：全景 9:3D全景 10：2D全景 12：VR全景 15：3D2DVR全景 16：2D源
    String vname;//视频名称
    String posterfid;//海报图片fid（16:9）
    String posterfid2;//海报图片fid（3:4）
    String vfid;//播放地址

    public ThreeddOnlineFilm(int connerMark, int duration, String fid, int is3d, String vname, String posterfid, String posterfid2, String vfid) {
        this.connerMark = connerMark;
        this.duration = duration;
        this.fid = fid;
        this.is3d = is3d;
        this.vname = vname;
        this.posterfid = posterfid;
        this.posterfid2 = posterfid2;
        this.vfid = vfid;
    }

    public int getConnerMark() {
        return connerMark;
    }

    public void setConnerMark(int connerMark) {
        this.connerMark = connerMark;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public int getIs3d() {
        return is3d;
    }

    public void setIs3d(int is3d) {
        this.is3d = is3d;
    }

    public String getVname() {
        return vname;
    }

    public void setVname(String vname) {
        this.vname = vname;
    }

    public String getPosterfid() {
        return posterfid;
    }

    public void setPosterfid(String posterfid) {
        this.posterfid = posterfid;
    }

    public String getPosterfid2() {
        return posterfid2;
    }

    public void setPosterfid2(String posterfid2) {
        this.posterfid2 = posterfid2;
    }

    public String getVfid() {
        return vfid;
    }

    public void setVfid(String vfid) {
        this.vfid = vfid;
    }
}
