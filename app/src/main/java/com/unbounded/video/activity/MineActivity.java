package com.unbounded.video.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mopic3d.mplayer3d.tune.TuningActivity;
import com.stereo.Holography.Holography;
import com.stereo.Interaction.InteractionManager;
import com.stereo.angle.AngleActivity;
import com.stereo.util.ActionCallback;
import com.tbruyelle.rxpermissions.Permission;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.tencent.connect.UserInfo;
import com.unbounded.video.BaseActivity;
import com.unbounded.video.R;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.constants.HttpConstants;
import com.unbounded.video.utils.ExitApplication;
import com.unbounded.video.utils.FileDownloaderManager;
import com.unbounded.video.utils.HttpGetUtil;
import com.unbounded.video.utils.JsonUtil;
import com.unbounded.video.utils.SharedPrefsUtil;
import com.unbounded.video.utils.ToastUtil;
import com.unbounded.video.utils.imageLoad.GlideLogic;
import com.zhuge.analysis.stat.ZhugeSDK;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.onekeyshare.OnekeyShare;
import rx.functions.Action1;

/**
 * Created by zjf on 2017/7/17.
 * 4.我的界面
 *  无fragment直接编写
 */

public class MineActivity extends BaseActivity {
    static final int MYMSG_Flag = 0;
    static final int CALIBRATION_VIDEO=1;
    private RelativeLayout headrela,addpartrela,msgnumrela;
    private LinearLayout jiaozhunrela,mineDownloadLl,mineAboususLl,mymsgrela,collectLl;
    ImageView setrela;
    private ImageView collectiv,a/*ttentioniv,*/,historyiv,downloadiv;
  /*  TextView *//*personnametv,*//**//*historytv,*//**//*downloadtv*//*,msgnumtv;*/
//    private LinearLayout centerlinear;
    private ImageView setbtn;
    private ImageView headviewiv;
    private RxPermissions rxPermissions;
    private TextView personnametv;
    private static final String TAG = "info";
    private UserInfo mUserInfo;
    private long LastClickTime = 0;
    Dialog goSetcameraDialog;

    List<String> onlineids = new ArrayList<String>();
    String haveReadedjson,phoneNum;
    List<String> readedIds = new ArrayList<String>();
    int currentPage = 1,sumPage;

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case com.unbounded.video.constants.Constants.INTERNET_SUCCESS_FLAG:
                    String infovalue = msg.obj.toString();
//                    Log.e("info", "infovalue=" + infovalue);

