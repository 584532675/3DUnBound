package com.unbounded.video.activity;

import android.app.Dialog;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.unbounded.video.BaseActivity;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.constants.HttpConstants;
import com.unbounded.video.utils.ExitApplication;
import com.unbounded.video.utils.FileUtil;
import com.unbounded.video.utils.GetPath;
import com.unbounded.video.utils.HttpPostUtil;
import com.unbounded.video.utils.SharedPrefsUtil;
import com.unbounded.video.utils.ToastUtil;
import com.unbounded.video.utils.imageLoad.GlideLogic;
import com.unbounded.video.utils.upload.OkHttp3Utils;
import com.unbounded.video.view.banner.containsEmojiEditText.ContainsEmojiEditText;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by zjf on 2017/11/23 0023.
 */

public class Register2Activity extends BaseActivity {
    public static final int Update_UserImg= 0;
    public static final int Update_UserImgerror= 1;
    public static final int Update_UserInfo= 2;
    ContainsEmojiEditText nicknameedt;
    Button finishbtn;
    ImageView headviewiv;
    String nickname,sex,jianj,photoname,picPath;
    RxPermissions rxPermissions;
    OkHttp3Utils okHttp3Utils;
    PopupWindow camerapop;
    Dialog goSetcameraDialog;
    View cameraview;

    List<String> keyList = new ArrayList<String>();
    List<String> valueList = new ArrayList<String>();

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Update_UserInfo:
                    String updatevalue = msg.obj.toString();
                    Log.e("info","updatevalue="+updatevalue);

                    if("true".equals(updatevalue)){
//                        ToastUtil.showToast(Register2Activity.this, R.string.updateinfosuccess, ToastUtil.CENTER);

                        SharedPrefsUtil.putValue(getApplicationContext(), Constants.Username_FLAG, nickname);
                        SharedPrefsUtil.putValue(getApplicationContext(), Constants.Sex_FLAG, sex);
                        SharedPrefsUtil.putValue(getApplicationContext(), Constants.Autograph_FLAG, jianj);
                        SharedPrefsUtil.putValue(getApplicationContext(), Constants.UserInfoUpdate, true);

//                        finish();
                        ExitApplication.getInstance().exitLoginActivitys();

                    }else if("false".equals(updatevalue)){
                        ToastUtil.showToast(Register2Activity.this, com.unbounded.video.R.string.updateinfofail, ToastUtil.CENTER);
                    }
                    progressDiddismiss();

                    break;

                //头像成功
                case Update_UserImg:
                    progressDiddismiss();
//                    GlideLogic.glideLoadRoundHeadPic(Register2Activity.this, picPath, headviewiv, 50*oneDp, 50*oneDp);
                    GlideLogic.glideLoadHeadPic(Register2Activity.this, picPath, headviewiv, 50*oneDp, 50*oneDp);
//                    ToastUtil.showToast(Register2Activity.this, R.string.updateinfosuccess, ToastUtil.CENTER);
//                    SharedPrefsUtil.putValue(getApplicationContext(), Constants.UserImg_FLAG, picPath);
                    SharedPrefsUtil.putValue(getApplicationContext(), Constants.UserInfoUpdate, true);
                    picPath = "";

                    break;
                //头像失败
                case Update_UserImgerror:
                    progressDiddismiss();
                    ToastUtil.showToast(Register2Activity.this, com.unbounded.video.R.string.updateinfofail, ToastUtil.CENTER);
                    break;
                case Constants.INTERNET_ERROR_FLAG:
                    ToastUtil.showToast(Register2Activity.this, com.unbounded.video.R.string.internet_error, ToastUtil.CENTER);
                    progressDiddismiss();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        whiteBar();
    }

    @Override
    protected int getContentView() {
        return com.unbounded.video.R.layout.activity_register2;
    }

    @Override
    public void init(Intent intent) {
        super.init(intent);

        setTitleViewVisible(true);
        setTitleBg(com.unbounded.video.R.color.white);
        setLeftBtnBg(com.unbounded.video.R.mipmap.logincloseiv);

        ExitApplication.getInstance().addLoginActivity(this);
        userId = SharedPrefsUtil.getValue(getApplicationContext(), Constants.UserId_FLAG, "");

        rxPermissions = new RxPermissions(this);
        okHttp3Utils = OkHttp3Utils.getInstance();

    }

    @Override
    public void initView() {
        super.initView();
        initCameraPop();

        nicknameedt = (ContainsEmojiEditText) findViewById(com.unbounded.video.R.id.register2_nicknameedt);
        finishbtn = (Button) findViewById(com.unbounded.video.R.id.register2_finishbtn);
        headviewiv = (ImageView) findViewById(com.unbounded.video.R.id.register2_headviewiv);
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
    public void initEvent() {
        super.initEvent();

        finishbtn.setOnClickListener(this);
        headviewiv.setOnClickListener(this);
    }

    @Override
    public void onLeftClick(View v) {
        super.onLeftClick(v);

        ExitApplication.getInstance().exitLoginActivitys();
    }

    /**
     * 上传基本信息
     */
    private void upLoadUserInfo(String namestr, String sexstr, String jianjstr){
        keyList.clear();
        valueList.clear();

        keyList.add("userid");
        keyList.add("sex");
        keyList.add("username");
        keyList.add("autograph");

        valueList.add(userId);
        valueList.add(sexstr);
        valueList.add(namestr);
        valueList.add(jianjstr);

        String infourl = HttpConstants.UpdateUserInfo_Url;
        HttpPostUtil post = new HttpPostUtil(handler, infourl, keyList, valueList, Update_UserInfo);
        Thread thread = new Thread(post);
        thread.start();


    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case com.unbounded.video.R.id.register2_finishbtn:
                nickname = nicknameedt.getText().toString().trim();
                sex = SharedPrefsUtil.getValue(getApplicationContext(), Constants.Sex_FLAG, "");
                jianj = SharedPrefsUtil.getValue(getApplicationContext(), Constants.Autograph_FLAG, "");

                if(TextUtils.isEmpty(nickname)){
                    ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.word_register2nickname,ToastUtil.CENTER);

                }else{
                    progressDid();
                    upLoadUserInfo(nickname,sex,jianj);
                }

                break;
            case com.unbounded.video.R.id.register2_headviewiv:
                hideInputMode();
                setCamera();

                break;
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
                            camerapop.showAtLocation(findViewById(com.unbounded.video.R.id.activity_register2), Gravity.BOTTOM, 0, 0);
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
        View diaView = View.inflate(Register2Activity.this, com.unbounded.video.R.layout.notice_dialog, null);
        goSetcameraDialog = new Dialog(Register2Activity.this, com.unbounded.video.R.style.dialog);
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
                String str = GetPath.getPath(Register2Activity.this,photoUri);
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
        String str = GetPath.getPath(Register2Activity.this,uri);
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
            picPath = FileUtil.saveFile(Register2Activity.this, "headphoto"+photoname, photo);
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
        progressDid();
    }
}
