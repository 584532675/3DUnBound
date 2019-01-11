package com.unbounded.video.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.stereo.GlSurfaceView.MoviePlayGLSurfaceView;
import com.stereo.NativeUtility.Decrypt;
import com.stereo.util.SettingUtils;
import com.unbounded.video.BaseActivity;
import com.stereo.video.MoviePlayer;
import com.stereo.video.MoviePlayerCallListener;
import com.unbounded.video.R;
import com.unbounded.video.adapter.PoplvAdapter;
import com.unbounded.video.bean.VideoHistory;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.service.DownloadService;
import com.unbounded.video.subtitles.SubtitlesCoding;
import com.unbounded.video.subtitles.SubtitlesModel;
import com.unbounded.video.utils.HttpGetUtil;
import com.unbounded.video.utils.JsonUtil;
import com.unbounded.video.utils.SharedPrefsUtil;
import com.unbounded.video.utils.ToastUtil;
import com.unbounded.video.utils.facedetector.CameraView;
import com.zhuge.analysis.stat.ZhugeSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

//电影播放界面
public class MoviePlayActivity extends BaseActivity implements MoviePlayerCallListener {
    public final static int SRT_FLAG = 666666;
    static final int Five_Minutes = 5*60*1000;
    static final int Voice_Type = 0;
    static final int Light_Type = 1;
    static final int YOUTUBE_RESOLVE = 3;
    int PostHandler = 500;
    Context context;

    private  static final String TAG=MoviePlayActivity.class.getSimpleName();
    private  static final int TITLE_GONE = 0;
    private static final int SUBTITLE_UPDATE = 12;
    MoviePlayer moviePlayer;
    Decrypt decrypt;
    MoviePlayGLSurfaceView moviePlayGLSurfaceView;
    private Uri mUri;
    private ImageView iv_eyes_tacking;
    private boolean isPlay3D;

    RelativeLayout titlerela,bottomrela,voicerela;
    TextView movieNametv,currenttimetv,totaltimetv,modeltv,formattv,voicenametv,voicevaluetv,subtv;
    PopupWindow formatpop,modelpop;
    View formatview,modelview;
    ProgressBar pb;
    Button backbtn;
    ImageView playiv,voiceimg;
    SeekBar seekBar;
    MediaPlayer mediaPlayer;
    String playType,urlOrPath,playName,report,phoneNum,subtitle;
    VideoHistory videoHistory;
    int type2dor3d;
    int firstIn = 1,finishView = 0, onresume = 0, onpause = 0;
    int ownPause;
    int filmDuration;
    List<VideoHistory> historylist = new ArrayList<VideoHistory>();
    String historyJson;

    List<String> formatpoplist = new ArrayList<String>();
    List<String> modelpoplist = new ArrayList<String>();
    NumberFormat numberFormat = NumberFormat.getInstance();
    /*解析youtube参数post请求key和value*/
    List<String>key = new ArrayList<>();
    List<String>value = new ArrayList<>();
    /*解析youtube参数地址*/
    int moveType;
    int lightint,mCurrentLight, newlight,maxAudio,mCurrentAudio;
    float startY;
    AudioManager am;
    private ArrayList<SubtitlesModel> list = new ArrayList<>();
    CameraView cameraView;
    private boolean isYouTube;
    private boolean isCalibration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isFullScreen = true;
        statusColor = false;
        context = MoviePlayActivity.this;

