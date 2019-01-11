package com.unbounded.video;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.utils.DensityUtil;
import com.unbounded.video.utils.ExitApplication;
import com.unbounded.video.utils.SharedPrefsUtil;
import com.unbounded.video.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/14 0014.
 */

public abstract class BaseActivity extends FragmentActivity implements View.OnClickListener{
    //使用照相机拍照获取图片
    public static final int SELECT_PIC_BY_TACK_PHOTO = 1;
    //使用相册中的图片
    public static final int SELECT_PIC_BY_PICK_PHOTO = 2;
    //裁剪照片
    public static final int REQUESTCODE_CUTTING = 0;

    private static PermissionListerner permissionListerner;
    public static final int PERMISSION_REQUEST_CODE = 1;

    public Uri photoUri;

    public String userId,userName,userImg;
    public int screenWidth,screenHeight,onlineImgWidth,onelinepicimgWidth,keyHeight;
    public RelativeLayout contentView;
    private RelativeLayout titlerela,emptyView,checknetView,searchemptyView;
    private Button leftBtn;
    TextView righttv,emptytv;
    ImageView rightimg,emptyiv;
    private TextView titleNametv;
    public boolean isFullScreen = false,statusColor = true;
    public int oneMinute = 1000,oneDp;
    public String phoneModel, phoneType;
    public ProgressDialog pd,pd1;
    public Dialog picDialog;

    Dialog toSetDialog;
    public int isFirst = 1;

    static long  mLastFrameSavedTime = 0;
    static long  mCurrentFrameSavedTime = 0;
    static long  minusTime = 800;
    private TextView baseLeftTitle;
//    private HomeReceiver homeReceiver;


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setTheme(R.style.BaseTheme);
        //全屏
        if(isFullScreen == true){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base);
        ExitApplication.getInstance().addActivity(this);
        initScreen();

//        setTranslucentStatus();
        initBaseView();

        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);
        //pd.setCancelable(false);

        pd1 = new ProgressDialog(this);
        pd1.setCanceledOnTouchOutside(false);
        pd1.setCancelable(false);


        setTitleViewVisible(true);
//        setTitleBg(R.color.white);
        setTitleBg(R.drawable.threedd_titlecolor);
        setLeftBtnVisible(true);
        setLeftBtnBg(R.mipmap.whitebackbtn);
        setRightBtnVisible(false);
        setRightImgVisible(false);
        setRightBtnTitlecolor(R.color.white);

        contentView=(RelativeLayout) findViewById(R.id.base_contentview);
        View dataView = LayoutInflater.from(this).inflate(getContentView(), null);
        if (contentView != null) {
            contentView.removeAllViews();
            contentView.addView(dataView);
        }

        setDataViewVisible(true);
        setCheckNetViewVisible(false);
        setEmptyViewVisible(false);

//        statusColor(statusColor);

        init(getIntent());
        initDatas();
        initView();
        initParams();
        initEvent();
        initAnimation();
