package com.unbounded.video.utils.imageLoad;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.unbounded.video.R;

import java.io.File;


/**
 * com.best.bestedu.framework.utils
 * create for
 * Created by Administrator on 2016/8/15.
 */
public class GlideLogic {

    /**
     * 普通图片加载  指定大小压缩  不缓存内存  宽：高=3:4
     * @param context
     * @param url
     * @param view
     */
    public static void glideLoadPic324(Context context, String url, ImageView view, int width, int height){
        Glide.with(context)
                .load(url)
                .override(width,height)
                .centerCrop()
                .placeholder(R.mipmap.defaultloading_threefourimg)
                .error(R.mipmap.defaulterror_threefourimg)
                .dontAnimate()//防止图片变形
                //不缓存内存
                .skipMemoryCache(true)
                //设置下载的图片是否缓存在SD卡中
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(view);
    }
    /**
     * 普通图片加载  指定大小压缩  缓存内存  宽：高=3:4
     * @param context
     * @param url
     * @param view
     */
    public static void glideLoadPicMemoryCache324(Context context, String url, ImageView view, int width, int height){
        Glide.with(context)
                .load(url)
                .override(width,height)
                .centerCrop()
                .placeholder(R.mipmap.defaultloading_threefourimg)
                .error(R.mipmap.defaulterror_threefourimg)
                .dontAnimate()//防止图片变形
                //设置下载的图片是否缓存在SD卡中
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(view);
    }

    /**  宽：高=3:4
     *
     * @param fragment
     * @param url
     * @param view
     * @param width
     * @param height
     */
    public static void glideLoadPicWithFragment324(Fragment fragment, String url, ImageView view, int width, int height){
        Glide.with(fragment)
                .load(url)
                .override(width,height)
                .centerCrop()
                .placeholder(R.mipmap.defaultloading_threefourimg)
                .error(R.mipmap.defaulterror_threefourimg)
               /* .crossFade()
                .centerCrop()*/
                .skipMemoryCache(true)
                //设置下载的图片是否缓存在SD卡中
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(view);
    }
    /**
     * 普通图片加载  指定大小压缩  不在内存缓存  宽：高=4:3
     * @param context
     * @param url
     * @param view
     */
    public static void glideLoadPic423(Context context, String url, ImageView view, int width, int height){
        Glide.with(context)
                .load(url)
                .override(width,height)
                .centerCrop()
                .placeholder(R.mipmap.defaultloading_fourthreeimg)
                .error(R.mipmap.defaulterror_fourthreeimg)
                .dontAnimate()//防止图片变形
                //不在内存缓存
                .skipMemoryCache(true)
                //设置下载的图片是否缓存在SD卡中
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(view);
    }
    /**
     * 普通图片加载  指定大小压缩  不在内存缓存  宽：高=4:3 加载drawable图片
     * @param context
     * @param url
     * @param view
     */
    public static void glideLoadPic423(Context context, int url, ImageView view, int width, int height){
        Glide.with(context)
                .load(url)
                .override(width,height)
                .centerCrop()
                .placeholder(R.mipmap.defaultloading_fourthreeimg)
                .error(R.mipmap.defaulterror_fourthreeimg)
                .dontAnimate()//防止图片变形
                //不在内存缓存
                .skipMemoryCache(true)
                //设置下载的图片是否缓存在SD卡中
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(view);
    }

    /**
     * 普通图片加载  指定大小压缩  内存缓存  宽：高=4:3
     * @param context
     * @param url
     * @param view
     */
    public static void glideLoadPicMemoryCache423(Context context, String url, ImageView view, int width, int height){
        Glide.with(context)
                .load(url)
                .override(width,height)
                .centerCrop()
                .placeholder(R.mipmap.defaultloading_fourthreeimg)
                .error(R.mipmap.defaulterror_fourthreeimg)
                .dontAnimate()//防止图片变形
                //设置下载的图片是否缓存在SD卡中
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(view);
    }

    public static void glideLoadPic423fit(Context context, String url, ImageView view, int width, int height){
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .override(width,height)
                .fitCenter()
                .placeholder(R.mipmap.defaultloading_fourthreeimg)
                .error(R.mipmap.defaulterror_fourthreeimg)
                .into(view);
    }

    /**   宽：高=4:3
     *
     * @param fragment
     * @param url
     * @param view
     * @param width
     * @param height
     */
    public static void glideLoadPicWithFragment423(Fragment fragment, String url, ImageView view, int width, int height){
        Glide.with(fragment)
                .load(url)
                .override(width,height)
                .centerCrop()
                .placeholder(R.mipmap.defaultloading_fourthreeimg)
                .error(R.mipmap.defaulterror_fourthreeimg)
               /* .crossFade()
                .centerCrop()*/
                .skipMemoryCache(true)
                //设置下载的图片是否缓存在SD卡中
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(view);
    }

    public static void glideLoadPicNoDefault(Context context, String url, ImageView view){
        Glide.with(context)
                .load(url)
                /*.crossFade()
                .centerCrop()*/
                .into(view);
    }

