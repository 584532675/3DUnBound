package com.unbounded.video.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.unbounded.video.BaseActivity;
import com.unbounded.video.bean.LocalPic;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.utils.SharedPrefsUtil;
import com.unbounded.video.utils.ToastUtil;
import com.unbounded.video.utils.imageLoad.GlideLogic;
import com.unbounded.video.utils.upload.OkHttp3Utils;
import com.unbounded.video.view.banner.containsEmojiEditText.ContainsEmojiEditText;

/**
 * Created by zjf on 2017/8/2 0002.
 */

public class PicUploadActivity extends BaseActivity {
    public static final int Commit_Upload = 0;
    public static final int Commit_Uploaderror = 1;
    LocalPic localPic;

    RelativeLayout picrela,imgrela;
    ImageView imgiv,addpiciv,selectiv;
    TextView selectcontenttv, selectcontenttv2;
    int imgWidth,imgHeight;
    ContainsEmojiEditText jianjieedt,miaoshuedt;
    Button uploadbtn;
    OkHttp3Utils okHttp3Utils;
    boolean selectBoolean = false;

    Dialog successDialog, gprDialog, contentDialog;

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Commit_Upload:
                    progressDid1dismiss();
                    successDid();

                    break;
                case Commit_Uploaderror:
                    progressDid1dismiss();
                    ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.uploaderror, ToastUtil.CENTER);

                    break;
            }
        }
    };

    @Override
    protected int getContentView() {
        return com.unbounded.video.R.layout.activity_upload;
    }

    @Override
    public void init(Intent intent) {
        super.init(intent);

        localPic = (LocalPic) intent.getSerializableExtra("localPicBean");

        setTitleName(getString(com.unbounded.video.R.string.Word_picupload));
        userId = SharedPrefsUtil.getValue(getApplicationContext(), Constants.UserId_FLAG, "");
        okHttp3Utils = OkHttp3Utils.getInstance();

    }

    @Override
    public void initView() {
        super.initView();

        picrela = (RelativeLayout) findViewById(com.unbounded.video.R.id.upload_picrela);
        imgrela = (RelativeLayout) findViewById(com.unbounded.video.R.id.upload_imgivrela);
        imgiv = (ImageView) findViewById(com.unbounded.video.R.id.upload_imgiv);
        addpiciv = (ImageView) findViewById(com.unbounded.video.R.id.upload_addpiciv);
        jianjieedt = (ContainsEmojiEditText) findViewById(com.unbounded.video.R.id.upload_jianjieedt);
        miaoshuedt = (ContainsEmojiEditText) findViewById(com.unbounded.video.R.id.upload_miaoshuedt);
        uploadbtn = (Button) findViewById(com.unbounded.video.R.id.videoupload_uploadbtn);
        selectiv = (ImageView) findViewById(com.unbounded.video.R.id.videoupload_selectiv);
        selectcontenttv = (TextView) findViewById(com.unbounded.video.R.id.videoupload_contenttv1);
        selectcontenttv2 = (TextView) findViewById(com.unbounded.video.R.id.videoupload_contenttv2);

        addpiciv.setVisibility(View.GONE);

        if(localPic != null){
            GlideLogic.glideLoadPic423(PicUploadActivity.this, localPic.getPath(), imgiv, screenHeight * 11 / 16, screenWidth * 11 / 16);
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
     * 成功对话框
     */
    private void successDid(){
        View diaView = View.inflate(PicUploadActivity.this, com.unbounded.video.R.layout.upload_dialog, null);
        successDialog = new Dialog(PicUploadActivity.this, com.unbounded.video.R.style.dialog);
        successDialog.setContentView(diaView);
        successDialog.setCanceledOnTouchOutside(false);
        successDialog.setCancelable(false);

        Button yesbtn = (Button) successDialog.findViewById(com.unbounded.video.R.id.uploaddia_yesbtn);

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
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case com.unbounded.video.R.id.videoupload_contenttv1:
                selectMethod();
                break;
            case com.unbounded.video.R.id.videoupload_contenttv2:
                contentDia();
                break;

            //勾选协议
            case com.unbounded.video.R.id.videoupload_selectiv:
                selectMethod();

                break;
            //上传
            case com.unbounded.video.R.id.videoupload_uploadbtn:
                String picname = jianjieedt.getText().toString().trim();
                String introduction = miaoshuedt.getText().toString().trim();
                if(TextUtils.isEmpty(picname)){
                    ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.picnameedt, ToastUtil.CENTER);
                    return;
                }
                if(TextUtils.isEmpty(introduction)){
                    ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.picintroduceedt, ToastUtil.CENTER);
                    return;
                }

                int netType = getNetState();

                if(netType == Constants.WIFI_FLAG){
                    upLoadMethod(picname, introduction);
                }else if(netType == Constants.GPR_FLAG){
                    //数据流量弹出对话框
                    gprDia(picname, introduction);

                }else {
                    ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.checkbet_word, ToastUtil.CENTER);
                }

                break;
        }
    }

    /**
     * 用户上传协议
     */
    private void contentDia(){
        View diaView = View.inflate(PicUploadActivity.this, com.unbounded.video.R.layout.content_dialog, null);
        contentDialog = new Dialog(PicUploadActivity.this, com.unbounded.video.R.style.dialog);
        contentDialog.setContentView(diaView);
        contentDialog.setCanceledOnTouchOutside(false);
        contentDialog.setCancelable(false);

        Button yesbtn = (Button) contentDialog.findViewById(com.unbounded.video.R.id.content_yesbtn);
        TextView titletv = (TextView) contentDialog.findViewById(com.unbounded.video.R.id.content_tv1);
        TextView contenttv = (TextView) contentDialog.findViewById(com.unbounded.video.R.id.content_tv2);

        yesbtn.setText(getString(com.unbounded.video.R.string.sure_btn));
        titletv.setText(getString(com.unbounded.video.R.string.upload_title));
        contenttv.setText(getString(com.unbounded.video.R.string.upload_content));

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
     * 勾选方法
     */
    private void selectMethod(){
        if(selectBoolean == false){
            uploadbtn.setBackgroundResource(com.unbounded.video.R.drawable.uploadbuttonbtn_selector);
            selectiv.setImageResource(com.unbounded.video.R.mipmap.select);
            uploadbtn.setClickable(true);
            selectBoolean = true;
        }else{
            uploadbtn.setBackgroundColor(Color.parseColor("#d6eafc"));
            selectiv.setImageResource(com.unbounded.video.R.mipmap.unselect);
            uploadbtn.setClickable(false);
            selectBoolean = false;
        }
    }

    /**
     * 图片上传
     */
    private void upLoadMethod(String picname, String introduction){
        OkHttp3Utils.upFilePic(handler, userId, picname, introduction, localPic.getPath());
        progressDid1resid(com.unbounded.video.R.string.Word_uploadwait);

    }

    /**
     * 数据流量询问对话框
     */
    private void gprDia(final String picname, final String introduction){
        View diaView = View.inflate(PicUploadActivity.this, com.unbounded.video.R.layout.notice_dialog, null);
        gprDialog = new Dialog(PicUploadActivity.this, com.unbounded.video.R.style.dialog);
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
                upLoadMethod(picname, introduction);
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


}
