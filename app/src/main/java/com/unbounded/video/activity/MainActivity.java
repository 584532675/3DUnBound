package com.unbounded.video.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.VideoView;

import com.mopic3d.mplayer3d.tune.TuningActivity;
import com.stereo.Interaction.InteractionManager;
import com.stereo.angle.AngleActivity;
import com.stereo.util.ActionCallback;
import com.stereo.util.Utils;
import com.tbruyelle.rxpermissions.Permission;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.umeng.analytics.MobclickAgent;
import com.unbounded.video.R;
import com.unbounded.video.ZXing.CaptureActivity;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.constants.HttpConstants;
import com.unbounded.video.utils.ExitApplication;
import com.unbounded.video.utils.HttpGetUtil;
import com.unbounded.video.utils.SharedPrefsUtil;
import com.unbounded.video.utils.imageLoad.GlideLogic;
import com.unbounded.video.utils.updateVersion.UpdateManager;
import com.zhuge.analysis.stat.ZhugeSDK;

import org.json.JSONException;
import org.json.JSONObject;

import rx.functions.Action1;


public class MainActivity extends TabActivity implements View.OnClickListener {
    static final String Nomal_Update = "一般更新";
    static final String Must_Update = "强制更新";
    public static final int SPLASH_ADVERTISING = 2;
    public final static String Tabsrela_Height = "bottomrelaHeight";
    RelativeLayout bottomrela,videosrela,picsrela,minerela,mallsrela;
    LinearLayout tabsrela;
    Button videosbtn,picsbtn,minebtn,mallbtn;
    TextView videostv,picstv,minetv,malltv;
    TabHost tabhost;
    private static final int tabNumber = 4;
    private boolean update = false;
    private int versionCode;
    private boolean needUpdate = false;
    boolean forceupdate = false;
    String updateUrl,phoneNum, updateExplain;

    private int screenWidth,screenHeight,bottomrelaHeight;
    RxPermissions rxPermissions;
    Dialog goSetcameraDialog, activeDialog;
    boolean isCamera,isRecode;

    boolean isFirstEnter = true;
    int cameraType;
    ProgressDialog pd1;
    Boolean isWriteExternalPermission = false;
    Boolean isLocalPermission = false;
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.INTERNET_SUCCESS_FLAG:
                    String versionvalue = msg.obj.toString();
                    Log.e("info","versionvalue =" +versionvalue);
                    if(!(TextUtils.isEmpty(versionvalue))){
                        if("{}".equals(versionvalue)){
                            //最新版本，不用更新

                        }else{
                            try {
                                JSONObject versionjo = new JSONObject(versionvalue);
                                String state = versionjo.getString("state");
                                updateUrl = versionjo.getString("url");
                                updateExplain = versionjo.getString("updateExplain");

                                //一般更新
                                if(Nomal_Update.equals(state)){
                                    needUpdate = true;
                                    forceupdate = false;

                                }else if(Must_Update.equals(state)){
                                    needUpdate = true;
                                    forceupdate = true;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            UpdateManager manager = new UpdateManager(screenWidth, screenHeight, needUpdate, forceupdate, updateUrl, MainActivity.this, updateExplain);
                            manager.checkUpdate();
                        }
                    }

                    break;
                case SPLASH_ADVERTISING:
                    String advertising = msg.obj.toString();
                    if (!(TextUtils.isEmpty(advertising))) {
                        try {
                            JSONObject versionjo = new JSONObject(advertising);
                            final String timelong = versionjo.getString("timelong");
                            String endtime = versionjo.getString("endtime");
                            String starttime = versionjo.getString("starttime");
                            String state = versionjo.getString("state");
                            String type = versionjo.getString("type");
                            String url = versionjo.getString("url");
                            if (state.equals("1")) {
                                if (type.equals("gif")||type.equals("image")) {
                                    advertisingVideo.setVisibility(View.GONE);
                                    splashAdvertisingIv.setVisibility(View.VISIBLE);
                                    GlideLogic.glideLoadVideoImgAdvertising(MainActivity.this,
                                            url,splashAdvertisingIv);
                                    time = new TimeCount((Integer.valueOf(timelong)+1) * 1000, 1000
                                            , new TimeCountCallBack() {
                                        @Override
                                        public void onFinish() {
                                            if (splashAdvertisingRl != null && !isSkip) {
                                                /*splashAdvertisingRl.setVisibility(View.GONE);*/
                                                startAnimator();
                                                mainRl.setVisibility(View.VISIBLE);
                                            }
                                        }

                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            if (timeLongTv != null) {
                                                timeLongTv.setVisibility(View.VISIBLE);
                                                timeLongTv.setText(millisUntilFinished / 1000 + "跳过");
                                            }
                                        }
                                    });
                                    time.start();
                                } else if (type.equals("video")) {
                                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                                            RelativeLayout.LayoutParams.FILL_PARENT,
                                            RelativeLayout.LayoutParams.FILL_PARENT);
                                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                    advertisingVideo.setLayoutParams(layoutParams);
                                    advertisingVideo.setVisibility(View.VISIBLE);
                                    advertisingVideo.setVideoURI(Uri.parse(url));
                                    advertisingVideo.start();
                                    advertisingVideo.setBackground(getResources().getDrawable(R.mipmap.splash_background));
                                    advertisingVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                        @Override
                                        public void onPrepared(MediaPlayer mp) {
                                            mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                                                @Override
                                                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                                                    if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START){
                                                        advertisingVideo.setBackgroundColor(Color.TRANSPARENT);
                                                        time = new TimeCount((Integer.valueOf(timelong)+1) * 1000, 1000
                                                                , new TimeCountCallBack() {
                                                            @Override
                                                            public void onFinish() {
                                                                if (splashAdvertisingRl != null && !isSkip) {
                                                /*splashAdvertisingRl.setVisibility(View.GONE);*/
                                                                    startAnimator();
                                                                    mainRl.setVisibility(View.VISIBLE);
                                                                }
                                                            }

                                                            @Override
                                                            public void onTick(long millisUntilFinished) {
                                                                if (timeLongTv != null) {
                                                                    timeLongTv.setVisibility(View.VISIBLE);
                                                                    timeLongTv.setText(millisUntilFinished / 1000 + "跳过");
                                                                }
                                                            }
                                                        });
                                                        time.start();
                                                    }

                                                    return false;
                                                }
                                            });
                                        }
                                    });

                                }
                            }else{
                                mainRl.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            Log.v("", "");
                            e.printStackTrace();
                        }
                    }

                    break;
                case Constants.INTERNET_ERROR_FLAG:
                    mainRl.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };
    private boolean isFirstSplash;
    boolean isVisibileGuide = true;
    private RelativeLayout splashGuide;
    private ImageView intentCalibrationIv;
    private ImageView splashSkipIv;
    private RelativeLayout splashAdvertisingRl;
    private ImageView splashAdvertisingIv;
    private VideoView advertisingVideo;
    private RelativeLayout mainRl;
    private TimeCount time;
    private TextView timeLongTv;
    boolean isSkip = false;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setTheme(R.style.AppTheme_home);
        super.onCreate(savedInstanceState);
        smoothSwitchScreen();

        /*沉浸式状态栏*/
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    );
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }

        setContentView(com.unbounded.video.R.layout.activity_main);

        ExitApplication.getInstance().addActivity(this);
        rxPermissions = new RxPermissions(this);
        pd1 = new ProgressDialog(this);
        pd1.setCanceledOnTouchOutside(false);
        init();
        initView();
        initParams();
