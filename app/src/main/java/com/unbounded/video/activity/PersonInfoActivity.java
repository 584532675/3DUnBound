package com.unbounded.video.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.unbounded.video.BaseActivity;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.constants.HttpConstants;
import com.unbounded.video.utils.FileUtil;
import com.unbounded.video.utils.GetPath;
import com.unbounded.video.utils.HttpGetUtil;
import com.unbounded.video.utils.HttpPostUtil;
import com.unbounded.video.utils.SharedPrefsUtil;
import com.unbounded.video.utils.ToastUtil;
import com.unbounded.video.utils.imageLoad.GlideLogic;
import com.unbounded.video.utils.upload.OkHttp3Utils;
import com.unbounded.video.view.banner.containsEmojiEditText.ContainsEmojiEditText;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by zjf on 2017/7/19 0019.
 * 个人信息 页面
 */

public class PersonInfoActivity extends BaseActivity implements View.OnLayoutChangeListener {
    public static final int Update_UserImg= 0;
    public static final int Update_UserImgerror= 1;
    public static final int Update_UserInfo= 2;
    private ContainsEmojiEditText nicknameedt,jianjieedt;
    private RelativeLayout headviewrela,sexrela,nicknamerela,qianmrela;
    ImageView headiv;
    private TextView sextv,finishtv;
    private View sexview,cameraview;
    private PopupWindow sexpop,camerapop;
    OkHttp3Utils okHttp3Utils;

    String name,sex,jianj;

    List<String> keyList = new ArrayList<String>();
    List<String> valueList = new ArrayList<String>();

    String photoname,picPath,Regid,autograph,userSex;
    RxPermissions rxPermissions;
    Dialog goSetcameraDialog;


    RelativeLayout discussrela;
    ContainsEmojiEditText discussedt,qianmgoneedt;
    int discussedttype = -1;
    //Activity最外层的Layout视图
    private View activityRootView;

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.INTERNET_SUCCESS_FLAG:
                    String infovalue = msg.obj.toString();
                    Log.e("info","infovalue="+infovalue);

