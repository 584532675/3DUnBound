package com.unbounded.video.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.unbounded.video.activity.DownloadListActivity;
import com.unbounded.video.bean.VideoDownBean;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.utils.FileDownloaderManager;
import com.unbounded.video.utils.JsonUtil;
import com.unbounded.video.utils.SharedPrefsUtil;
import com.unbounded.video.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjf on 2017/7/20.
 */

public class DownloadService extends Service {
    static String temppath = ".temp";
    static String videoRoot = Environment.getExternalStorageDirectory().getAbsolutePath()+"/moyan/videos/";
    static String picRoot = Environment.getExternalStorageDirectory().getAbsolutePath()+"/moyan/images/";
    Context context;
    List<VideoDownBean> videoDownlist = new ArrayList<VideoDownBean>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        context = this;
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){

            String videoDownJson = SharedPrefsUtil.getValue(context, Constants.VIDEODOWN_FLAG, "");
            VideoDownBean videoDownBean = (VideoDownBean) intent.getSerializableExtra("videoDownBean");

            //当下载列表不存在,添加进下载列表下载
            if(TextUtils.isEmpty(videoDownJson)){
                if(videoDownlist != null){
                    videoDownlist.clear();
                }
                int downTag = downLoadVideo(videoDownBean);

                videoDownBean.setDownTag(downTag);
                videoDownlist.add(videoDownBean);
                String videojsonStr = JsonUtil.objectToJson(videoDownlist);
                SharedPrefsUtil.putValue(context, Constants.VIDEODOWN_FLAG, videojsonStr);
                ToastUtil.showToast(context, com.unbounded.video.R.string.Word_adddownload, ToastUtil.CENTER);
                Log.e("info","videoDownlist.size="+videoDownlist.size());

            //当有下载列表,json转成list
            }else {
                videoDownlist = JsonUtil.videoDownJsonToList(videoDownJson);
                //遍历下载列表是否有要下载的资源id
                for(int i=0;i<videoDownlist.size();i++){
                    //当记录中有,提示
                    if(videoDownBean.getId().equals(videoDownlist.get(i).getId())){

                        ToastUtil.showToast(DownloadService.this, com.unbounded.video.R.string.Word_downloadhave, ToastUtil.CENTER );
                        break;
                    //当遍历到最后一个都没有相同记录时,就添加到下载列表
                    }else if(i == videoDownlist.size() - 1){
                        int downTag = downLoadVideo(videoDownBean);
                        Log.e("info","videoDownJson !=null");

                        videoDownBean.setDownTag(downTag);
                        videoDownlist.add(videoDownBean);
                        String videojsonStr = JsonUtil.objectToJson(videoDownlist);
                        SharedPrefsUtil.putValue(context, Constants.VIDEODOWN_FLAG, videojsonStr);
                        ToastUtil.showToast(context, com.unbounded.video.R.string.Word_adddownload, ToastUtil.CENTER);
                        break;
                    }
                }
            }


        }



        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 下载视频方法
     */
    public static int downLoadVideo(VideoDownBean videoDownBean){
        int i = FileDownloaderManager.getInstance().startDownLoadFileSingle(videoDownBean.getUploadUrl(), getVideoDownPath(videoDownBean.getUploadUrl()), new DownloadListActivity.MyFileDownLoaderCallBack(videoDownBean.getId()));
        return i;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    /**
     * 获取视频存储路径
     */
    public static String getVideoDownPath(String url){
        String url1 = url;
        String c = url1.substring(url1.lastIndexOf("/"));
        return videoRoot + c;
    }
    /**
     * 获取视频缓存文件存储路径
     */
    public static String getVideoTempPath(String url){
        String url1 = url;
        String c = url1.substring(url1.lastIndexOf("/"));
        return videoRoot + c + temppath;
    }
    public static String getImageDownPath(String url){
        String url1 = url;
        String c = url1.substring(url1.lastIndexOf("/"));
        return picRoot + c;
    }
}
