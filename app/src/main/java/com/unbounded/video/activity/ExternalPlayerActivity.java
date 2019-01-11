package com.unbounded.video.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mopic3d.mplayer3d.ModelNum;
import com.mopic3d.mplayer3d.ShareParam;
import com.mopic3d.mplayer3d.SharedPreferenceManager;
import com.mopic3d.mplayer3d.SpriteEyeView;
import com.mopic3d.mplayer3d.contentprovider.DatabaseManager;
import com.mopic3d.mplayer3d.glrender.GLVideoView;
import com.mopic3d.mplayer3d.ht.HeadTracker;
import com.mopic3d.mplayer3d.ht.StartAndStopHt;
import com.mopic3d.mplayer3d.tune.TuningActivity;
import com.mopic3d.mplayer3d.utils.VideoMode;
import com.umeng.analytics.MobclickAgent;
import com.unbounded.video.R;
import com.unbounded.video.utils.DensityUtil;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;


public class ExternalPlayerActivity extends Activity implements SurfaceHolder.Callback {

    private static final String TAG = "ExternalPlayerActivity";
    private static final int UPDATE_PROGRESS = 1;
    private static final int FADE_OUT = 2;
    private MediaPlayer mPlayer;
    private Uri uri;
    private GLVideoView mVideoView;
    private double[] params;
    SurfaceHolder sfHoler;
    private HeadTracker mHeadTracker;

    private boolean mShowing;
    private TextView currentTime;
    private TextView totalTime;

    private int mWidth = 0;
    private int mHeight = 0;

    private float dpWidth;
    private float dpHeight;
    private int mModel = 0;
    private FrameLayout mFrameExternalPlayer;
    private View decorView;
    private int uiOption;
    private int progress;

    private boolean isShowOverlay = false;

    private LinearLayout linearPopupMenu;

    private Context mContext;
    private boolean isShowPopup = false;

    private boolean imageButton_FSBS_Click;
    private boolean imageButton_3D_Click;
    private boolean imageButton_LR_Click;
    private boolean imageButton_VR_Click = false;
    private boolean imageButton_SBSTB_Click;
    private int whereVideo = 0;
    private static final int EXTERNAL_URL = 0;
    private static final int EXTERNAL_PATH = 1;
    private boolean isPlayVideo = false;
    private boolean isShowHelp = false;
    private static final int OVERLAY_TIMEOUT = 3000;
    private static final int OVERLAY_INFINITE = 3600000;


    private SpriteEyeView mSpriteEyeView;
    private boolean mPause = false;

    boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
    boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);

    private String url;
    private String videoPath;
    private String mTitle;

    private static final int SETTING_RESULT = 0;

    private SharedPreferenceManager mPrefManager;

    //-------------menu dialog------------
    private Dialog menuDialog;
    private Button subTitleSettingBtn;
    private TextView _3dSettingBtn;
    private Button shareBtn;

    private ScaleGestureDetector mScaleGestureDetector;

    //------------overlay-----------
    private RelativeLayout playerOverlayLayout;
    private TextView exitBtn;
    private TextView playerNameTv;
    private ImageButton moreBtn;
    private ImageButton playAndPauseBtn;
    private SeekBar seekBar;
    private SeekBar m3DControlBar;
    private FrameLayout m3DControlBarLayout;

    private TextView mFSBSToggleBtn;
    private TextView m3DToggleBtn;
    private TextView m3DControlBtn;
    private TextView mVrToggleBtn;
    private TextView mLrToggleBtn;
    private TextView mUdlrToggleBtn;
    private ImageView mEyeTrackingBtn;
    private TextView mVrAutoBtn;

    private boolean isLocked = false;

    private int mYDisplayRange;
    private float mTouchY, mTouchX, mVol;

    private AudioManager mAudioManager;

    private boolean mVrAutoSelect = true;
    private boolean mIsVrOn = false;
    private boolean mIsFullOn = false; // 0:half, 1:full
    private float preX;
    private float preY;
    private int mAction;
    private final int TOUCH_NONE = 0;
    private int _3DControlValue;

    Float[] val;
    private float timestamp;
    private static final float NS2S = 1.0f / 1000000000.0f;
    private final float[] deltaRotationVector = new float[4];

    private boolean vrButtonMode = false;
    private boolean endVrMode = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mTitle = "";
        url = getIntent().getStringExtra("urlOrPath");
        mTitle = getIntent().getStringExtra("playName");
        progress = 0;
        if (url == null) {
            videoPath = getIntent().getDataString();
        }
        loadActivity();
    }

    private void loadActivity() {

        mContext = getApplicationContext();
        decorView = getWindow().getDecorView();
        uiOption = getWindow().getDecorView().getSystemUiVisibility();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            uiOption |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            uiOption |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOption);

        ModelNum modelNum = new ModelNum();
        mModel = modelNum.getModel();

        mPrefManager = new SharedPreferenceManager(getApplicationContext());
        Display display1 = getWindowManager().getDefaultDisplay();
        int screenOrientation = display1.getRotation();
        // S8 / S8Plus 화면 자름
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_external_player);
        DatabaseManager mDBManager = new DatabaseManager(getApplicationContext());
        mDBManager.getParametersFromDatabase();
        params = ShareParam.getinstance().getParams();
        mFrameExternalPlayer = (FrameLayout) findViewById(R.id.frame_external_player);
        mSpriteEyeView = (SpriteEyeView) findViewById(R.id.ex_player_show_eyetrack);
        if (mModel != 0)
            mSpriteEyeView.setVisibility(View.VISIBLE);
        m3DControlBarLayout = (FrameLayout) findViewById(R.id._3dcontrol_seek_layout);
        m3DControlBar = (SeekBar) findViewById(R.id._3dcontrol_seek);
        setOverlay();
        StartAndStopHt.startHT();
        mHeadTracker = (HeadTracker) findViewById(R.id.ex_player_head_tune);

        DisplayMetrics realMetrics = new DisplayMetrics();
        display1.getRealMetrics(realMetrics);

        mWidth = realMetrics.widthPixels;
        mHeight = realMetrics.heightPixels;
        if (mModel == ModelNum.DEVICE_S8_PLUS || mModel == ModelNum.DEVICE_S8 || mModel == ModelNum.DEVICE_NOTE8) {
            if (mPrefManager.getCorverType().equals("camfit")) { // if camera hole cover case, reduce screen size
                mWidth = 2444;
            }
        }
        dpWidth = realMetrics.widthPixels / realMetrics.density;
        dpHeight = realMetrics.heightPixels / realMetrics.density;
        FrameLayout.LayoutParams eXparams = new FrameLayout.LayoutParams(mWidth, mHeight, Gravity.RIGHT);// | Gravity.CENTER_VERTICAL);
        mFrameExternalPlayer.setLayoutParams(eXparams);

        dpWidth = mWidth / realMetrics.density;
        dpHeight = mHeight / realMetrics.density;

        imageButton_3D_Click = true;

        mVideoView = (GLVideoView) findViewById(R.id.ex_glview);
        if(mVideoView.availablePlay() == false) {
            Toast.makeText(getApplicationContext(), "license expire", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        mVideoView.setZOrderMediaOverlay(true);
        mVideoView.setKeepScreenOn(true);

        if (url == null) {
            try {
                whereVideo = EXTERNAL_PATH;
                uri = Uri.parse(videoPath);
                Cursor c = getContentResolver().query(uri, null, null, null, null);
                c.moveToNext();
                String path = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
                uri = Uri.fromFile(new File(path));
                c.close();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                finish();
            }
        } else {
            whereVideo = EXTERNAL_URL;
            try {
                uri = Uri.parse(URLDecoder.decode(url, "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        mHandler.sendEmptyMessage(UPDATE_PROGRESS);
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        initPlayerMenu();

        initGyro();
        mScaleGestureDetector = new ScaleGestureDetector(this, mScaleGestureListener);
        playVideo();
    }

    public void showOverlay(int timeout) {
        playerOverlayLayout.setVisibility(View.VISIBLE);
        Message msg = mHandler.obtainMessage(FADE_OUT);

        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(msg, timeout);
        }
        isShowOverlay = true;

        if(VideoMode.getVideoVRMode()) {
            mVrAutoBtn.setVisibility(View.GONE);
        }
    }

    public void hideOverlay() {
        playerOverlayLayout.setVisibility(View.GONE);
        isShowOverlay = false;
        if(VideoMode.getVideoVRMode()) {
            mVrAutoBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        if (mPause) {
            if (mModel != 0) {
                mSpriteEyeView.resume();
                mSpriteEyeView.setVisibility(View.VISIBLE);
            }
        }
        decorView.setSystemUiVisibility(uiOption);
        StartAndStopHt.startHT();
        mHeadTracker = (HeadTracker) findViewById(R.id.ex_player_head_tune);
        mPause = false;
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPause = true;
        if (mPlayer != null) {
            mPlayer.pause();
            playAndPauseBtn.setImageResource(mPlayer.isPlaying() ? R.mipmap.btn_pause : R.mipmap.btn_play);
        }
        if (mModel != 0)
            mSpriteEyeView.pause();
        if (mHeadTracker != null) {
            mHeadTracker.destroyCamera();
            mHeadTracker.destroyDrawingCache();
        }
        StartAndStopHt.stopHT();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        mHandler.removeMessages(UPDATE_PROGRESS);

        if (mSpriteEyeView != null) {
            mSpriteEyeView.destroy();
        }

        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
        }

        if (mHeadTracker != null) {
            mHeadTracker.destroyCamera();
            mHeadTracker.destroyDrawingCache();
        }
        if(mVrAutoBtn != null)
            mVrAutoBtn.setVisibility(View.GONE);
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            decorView.setSystemUiVisibility(uiOption);
        }
    }

    @Override
    public void onBackPressed() {
        if (!hasBackKey && !hasHomeKey) {
            decorView.setSystemUiVisibility(uiOption);
        } else if (isShowHelp) {
            isShowHelp = false;
        } else {
            finish();
        }
    }

    public void playVideo() {
        mPlayer = new MediaPlayer();

        if (mPlayer != null && !mPlayer.isPlaying()) {
            try {
                mPlayer.setOnCompletionListener(new MediaPlayerOnCompletionListener());
                mPlayer.setDataSource(uri.toString());

                mPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        switch (what) {
                            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
//                                Toast.makeText(getApplicationContext(), "BUFFERING...", Toast.LENGTH_SHORT).show();
                                break;
                            case MediaPlayer.MEDIA_INFO_BUFFERING_END:

                                break;
                        }
                        return false;
                    }
                });

                mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mSpriteEyeView.resume();
                        mp.setLooping(false);
                        //mVideoView.setAspectRatio(mWidth, mHeight, mWidth, mHeight);
                        int videoWidth = mPlayer.getVideoWidth();
                        int videoHeight = mPlayer.getVideoHeight() ;
                        int proportion =  mWidth / mHeight ;
                        mVideoView.setAspectRatio(mWidth, mHeight, videoWidth, videoHeight, mIsFullOn, mIsVrOn);
                        Log.v("mVideoView:","mWidth:"+mWidth+"-mHeight:"+mHeight
                                + "-getVideoWidth:" + videoWidth + "-videoHeight:" + videoHeight+
                        "-proportion:"+proportion);

                        if (proportion >= 2) {
                            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) exitBtn.getLayoutParams();
                            layoutParams.setMarginStart(DensityUtil.dip2px(ExternalPlayerActivity.this, 25));
                            exitBtn.setLayoutParams(layoutParams);
                            RelativeLayout.LayoutParams playAndPauseLayoutParams = (RelativeLayout.LayoutParams) playAndPauseBtn.getLayoutParams();
                            playAndPauseLayoutParams.setMarginStart(DensityUtil.dip2px(ExternalPlayerActivity.this, 25));
                            playAndPauseBtn.setLayoutParams(playAndPauseLayoutParams);
                        }
                        mVideoView.setRender3DParams(params);
                        mVideoView.setRender3DMode(true);
                        mVideoView.setRender3DSwapMode(false);
                        mVideoView.setKeepScreenOn(true);
                        initSeekProgress();
                        mp.setSurface(mVideoView.getVideoSurface());
                        mp.start();
                        isPlayVideo = true;
                    }
                });
                mPlayer.prepareAsync();

                //3D Control
                int temp = mPrefManager.getPreferences(SharedPreferenceManager.PREF_3D_DEPTH, 25);
                m3DControlBar.setMax(50);
                m3DControlBar.setProgress(temp);
                mVideoView.setDControl(temp);
                _3DControlValue = temp;
                m3DControlBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        Log.d(TAG, "progress " + progress);
                        mVideoView.setDControl(progress);
                        _3DControlValue = progress;
                    }
                });
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class MediaPlayerOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if (isPlayVideo && mPlayer != null) {
                mp.release();
                mPlayer = null;
                finish();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        sfHoler.setFixedSize(mWidth, mHeight);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    private class ProgressChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
            try {
                if (arg2) {
                    mPlayer.seekTo(arg1);
                    setSeekProgress();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //    pressSeekbar = true;
            showOverlay(OVERLAY_INFINITE);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //  pressSeekbar = false;
            showOverlay(OVERLAY_TIMEOUT);
        }
    }

    private Handler mHandler = new VideoPlayerHandler(this);

    private static class VideoPlayerHandler extends Handler {
        private static final int UPDATE_PROGRESS = 1;
        private static final int FADE_OUT = 2;

        private static final int FADE_OUT_INFO = 3;
        private static final int CHANGE_IMAGE_EYE = 4;
        private static final int OVERLAY_TIMEOUT = 3000;
        private static final int OVERLAY_INFINITE = 3600000;
        WeakReference<ExternalPlayerActivity> mActivity;

        public VideoPlayerHandler(ExternalPlayerActivity activity) {
            mActivity = new WeakReference<ExternalPlayerActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            ExternalPlayerActivity activity = mActivity.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case FADE_OUT:
                    activity.hideOverlay();
                    break;
                case UPDATE_PROGRESS:
                    int pos = activity.setSeekProgress();
                    msg = obtainMessage(UPDATE_PROGRESS);
                    sendMessageDelayed(msg, 1000 - (pos % 1000));
                    break;
            }
        }
    }

    private void initSeekProgress() {
        if (mPlayer != null) {
            int time;
            if(progress != 0) {
                time = progress;
            } else {
                time = mPlayer.getCurrentPosition();
            }
            seekBar.setProgress(time);
            int max = mPlayer.getDuration();
            seekBar.setMax(max);

            currentTime.setText(millisToString(time));
            totalTime.setText(millisToString(max));
        }
    }

    private int setSeekProgress() {
        if (mPlayer == null) {
            return 0;
        }
        int time = mPlayer.getCurrentPosition();
        int max = mPlayer.getDuration();
        seekBar.setProgress(time);
        currentTime.setText(millisToString(time));
        totalTime.setText(millisToString(max));
        return time;
    }

    private static String millisToString(int millis) {
        boolean negative = millis < 0;
        millis = Math.abs(millis);

        millis /= 1000;
        int sec = (int) (millis % 60);
        millis /= 60;
        int min = (int) (millis % 60);
        millis /= 60;
        int hours = (int) millis;

        String time;
        DecimalFormat format = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        format.applyPattern("00");

        if (millis > 0)
            time = (negative ? "-" : "") + hours + ":" + format.format(min) + ":" + format.format(sec);
        else
            time = (negative ? "-" : "") + min + ":" + format.format(sec);

        return time;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        DisplayMetrics screen = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(screen);

        boolean retVal = mScaleGestureDetector.onTouchEvent(event);

        if (mYDisplayRange == 0)
            mYDisplayRange = Math.min(screen.widthPixels, screen.heightPixels);

        float y_changed = event.getRawY() - mTouchY;
        float x_changed = event.getRawX() - mTouchX;
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                preX = event.getX();
                preY = event.getY();
                mTouchY = event.getRawY();
                mTouchX = event.getRawX();
                mVol = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                mAction = TOUCH_NONE;

                if (!hasBackKey && !hasHomeKey) {
                    decorView.setSystemUiVisibility(uiOption);
                }

                if (m3DControlBarLayout.isShown()) {
                    m3DControlBarLayout.setVisibility(View.GONE);
                    return true;
                }

                if (isShowOverlay) {
                    hideOverlay();
                } else {
                    showOverlay(OVERLAY_TIMEOUT);
                }

                if (imageButton_VR_Click && !mVrAutoSelect)
                    mVideoView.setTouchEvent(mVideoView.ACTION_DOWN, -x, y);
                break;


            case MotionEvent.ACTION_UP:
                if (imageButton_VR_Click && !mVrAutoSelect)
                    mVideoView.setTouchEvent(mVideoView.ACTION_UP, -x, y);
                break;

            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();
                if (imageButton_VR_Click && !mVrAutoSelect)
                    mVideoView.setTouchEvent(mVideoView.ACTION_MOVE, -x, y);
                break;
        }


        return true;
    }

    private final ScaleGestureDetector.OnScaleGestureListener mScaleGestureListener
            = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            float scale = scaleGestureDetector.getScaleFactor();
            Log.d(TAG, "SCALE TEST " + scale);
            mVideoView.setScaleFactor(scale);
            return true;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SETTING_RESULT) {
            if (resultCode == Activity.RESULT_OK) {

                //url = data.getExtras().getString("url");

                //if (url == null) {
                //    videoPath = data.getExtras().getString("videoPath");
               // }
               // loadActivity();
                DatabaseManager mDBManager = new DatabaseManager(getApplicationContext());
                mDBManager.getParametersFromDatabase();
                params = ShareParam.getinstance().getParams();
                mVideoView.setRender3DParams(params);
            }
        }
    }

    private void setOverlay() {
        playerOverlayLayout = (RelativeLayout) findViewById(R.id.ex_player_play_overlay);
        exitBtn = (TextView) findViewById(R.id.ex_player_exit_btn);
        playerNameTv = (TextView) findViewById(R.id.ex_player_name_tv);
        playAndPauseBtn = (ImageButton) findViewById(R.id.ex_player_play_pause);
        seekBar = (SeekBar) findViewById(R.id.ex_player_seek);
        currentTime = (TextView) findViewById(R.id.ex_player_current_time);
        totalTime = (TextView) findViewById(R.id.ex_player_total_time);

        playerOverlayLayout.setVisibility(View.INVISIBLE);

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT >= 21) {
                    finishAndRemoveTask();
                } else {
                finish();
            }
            }
        });

        playerNameTv.setText(mTitle);

        playAndPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mPlayer.isPlaying()) {
                    mPlayer.pause();
                    mSpriteEyeView.pause();
                    mVideoView.setKeepScreenOn(false);
                } else {
                    mPlayer.start();
                    mSpriteEyeView.resume();
                    mVideoView.setKeepScreenOn(true);
                }

                playAndPauseBtn.setImageResource(mPlayer.isPlaying() ? R.mipmap.btn_pause : R.mipmap.btn_play);
                showOverlay(OVERLAY_TIMEOUT);
            }
        });

        seekBar.setOnSeekBarChangeListener(new ProgressChangeListener());

    }

    private void setViewEnable(boolean enable) {
        playAndPauseBtn.setEnabled(enable);
        seekBar.setEnabled(enable);
        moreBtn.setEnabled(enable);
    }

    private void initPlayerMenu() {

        _3dSettingBtn = (TextView) findViewById(R.id.playback_top_3dsetting);

        mEyeTrackingBtn = (ImageView) findViewById(R.id.playback_top_eye_tracking);
        mFSBSToggleBtn = (TextView) findViewById(R.id.playback_select_fsbs);
        m3DToggleBtn = (TextView) findViewById(R.id.playback_select_3d);
        m3DControlBtn = (TextView) findViewById(R.id.playback_top_depth_control);
        mUdlrToggleBtn = (TextView) findViewById(R.id.playback_select_sbs_tb);
        mLrToggleBtn = (TextView) findViewById(R.id.playback_select_lr);
        mVrToggleBtn = (TextView) findViewById(R.id.playback_select_vr);
        mVrAutoBtn = (TextView) findViewById(R.id.playback_select_vr_auto);

        _3dSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress = mPlayer.getCurrentPosition();
                mPlayer.pause();
                Intent intent = new Intent(ExternalPlayerActivity.this, TuningActivity.class);
                intent.putExtra("fromPlayer", "fromPlayer");
                startActivityForResult(intent, SETTING_RESULT);

            }
        });

        mSpriteEyeView.setVisibility(View.VISIBLE);
        mEyeTrackingBtn.setSelected(true);

        mEyeTrackingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mEyeTrackingBtn.isSelected()) {

                    //mHeadTracker.setVisibility(View.VISIBLE);
                    mHeadTracker.isHeadTrackingOff = false;

                    mSpriteEyeView.setVisibility(View.VISIBLE);
                    mPrefManager.savePreferences(SharedPreferenceManager.PREF_PLAYER_SHOW_EYE, true);
                    mEyeTrackingBtn.setSelected(true);
                    mEyeTrackingBtn.setImageResource(R.mipmap.eyes_tracking);
                } else {
                    //mHeadTracker.setVisibility(View.VISIBLE);
                    mHeadTracker.isHeadTrackingOff = true;
                    mSpriteEyeView.setVisibility(View.GONE);

                    mPrefManager.savePreferences(SharedPreferenceManager.PREF_PLAYER_SHOW_EYE, false);
                    mEyeTrackingBtn.setSelected(false);
                    mEyeTrackingBtn.setImageResource(R.mipmap.eyes_not_tracking);
                }
                showOverlay(OVERLAY_TIMEOUT);
            }
        });

        if (mPrefManager.getPreferences(SharedPreferenceManager.PREF_PLAYER_IS_3D, true)) {
            imageButton_3D_Click = true;
            VideoMode.setVideo3DMode(true);
            if (mPrefManager.getPreferences(SharedPreferenceManager.PREF_PLAYER_SHOW_EYE, true))
                mSpriteEyeView.setVisibility(View.VISIBLE);
            m3DToggleBtn.setSelected(true);
            m3DToggleBtn.setText("3D");
        } else {
            m3DToggleBtn.setSelected(false);
            m3DToggleBtn.setText("2D");
        }

        imageButton_FSBS_Click = false;
        mFSBSToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsFullOn = !mFSBSToggleBtn.isSelected();
                imageButton_FSBS_Click = mIsFullOn;
                mFSBSToggleBtn.setSelected(mIsFullOn);
                mVideoView.setAspectRatio(mWidth, mHeight, mPlayer.getVideoWidth(), mPlayer.getVideoHeight(), mIsFullOn, mIsVrOn);
            }
        });

        m3DToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m3DToggleBtn.isSelected()) {

                    imageButton_3D_Click = false;
                    VideoMode.setVideo3DMode(false);
                    mPrefManager.savePreferences(SharedPreferenceManager.PREF_PLAYER_IS_3D, false);

                    mSpriteEyeView.setVisibility(View.GONE);
                    m3DToggleBtn.setSelected(false);
                    m3DToggleBtn.setText("2D");
                } else {
                    imageButton_3D_Click = true;
                    VideoMode.setVideo3DMode(true);
                    mPrefManager.savePreferences(SharedPreferenceManager.PREF_PLAYER_IS_3D, true);

                    if (mPrefManager.getPreferences(SharedPreferenceManager.PREF_PLAYER_SHOW_EYE, true))
                        mSpriteEyeView.setVisibility(View.VISIBLE);
                    m3DToggleBtn.setSelected(true);
                    m3DToggleBtn.setText("3D");
                }
                showOverlay(OVERLAY_TIMEOUT);
            }
        });

        VideoMode.setVideoFormat(0);
        mUdlrToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mUdlrToggleBtn.isSelected()) {
                    VideoMode.setVideoFormat(1);
                    mUdlrToggleBtn.setSelected(true);
                } else {
                    VideoMode.setVideoFormat(0);
                    mUdlrToggleBtn.setSelected(false);
                }
                showOverlay(OVERLAY_TIMEOUT);
            }
        });

        VideoMode.setVideoSwapMode(0);
        mLrToggleBtn.setOnClickListener(new View.OnClickListener() {    //@@@@문제 있음 살펴 볼것!!
            @Override
            public void onClick(View v) {

                if (!mLrToggleBtn.isSelected()) {
                    VideoMode.setVideoSwapMode(1);
                    mLrToggleBtn.setSelected(true);
                } else {
                    VideoMode.setVideoSwapMode(0);
                    mLrToggleBtn.setSelected(false);
                }
                showOverlay(OVERLAY_TIMEOUT);
            }
        });

        imageButton_VR_Click = false;
        mVideoView.stopOrientationSensor();
        VideoMode.setVideoVRMode(imageButton_VR_Click);
        mVrToggleBtn.setSelected(imageButton_VR_Click);

        mVrToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVrToggleBtn.isSelected()) {
                    imageButton_VR_Click = false;
                    mVideoView.stopOrientationSensor();
                    VideoMode.setVideoVRMode(imageButton_VR_Click);
                    mVrToggleBtn.setSelected(imageButton_VR_Click);
                    mVideoView.setAspectRatio(mWidth, mHeight, mPlayer.getVideoWidth(), mPlayer.getVideoHeight(), mIsFullOn, false);
                    mFSBSToggleBtn.setVisibility(View.VISIBLE);
                } else {
                    imageButton_VR_Click = true;
                    mVideoView.startOrientationSensor();
                    VideoMode.setVideoVRMode(imageButton_VR_Click);
                    mVrToggleBtn.setSelected(imageButton_VR_Click);
                    mVideoView.setAspectRatio(mWidth, mHeight, mPlayer.getVideoWidth(), mPlayer.getVideoHeight(), mIsFullOn, true);
                    mFSBSToggleBtn.setVisibility(View.INVISIBLE);
                }
                showOverlay(OVERLAY_TIMEOUT);
            }
        });

        mVrAutoBtn.setVisibility(View.GONE);

        if (mPrefManager.getPreferences(SharedPreferenceManager.PREF_VR_AUTO, true)) {
            mVrAutoSelect = true;
            mVrAutoBtn.setSelected(true);
            mVrAutoBtn.setText("GYRO");
        } else {
            mVrAutoSelect = false;
            mVrAutoBtn.setSelected(false);
            mVrAutoBtn.setText("TOUGH");
        }

        mVrAutoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageButton_VR_Click) {
                    if (mVrAutoBtn.isSelected()) {
                        mVrAutoSelect = false;
                        mVrAutoBtn.setSelected(false);
                        mVrAutoBtn.setText("TOUGH");
                    } else {
                        mVrAutoSelect = true;
                        mVrAutoBtn.setSelected(true);
                        mVrAutoBtn.setText("GYRO");
                    }
                    mPrefManager.savePreferences(SharedPreferenceManager.PREF_VR_AUTO, mVrAutoSelect);
                    showOverlay(OVERLAY_TIMEOUT);
                }
            }
        });

        m3DControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m3DControlBarLayout.setVisibility(View.VISIBLE);
                playerOverlayLayout.setVisibility(View.GONE);
                isShowOverlay = false;
                hideOverlay();
            }
        });
    }

    private void initGyro() {

        val = new Float[3];
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        Sensor accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SensorEventListener sensorEventListener;

        if (gyroSensor == null || accelSensor == null) {
            Log.d("Gyro", "had no gyro or accel sensor");
        }

        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                if (timestamp != 0) {

                    final float dT = (event.timestamp - timestamp) * NS2S;

                    val[0] = event.values[0];
                    val[1] = event.values[1];
                    val[2] = event.values[2];

                    float omegaMagnitude = (float) Math.sqrt(val[0] * val[0] + val[1] * val[1] + val[2] * val[2]);

                    val[0] /= omegaMagnitude;
                    val[1] /= omegaMagnitude;
                    val[2] /= omegaMagnitude;

                    float thetaOverTwo = omegaMagnitude * dT / 2.0f;
                    float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
                    float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
                    deltaRotationVector[0] += sinThetaOverTwo * val[0];
                    deltaRotationVector[1] += sinThetaOverTwo * val[1];
                    deltaRotationVector[2] += sinThetaOverTwo * val[2];
                    deltaRotationVector[3] = cosThetaOverTwo;


                    if (imageButton_VR_Click && vrButtonMode == false && endVrMode == false && mVrAutoSelect) {
                        mVideoView.setTouchEvent(mVideoView.ACTION_GYRO, -(int) (deltaRotationVector[0] * 1000), -(int) (deltaRotationVector[1] * 1000));
                    }

                }
                timestamp = event.timestamp;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(sensorEventListener, gyroSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }
}