//        //创建广播
//        homeReceiver = new HomeReceiver();
//
//        //动态注册广播
//        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//        //启动广播
//        registerReceiver(homeReceiver, intentFilter);
    }

    public void whiteBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.white));

            //底部导航栏
            //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
        }
    }

    /**
     * 状态栏
     */
    public void statusColor(boolean b){
        if(b == true){

        }else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                );
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                window.setNavigationBarColor(Color.TRANSPARENT);
            }
        }
    }

    public void init(Intent intent){

    }
    public void initDatas(){

    }
    public void initView(){

    }
    public void initParams(){

    }
    public void initEvent(){

    }
    public void initAnimation(){

    }

    private void initBaseView(){
        titlerela = (RelativeLayout) findViewById(R.id.base_titlerela);
        emptyView = (RelativeLayout) findViewById(R.id.base_emptyview);
        searchemptyView = (RelativeLayout) findViewById(R.id.base_searchemptyview);
        checknetView = (RelativeLayout) findViewById(R.id.base_neterrorview);
        leftBtn = (Button) findViewById(R.id.base_backbtn);
        righttv = (TextView) findViewById(R.id.base_rightv);
        rightimg = (ImageView) findViewById(R.id.base_rightimg);
        titleNametv = (TextView) findViewById(R.id.base_titletv);
        emptyiv = (ImageView) findViewById(R.id.base_emptyiv);
        emptytv = (TextView) findViewById(R.id.base_emptytv);
        baseLeftTitle = (TextView) findViewById(R.id.base_left_title);

        ViewGroup.LayoutParams params = titlerela.getLayoutParams();
        params.height = screenHeight * 65/ 1210;
        titlerela.setLayoutParams(params);

        int emptyivwidth = screenWidth * 633/ 1080;
//        ViewGroup.LayoutParams params1 = (ViewGroup.LayoutParams) emptyiv.getLayoutParams();
//        params1.height = emptyivwidth * 420/ 633;
//        params1.width = emptyivwidth;
//        emptyiv.setLayoutParams(params1);

    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     *
     */
    public void progressDid(){
        pd.setMessage(getString(R.string.Word_loading));
        pd.show();
    }
    public void progressDidresid(int resid){
        pd.setMessage(getString(resid));
        pd.show();
    }

    public void progressDiddismiss(){
        pd.dismiss();
    }

    public void progressDid1(){
        pd1.setMessage(getString(R.string.Word_loading));
        pd1.show();
    }
    public void progressDid1resid(int resid){
        pd1.setMessage(getString(resid));
        pd1.show();
    }

    public void progressDid1dismiss(){
        pd1.dismiss();
    }

    /**
     * 通知媒体库更新数据库
     */
    public static void mountFile(Context context, String savePath){
        Uri data = Uri.parse("file://" +savePath);
        context.sendBroadcast(new  Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data));
    }

    /**
     *获取网络连接状态
     */
    public int getNetState(){
        int netState = Constants.INTERNET_ERROR_FLAG;

        ConnectivityManager mConnectivity = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        //检查网络连接
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();

        if(info != null){
            int netType = info.getType();

            if (netType == ConnectivityManager.TYPE_WIFI) {  //WIFI
//            Log.e("info", "WIFI");
                netState = Constants.WIFI_FLAG;
//            return info.isConnected();
            } else if (netType == ConnectivityManager.TYPE_MOBILE) {   //MOBILE
//            Log.e("info", "GPR");
                netState = Constants.GPR_FLAG;
//            return info.isConnected();
            } else {
                netState = Constants.INTERNET_ERROR_FLAG;
            }
        }

        return netState;
    }

    public static void requestPermissions(String[] permissions,PermissionListerner listerner) {
        Activity topActivity = ActivityManager.getTopActivity();
        if (topActivity==null)
            return;
        permissionListerner = listerner;
        //定义一个权限list
        List<String> permissionLists = new ArrayList<>();
        for (String permission : permissions) {
            //判断所申请的权限是不是已经通过，没通过返回false,通过返回true，则提示出来并拨打电话
            if (ContextCompat.checkSelfPermission(topActivity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionLists.add(permission);
            }
        }

        if (!permissionLists.isEmpty()) {
            //申请权限回调函数
            ActivityCompat.requestPermissions(topActivity, permissionLists.toArray(new String[permissionLists.size()]), PERMISSION_REQUEST_CODE);
        } else {
//            Toast.makeText(topActivity, "权限已全部被申请通过咯！", Toast.LENGTH_SHORT).show();
            permissionListerner.onGranted();
            //            call();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length>0){
                    List<String> denidPermissionList = new ArrayList<>();
                    for (int i=0;i<grantResults.length;i++){
                        int grandResult = grantResults[i];
                        String permission = permissions[i];
                        if (grandResult!=PackageManager.PERMISSION_GRANTED){
                            denidPermissionList.add(permission);
                        }
                    }
                    if (denidPermissionList.isEmpty()){
                        permissionListerner.onGranted();
                    }else{
                        permissionListerner.onDenid(denidPermissionList);
                    }
                }

                break;
            default:
                break;
        }

    }

    /**
     * 用户拒绝后，继续弹出对话框
     */
    public void toSetDialog(){
        View diaView = View.inflate(BaseActivity.this, R.layout.notice_dialog, null);
        toSetDialog = new Dialog(BaseActivity.this, R.style.dialog);
        toSetDialog.setContentView(diaView);
        toSetDialog.setCanceledOnTouchOutside(false);
        toSetDialog.setCancelable(false);

        Button toSetbtn = (Button) toSetDialog.findViewById(R.id.notice_yesbtn);
        Button exitbtn = (Button) toSetDialog.findViewById(R.id.notice_canclebtn);
        TextView titletv = (TextView) toSetDialog.findViewById(R.id.notice_tv1);
        TextView contenttv = (TextView) toSetDialog.findViewById(R.id.notice_tv2);

        toSetbtn.setText(getString(R.string.Word_Set));
        exitbtn.setText(getString(R.string.Word_close));
        titletv.setText(getString(R.string.Word_noticetitle));
        contenttv.setText(getString(R.string.Word_noticecontent));

        toSetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSet();
                toSetDialog.dismiss();
                isFirst = 1;
            }
        });
        //
        exitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitApp();
            }
        });

        toSetDialog.show();

        WindowManager.LayoutParams params = toSetDialog.getWindow().getAttributes();
        params.width = screenWidth * 3 / 4;
        // params.height = 200 ;
        toSetDialog.getWindow().setAttributes(params);
    }

    /**
     * 跳转设置界面用户自己设置权限
     */
    public void toSet(){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, PERMISSION_REQUEST_CODE);
    }

    /**
     * B转成 KB MB GB
     */
    public static String getPrintSize(long size) {
        //如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
        if (size < 1024) {
            return String.valueOf(size) + "B";
        } else {
            size = size / 1024;
        }
        //如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        //因为还没有到达要使用另一个单位的时候
        //接下去以此类推
        if (size < 1024) {
            return String.valueOf(size) + "KB";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            //因为如果以MB为单位的话，要保留最后1位小数，
            //因此，把此数乘以100之后再取余
            size = size * 100;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "MB";
        } else {
            //否则如果要以GB为单位的，先除于1024再作同样的处理
            size = size * 100 / 1024;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "GB";
        }
    }

    /**
     * 毫秒转换为  天 时  分  秒
     */
    public String formatDuring(long mss) {
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

//        return days + " days " + hours + " hours " + minutes + " minutes " + seconds + " seconds ";
//        return hoursStr + ":" + minutesStr + ":" + secondsStr;
        return minutesStr + "\'" + secondsStr + "\"";
    }



    /**
     * 对话框
     */
    public void initPicDialog(){
        View diaView = View.inflate(BaseActivity.this, R.layout.picpop, null);
        picDialog = new Dialog(BaseActivity.this, R.style.dialog);
        picDialog.setContentView(diaView);
//        picDialog.setCanceledOnTouchOutside(false);

        Button takepicbtn = (Button) picDialog.findViewById(R.id.picpop_paizhaobtn);
        Button pickpicbtn = (Button) picDialog.findViewById(R.id.picpop_xiangcebtn);
        ImageView closeiv = (ImageView) picDialog.findViewById(R.id.picpop_closeiv);

        //
        closeiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                picDialog.dismiss();
            }
        });
        //拍照
        takepicbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
                picDialog.dismiss();
            }
        });
        //相册
        pickpicbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickPhoto();
                picDialog.dismiss();
            }
        });



        picDialog.show();

        WindowManager.LayoutParams params = picDialog.getWindow().getAttributes();
        params.width = screenWidth * 3 / 4;
        // params.height = 200 ;
        picDialog.getWindow().setAttributes(params);
    }

    /**
     * 拍照获取图片
     */

    public void takePhoto() {
        //执行拍照前，应该先判断SD卡是否存在
        String SDState = Environment.getExternalStorageState();
        if(SDState.equals(Environment.MEDIA_MOUNTED))
        {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//"android.media.action.IMAGE_CAPTURE"
//            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");//"android.media.action.IMAGE_CAPTURE"
            /***
             * 需要说明一下，以下操作使用照相机拍照，拍照后的图片会存放在相册中的
             *
             *
             *
             *
             * 这里使用的这种方式有一个好处就是获取的图片是拍照后的原图
             * 如果不实用ContentValues存放照片路径的话，拍照后获取的图片为缩略图不清晰
             */
            ContentValues values = new ContentValues();
            photoUri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
            /**-----------------*/
            startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
        }else{
            ToastUtil.showToast(getApplicationContext(), R.string.Word_nosd, ToastUtil.CENTER);
        }
    }


    /***
     * 从相册中取图片
     */
    public void pickPhoto() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
        // 如果朋友们要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(pickIntent, SELECT_PIC_BY_PICK_PHOTO);

    }

    /**
     * 根据前后两次时间显示下载速度，下载速度变化太快，只是显示没必要刷那么快
     */
    public static boolean startReco(){
        boolean save = false;
        //第一次,成功
        if(mLastFrameSavedTime == 0){
            save = true;
            mCurrentFrameSavedTime = System.currentTimeMillis();
            mLastFrameSavedTime = mCurrentFrameSavedTime;

            //第二次以后,判断时间
        }else {
            mCurrentFrameSavedTime = System.currentTimeMillis();
            if(mCurrentFrameSavedTime - mLastFrameSavedTime > minusTime){

                save = true;
                mLastFrameSavedTime = mCurrentFrameSavedTime;
            }else {

                save = false;
            }
        }

        return save;
    }
    /**
     * 屏幕尺寸,手机型号
     */
    public void initScreen(){
        screenWidth = SharedPrefsUtil.getValue(getApplicationContext(), Constants.SCREEN_WIDTH, Constants.ZERO);
        screenHeight = SharedPrefsUtil.getValue(getApplicationContext(), Constants.SCREEN_HEIGHT, Constants.ZERO);
        phoneType = SharedPrefsUtil.getValue(getApplicationContext(), Constants.PHONE_TYPE, "");
        oneDp = DensityUtil.dip2px(getApplicationContext(), 1);

        if(screenWidth == Constants.ZERO || screenHeight == Constants.ZERO || oneDp == Constants.ZERO){
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            screenWidth = dm.widthPixels;
            screenHeight = dm.heightPixels;

            SharedPrefsUtil.putValue(getApplicationContext(), Constants.SCREEN_WIDTH, screenWidth);
            SharedPrefsUtil.putValue(getApplicationContext(), Constants.SCREEN_HEIGHT, screenHeight);
            SharedPrefsUtil.putValue(getApplicationContext(), Constants.One_Dp, oneDp);

        }

        onlineImgWidth = screenWidth - 24*oneDp;
        onelinepicimgWidth = (screenWidth - 2*oneDp)/2;
        keyHeight = screenHeight/3;

        if(TextUtils.isEmpty(phoneType)){
            phoneModel = android.os.Build.MODEL;
            if (("VTR-AL00").equals(phoneModel)) {
                // phoneType = "P10";
                phoneType = "iPhone6Plus";
            } else if (("MI 6").equals(phoneModel) || ("SM-N9100").equals(phoneModel)) {
                //phoneType = "米6";
                phoneType = "iPhone6Plus";
            }
            SharedPrefsUtil.putValue(getApplicationContext(), Constants.PHONE_TYPE, phoneType);
        }
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams wlp = getWindow().getAttributes();
        wlp.alpha = bgAlpha; // 0.0-1.0
        getWindow().setAttributes(wlp);
    }
    public void onLeftClick(View v){
        finish();
    }
    /**
     * 右边按钮
     */
    public void onRightClick(View v){
//        Log.e("info", "onRightClick");
    }

