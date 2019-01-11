package com.unbounded.video.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stereo.FaceDetector.FaceDetector;
import com.stereo.GlSurfaceView.PictureGLSurfaceView;
import com.stereo.Holography.Holography;
import com.stereo.util.SettingUtils;
import com.stereo.util.Utils;
import com.unbounded.video.BaseActivity;
import com.unbounded.video.R;
import com.unbounded.video.adapter.PoplvAdapter;
import com.unbounded.video.bean.LocalPic;
import com.unbounded.video.utils.SharedPrefsUtil;
import com.unbounded.video.utils.facedetector.CameraView;
import com.zhuge.analysis.stat.ZhugeSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import CameraLogic.CameraManager;

/**
 * Created by zjf on 2017/8/1.
 */

public class LocalPicInfoActivity extends BaseActivity {
    public final static int TITLE_VIEW = 3;
    RelativeLayout titlerela;
    ImageView imgiv;
    Button backbtn,uploadbtn;
    TextView picnametv,formattv,modeltv;
    LocalPic localPic;
    String phoneNum;

    PopupWindow formatpop,modelpop;
    View formatview,modelview;
    List<String> formatpoplist = new ArrayList<String>();
    List<String> modelpoplist = new ArrayList<String>();

    private PictureGLSurfaceView mPictureView;
    private ImageView ivpic_eyes_tacking;
    private Bitmap btmap;
    private int modeFormat;
    private boolean isPlay3D;

    CameraView cameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isFullScreen = true;
        super.onCreate(savedInstanceState);

