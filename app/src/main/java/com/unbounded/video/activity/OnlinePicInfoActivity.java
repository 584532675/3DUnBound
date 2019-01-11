package com.unbounded.video.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.stereo.FaceDetector.FaceDetector;
import com.stereo.GlSurfaceView.PictureGLSurfaceView;
import com.stereo.Holography.Holography;
import com.stereo.util.SettingUtils;
import com.stereo.util.Utils;
import com.unbounded.video.BaseActivity;
import com.unbounded.video.adapter.PoplvAdapter;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.constants.HttpConstants;
import com.unbounded.video.service.DownloadService;
import com.unbounded.video.utils.FileDownloaderManager;
import com.unbounded.video.utils.HttpGetUtil;
import com.unbounded.video.utils.HttpPostUtil;
import com.unbounded.video.utils.SharedPrefsUtil;
import com.unbounded.video.utils.ToastUtil;
import com.unbounded.video.utils.facedetector.CameraView;
import com.unbounded.video.utils.imageLoad.GetBitmap;
import com.zhuge.analysis.stat.ZhugeSDK;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import CameraLogic.CameraManager;


/**
 * Created by zjf on 2017/8/1.
 */

public class OnlinePicInfoActivity extends BaseActivity{
    String picRoot = Environment.getExternalStorageDirectory().getAbsolutePath()+"/moyan/images/";
    public final static int PICINFO_SUCCESS_FLAG = 1;
    public final static int COLLECT_FLAG = 2;
    public final static int TITLE_VIEW = 3;
    public final static int BITMAP_FLAG = 4;
    String picId,phoneNum;
    RelativeLayout titlerela;
    LinearLayout btnslinear;
    ImageView imgiv;
    Button backbtn,downloadbtn,collectbtn,sharebtn;
    int picInfoUpdate = 0;

    String userimg,opencount,type3d,name,report,uploadimg,likecount,introduction,username,uploadtime;
    int type2dor3d = -1;
    boolean follow,collector;
    List<String> keyList = new ArrayList<String>();
    List<String> valueList = new ArrayList<String>();

    private PictureGLSurfaceView mPictureView;

    private ImageView ivpic_eyes_tacking;

    private Bitmap btmap;
    private int modeFormat;

    TextView picnametv,formattv,modeltv;

    Dialog gprDialog;
    PopupWindow formatpop,modelpop;
    View formatview,modelview;
    List<String> formatpoplist = new ArrayList<String>();
    List<String> modelpoplist = new ArrayList<String>();

    CameraView cameraView;

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //图片详情
                case PICINFO_SUCCESS_FLAG:
                    String infovalue = msg.obj.toString();
                    Log.e("info","infovalue=" + infovalue);

