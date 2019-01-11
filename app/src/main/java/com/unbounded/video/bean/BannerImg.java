package com.unbounded.video.bean;

/**
 * Created by zjf on 2017/7/25 0025.
 * 广告图片
 */

public class BannerImg {
    String imgurl;
    String type;
    String url;

    public BannerImg(String imgurl, String type, String url) {
        this.imgurl = imgurl;
        this.type = type;
        this.url = url;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
