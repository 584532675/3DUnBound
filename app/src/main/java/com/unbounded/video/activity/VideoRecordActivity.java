package com.unbounded.video.activity;

import android.app.Dialog;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unbounded.video.BaseActivity;
import com.unbounded.video.utils.videoRecord.PreviewSizeutil;
import com.unbounded.video.utils.videoRecord.ScreenUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by zjf on 2017/8/3 0003.
 */

public class VideoRecordActivity extends BaseActivity implements SurfaceHolder.Callback{
    public static final int RUNNING_FLAG= 0;
    public static final int BACK_FLAG= 1;
    public static final int FRONT_FLAG= 2;
    public static final int ON_FLAG= 3;
    public static final int OFF_FLAG= 4;
    public int Thirty= 30;
    private static final String TAG = "Havorld";
    //路径/storage/emulated/0/Movies/
    private String savePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/moyan/recodeVideos/";
    private String path;
    private MediaRecorder mediaRecorder;// 录制视频的类
    private SurfaceView surfaceview;// 显示视频的控件
    private Camera camera;// 摄像头
    private SurfaceHolder surfaceHolder;
    private FrameLayout frameLayout;
    private ImageButton imageButton, reset, ok;
    private boolean isSufaceCreated = false;
    private boolean isRecording = false;
    LinearLayout timelinear;
    ImageView timeiv,switchiv,flashiv;
    int cameraType = BACK_FLAG;
    int flashType = OFF_FLAG;
    TextView timetv;
    int timeInt = Thirty;

    Dialog deleteDialog;

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RUNNING_FLAG:
                    if(timeInt > 0){
                        timeInt = timeInt - 1;
                        if(timeInt > 9){
                            timetv.setText("00:"+timeInt);
                        }else{
                            timetv.setText("00:0"+timeInt);
                        }
                        handler.sendEmptyMessageDelayed(RUNNING_FLAG, oneMinute);
                    }else {
                        isRecording = false;
                        setRecord(isRecording);
                        timeInt = Thirty;
                    }

                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isFullScreen = true;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentView() {
        return com.unbounded.video.R.layout.activity_videorecord;
    }

    @Override
    public void init(Intent intent) {
        super.init(intent);

        setTitleViewVisible(false);
    }