        /**
         *
         *
         * 播放总次数
         */
        phoneNum = SharedPrefsUtil.getValue(getApplicationContext(), Constants.Phone_FLAG, Constants.Phone_default);
        JSONObject personObject = null;
        try {
            personObject = new JSONObject();
            personObject.put("phoneNum", phoneNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ZhugeSDK.getInstance().track(getApplicationContext(),"播放总次数",personObject);

        //播放界面一些参数设置
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        numberFormat.setMaximumFractionDigits(2);

        lightint = getScreenBrightness();

        if(lightint <= 4){
            mCurrentLight = 0;
        }else{
            lightint = lightint - 4;
            float c = (float)lightint / 25.0f;
            BigDecimal bb = new BigDecimal(String.valueOf(c)).setScale(0, BigDecimal.ROUND_HALF_UP);
            mCurrentLight = Integer.valueOf(bb.toString());

        }
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getContentView() {
        return com.unbounded.video.R.layout.activity_movie_play;
    }

    @Override
    public void init(Intent intent) {
        super.init(intent);
        setTitleViewVisible(false);

        am = (AudioManager)getSystemService(AUDIO_SERVICE);
        //最大音量
        maxAudio = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        initMplay(intent);
        initMethod();
    }

    private void initMplay(Intent intent){
        playType = intent.getStringExtra("playType");
        urlOrPath = intent.getStringExtra("urlOrPath");
        isCalibration = intent.getBooleanExtra("isCalibration",false);
//        urlOrPath = "http://edu.moyansz.com/Act-ss-mp4-hd/a593778781bf4140ab19f72000e5e950/flight.mp4";
        playName = intent.getStringExtra("playName");
        if (SharedPrefsUtil.getValue(this, Constants.Mopic, false)) {
//            try {
//                Intent intent1 = new Intent();
//                intent1.putExtra("URL", urlOrPath);
//                intent1.setClassName("com.mopic3d.mplayer3d", "com.mopic3d.mplayer3d.ExternalPlayerActivity");
//                startActivity(intent1);
//                finish();
//            } catch (ActivityNotFoundException e) {
//                Intent intent1 = new Intent(Intent.ACTION_VIEW);
//                intent1.setData(Uri.parse("market://search?q=Mplayer3D"));
//                startActivity(intent1);
//                finish();
//            }
            Intent intent1 = new Intent(this, ExternalPlayerActivity.class);
            intent1.putExtra("urlOrPath", urlOrPath);
            intent1.putExtra("playName", getResources().getString(R.string.word_jiaozhun_video));
            startActivity(intent1);
            finish();
        }
        //获取播放视频的MoviePlayGLSurfaceView
        moviePlayGLSurfaceView = (MoviePlayGLSurfaceView)findViewById(com.stereo.videosdk.R.id.movie_view);
        iv_eyes_tacking = (ImageView)findViewById(com.unbounded.video.R.id.iv_eyes_tacking);

        //在线   当在线视频详情进入的，就要判断视频是否下载了
        if("online".equals(playType)){
            type2dor3d = intent.getIntExtra("type2dor3d", 0);
            subtitle = intent.getStringExtra("subtitle");
            videoHistory = (VideoHistory) intent.getSerializableExtra("videoHistory");

            //检查本地是否已有该视频
            if(videoIsHave(urlOrPath) == true){
                urlOrPath = DownloadService.getVideoDownPath(urlOrPath);

            }else{
                report = intent.getStringExtra("report");

            }

        }
    }

    /**
     * 判断是否有前置摄像
     * @return
     */
    public boolean hasFrontCamera(){
        Camera.CameraInfo info = new Camera.CameraInfo();
        int count = Camera.getNumberOfCameras();
        for(int i = 0; i < count; i++){
            Camera.getCameraInfo(i, info);
            if(info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
                return true;
            }
        }
        return false;
    }

    /**
     *
     */
    private void initcameraView(){
        cameraView = (CameraView) findViewById(com.unbounded.video.R.id.movie_cameraview);
        cameraView.setOnFaceDetectedListener(new CameraView.OnFaceDetectedListener() {
            @Override
            public void onFaceDetected(Camera.Face[] faces, Camera camera) {
                //检测到人脸后的回调方法
                if (null == faces || faces.length == 0) {
                    Log.e("info1", "There is no face.");
                    iv_eyes_tacking.setImageResource(com.unbounded.video.R.mipmap.eyes_not_tracking);

                } else {
                    Log.e("info1", "onFaceDetection : At least one face has been found.");
                    iv_eyes_tacking.setImageResource(com.unbounded.video.R.mipmap.eyes_tracking);

                }
            }
        });

    }

    /**
     * 检查视频文件是否已有
     */
    private boolean videoIsHave(String url){
        String url1 = url;
        boolean videoHave = false;
        String path = DownloadService.getVideoDownPath(url1);
        File file = new File(path);
        if(file.exists()){
            videoHave = true;
        }

        return videoHave;
    }
    /**
     * 检查视频缓存文件是否已有
     */
    private boolean videoTempIsHave(String url){
        String url1 = url;
        boolean videoTempHave = false;
        String path = DownloadService.getVideoTempPath(url1);
        File file = new File(path);
        if(file.exists()){
            videoTempHave = true;
        }

        return videoTempHave;
    }


    //
    private void initMethod(){
        //根据文件路径获取uri  "/mnt/sdcard/Movies/[4K] LG 3D Demo Fantasy World 4k ULTRA HD 2160p.mp4"
//        mUri= Uri.parse("http://edu.moyansz.com/Act-ss-mp4-sd/0c95d3b01b0f467badc383e1c990d33e/%E5%AE%87%E5%AE%99%E4%B8%AD%E7%9A%84%E5%9C%B0%E7%90%83_3D.mp4");

        mUri= Uri.parse(urlOrPath);
        //设置uri
        moviePlayGLSurfaceView.setUri(mUri);
        //获取视频播放对象
        moviePlayer= MoviePlayer.getInstance(this,moviePlayGLSurfaceView,mUri);
        //设置视频播放回调对象  目前对应下面三个接口
        moviePlayer.setMoviePlayerCallLister(this);
        isYouTube = getIntent().getBooleanExtra("isYouTube",false);
        if(isYouTube){
            String youtubeUrl = /*HttpConstants.Resolve_video + mUri*/"https://api.lylares.com/video/youtube/?AppKey="+Constants
                    .YouTubeKye+"&url=";

//            HttpGetUtil getyoutube = new HttpGetUtil(handler, youtubeUrl, YOUTUBE_RESOLVE);
//            key.clear();
//            value.clear();
//            key.add("url");
//            value.add(mUri.toString());
            HttpGetUtil getyoutube = new HttpGetUtil(handler, youtubeUrl + mUri, YOUTUBE_RESOLVE);
            Log.v("ThreeDDFragment", "youtubeUrl：" + youtubeUrl + mUri);
            Thread threadYoutube = new Thread(getyoutube);
            threadYoutube.start();
        }
    }

    @Override
    public void initView() {
        super.initView();

        //注册人眼追踪刷新图标广播接收器
        IntentFilter filter= new IntentFilter();
        filter.addAction("com.android.action.update");
//        registerReceiver(updateReceiver , filter);

        formatpoplist.clear();
        formatpoplist.add("2D视频");
        formatpoplist.add("左右视频");
        formatpoplist.add("上下视频");
        formatPop();

        titlerela = (RelativeLayout) findViewById(com.unbounded.video.R.id.movie_titlerela);
        bottomrela = (RelativeLayout) findViewById(com.unbounded.video.R.id.movie_bottomrela);
        seekBar = (SeekBar) findViewById(com.unbounded.video.R.id.movie_seekbar);
        movieNametv = (TextView) findViewById(com.unbounded.video.R.id.movie_nametv);
        currenttimetv = (TextView) findViewById(com.unbounded.video.R.id.movie_currenttimetv);
        totaltimetv = (TextView) findViewById(com.unbounded.video.R.id.movie_totaltimetv);
        playiv = (ImageView) findViewById(com.unbounded.video.R.id.movie_playbtn);
        backbtn = (Button) findViewById(com.unbounded.video.R.id.movie_backbtn);
        formattv = (TextView) findViewById(com.unbounded.video.R.id.movie_formattv);
        modeltv = (TextView) findViewById(com.unbounded.video.R.id.movie_modeltv);
        pb = (ProgressBar) findViewById(com.unbounded.video.R.id.movie_pb);
        subtv = (TextView) findViewById(com.unbounded.video.R.id.movie_subtv);

        //voicerela,voiceimg,voicenametv,voicevaluetv
        voicerela = (RelativeLayout) findViewById(com.unbounded.video.R.id.voicedia_rela);
        voiceimg = (ImageView) findViewById(com.unbounded.video.R.id.voicedia_imgiv);
        voicenametv = (TextView) findViewById(com.unbounded.video.R.id.voicedia_nametv);
        voicevaluetv = (TextView) findViewById(com.unbounded.video.R.id.voicedia_valuetv);


        movieNametv.setText(playName);

        /**
         以下是调试界面
         */
        //人眼追踪图标初始化和更新
        if (SettingUtils.isShowTrackingStatus(this)) {
            iv_eyes_tacking.setImageResource(com.unbounded.video.R.mipmap.eyes_tracking);
        } else {
            iv_eyes_tacking.setImageResource(com.unbounded.video.R.mipmap.eyes_not_tracking);
        }

    }

    @Override
    public void initParams() {
        super.initParams();

        ViewGroup.LayoutParams bottomrelaparams = bottomrela.getLayoutParams();
        bottomrelaparams.height = screenWidth * 220 / 1080;
        bottomrela.setLayoutParams(bottomrelaparams);
    }

    @Override
    public void initEvent() {
        super.initEvent();

        //检查视频格式函数  在播放之前需要调用此方法进行视频格式检测并保存对应的格式  value -2:该视频不能播放; -1:出错; 0:2D视频 1:左右3D视频 2:上下3D视频
        // int value= Utils.naviveCheckVideo("/mnt/sdcard/Movies/[4K] LG 3D Demo Fantasy World 4k ULTRA HD 2160p.mp4",moviePlayGLSurfaceView);


        //只有当激活后 才能调用mPictureView.setShowModel(1)切换成3d模式  否则会报错 其他界面也是类似
        isPlay3D = SharedPrefsUtil.getValue(this, com.stereo.util.Constants.PALY_3D, false);
        Log.i(TAG, "onStart:  isPlay3D "+isPlay3D);
        if (!isPlay3D){
            //无激活状态下初始化播放状态图标为2D并隐藏人眼追踪图标和关闭人眼追踪功能
            if (iv_eyes_tacking.getVisibility() == View.VISIBLE) {
                iv_eyes_tacking.setVisibility(View.GONE);//变成不追踪
                //moviePlayer.switchMode(0);
            }
        }

        formattv.setOnClickListener(this);
        modeltv.setOnClickListener(this);
        playiv.setOnClickListener(this);
        backbtn.setOnClickListener(this);
        moviePlayGLSurfaceView.setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                currenttimetv.setText(formatTime(progress));

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
//                handler.removeCallbacks(updateThread);

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                pb.setVisibility(View.VISIBLE);
                mediaPlayer.seekTo(seekBar.getProgress());
//                handler.post(updateThread);
            }
        });

