package com.unbounded.video.view.banner;

/**
 * Created by Administrator on 2017/7/14 0014.
 */

public class BannerData {
    int id;
    String imgUrl;

    public BannerData(int id, String imgUrl) {
        this.id = id;
        this.imgUrl = imgUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
