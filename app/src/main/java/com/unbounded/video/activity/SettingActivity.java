package com.unbounded.video.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.mopic3d.mplayer3d.tune.TuningActivity;
import com.stereo.Interaction.InteractionManager;
import com.stereo.angle.AngleActivity;
import com.stereo.util.ActionCallback;
import com.stereo.util.Constants;
import com.umeng.analytics.MobclickAgent;
import com.unbounded.video.BaseActivity;
import com.unbounded.video.R;
import com.unbounded.video.ZXing.CaptureActivity;
import com.unbounded.video.constants.HttpConstants;
import com.unbounded.video.utils.DataCleanManager;
import com.unbounded.video.utils.FileUtil;
import com.unbounded.video.utils.HttpGetUtil;
import com.unbounded.video.utils.HttpPostUtil;
import com.unbounded.video.utils.SharedPrefsUtil;
import com.unbounded.video.utils.ToastUtil;
import com.tbruyelle.rxpermissions.Permission;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.unbounded.video.view.banner.CustomDialog;
import com.youzan.androidsdk.YouzanSDK;
import com.zhuge.analysis.stat.ZhugeSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by zjf on 2017/7/19 0019.
 */

public class SettingActivity extends BaseActivity {
    private static final String TAG = SettingActivity.class.getSimpleName();
    RelativeLayout clearcacherela, introducerela, feedbackrela, activerela, eyeballrela, saferela, copyrightrela;
    TextView cachetv, layouttv;
    private RxPermissions rxPermissions;
    private ProgressDialog m_pDialog;
    Dialog goSetcameraDialog, layoutDialog, activeedTipDialog;
    String phoneNum;
    boolean isPlay3D;
    private static final int YOUZAN_MALL_LOGIN_OUT = 1;
    List<String> youZanLoginOutKeyList = new ArrayList<>();
    List<Object> youZanLoginOutValueList = new ArrayList<>();
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case com.unbounded.video.constants.Constants.INTERNET_SUCCESS_FLAG:
                    String value = msg.obj.toString();
                    Log.e("info", "value=" + value);

                    if ("true".equals(value)) {
//                        layoutSuccess();
//                        SharedPrefsUtil.putValue(getApplicationContext(), com.unbounded.video.constants.Constants.UserInfoUpdate, true);
                        /*有赞商城清空登陆信息，记录*/
                        youZanLoginOutKeyList.add("client_id");
                        youZanLoginOutValueList.add(com.unbounded.video.constants.Constants.YOU_ZAN_MALL_CLIENT_ID);
                        youZanLoginOutKeyList.add("client_secret");
                        youZanLoginOutValueList.add(
                                com.unbounded.video.constants.Constants.YOU_ZAN_MALL_CLIENT_SECRE);
                        youZanLoginOutKeyList.add("open_user_id");
                        youZanLoginOutValueList.add(SharedPrefsUtil.getValue(SettingActivity.this,
                                com.unbounded.video.constants.Constants.UserId_FLAG, ""));
                        HttpPostUtil post = new HttpPostUtil(handler, HttpConstants.YouZan_Mall_Login_OUT, youZanLoginOutKeyList, youZanLoginOutValueList
                                , YOUZAN_MALL_LOGIN_OUT, 0);
                        Thread thread = new Thread(post);
                        thread.start();
                    } else if ("false".equals(value)) {
                        ToastUtil.showToast(SettingActivity.this, com.unbounded.video.R.string.Word_layoutfail, ToastUtil.CENTER);
                        progressDid1dismiss();
                    }

                    break;

                case com.unbounded.video.constants.Constants.INTERNET_ERROR_FLAG:
                    ToastUtil.showToast(SettingActivity.this, com.unbounded.video.R.string.internet_error, ToastUtil.CENTER);
                    progressDid1dismiss();
                    break;
                case YOUZAN_MALL_LOGIN_OUT:
                    String youZanLogin = msg.obj.toString();
                    Log.e("info", "youZanLoginOut=" + youZanLogin);

