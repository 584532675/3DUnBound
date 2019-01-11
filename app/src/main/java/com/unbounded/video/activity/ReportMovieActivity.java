package com.unbounded.video.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.unbounded.video.BaseActivity;
import com.unbounded.video.R;
import com.unbounded.video.utils.DensityUtil;
import com.unbounded.video.utils.HttpPostUtil;
import com.unbounded.video.utils.ToastUtil;
import com.unbounded.video.utils.imageLoad.GlideLogic;
import com.unbounded.video.utils.upload.OkHttp3Utils;
import com.unbounded.video.view.banner.GlideImageLoader;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2018/8/14 0014.
 */

public class ReportMovieActivity extends BaseActivity {
    //调用系统相册-选择图片
    private static final int IMAGE = 1;
    private TextView contentType;
    private TextView copyrightType;
    private ImageView picSelectIv;
    private LinearLayout selectImageLl;
    private Button reportSubmit;
    public static final int Commit_Upload = 1;
    public static final int Commit_Uploaderror = 2;
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Commit_Upload:
                    progressDid1dismiss();
                    ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.submit_success, ToastUtil.CENTER);
                    finish();
                    break;
                case Commit_Uploaderror:
                    progressDid1dismiss();
                    ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.uploaderror, ToastUtil.CENTER);

                    break;
            }
        }
    };
    private String videoId;
    private EditText contentEd;
    private EditText contactPhone;
    private String useridStr;

    @Override
    protected int getContentView() {
        return R.layout.activity_report_movie;
    }

    @Override
    public void initView() {
        super.initView();
        setTitleName(getString(R.string.report_title));
//        setLeftTitleStr("关闭");
        setLeftTitleVisible(true);
        contentType = (TextView) findViewById(R.id.content_type);
        copyrightType = (TextView) findViewById(R.id.copyright_type);
        picSelectIv = (ImageView) findViewById(R.id.pic_select_iv);
        selectImageLl = (LinearLayout) findViewById(R.id.select_image_ll);
        reportSubmit = (Button) findViewById(R.id.report_submit);
        contentEd = (EditText) findViewById(R.id.content_ed);
        contactPhone = (EditText) findViewById(R.id.contact_phone);
    }

    @Override
    public void initDatas() {
        super.initDatas();
        videoId = getIntent().getStringExtra("videoId");
        useridStr = getIntent().getStringExtra("userid");
    }

    @Override
    public void initEvent() {
        super.initEvent();
        contentType.setOnClickListener(this);
        copyrightType.setOnClickListener(this);
        picSelectIv.setOnClickListener(this);
        reportSubmit.setOnClickListener(this);
    }

    public int CONTENT_TYPE = 0;

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.content_type:
                CONTENT_TYPE = 1;
                contentType.setTextColor(getResources().getColor(R.color.theme_color));
                copyrightType.setTextColor(getResources().getColor(R.color.black));
                contentType.setBackground(getResources().getDrawable(R.drawable.report_content_select));
                copyrightType.setBackground(getResources().getDrawable(R.drawable.report_content_unselect));
                break;
            case R.id.copyright_type:
                CONTENT_TYPE = 2;
                contentType.setTextColor(getResources().getColor(R.color.black));
                copyrightType.setTextColor(getResources().getColor(R.color.theme_color));
                contentType.setBackground(getResources().getDrawable(R.drawable.report_content_unselect));
                copyrightType.setBackground(getResources().getDrawable(R.drawable.report_content_select));
                break;
            case R.id.pic_select_iv:
                if(imagePathList.size()==3){
                    ToastUtil.showToast(this, "最多选择3张图片", Toast.LENGTH_LONG);
                    return;
                }
                int size = 3 - imagePathList.size();
                Matisse.from(ReportMovieActivity.this)
                        .choose(MimeType.allOf()) // 选择 mime 的类型
                        .countable(true)
                        .maxSelectable(size) // 图片选择的最多数量
                        .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.85f) // 缩略图的比例
                        .imageEngine(new GlideEngine()) // 使用的图片加载引擎
                        .forResult(IMAGE); // 设置作为标记的请求码
//                Intent intent = new Intent(Intent.ACTION_PICK,
//                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(intent, IMAGE);
                break;
            case R.id.report_submit:
                String contentType = "";
                if (CONTENT_TYPE == 0) {
                    ToastUtil.showToast(this, "请选择举报类型", Toast.LENGTH_LONG);
                    return;
                } else if (CONTENT_TYPE == 1) {
                    contentType = "内容问题";
                } else {
                    contentType = "版权问题";
                }

                if (TextUtils.isEmpty(contactPhone.getText().toString())) {
                    ToastUtil.showToast(this, "请选择联系方式", Toast.LENGTH_LONG);
                    return;
                }
                progressDid1();
                OkHttp3Utils okHttp3Utils = OkHttp3Utils.getInstance();
                okHttp3Utils.upReportFilePic(handler, videoId, useridStr, "image", contentType,
                        contentEd.getText().toString(), contactPhone.getText().toString(), imagePathList);
                break;
        }
    }
    List<ImageView> imagePathList = new ArrayList<>();
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获取图片路径
        if (requestCode == IMAGE && resultCode == Activity.RESULT_OK && data != null) {
//            Uri selectedImage = data.getData();
            List<Uri> dataList = Matisse.obtainResult(data);
//            selectImageLl.addView(imageView);
//            imagePathList.add(imagePath);
            Log.v("dataList",""+dataList.toString());
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            for (int i = 0 ;i<dataList.size();i++){
                Cursor c = getContentResolver().query(dataList.get(i), filePathColumns, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePathColumns[0]);
                String imagePath = c.getString(columnIndex);
                Log.v("imagePath", "" + imagePath);

                View view = LayoutInflater.from(this).inflate(R.layout.item_reprot_image,null);
                ImageView imageView = (ImageView) view.findViewById(R.id.iamge_iv);
                ImageView deleteImage = (ImageView) view.findViewById(R.id.report_delete_iv);
//                ImageView imageView = new ImageView(this);
//                imageView.setLayoutParams(new LinearLayout.LayoutParams(DensityUtil.dip2px(this,50),
//                        DensityUtil.dip2px(this,60)));

                File file = new File(imagePath);
                GlideLogic.glideLoadVideoImg324(this, file, imageView, DensityUtil.dip2px(this,50),
                        DensityUtil.dip2px(this,60));
                deleteImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View view1 = (View) v.getTag();
                        selectImageLl.removeView(view1);
                        ImageView imageView1 = (ImageView) view1.findViewById(R.id.iamge_iv);
                        imagePathList.remove(imageView1);
                    }
                });
                deleteImage.setTag(view);
                imageView.setTag(imagePath);
                selectImageLl.addView(view);
                imagePathList.add(imageView);
                c.close();
            }

        }
    }

}