    @Override
    public void initView() {
        super.initView();

        frameLayout = (FrameLayout) findViewById(com.unbounded.video.R.id.videorecord_frameLayout);
        imageButton = (ImageButton) findViewById(com.unbounded.video.R.id.videorecord_imageButton);
        reset = (ImageButton) findViewById(com.unbounded.video.R.id.videorecord_reset);
        ok = (ImageButton) findViewById(com.unbounded.video.R.id.videorecord_ok);
        timelinear = (LinearLayout) findViewById(com.unbounded.video.R.id.videorecord_timelinear);
        timeiv = (ImageView) findViewById(com.unbounded.video.R.id.videorecord_timeiv);
        timetv = (TextView) findViewById(com.unbounded.video.R.id.videorecord_timetv);
        switchiv = (ImageView) findViewById(com.unbounded.video.R.id.videorecord_switchiv);
        flashiv = (ImageView) findViewById(com.unbounded.video.R.id.videorecord_flashiv);

        surfaceview = (SurfaceView) this.findViewById(com.unbounded.video.R.id.videorecord_surfaceview);
        frameLayout.setOnClickListener(this);
        imageButton.setOnClickListener(this);
        switchiv.setOnClickListener(this);
        flashiv.setOnClickListener(this);
        reset.setOnClickListener(this);
        ok.setOnClickListener(this);
        // 绑定SurfaceView，取得SurfaceHolder对象
        surfaceHolder = surfaceview.getHolder();
        // surfaceHolder加入回调接口
        surfaceHolder.addCallback(this);
        // 设置预览大小
        // surfaceHolder.setFixedSize(width, height);
        // 设置显示器类型 ，setType必须设置
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        imageButton.setVisibility(View.VISIBLE);
        timelinear.setVisibility(View.GONE);
        ok.setVisibility(View.GONE);
        reset.setVisibility(View.GONE);


    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case com.unbounded.video.R.id.videorecord_imageButton:
//                isRecording = !isRecording;
//                setRecord(isRecording);
                if(isRecording == false){
                    isRecording = true;
                    setRecord(isRecording);
                    timelinear.setVisibility(View.VISIBLE);
                    handler.sendEmptyMessageDelayed(RUNNING_FLAG, oneMinute);

                }else {
                    isRecording = false;
                    setRecord(isRecording);
                    timelinear.setVisibility(View.VISIBLE);
                    handler.removeMessages(RUNNING_FLAG);
                    timeInt = Thirty;
                }


                break;
            case com.unbounded.video.R.id.videorecord_ok:
                Intent intent = new Intent(VideoRecordActivity.this,VideoUpLoadActivity.class);
                intent.putExtra("recodepath", path);
                startActivity(intent);
                mountFile(VideoRecordActivity.this, path);
                finish();

                break;
            case com.unbounded.video.R.id.videorecord_reset:
                deleteDia();

                break;
            //
            case com.unbounded.video.R.id.videorecord_switchiv:
                camera.stopPreview();//停掉原来摄像头的预览
                camera.release();//释放资源
                camera = null;//取消原来摄像头

                if(cameraType == BACK_FLAG){
                    cameraType = FRONT_FLAG;

                    camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                }else {
                    cameraType = BACK_FLAG;

                    camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                }

                try {
                    camera.setPreviewDisplay(surfaceHolder);//通过surfaceview显示取景画面
                } catch (IOException e) {
                    e.printStackTrace();
                }
                camera.startPreview();//开始预览


                break;
            //
            case com.unbounded.video.R.id.videorecord_flashiv:
                if(flashType == OFF_FLAG){
                    flashType = ON_FLAG;
                    Camera.Parameters parameter = camera.getParameters();
                    parameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(parameter);
                }else{
                    flashType = OFF_FLAG;
                    Camera.Parameters parameter = camera.getParameters();
                    parameter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(parameter);
                }


                break;

        }
    }

    private void setRecord(boolean isRecording) {
        if (isRecording) {
            initMediaRecorder();
//            frameLayout.setBackgroundResource(R.mipmap.start_bc);
            imageButton.setBackgroundResource(com.unbounded.video.R.mipmap.whitepausebtn1);
            reset.setVisibility(View.GONE);
            ok.setVisibility(View.GONE);
            timeiv.setImageResource(com.unbounded.video.R.mipmap.redicon);
        } else {
            freeMediaRecorder();
//            frameLayout.setBackgroundResource(R.mipmap.stop_bc);
            imageButton.setVisibility(View.GONE);
            frameLayout.setVisibility(View.GONE);
            imageButton.setBackgroundResource(com.unbounded.video.R.mipmap.stop);
            reset.setVisibility(View.VISIBLE);
            ok.setVisibility(View.VISIBLE);
            timeiv.setImageResource(com.unbounded.video.R.mipmap.stop);
        }
    }

    /**
     * 删除刚刚拍摄的视频
     */
    private void deleteRecode(){
        if (path == null || path == "")
            return;
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        file = null;

//        frameLayout.setBackgroundResource(R.mipmap.stop_bc);
        imageButton.setBackgroundResource(com.unbounded.video.R.mipmap.stop);
        ok.setVisibility(View.GONE);
        reset.setVisibility(View.GONE);
        imageButton.setVisibility(View.VISIBLE);
        frameLayout.setVisibility(View.VISIBLE);

        timeInt = Thirty;
        timetv.setText("00:"+timeInt);
    }

    /**
     * 删除dialog
     */
    private void deleteDia(){
        View diaView = View.inflate(VideoRecordActivity.this, com.unbounded.video.R.layout.notice_dialog, null);
        deleteDialog = new Dialog(VideoRecordActivity.this, com.unbounded.video.R.style.dialog);
        deleteDialog.setContentView(diaView);
        deleteDialog.setCanceledOnTouchOutside(false);
        deleteDialog.setCancelable(false);

        Button surebtn = (Button) deleteDialog.findViewById(com.unbounded.video.R.id.notice_yesbtn);
        Button exitbtn = (Button) deleteDialog.findViewById(com.unbounded.video.R.id.notice_canclebtn);
        TextView titletv = (TextView) deleteDialog.findViewById(com.unbounded.video.R.id.notice_tv1);
        TextView contenttv = (TextView) deleteDialog.findViewById(com.unbounded.video.R.id.notice_tv2);

        surebtn.setText(getString(com.unbounded.video.R.string.sure_btn));
        exitbtn.setText(getString(com.unbounded.video.R.string.cancle_btn));
        titletv.setText(getString(com.unbounded.video.R.string.Word_noticetitle));
        contenttv.setText(getString(com.unbounded.video.R.string.Word_deleterecode));

        surebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRecode();
                deleteDialog.dismiss();
            }
        });
        //
        exitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
            }
        });

        deleteDialog.show();

        WindowManager.LayoutParams params = deleteDialog.getWindow().getAttributes();
        params.width = screenWidth * 3 / 4;
        // params.height = 200 ;
        deleteDialog.getWindow().setAttributes(params);

    }



    private void initCamera() {
        // 获取照相机实例
        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
//        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        // 获取相机的参数
        Camera.Parameters parameters = camera.getParameters();

        // 获取支持的预览尺寸
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        for (int i = 0; i < sizes.size(); i++) {
//			 Log.e(TAG,"width:"+sizes.get(i).width+"---"+"height:"+sizes.get(i).height);
        }

        Camera.Size csize = camera.getParameters().getPreviewSize();
        Camera.Size previewSize = PreviewSizeutil.getInstance().getCameraPreviewSize(parameters.getSupportedPreviewSizes(),
                ScreenUtil.getScreenHeight(this));
        Log.e(TAG, previewSize.width + "===" + previewSize.height);

        parameters.setPreviewSize(previewSize.width, previewSize.height);

        // 获取对焦模式
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            // 设置自动对焦
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        // 设置预览帧数
        parameters.setPreviewFrameRate(20);
        // 解决在电脑端视频缩略图倾斜了90的问题(注意：设置这个参数后如果PictureSize的参数是X*Y，得到的图片大小是Y*X)
        parameters.set("rotation", 90);
        camera.setParameters(parameters);
        // 设置预览方向， 摄像图旋转90度
        camera.setDisplayOrientation(0);

        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
            releaseResource();
        }
    }

    private void initMediaRecorder() {

        if (mediaRecorder != null) {

            freeMediaRecorder();
        }
        mediaRecorder = new MediaRecorder();// 创建mediarecorder对象
        if (camera == null) {

            initCamera();
        }
        camera.unlock();// 允许media进程得以访问camera。
        mediaRecorder.setCamera(camera);
        // 解决在电脑上播放视频旋转90度的问题
//		mediaRecorder.setOrientationHint(90);
        mediaRecorder.setOrientationHint(0);

        // 设置音频源,从麦克风采集声音
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 设置录制视频源,从摄像头采集图像
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

		/*
		 * //在Android2.2 (API
		 * Level8)之前，你必须直接设置输出格式和编码格式等参数，而不是使用CamcorderProfile // 设置视频的输出格式:
		 * THREE_GPP(3gp)、MPEG_4(mp4)
		 * mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		 * //设置音频的编码格式
		 * mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		 * //设置视频的编码格式
		 * mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
		 * //设置视频分辨率 mediaRecorder.setVideoSize(960,720);
		 */

        // 在Android2.2 (API Level8)之后，你可以直接使用CamcorderProfile
        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
        // 构造CamcorderProfile,使用高质量视频
        mediaRecorder.setProfile(profile);
        // 设置视频分辨率
        mediaRecorder.setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight);

        // 设置视频捕获帧速率
        mediaRecorder.setVideoFrameRate(30);
        // 设置视频编码位率(比特率)
        mediaRecorder.setVideoEncodingBitRate(5 * 512 * 512);
        // 设置最大录像时间单位为毫秒
        mediaRecorder.setMaxDuration(30000);
        // 设置使用SurfaceView来显示视频预览
        mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());

        File file = new File(savePath);
        if (!file.exists()) {
            //多级文件夹的创建
            file.mkdirs();
        }
        file = null;
        path = savePath+"XiuMF_" + System.currentTimeMillis() + ".mp4";
        // 设置视频输出路径
        mediaRecorder.setOutputFile(path);
        try {
            // 准备录制
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (Exception e) {
            releaseResource();
            e.printStackTrace();
        }

    }

    private void freeMediaRecorder() {

        if (mediaRecorder != null) {
            // 停止录制
            mediaRecorder.stop();
            // 重置MediaRecorder对象，使其为空闲状态
            mediaRecorder.reset();
            // 释放MediaRecorder对象
            mediaRecorder.release();
            mediaRecorder = null;
        }
        if (camera != null) {

            camera.lock();
        }
    }

    private void freeCamera() {

        if (camera != null) {

            try {
                camera.setPreviewDisplay(null);
            } catch (Exception e) {
                e.printStackTrace();
            }finally{

                camera.setPreviewCallback(null);
                camera.stopPreview();
                camera.lock();
                camera.release();
                camera = null;
            }
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // 将holder，这个holder为开始在oncreat里面取得的holder，将它赋给surfaceHolder
        surfaceHolder = holder;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isSufaceCreated = true;
        // 将holder，这个holder为开始在oncreat里面取得的holder，将它赋给surfaceHolder
        surfaceHolder = holder;
        initCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isSufaceCreated = true;
        releaseResource();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseResource();
    }

    private void releaseResource() {
        freeCamera();
        freeMediaRecorder();
        surfaceview = null;
        surfaceHolder = null;
    }
}
