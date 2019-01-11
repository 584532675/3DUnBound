package com.unbounded.video.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.unbounded.video.BaseActivity;
import com.unbounded.video.R;
import com.unbounded.video.bean.LocalFilm;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.utils.FileUtil;
import com.unbounded.video.utils.GetPath;
import com.unbounded.video.utils.SharedPrefsUtil;
import com.unbounded.video.utils.ToastUtil;
import com.unbounded.video.utils.imageLoad.GlideLogic;
import com.unbounded.video.utils.upload.OkHttp3Utils;
import com.unbounded.video.view.banner.containsEmojiEditText.ContainsEmojiEditText;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import rx.functions.Action1;

/**
 * Created by zjf on 2017/7/26.
 */

public class VideoUpLoadActivity extends BaseActivity {
    String root = Environment.getExternalStorageDirectory() + "/moyan/temporary/";
    public static final int Commit_Upload = 3;
    public static final int Commit_RecodeUpload = 4;
    public static final int Commit_RecodeUploaderror = 5;
    //使用照相机拍照获取图片
    public static final int SELECT_PIC_BY_TACK_PHOTO = 1;
    //使用相册中的图片
    public static final int SELECT_PIC_BY_PICK_PHOTO = 2;
    //裁剪照片
    public static final int REQUESTCODE_CUTTING = 0;

    RelativeLayout picrela,imgrela;
    ImageView imgiv,addpiciv,selectiv;
    TextView selectcontenttv,selectcontenttv2;
    int imgWidth,imgHeight;
    ContainsEmojiEditText jianjieedt,miaoshuedt;
    Button uploadbtn;
    boolean selectBoolean = false;

    String photoname,picPath,localVideoPath,recodepath;
    int localVideoId;
    LocalFilm localVideo;
    Dialog successDialog,gprDialog;
    Dialog goSetcameraDialog;
    Dialog contentDialog;

    PopupWindow camerapop;
    View cameraview;

    OkHttp3Utils okHttp3Utils;
    RxPermissions rxPermissions;


    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Commit_Upload:
                    progressDid1dismiss();
//                    finish();
                    successDialog(Commit_Upload);

                    break;

                case Commit_RecodeUpload:
                    progressDid1dismiss();
                    successDialog(Commit_RecodeUpload);
                    break;
                case Commit_RecodeUploaderror:
                    progressDid1dismiss();
                    ToastUtil.showToast(getApplicationContext(), R.string.uploaderror, ToastUtil.CENTER);

