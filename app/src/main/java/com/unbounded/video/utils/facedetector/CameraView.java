package com.unbounded.video.utils.facedetector;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.WindowManager;
import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2017/12/7 0007.
 */

public class CameraView extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private Context context;
    private Camera camera;

    private OnFaceDetectedListener onFaceDetectedListener;

    /**
     * 摄像头最大的预览尺寸
     */
    private Camera.Size maxPreviewSize;

    public CameraView(Context context) {
        super(context);
        init(context);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        getHolder().addCallback(this);
    }

    /** 摄像头重新开始预览 */
    public void reset() {
        if(camera != null) {
            camera.setPreviewCallback(this);
            camera.startPreview();
                try {
                    camera.startFaceDetection();
                }catch (Exception e){
                    Log.e("info1","Exception .....");
                }
        }
    }

    public void destroy(){
        if (camera != null) {
            camera.stopPreview();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            if(camera != null) {
                camera.setDisplayOrientation(90);
                camera.setPreviewDisplay(holder);
                camera.setPreviewCallback(this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (camera != null) {
            maxPreviewSize = getMaxPreviewSize(camera);
            if (maxPreviewSize != null) {
                ViewGroup.LayoutParams params = getLayoutParams();
                Point point = getScreenSize();
                params.width = point.x;
                params.height = maxPreviewSize.width * point.x / maxPreviewSize.height;
                setLayoutParams(params);
                Camera.Parameters parameters = camera.getParameters();
                parameters.setPreviewSize(maxPreviewSize.width, maxPreviewSize.height);
                camera.setParameters(parameters);

                camera.startPreview();
                if(parameters.getMaxNumDetectedFaces() > 0){
                    try {
                        camera.startFaceDetection();
                    }catch (Exception e){
                        Log.e("info1","Exception .....");
                    }
                }
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null) {
            try {
                camera.stopFaceDetection();
                camera.stopPreview();
                camera.setPreviewDisplay(null);
                camera.setPreviewCallback(null);
                camera.release();
                camera = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取手机屏幕的尺寸
     *
     * @return
     */
    private Point getScreenSize() {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        return new Point(outMetrics.widthPixels, outMetrics.heightPixels);
    }

    /**
     * 获取摄像头最大的预览尺寸
     *
     * @param camera
     * @return
     */
    private Camera.Size getMaxPreviewSize(Camera camera) {
        List<Camera.Size> list = camera.getParameters().getSupportedPreviewSizes();
        if (list != null) {
            int max = 0;
            Camera.Size maxSize = null;
            for (Camera.Size size : list) {
                int n = size.width * size.height;
                if (n > max) {
                    max = n;
                    maxSize = size;
                }
            }
            return maxSize;
        }
        return null;
    }

    @Override
    public void onPreviewFrame(final byte[] data, Camera camera) {
        camera.setFaceDetectionListener(new Camera.FaceDetectionListener() {
            @Override
            public void onFaceDetection(Camera.Face[] faces, Camera camera) {
//                Log.e("info1","onFaceDetection .....");
                if(onFaceDetectedListener != null){
                    onFaceDetectedListener.onFaceDetected(faces, camera);
                }
            }
        });
    }

    /**
     * 检测到人脸的监听器
     */
    public interface OnFaceDetectedListener {
        void onFaceDetected(Camera.Face[] faces, Camera camera);
    }

    /**
     * setFaceDetectionListener
     * 设置监听器，监听检测到人脸的动作
     */
    public void setOnFaceDetectedListener(OnFaceDetectedListener listener) {
        if (listener != null) {
            this.onFaceDetectedListener = listener;
        }
    }
}
