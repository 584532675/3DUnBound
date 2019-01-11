package com.unbounded.video.bean;

/**
 * Created by Administrator on 2017/9/2.
 */

public class ThreeddPic {
    String id;
    String name;
    String imgurl;

    public ThreeddPic(String id, String name, String imgurl) {
        this.id = id;
        this.name = name;
        this.imgurl = imgurl;
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

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }
}
