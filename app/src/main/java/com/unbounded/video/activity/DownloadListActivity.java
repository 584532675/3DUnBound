package com.unbounded.video.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.unbounded.video.BaseActivity;
import com.unbounded.video.R;
import com.unbounded.video.bean.VideoDownBean;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.service.DownloadService;
import com.unbounded.video.utils.FileDownloaderManager;
import com.unbounded.video.utils.JsonUtil;
import com.unbounded.video.utils.SharedPrefsUtil;
import com.unbounded.video.utils.ToastUtil;
import com.unbounded.video.utils.imageLoad.GlideLogic;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjf on 2017/7/20 0020.
 */

public class DownloadListActivity extends BaseActivity {
    static Context context;
    static long  mLastFrameSavedTime = 0;
    static long  mCurrentFrameSavedTime = 0;
    static long  minusTime = 500;
    static ListView lv;
    static MyDownAdapter adapter;
    int deleteposition;
    public static List<VideoDownBean> downBeanlist = new ArrayList<VideoDownBean>();
    static String videoDownJson;
    static NumberFormat numberFormat = NumberFormat.getInstance();
    Dialog deleteDialog,deleteLoadingDialog;
    TextView usedspacetv,spaceavailabletv;
    LinearLayout spacelinear;


    @Override
    protected int getContentView() {
        return R.layout.activity_playerhistory;
    }

    @Override
    public void init(Intent intent) {
        super.init(intent);

        setTitleName(getResources().getString(R.string.word_mydown));

        context = DownloadListActivity.this;
        // 设置精确到小数点后2位

    }

    @Override
    public void initDatas() {
        super.initDatas();

        videoDownJson = SharedPrefsUtil.getValue(DownloadListActivity.this, Constants.VIDEODOWN_FLAG, "");

        Log.e("info","videoDownJson="+videoDownJson);

        //当下载列表不存在,下载列表为空
        if(TextUtils.isEmpty(videoDownJson)){
            setEmptyViewVisible(true);
            setEmptyTvVisible(true);

        }else {
            setEmptyViewVisible(false);
            setEmptyTvVisible(false);

            downBeanlist = JsonUtil.videoDownJsonToList(videoDownJson);

        }

    }

    @Override
    public void initView() {
        super.initView();

        adapter = new MyDownAdapter();
        lv = (ListView) findViewById(R.id.playerhistory_lv);
        usedspacetv = (TextView) findViewById(R.id.history_usedmemorytv);
        spaceavailabletv = (TextView) findViewById(R.id.history_canmemorytv);
        spacelinear = (LinearLayout) findViewById(R.id.history_spacelinear);
        lv.setAdapter(adapter);

        spacelinear.setVisibility(View.VISIBLE);

    }