                    if (!(TextUtils.isEmpty(youZanLogin))) {
                        try {
                            JSONObject loginjo = new JSONObject(youZanLogin);
                            int code = loginjo.getInt("code");
                            if (code == 0) {
                                layoutSuccess();
                                SharedPrefsUtil.putValue(getApplicationContext(), com.unbounded.video.constants.Constants.UserInfoUpdate, true);
                                YouzanSDK.userLogout(SettingActivity.this);
                                SharedPrefsUtil.putValue(SettingActivity.this,
                                        com.unbounded.video.constants.Constants.IS_LOGIN,false);
                                //登出
                                MobclickAgent.onProfileSignOff();
                            }else{
                                ToastUtil.showToast(SettingActivity.this, com.unbounded.video.R.string.Word_layoutfail, ToastUtil.CENTER);
                                progressDid1dismiss();
                            }
                        } catch (Exception e) {
                        }
                    }
                    break;
            }
        }
    };
    private RelativeLayout setPlayer;
    private TextView contentPlayer;

    @Override
    protected int getContentView() {
        return com.unbounded.video.R.layout.activity_setting;
    }

    @Override
    public void init(Intent intent) {
        super.init(intent);

        setTitleName(getString(com.unbounded.video.R.string.Word_Set));
        userId = SharedPrefsUtil.getValue(getApplicationContext(), com.unbounded.video.constants.Constants.UserId_FLAG, "");
        rxPermissions = new RxPermissions(this);

        feedbackImg();
    }

    /**
     * t添加意见反馈默认图片
     */
    private void feedbackImg() {
        File file = new File(FeedBackActivity.defaultpath);
        if (!(file != null && file.exists())) {
            Glide.with(getApplicationContext()).load(com.unbounded.video.R.mipmap.post_pitcture).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    FileUtil.saveFile(SettingActivity.this, "defaultfeedbackimg.jpg", resource);
                }
            });
        }
    }

    @Override
    public void initView() {
        super.initView();

        clearcacherela = (RelativeLayout) findViewById(com.unbounded.video.R.id.set_cacherela);
        introducerela = (RelativeLayout) findViewById(com.unbounded.video.R.id.set_introductionrela);
        feedbackrela = (RelativeLayout) findViewById(com.unbounded.video.R.id.set_feedbackrela);
        layouttv = (TextView) findViewById(com.unbounded.video.R.id.set_layouttv);

        activerela = (RelativeLayout) findViewById(com.unbounded.video.R.id.set_activerela);
        eyeballrela = (RelativeLayout) findViewById(com.unbounded.video.R.id.set_eyeballrela);
        saferela = (RelativeLayout) findViewById(com.unbounded.video.R.id.set_saferela);
        copyrightrela = (RelativeLayout) findViewById(com.unbounded.video.R.id.set_banquanrela);
        setPlayer = (RelativeLayout) findViewById(R.id.set_player);
        cachetv = (TextView) findViewById(com.unbounded.video.R.id.set_cachetv);
        contentPlayer = (TextView) findViewById(R.id.content_player);
        boolean isMopic = SharedPrefsUtil.getValue(SettingActivity.this, com.unbounded.video.constants.Constants.Mopic,true);
        if(isMopic){
            contentPlayer.setText("OLED版播放器");
        }else{
            contentPlayer.setText("LCD版播放器");
        }


        if (!(TextUtils.isEmpty(userId))) {
            layouttv.setVisibility(View.VISIBLE);
        } else {
            layouttv.setVisibility(View.GONE);
        }

    }

    @Override
    public void initEvent() {
        super.initEvent();

        try {
            cachetv.setText(DataCleanManager.getTotalCacheSize(SettingActivity.this));
        } catch (Exception e) {
            e.printStackTrace();
        }
        clearcacherela.setOnClickListener(this);
        introducerela.setOnClickListener(this);
        feedbackrela.setOnClickListener(this);
        activerela.setOnClickListener(this);
        eyeballrela.setOnClickListener(this);
        layouttv.setOnClickListener(this);
        saferela.setOnClickListener(this);
        copyrightrela.setOnClickListener(this);
        setPlayer.setOnClickListener(this);
    }

    private void showProgressDialog() {
        m_pDialog = new ProgressDialog(this);
        //设置进度条风格，风格为圆形，旋转的
        m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        m_pDialog.setTitle("温馨提示");
        m_pDialog.setMessage("正在加载服务器数据，请稍候");
        // 是否可以返回取消
        m_pDialog.setCancelable(false);
        m_pDialog.setProgress(0);
        //进度条增加
        m_pDialog.incrementProgressBy(1);
        //进度条减少
        m_pDialog.incrementProgressBy(-1);
        m_pDialog.setIndeterminate(true);
        m_pDialog.show();
    }

    private void cancleProgressDialog() {
        if (m_pDialog != null) {
            m_pDialog.dismiss();
        }
    }

    /**
     * 注销成功
     */
    private void layoutSuccess() {
        ToastUtil.showToast(SettingActivity.this, com.unbounded.video.R.string.Word_layoutsuccess, ToastUtil.CENTER);
        layouttv.setVisibility(View.GONE);
        progressDid1dismiss();

        //注销成功，把信息都要清空
        SharedPrefsUtil.putValue(getApplicationContext(), com.unbounded.video.constants.Constants.UserImg_FLAG, "");
        SharedPrefsUtil.putValue(getApplicationContext(), com.unbounded.video.constants.Constants.Phone_FLAG, "");
        SharedPrefsUtil.putValue(getApplicationContext(), com.unbounded.video.constants.Constants.Sex_FLAG, "");
//                    SharedPrefsUtil.putValue(getApplicationContext(), Constants.Regid_FLAG, "");
        SharedPrefsUtil.putValue(getApplicationContext(), com.unbounded.video.constants.Constants.Autograph_FLAG, "");
        SharedPrefsUtil.putValue(getApplicationContext(), com.unbounded.video.constants.Constants.UserId_FLAG, "");
        SharedPrefsUtil.putValue(getApplicationContext(), com.unbounded.video.constants.Constants.UserCase_FLAG, "");
        SharedPrefsUtil.putValue(getApplicationContext(), com.unbounded.video.constants.Constants.Userfans_FLAG, "");
        SharedPrefsUtil.putValue(getApplicationContext(), com.unbounded.video.constants.Constants.Account_FLAG, "");
        SharedPrefsUtil.putValue(getApplicationContext(), com.unbounded.video.constants.Constants.Username_FLAG, "");

        SharedPrefsUtil.putValue(getApplicationContext(), com.unbounded.video.constants.Constants.VideoInfoUpdate, 1);
        SharedPrefsUtil.putValue(getApplicationContext(), com.unbounded.video.constants.Constants.PicInfoUpdate, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + requestCode);
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
//                    showProgressDialog();
                    progressDid1();
                    //显示扫描到的内容
                    String sanString = data.getStringExtra("result").trim();
                    Log.d(TAG, "sanString=" + sanString);
                    //调用激活接口激活
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
                            Log.i("PlayActivity", "onRequset: " + returnCode);

                            /**
                             * 激活结果总次数
                             */
                            phoneNum = SharedPrefsUtil.getValue(getApplicationContext(), com.unbounded.video.constants.Constants.Phone_FLAG, com.unbounded.video.constants.Constants.Phone_default);
                            JSONObject personObject = null;
                            try {
                                personObject = new JSONObject();
                                personObject.put("phoneNum", phoneNum);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ZhugeSDK.getInstance().track(getApplicationContext(), "激活结果总次数", personObject);

                            switch (returnCode) {
                                case 1:
//                                    ToastUtil.showToast(SettingActivity.this, "激活成功,请开始眼球追踪校准",ToastUtil.CENTER);

                                    Intent intent = new Intent();
                                    intent.putExtra("activeSuccess", true);
                                    intent.setClass(SettingActivity.this, AngleActivity.class);
                                    startActivity(intent);

                                    /**
                                     * 激活成功
                                     */
                                    ZhugeSDK.getInstance().track(getApplicationContext(), "激活成功", personObject);
                                    ZhugeSDK.getInstance().track(getApplicationContext(), "进入校准界面总次数", personObject);

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
//                            cancleProgressDialog();
                            progressDid1dismiss();
                            //Toast.makeText(SettingActivity.this, "returnCode" + returnCode, Toast.LENGTH_SHORT).show();
                        }
                    }).interAction(sanString);
                }
                break;
        }
    }

    /**
     * 清除缓存
     */
    private void clearCache() {
        DataCleanManager.clearAllCache(SettingActivity.this);

        ToastUtil.showToast(SettingActivity.this, com.unbounded.video.R.string.Word_clearcacheok, ToastUtil.CENTER);
        try {
            cachetv.setText(DataCleanManager.getTotalCacheSize(SettingActivity.this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 注销方法
     */
    private void layoutMethod() {
        progressDid1();
        String url = HttpConstants.Exit_Url + "?userid=" + userId;
        HttpGetUtil get = new HttpGetUtil(handler, url, com.unbounded.video.constants.Constants.INTERNET_SUCCESS_FLAG);
        Thread thread = new Thread(get);
        thread.start();
    }

    @Override
    protected void onResume() {
        super.onResume();

        userId = SharedPrefsUtil.getValue(getApplicationContext(), com.unbounded.video.constants.Constants.UserId_FLAG, userId);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            //版权
            case com.unbounded.video.R.id.set_banquanrela:
                openActivity(CopyrightActivity.class);
                break;
            //
            //切换播放器
            case com.unbounded.video.R.id.set_player:
//去掉Mopic手机壳设置rjz添加
                CustomDialog.Builder builder = new CustomDialog.Builder(this);
                builder.setTitle("请选择正在使用的手机壳")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositionButton("OLED版播放器", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPrefsUtil.putValue(SettingActivity.this, com.unbounded.video.constants.Constants.Mopic,true);
                                ToastUtil.showToast(SettingActivity.this,"切换OLED播放器成功", Toast.LENGTH_SHORT);
//                                Intent intent = new Intent(SettingActivity.this, TuningActivity.class);
//                                intent.putExtra("fromPlayer", "fromFirst"); //"fromPlayer");
//                                startActivity(intent);
                                contentPlayer.setText("OLED版播放器");
                                dialog.dismiss();
                            }
                        })
                        .setMoYanButton("LCD版播放器", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                SharedPrefsUtil.putValue(SettingActivity.this, com.unbounded.video.constants.Constants.Mopic,false);
                                ToastUtil.showToast(SettingActivity.this,"切换LCD播放器成功", Toast.LENGTH_SHORT);
                                contentPlayer.setText("LCD版播放器");
//                                eyeBall();
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();

                break;
            //
            case com.unbounded.video.R.id.set_saferela:
                userId = SharedPrefsUtil.getValue(getApplicationContext(), com.unbounded.video.constants.Constants.UserId_FLAG, "");
                if (TextUtils.isEmpty(userId)) {
                    openActivity(LoginActivity.class);
                } else {
                    openActivity(SafeActivity.class);
                }
                break;
            //注销
            case com.unbounded.video.R.id.set_layouttv:
                layoutDia();

                break;
            //清除缓存
            case com.unbounded.video.R.id.set_cacherela:
                clearCache();

                break;
            //使用说明
            case com.unbounded.video.R.id.set_introductionrela:
                openActivity(IntroduceActivity.class);

                break;
            //意见反馈
            case com.unbounded.video.R.id.set_feedbackrela:
                openActivity(FeedBackActivity.class);
                break;

            //人眼追踪
            case com.unbounded.video.R.id.set_eyeballrela:
                eyeBall();

                break;
            //激活
            case com.unbounded.video.R.id.set_activerela:
                //点击button之后RxPermissions会为我们申请运行时权限
                isPlay3D = SharedPrefsUtil.getValue(this, com.stereo.util.Constants.PALY_3D, false);
                if (isPlay3D == true) {
                    activeedTipDia();
                } else {
                    activeMethod();
                }
//                active();

                break;
        }
    }

    /**
     * 激活
     */
    private void activeMethod() {
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
                phoneNum = SharedPrefsUtil.getValue(getApplicationContext(), com.unbounded.video.constants.Constants.Phone_FLAG, com.unbounded.video.constants.Constants.Phone_default);
                JSONObject personObject = null;
                try {
                    personObject = new JSONObject();
                    personObject.put("phoneNum", phoneNum);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ZhugeSDK.getInstance().track(getApplicationContext(), "激活结果总次数", personObject);

                switch (returnCode) {
                    case 1:
                        //ToastUtil.showToast(getApplicationContext(), "激活成功,请开始眼球追踪校准",ToastUtil.CENTER);
                        Log.i("jihuo", "jihuosuccess");
                        /**
                         * 激活成功
                         */
                        ZhugeSDK.getInstance().track(getApplicationContext(), "激活成功", personObject);
                        ZhugeSDK.getInstance().track(getApplicationContext(), "进入校准界面总次数", personObject);

                        break;
                    case 2:
                        ToastUtil.showToast(getApplicationContext(), "解密失败", ToastUtil.CENTER);
                        break;
                    case 3:
                        ToastUtil.showToast(getApplicationContext(), "网络超时", ToastUtil.CENTER);
                        break;
                    case 4:
                        ToastUtil.showToast(getApplicationContext(), "接受的参数为空", ToastUtil.CENTER);
                        break;
                    case 5:
                        ToastUtil.showToast(getApplicationContext(), "解密失败，解密后的序列号非全字符串", ToastUtil.CENTER);
                        break;
                    case 6:
                        ToastUtil.showToast(getApplicationContext(), "序列号拆分失败", ToastUtil.CENTER);
                        break;
                    case 7:
                        ToastUtil.showToast(getApplicationContext(), "根据解密后机型号没有获取到对应的机型", ToastUtil.CENTER);
                        break;
                    case 8:
                        ToastUtil.showToast(getApplicationContext(), "机型不匹配", ToastUtil.CENTER);
                        break;
                    case 9:
                        ToastUtil.showToast(getApplicationContext(), "根据拆分后的序列号没有查到对应的参数", ToastUtil.CENTER);
                        break;
                    case 10:
                        ToastUtil.showToast(getApplicationContext(), "调用加密算法失败", ToastUtil.CENTER);
                        break;
                    case 11:
                        ToastUtil.showToast(getApplicationContext(), "序列号不存在", ToastUtil.CENTER);
                        break;
                    case 12:
                        ToastUtil.showToast(getApplicationContext(), "json解析异常", ToastUtil.CENTER);
                        break;
                    case 13:
                        ToastUtil.showToast(getApplicationContext(), "暂不支持此机型", ToastUtil.CENTER);
                        break;
                }
            }
        }).Action();
    }

    /**
     * 已经激活成功提示框
     */
    private void activeedTipDia() {
        View diaView = View.inflate(SettingActivity.this, com.unbounded.video.R.layout.activetip_dialog, null);
        activeedTipDialog = new Dialog(SettingActivity.this, com.unbounded.video.R.style.dialog);
        activeedTipDialog.setContentView(diaView);
        activeedTipDialog.setCanceledOnTouchOutside(false);
        activeedTipDialog.setCancelable(false);

        Button yesbtn = (Button) activeedTipDialog.findViewById(com.unbounded.video.R.id.notice_yesbtn);
        TextView titletv = (TextView) activeedTipDialog.findViewById(com.unbounded.video.R.id.notice_tv1);
        TextView contenttv = (TextView) activeedTipDialog.findViewById(com.unbounded.video.R.id.notice_tv2);

        titletv.setVisibility(View.GONE);

        contenttv.setText(com.unbounded.video.R.string.word_activetip);

        yesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activeedTipDialog.dismiss();
            }
        });


        activeedTipDialog.show();

        WindowManager.LayoutParams params = activeedTipDialog.getWindow().getAttributes();
        params.width = screenWidth * 3 / 4;
        // params.height = 200 ;
        activeedTipDialog.getWindow().setAttributes(params);

    }


    /**
     * 注销
     */
    private void layoutDia() {
        View diaView = View.inflate(SettingActivity.this, com.unbounded.video.R.layout.notice_dialog, null);
        layoutDialog = new Dialog(SettingActivity.this, com.unbounded.video.R.style.dialog);
        layoutDialog.setContentView(diaView);
        layoutDialog.setCanceledOnTouchOutside(false);
        layoutDialog.setCancelable(false);

        Button yesbtn = (Button) layoutDialog.findViewById(com.unbounded.video.R.id.notice_yesbtn);
        Button exitbtn = (Button) layoutDialog.findViewById(com.unbounded.video.R.id.notice_canclebtn);
        TextView titletv = (TextView) layoutDialog.findViewById(com.unbounded.video.R.id.notice_tv1);
        TextView contenttv = (TextView) layoutDialog.findViewById(com.unbounded.video.R.id.notice_tv2);

        yesbtn.setText(getString(com.unbounded.video.R.string.sure_btn));
        exitbtn.setText(getString(com.unbounded.video.R.string.cancle_btn));
        titletv.setText(getString(com.unbounded.video.R.string.Word_noticetitle));
        contenttv.setText(getString(com.unbounded.video.R.string.Word_islayout));


        yesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutMethod();
                layoutDialog.dismiss();
            }
        });
        //
        exitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutDialog.dismiss();
            }
        });

        layoutDialog.show();

        WindowManager.LayoutParams params = layoutDialog.getWindow().getAttributes();
        params.width = screenWidth * 3 / 4;
        // params.height = 200 ;
        layoutDialog.getWindow().setAttributes(params);

    }

    /**
     * 激活
     */
    private void active() {
        rxPermissions.request(Manifest.permission.CAMERA)//这里填写所需要的权限
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {//true表示获取权限成功（注意这里在android6.0以下默认为true）
                            //Log.i(TAG, Manifest.permission.CAMERA + "success");
                            Intent intent = new Intent();
                            intent.setClass(SettingActivity.this, CaptureActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivityForResult(new Intent(SettingActivity.this, CaptureActivity.class), Constants.REQUESTCODE);

                            /**
                             * 设置激活
                             */
                            phoneNum = SharedPrefsUtil.getValue(getApplicationContext(), com.unbounded.video.constants.Constants.Phone_FLAG, com.unbounded.video.constants.Constants.Phone_default);
                            JSONObject personObject = null;
                            try {
                                personObject = new JSONObject();
                                personObject.put("phoneNum", phoneNum);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ZhugeSDK.getInstance().track(SettingActivity.this, "设置激活", personObject);
                        } else {
                            cameradiaShow();
                        }
                    }
                });
    }

    /**
     * 人眼追踪
     */
    private void eyeBall() {
        rxPermissions.requestEach(Manifest.permission.CAMERA).subscribe(new Action1<Permission>() {
            @Override
            public void call(Permission permission) {
                switch (permission.name) {
                    case Manifest.permission.CAMERA:
                        //有权限
                        if (permission.granted) {
                            // 有
                            /**
                             * 设置进入校准
                             */
                            phoneNum = SharedPrefsUtil.getValue(getApplicationContext(), com.unbounded.video.constants.Constants.Phone_FLAG, com.unbounded.video.constants.Constants.Phone_default);
                            JSONObject personObject = null;
                            try {
                                personObject = new JSONObject();
                                personObject.put("phoneNum", phoneNum);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ZhugeSDK.getInstance().track(getApplicationContext(), "设置进入校准界面", personObject);
                            ZhugeSDK.getInstance().track(getApplicationContext(), "进入校准界面总次数", personObject);

                            Intent intent = new Intent();
                            intent.putExtra("activeSuccess", false);
                            intent.setClass(getApplicationContext(), AngleActivity.class);
                            startActivity(intent);
                        } else {
                            //拒绝读取位置权限
                            cameradiaShow();

                        }

                        break;
                }
            }
        });
    }

    /**
     *
     */
    public void cameradiaShow() {
        if (goSetcameraDialog != null) {
            if (!(goSetcameraDialog.isShowing())) {
                goSetcameraDialog.show();
            }
        } else {
            goSetCameraDia();
        }
    }

    /**
     * 用户拒绝后，继续弹出对话框
     */
    public void goSetCameraDia() {
        View diaView = View.inflate(SettingActivity.this, com.unbounded.video.R.layout.notice_dialog, null);
        goSetcameraDialog = new Dialog(SettingActivity.this, com.unbounded.video.R.style.dialog);
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
        contenttv.setText(getString(com.unbounded.video.R.string.Word_noticecamera));


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
}