                    break;
            }
        }
    };

    @Override
    protected int getContentView() {
        return R.layout.activity_upload;
    }

    @Override
    public void init(Intent intent) {
        super.init(intent);

//        localVideoPath = intent.getStringExtra("localVideoPath");
//        localVideoId = intent.getIntExtra("localVideoId",0);
        localVideo = (LocalFilm) intent.getSerializableExtra("localVideo");
        if(localVideo != null){
            localVideoPath = localVideo.getPath();
            localVideoId = localVideo.getId();
        }else{
            recodepath = intent.getStringExtra("recodepath");
        }



        setTitleName(getString(R.string.Word_videoupload));
        userId = SharedPrefsUtil.getValue(getApplicationContext(), Constants.UserId_FLAG, "");
        okHttp3Utils = OkHttp3Utils.getInstance();
        rxPermissions = new RxPermissions(this);
    }

    @Override
    public void initView() {
        super.initView();

        initCameraPop();
        picrela = (RelativeLayout) findViewById(R.id.upload_picrela);
        imgrela = (RelativeLayout) findViewById(R.id.upload_imgivrela);
        imgiv = (ImageView) findViewById(R.id.upload_imgiv);
        addpiciv = (ImageView) findViewById(R.id.upload_addpiciv);
        jianjieedt = (ContainsEmojiEditText) findViewById(R.id.upload_jianjieedt);
        miaoshuedt = (ContainsEmojiEditText) findViewById(R.id.upload_miaoshuedt);
        uploadbtn = (Button) findViewById(R.id.videoupload_uploadbtn);
        selectiv = (ImageView) findViewById(R.id.videoupload_selectiv);
        selectcontenttv = (TextView) findViewById(R.id.videoupload_contenttv1);
        selectcontenttv2 = (TextView) findViewById(R.id.videoupload_contenttv2);

        if(localVideo != null){
            GlideLogic.glideLoadPic423(VideoUpLoadActivity.this, localVideoPath, imgiv, screenHeight * 11 / 16, screenWidth * 11 / 16);

            File file = new File(root + "defaultvideoUploadimg"+localVideo.getId()+".jpg");
            if(file!=null && file.exists()){
                picPath = root + "defaultvideoUploadimg"+localVideo.getId()+".jpg";
            }else {
                Glide.with(getApplicationContext()).load(localVideoPath).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        picPath = FileUtil.saveFile(VideoUpLoadActivity.this, "defaultvideoUploadimg"+localVideo.getId()+".jpg", resource);
                    }
                });
            }
        }else{
            GlideLogic.glideLoadPic423(VideoUpLoadActivity.this, recodepath, imgiv, screenHeight * 11 / 16, screenWidth * 11 / 16);

            Glide.with(getApplicationContext()).load(recodepath).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    picPath = FileUtil.saveFile(VideoUpLoadActivity.this, "defaultrecodevideo.jpg", resource);
                }
            });

        }

    }

    @Override
    public void initParams() {
        super.initParams();

        imgWidth = screenWidth-140*oneDp;
        imgHeight = imgWidth*3/4;

        ViewGroup.LayoutParams imgrelaparams = imgrela.getLayoutParams();
        imgrelaparams.height = imgHeight;
        imgrelaparams.width = imgWidth;
        imgrela.setLayoutParams(imgrelaparams);

        ViewGroup.LayoutParams imgivparams = imgiv.getLayoutParams();
        imgrelaparams.height = imgHeight;
        imgrelaparams.width = imgWidth;
        imgiv.setLayoutParams(imgivparams);
    }

    @Override
    public void initEvent() {
        super.initEvent();

        addpiciv.setOnClickListener(this);
        uploadbtn.setOnClickListener(this);
        selectiv.setOnClickListener(this);
        selectcontenttv.setOnClickListener(this);
        selectcontenttv2.setOnClickListener(this);

        uploadbtn.setClickable(false);
    }

    /**
     * 勾选方法
     */
    private void selectMethod(){
        if(selectBoolean == false){
            uploadbtn.setBackgroundResource(R.drawable.uploadbuttonbtn_selector);
            selectiv.setImageResource(R.mipmap.select);
            uploadbtn.setClickable(true);
            selectBoolean = true;
        }else{
            uploadbtn.setBackgroundColor(Color.parseColor("#d6eafc"));
            selectiv.setImageResource(R.mipmap.unselect);
            uploadbtn.setClickable(false);
            selectBoolean = false;
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            //添加图片
            case R.id.upload_addpiciv:
                setCamera();

                break;
            case R.id.videoupload_contenttv1:
                selectMethod();
                break;
            case R.id.videoupload_contenttv2:
                contentDia();
                break;

            //勾选协议
            case R.id.videoupload_selectiv:
                selectMethod();

                break;

            //上传
            case R.id.videoupload_uploadbtn:
                String videoname = jianjieedt.getText().toString().trim();
                String introduction = miaoshuedt.getText().toString().trim();
                userId = SharedPrefsUtil.getValue(getApplicationContext(), Constants.UserId_FLAG, "");

                if(TextUtils.isEmpty(picPath)){
                    ToastUtil.showToast(getApplicationContext(), R.string.videonpicedt, ToastUtil.CENTER);
                    return;
                }
                if(TextUtils.isEmpty(videoname)){
                    ToastUtil.showToast(getApplicationContext(), R.string.videonameedt, ToastUtil.CENTER);
                    return;
                }
                if(TextUtils.isEmpty(introduction)){
                    ToastUtil.showToast(getApplicationContext(), R.string.videointroduceedt, ToastUtil.CENTER);
                    return;
                }
                if(TextUtils.isEmpty(userId)){
                    openActivity(LoginActivity.class);
                    return;
                }

                int netType = getNetState();

                if(netType == Constants.WIFI_FLAG){
                    upLoadMethod(videoname, introduction);
                }else if(netType == Constants.GPR_FLAG){
                    //数据流量弹出对话框
                    gprDia(videoname, introduction);

                }else {
                    ToastUtil.showToast(getApplicationContext(), R.string.checkbet_word, ToastUtil.CENTER);
                }

                break;
        }
    }

    /**
     * 用户上传协议
     */
    private void contentDia(){
        View diaView = View.inflate(VideoUpLoadActivity.this, R.layout.content_dialog, null);
        contentDialog = new Dialog(VideoUpLoadActivity.this, R.style.dialog);
        contentDialog.setContentView(diaView);
        contentDialog.setCanceledOnTouchOutside(false);
        contentDialog.setCancelable(false);

        Button yesbtn = (Button) contentDialog.findViewById(R.id.content_yesbtn);
        TextView titletv = (TextView) contentDialog.findViewById(R.id.content_tv1);
        TextView contenttv = (TextView) contentDialog.findViewById(R.id.content_tv2);

        yesbtn.setText(getString(R.string.sure_btn));
        titletv.setText(getString(R.string.upload_title));
        contenttv.setText(getString(R.string.upload_content));

        yesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentDialog.dismiss();
            }
        });

        contentDialog.show();

        WindowManager.LayoutParams params = contentDialog.getWindow().getAttributes();
        params.width = screenWidth * 3 / 4;
        params.height = screenHeight * 3 / 4 ;
        contentDialog.getWindow().setAttributes(params);
    }

    /**
     * 上传本地已有方法
     */
    private void upLoadMethod(String videoname, String introduction){
        //录制视频进入的
        if(!(TextUtils.isEmpty(recodepath))){
            OkHttp3Utils.upFileRecode(handler, userId, recodepath, picPath, videoname, introduction);
            progressDid1resid(R.string.Word_uploadwait);

            //本地视频进入的
        }else if(!(TextUtils.isEmpty(localVideoPath))){
            OkHttp3Utils.upFile(new MyProgressListener(localVideoId), VideoUpLoadActivity.this, userId, localVideoId, localVideoPath, picPath, videoname, introduction);

            progressDid1resid(R.string.Word_uploadwait);
            handler.sendEmptyMessageDelayed(Commit_Upload, 500);

        }
    }

    /**
     * 数据流量询问对话框
     */
    private void gprDia(final String videoname, final String introduction){
        View diaView = View.inflate(VideoUpLoadActivity.this, R.layout.notice_dialog, null);
        gprDialog = new Dialog(VideoUpLoadActivity.this, R.style.dialog);
        gprDialog.setContentView(diaView);
        gprDialog.setCanceledOnTouchOutside(false);
        gprDialog.setCancelable(false);

        Button yesbtn = (Button) gprDialog.findViewById(R.id.notice_yesbtn);
        Button exitbtn = (Button) gprDialog.findViewById(R.id.notice_canclebtn);
        TextView titletv = (TextView) gprDialog.findViewById(R.id.notice_tv1);
        TextView contenttv = (TextView) gprDialog.findViewById(R.id.notice_tv2);

        yesbtn.setText(getString(R.string.sure_btn));
        exitbtn.setText(getString(R.string.cancle_btn));
        titletv.setText(getString(R.string.Word_noticetitle));
        contenttv.setText(getString(R.string.Word_gprdownload));


        yesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upLoadMethod(videoname, introduction);
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
     * 图片
     */
    private void initCameraPop(){
        cameraview = this.getLayoutInflater().inflate(R.layout.camerapop, null);
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

        ImageView takephotobtn = (ImageView) cameraview.findViewById(R.id.sexpop_takepiciv);
        ImageView pickphotobtn = (ImageView) cameraview.findViewById(R.id.sexpop_pickphotoiv);
        ImageView closeiv = (ImageView) cameraview.findViewById(R.id.sexpop_closeiv);

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
//                    initPicDialog();
                    camerapop.showAtLocation(findViewById(R.id.upload_main), Gravity.BOTTOM, 0, 0);
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
        View diaView = View.inflate(VideoUpLoadActivity.this, R.layout.notice_dialog, null);
        goSetcameraDialog = new Dialog(VideoUpLoadActivity.this, R.style.dialog);
        goSetcameraDialog.setContentView(diaView);
        goSetcameraDialog.setCanceledOnTouchOutside(false);
        goSetcameraDialog.setCancelable(false);

        Button toSetbtn = (Button) goSetcameraDialog.findViewById(R.id.notice_yesbtn);
        Button exitbtn = (Button) goSetcameraDialog.findViewById(R.id.notice_canclebtn);
        TextView titletv = (TextView) goSetcameraDialog.findViewById(R.id.notice_tv1);
        TextView contenttv = (TextView) goSetcameraDialog.findViewById(R.id.notice_tv2);

        toSetbtn.setText(getString(R.string.Word_Set));
        exitbtn.setText(getString(R.string.cancle_btn));
        titletv.setText(getString(R.string.Word_noticetitle));
        contenttv.setText(getString(R.string.Word_noticecamera));


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
     * 提示上传成功
     */
    private void successDialog(int type){
        View diaView = View.inflate(VideoUpLoadActivity.this, R.layout.upload_dialog, null);
        successDialog = new Dialog(VideoUpLoadActivity.this, R.style.dialog);
        successDialog.setContentView(diaView);
        successDialog.setCanceledOnTouchOutside(false);
        successDialog.setCancelable(false);

        Button yesbtn = (Button) successDialog.findViewById(R.id.uploaddia_yesbtn);
        TextView tv1 = (TextView) successDialog.findViewById(R.id.uploaddia_tv1);

        if(type == Commit_Upload){
            tv1.setText(R.string.Word_uploadsuccesstv1);
        }else if(type == Commit_RecodeUpload){
            tv1.setText(R.string.Word_uploadsuccesstv);
        }


        yesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                successDialog.dismiss();
                finish();
            }
        });

        successDialog.show();

        WindowManager.LayoutParams params = successDialog.getWindow().getAttributes();
        params.width = screenWidth * 3 / 4;
        // params.height = 200 ;
        successDialog.getWindow().setAttributes(params);

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
                String str = GetPath.getPath(VideoUpLoadActivity.this,photoUri);
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
        String str = GetPath.getPath(VideoUpLoadActivity.this,uri);
        photoname = str.substring(str.lastIndexOf("/") + 1);

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 4);
        intent.putExtra("aspectY", 3);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 400);
        intent.putExtra("outputY", 300);
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
            picPath = FileUtil.saveFile(VideoUpLoadActivity.this, "videoUploadimg"+photoname, photo);

            GlideLogic.glideLoadPic423(VideoUpLoadActivity.this, picPath, imgiv, imgWidth*11/16, imgHeight*11/16);

        }
    }

    /**
     *
     */
    public class MyProgressListener implements ProgressListener{
        int localFilmId;
        Intent intent = new Intent();

        public MyProgressListener(int localFilmId) {
            this.localFilmId = localFilmId;
        }

        @Override
        public void onProgress(long totalBytes, long remainingBytes, boolean done) {
            if(startReco() == true){
                Log.e("info", localFilmId+"进度="+(totalBytes - remainingBytes) * 100 / totalBytes + "%");

                int progress = (int)((totalBytes - remainingBytes) * 100 / totalBytes);

                intent.setAction("com.myBroadcast1");//设置意图
                intent.putExtra("localFilmId", localFilmId);//设置所需发送的消息标签以及内容    视频id
                intent.putExtra("localFilmProgress", progress);//设置所需发送的消息标签以及内容    本视频对应进度
                intent.putExtra("localFilmFlag", LocalFilm.UPLOADING);//设置所需发送的消息标签以及内容    本视频对应进度
                VideoUpLoadActivity.this.sendBroadcast(intent);//发送普通广播

            }
        }
    }

    public interface ProgressListener {
        void onProgress(long totalBytes, long remainingBytes, boolean done);
    }

    /**
     * Bitmap对象保存为图片文件
     */
    public void saveBitmapFile(Bitmap bitmap){
        File file=new File("/mnt/sdcard/pic/01.jpg");//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