//        pd1.setCancelable(false);
        initEvent();

        final int M = 1024 * 1024;
        final Runtime runtime = Runtime.getRuntime();
        Log.i("info", "max =" + runtime.maxMemory() / M + "M");
        Log.i("info", "avila =" + runtime.totalMemory() / M + "M");
        Log.i("info", "free =" + runtime.freeMemory() / M + "M");
        Log.i("info", "used =" + (runtime.totalMemory() - runtime.freeMemory()) / M + "M");

    }
    private void smoothSwitchScreen() {
        // 5.0以上修复了此bug
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ViewGroup rootView = ((ViewGroup) this.findViewById(android.R.id.content));
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            int statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            rootView.setPadding(0, statusBarHeight, 0, 0);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     *
     */
    private void init() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        isFirstSplash = SharedPrefsUtil.getValue(this, Constants.IS_FIRST_SPLASH, true);
        if (isFirstSplash) {
            SharedPrefsUtil.putValue(this, Constants.IS_FIRST_SPLASH, false);
        }
//        update = bundle.getBoolean("update");
//        isFirstSplash = bundle.getBoolean("isFirstSplash");
        getVersionCode();
        checkVersion();

        if(screenWidth == Constants.ZERO || screenHeight == Constants.ZERO){
            screenWidth = SharedPrefsUtil.getValue(getApplicationContext(), Constants.SCREEN_WIDTH, Constants.ZERO);
            screenHeight = SharedPrefsUtil.getValue(getApplicationContext(), Constants.SCREEN_HEIGHT, Constants.ZERO);

            if(screenWidth == Constants.ZERO || screenHeight == Constants.ZERO ){
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                screenWidth = dm.widthPixels;
                screenHeight = dm.heightPixels;

                SharedPrefsUtil.putValue(getApplicationContext(), Constants.SCREEN_WIDTH, screenWidth);
                SharedPrefsUtil.putValue(getApplicationContext(), Constants.SCREEN_HEIGHT, screenHeight);

            }
        }

        isFirstEnter = SharedPrefsUtil.getValue(getApplicationContext(), Constants.IsFirstEnter_Flag, true);
        //判断是否第一次启动app
        if(isFirstEnter){
            /*添加权限判断*/
            requestPermission2();
             /*判断默认播放器*/
            String phoneModel = android.os.Build.MODEL;
            SharedPrefsUtil.putValue(this,Constants.MODEL,phoneModel);
            if (phoneModel.equals("MI 8") || phoneModel.equals("EML-AL00") || phoneModel.equals("EML-AL00")
                    || phoneModel.equals("CLT-AL01") || phoneModel.equals("vivo X21A")
                    || phoneModel.equals("vivo X21UD") || phoneModel.equals("vivo X21UD A") ||
                    phoneModel.equals("OPPO PACM00")) {
                Log.v("isFirstEnter phoneModel","true:"+phoneModel);
                SharedPrefsUtil.putValue(this, Constants.Mopic, true);
            } else {
                Log.v("isFirstEnter phoneModel","false:"+phoneModel);
                SharedPrefsUtil.putValue(this, Constants.Mopic, false);
            }
        } else {
            //判断是否激活
            if (Utils.isPlay3D(this)) {
                //3d播放
                SharedPrefsUtil.putValue(this, com.stereo.util.Constants.PALY_3D, true);
            } else {
                //2d播放
                SharedPrefsUtil.putValue(this, com.stereo.util.Constants.PALY_3D, false);
            }
        }


    }
    public void activeFlag(){
        //调用无二位码激活接口
        InteractionManager.getInstance(this, new ActionCallback() {
            /**
             * 激活请求接口
             *  resultCode
             *  1.激活成功
             *  2.解密失败
             *  3.网络超时
             *  4.接受的参数为空
             *  5.解密失败，解密后的序列号非全字符串
             *  6.序列号拆分失败
             *  7.根据解密后机型号没有获取到对应的机型
             *  8.机型不匹配
             *  9.根据拆分后的序列号没有查到对应的参数
             *  10.调用加密算法失败
             *  11.序列号不存在
             *  12.json解析异常
             *  13.暂不支持此机型
             *
             */
            @Override
            public void onRequset(int returnCode) {
                Log.i("jihuo", "onRequset: " + returnCode);

                /**
                 * 激活结果总次数
                 */
                phoneNum = SharedPrefsUtil.getValue(getApplicationContext(), Constants.Phone_FLAG, Constants.Phone_default);
                JSONObject personObject = null;
                try {
                    personObject = new JSONObject();
                    personObject.put("phoneNum", phoneNum);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ZhugeSDK.getInstance().track(getApplicationContext(),"激活结果总次数",personObject);

                switch (returnCode){
                    case 1:
//                        ToastUtil.showToast(SettingActivity.this, "激活成功,请开始眼球追踪校准",ToastUtil.CENTER);
//                        Intent intent = new Intent();
//                        intent.putExtra("activeSuccess",true);
//                        intent.setClass(getApplicationContext(), AngleActivity.class);
//                        startActivity(intent);

                        //ToastUtil.showToast(getApplicationContext(), "激活成功,请开始眼球追踪校准",ToastUtil.CENTER);
                        Log.i("jihuo","jihuosuccess");
                        /**
                         * 激活成功
                         */
                        ZhugeSDK.getInstance().track(getApplicationContext(),"激活成功",personObject);
                        ZhugeSDK.getInstance().track(getApplicationContext(),"进入校准界面总次数",personObject);

                        break;
                    case 2:
//                                    ToastUtil.showToast(getActivity(), "解密失败", ToastUtil.CENTER);
                        Log.v("InteractionManager 2","解密失败");
                        break;
                    case 3:
//                                    ToastUtil.showToast(getActivity(), "网络超时", ToastUtil.CENTER);
                        Log.v("InteractionManager 3","网络超时");
                        break;
                    case 4:
                        Log.v("InteractionManager 4","接受的参数为空");
//                                    ToastUtil.showToast(getActivity(), "接受的参数为空", ToastUtil.CENTER);
                        break;
                    case 5:
                        Log.v("InteractionManager 5","解密失败，解密后的序列号非全字符串");
//                                    ToastUtil.showToast(getActivity(), "解密失败，解密后的序列号非全字符串", ToastUtil.CENTER);
                        break;
                    case 6:
                        Log.v("InteractionManager 6","序列号拆分失败");
//                                    ToastUtil.showToast(getActivity(), "序列号拆分失败", ToastUtil.CENTER);
                        break;
                    case 7:
                        Log.v("InteractionManager 7","根据解密后机型号没有获取到对应的机型");
//                                    ToastUtil.showToast(getActivity(), "根据解密后机型号没有获取到对应的机型", ToastUtil.CENTER);
                        break;
                    case 8:
                        Log.v("InteractionManager 8","机型不匹配");
//                                    ToastUtil.showToast(getActivity(), "机型不匹配", ToastUtil.CENTER);
                        break;
                    case 9:
                        Log.v("InteractionManager 9","根据拆分后的序列号没有查到对应的参数");
//                                    ToastUtil.showToast(getActivity(), "根据拆分后的序列号没有查到对应的参数", ToastUtil.CENTER);
                        break;
                    case 10:
                        Log.v("InteractionManager 10","调用加密算法失败");
//                                    ToastUtil.showToast(getActivity(), "调用加密算法失败", ToastUtil.CENTER);
                        break;
                    case 11:
                        Log.v("InteractionManager 11","序列号不存在");
//                                    ToastUtil.showToast(getActivity(), "序列号不存在", ToastUtil.CENTER);
                        break;
                    case 12:
                        Log.v("InteractionManager 12","json解析异常");
//                                    ToastUtil.showToast(getActivity(), "json解析异常", ToastUtil.CENTER);
                        break;
                    case 13:
                        Log.v("InteractionManager 13","暂不支持此机型");
//                                    ToastUtil.showToast(getActivity(), "暂不支持此机型", ToastUtil.CENTER);
                        break;
                }
            }
        }).Action();

        SharedPrefsUtil.putValue(getApplicationContext(), Constants.IsFirstEnter_Flag, false);
        //2d播放
//            SharedPrefsUtil.putValue(this, com.stereo.util.Constants.PALY_3D, false);

        //弹出询问是否校准对话框
//        activeDia();
        //不是首次进入
    }
    /**
     * 激活对话框
     */
    private void activeDia(){
        View diaView = View.inflate(MainActivity.this, com.unbounded.video.R.layout.notice_dialog, null);
        activeDialog = new Dialog(MainActivity.this, com.unbounded.video.R.style.dialog);
        activeDialog.setContentView(diaView);
        activeDialog.setCanceledOnTouchOutside(false);
        activeDialog.setCancelable(false);

        Button setafterbtn = (Button) activeDialog.findViewById(com.unbounded.video.R.id.notice_yesbtn);
        Button tonowbtn = (Button) activeDialog.findViewById(com.unbounded.video.R.id.notice_canclebtn);
        TextView titletv = (TextView) activeDialog.findViewById(com.unbounded.video.R.id.notice_tv1);
        TextView contenttv = (TextView) activeDialog.findViewById(com.unbounded.video.R.id.notice_tv2);

        titletv.setVisibility(View.GONE);

        contenttv.setText(com.unbounded.video.R.string.activebefore);
        setafterbtn.setText(com.unbounded.video.R.string.setafter);
        tonowbtn.setText(com.unbounded.video.R.string.tonow);

        setafterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activeDialog.dismiss();
            }
        });
        //
        tonowbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activeDialog.dismiss();
                /*去校准*/
                goCalibration();
            }
        });


        activeDialog.show();

        WindowManager.LayoutParams params = activeDialog.getWindow().getAttributes();
        params.width = screenWidth * 3 / 4;
        // params.height = 200 ;
        activeDialog.getWindow().setAttributes(params);

    }

    /*去校准*/
    public void goCalibration() {
        boolean isMopic = SharedPrefsUtil.getValue(this, Constants.Mopic, false);
        if(isMopic){
            try {
                Intent intent = new Intent(getApplicationContext(), TuningActivity.class);
                intent.putExtra("fromPlayer", "fromFirst"); //"fromPlayer");
                startActivity(intent);
            }catch (ActivityNotFoundException exception){
                SharedPrefsUtil.putValue(this, Constants.Mopic, false);
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), AngleActivity.class);
                startActivity(intent);
            }
        }else{
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), AngleActivity.class);
            startActivity(intent);
        }
        /**
         * 去校准
         */
        phoneNum = SharedPrefsUtil.getValue(getApplicationContext(), Constants.Phone_FLAG, Constants.Phone_default);
        JSONObject personObject = null;
        try {
            personObject = new JSONObject();
            personObject.put("phoneNum", phoneNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ZhugeSDK.getInstance().track(MainActivity.this, "去校准", personObject);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
//                    showProgressDialog();
                    pd1.setMessage(getString(com.unbounded.video.R.string.Word_loading));
                    pd1.show();
                    //显示扫描到的内容
                    String sanString = data.getStringExtra("result").trim();
                    //调用激活接口激活
//                    InteractionManager.getInstance(this, new ActionCallback() {
//                        /**
//                         * 激活请求接口
//                         *  resultCode
//                         *  1.激活成功
//                         *  2.解密失败
//                         *  3.网络超时
//                         *  4.接受的参数为空
//                         *  5.解密失败，解密后的序列号非全字符串
//                         *  6.序列号拆分失败
//                         *  7.根据解密后机型号没有获取到对应的机型
//                         *  8.机型不匹配
//                         *  9.根据拆分后的序列号没有查到对应的参数
//                         *  10.调用加密算法失败
//                         *  11.序列号不存在
//                         *  12.json解析异常
//                         *  13.暂不支持此机型
//                         *
//                         */
//                        @Override
//                        public void onRequset(int returnCode) {
//                            Log.i("PlayActivity", "onRequset: " + returnCode);
//
//                            /**
//                             * 激活结果总次数
//                             */
//                            phoneNum = SharedPrefsUtil.getValue(getApplicationContext(), Constants.Phone_FLAG, Constants.Phone_default);
//                            JSONObject personObject = null;
//                            try {
//                                personObject = new JSONObject();
//                                personObject.put("phoneNum", phoneNum);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                            ZhugeSDK.getInstance().track(MainActivity.this,"激活结果总次数", personObject);
//
//                            switch (returnCode){
//                                case 1:
//                                    ToastUtil.showToast(MainActivity.this, "激活成功,请开始眼球追踪校准",ToastUtil.BOTTOM);
//
//                                    Intent intent = new Intent();
//                                    intent.putExtra("activeSuccess",true);
//                                    intent.setClass(getApplicationContext(), AngleActivity.class);
//                                    startActivity(intent);
//
//                                    /**
//                                     * 激活成功
//                                     */
//                                    ZhugeSDK.getInstance().track(MainActivity.this,"激活成功",personObject);
//                                    ZhugeSDK.getInstance().track(getApplicationContext(),"进入校准界面总次数",personObject);
//
//                                    break;
//                                case 2:
//                                    ToastUtil.showToast(MainActivity.this, "解密失败",ToastUtil.CENTER);
//                                    break;
//                                case 3:
//                                    ToastUtil.showToast(MainActivity.this, "网络超时",ToastUtil.CENTER);
//                                    break;
//                                case 4:
//                                    ToastUtil.showToast(MainActivity.this, "接受的参数为空",ToastUtil.CENTER);
//                                    break;
//                                case 5:
//                                    ToastUtil.showToast(MainActivity.this, "解密失败，解密后的序列号非全字符串",ToastUtil.CENTER);
//                                    break;
//                                case 6:
//                                    ToastUtil.showToast(MainActivity.this, "序列号拆分失败",ToastUtil.CENTER);
//                                    break;
//                                case 7:
//                                    ToastUtil.showToast(MainActivity.this, "根据解密后机型号没有获取到对应的机型",ToastUtil.CENTER);
//                                    break;
//                                case 8:
//                                    ToastUtil.showToast(MainActivity.this, "机型不匹配",ToastUtil.CENTER);
//                                    break;
//                                case 9:
//                                    ToastUtil.showToast(MainActivity.this, "根据拆分后的序列号没有查到对应的参数",ToastUtil.CENTER);
//                                    break;
//                                case 10:
//                                    ToastUtil.showToast(MainActivity.this, "调用加密算法失败",ToastUtil.CENTER);
//                                    break;
//                                case 11:
//                                    ToastUtil.showToast(MainActivity.this, "序列号不存在",ToastUtil.CENTER);
//                                    break;
//                                case 12:
//                                    ToastUtil.showToast(MainActivity.this, "json解析异常",ToastUtil.CENTER);
//                                    break;
//                                case 13:
//                                    ToastUtil.showToast(MainActivity.this, "暂不支持此机型",ToastUtil.CENTER);
//                                    break;
//                            }
//
//                            pd1.dismiss();
//                        }
//                    }).interAction(sanString);
                }
                break;
        }
    }
    /**
     * 激活
     */
    private void active(){
        rxPermissions.request(Manifest.permission.CAMERA)//这里填写所需要的权限
            .subscribe(new Action1<Boolean>() {
                @Override
                public void call(Boolean aBoolean) {
                    if (aBoolean) {//true表示获取权限成功（注意这里在android6.0以下默认为true）
                        //Log.i(TAG, Manifest.permission.CAMERA + "success");
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, CaptureActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivityForResult(new Intent(MainActivity.this, CaptureActivity.class), com.stereo.util.Constants.REQUESTCODE);
                    } else {
                        cameraType = 0;
                        cameradiaShow();
                    }
                }
            });
    }
    /**
     *
     */
    public void cameradiaShow(){
        if(goSetcameraDialog != null){
            if(!(goSetcameraDialog.isShowing())){
                goSetcameraDialog.show();
            }
        }else {
            goSetCameraDia();
        }
    }

    /**
     * 检测升级
     */
    private void checkVersion(){
        String checkUrl = HttpConstants.VersionUpdate_Url + "?version=" + versionCode + "&comeFrom=gm";
        HttpGetUtil get = new HttpGetUtil(handler,checkUrl , Constants.INTERNET_SUCCESS_FLAG);
        Thread thread = new Thread(get);
        thread.start();
    }

    /**
     * 初始化控件
     */
    private void initView(){
        bottomrela = (RelativeLayout) findViewById(com.unbounded.video.R.id.first_buttomrela);
        tabsrela = (LinearLayout) findViewById(com.unbounded.video.R.id.first_tabsrela);
        videosrela = (RelativeLayout) findViewById(com.unbounded.video.R.id.first_videosrela);
        mallsrela = (RelativeLayout) findViewById(R.id.mall_videosrela);
        picsrela = (RelativeLayout) findViewById(com.unbounded.video.R.id.first_picsrela);
        minerela = (RelativeLayout) findViewById(com.unbounded.video.R.id.first_minerela);

        videosbtn = (Button) findViewById(com.unbounded.video.R.id.first_videosbtn);
        picsbtn = (Button) findViewById(com.unbounded.video.R.id.first_picsbtn);
        minebtn = (Button) findViewById(com.unbounded.video.R.id.first_minebtn);
        mallbtn = (Button) findViewById(R.id.mall_videosbtn);

        videostv = (TextView) findViewById(com.unbounded.video.R.id.first_videostv);
        picstv = (TextView) findViewById(com.unbounded.video.R.id.first_picstv);
        minetv = (TextView) findViewById(com.unbounded.video.R.id.first_minetv);
        malltv = (TextView) findViewById(R.id.mall_videostv);
        intentCalibrationIv = (ImageView) findViewById(R.id.intent_calibration_iv);
        splashSkipIv = (ImageView) findViewById(R.id.splash_skip_iv);
        splashGuide = (RelativeLayout) findViewById(R.id.splash_guide);
        splashAdvertisingRl = (RelativeLayout)findViewById(R.id.splash_advertising_rl);
        splashAdvertisingIv = (ImageView) findViewById(R.id.splash_advertising_iv);
        advertisingVideo = (VideoView) findViewById(R.id.advertising_video);
        mainRl = (RelativeLayout)findViewById(R.id.main_rl);
        timeLongTv = (TextView) findViewById(R.id.time_long_tv);
        if(isFirstSplash){
            splashGuide.setVisibility(View.VISIBLE);
            splashAdvertisingRl.setVisibility(View.GONE);
        }else{
            splashAdvertisingRl.setVisibility(View.VISIBLE);
            splashAdvertisingRl.setAlpha(1);
            mainRl.setVisibility(View.GONE);
            HttpGetUtil advertisingHttp = new HttpGetUtil(handler, HttpConstants.SPLASH_ADVERTISING, SPLASH_ADVERTISING);
            Thread advertisingThread = new Thread(advertisingHttp);
            advertisingThread.start();
        }
    }


    @Override
    protected void onResume() {
        if(isFirstSplash){
            splashGuide.setVisibility(View.VISIBLE);
        }else{
            splashGuide.setVisibility(View.GONE);
        }
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    /**
     * 启动时请求权限
     */
    private void requestPermission2() {
        rxPermissions.requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE, /*Manifest.permission.ACCESS_COARSE_LOCATION, */Manifest.permission.CAMERA/*,*//* Manifest.permission.RECORD_AUDIO,*//*Manifest.permission.ACCESS_FINE_LOCATION*/).subscribe(new Action1<Permission>() {
            @Override
            public void call(Permission permission) {
                switch (permission.name) {
                    case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                        //有读写权限
                        // 有读取权限时先读取数据，再进入主页
//拒绝读取权限，弹出对话框
                        isWriteExternalPermission = permission.granted;
                        break;
                    case Manifest.permission.ACCESS_COARSE_LOCATION:
                        //有读写权限
                        // 有读取位置
//拒绝读取位置权限
                        isLocalPermission = permission.granted;
                        break;
                    case Manifest.permission.CAMERA:
                        //有权限
                        // 有
//拒绝读取位置权限
                        isCamera = permission.granted;
                        if (permission.granted) {
                            // 有
                            isRecode = true;
                            if(isWriteExternalPermission == true/* && isLocalPermission == true*/ /*&& isCamera == true *//*&& isRecode == true*/){
//                                enterFirst();
                                activeFlag();
                            }else {
                                diaShow();

                            }
                        } else {
                            //拒绝读取位置权限
                            isRecode = false;
                            diaShow();

                        }
                        break;
                    case Manifest.permission.RECORD_AUDIO:
                        //有权限

                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     *
     */
    private void initParams(){
        bottomrelaHeight = screenHeight * 19 / 250;
        SharedPrefsUtil.putValue(getApplicationContext(), Tabsrela_Height, bottomrelaHeight);

        ViewGroup.LayoutParams bottomrelaparams = bottomrela.getLayoutParams();
        bottomrelaparams.height = (screenHeight * 17 / 192) + 20;
        bottomrela.setLayoutParams(bottomrelaparams);


        ViewGroup.LayoutParams tabsrelaparams = tabsrela.getLayoutParams();
        tabsrelaparams.height = screenHeight * 19 / 250;
        tabsrela.setLayoutParams(tabsrelaparams);

        ViewGroup.LayoutParams videosrelaparams = videosrela.getLayoutParams();
//        videosrelaparams.height = screenHeight * 19 / 250;
        videosrelaparams.width = screenWidth / tabNumber;
        videosrela.setLayoutParams(videosrelaparams);



        ViewGroup.LayoutParams malllaparams = mallsrela.getLayoutParams();
//        videosrelaparams.height = screenHeight * 19 / 250;
        malllaparams.width = screenWidth / tabNumber;
        mallsrela.setLayoutParams(malllaparams);



        ViewGroup.LayoutParams picsrelaparams = picsrela.getLayoutParams();
//        picsrelaparams.height = screenHeight * 19 / 250;
        picsrelaparams.width = screenWidth / tabNumber;
        picsrela.setLayoutParams(picsrelaparams);


        ViewGroup.LayoutParams minerelaparams = minerela.getLayoutParams();
//        minerelaparams.height = screenHeight * 19 / 250;
        minerelaparams.width = screenWidth / tabNumber;
        minerela.setLayoutParams(minerelaparams);

        ViewGroup.LayoutParams videosbtnparams = videosbtn.getLayoutParams();
        videosbtnparams.height = screenWidth * 19 / 270;
        videosbtnparams.width = screenWidth * 19 / 270;
        videosbtn.setLayoutParams(videosbtnparams);

        ViewGroup.LayoutParams picsbtnparams = picsbtn.getLayoutParams();
        picsbtnparams.height = screenWidth * 19 / 270;
        picsbtnparams.width = screenWidth * 19 / 270;
        picsbtn.setLayoutParams(picsbtnparams);

        ViewGroup.LayoutParams minebtnparams = minebtn.getLayoutParams();
        minebtnparams.height = screenWidth * 19 / 270;
        minebtnparams.width = screenWidth * 19 / 270;
        minebtn.setLayoutParams(minebtnparams);


        ViewGroup.LayoutParams mallbtnparams = mallbtn.getLayoutParams();
        mallbtnparams.height = screenWidth * 19 / 270;
        mallbtnparams.width = screenWidth * 19 / 270;
        mallbtn.setLayoutParams(mallbtnparams);
    }

    /**
     * 初始化监 界面跳转
     */
    private void initEvent(){
//        videosrela,picsrela,takepicrela,shoprela,minerela;
//        Button videosbtn,picsbtn,takepicbtn,shopbtn,minebtn;
//        TextView videostv,picstv,shoptv,minetv;
        tabhost = getTabHost();
        tabhost.addTab(tabhost.newTabSpec("videos").setIndicator("videos")
                .setContent(new Intent(this, VideosActivity.class)));  //首页
        tabhost.addTab(tabhost.newTabSpec("mall").setIndicator("mall")
                .setContent(new Intent(this, MallActivity.class)));   //商城
        tabhost.addTab(tabhost.newTabSpec("pics").setIndicator("pics")
                .setContent(new Intent(this,/*PicsActivity*/LocalVideoActivity.class)));    //本地
        tabhost.addTab(tabhost.newTabSpec("mine").setIndicator("mine")
                .setContent(new Intent(this,MineActivity.class)));              //我的

        videosrela.setOnClickListener(this);
        picsrela.setOnClickListener(this);
        minerela.setOnClickListener(this);
        mallsrela.setOnClickListener(this);

        videosbtn.setOnClickListener(this);
        picsbtn.setOnClickListener(this);
        minebtn.setOnClickListener(this);
        mallbtn.setOnClickListener(this);

        intentCalibrationIv.setOnClickListener(this);
        splashSkipIv.setOnClickListener(this);
        timeLongTv.setOnClickListener(this);
    }

    private void picTabNum(){
        /**
         * 图片tab次数
         */
        phoneNum = SharedPrefsUtil.getValue(getApplicationContext(), Constants.Phone_FLAG, Constants.Phone_default);
        JSONObject personObject = null;
        try {
            personObject = new JSONObject();
            personObject.put("phoneNum", phoneNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ZhugeSDK.getInstance().track(getApplicationContext(),"首页图片tab", personObject);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case com.unbounded.video.R.id.first_videosrela:
                if (!("videos".equals(tabhost.getCurrentTabTag())))
                {
                    tabhost.setCurrentTabByTag("videos");
                    checkTab();
                    videostv.setTextColor(getResources().getColor(com.unbounded.video.R.color.redff));
                    videosbtn.setBackgroundResource(com.unbounded.video.R.mipmap.home);
                }

                break;
            case R.id.mall_videosrela:
                if (!("mall".equals(tabhost.getCurrentTabTag())))
                {
                tabhost.setCurrentTabByTag("mall");
                checkTab();
                malltv.setTextColor(getResources().getColor(com.unbounded.video.R.color.redff));
                mallbtn.setBackgroundResource(com.unbounded.video.R.mipmap.mall);
                picTabNum();
                }
                break;
            case com.unbounded.video.R.id.first_picsrela:
                if (!("pics".equals(tabhost.getCurrentTabTag())))
                {
                    tabhost.setCurrentTabByTag("pics");
                    checkTab();
                    picstv.setTextColor(getResources().getColor(com.unbounded.video.R.color.redff));
                    picsbtn.setBackgroundResource(com.unbounded.video.R.mipmap.local);
                    picTabNum();
                }

                break;

            case com.unbounded.video.R.id.first_minerela:
                if (!("mine".equals(tabhost.getCurrentTabTag())))
                {
                    tabhost.setCurrentTabByTag("mine");
                    checkTab();
                    minetv.setTextColor(getResources().getColor(com.unbounded.video.R.color.redff));
                    minebtn.setBackgroundResource(com.unbounded.video.R.mipmap.mine);

                }
                break;
            case com.unbounded.video.R.id.first_videosbtn:
                if (!("videos".equals(tabhost.getCurrentTabTag())))
                {
                    tabhost.setCurrentTabByTag("videos");
                    checkTab();
                    videostv.setTextColor(getResources().getColor(com.unbounded.video.R.color.redff));
                    videosbtn.setBackgroundResource(R.mipmap.home);
                }
                break;
            case R.id.mall_videosbtn:
                if (!("mall".equals(tabhost.getCurrentTabTag())))
                {
                    tabhost.setCurrentTabByTag("mall");
                    checkTab();
                    malltv.setTextColor(getResources().getColor(com.unbounded.video.R.color.redff));
                    mallbtn.setBackgroundResource(com.unbounded.video.R.mipmap.mall);
                    picTabNum();
                }
                break;
            case com.unbounded.video.R.id.first_picsbtn:
                if (!("pics".equals(tabhost.getCurrentTabTag())))
                {
                    tabhost.setCurrentTabByTag("pics");
                    checkTab();
                    picstv.setTextColor(getResources().getColor(com.unbounded.video.R.color.redff));
                    picsbtn.setBackgroundResource(com.unbounded.video.R.mipmap.local);
                    picTabNum();

                }
                break;
            case com.unbounded.video.R.id.first_minebtn:
                if (!("mine".equals(tabhost.getCurrentTabTag())))
                {
                    tabhost.setCurrentTabByTag("mine");
                    checkTab();
                    minetv.setTextColor(getResources().getColor(com.unbounded.video.R.color.redff));
                    minebtn.setBackgroundResource(com.unbounded.video.R.mipmap.mine);
                }
                break;
            case R.id.intent_calibration_iv:
                isFirstSplash =false;
                goCalibration();
                break;
            case R.id.splash_skip_iv:
                isFirstSplash =false;
                splashGuide.setVisibility(View.GONE);
                break;
            case R.id.time_long_tv:
                if (splashAdvertisingRl != null) {
//                    splashAdvertisingRl.setVisibility(View.GONE);
                    //透明度起始为1，结束时为0
                    isSkip = true;
                    startAnimator();
                    mainRl.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    public void startAnimator() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(splashAdvertisingRl, "alpha", 1f, 0f);
        animator.setDuration(1000);//时间1s
        animator.start();
    }
    /**
     * 摄像申请权限方法
     */
    private void takeVideo(){
        rxPermissions.requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO).subscribe(new Action1<Permission>() {
            @Override
            public void call(Permission permission) {
                switch (permission.name) {

                    case Manifest.permission.CAMERA:
                        //有权限
                        // 有
//拒绝读取位置权限
                        isCamera = permission.granted;
                        break;
                    case Manifest.permission.RECORD_AUDIO:
                        //有权限
                        if (permission.granted) {
                            // 有
                            isRecode = true;
                            if(isCamera == true && isRecode == true){
                                Intent intent = new Intent();
                                intent.setClass(getApplicationContext(), VideoRecordActivity.class);
                                startActivity(intent);
                            }else {
                                diaShow();

                            }
                        } else {
                            //拒绝读取位置权限
                            isRecode = false;
                            diaShow();

                        }
                        break;
                }
            }
        });
    }


    /**
     *
     */
    private void diaShow(){
        if(goSetcameraDialog != null){
            if(!(goSetcameraDialog.isShowing())){
                goSetcameraDialog.show();
            }
        }else {
            cameraType = 1;
            goSetCameraDia();
        }
    }

    /**
     * 用户拒绝后，继续弹出对话框
     */
    public void goSetCameraDia(){
        View diaView = View.inflate(MainActivity.this, com.unbounded.video.R.layout.notice_dialog, null);
        goSetcameraDialog = new Dialog(MainActivity.this, com.unbounded.video.R.style.dialog);
        goSetcameraDialog.setContentView(diaView);
        goSetcameraDialog.setCanceledOnTouchOutside(false);
        goSetcameraDialog.setCancelable(false);

        Button toSetbtn = (Button) goSetcameraDialog.findViewById(com.unbounded.video.R.id.notice_yesbtn);
        Button exitbtn = (Button) goSetcameraDialog.findViewById(com.unbounded.video.R.id.notice_canclebtn);
        TextView titletv = (TextView) goSetcameraDialog.findViewById(com.unbounded.video.R.id.notice_tv1);
        TextView contenttv = (TextView) goSetcameraDialog.findViewById(com.unbounded.video.R.id.notice_tv2);

        toSetbtn.setText(getString(com.unbounded.video.R.string.Word_Set));
        exitbtn.setText(getString(com.unbounded.video.R.string.cancle_btn));
        titletv.setText(getString(com.unbounded.video.R.string.Word_noticetitle));

        if(cameraType == 0){
            contenttv.setText(getString(com.unbounded.video.R.string.Word_noticecamera));
        }else {
            contenttv.setText(getString(com.unbounded.video.R.string.Word_noticerecord));
        }



        toSetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSet();
                goSetcameraDialog.dismiss();
            }
        });
        //
        exitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSetcameraDialog.dismiss();
            }
        });

        goSetcameraDialog.show();

        WindowManager.LayoutParams params = goSetcameraDialog.getWindow().getAttributes();
        params.width = screenWidth * 3 / 4;
        // params.height = 200 ;
        goSetcameraDialog.getWindow().setAttributes(params);
    }

    /**
     * 跳转设置界面用户自己设置权限
     */
    public void toSet(){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 1);
    }

    /**
     *
     */
    private void checkTab(){
        videostv.setTextColor(getResources().getColor(com.unbounded.video.R.color.word_three_nine));
        picstv.setTextColor(getResources().getColor(com.unbounded.video.R.color.word_three_nine));
        minetv.setTextColor(getResources().getColor(com.unbounded.video.R.color.word_three_nine));
        malltv.setTextColor(getResources().getColor(com.unbounded.video.R.color.word_three_nine));

        videosbtn.setBackgroundResource(R.mipmap.home_nor);
        picsbtn.setBackgroundResource(R.mipmap.local_nor);
        minebtn.setBackgroundResource(R.mipmap.mine_nor);
        mallbtn.setBackgroundResource(R.mipmap.mall_nor);
    }

    /**
     * 获取版本号
     */
    private void getVersionCode(){
        try
        {
            // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = MainActivity.this.getPackageManager().getPackageInfo("com.unbounded.video", 0).versionCode;
            Log.e("info","versionCode="+versionCode);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
    }


    public interface TimeCountCallBack {
        void onFinish();

        void onTick(long millisUntilFinished);
    }
    /**
     * 倒计时
     */
    class TimeCount extends CountDownTimer {
        TimeCountCallBack timeCountCallBack;
        public TimeCount(long millisInFuture, long countDownInterval,TimeCountCallBack timeCountCallBack) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
            this.timeCountCallBack = timeCountCallBack;
        }
        @Override
        public void onFinish() {// 计时完毕时触发
            timeCountCallBack.onFinish();
        }
        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            timeCountCallBack.onTick(millisUntilFinished);
        }
    }
}