                    if(!(TextUtils.isEmpty(infovalue))) {
                        try {
                            JSONObject infojo = new JSONObject(infovalue);
                            follow = infojo.getBoolean("follow");
                            collector = infojo.getBoolean("collector");

                            JSONArray data = infojo.getJSONArray("data");
                            if (data != null && data.length() > 0) {
                                JSONObject jo = data.getJSONObject(0);

                                userimg = jo.getString("userimg");
                                opencount = jo.getString("opencount");
                                type3d = jo.getString("type3d");
                                name = jo.getString("name");
                                report = jo.getString("report");
                                uploadimg = jo.getString("uploadimg");
                                likecount = jo.getString("likecount");
                                introduction = jo.getString("introduction");
                                uploadtime = jo.getString("uploadtime");

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //
                        //格式
                        if(TextUtils.isEmpty(type3d)){
                            type2dor3d = 0;
                        }else {
                            if("0".equals(type3d)){
                                type2dor3d = 0;
                            }else if("1".equals(type3d)){
                                type2dor3d = 1;
                            }else if("2".equals(type3d)){
                                type2dor3d = 2;
                            }

                        }

                        //
                        if(picnametv != null){
                            picnametv.setText(name);
                        }
                        //收藏
                        if (collector == false) {
                            collectbtn.setBackgroundResource(com.unbounded.video.R.mipmap.whitecollectbtn1);
                            collectbtn.setClickable(true);

                        } else {
                            collectbtn.setBackgroundResource(com.unbounded.video.R.mipmap.collectbtn2);
                            collectbtn.setClickable(false);
                        }
                        //图片
                        if(!(TextUtils.isEmpty(uploadimg))){
//                            Glide.with(getApplicationContext()).load(uploadimg).asBitmap().into(new SimpleTarget<Bitmap>() {
//                                @Override
//                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                                    btmap = resource;
//                                    if(btmap != null){
//                                        //检查图片格式函数  在展示图片之前需要调用此方法进行图片格式检测并保存对应的格式  modeFormat  -1:出错; 0:2D图片 1:左右3D图片 2:上下3D图片
//                                        modeFormat= Utils.naviveCheckPicture(btmap,mPictureView);
//                                        mPictureView.setBitmap(btmap,modeFormat);
//
//
//                                        //只有当激活后 才能调用mPictureView.setShowModel(1)切换成3d模式  否则会报错 其他界面也是类似
//                                        isPlay3D = SharedPrefsUtil.getValue(OnlinePicInfoActivity.this, com.stereo.util.Constants.PALY_3D, false);
//
//                                        if (!isPlay3D){
//                                            //无激活状态下初始化播放状态图标为2D并隐藏人眼追踪图标和关闭人眼追踪功能
//                                            if (ivpic_eyes_tacking.getVisibility() == View.VISIBLE) {
//                                                ivpic_eyes_tacking.setVisibility(View.GONE);//变成不追踪
//                                                //切换成3d  0 2d 1 3d
//                                                mPictureView.setShowModel(0);
//                                                //切换视频格式    0 2d图片  1 左右图片 2 上下图片
//                                                mPictureView.switchPictureFormat(2);
//                                            }
//                                        }
//                                    }
//                                }
//                            });


                            GetBitmap getBitmap = new GetBitmap(handler, uploadimg, BITMAP_FLAG, screenWidth, screenHeight);
                            Thread thread = new Thread(getBitmap);
                            thread.start();
                        }
                    }
                    break;
                //图片加载
                case BITMAP_FLAG:
                    btmap = (Bitmap) msg.obj;
                    if(btmap != null){
                        //检查图片格式函数  在展示图片之前需要调用此方法进行图片格式检测并保存对应的格式  modeFormat  -1:出错; 0:2D图片 1:左右3D图片 2:上下3D图片
                        modeFormat= Utils.naviveCheckPicture(btmap,mPictureView);
                        mPictureView.setBitmap(btmap,modeFormat);

                        if(hasFrontCamera()) {
                            initcameraView();
                        }


                        isPlay3D = SharedPrefsUtil.getValue(OnlinePicInfoActivity.this, com.stereo.util.Constants.PALY_3D, false);

                        if(isPlay3D == true){
                            modeltv.setText("3D");
                            mPictureView.setShowModel(1);
                            ivpic_eyes_tacking.setVisibility(View.VISIBLE);
                        }else{
                            modeltv.setText("2D");
                            mPictureView.setShowModel(0);
                            ivpic_eyes_tacking.setVisibility(View.GONE);
                            if(cameraView != null){
                                cameraView.destroy();
                            }
                        }

                        //切换视频格式    0 2d图片  1 左右图片 2 上下图片
                        if(type2dor3d != -1){
                            mPictureView.switchPictureFormat(type2dor3d);
                            formattv.setText(formatpoplist.get(type2dor3d));
                        }
                    }

                    progressDiddismiss();
                    titleView(true);

                    break;
                //收藏
                case COLLECT_FLAG:
                String collectvalue = msg.obj.toString();
//                    Log.e("info","collectvalue="+collectvalue);

                if(!(TextUtils.isEmpty(collectvalue))){
                    try {
                        JSONObject collectjo = new JSONObject(collectvalue);
                        String result = collectjo.getString("result");
                        if(Constants.SUCCESS_STR.equals(result)){
                            ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_collectsuccess, ToastUtil.CENTER);
                            collectbtn.setBackgroundResource(com.unbounded.video.R.mipmap.collectbtn2);
                            collectbtn.setClickable(false);
                        }else {
                            ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_collecterror, ToastUtil.CENTER);
                            collectbtn.setBackgroundResource(com.unbounded.video.R.mipmap.infocollectbtn1);
                            collectbtn.setClickable(true);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    progressDiddismiss();
                }

                break;
                //
                case Constants.INTERNET_ERROR_FLAG:
                    ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.internet_error, ToastUtil.CENTER);
                    progressDiddismiss();

                    break;

                //
                case TITLE_VIEW:
                    titleView(false);

                    break;
            }
        }
    };
    private boolean isPlay3D;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isFullScreen = true;

        /**
         * 图片详情次数
         */
        phoneNum = SharedPrefsUtil.getValue(getApplicationContext(), Constants.Phone_FLAG, Constants.Phone_default);
        JSONObject personObject = null;
        try {
            personObject = new JSONObject();
            personObject.put("phoneNum", phoneNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ZhugeSDK.getInstance().track(getApplicationContext(),"图片详情",personObject);

        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getContentView() {
        return com.unbounded.video.R.layout.activity_onlinepicinfo;
    }

    @Override
    public void init(Intent intent) {
        super.init(intent);

        picId = intent.getStringExtra("picId");

        setTitleViewVisible(false);
        //获取显示图片的PictureGLSurfaceView
        mPictureView = (PictureGLSurfaceView) findViewById(com.unbounded.video.R.id.movie_view);
        isPlay3D = SharedPrefsUtil.getValue(OnlinePicInfoActivity.this, com.stereo.util.Constants.PALY_3D, false);

        //注册人眼追踪刷新图标广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.android.action.update");
//        registerReceiver(updateReceiver, filter);
    }

    //刷新人眼追踪刷新图标
    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.android.action.update")) {
                if (SettingUtils.isShowTrackingStatus(OnlinePicInfoActivity.this)) {
                    ivpic_eyes_tacking.setImageResource(com.unbounded.video.R.mipmap.eyes_tracking);

                } else {
                    ivpic_eyes_tacking.setImageResource(com.unbounded.video.R.mipmap.eyes_not_tracking);
                }
            }

        }
    };
    /**
     * 判断是否有前置摄像
     * @return
     */
    public boolean hasFrontCamera(){
        Camera.CameraInfo info = new Camera.CameraInfo();
        int count = Camera.getNumberOfCameras();
        for(int i = 0; i < count; i++){
            Camera.getCameraInfo(i, info);
            if(info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
                return true;
            }
        }
        return false;
    }

    /**
     *
     */
    private void initcameraView(){
        cameraView = (CameraView) findViewById(com.unbounded.video.R.id.onlinepicinfo_cameraview);
        cameraView.setOnFaceDetectedListener(new CameraView.OnFaceDetectedListener() {
            @Override
            public void onFaceDetected(Camera.Face[] faces, Camera camera) {
                //检测到人脸后的回调方法
                if (null == faces || faces.length == 0) {
                    Log.e("info1", "There is no face.");
                    ivpic_eyes_tacking.setImageResource(com.unbounded.video.R.mipmap.eyes_not_tracking);

                } else {
                    Log.e("info1", "onFaceDetection : At least one face has been found.");
                    ivpic_eyes_tacking.setImageResource(com.unbounded.video.R.mipmap.eyes_tracking);

                }
            }
        });
    }

    @Override
    public void initDatas() {
        super.initDatas();

        userId = SharedPrefsUtil.getValue(getApplicationContext(), Constants.UserId_FLAG, "");
        picInfoUpdate = SharedPrefsUtil.getValue(getApplicationContext(), Constants.PicInfoUpdate, 0);

        if(picInfoUpdate == 0){
            progressDid();
            initPicInfo();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        userId = SharedPrefsUtil.getValue(getApplicationContext(), Constants.UserId_FLAG, "");
        picInfoUpdate = SharedPrefsUtil.getValue(getApplicationContext(), Constants.PicInfoUpdate, 0);

        if(picInfoUpdate == 1){
            progressDid();
            initPicInfo();
            SharedPrefsUtil.putValue(getApplicationContext(), Constants.PicInfoUpdate, 0);
        }

        if(cameraView != null){
            cameraView.reset();
        }
    }

    //
    void initPicInfo(){
        String url = HttpConstants.selectByIdImg_Url + "?id=" + picId + "&userId=" + userId;
        HttpGetUtil get = new HttpGetUtil(handler, url, PICINFO_SUCCESS_FLAG);
        Thread thread = new Thread(get);
        thread.start();
    }

    @Override
    public void initView() {
        super.initView();

        formatpoplist.clear();
        formatpoplist.add("2D图片");
        formatpoplist.add("左右图片");
        formatpoplist.add("上下图片");
        formatPop();

        titlerela = (RelativeLayout) findViewById(com.unbounded.video.R.id.onlinepicinfo_titlerela);
        btnslinear = (LinearLayout) findViewById(com.unbounded.video.R.id.onlinepicinfo_btnslinear);
        imgiv = (ImageView) findViewById(com.unbounded.video.R.id.onlinepicinfo_img);
        backbtn = (Button) findViewById(com.unbounded.video.R.id.onlinepicinfo_backbtn);
        picnametv = (TextView) findViewById(com.unbounded.video.R.id.onlinepicinfo_nametv);
        formattv = (TextView) findViewById(com.unbounded.video.R.id.onlinepicinfo_formattv);
        modeltv = (TextView) findViewById(com.unbounded.video.R.id.onlinepicinfo_modeltv);
        downloadbtn = (Button) findViewById(com.unbounded.video.R.id.onlinepicinfo_downloadbtn);
        collectbtn = (Button) findViewById(com.unbounded.video.R.id.onlinepicinfo_collectbtn);
        sharebtn = (Button) findViewById(com.unbounded.video.R.id.onlinepicinfo_sharebtn);
        ivpic_eyes_tacking = (ImageView) findViewById(com.unbounded.video.R.id.ivpic_eyes_tacking);

    }
    /**
     * 模式pop
     */
    private void modelPop(){
        modelview = this.getLayoutInflater().inflate(com.unbounded.video.R.layout.switchbtnspop, null);
        modelpop = new PopupWindow(modelview,43*oneDp, ViewGroup.LayoutParams.WRAP_CONTENT);
        modelpop.setFocusable(true);
        modelpop.setOutsideTouchable(true);
        modelpop.setBackgroundDrawable(new BitmapDrawable());

        modelpoplist.clear();
        if(isPlay3D == true){
            modelpoplist.add("2D");
            modelpoplist.add("3D");
        }else {
            modelpoplist.add("2D");
        }
        ListView modellv = (ListView) modelview.findViewById(com.unbounded.video.R.id.switchbtns_lv);

        ViewGroup.LayoutParams lvparams = modellv.getLayoutParams();
        lvparams.height = 40*modelpoplist.size()*oneDp + modelpoplist.size()-1;
        modellv.setLayoutParams(lvparams);

        PoplvAdapter poplvAdapter = new PoplvAdapter(getApplicationContext(), modelpoplist);
        modellv.setAdapter(poplvAdapter);

        modellv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                modeltv.setText(modelpoplist.get(position));
                mPictureView.setShowModel(position);
                modelpop.dismiss();

                if("2D".equals(modelpoplist.get(position))){
                    ivpic_eyes_tacking.setVisibility(View.GONE);
                    if(cameraView != null){
                        cameraView.destroy();
                    }
                }else if("3D".equals(modelpoplist.get(position))){
                    ivpic_eyes_tacking.setVisibility(View.VISIBLE);
                    if(cameraView != null){
                        cameraView.reset();
                    }
                }
            }
        });
    }
    /**
     * 格式
     */
    private void formatPop(){
        formatview = this.getLayoutInflater().inflate(com.unbounded.video.R.layout.switchbtnspop, null);
        formatpop = new PopupWindow(formatview,73*oneDp, ViewGroup.LayoutParams.WRAP_CONTENT);
        formatpop.setFocusable(true);
        formatpop.setOutsideTouchable(true);
        formatpop.setBackgroundDrawable(new BitmapDrawable());

        ListView formatlv = (ListView) formatview.findViewById(com.unbounded.video.R.id.switchbtns_lv);

        ViewGroup.LayoutParams lvparams = formatlv.getLayoutParams();
        lvparams.height = 40*formatpoplist.size()*oneDp + 2;
        formatlv.setLayoutParams(lvparams);

        PoplvAdapter poplvAdapter = new PoplvAdapter(getApplicationContext(),formatpoplist);
        formatlv.setAdapter(poplvAdapter);

        formatlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                formattv.setText(formatpoplist.get(position));
                mPictureView.switchPictureFormat(position);
                formatpop.dismiss();
            }
        });

    }
    @Override
    public void initEvent() {
        super.initEvent();

        backbtn.setOnClickListener(this);
        downloadbtn.setOnClickListener(this);
        collectbtn.setOnClickListener(this);
        sharebtn.setOnClickListener(this);
        imgiv.setOnClickListener(this);
        formattv.setOnClickListener(this);
        modeltv.setOnClickListener(this);
        mPictureView.setOnClickListener(this);

    }

    /**
     * 控制栏显示隐藏
     */
    private void titleView(boolean isvisible){
        if(isvisible == true){
            titlerela.setVisibility(View.VISIBLE);
            btnslinear.setVisibility(View.VISIBLE);

            handler.removeMessages(TITLE_VIEW);
            handler.sendEmptyMessageDelayed(TITLE_VIEW, 8*oneMinute);

        }else {
            titlerela.setVisibility(View.GONE);
            btnslinear.setVisibility(View.GONE);

            if(formatpop != null){
                formatpop.dismiss();
            }
            if(modelpop != null){
                modelpop.dismiss();
            }
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case com.unbounded.video.R.id.onlinepicinfo_backbtn:
                finish();
                break;
            case com.unbounded.video.R.id.movie_view:
                if(btnslinear.getVisibility() == View.GONE){
                    titleView(true);
                }else {
                    titleView(false);
                }

                break;
            //格式
            case com.unbounded.video.R.id.onlinepicinfo_formattv:
                formatpop.showAsDropDown(formattv);

                break;
            //模式
            case com.unbounded.video.R.id.onlinepicinfo_modeltv:
                isPlay3D = SharedPrefsUtil.getValue(this, com.stereo.util.Constants.PALY_3D, false);
                modelPop();
                modelpop.showAsDropDown(modeltv);

                break;
            //下载
            case com.unbounded.video.R.id.onlinepicinfo_downloadbtn:
                userId = SharedPrefsUtil.getValue(getApplicationContext(), Constants.UserId_FLAG, "");
                int netType = getNetState();

                if(netType == Constants.WIFI_FLAG){
                    downLoadMethod();
                }else if(netType == Constants.GPR_FLAG){
                    //数据流量弹出对话框
                    gprDia();

                }else {
                    ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.checkbet_word, ToastUtil.CENTER);
                }


                break;
            //收藏
            case com.unbounded.video.R.id.onlinepicinfo_collectbtn:
                userId = SharedPrefsUtil.getValue(getApplicationContext(), Constants.UserId_FLAG, "");

                if(TextUtils.isEmpty(userId)){
                    openActivity(LoginActivity.class);
                }else{
                    progressDid();
                    collectMethod();
                }

                break;
            //分享
            case com.unbounded.video.R.id.onlinepicinfo_sharebtn:


                break;
        }
    }

    /**
     * 数据流量询问对话框
     */
    private void gprDia(){
        View diaView = View.inflate(OnlinePicInfoActivity.this, com.unbounded.video.R.layout.notice_dialog, null);
        gprDialog = new Dialog(OnlinePicInfoActivity.this, com.unbounded.video.R.style.dialog);
        gprDialog.setContentView(diaView);
        gprDialog.setCanceledOnTouchOutside(false);
        gprDialog.setCancelable(false);

        Button yesbtn = (Button) gprDialog.findViewById(com.unbounded.video.R.id.notice_yesbtn);
        Button exitbtn = (Button) gprDialog.findViewById(com.unbounded.video.R.id.notice_canclebtn);
        TextView titletv = (TextView) gprDialog.findViewById(com.unbounded.video.R.id.notice_tv1);
        TextView contenttv = (TextView) gprDialog.findViewById(com.unbounded.video.R.id.notice_tv2);

        yesbtn.setText(getString(com.unbounded.video.R.string.sure_btn));
        exitbtn.setText(getString(com.unbounded.video.R.string.cancle_btn));
        titletv.setText(getString(com.unbounded.video.R.string.Word_noticetitle));
        contenttv.setText(getString(com.unbounded.video.R.string.Word_gprdownload));


        yesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downLoadMethod();
                gprDialog.dismiss();
            }
        });
        //
        exitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gprDialog.dismiss();
            }
        });

        gprDialog.show();

        WindowManager.LayoutParams params = gprDialog.getWindow().getAttributes();
        params.width = screenWidth * 3 / 4;
        // params.height = 200 ;
        gprDialog.getWindow().setAttributes(params);

    }

    /**
     * 图片下载方法
     */
    private void downLoadMethod(){
        String path = DownloadService.getImageDownPath(uploadimg);

        if(TextUtils.isEmpty(userId)){
            openActivity(LoginActivity.class);
        }else{
            if (!(TextUtils.isEmpty(path))){
                File file = new File(path);
                if (file.exists()) {
                    ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_havedownload, ToastUtil.CENTER);
                }else {
                    progressDid1resid(com.unbounded.video.R.string.Word_downloadingwait);
                    FileDownloaderManager.getInstance().startDownLoadFileSingle(uploadimg,
                            getPicDownPath(uploadimg),
                            new MyFileDownLoadCallBack());
                }

            }else{
                progressDid1resid(com.unbounded.video.R.string.Word_downloadingwait);
                FileDownloaderManager.getInstance().startDownLoadFileSingle(uploadimg,
                        getPicDownPath(uploadimg),
                        new MyFileDownLoadCallBack());
            }
        }

    }

    //
    private String getPicDownPath(String url){
        String c = url.substring(url.lastIndexOf("/"));
        return picRoot + c;
    }

    /**
     * 收藏
     */
    private void collectMethod(){
        keyList.clear();
        valueList.clear();

        keyList.add("collectorId");
        keyList.add("userId");
        keyList.add("type");

        valueList.add(picId);
        valueList.add(userId);
        valueList.add("image");

        String discussurl = HttpConstants.Collect_Url;
        HttpPostUtil post = new HttpPostUtil(handler, discussurl,keyList , valueList, COLLECT_FLAG);
        Thread thread = new Thread(post);
        thread.start();

    }

    /**
     *
     */
    private class MyFileDownLoadCallBack implements FileDownloaderManager.FileDownLoaderCallBack{

        @Override
        public void downLoadComplated(BaseDownloadTask task) {
//            Log.e("info","完成");
            progressDid1dismiss();
            ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_finishdownload, ToastUtil.CENTER);
        }

        @Override
        public void downLoadError(BaseDownloadTask task, Throwable e) {
//            Log.e("info","错误");
            progressDid1dismiss();
            ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_downloaderror, ToastUtil.CENTER);
        }

        @Override
        public void downLoadProgress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
//            Log.e("info","下载中");
        }

        @Override
        public void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
//            Log.e("info","暂停");
        }

        @Override
        public void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
//            Log.e("info","准备");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        CameraManager.stop();
        if (cameraView != null) {
            cameraView.destroy();
        }
    }

    @Override
    protected void onDestroy() {
//        unregisterReceiver(updateReceiver);
        new Thread() {
            @Override
            public void run() {
                FaceDetector.nativeRelease();
                Holography.deinitHolography();
            }
        }.start();
        super.onDestroy();
    }
}