        /**
         * 图片详情次数
         */
        phoneNum = SharedPrefsUtil.getValue(getApplicationContext(), com.unbounded.video.constants.Constants.Phone_FLAG, com.unbounded.video.constants.Constants.Phone_default);
        JSONObject personObject = null;
        try {
            personObject = new JSONObject();
            personObject.put("phoneNum", phoneNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ZhugeSDK.getInstance().track(getApplicationContext(),"图片详情",personObject);


        //注册人眼追踪刷新图标广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.android.action.update");
//        registerReceiver(updateReceiver, filter);


    }

    @Override
    protected int getContentView() {
        return R.layout.activity_localpicinfo;
    }

    @Override
    public void init(Intent intent) {
        super.init(intent);

        setTitleViewVisible(false);
        localPic = (LocalPic) intent.getSerializableExtra("localPicBean");
        ivpic_eyes_tacking = (ImageView) findViewById(R.id.ivpic_eyes_tacking);
        //获取显示图片的PictureGLSurfaceView
        mPictureView = (PictureGLSurfaceView) findViewById(R.id.movie_view);


    }

    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.android.action.update")) {
                if (SettingUtils.isShowTrackingStatus(LocalPicInfoActivity.this)) {
                    ivpic_eyes_tacking.setImageResource(R.mipmap.eyes_tracking);
                } else {
                    ivpic_eyes_tacking.setImageResource(R.mipmap.eyes_not_tracking);
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
        cameraView = (CameraView) findViewById(R.id.onlinepicinfo_cameraview);
        cameraView.setOnFaceDetectedListener(new CameraView.OnFaceDetectedListener() {
            @Override
            public void onFaceDetected(Camera.Face[] faces, Camera camera) {
                //检测到人脸后的回调方法
                if (null == faces || faces.length == 0) {
                    Log.e("info1", "There is no face.");
                    ivpic_eyes_tacking.setImageResource(R.mipmap.eyes_not_tracking);

                } else {
                    Log.e("info1", "onFaceDetection : At least one face has been found.");
                    ivpic_eyes_tacking.setImageResource(R.mipmap.eyes_tracking);

                }
            }
        });
    }


    @Override
    public void initView() {
        super.initView();

        formatpoplist.clear();
        formatpoplist.add("2D图片");
        formatpoplist.add("左右图片");
        formatpoplist.add("上下图片");
        formatPop();

        titlerela = (RelativeLayout) findViewById(R.id.localpicinfo_titlerela);
        picnametv = (TextView) findViewById(R.id.localpicinfo_nametv);
        formattv = (TextView) findViewById(R.id.localpicinfo_formattv);
        modeltv = (TextView) findViewById(R.id.localpicinfo_modeltv);
        imgiv = (ImageView) findViewById(R.id.localpicinfo_img);
        backbtn = (Button) findViewById(R.id.localpicinfo_backbtn);
        uploadbtn = (Button) findViewById(R.id.localpicinfo_uploadbtn);


        if(localPic != null){
            int scale = 1;
            picnametv.setText(localPic.getTitle());

            //将uri转成bitmap的  此方法目前会报错，到时候需要调试  现在用测试图片
            //btmap= BitmapUtil.decodeUri(LocalPicInfoActivity.this, Uri.parse(localPic.getPath()),screenHeight * 11 / 16, screenWidth * 11 / 16);
//            btmap = BitmapFactory.decodeResource(getResources(), R.mipmap.p02_tiger);


//            btmap = BitmapFactory.decodeFile(localPic.getPath());

            BitmapFactory.Options opts = new BitmapFactory.Options();// 解析位图的附加条件
            opts.inJustDecodeBounds = true;// 不去解析真实的位图，只是获取这个位图的头文件信息
            BitmapFactory.decodeFile(localPic.getPath(), opts);
            int bitmapWidth = opts.outWidth;
            int bitmapHeight = opts.outHeight;

            //计算缩放比例
            int dx = bitmapWidth / screenHeight;
            int dy = bitmapHeight / screenWidth;

            if (dx > dy && dy >= 1) {
                scale = dx;
            }

            if (dy >= dx && dx >= 1) {
                scale = dy;
            }
            //缩放加载图片到内存。
            opts.inSampleSize = scale;
            opts.inJustDecodeBounds = false;// 真正的去解析这个位图。
            opts.inPurgeable = true;
            opts.inInputShareable = true;

            try {
                btmap = BitmapFactory.decodeFile(localPic.getPath(), opts);
            }catch (Exception e){

            }




            Log.i("test", "initView: btmap "+btmap);
            //检查图片格式函数  在展示图片之前需要调用此方法进行图片格式检测并保存对应的格式  modeFormat  -1:出错; 0:2D图片 1:左右3D图片 2:上下3D图片
//            modeFormat= Utils.naviveCheckPicture(btmap,mPictureView);
            modeFormat= Utils.naviveCheckSdPicture(btmap,mPictureView);
            Log.i("test", "initView: modeFormat "+modeFormat);

            Log.i("test", "initView: mPictureView "+mPictureView);
            //显示位图
            mPictureView.setBitmap(btmap,modeFormat);

            if(hasFrontCamera()) {
                initcameraView();
            }

            //只有当激活后 才能调用mPictureView.setShowModel(1)切换成3d模式  否则会报错 其他界面也是类似
            isPlay3D = SharedPrefsUtil.getValue(this, com.stereo.util.Constants.PALY_3D, false);

            if (!isPlay3D){
                //无激活状态下初始化播放状态图标为2D并隐藏人眼追踪图标和关闭人眼追踪功能
                if (ivpic_eyes_tacking.getVisibility() == View.VISIBLE) {
                    ivpic_eyes_tacking.setVisibility(View.GONE);//变成不追踪

                    //切换成3d  0 2d 1 3d
                    mPictureView.setShowModel(0);
                    //切换视频格式    0 2d图片  1 左右图片 2 上下图片
                    mPictureView.switchPictureFormat(0);

                }
            }

            isPlay3D = SharedPrefsUtil.getValue(this, com.stereo.util.Constants.PALY_3D, false);

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

            mPictureView.switchPictureFormat(0);
            formattv.setText("2D图片");
           // GlideLogic.glideLoadPic423(LocalPicInfoActivity.this, localPic.getPath(), imgiv, screenHeight * 11 / 16, screenWidth * 11 / 16);
//            GlideLogic.glideLoadPic423fit(LocalPicInfoActivity.this, localPic.getPath(), imgiv, screenHeight * 11 / 16, screenWidth * 11 / 16);
        }

        titleView(true);
    }

    @Override
    public void initEvent() {
        super.initEvent();

        imgiv.setOnClickListener(this);
        backbtn.setOnClickListener(this);
        uploadbtn.setOnClickListener(this);
        formattv.setOnClickListener(this);
        modeltv.setOnClickListener(this);
        mPictureView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.localpicinfo_backbtn:
                finish();
                break;
            case R.id.localpicinfo_uploadbtn:
                userId = SharedPrefsUtil.getValue(getApplicationContext(), com.unbounded.video.constants.Constants.UserId_FLAG, "");

                if(TextUtils.isEmpty(userId)){
                    openActivity(LoginActivity.class);
                }else {
                    Intent intent = new Intent();
                    intent.putExtra("localPicBean",localPic);
                    intent.setClass(getApplicationContext(), PicUploadActivity.class);
                    startActivity(intent);
                }

                break;
            //格式
            case R.id.localpicinfo_formattv:
                formatpop.showAsDropDown(formattv);

                break;
            //模式
            case R.id.localpicinfo_modeltv:
                isPlay3D = SharedPrefsUtil.getValue(this, com.stereo.util.Constants.PALY_3D, false);
                modelPop();
                modelpop.showAsDropDown(modeltv);

                break;
            //title显示隐藏
            case R.id.movie_view:
                if(titlerela.getVisibility() == View.VISIBLE){
                    titleView(false);
                }else{
                    titleView(true);
                }

                break;
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //图片详情
                case TITLE_VIEW:
                    titleView(false);

                    break;
            }
        }
    };

    /**
     * 控制栏显示隐藏
     */
    private void titleView(boolean isvisible){
        if(isvisible == true){
            titlerela.setVisibility(View.VISIBLE);

            handler.removeMessages(TITLE_VIEW);
            handler.sendEmptyMessageDelayed(TITLE_VIEW, 8*oneMinute);
        }else {
            titlerela.setVisibility(View.GONE);

            if(formatpop != null){
                formatpop.dismiss();
            }
            if(modelpop != null){
                modelpop.dismiss();
            }
        }
    }

    /**
     * 模式pop
     */
    private void modelPop(){
        modelview = this.getLayoutInflater().inflate(R.layout.switchbtnspop, null);
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
        ListView modellv = (ListView) modelview.findViewById(R.id.switchbtns_lv);

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
        formatview = this.getLayoutInflater().inflate(R.layout.switchbtnspop, null);
        formatpop = new PopupWindow(formatview,73*oneDp, ViewGroup.LayoutParams.WRAP_CONTENT);
        formatpop.setFocusable(true);
        formatpop.setOutsideTouchable(true);
        formatpop.setBackgroundDrawable(new BitmapDrawable());

        ListView formatlv = (ListView) formatview.findViewById(R.id.switchbtns_lv);

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
    protected void onPause() {
        super.onPause();
        CameraManager.stop();
        if (cameraView != null) {
            cameraView.destroy();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(cameraView != null){
            cameraView.reset();
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
