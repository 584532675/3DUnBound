package com.unbounded.video.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.unbounded.video.BaseActivity;
import com.unbounded.video.adapter.FeedbackAdapter;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.utils.FileUtil;
import com.unbounded.video.utils.GetPath;
import com.unbounded.video.utils.ToastUtil;
import com.unbounded.video.utils.upload.OkHttp3Utils;
import com.unbounded.video.view.banner.containsEmojiEditText.ContainsEmojiEditText;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.functions.Action1;

/**
 * Created by zjf on 2017/7/22 0022.
 */

public class FeedBackActivity extends BaseActivity {
    public static String root = Environment.getExternalStorageDirectory() + "/moyan/temporary/";
    public static String defaultpath = root + "defaultfeedbackimg.jpg";
    public final static int HaveImgsuccess_FLAG = 0;
    public final static int HaveImgfail_FLAG = 1;
    ContainsEmojiEditText contentedt,contractsedt;
    GridView gv;
    Button commitbtn;
    FeedbackAdapter adapter;
    List<String> list = new ArrayList<String>();
    int gvImgWidth;
    String photoname,picPath,discuss,contracts;
    RxPermissions rxPermissions;
    Dialog goSetcameraDialog;
    OkHttp3Utils okHttp3Utils;

    PopupWindow camerapop;
    View cameraview;

    List<String> keyList = new ArrayList<String>();
    List<String> valueList = new ArrayList<String>();
    Map<String, String> params = new HashMap<String, String>();

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HaveImgsuccess_FLAG:
                    ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.uploaded, ToastUtil.CENTER);
                    progressDid1dismiss();
                    finish();
                    break;
                case HaveImgfail_FLAG:
                    ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.uploaderror, ToastUtil.CENTER);
                    progressDid1dismiss();
                    break;
                case Constants.INTERNET_ERROR_FLAG:
                    ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.internet_error, ToastUtil.CENTER);
                    progressDid1dismiss();

                    break;
            }
        }
    };

    @Override
    protected int getContentView() {
        return com.unbounded.video.R.layout.activity_feedback;
    }

    @Override
    public void init(Intent intent) {
        super.init(intent);

        setTitleName(getString(com.unbounded.video.R.string.Word_feedback));
        okHttp3Utils = OkHttp3Utils.getInstance();
        rxPermissions = new RxPermissions(this);

        gvImgWidth = (screenWidth - 50*oneDp)/4;
        list.clear();
//        list.add("");
        feedbackImg();
        list.add(defaultpath);
    }

    /**
     * t添加意见反馈默认图片
     */
    private void feedbackImg(){
        File file = new File(FeedBackActivity.defaultpath);
        if(!(file!=null && file.exists())){
            Glide.with(getApplicationContext()).load(com.unbounded.video.R.mipmap.post_pitcture).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    FileUtil.saveFile(FeedBackActivity.this, "defaultfeedbackimg.jpg", resource);
                }
            });
        }
    }

    @Override
    public void initView() {
        super.initView();

        initCameraPop();
        adapter = new FeedbackAdapter(FeedBackActivity.this, list, gvImgWidth);
        gv = (GridView) findViewById(com.unbounded.video.R.id.gv_picture);
        contentedt = (ContainsEmojiEditText) findViewById(com.unbounded.video.R.id.et_feedback_adviceedt);
        contractsedt = (ContainsEmojiEditText) findViewById(com.unbounded.video.R.id.et_feedback_contact);
        commitbtn = (Button) findViewById(com.unbounded.video.R.id.bt_feedback_post);
        gv.setAdapter(adapter);
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

        ImageView takephotobtn = (ImageView) cameraview.findViewById(com.unbounded.video.R.id.sexpop_takepiciv);//相册
        ImageView pickphotobtn = (ImageView) cameraview.findViewById(com.unbounded.video.R.id.sexpop_pickphotoiv);//拍照
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

    /**
     * 相机
     */
    public void setCamera(){
        rxPermissions.request(android.Manifest.permission.CAMERA)//这里填写所需要的权限
            .subscribe(new Action1<Boolean>() {
                @Override
                public void call(Boolean aBoolean) {
                if (aBoolean) {//true表示获取权限成功（注意这里在android6.0以下默认为true）
                    //Log.i(TAG, Manifest.permission.CAMERA + "success");
                    camerapop.showAtLocation(findViewById(com.unbounded.video.R.id.feedback_main), Gravity.BOTTOM, 0, 0);
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
        View diaView = View.inflate(FeedBackActivity.this, com.unbounded.video.R.layout.notice_dialog, null);
        goSetcameraDialog = new Dialog(FeedBackActivity.this, com.unbounded.video.R.style.dialog);
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
    public void initEvent() {
        super.initEvent();

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(position == list.size() - 1){
                    if(list.size() < 8){
                        setCamera();

                    }else {
                        ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_picnomore, ToastUtil.CENTER);
                    }
                }
            }
        });

        gv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {

                return false;
            }
        });

        commitbtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case com.unbounded.video.R.id.bt_feedback_post:
                hideInputMode();
                discuss = contentedt.getText().toString().trim();
                contracts = contractsedt.getText().toString().trim();

                if(TextUtils.isEmpty(discuss)){
                    ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_enterfeedback, ToastUtil.CENTER);
                    return;
                }
                if(TextUtils.isEmpty(contracts)){
                    ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_entercontracts, ToastUtil.CENTER);
                    return;
                }

                feedBackMethod();
                break;
        }
    }

    /**
     * 意见反馈
     */
    private void feedBackMethod(){
        progressDid1resid(com.unbounded.video.R.string.Word_uploadwait);
        haveImgMethod();

    }
    //
    private void haveImgMethod(){
        params.clear();

        params.put("content", discuss);
        params.put("contact", contracts);

        OkHttp3Utils.uploadPFile(handler, list, params);

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
                String str = GetPath.getPath(FeedBackActivity.this,photoUri);
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
        String str = GetPath.getPath(FeedBackActivity.this,uri);
        photoname = str.substring(str.lastIndexOf("/") + 1);

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 9);
        intent.putExtra("aspectY", 16);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 450);
        intent.putExtra("outputY", 800);
        intent.putExtra("return-data", true);
        intent.putExtra("scale", true);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    /**
     * 显示裁剪后的图片
     */

    public void setPicToView(Intent data){
        Bundle extras = data.getExtras();

        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            picPath = FileUtil.saveFile(FeedBackActivity.this, "videoUploadimg"+photoname, photo);

            list.add(list.size()-1, picPath);
            adapter.notifyDataSetChanged();

        }
    }
}