        //
        moviePlayGLSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if(titlerela.getVisibility() == View.VISIBLE){
                            titleView(false);

                        }else{
                            if(firstIn == 0){
                                titleView(true);
                            }
                        }

                        float downx = event.getX();
                        startY = event.getY();
                        //当前音量
                        mCurrentAudio = am.getStreamVolume(AudioManager.STREAM_MUSIC);

                        if(downx < screenHeight/2){
                            moveType = Light_Type;
                        }else if(downx > screenHeight/2){
                            moveType = Voice_Type;
                        }


                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (event.getPointerCount() == 1) {
                            float endY = event.getY();
                            float distanceY = endY - startY;

                            //改变声音 = （滑动屏幕的距离： 总距离）*音量最大值
                            float delta = (distanceY / screenWidth) * maxAudio;

                            //向上滑动
                            if(delta < 0){
                                //音量
                                if(moveType == Voice_Type && Math.abs(delta) >= 1.0f){
                                    int voice = mCurrentAudio + (int)Math.abs(delta);

                                    if(voice >= maxAudio){
                                        updateVoice(maxAudio, false);
                                    }else if(voice <= 0){
                                        updateVoice(0, false);
                                    }else{
                                        updateVoice(voice, false);
                                    }
                                //亮度
                                }else if(moveType == Light_Type && Math.abs(delta) >= 1.0f){
                                    newlight = mCurrentLight + (int)Math.abs(delta);

                                    if(newlight >= 10){
                                        lightint = 10 * 25;
                                        setLight(MoviePlayActivity.this, 10, lightint);
                                    }else if(newlight < 10 && newlight > 0){
                                        lightint = newlight * 25;
                                        setLight(MoviePlayActivity.this, newlight, lightint);
                                    }else if(newlight == 0){
                                        lightint = 4;
                                        setLight(MoviePlayActivity.this, newlight, lightint);
                                    }
                                }

                            //向下滑动
                            }else if(delta > 0){
                                //音量
                                if(moveType == Voice_Type && Math.abs(delta) >= 1.0f){
                                    int voice = mCurrentAudio - (int)Math.abs(delta);

                                    if(voice >= maxAudio){
                                        updateVoice(maxAudio, false);
                                    }else if(voice <= 0){
                                        updateVoice(0, false);
                                    }else{
                                        updateVoice(voice, false);
                                    }
                                //亮度
                                }else if(moveType == Light_Type && Math.abs(delta) >= 1.0f){
                                    newlight = mCurrentLight - (int)Math.abs(delta);

                                    if(newlight <= 10 && newlight > 0){
                                        lightint = newlight * 25;
                                        setLight(MoviePlayActivity.this, newlight, lightint);
                                    }else if(newlight == 0){
                                        lightint = 4;
                                        setLight(MoviePlayActivity.this, newlight, lightint);
                                    }
                                }
                            }
                        }else{
                            voicerela.setVisibility(View.GONE);
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        voicerela.setVisibility(View.GONE);
                        mCurrentLight = newlight;

                        break;
                }

