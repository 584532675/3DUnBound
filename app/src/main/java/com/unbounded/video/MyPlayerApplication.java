package com.unbounded.video;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;

import com.liulishuo.filedownloader.FileDownloader;
import com.mob.MobApplication;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.unbounded.video.activity.SplashActivity;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.constants.HttpConstants;
import com.unbounded.video.utils.ACache;
import com.unbounded.video.utils.HttpGetUtil;
import com.unbounded.video.utils.SharedPrefsUtil;
import com.unbounded.video.view.banner.GlideImageLoader;
import com.youzan.androidsdk.YouzanSDK;
import com.youzan.androidsdk.basic.YouzanBasicSDKAdapter;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by zjf on 2017/7/14 0014.
 */

public class MyPlayerApplication extends MobApplication {
    private String TAG ="MyPlayerApplication";
    public static final int USER_TIME = 1;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case USER_TIME:
                    ACache aCache  =ACache.get(MyPlayerApplication.this);
                    aCache.put(Constants.USER_REDUCE_TIME, 0);
                    aCache.put(Constants.TIME_USER_ID,"");
                    aCache.put(Constants.START_USER_TIME,System.currentTimeMillis());
                    break;
            }
            super.handleMessage(msg);
        }
    };
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化友盟
        UMConfigure.init(this,UMConfigure.DEVICE_TYPE_PHONE,null);
        MobclickAgent.setScenarioType(this,MobclickAgent.EScenarioType.E_UM_NORMAL);
        //secret未申请
//        MobclickAgent.setSecret(this, "s10bacedtyz");

        JPushInterface.setDebugMode(false);/*设置为true，会第一次进入弹出定位权限框*/
        JPushInterface.init(this);
//        /**
//         * 仅仅是缓存Application的Context，不耗时
//         */
        FileDownloader.init(getApplicationContext());
//        CrashReport.initCrashReport(getApplicationContext(),"229768cf95",true);
        YouzanSDK.init(this, Constants.YOU_ZAN_MALL_CLIENT_ID, new YouzanBasicSDKAdapter());
        /*获取当前时间*/
        ACache aCache =ACache.get(this);
        Object time = aCache.getAsObject(Constants.USER_REDUCE_TIME);
        int tiem = 0;
        if(time!=null){
            tiem = (int) time;
        }

        String tiemUserId = aCache.getAsString(Constants.TIME_USER_ID);
        Log.v("userTime", "" + tiem);
        Log.v("tiemuserid", "" + tiemUserId);
        if (tiem != 0 && tiemUserId != null && !tiemUserId.equals("")) {
            HttpGetUtil userTime = new HttpGetUtil(handler, HttpConstants.USER_TIME + "?onlinetime=" + tiem + "&userid=" + tiemUserId, USER_TIME);
            Thread timeThread = new Thread(userTime);
            timeThread.start();
        }else{
            aCache.put(Constants.USER_REDUCE_TIME, 0);
            aCache.put(Constants.TIME_USER_ID,"");
            aCache.put(Constants.START_USER_TIME,System.currentTimeMillis());
        }
    }


//    public static HttpProxyCacheServer getProxy(Context context) {
//        MyPlayerApplication app = (MyPlayerApplication) context.getApplicationContext();
//        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
//    }
//
//
//    private HttpProxyCacheServer newProxy() {
//        return new HttpProxyCacheServer.Builder(this)
//                .maxCacheSize(1024 * 1024 * 1024)       // 1 Gb for cache
//                .build();
//    }

}