    @Override
    public void initEvent() {
        super.initEvent();

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                deleteDownload(position);

                Log.e("info","onItemLongClick =" + position);
                return false;
            }
        });
    }

    /**
     * 删除下载方法
     */
    private void deleteDownload(int position){
        VideoDownBean videoDownBean = downBeanlist.get(position);

        if(videoDownBean.getFlag() == VideoDownBean.Downloading || videoDownBean.getFlag() == VideoDownBean.DownloadPending || videoDownBean.getFlag() == VideoDownBean.DownloadPaused){
            //已开始下载，是否删除
            //正在下载的任务还要暂停掉
            FileDownloaderManager.getInstance().pauseDownLoadFile(downBeanlist.get(position).getDownTag());
            setDeleteLoadingDia(position);
        }else {
            //是否确认删除
            deleteDia(position);
        }
    }

    /**
     * 是否删除正在下载的    正在下载的任务还要暂停掉
     */
    public void setDeleteLoadingDia(final int position){
        View diaView = View.inflate(DownloadListActivity.this, R.layout.notice_dialog, null);
        deleteLoadingDialog = new Dialog(DownloadListActivity.this, R.style.dialog);
        deleteLoadingDialog.setContentView(diaView);
        deleteLoadingDialog.setCanceledOnTouchOutside(false);
        deleteLoadingDialog.setCancelable(false);

        Button yesbtn = (Button) deleteLoadingDialog.findViewById(R.id.notice_yesbtn);
        Button exitbtn = (Button) deleteLoadingDialog.findViewById(R.id.notice_canclebtn);
        TextView titletv = (TextView) deleteLoadingDialog.findViewById(R.id.notice_tv1);
        TextView contenttv = (TextView) deleteLoadingDialog.findViewById(R.id.notice_tv2);

        yesbtn.setText(getString(R.string.sure_btn));
        exitbtn.setText(getString(R.string.cancle_btn));
        titletv.setText(getString(R.string.Word_noticetitle));
        contenttv.setText(getString(R.string.Word_deleteloading));


        yesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = DownloadService.getVideoTempPath(downBeanlist.get(position).getUploadUrl());
                File file = new File(path);
                if(file != null && file.exists()){
                    file.delete();
                }

                downBeanlist.remove(position);
                deleteLoadingDialog.dismiss();
                ToastUtil.showToast(getApplicationContext(), R.string.Word_worksuccess, ToastUtil.BOTTOM);

                adapter.notifyDataSetChanged();

                Log.e("info","downBeanlist.size()="+downBeanlist.size());

                if(downBeanlist.size() == 0){
                    SharedPrefsUtil.putValue(DownloadListActivity.this, Constants.VIDEODOWN_FLAG, "");

                    setEmptyViewVisible(true);
                }else {
                    List<VideoDownBean> list1 = new ArrayList<VideoDownBean>();

                    for(int i=0;i<downBeanlist.size();i++){
                        String videoId = downBeanlist.get(i).getId();
                        String uploadImg = downBeanlist.get(i).getImgurl();
                        String name = downBeanlist.get(i).getName();
                        String uploadUrl = downBeanlist.get(i).getUploadUrl();
                        String uploadSize = downBeanlist.get(i).getTotalsize();

                        VideoDownBean bean = new VideoDownBean(videoId, uploadImg, name, uploadUrl, 0, 0, VideoDownBean.UNDOWNLOAD, uploadSize);
                        list1.add(bean);
                    }

                    videoDownJson = JsonUtil.objectToJson(list1);
                    SharedPrefsUtil.putValue(DownloadListActivity.this, Constants.VIDEODOWN_FLAG, videoDownJson);

                }
            }
        });
        //
        exitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = DownloadService.downLoadVideo(downBeanlist.get(position));
                downBeanlist.get(position).setDownTag(i);
                String videojsonStr = JsonUtil.objectToJson(downBeanlist);
                SharedPrefsUtil.putValue(DownloadListActivity.this, Constants.VIDEODOWN_FLAG, videojsonStr);
                deleteLoadingDialog.dismiss();
            }
        });

        deleteLoadingDialog.show();

        WindowManager.LayoutParams params = deleteLoadingDialog.getWindow().getAttributes();
        params.width = screenWidth * 3 / 4;
        // params.height = 200 ;
        deleteLoadingDialog.getWindow().setAttributes(params);
    }
    /**
     * 是否删除
     */
    public void deleteDia(final int position){
        View diaView = View.inflate(DownloadListActivity.this, R.layout.notice_dialog, null);
        deleteDialog = new Dialog(DownloadListActivity.this, R.style.dialog);
        deleteDialog.setContentView(diaView);
        deleteDialog.setCanceledOnTouchOutside(false);
        deleteDialog.setCancelable(false);

        Button yesbtn = (Button) deleteDialog.findViewById(R.id.notice_yesbtn);
        Button exitbtn = (Button) deleteDialog.findViewById(R.id.notice_canclebtn);
        TextView titletv = (TextView) deleteDialog.findViewById(R.id.notice_tv1);
        TextView contenttv = (TextView) deleteDialog.findViewById(R.id.notice_tv2);

        yesbtn.setText(getString(R.string.sure_btn));
        exitbtn.setText(getString(R.string.cancle_btn));
        titletv.setText(getString(R.string.Word_noticetitle));
        contenttv.setText(getString(R.string.Word_deletedownload));


        yesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = DownloadService.getVideoDownPath(downBeanlist.get(position).getUploadUrl());
                File file = new File(path);
                if(file != null && file.exists()){
                    file.delete();
                }

                downBeanlist.remove(position);
                deleteDialog.dismiss();
                ToastUtil.showToast(getApplicationContext(), R.string.Word_worksuccess, ToastUtil.BOTTOM);

                adapter.notifyDataSetChanged();

                if(downBeanlist.size() == 0){
                    SharedPrefsUtil.putValue(DownloadListActivity.this, Constants.VIDEODOWN_FLAG, "");

                    setEmptyViewVisible(true);
                }else {
                    List<VideoDownBean> list1 = new ArrayList<VideoDownBean>();

                    for(int i=0;i<downBeanlist.size();i++){
                        String videoId = downBeanlist.get(i).getId();
                        String uploadImg = downBeanlist.get(i).getImgurl();
                        String name = downBeanlist.get(i).getName();
                        String uploadUrl = downBeanlist.get(i).getUploadUrl();
                        String uploadSize = downBeanlist.get(i).getTotalsize();

                        VideoDownBean bean = new VideoDownBean(videoId, uploadImg, name, uploadUrl, 0, 0, VideoDownBean.UNDOWNLOAD, uploadSize);
                        list1.add(bean);
                    }

                    videoDownJson = JsonUtil.objectToJson(list1);
                    SharedPrefsUtil.putValue(DownloadListActivity.this, Constants.VIDEODOWN_FLAG, videoDownJson);
                }

            }
        });
        //
        exitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
            }
        });

        deleteDialog.show();

        WindowManager.LayoutParams params = deleteDialog.getWindow().getAttributes();
        params.width = screenWidth * 3 / 4;
        // params.height = 200 ;
        deleteDialog.getWindow().setAttributes(params);
    }


    /**
     * 下载回调
     */
    public static class MyFileDownLoaderCallBack implements FileDownloaderManager.FileDownLoaderCallBack{
        String filmId;

        public MyFileDownLoaderCallBack(String filmId) {
            this.filmId = filmId;
            numberFormat.setMaximumFractionDigits(2);
        }

        @Override
        public void downLoadComplated(BaseDownloadTask task) {
            Log.e("info","完成");
            publishProgress(filmId, 0, VideoDownBean.DownloadComplated, 0 ,0);

            try{
                int position=0;
                for(int i=0;i<downBeanlist.size();i++){
                    if(filmId.equals(downBeanlist.get(i).getId())){
                        position = i;
                        break;
                    }
                }

                String url = downBeanlist.get(position).getUploadUrl();
                String path = DownloadService.getVideoDownPath(url);
                mountFile(context, path);//通知sd卡挂载更新数据库
            }catch (Exception e){
                Log.e("info","允许异常");
            }
        }

        @Override
        public void downLoadError(BaseDownloadTask task, Throwable e) {
//            Log.e("info","错误");
            publishProgress(filmId, 0, VideoDownBean.DownloadError, 0, 0);
        }

        @Override
        public void downLoadProgress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            if(startReco() == true){
//                Log.e("info","下载中");
//                String progressstr = (numberFormat.format((float) soFarBytes/ (float) totalBytes * 100))+"%";
//                Log.e("info","filmId="+filmId + ":下载中 progress="+progressstr);
                String str = numberFormat.format((float) soFarBytes/ (float) totalBytes * 100);
                //进度是数字才显示
                if(isNumber(str) == true){
                    double progress =(Double.valueOf(str));
//                    Log.e("info","progress="+progress);
                    int speed = task.getSpeed();
                    publishProgress(filmId, (int)progress, VideoDownBean.Downloading, speed, totalBytes);
                }

            }
        }

        @Override
        public void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