                return true;
            }
        });


    }

    /**
     * 改变屏幕亮度
     */
    private void setLight(Activity context, int brightness, int light) {
        voicerela.setVisibility(View.VISIBLE);
        voiceimg.setImageResource(com.unbounded.video.R.mipmap.lighticon);
        voicenametv.setText(com.unbounded.video.R.string.word_light);

        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(light) * (1f / 255f);
        context.getWindow().setAttributes(lp);

        voicevaluetv.setText(brightness + "");

    }
    /**
     * 获得当前屏幕亮度值 0--255
     */
    private int getScreenBrightness() {
        int screenBrightness = 255;
        try {
            screenBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception localException) {

        }
        return screenBrightness;
    }

    /**
     * 设置音量的大小
     *
     * @param progress
     */
    private void updateVoice(int progress, boolean isMute) {
        voicerela.setVisibility(View.VISIBLE);
        voiceimg.setImageResource(com.unbounded.video.R.mipmap.voiceicon);
        voicenametv.setText(com.unbounded.video.R.string.word_voice);

        if (isMute) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);

        } else {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            voicevaluetv.setText(progress + "");
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case com.unbounded.video.R.id.movie_backbtn:
                finish();
                break;
            case com.unbounded.video.R.id.movie_playbtn:
                playMethod();

                break;
            //视频格式
            case com.unbounded.video.R.id.movie_formattv:
                formatpop.showAsDropDown(formattv);

                break;
            //视频模式
            case com.unbounded.video.R.id.movie_modeltv:
                isPlay3D = SharedPrefsUtil.getValue(this, com.stereo.util.Constants.PALY_3D, false);
                modelPop();
                modelpop.showAsDropDown(modeltv);
                break;
            //
            case com.unbounded.video.R.id.movie_view:
//                if(titlerela.getVisibility() == View.VISIBLE){
//                    titleView(false);
//
//                }else{
//                    if(firstIn == 0){
//                        titleView(true);
//                    }
//                }

                break;

        }
    }

    /**
     * title和底部区域显示隐藏
     */
    private void titleView(boolean isview){
        if(isview == true){
            titlerela.setVisibility(View.VISIBLE);
            bottomrela.setVisibility(View.VISIBLE);

            handler.removeMessages(TITLE_GONE);
            handler.post(updateThread);
            handler.sendEmptyMessageDelayed(TITLE_GONE, 8*oneMinute);
        }else{
            titlerela.setVisibility(View.GONE);
            bottomrela.setVisibility(View.GONE);

            if(formatpop != null){
                formatpop.dismiss();
            }
            if(modelpop != null){
                modelpop.dismiss();
            }
        }
    }

    /**
     * 模式pop
     */
    private void modelPop(){
        modelview = this.getLayoutInflater().inflate(com.unbounded.video.R.layout.switchbtnspop, null);
        modelpop = new PopupWindow(modelview,43*oneDp, ViewGroup.LayoutParams.WRAP_CONTENT);
        modelpop.setFocusable(true);
        modelpop.setOutsideTouchable(true);
        modelpop.setBackgroundDrawable(new BitmapDrawable());

        modelpoplist.clear();
        if(isPlay3D == true){
            modelpoplist.add("2D");
            modelpoplist.add("3D");
        }else {
            modelpoplist.add("2D");
        }
        ListView modellv = (ListView) modelview.findViewById(com.unbounded.video.R.id.switchbtns_lv);

        ViewGroup.LayoutParams lvparams = modellv.getLayoutParams();
        lvparams.height = 40*modelpoplist.size()*oneDp + modelpoplist.size()-1;
        modellv.setLayoutParams(lvparams);

        PoplvAdapter poplvAdapter = new PoplvAdapter(MoviePlayActivity.this, modelpoplist);
        modellv.setAdapter(poplvAdapter);

        modellv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                int playingBoolean;
                if(moviePlayer.isPlaying()){
                    playingBoolean = 1;
                }else{
                    playingBoolean = 0;
                }

                modeltv.setText(modelpoplist.get(position));
                moviePlayer.switchMode(position);
                if(moviePlayer != null && playingBoolean == 0){
                    moviePlayer.onPause();
                }
                modelpop.dismiss();

                if("2D".equals(modelpoplist.get(position))){
                    iv_eyes_tacking.setVisibility(View.GONE);
                    if(cameraView != null){
                        cameraView.destroy();
                    }
                }else if("3D".equals(modelpoplist.get(position))){
                    iv_eyes_tacking.setVisibility(View.VISIBLE);
                    if(cameraView != null){
                        cameraView.reset();
                    }
                }
            }
        });
    }

    /**
     * 视频格式
     */
    private void formatPop(){
        formatview = this.getLayoutInflater().inflate(com.unbounded.video.R.layout.switchbtnspop, null);
        formatpop = new PopupWindow(formatview,73*oneDp, ViewGroup.LayoutParams.WRAP_CONTENT);
        formatpop.setFocusable(true);
        formatpop.setOutsideTouchable(true);
        formatpop.setBackgroundDrawable(new BitmapDrawable());

        ListView formatlv = (ListView) formatview.findViewById(com.unbounded.video.R.id.switchbtns_lv);

        ViewGroup.LayoutParams lvparams = formatlv.getLayoutParams();
        lvparams.height = 40*formatpoplist.size()*oneDp + 2;
        formatlv.setLayoutParams(lvparams);

        PoplvAdapter poplvAdapter = new PoplvAdapter(MoviePlayActivity.this, formatpoplist);
        formatlv.setAdapter(poplvAdapter);

        formatlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                int playingBoolean;
                if(moviePlayer.isPlaying()){
                    playingBoolean = 1;
                }else{
                    playingBoolean = 0;
                }
                formattv.setText(formatpoplist.get(position));
                moviePlayer.switchFormat(position);
                if(moviePlayer != null && playingBoolean == 0){
                    moviePlayer.onPause();
                }

                formatpop.dismiss();

            }
        });

    }

    //播放按钮方法
    private void playMethod(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            playiv.setImageResource(com.unbounded.video.R.mipmap.whiteplaybtn1);
            handler.removeCallbacks(updateThread);
            ownPause = 1;
        }else {
            mediaPlayer.start();
            playiv.setImageResource(com.unbounded.video.R.mipmap.whitepausebtn1);
            handler.post(updateThread);
            ownPause = 0;
        }
    }


    //刷新人眼追踪刷新图标
    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.android.action.update")) {
                if (SettingUtils.isShowTrackingStatus(MoviePlayActivity.this)) {
                    iv_eyes_tacking.setImageResource(com.unbounded.video.R.mipmap.eyes_tracking);
                } else {
                    iv_eyes_tacking.setImageResource(com.unbounded.video.R.mipmap.eyes_not_tracking);
                }
            }
        }
    };



    @Override
    public void onStart() {
        Log.e("info", "onStart: ");
        //使其他媒体类播放暂停
        ((AudioManager) getSystemService(AUDIO_SERVICE)).requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        super.onStart();
    }

    @Override
    protected void onStop() {

        handler.removeMessages(SRT_FLAG);
        handler.removeCallbacks(updateThread);

        onresume = 0;

        ((AudioManager) getSystemService(AUDIO_SERVICE)).abandonAudioFocus(null);
        super.onStop();
        Log.e("info", "onStop: ");
    }

    @Override
    public void onPause() {

        handler.removeMessages(SRT_FLAG);
        handler.removeCallbacks(updateThread);

        onresume = 0;
        onpause = 1;

        if(mediaPlayer != null && "online".equals(playType)) {
//            mediaPlayer.seekTo(Five_Minutes);
            String today = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
            VideoHistory videoHis1;

            if(finishView == 1){
                videoHis1 = new VideoHistory(videoHistory.getId(), videoHistory.getHeadImg(),
                        videoHistory.getAuthorName(),videoHistory.getVideoImg(),videoHistory.getVideoName(),today,
                        videoHistory.getVideoUrl(), 0, 4, -1);
            }else{
                videoHis1 = new VideoHistory(videoHistory.getId(), videoHistory.getHeadImg(),
                        videoHistory.getAuthorName(),videoHistory.getVideoImg(),videoHistory.getVideoName(),today,
                        videoHistory.getVideoUrl(), 0, 4, mediaPlayer.getCurrentPosition());

            }

            addHistory(videoHis1);
        }


        if(mediaPlayer != null){
                mediaPlayer.pause();

//            moviePlayer.onPause();

        }
        if (cameraView != null) {
            cameraView.destroy();
        }
        super.onPause();
        Log.e("info","onPause......");
    }

    /**
     * 添加进历史记录
     */
    private void addHistory(VideoHistory videoHis1){
        //首先播放要判断该视频是否在记录列表中
        historyJson = SharedPrefsUtil.getValue(getApplicationContext(), Constants.VIDEOHISTORY_FLAG, "");
        Log.e("info","历史记录111="+ historyJson);
        //首先没有记录
        if(TextUtils.isEmpty(historyJson)){
            //直接把记录加进去
            historylist.add(videoHis1);

            //如果有记录，要判断是否有一样的
        }else {
            historylist = JsonUtil.historyJsonToList(historyJson);
            for(int i=0;i<historylist.size();i++){
                //记录中有,删除以前的，插入新的
                if(videoHis1.getId().equals(historylist.get(i).getId())){
                    historylist.remove(i);

                    //新的插到第一个
                    historylist.add(0, videoHis1);
                    break;
                    //当遍历到最后一个都没有相同记录时
                }else if(i == historylist.size() - 1){
                    //直接把记录加进去，插到第一个
                    historylist.add(0, videoHis1);
                    break;
                }
            }
        }
        //list存好后，转成json存起来
        historyJson = JsonUtil.objectToJson(historylist);
        SharedPrefsUtil.putValue(MoviePlayActivity.this, Constants.VIDEOHISTORY_FLAG, historyJson);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e("info","onResume......");

        if(onpause == 1 && pb != null){
            if(pb.getVisibility() == View.GONE){
                pb.setVisibility(View.VISIBLE);
            }
        }

        if(cameraView != null){
            cameraView.reset();
        }

        firstIn = 1;//息屏一进来不让进度条显示
        onresume = 1;

    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(updateThread);
        handler.removeMessages(SUBTITLE_UPDATE);
        //rjz 注释，第一次校准完成后，播放视频退出会崩溃 start
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            moviePlayer.stopControlHandler();
//            moviePlayer.onDestroy();
        }
        //rjz 注释，第一次校准完成后，播放视频退出会崩溃 end