                    if (!(TextUtils.isEmpty(infovalue))) {
                        if ("{}".equals(infovalue)) {
                            ToastUtil.showToast(MineActivity.this, com.unbounded.video.R.string.Word_loadfail, ToastUtil.CENTER);
                            progressDid1dismiss();
                        } else {
                            progressDid1dismiss();

                            try {
                                JSONObject loginjo = new JSONObject(infovalue);

                                userImg = loginjo.getString("userimg");
                                String phone = loginjo.getString("phone");
                                userId = loginjo.getString("userid");
                                String userCase = loginjo.getString("userCase");
                                String userfans = loginjo.getString("userfans");
                                String account = loginjo.getString("account");
                                userName = loginjo.getString("username");

                                SharedPrefsUtil.putValue(getApplicationContext(), com.unbounded.video.constants.Constants.UserImg_FLAG, userImg);
                                SharedPrefsUtil.putValue(getApplicationContext(), com.unbounded.video.constants.Constants.Phone_FLAG, phone);
//                                SharedPrefsUtil.putValue(getApplicationContext(), Constants.UserId_FLAG, userId);
                                SharedPrefsUtil.putValue(getApplicationContext(), com.unbounded.video.constants.Constants.UserCase_FLAG, userCase);
                                SharedPrefsUtil.putValue(getApplicationContext(), com.unbounded.video.constants.Constants.Userfans_FLAG, userfans);
                                SharedPrefsUtil.putValue(getApplicationContext(), com.unbounded.video.constants.Constants.Account_FLAG, account);
                                SharedPrefsUtil.putValue(getApplicationContext(), com.unbounded.video.constants.Constants.Username_FLAG, userName);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            personnametv.setText(userName);
                            //
                            GlideLogic.glideLoadHeadPic(MineActivity.this, userImg, headviewiv, 64*oneDp, 64*oneDp);

                        }
                    }

                    break;
                case MYMSG_Flag:
                    String mymsgvalue = msg.obj.toString();

                    if(!(TextUtils.isEmpty(mymsgvalue))){
                        try {
                            JSONObject mymsgjo = new JSONObject(mymsgvalue);
                            JSONObject page = mymsgjo.getJSONObject("page");
                            sumPage = Integer.valueOf(page.getString("sumPage"));
                            JSONArray data = mymsgjo.getJSONArray("data");
                            if(data != null && data.length() > 0){
                                for(int i=0;i<data.length();i++){
                                    JSONObject jo = data.getJSONObject(i);

                                    String id = jo.getString("id");

                                    if(!(readedIds.contains(id))){
                                        onlineids.add(id);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(currentPage < sumPage){
                            currentPage = currentPage + 1;
                            initMsgList();
                        }else{
                            Log.e("info","onlineids.size()="+onlineids.size());

                            if(onlineids.size() > 0){
//                                msgnumrela.setVisibility(View.VISIBLE);
//                                msgnumtv.setText(onlineids.size()+"");
                            }else{
//                                msgnumrela.setVisibility(View.GONE);
                            }
                        }

                    }

                    break;
                case CALIBRATION_VIDEO:
                    String calibrationData = msg.obj.toString();

                    if(!(TextUtils.isEmpty(calibrationData))){
                        try {
                            JSONObject mymsgjo = new JSONObject(calibrationData);
                            String url = mymsgjo.getString("uploadUrl");
                            Log.v("rjz url","url:"+url);
                            if(!url.isEmpty()){
                                Intent intent =new Intent(MineActivity.this,MoviePlayActivity.class);
                                intent.putExtra("urlOrPath",url);
                                intent.putExtra("isCalibration",true);
                                intent.putExtra("playName", "校准视屏");
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    break;

            }
        }
    };
    private LinearLayout jiaozhunrelaVideo;
    private LinearLayout mineShareLl;
    private LinearLayout mineHistoryLl;
    private LinearLayout feedbackrela;
    private LinearLayout mineSharerela;

    @Override
    protected int getContentView() {
        return com.unbounded.video.R.layout.activity_mine;
    }

    @Override
    public void init(Intent intent) {
        super.init(intent);

        setTitleViewVisible(false);
        rxPermissions = new RxPermissions(this);

    }
    /**
     * 个人信息
     */
    private void initUserInfo(){
        progressDid1();
        String url = HttpConstants.UserInfo_Url + "?userid=" + userId;
        HttpGetUtil get = new HttpGetUtil(handler, url, com.unbounded.video.constants.Constants.INTERNET_SUCCESS_FLAG);
        Thread thread = new Thread(get);
        thread.start();

    }

    @Override
    public void initView() {
        super.initView();
        setbtn = (ImageView) findViewById(com.unbounded.video.R.id.mine_setbtn);
        headviewiv = (ImageView) findViewById(com.unbounded.video.R.id.mine_headviewiv);
//        headrela = (RelativeLayout) findViewById(com.unbounded.video.R.id.mine_headrela);
        mymsgrela = (LinearLayout) findViewById(com.unbounded.video.R.id.mine_mymsgrela);
//        addpartrela = (RelativeLayout) findViewById(com.unbounded.video.R.id.mine_loginpartrela);
//        centerlinear = (LinearLayout) findViewById(com.unbounded.video.R.id.mine_centerlinear);
//        attentioniv = (ImageView) findViewById(com.unbounded.video.R.id.mine_attentioniv);
        historyiv = (ImageView) findViewById(com.unbounded.video.R.id.mine_historyiv);
        mineHistoryLl = (LinearLayout) findViewById(R.id.mine_history_ll);

//        historytv = (TextView) findViewById(com.unbounded.video.R.id.mine_historytv);
        mineDownloadLl = (LinearLayout) findViewById(com.unbounded.video.R.id.mine_download_ll);
        collectLl = (LinearLayout) findViewById(com.unbounded.video.R.id.mine_collect_ll);

//        attentiontv = (TextView) findViewById(com.unbounded.video.R.id.mine_attentiontv);
//        downloadtv = (TextView) findViewById(com.unbounded.video.R.id.mine_downloadtv);
        personnametv = (TextView) findViewById(com.unbounded.video.R.id.mine_personnametv);
//        msgnumtv = (TextView) findViewById(com.unbounded.video.R.id.mine_mymsgnumtv);
//        nulltv1 = (TextView) findViewById(com.unbounded.video.R.id.mine_nulltv1);
//        msgnumrela = (RelativeLayout) findViewById(com.unbounded.video.R.id.mine_mymsgnumrela);
        jiaozhunrela = (LinearLayout) findViewById(com.unbounded.video.R.id.mine_localrela);
        feedbackrela = (LinearLayout) findViewById(R.id.mine_feedbackrela);
        mineAboususLl = (LinearLayout) findViewById(com.unbounded.video.R.id.mine_abousus_ll);
        mineSharerela = (LinearLayout) findViewById(R.id.mine_share_ll);

        setrela = (ImageView) findViewById(com.unbounded.video.R.id.mine_setbtn);
        jiaozhunrelaVideo = (LinearLayout) findViewById(com.unbounded.video.R.id.mine_localrela_video);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            ((RelativeLayout)findViewById(R.id.activity_mine)).setPadding(0, SharedPrefsUtil.getStatusHeight(this), 0,0);
//        }
    }

    @Override
    public void initParams() {
        super.initParams();
//        ViewGroup.LayoutParams headrelaparams = (ViewGroup.LayoutParams) headrela.getLayoutParams();
//        headrelaparams.height = screenHeight * 415/ 1920;
//        headrela.setLayoutParams(headrelaparams);

//        ViewGroup.LayoutParams centerlinearparams = (ViewGroup.LayoutParams) centerlinear.getLayoutParams();
//        centerlinearparams.height = screenHeight * 107/ 960;
//        centerlinear.setLayoutParams(centerlinearparams);

//        ViewGroup.LayoutParams nulltv1params = (ViewGroup.LayoutParams) nulltv1.getLayoutParams();
//        nulltv1params.width = screenWidth * 64/ 1080;
//        nulltv1.setLayoutParams(nulltv1params);

    }

    @Override
    public void initEvent() {
        super.initEvent();

        setbtn.setOnClickListener(this);
        headviewiv.setOnClickListener(this);
//        attentioniv.setOnClickListener(this);
//        downloadiv.setOnClickListener(this);
        personnametv.setOnClickListener(this);
        setrela.setOnClickListener(this);
//        addpartrela.setOnClickListener(this);
        mymsgrela.setOnClickListener(this);
//        collecttv.setOnClickListener(this);
//        downloadtv.setOnClickListener(this);
//        attentiontv.setOnClickListener(this);
        jiaozhunrela.setOnClickListener(this);
        jiaozhunrelaVideo.setOnClickListener(this);
        mineDownloadLl.setOnClickListener(this);
        collectLl.setOnClickListener(this);
        feedbackrela.setOnClickListener(this);
        mineAboususLl.setOnClickListener(this);
        historyiv.setOnClickListener(this);
        mineHistoryLl.setOnClickListener(this);
//        historytv.setOnClickListener(this);
        mineSharerela.setOnClickListener(this);
        if(com.stereo.util.FileUtil.ischeckOK("sdcard/HolographyProfile.txt")){


        }else {
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
        }

//        UpdateData();
    }

    private void UpdateData() {
        Log.i(TAG, "UpdateData: ");
        WindowManager wm = (WindowManager)
                getSystemService(Context.WINDOW_SERVICE);
        screenWidth= wm.getDefaultDisplay().getWidth();
        screenHeight = wm.getDefaultDisplay().getHeight();
        String phoneModel = android.os.Build.MODEL;
        Log.i(TAG, "phoneModel " + phoneModel);
        if (("MHA-AL00").equals(phoneModel)) {
            screenHeight=1920;
        }if (("GM2017M27A").equals(phoneModel)) {
            screenHeight=2160;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                Holography.HolographyInit(screenWidth, screenHeight);
                Holography.sendDelt(0);
                Log.i(TAG, "HolographyInit  :");
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean userInfoUpdate = SharedPrefsUtil.getValue(getApplicationContext(), com.unbounded.video.constants.Constants.UserInfoUpdate, false);
        userId = SharedPrefsUtil.getValue(getApplicationContext(), com.unbounded.video.constants.Constants.UserId_FLAG, "");
        userImg = SharedPrefsUtil.getValue(getApplicationContext(), com.unbounded.video.constants.Constants.UserImg_FLAG, "");
        userName = SharedPrefsUtil.getValue(getApplicationContext(), com.unbounded.video.constants.Constants.Username_FLAG, "");

        if(TextUtils.isEmpty(userId)){
//            msgnumrela.setVisibility(View.GONE);

        }else{
            if(onlineids != null){
                onlineids.clear();
            }
            if(readedIds != null){
                readedIds.clear();
            }
            haveReadedjson = SharedPrefsUtil.getValue(getApplicationContext(), com.unbounded.video.constants.Constants.HaveReaded_Flag, "");
            if(!(TextUtils.isEmpty(haveReadedjson))){
                readedIds = JsonUtil.msgIdsJsonToList(haveReadedjson);
            }
            currentPage = 1;
            initMsgList();
        }

        if(userInfoUpdate == false){
            if(TextUtils.isEmpty(userId)){
                personnametv.setText(com.unbounded.video.R.string.clicklogin);
                headviewiv.setImageResource(R.mipmap.login_me);
            }else{
                personnametv.setText(userName);
                GlideLogic.glideLoadHeadPic(MineActivity.this, userImg, headviewiv, 64*oneDp, 64*oneDp);
            }

            return;
        }


        if(TextUtils.isEmpty(userId)){
            personnametv.setText(com.unbounded.video.R.string.clicklogin);
            headviewiv.setImageResource(com.unbounded.video.R.mipmap.login_me);
        }else{
            Log.e("info","刷新");
            initUserInfo();
            SharedPrefsUtil.putValue(getApplicationContext(), com.unbounded.video.constants.Constants.UserInfoUpdate, false);

        }
    }

    /**
     *
     */
    private void initMsgList(){
        String url = HttpConstants.PushMsg_Url + "?currentPage=" + currentPage + "&userId=" + userId;
        HttpGetUtil get = new HttpGetUtil(handler, url, MYMSG_Flag);
        Thread thread = new Thread(get);
        thread.start();
    }

    /**
     * 人眼追踪
     */
    private void eyeBall(){
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
                            ZhugeSDK.getInstance().track(getApplicationContext(),"设置进入校准界面",personObject);
                            ZhugeSDK.getInstance().track(getApplicationContext(),"进入校准界面总次数",personObject);

                            Intent intent = new Intent();
                            intent.putExtra("activeSuccess",false);
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
     * 用户拒绝后，继续弹出对话框
     */
    public void goSetCameraDia(){
        View diaView = View.inflate(MineActivity.this, com.unbounded.video.R.layout.notice_dialog, null);
        goSetcameraDialog = new Dialog(MineActivity.this, com.unbounded.video.R.style.dialog);
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
    /**
     * 一键分享方法
     */
    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        String url = "http://a.eqxiu.com/s/LAW2acKt?eqrcode=1&share_level=1&from_user=86614aa9-4064-447a-bdea-84deaa67d527&from_id=1484f24f-19a6-435e-a96d-f27e26f6e911&share_time=1508922719615";
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle("3D新视界");
        // titleUrl是标题的网络链接，QQ和QQ空间等使用
//        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("3D新视界");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(url);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("3D新视界");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(com.unbounded.video.R.string.app_name));
        oks.setImageUrl(url);
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(url);

        // 启动分享GUI
        oks.show(this);
    }
    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            //校准视频
            case com.unbounded.video.R.id.mine_localrela_video:
                String url = HttpConstants.Calibration_VideosUrl +"?comeFrom=en";
                HttpGetUtil get = new HttpGetUtil(handler, url, CALIBRATION_VIDEO);
                Thread thread = new Thread(get);
                thread.start();
                break;
            //校准
            case com.unbounded.video.R.id.mine_localrela:
                //去掉Mopic手机壳设置rjz添加
                boolean isMppic = SharedPrefsUtil.getValue(MineActivity.this, Constants.Mopic, true);

                if (isMppic) {
                    Intent intent = new Intent(MineActivity.this, TuningActivity.class);
                    intent.putExtra("fromPlayer", "fromFirst"); //"fromPlayer");
                    startActivity(intent);
                } else {
                    eyeBall();
                }
//                CustomDialog.Builder builder = new CustomDialog.Builder(this);
//                builder.setTitle("请选择正在使用的手机壳")
//                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        })
//                        .setPositionButton("Mopic手机壳", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                SharedPrefsUtil.putValue(MineActivity.this, Constants.Mopic,true);
//                                ToastUtil.showToast(MineActivity.this,"设置Mopic手机壳成功",Toast.LENGTH_SHORT);
//                                Intent intent = new Intent(MineActivity.this, TuningActivity.class);
//                                intent.putExtra("fromPlayer", "fromFirst"); //"fromPlayer");
//                                startActivity(intent);
//                                dialog.dismiss();
//                            }
//                        })
//                        .setMoYanButton("魔眼手机壳", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                SharedPrefsUtil.putValue(MineActivity.this, Constants.Mopic,false);
//                                eyeBall();
//                                dialog.dismiss();
//                            }
//                        })
//                        .create()
//                        .show();
//                eyeBall();

                break;
//            //我的上传
//            case com.unbounded.video.R.id.mine_uploadrela:
//                userId = SharedPrefsUtil.getValue(getApplicationContext(), com.unbounded.video.constants.Constants.UserId_FLAG, "");
//                if(TextUtils.isEmpty(userId)){
//                    openActivity(LoginActivity.class);
//                }else{
//                    openActivity(MyUpLoadActivity.class);
//                }
//
//                break;

            //我的消息
            case com.unbounded.video.R.id.mine_mymsgrela:
                userId = SharedPrefsUtil.getValue(getApplicationContext(), com.unbounded.video.constants.Constants.UserId_FLAG, "");
                if(TextUtils.isEmpty(userId)){
                    openActivity(LoginActivity.class);
                }else{
                    openActivity(MessageCenterActivity.class);
                }

                break;
            //设置
            case com.unbounded.video.R.id.mine_setbtn:
                openActivity(SettingActivity.class);

                break;
            //点击登录
            case com.unbounded.video.R.id.mine_headviewiv:
                userId = SharedPrefsUtil.getValue(getApplicationContext(), com.unbounded.video.constants.Constants.UserId_FLAG, "");
                if(TextUtils.isEmpty(userId)){
                    openActivityFromBottom(LoginActivity.class);
                }else{
                    openActivity(PersonInfoActivity.class);
                }

                break;
            case com.unbounded.video.R.id.mine_personnametv:
                userId = SharedPrefsUtil.getValue(getApplicationContext(), com.unbounded.video.constants.Constants.UserId_FLAG, "");
                if(TextUtils.isEmpty(userId)){
                    openActivityFromBottom(LoginActivity.class);
                }
//
                break;
            //收藏列表
            case R.id.mine_collect_ll:
                collectbtnMethod();

                break;
//            case com.unbounded.video.R.id.mine_collecttv:
//                collectbtnMethod();
//                break;
            //关注列表
//            case com.unbounded.video.R.id.mine_attentioniv:
//                attentionbtnMethod();
//                break;
//            case com.unbounded.video.R.id.mine_attentiontv:
//                attentionbtnMethod();
//                break;
            //历史记录
            case com.unbounded.video.R.id.mine_history_ll:
                openActivity(PlayerHistoryActivity.class);
                break;
//            case com.unbounded.video.R.id.mine_historytv:
//                openActivity(PlayerHistoryActivity.class);
//                break;
            //下载列表
            case com.unbounded.video.R.id.mine_download_ll:
                downloadbtnMethod();

                break;
//            case com.unbounded.video.R.id.mine_downloadtv:
//                downloadbtnMethod();
//                break;

//            //加入组织
//            case com.unbounded.video.R.id.mine_loginpartrela:
//                openActivity(AddPartActivity.class);
//                break;
            //意见反馈
            case com.unbounded.video.R.id.mine_feedbackrela:
                openActivity(FeedBackActivity.class);
                break;
            //关于我们
            case com.unbounded.video.R.id.mine_abousus_ll:
                openActivity(AboutUsActivity.class);
                break;
            case com.unbounded.video.R.id.mine_share_ll:
                showShare();
                break;
        }
    }
    /**
     * 通过包名 在应用商店打开应用
     *
     * @param packageName 包名
     */
    private void openApplicationMarket(String packageName) {
        try {
            String str = "market://details?id=" + packageName;
            Intent localIntent = new Intent(Intent.ACTION_VIEW);
            localIntent.setData(Uri.parse(str));
            startActivity(localIntent);
        } catch (Exception e) {
            // 打开应用商店失败 可能是没有手机没有安装应用市场
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "打开应用商店失败", Toast.LENGTH_SHORT).show();
            // 调用系统浏览器进入商城
            String url = "http://app.mi.com/detail/163525?ref=search";
            openLinkBySystem(url);
        }
    }

    /**
     * 调用系统浏览器打开网页
     *
     * @param url 地址
     */
    private void openLinkBySystem(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
    /**
     * 下载按钮
     */
    private void downloadbtnMethod(){
        userId = SharedPrefsUtil.getValue(getApplicationContext(), com.unbounded.video.constants.Constants.UserId_FLAG, "");
        if(TextUtils.isEmpty(userId)){
            openActivity(LoginActivity.class);
        }else {
            openActivity(DownloadListActivity.class);
        }
    }

    /**
     * 关注按钮
     */
    private void attentionbtnMethod(){
        userId = SharedPrefsUtil.getValue(getApplicationContext(), com.unbounded.video.constants.Constants.UserId_FLAG, "");
        if(TextUtils.isEmpty(userId)){
            openActivity(LoginActivity.class);
        }else {
            openActivity(AttentionActivity.class);
        }
    }

    /**
     * 收藏按钮方法
     */
    private void collectbtnMethod(){
        userId = SharedPrefsUtil.getValue(getApplicationContext(), com.unbounded.video.constants.Constants.UserId_FLAG, "");
        if(TextUtils.isEmpty(userId)){
            openActivity(LoginActivity.class);
        }else{
            openActivity(CollectActivity.class);
        }
    }


    @Override
    public void onBackPressed()
    {
        if (LastClickTime <= 0)
        {
            ToastUtil.showToast(MineActivity.this, com.unbounded.video.R.string.onemoreexit, ToastUtil.BOTTOM);
            LastClickTime = System.currentTimeMillis();

        }
        else
        {
            long currentClickTime = System.currentTimeMillis();
            if ((currentClickTime - LastClickTime) < 3000)
            {
                //暂停所有下载
                FileDownloaderManager.getInstance().pauseDownLoadAllFile();
                ExitApplication.getInstance().exit(this);

            }
            else
            {
                ToastUtil.showToast(MineActivity.this, com.unbounded.video.R.string.onemoreexit, ToastUtil.BOTTOM);
                LastClickTime = currentClickTime;
            }
        }
    }
}