//            Log.e("info","暂停");
            String str = numberFormat.format((float) soFarBytes/ (float) totalBytes * 100);
            if(isNumber(str) == true){

                int progress = (int)((float) soFarBytes * 100 / (float)totalBytes);
                publishProgress(filmId, progress, VideoDownBean.DownloadPaused, 0, totalBytes);
            }

        }

        @Override
        public void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
//            Log.e("info","等待下载");
            publishProgress(filmId, 0, VideoDownBean.DownloadPending, 0, totalBytes);

            try{
                int position=0;
                for(int i=0;i<downBeanlist.size();i++){
                    if(filmId.equals(downBeanlist.get(i).getId())){
                        position = i;
                        break;
                    }
                }

                downBeanlist.get(position).setTotalsize(getPrintSize(totalBytes));
                adapter.notifyDataSetChanged();

            }catch (Exception e){
                Log.e("info","允许异常");
            }

        }
    }

    /**
     * 判断一串字符串是否都为数字，可以是小数，有小数点
     */
    public static boolean isNumber(String str){
        boolean b;

        try {
            double d = Double.valueOf(str);
            b = true;
        } catch (NumberFormatException e) {
            b = false;
        }

        return b;
    }

    /**
     * 更新控件
     */
    private static void publishProgress(final String filmId, final int progress, final int flag, final int speed, final int totalsize){
        try{
            int position=0;
            for(int i=0;i<downBeanlist.size();i++){
                if(filmId.equals(downBeanlist.get(i).getId())){
                    position = i;
                    break;
                }
            }

            downBeanlist.get(position).setProgress(progress);
            downBeanlist.get(position).setSpeed(speed);
            downBeanlist.get(position).setFlag(flag);
            if(totalsize != 0){
                downBeanlist.get(position).setTotalsize(getPrintSize(totalsize));
            }


            //当该项在屏幕可见范围
            if(position >= lv.getFirstVisiblePosition() && position <= lv.getLastVisiblePosition()) {
                int positionInListView = position - lv.getFirstVisiblePosition();
                ProgressBar pb = (ProgressBar) lv.getChildAt(positionInListView).findViewById(R.id.videolocalitem_progress1);
                TextView pausebtn = (TextView) lv.getChildAt(positionInListView).findViewById(R.id.videolocalitem_cancleuploadbtn);
                TextView flagtv = (TextView) lv.getChildAt(positionInListView).findViewById(R.id.videolocalitem_flagtv);
                TextView downloadbtn = (TextView) lv.getChildAt(positionInListView).findViewById(R.id.videolocalitem_uploadbtn);
                TextView speedtv = (TextView) lv.getChildAt(positionInListView).findViewById(R.id.videolocalitem_timeAndsizetv);
                TextView totalsizetv = (TextView) lv.getChildAt(positionInListView).findViewById(R.id.videolocalitem_totalsizetv);

                //没下载
                if(flag == VideoDownBean.UNDOWNLOAD){
                    pb.setVisibility(View.GONE);
                    speedtv.setVisibility(View.GONE);
                    pausebtn.setVisibility(View.GONE);
                    flagtv.setVisibility(View.GONE);

                    downloadbtn.setVisibility(View.VISIBLE);
                    totalsizetv.setVisibility(View.VISIBLE);
                    totalsizetv.setText(getPrintSize(totalsize));
                    //正在下载
                }else if(flag == VideoDownBean.Downloading){
                    pb.setVisibility(View.VISIBLE);
                    speedtv.setVisibility(View.VISIBLE);
                    pausebtn.setVisibility(View.VISIBLE);
                    flagtv.setVisibility(View.VISIBLE);

                    downloadbtn.setVisibility(View.GONE);

                    speedtv.setText(speed + "KB/S");
                    pb.setProgress(progress);
                    flagtv.setText(R.string.Word_downloading);

                    totalsizetv.setVisibility(View.VISIBLE);
                    totalsizetv.setText(getPrintSize(totalsize));
                    //下载完成
                }else if(flag == VideoDownBean.DownloadComplated){
                    pb.setVisibility(View.GONE);
                    speedtv.setVisibility(View.GONE);
                    pausebtn.setVisibility(View.GONE);
                    flagtv.setVisibility(View.VISIBLE);

                    downloadbtn.setVisibility(View.GONE);

                    flagtv.setText(R.string.Word_finishdownload);
                    totalsizetv.setVisibility(View.VISIBLE);
                    //暂停
                }else if(flag == VideoDownBean.DownloadPaused){
                    pb.setVisibility(View.VISIBLE);
                    speedtv.setVisibility(View.GONE);
                    pausebtn.setVisibility(View.GONE);
                    flagtv.setVisibility(View.VISIBLE);

                    downloadbtn.setVisibility(View.VISIBLE);

                    pb.setProgress(progress);
                    flagtv.setText(R.string.Word_pause);
                    totalsizetv.setVisibility(View.VISIBLE);
                    totalsizetv.setText(getPrintSize(totalsize));
                    //等待
                }else if(flag == VideoDownBean.DownloadPending){
                    pb.setVisibility(View.GONE);
                    speedtv.setVisibility(View.GONE);
                    pausebtn.setVisibility(View.VISIBLE);
                    flagtv.setVisibility(View.VISIBLE);

                    downloadbtn.setVisibility(View.GONE);
                    flagtv.setText(R.string.Word_pending);
                    totalsizetv.setVisibility(View.VISIBLE);
                    totalsizetv.setText(getPrintSize(totalsize));
                    //错误
                }else if(flag == VideoDownBean.DownloadError){
                    pb.setVisibility(View.GONE);
                    speedtv.setVisibility(View.GONE);
                    pausebtn.setVisibility(View.GONE);
                    flagtv.setVisibility(View.VISIBLE);

                    downloadbtn.setVisibility(View.VISIBLE);
                    flagtv.setText(R.string.Word_downloaderror);
                    totalsizetv.setVisibility(View.VISIBLE);

                }
            }
        }catch (Exception e){
            Log.e("info","允许异常");
        }
    }


    /**
     * adapter
     */
    class MyDownAdapter extends BaseAdapter{
        int imgWidth,imgHeight;

        @Override
        public int getCount() {
            return downBeanlist.size();
        }

        @Override
        public Object getItem(int i) {
            return downBeanlist.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(DownloadListActivity.this).inflate(R.layout.videolocal_item, null);
                viewHolder.img = (ImageView) convertView.findViewById(R.id.videolocalitem_imgiv);
                viewHolder.playiv = (ImageView) convertView.findViewById(R.id.videolocalitem_playiv);
                viewHolder.nametv = (TextView) convertView.findViewById(R.id.videolocalitem_nametv);
                viewHolder.speedtv = (TextView) convertView.findViewById(R.id.videolocalitem_timeAndsizetv);
                viewHolder.totalsizetv = (TextView) convertView.findViewById(R.id.videolocalitem_totalsizetv);
                viewHolder.flagtv = (TextView) convertView.findViewById(R.id.videolocalitem_flagtv);
                viewHolder.pb = (ProgressBar) convertView.findViewById(R.id.videolocalitem_progress1);
                viewHolder.downloadbtn = (TextView) convertView.findViewById(R.id.videolocalitem_uploadbtn);
                viewHolder.pausebtn = (TextView) convertView.findViewById(R.id.videolocalitem_cancleuploadbtn);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.downloadbtn.setText(R.string.Word_Download);
            viewHolder.pausebtn.setText(R.string.Word_pausedown);
            viewHolder.totalsizetv.setVisibility(View.VISIBLE);
            viewHolder.pb.setVisibility(View.GONE);
            viewHolder.downloadbtn.setVisibility(View.GONE);
            viewHolder.pausebtn.setVisibility(View.GONE);


            if (imgWidth == Constants.ZERO || imgHeight == Constants.ZERO) {
                imgWidth = screenWidth * 3 / 10;
                imgHeight = imgWidth * 3 / 4;
            }

            final VideoDownBean videoDownBean = downBeanlist.get(position);

            ViewGroup.LayoutParams params = viewHolder.img.getLayoutParams();
            params.width = imgWidth;
            params.height = imgHeight;
            viewHolder.img.setLayoutParams(params);

            viewHolder.nametv.setText(videoDownBean.getName());
            viewHolder.totalsizetv.setText(videoDownBean.getTotalsize());
            //指定大小压缩,节省资源
            GlideLogic.glideLoadPic423(DownloadListActivity.this, videoDownBean.getImgurl(), viewHolder.img, imgWidth, imgHeight);

            //判断是否已下载
            String path = DownloadService.getVideoDownPath(videoDownBean.getUploadUrl());
            if (!(TextUtils.isEmpty(path))){
                File file = new File(path);
                if (file.exists()) {
                    videoDownBean.setFlag(VideoDownBean.DownloadComplated);
                    viewHolder.totalsizetv.setText(getPrintSize(file.length()));
                }
            }


            //没下载
            if(videoDownBean.getFlag() == VideoDownBean.UNDOWNLOAD){
                viewHolder.pb.setVisibility(View.GONE);
                viewHolder.speedtv.setVisibility(View.GONE);
                viewHolder.pausebtn.setVisibility(View.GONE);
                viewHolder.flagtv.setVisibility(View.GONE);

                viewHolder.downloadbtn.setVisibility(View.VISIBLE);
            //正在下载
            }else if(videoDownBean.getFlag() == VideoDownBean.Downloading){
                viewHolder.pb.setVisibility(View.VISIBLE);
                viewHolder.speedtv.setVisibility(View.VISIBLE);
                viewHolder.pausebtn.setVisibility(View.VISIBLE);
                viewHolder.flagtv.setVisibility(View.VISIBLE);

                viewHolder.downloadbtn.setVisibility(View.GONE);

                viewHolder.speedtv.setText(videoDownBean.getSpeed() + "KB/S");
                viewHolder.pb.setProgress(videoDownBean.getProgress());
                viewHolder.flagtv.setText(R.string.Word_downloading);
            //下载完成
            }else if(videoDownBean.getFlag() == VideoDownBean.DownloadComplated){
                viewHolder.pb.setVisibility(View.GONE);
                viewHolder.speedtv.setVisibility(View.GONE);
                viewHolder.pausebtn.setVisibility(View.GONE);
                viewHolder.flagtv.setVisibility(View.VISIBLE);

                viewHolder.downloadbtn.setVisibility(View.GONE);

                viewHolder.flagtv.setText(R.string.Word_finishdownload);
            //暂停
            }else if(videoDownBean.getFlag() == VideoDownBean.DownloadPaused){
                viewHolder.pb.setVisibility(View.VISIBLE);
                viewHolder.speedtv.setVisibility(View.GONE);
                viewHolder.pausebtn.setVisibility(View.GONE);
                viewHolder.flagtv.setVisibility(View.VISIBLE);

                viewHolder.downloadbtn.setVisibility(View.VISIBLE);

                viewHolder.pb.setProgress(videoDownBean.getProgress());
                viewHolder.flagtv.setText(R.string.Word_pause);
            //等待
            }else if(videoDownBean.getFlag() == VideoDownBean.DownloadPending){
                viewHolder.pb.setVisibility(View.GONE);
                viewHolder.speedtv.setVisibility(View.GONE);
                viewHolder.pausebtn.setVisibility(View.VISIBLE);
                viewHolder.flagtv.setVisibility(View.VISIBLE);

                viewHolder.downloadbtn.setVisibility(View.GONE);
                viewHolder.flagtv.setText(R.string.Word_pending);
            //错误
            }else if(videoDownBean.getFlag() == VideoDownBean.DownloadError){
                viewHolder.pb.setVisibility(View.GONE);
                viewHolder.speedtv.setVisibility(View.GONE);
                viewHolder.pausebtn.setVisibility(View.GONE);
                viewHolder.flagtv.setVisibility(View.VISIBLE);

                viewHolder.downloadbtn.setVisibility(View.VISIBLE);
                viewHolder.flagtv.setText(R.string.Word_downloaderror);

            }

            //
            viewHolder.downloadbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int netState = getNetState();

                    if(netState == Constants.WIFI_FLAG){
                        int i = DownloadService.downLoadVideo(downBeanlist.get(position));
                        downBeanlist.get(position).setDownTag(i);
                        String videojsonStr = JsonUtil.objectToJson(downBeanlist);
                        SharedPrefsUtil.putValue(DownloadListActivity.this, Constants.VIDEODOWN_FLAG, videojsonStr);

                    }else if(netState == Constants.GPR_FLAG){
                        ToastUtil.showToast(DownloadListActivity.this, R.string.netstate_gpr, ToastUtil.CENTER);

                    }else {
                        ToastUtil.showToast(DownloadListActivity.this, R.string.internet_error, ToastUtil.CENTER);
                    }

                }
            });

            //
            viewHolder.pausebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FileDownloaderManager.getInstance().pauseDownLoadFile(downBeanlist.get(position).getDownTag());
                }
            });
            //
            viewHolder.playiv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(downBeanlist.get(position).getFlag() == VideoDownBean.DownloadComplated){
                        //进入本地播放
//                        String path = DownloadService.getVideoDownPath(videoDownBean.getUploadUrl());
//                        Intent intent = new Intent(DownloadListActivity.this, MoviePlayActivity.class);
//                        intent.putExtra("playType", "local");
//                        intent.putExtra("urlOrPath", path);
//                        intent.putExtra("playName", getNamestr(path));
//                        startActivity(intent);
                        Intent intent = new Intent();
                        intent.setClass(DownloadListActivity.this, OnlineVideoInfoActivity.class);
                        intent.putExtra("videoId",downBeanlist.get(position).getId());
                        startActivity(intent);

                    }else {
                        ToastUtil.showToast(DownloadListActivity.this, R.string.Word_notloaded, ToastUtil.CENTER);
                    }
                }
            });

            return convertView;
        }

        class ViewHolder {
            ImageView img;
            ImageView playiv;
            TextView nametv;
            TextView speedtv;
            TextView totalsizetv;
            TextView flagtv;
            ProgressBar pb;
            TextView downloadbtn;
            TextView pausebtn;
        }
    }

    private String getNamestr(String path){
        String s = path.substring(path.lastIndexOf("/")+1,path.lastIndexOf("."));
        return s;
    }

    @Override
    protected void onResume() {
        super.onResume();

        usedspacetv.setText(getString(R.string.word_usedspace) + getPrintSize(getSDTotalSize() - getSDAvailableSize()));
        spaceavailabletv.setText(getString(R.string.word_spaceavailable) + getPrintSize(getSDAvailableSize()));

    }

    /**
     * 获得SD卡总大小
     *
     * @return
     */
    private long getSDTotalSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
//        return Formatter.formatFileSize(DownloadListActivity.this, blockSize * totalBlocks);
        Log.e("info","String.valueOf(blockSize * totalBlocks="+String.valueOf(blockSize * totalBlocks));
        return blockSize * totalBlocks;
    }
    /**
     * 获得sd卡剩余容量，即可用大小
     *
     * @return
     */
    private long getSDAvailableSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
//        return Formatter.formatFileSize(DownloadListActivity.this, blockSize * availableBlocks);
        Log.e("info","availableBlocks * blockSize="+String.valueOf(availableBlocks * blockSize));
        return availableBlocks * blockSize;
    }

}
