package com.unbounded.video.view.banner;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;


public class GlideImageLoader implements ImageLoaderInterface {

    @Override
    public void displayImage(Context context, Object path, View imageView) {
        //具体方法内容自己去选择，次方法是为了减少banner过多的依赖第三方包，所以将这个权限开放给使用者去选择
        Glide.with(context).load((String)path).centerCrop().placeholder(com.unbounded.video.R.mipmap.defaultloading_fourthreeimg).error(com.unbounded.video.R.mipmap.defaulterror_fourthreeimg).into((ImageView) imageView);
    }

    @Override
    public View createImageView(Context context) {
        //加载圆角图片
//        RoundImageView imageView = new RoundImageView(context);
//        imageView.setType(RoundImageView.TYPE_ROUND);

        ImageView imageView = new ImageView(context);
        return imageView;
    }
}