//    @Override
//    public void overridePendingTransition(int enterAnim, int exitAnim) {
////        super.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//        super.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//    }

    /**
     * 设置状态栏背景状态
     */
    private void setTranslucentStatus()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            );
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }

    }

    /**
     * 空数据布局
     */
    public void setEmptyViewVisible(Boolean visible) {
        if (emptyView!=null) {
            if (visible == true) {
                emptyView.setVisibility(View.VISIBLE);
            }else{
                emptyView.setVisibility(View.GONE);
            }
        }
    }
    /**
     * 空数据文字
     */
    public void setEmptyTvVisible(Boolean visible) {
        if (emptytv!=null) {
            if (visible == true) {
                emptytv.setVisibility(View.VISIBLE);
            }else{
                emptytv.setVisibility(View.GONE);
            }
        }
    }


    /**
     * 搜索空布局
     */
    public void setSearchEmptyViewVisible(Boolean visible) {
        if (searchemptyView!=null) {
            if (visible == true) {
                searchemptyView.setVisibility(View.VISIBLE);
            }else{
                searchemptyView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 有数据时布局
     */
    public void setDataViewVisible(Boolean visible) {
        if (contentView!=null) {
            if (visible == true) {
                contentView.setVisibility(View.VISIBLE);
            }else{
                contentView.setVisibility(View.GONE);
            }
        }
    }
    /**
     * 判断网络布局
     */
    public void setCheckNetViewVisible(Boolean visible) {
        if (checknetView!=null) {
            if (visible == true) {
                checknetView.setVisibility(View.VISIBLE);
            }else{
                checknetView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 设置标题栏显示与隐藏
     */
    public void setTitleViewVisible(Boolean visible){
        if (titlerela!=null) {
            if (visible == true) {
                titlerela.setVisibility(View.VISIBLE);
            }else{
                titlerela.setVisibility(View.GONE);
            }
        }
    }

    public void setLeftTitleVisible(Boolean visible) {
        if (baseLeftTitle != null) {
            if (visible) {
                baseLeftTitle.setVisibility(View.VISIBLE);
            } else {
                baseLeftTitle.setVisibility(View.GONE);
            }
        }

    }
    public void onLeftTitleOnclick(){
        finish();
    }
    public void setLeftTitleStr(String title){
        if(baseLeftTitle!=null){
            baseLeftTitle.setText(title);
        }
    }
    /**
     * 设置左侧按钮显示与隐藏
     * @param visible
     */
    public void setLeftBtnVisible(Boolean visible) {
        if (leftBtn!=null) {
            if (visible == true) {
                leftBtn.setVisibility(View.VISIBLE);
            }else{
                leftBtn.setVisibility(View.GONE);
            }
        }
    }

    /**
     *
     */
    public void setLeftBtnBg(int resid) {
        if (leftBtn!=null) {
            leftBtn.setBackgroundResource(resid);
        }
    }
    /**
     * 设置左边按钮大小
     */
    public void setLeftBtnBgSize(int height, int width) {
        if (leftBtn!=null) {
            leftBtn.setHeight(height);
            leftBtn.setWidth(width);
        }
    }

    /**
     * 设置右侧按钮显示与隐藏
     * @param visible
     */
    public void setRightBtnVisible(Boolean visible) {
        if (righttv!=null) {
            if (visible == true) {
                righttv.setVisibility(View.VISIBLE);
            }else{
                righttv.setVisibility(View.GONE);
            }
        }
    }
    /**
     * 设置右侧图片按钮显示与隐藏
     * @param visible
     */
    public void setRightImgVisible(Boolean visible) {
        if (rightimg!=null) {
            if (visible == true) {
                rightimg.setVisibility(View.VISIBLE);
            }else{
                rightimg.setVisibility(View.GONE);
            }
        }
    }
    /**
     * 设置标题栏右边图片按钮背景
     * @param resource
     */
    public void setRightImgBg(int resource){
        if (rightimg!=null) {
            rightimg.setBackgroundResource(resource);
        }
    }
    /**
     * 设置标题栏右边按钮文字属性
     * @param title
     */
    public void setRightBtnTitle(String title){
        if (righttv!=null) {
            righttv.setText(title);
        }
    }
    /**
     * 设置标题栏右边按钮文字颜色
     * @param color
     */
    public void setRightBtnTitlecolor(int color){
        if (righttv!=null) {
            righttv.setTextColor(getResources().getColor(color));
        }
    }
    /**
     * 设置标题栏右边按钮背景
     * @param resource
     */
    public void setRightBtnBg(int resource){
        if (righttv!=null) {
            righttv.setBackgroundResource(resource);
        }
    }

    /**
     * 设置中间标题
     * @param title
     */
    public void setTitleName(String title){
        if (titleNametv!=null) {
            if (title!=null) {
                titleNametv.setText(title);
            }
        }
    }

    /**
     * 设置标题背景
     * @param
     */
    public void setTitleBg(int resource){
        if (titlerela!=null) {
            titlerela.setBackgroundResource(resource);

        }
    }

    /**
     * 获取中间内容显示区
     * @return
     */
    protected abstract int getContentView();



    /**
     * 通过类名启动Activity
     *
     * @param pClass
     */
    public void openActivity(Class<?> pClass) {
        openActivity(pClass, null);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    /**
     * 通过类名启动Activity，并且含有Bundle数据
     *
     * @param pClass
     * @param pBundle
     */
    public void openActivity(Class<?> pClass, Bundle pBundle) {
        Intent intent = new Intent(this, pClass);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivityByIntent(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
    private void startActivityByIntent(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    /**
     * 从底部弹出 主要用于登录界面
     * 至于结束效果要在对应界面的结束操作响应
     * @param pClass
     */
    protected void openActivityFromBottom(Class<?> pClass){
        startActivity(pClass);
        overridePendingTransition(R.anim.slide_in_from_bottom, 0);
    }

    public void openActivityFromBottomForResult(Intent intent, int reqeustCode) {
        startActivityForResult(intent, reqeustCode);
        overridePendingTransition(R.anim.slide_in_from_bottom, 0);
    }
    protected void startActivity(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    /**
     * Toast 显示
     */
    public void showToastResId(int resource, int gravity){
        ToastUtil.showToast(getApplicationContext(), resource, gravity);
    }

    public void showToastStr(String Str, int gravity){
        ToastUtil.showToast(getApplicationContext(), Str, gravity);
    }


    /**
     * @Description 隐藏键盘
     * @author deiw-liao
     */
    public void hideInputMode() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if (view != null) {
            View rootView = view.getRootView();
            if(rootView!=null){
                imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
            }

        }
    }


//    public void finish() {
//        super.finish();
//        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
//    }

    public void exitApp(){
        ExitApplication.getInstance().exit(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (homeReceiver != null) {
//            unregisterReceiver(homeReceiver);
//        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
        }
    }
//    class HomeReceiver extends BroadcastReceiver {
//        final String SYSTEM_DIALOG_REASON_KEY = "reason";
//
//        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
//
//        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
//                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
//                if (reason != null) {
//                    if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
//                        Toast.makeText(context, "Home键被监听", Toast.LENGTH_SHORT).show();
//
//                    } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
//                        Toast.makeText(context, "多任务键被监听", Toast.LENGTH_SHORT).show();
//
//                    }
//                }
//            }
//        }
//    }
}