//        if (mediaPlayer != null) {
//            moviePlayer.onDestroy();
//        }

//        unregisterReceiver(updateReceiver);
        decrypt = Decrypt.getDecrypt(this);
        decrypt.onEGLDestroy();
        Log.e("info","onDestroy......");
        super.onDestroy();
    }


    @Override
    public void OnErrorNotSupport(int i, int i1) {
        if (!mUri.toString().contains("www.youtube.com/watch?v=")) {
            Log.v("isYoutu","return："+mUri.toString());
            if(isYouTube){
                ToastUtil.showToast(this, "播放失败" , ToastUtil.CENTER);
            }else{
                ToastUtil.showToast(this, "格式不支持" , ToastUtil.CENTER);
            }

        }else{
            Log.v("isYoutu","Unfinished analysis");
        }
        if(mediaPlayer != null){
            Log.e("info","OnErrorNotSupport mediaPlayer != null");
        }else{
            Log.e("info","OnErrorNotSupport mediaPlayer == null");
        }
    }

    //mediaplayer播放失败回调的函数
    @Override
    public void OnError(int what, int extra) {
        if (!isYouTube && !mUri.toString().contains("www.youtube.com/watch?v=")) {
            Log.v("isYoutu","return");
            ToastUtil.showToast(this, "播放失败" , ToastUtil.CENTER);
        }

        if(mediaPlayer != null){
            Log.e("info","OnError mediaPlayer != null");
        }else{
            Log.e("info","OnError mediaPlayer == null");
        }

//        initMplay(getIntent());
//        initMethod();

    }

    SubtitlesModel subtitlesM;

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TITLE_GONE:
                    titleView(false);
                    break;
                case SRT_FLAG:
                    if(list != null && !list.isEmpty()){
                        try {
                            subtitlesM = searchSub(mediaPlayer.getCurrentPosition());
                            if(subtitlesM != null){
                                subtv.setText(subtitlesM.getContextC());

                            }else{
                                subtv.setText("");
                            }
                        }catch (IllegalStateException e){
                            Log.e("IllegalStateException","MediaPlayer.getCurrentPosition");
                        }

                    }else{

                    }

                    handler.sendEmptyMessageDelayed(SRT_FLAG, 800);

                    break;
                case SUBTITLE_UPDATE:
                    try {
                        ArrayList<SubtitlesModel> SubtitlesModellist = (ArrayList<SubtitlesModel>) msg.obj;
                        if(SubtitlesModellist != null && !SubtitlesModellist.isEmpty()){
                            list = SubtitlesModellist;

                        }else{

                        }

                    } catch (ConcurrentModificationException e) {
                      Log.i(TAG,"e "+e.getMessage());
                    }

                    handler.sendEmptyMessageDelayed(SRT_FLAG, 800);
                    break;
                case Constants.INTERNET_ERROR_FLAG:
                    ToastUtil.showToast(MoviePlayActivity.this, com.unbounded.video.R.string.internet_error, ToastUtil.CENTER);
                    finish();

                    break;
                case YOUTUBE_RESOLVE:

                    String freeYoutube = msg.obj.toString();
                    Log.v("ThreeDDFragment", "freeYoutube:" + freeYoutube);
                    if (!(TextUtils.isEmpty(freeYoutube))) {
                        try {
                            JSONObject mainjo = new JSONObject(freeYoutube);
                            JSONObject youTubeData = mainjo.getJSONObject("data");
                            JSONObject objectH = youTubeData.getJSONObject("18");/*18分辨率为360,22分辨率为720*/
                            String youTubeUrl = objectH.getString("url");
                            if (!TextUtils.isEmpty(youTubeUrl)) {
                                Log.v("ThreeDDFragment", "youTubeResolveUrl:" + youTubeUrl);
                                mUri= Uri.parse(youTubeUrl);
                                moviePlayer.playVideo(mUri);
                            } else {
                                ToastUtil.showToast(MoviePlayActivity.this, "解析失败，请重试，或者跟换视频", Toast.LENGTH_LONG);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    break;
            }
        }
    };

    /**
     * 解析字幕list按时间显示
     */
    public SubtitlesModel searchSub(int key)
    {
        int start = 0;
        int end = list.size() - 1;
        while (start <= end)
        {
            int middle = (start + end) / 2;
            if (key < list.get(middle).star)
            {
                if (key > list.get(middle).end)
                {
                    return list.get(middle);
                }
                end = middle - 1;
            }
            else if (key > list.get(middle).end)
            {
                if (key < list.get(middle).star)
                {
                    return list.get(middle);
                }
                start = middle + 1;
            }
            else if (key >= list.get(middle).star && key <= list.get(middle).end)
            {
                return list.get(middle);
            }
        }
        return null;
    }

    Runnable updateThread = new Runnable() {
        public void run() {
                // 获得歌曲现在播放位置并设置成播放进度条的值
                if (mediaPlayer != null && mediaPlayer.isPlaying()  && moviePlayer.isPlaying()) {
                    //免费
                    if(Constants.Free_Flag.equals(report) || TextUtils.isEmpty(report)){
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());

                        //收费
                    }else if(Constants.NotFree_Flag.equals(report)){
//                    Log.e("info","mediaPlayer.getDuration()="+mediaPlayer.getDuration());
                        if(filmDuration < Five_Minutes){
                            seekBar.setProgress(mediaPlayer.getCurrentPosition());
                        }else{
                            if(mediaPlayer.getCurrentPosition() <= Five_Minutes){
                                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                            }else{
                                mediaPlayer.stop();
                                finish();
                            }
                        }
                    }

                    // 每次延迟500毫秒再启动线程
                    handler.postDelayed(updateThread, PostHandler);
                }




        }
    };

    //mediaplayer播放解析完成的函数
    @Override
    public void OnPrepared() {
//        ToastUtil.showToast(this, "播放解析完成" , ToastUtil.CENTER);
        Log.e("info","OnPrepared ......");

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();//如果为true，则表示屏幕“亮”了，否则屏幕“暗”了。

        if(isScreenOn == true){
            Log.e("info","light ....");
            Log.e("info","onresume ....=="+onresume);
            if(onresume == 1){
                preparedInit();
                onresume = 0;
            }else{
                mediaPlayer = moviePlayer.getmMediaPlayer();
                if(mediaPlayer != null){
                    mediaPlayer.pause();
                }
            }
        }else{
            Log.e("info","dark ....");
            if(mediaPlayer != null){
                mediaPlayer.pause();
            }

        }
    }

    /**
     * 解析准备完成
     */
    private void preparedInit(){
        Log.e("info","preparedInit......");
        if(hasFrontCamera()) {
            initcameraView();
        }

        pb.setVisibility(View.GONE);

        mediaPlayer = moviePlayer.getmMediaPlayer();

        if(!(TextUtils.isEmpty(subtitle))){
            SubtitlesCoding subtitlesCoding = new SubtitlesCoding(handler, subtitle);
            Thread thread = new Thread(subtitlesCoding);
            thread.start();
        }

        if(mediaPlayer != null){
            filmDuration = mediaPlayer.getDuration();
        }


        //免费
        if(Constants.Free_Flag.equals(report) || TextUtils.isEmpty(report)){
            totaltimetv.setText(formatTime(filmDuration));
            seekBar.setMax(filmDuration);

        //收费
        }else if(Constants.NotFree_Flag.equals(report)){
            if(filmDuration < Five_Minutes){
                totaltimetv.setText(formatTime(filmDuration));
                seekBar.setMax(filmDuration);
            }else{
                totaltimetv.setText(formatTime(Five_Minutes));
                seekBar.setMax(Five_Minutes);
            }
        }


        handler.post(updateThread);

        if("online".equals(playType)){
            moviePlayer.switchFormat(type2dor3d);
            formattv.setText(formatpoplist.get(type2dor3d));

            isPlay3D = SharedPrefsUtil.getValue(this, com.stereo.util.Constants.PALY_3D, false);

            if(isPlay3D == true){
                modeltv.setText("3D");
                moviePlayer.switchMode(1);
                iv_eyes_tacking.setVisibility(View.VISIBLE);

            }else{
                modeltv.setText("2D");
                moviePlayer.switchMode(0);
                iv_eyes_tacking.setVisibility(View.GONE);

                cameraView.destroy();
            }

            String historyStr = SharedPrefsUtil.getValue(getApplicationContext(), Constants.VIDEOHISTORY_FLAG, "");
            Log.e("info","historyStr="+historyStr);
            if(!(TextUtils.isEmpty(historyStr))){
                historylist = JsonUtil.historyJsonToList(historyStr);
                for(int i=0;i<historylist.size();i++){
                    //记录中有
                    if(videoHistory.getId().equals(historylist.get(i).getId())){
                        mediaPlayer.seekTo(historylist.get(i).getPlayPosition());
                        ToastUtil.showToast(MoviePlayActivity.this, getString(com.unbounded.video.R.string.word_lastplay) + formatTime(historylist.get(i).getPlayPosition()), ToastUtil.BOTTOM);

                        break;
                        //当遍历到最后一个都没有相同记录时
                    }else if(i == historylist.size() - 1){
                        break;
                    }
                }
            }
        }else{

            if(isCalibration){
                moviePlayer.switchMode(0);
                modeltv.setText("2D");
                formattv.setText("2D视频");
                moviePlayer.switchFormat(0);
            }else{
                moviePlayer.switchFormat(0);
                formattv.setText(formatpoplist.get(0));
                modeltv.setText("3D");
                moviePlayer.switchMode(0);
            }
            iv_eyes_tacking.setVisibility(View.GONE);

            cameraView.destroy();

        }

        if(firstIn == 1){
            titleView(true);
        }

        firstIn = 0;

        /**
         * 缓冲监听
         */
        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
                switch (i) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        //开始缓存，暂停播放
                        Log.e("info","开始缓存，暂停播放");
                        handler.removeCallbacks(updateThread);
                        pb.setVisibility(View.VISIBLE);

                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        //缓存完成，继续播放
                        Log.e("info","缓存完成，继续播放");
                        handler.post(updateThread);
                        pb.setVisibility(View.GONE);

                        break;
                }

                return true;
            }
        });

        handler.sendEmptyMessageDelayed(SUBTITLE_UPDATE, 500);
    }

    //mediaplayer播放完成的函数
    @Override
    public void OnCompletion() {
//        ToastUtil.showToast(this, "播放完成" , ToastUtil.CENTER);
        /**
         * 播放完次数
         */
        phoneNum = SharedPrefsUtil.getValue(getApplicationContext(), Constants.Phone_FLAG, Constants.Phone_default);
        JSONObject personObject = null;
        try {
            personObject = new JSONObject();
            personObject.put("phoneNum", phoneNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ZhugeSDK.getInstance().track(getApplicationContext(),"播放完次数", personObject);
        finishView = 1;

        finish();
    }

    /**
     * 毫秒转换为  天 时  分  秒
     */
    public String formatTime(long mss) {
        String daysStr,hoursStr,minutesStr,secondsStr;

        /*
        long days = mss / (1000 * 60 * 60 * 24);
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        if(hours < 10){
            hoursStr = "0" + hours;
        }else {
            hoursStr = hours+"";
        }

        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        if(minutes < 10){
            minutesStr = "0" + minutes;
        }else {
            minutesStr = minutes+"";
        }

        long seconds = (mss % (1000 * 60)) / 1000;
        if(seconds < 10){
            secondsStr = "0" + seconds;
        }else {
            secondsStr = seconds+"";
        }
        */

        long minutes = mss / (1000 * 60);
        if(minutes < 10){
            minutesStr = "0" + minutes;
        }else {
            minutesStr = minutes+"";
        }

        long seconds = (mss % (1000 * 60)) / 1000;
        if(seconds < 10){
            secondsStr = "0" + seconds;
        }else {
            secondsStr = seconds+"";
        }

        return minutesStr + ":" + secondsStr;
    }

}