                    if(!(TextUtils.isEmpty(infovalue))){
                        if("{}".equals(infovalue)){
                            ToastUtil.showToast(PersonInfoActivity.this, com.unbounded.video.R.string.Word_loadfail, ToastUtil.CENTER);
                            progressDid1dismiss();
                        }else {
                            progressDid1dismiss();

                            try {
                                JSONObject loginjo = new JSONObject(infovalue);

                                userImg = loginjo.getString("userimg");
                                String phone = loginjo.getString("phone");
                                userSex = loginjo.getString("sex");
                                Regid = loginjo.getString("regid");
                                autograph = loginjo.getString("autograph");
                                userId = loginjo.getString("userid");
                                String userCase = loginjo.getString("userCase");
                                String userfans = loginjo.getString("userfans");
                                String account = loginjo.getString("account");
                                userName = loginjo.getString("username");

                                nicknameedt.setText(userName);
                                sextv.setText(userSex);
                                jianjieedt.setText(autograph);

                                if(headiv != null){
                                    GlideLogic.glideLoadRoundHeadPic(PersonInfoActivity.this, userImg, headiv, 50*oneDp, 50*oneDp);
                                }

                                SharedPrefsUtil.putValue(getApplicationContext(), Constants.UserImg_FLAG, userImg);
                                SharedPrefsUtil.putValue(getApplicationContext(), Constants.Phone_FLAG, phone);
                                SharedPrefsUtil.putValue(getApplicationContext(), Constants.Sex_FLAG, userSex);
                                SharedPrefsUtil.putValue(getApplicationContext(), Constants.Regid_FLAG, Regid);
                                SharedPrefsUtil.putValue(getApplicationContext(), Constants.Autograph_FLAG, autograph);
//                                SharedPrefsUtil.putValue(getApplicationContext(), Constants.UserId_FLAG, userId);
                                SharedPrefsUtil.putValue(getApplicationContext(), Constants.UserCase_FLAG, userCase);
                                SharedPrefsUtil.putValue(getApplicationContext(), Constants.Userfans_FLAG, userfans);
                                SharedPrefsUtil.putValue(getApplicationContext(), Constants.Account_FLAG, account);
                                SharedPrefsUtil.putValue(getApplicationContext(), Constants.Username_FLAG, userName);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            setDataViewVisible(true);

                        }
                    }

                    break;
                //头像成功
                case Update_UserImg:
                    progressDid1dismiss();
                    GlideLogic.glideLoadRoundHeadPic(PersonInfoActivity.this, picPath, headiv, 50*oneDp, 50*oneDp);
                    ToastUtil.showToast(PersonInfoActivity.this, com.unbounded.video.R.string.updateinfosuccess, ToastUtil.CENTER);
//                    SharedPrefsUtil.putValue(getApplicationContext(), Constants.UserImg_FLAG, picPath);
                    SharedPrefsUtil.putValue(getApplicationContext(), Constants.UserInfoUpdate, true);
                    picPath = "";

                    break;
                //头像失败
                case Update_UserImgerror:
                    progressDid1dismiss();
                    ToastUtil.showToast(PersonInfoActivity.this, com.unbounded.video.R.string.updateinfofail, ToastUtil.CENTER);
                    break;
                //个人信息
                case Update_UserInfo:
                    String updatevalue = msg.obj.toString();
                    Log.e("info","updatevalue="+updatevalue);

                    if("true".equals(updatevalue)){
                        ToastUtil.showToast(PersonInfoActivity.this, com.unbounded.video.R.string.updateinfosuccess, ToastUtil.CENTER);

                        SharedPrefsUtil.putValue(getApplicationContext(), Constants.Username_FLAG, name);
                        SharedPrefsUtil.putValue(getApplicationContext(), Constants.Sex_FLAG, sex);
                        SharedPrefsUtil.putValue(getApplicationContext(), Constants.Autograph_FLAG, jianj);
                        SharedPrefsUtil.putValue(getApplicationContext(), Constants.UserInfoUpdate, true);

                        finish();

                    }else if("false".equals(updatevalue)){
                        ToastUtil.showToast(PersonInfoActivity.this, com.unbounded.video.R.string.updateinfofail, ToastUtil.CENTER);
                    }
                    progressDid1dismiss();

                    break;

                case Constants.INTERNET_ERROR_FLAG:
                    ToastUtil.showToast(PersonInfoActivity.this, com.unbounded.video.R.string.internet_error, ToastUtil.CENTER);
                    progressDid1dismiss();
                    break;
            }
        }
    };

    @Override
    protected int getContentView() {
        return com.unbounded.video.R.layout.activity_personinfo;
    }

    @Override
    public void init(Intent intent) {
        super.init(intent);

        setRightImgVisible(true);
        setRightImgBg(com.unbounded.video.R.mipmap.finishbtn1);
        setTitleName(getString(com.unbounded.video.R.string.Word_Personinfo));
        setDataViewVisible(false);

        rxPermissions = new RxPermissions(this);
        okHttp3Utils = OkHttp3Utils.getInstance();


        initUserInfo();
    }

    @Override
    public void initView() {
        super.initView();

        initSexPop();
        initCameraPop();
        headviewrela = (RelativeLayout) findViewById(com.unbounded.video.R.id.personinfo_headrela);
        nicknamerela = (RelativeLayout) findViewById(com.unbounded.video.R.id.personinfo_nicknamerela);
        qianmrela = (RelativeLayout) findViewById(com.unbounded.video.R.id.personinfo_signaturerela);
        headiv = (ImageView) findViewById(com.unbounded.video.R.id.mine_headviewiv);
        sexrela = (RelativeLayout) findViewById(com.unbounded.video.R.id.personinfo_sexrela);
        sextv = (TextView) findViewById(com.unbounded.video.R.id.personinfo_sextv);
        nicknameedt = (ContainsEmojiEditText) findViewById(com.unbounded.video.R.id.personinfo_nicknameedt);
        jianjieedt = (ContainsEmojiEditText) findViewById(com.unbounded.video.R.id.personinfo_signatureedt);

        discussrela = (RelativeLayout) findViewById(com.unbounded.video.R.id.onlinevideoinfo_discussgonerela);
        discussedt = (ContainsEmojiEditText) findViewById(com.unbounded.video.R.id.bottompop_discussedt);
        qianmgoneedt = (ContainsEmojiEditText) findViewById(com.unbounded.video.R.id.bottompop_qianmgoneedt);
        finishtv = (TextView) findViewById(com.unbounded.video.R.id.bottompop_finishtv);
        activityRootView = findViewById(com.unbounded.video.R.id.personinfo_view);

        GlideLogic.glideLoadRoundHeadPic(PersonInfoActivity.this, userImg, headiv, 50*oneDp, 50*oneDp);
    }

    @Override
    public void initEvent() {
        super.initEvent();

        sexrela.setOnClickListener(this);
        headviewrela.setOnClickListener(this);
        nicknamerela.setOnClickListener(this);
        qianmrela.setOnClickListener(this);
        finishtv.setOnClickListener(this);
        nicknameedt.setOnClickListener(this);
        jianjieedt.setOnClickListener(this);
    }

    /**
     * 个人信息
     */
    private void initUserInfo(){
        progressDid1();
        userId = SharedPrefsUtil.getValue(getApplicationContext(), Constants.UserId_FLAG, "");
        String url = HttpConstants.UserInfo_Url + "?userid=" + userId;
        HttpGetUtil get = new HttpGetUtil(handler, url, Constants.INTERNET_SUCCESS_FLAG);
        Thread thread = new Thread(get);
        thread.start();

    }

    /**
     * 性别
     * @param
     */
    private void initSexPop(){
        sexview = this.getLayoutInflater().inflate(com.unbounded.video.R.layout.sexpop, null);
        sexpop = new PopupWindow(sexview,screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        sexpop.setFocusable(true);
        sexpop.setOutsideTouchable(true);
        sexpop.setBackgroundDrawable(new BitmapDrawable());
        sexpop.setOnDismissListener(new PopupWindow.OnDismissListener()
        {
            @Override
            public void onDismiss()
            {
                backgroundAlpha(1f);
            }

        });

        ImageView nanbtn = (ImageView) sexview.findViewById(com.unbounded.video.R.id.sexpop_naniv);
        ImageView nvbtn = (ImageView) sexview.findViewById(com.unbounded.video.R.id.sexpop_nviv);
        ImageView closeiv = (ImageView) sexview.findViewById(com.unbounded.video.R.id.sexpop_closeiv);


        nanbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sextv != null){
                    sextv.setText(com.unbounded.video.R.string.Word_nan);
                }
                sexpop.dismiss();
            }
        });
        //
        nvbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sextv != null){
                    sextv.setText(com.unbounded.video.R.string.Word_nv);
                }
                sexpop.dismiss();
            }
        });
        //
        closeiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sexpop.dismiss();
            }
        });
    }

    /**
     * 图片
     */
    private void initCameraPop(){
        cameraview = this.getLayoutInflater().inflate(com.unbounded.video.R.layout.camerapop, null);
        camerapop = new PopupWindow(cameraview,screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        camerapop.setFocusable(true);
        camerapop.setOutsideTouchable(true);
        camerapop.setBackgroundDrawable(new BitmapDrawable());
        camerapop.setOnDismissListener(new PopupWindow.OnDismissListener()
        {
            @Override
            public void onDismiss()
            {
                backgroundAlpha(1f);
            }

        });

        ImageView takephotobtn = (ImageView) cameraview.findViewById(com.unbounded.video.R.id.sexpop_takepiciv);
        ImageView pickphotobtn = (ImageView) cameraview.findViewById(com.unbounded.video.R.id.sexpop_pickphotoiv);
        ImageView closeiv = (ImageView) cameraview.findViewById(com.unbounded.video.R.id.sexpop_closeiv);

        takephotobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
                camerapop.dismiss();
            }
        });
        //
        pickphotobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickPhoto();
                camerapop.dismiss();
            }
        });
        //
        closeiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camerapop.dismiss();
            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case com.unbounded.video.R.id.personinfo_headrela:
                hideInputMode();
                setCamera();

                break;
            case com.unbounded.video.R.id.personinfo_sexrela:
                hideInputMode();
                sexpop.showAtLocation(findViewById(com.unbounded.video.R.id.personinfo_view), Gravity.BOTTOM, 0, 0);
                backgroundAlpha(0.4f);

                break;
            case com.unbounded.video.R.id.personinfo_nicknamerela:
                discussedttype = 0;
                discussedt.setText(nicknameedt.getText().toString());

                typeMethod();

                break;
            case com.unbounded.video.R.id.personinfo_nicknameedt:
                discussedttype = 0;
                discussedt.setText(nicknameedt.getText().toString());

                typeMethod();
                break;

            case com.unbounded.video.R.id.personinfo_signaturerela:
                discussedttype = 1;
                qianmgoneedt.setText(jianjieedt.getText().toString());

                qianmMethod();

                break;
            case com.unbounded.video.R.id.personinfo_signatureedt:
                discussedttype = 1;
                qianmgoneedt.setText(jianjieedt.getText().toString());

                qianmMethod();
                break;

            //完成
            case com.unbounded.video.R.id.bottompop_finishtv:
                hideInputMode();

                //昵称
                if(discussedttype == 0){
                    String discussedtStr = discussedt.getText().toString().trim();
                    if(TextUtils.isEmpty(discussedtStr)){
//                        nicknameedt.setText(userName);
                    }else{
                        nicknameedt.setText(discussedtStr);
                    }

                    //个性签名
                }else if(discussedttype == 1){
                    String qianmedtStr = qianmgoneedt.getText().toString().trim();
//                    if(TextUtils.isEmpty(qianmedtStr)){
//                        return;
//                    }
                    jianjieedt.setText(qianmedtStr);
                }

                break;
        }
    }

    /**
     * 昵称软键盘
     */
    private void typeMethod(){
        discussrela.setVisibility(View.VISIBLE);
        discussedt.setVisibility(View.VISIBLE);
        qianmgoneedt.setVisibility(View.GONE);
        discussedt.setFocusable(true);
        discussedt.setFocusableInTouchMode(true);
        discussedt.requestFocus();
        discussedt.setSelection(discussedt.getText().toString().length());

        //打开软键盘
        InputMethodManager imm1 = (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm1.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 签名软键盘
     */
    private void qianmMethod(){
        discussrela.setVisibility(View.VISIBLE);
        qianmgoneedt.setVisibility(View.VISIBLE);
        discussedt.setVisibility(View.GONE);
        qianmgoneedt.setFocusable(true);
        qianmgoneedt.setFocusableInTouchMode(true);
        qianmgoneedt.requestFocus();
        qianmgoneedt.setSelection(qianmgoneedt.getText().toString().length());

        //打开软键盘
        InputMethodManager imm1 = (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm1.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //添加layout大小发生改变监听器
        activityRootView.addOnLayoutChangeListener(PersonInfoActivity.this);
    }
    @Override
    public void onLayoutChange(View v, int left, int top, int right,
                               int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

        if(oldBottom != 0 && bottom != 0 &&(oldBottom - bottom > keyHeight)){
//            Toast.makeText(PersonInfoActivity.this, "监听到软键盘弹起...", Toast.LENGTH_SHORT).show();

        }else if(oldBottom != 0 && bottom != 0 &&(bottom - oldBottom > keyHeight)){
            discussrela.setVisibility(View.GONE);
            discussedt.setText("");
//            Toast.makeText(PersonInfoActivity.this, "监听到软件盘关闭...", Toast.LENGTH_SHORT).show();

        }
    }

    /**
     * 相机
     */
    public void setCamera(){
        rxPermissions.request(android.Manifest.permission.CAMERA)//这里填写所需要的权限
            .subscribe(new Action1<Boolean>() {
                @Override
                public void call(Boolean aBoolean) {
                    if (aBoolean) {//true表示获取权限成功（注意这里在android6.0以下默认为true）
                        camerapop.showAtLocation(findViewById(com.unbounded.video.R.id.personinfo_view), Gravity.BOTTOM, 0, 0);
                        backgroundAlpha(0.4f);

                    } else {
                        diaShow();
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
            goSetCameraDia();
        }
    }

    /**
     * 用户拒绝后，继续弹出对话框
     */
    public void goSetCameraDia(){
        View diaView = View.inflate(PersonInfoActivity.this, com.unbounded.video.R.layout.notice_dialog, null);
        goSetcameraDialog = new Dialog(PersonInfoActivity.this, com.unbounded.video.R.style.dialog);
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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SELECT_PIC_BY_PICK_PHOTO:// 直接从相册获取
                try {
                    startPhotoZoom(data.getData());

                } catch (NullPointerException e) {
                    e.printStackTrace();// 用户点击取消操作
                }
                break;
            case SELECT_PIC_BY_TACK_PHOTO:// 调用相机拍照
                String str = GetPath.getPath(PersonInfoActivity.this,photoUri);
                File file = new File(str);
                if(!(file.exists())){
                    return;
                }
                startPhotoZoom(photoUri);

                break;
            case REQUESTCODE_CUTTING:// 取得裁剪后的图片
                if (data != null) {
                    setPicToView(data);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        String str = GetPath.getPath(PersonInfoActivity.this,uri);
        photoname = str.substring(str.lastIndexOf("/") + 1);

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    /**
     * 显示裁剪后的图片
     */

    public void setPicToView(Intent data){
        Bundle extras = data.getExtras();

        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            picPath = FileUtil.saveFile(PersonInfoActivity.this, "headphoto"+photoname, photo);
            Log.e("info", "picPath="+picPath);
            upLoadHead();
//            GlideLogic.glideLoadHeadPic(PersonInfoActivity.this, picPath, headiv, 64*oneDp, 64*oneDp);
        }
    }

    /**
     * 上传头像
     */
    private void upLoadHead(){
        OkHttp3Utils.upUserInfo(handler, picPath, userId);
        progressDid1();
    }

    /**
     * 点击保存按钮
     */
    @Override
    public void onRightClick(View v) {
        super.onRightClick(v);

        name = nicknameedt.getText().toString().trim();
        sex = sextv.getText().toString().trim();
        jianj = jianjieedt.getText().toString().trim();

        if(name.equals(userName) && sex.equals(userSex) && jianj.equals(autograph)){
            finish();
        }else {
            progressDid1();
            upLoadUserInfo(name,sex,jianj);
        }
    }

    /**
     * 上传基本信息
     */
    private void upLoadUserInfo(String name, String sex, String jianj){
        keyList.clear();
        valueList.clear();

        keyList.add("userid");
        keyList.add("sex");
        keyList.add("username");
        keyList.add("autograph");

        valueList.add(userId);
        valueList.add(sex);
        valueList.add(name);
        valueList.add(jianj);

        String infourl = HttpConstants.UpdateUserInfo_Url;
        HttpPostUtil post = new HttpPostUtil(handler, infourl, keyList, valueList, Update_UserInfo);
        Thread thread = new Thread(post);
        thread.start();


    }
}