    /**
     * 加载头像（圆形）
     * @param context
     * @param url
     * @param view
     */
    public static void glideLoadHeadPic(Context context, String url, ImageView view, int width, int height){
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.RESULT )
                .override(width,height)
                .centerCrop()
                .placeholder(R.mipmap.head_noimg)
                .error(R.mipmap.head_noimg)
                /*.crossFade()
                .centerCrop()*/
                .transform(new GlideCircleTransform(context))
                .dontAnimate()
                .into(view);
    }
    /**
     * 加载头像（圆角）
     * @param context
     * @param url
     * @param view
     */
    public static void glideLoadRoundHeadPic(Context context, String url, ImageView view, int width, int height){
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.RESULT )
                .override(width,height)
                .centerCrop()
                .placeholder(R.mipmap.head_default)
                .error(R.mipmap.head_default)
                /*.crossFade()
                .centerCrop()*/
                .transform(new GlideRoundTransform(context, 10))
                .dontAnimate()
                .into(view);
    }

    /**
     * 根据Uri加载
     */
    public static void glideLoadHeadPicUri(Context context, Uri uri, ImageView view, int width, int height){
        Glide.with(context)
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.RESULT )
                .transform(new GlideCircleTransform(context))
                .override(width,height)
                .centerCrop()
                .placeholder(R.mipmap.head_noimg)
                .error(R.mipmap.head_noimg)
                /*.crossFade()
                .centerCrop()*/
                .into(view);
    }

    /**
     * 加载头像
     * @param
     * @param url
     * @param view
     */
    public static void glideLoadHeadPicWithFragment(Fragment fragment, String url, ImageView view, int width, int height){
        Glide.with(fragment)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.RESULT )
                .transform(new GlideCircleTransform(fragment.getContext()))
                .override(width,height)
                .centerCrop()
                .placeholder(R.mipmap.head_noimg)
                .error(R.mipmap.head_noimg)
                /*.crossFade()
                .centerCrop()*/
                .into(view);
    }

    public static void glideLoadCircle(Context context, String url, ImageView view, int width, int height){
        Glide.with(context)
                .load(url)
                .override(width,height)
                .placeholder(R.mipmap.default_logo)
                .error(R.mipmap.default_logo)
                /*.crossFade()
                .centerCrop()*/
                .into(view);
    }


    /**
     * 普通图片加载
     * @param context
     * @param view
     */
    public static void glideLoadHeadPic(Context context, Uri uri, ImageView view, int width, int height){
        Glide.with(context)
                .load(uri)
                .override(width,height)
                .placeholder(R.mipmap.head_default)
                .error(R.mipmap.head_default)
                /*.crossFade()
                .centerCrop()*/
                .into(view);
    }

    /**
     * 传入transformation加载圆角图片
     * @param context
     * @param url
     * @param view
     */
    public static void glideLoadRoundPic(Context context, String url, ImageView view){
        RequestManager glideRequest;
        glideRequest = Glide.with(context);
        glideRequest.load(url)
                .transform(new GlideRoundTransform(context))
                .placeholder(R.mipmap.default_logo)
                .error(R.mipmap.default_logo)
                /*.crossFade()
                .centerCrop()*/
                .into(view);
    }

    /**
     * 传入transformation加载圆形图片
     * @param context
     * @param url
     * @param view
     */
    public static void glideLoadCirclePic(final Context context, String url, ImageView view){
        RequestManager glideRequest;
        glideRequest = Glide.with(context);
        glideRequest.load("https://www.baidu.com/img/bdlogo.png")
                .transform(new GlideCircleTransform(context))
                .placeholder(R.mipmap.head_default)
                .error(R.mipmap.head_default)
                .into(view);
    }

    /**
     * 加载本地视频缩略图,指定大小压缩    宽：高=4:3
     */
    public static void glideLoadVideoImg423(Context context, File mVideoFile, ImageView view, int width, int height){
        //内存缓存
        Glide.with(context).load(mVideoFile)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .override(width,height)
                .centerCrop()
                .placeholder(R.mipmap.defaultloading_fourthreeimg)
                .error(R.mipmap.defaulterror_fourthreeimg)
                .into(view);
    }
    /**
     * 加载本地视频缩略图,指定大小压缩    宽：高=3:4
     */
    public static void glideLoadVideoImg324(Context context, File mVideoFile, ImageView view, int width, int height){
        //内存缓存
        Glide.with(context).load(mVideoFile)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .override(width,height)
                .centerCrop()
                .placeholder(R.mipmap.defaultloading_threefourimg)
                .error(R.mipmap.defaulterror_threefourimg)
                .into(view);
    }

    /**
     * 加载本地视频缩略图,指定大小压缩     宽：高=4:3
     */
    public static void glideLoadVideoImgWithFragment423(Fragment fragment, File mVideoFile, ImageView view, int width, int height){
        //内存缓存
        Glide.with(fragment).load(mVideoFile)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .override(width,height)
                .centerCrop()
                .placeholder(R.mipmap.defaultloading_fourthreeimg)
                .error(R.mipmap.defaulterror_fourthreeimg)
                .dontAnimate()
                .into(view);
    }

    /**
     * 加载本地视频缩略图,指定大小压缩     宽：高=3:4
     */
    public static void glideLoadVideoImgWithFragment324(Fragment fragment, File mVideoFile, ImageView view, int width, int height){
        //内存缓存
        Glide.with(fragment).load(mVideoFile)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .override(width,height)
                .centerCrop()
                .placeholder(R.mipmap.defaultloading_threefourimg)
                .error(R.mipmap.defaulterror_threefourimg)
                .dontAnimate()
                .into(view);
    }

    /**
     * 加载首页广告,指定大小压缩     宽：高=3:4
     */
    public static void glideLoadVideoImgAdvertising(Context context, String url, ImageView view){
        //内存缓存
        Glide.with(context).load(url)
//                .diskCacheStrategy(DiskCacheStrategy.RESULT)
//                .placeholder(R.mipmap.splash_background)
                .centerCrop()
//                .error(R.mipmap.splash_background)
                .into(view);
    }
}
