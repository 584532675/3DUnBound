package com.unbounded.video.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.unbounded.video.BaseActivity;
import com.unbounded.video.PermissionListerner;
import com.unbounded.video.R;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.constants.HttpConstants;
import com.unbounded.video.utils.ACache;
import com.unbounded.video.utils.HttpGetUtil;
import com.unbounded.video.utils.SharedPrefsUtil;
import com.tbruyelle.rxpermissions.Permission;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.zhuge.analysis.stat.ZhugeSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import rx.functions.Action1;

/**
 * Created by zjf on 2017/7/17 0017.
 */

public class SplashActivity extends BaseActivity {
    //    RelativeLayout rela1;
    ImageView logo/*,wordimg*/;
    RxPermissions rxPermissions;
    Boolean isWriteExternalPermission = false;
    Boolean isLocalPermission = false;
    Boolean isCamera = false;
    Boolean isRecode = false;
    Dialog goSetDialog;
    String phoneNum;
    private boolean isFirstSplash;
    public static final int USER_TIME = 1;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case USER_TIME:
                    ACache aCache  =ACache.get(SplashActivity.this);
                    aCache.put(Constants.USER_REDUCE_TIME, 0);
                    aCache.put(Constants.TIME_USER_ID,"");
                    aCache.put(Constants.START_USER_TIME,0);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected int getContentView() {
        return com.unbounded.video.R.layout.activity_splash;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        isFullScreen = true;
        whiteBar();

        ZhugeSDK.getInstance().openLog();
        ZhugeSDK.getInstance().openDebug();
        ZhugeSDK.getInstance().init(SplashActivity.this);

        super.onCreate(savedInstanceState);
    }

    @Override
    public void init(Intent intent) {
        super.init(intent);
        /**
         * 启动计数
         */
        phoneNum = SharedPrefsUtil.getValue(getApplicationContext(), Constants.Phone_FLAG, Constants.Phone_default);
        JSONObject personObject = null;
        try {
            personObject = new JSONObject();
            personObject.put("phoneNum", phoneNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ZhugeSDK.getInstance().track(SplashActivity.this, "APP启动", personObject);

        setTitleViewVisible(false);
        rxPermissions = new RxPermissions(this);
        ACache aCache =ACache.get(this);
        Object time = aCache.getAsObject(Constants.USER_REDUCE_TIME);
        int tiem = 0;
        if(time!=null){
            tiem = (int) time;
        }

        String tiemUserId = aCache.getAsString(Constants.TIME_USER_ID);
        Log.v("userTime", "" + tiem);
        Log.v("tiemuserid", "" + tiemUserId);
        if (tiem != 0 && tiemUserId != null) {
            HttpGetUtil userTime = new HttpGetUtil(handler, HttpConstants.USER_TIME + "?onlinetime=" + tiem + "&userid=" + tiemUserId, USER_TIME);
            Thread timeThread = new Thread(userTime);
            timeThread.start();
        }

    }

    @Override
    public void initView() {
        super.initView();
        isFirstSplash = SharedPrefsUtil.getValue(this, Constants.IS_FIRST_SPLASH, true);
        if (isFirstSplash) {
            enterFirst();
            SharedPrefsUtil.putValue(this, Constants.IS_FIRST_SPLASH, false);
        }
        logo = (ImageView) findViewById(com.unbounded.video.R.id.splash_logo);
//        wordimg= (ImageView) findViewById(com.unbounded.video.R.id.splash_wordimg);
//        rela1 = (RelativeLayout) findViewById(com.unbounded.video.R.id.splash_rela1);
//
//        rela1.setPadding(0,0,0,screenHeight/2);

    }

    @Override
    public void initParams() {
        super.initParams();

//        int width = screenWidth * 5 / 24;
//        ViewGroup.LayoutParams params1 = (ViewGroup.LayoutParams) logo.getLayoutParams();
//        params1.height = width;
//        params1.width = width;
//        logo.setLayoutParams(params1);

//        ViewGroup.LayoutParams wordimgparams = (ViewGroup.LayoutParams) wordimg.getLayoutParams();
//        wordimgparams.height = width * 47/255;
//        wordimgparams.width = width;
//        wordimg.setLayoutParams(wordimgparams);
    }

    @Override
    public void initAnimation() {
        super.initAnimation();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isFirst == 1) {
            ani();
            isFirst = isFirst + 1;
        }


    }

    //
    private void ani() {
        //渐变展示启动屏
//        AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
//        animation.setDuration(100);
//        wordimg.startAnimation(animation);

        AlphaAnimation animation1 = new AlphaAnimation(0.3f, 1.0f);
        animation1.setDuration(100);
        logo.startAnimation(animation1);
        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                requestPermission2();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });
//        requestPermission2();
    }

    /**
     * 进入首页
     */
    private void enterFirst() {
        if (isFirstSplash) {

        }
        Bundle bundle = new Bundle();
        bundle.putBoolean("update", true);
        bundle.putBoolean("isFirstSplash", isFirstSplash);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        openActivity(MainActivity.class, bundle);
        finish();
    }


    /**
     * 点击获取权限方法
     */
    private void requestPermission() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA}, new PermissionListerner() {
            @Override
            public void onGranted() {
//                Toast.makeText(SplashActivity.this,"权限全部同意了",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDenid(List<String> denidPermission) {
                for (String permission : denidPermission) {
//                    Toast.makeText(SplashActivity.this,"权限被拒绝:"+permission,Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    /**
     * 启动时请求权限
     */
    private void requestPermission2() {
        rxPermissions.requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO).subscribe(new Action1<Permission>() {
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
                        break;
                    case Manifest.permission.RECORD_AUDIO:
                        //有权限
                        if (permission.granted) {
                            // 有
                            isRecode = true;
                            if (isWriteExternalPermission == true && isLocalPermission == true && isCamera == true && isRecode == true) {
                                enterFirst();
                            } else {
                                diaShow();

                            }
                        } else {
                            //拒绝读取位置权限
                            isRecode = false;
                            diaShow();

                        }
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
    private void diaShow() {
        if (goSetDialog != null) {
            if (!(goSetDialog.isShowing())) {
                goSetDialog.show();
            }
        } else {
            goSetDia();
        }
    }


    /**
     * 用户拒绝后，继续弹出对话框
     */
    public void goSetDia() {
        View diaView = View.inflate(SplashActivity.this, com.unbounded.video.R.layout.notice_dialog, null);
        goSetDialog = new Dialog(SplashActivity.this, com.unbounded.video.R.style.dialog);
        goSetDialog.setContentView(diaView);
        goSetDialog.setCanceledOnTouchOutside(false);
        goSetDialog.setCancelable(false);

        Button toSetbtn = (Button) goSetDialog.findViewById(com.unbounded.video.R.id.notice_yesbtn);
        Button exitbtn = (Button) goSetDialog.findViewById(com.unbounded.video.R.id.notice_canclebtn);
        TextView titletv = (TextView) goSetDialog.findViewById(com.unbounded.video.R.id.notice_tv1);
        TextView contenttv = (TextView) goSetDialog.findViewById(com.unbounded.video.R.id.notice_tv2);

        toSetbtn.setText(getString(com.unbounded.video.R.string.Word_Set));
        exitbtn.setText(getString(com.unbounded.video.R.string.Word_close));
        titletv.setText(getString(com.unbounded.video.R.string.Word_noticetitle));
        contenttv.setText(getString(com.unbounded.video.R.string.Word_noticecontent));

        toSetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSet();
                goSetDialog.dismiss();
                isFirst = 1;
            }
        });
        //
        exitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitApp();
            }
        });

        goSetDialog.show();

        WindowManager.LayoutParams params = goSetDialog.getWindow().getAttributes();
        params.width = screenWidth * 3 / 4;
        // params.height = 200 ;
        goSetDialog.getWindow().setAttributes(params);
    }


}
