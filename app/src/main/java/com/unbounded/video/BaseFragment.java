package com.unbounded.video;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.unbounded.video.activity.MainActivity;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.utils.DensityUtil;
import com.unbounded.video.utils.ExitApplication;
import com.unbounded.video.utils.SharedPrefsUtil;

/**
 * Created by zjf on 2017/7/18 0018.
 */

public abstract class BaseFragment extends Fragment{
    public static final int PERMISSION_REQUEST_CODE = 1;
    private static PermissionListerner permissionListerner;
    public int screenWidth,screenHeight;
    public RelativeLayout contentView;
    private RelativeLayout titlerela,emptyView,checknetView;
    private Button leftBtn,rightBtn;
    private TextView titleNametv;
    ImageView emptyiv;
    public String userId;
    public int oneDp,onlineImgWidth,tabsmrelaHeight,localpicimgWidth, onelinepicimgWidth;

    public ProgressDialog pd,pd1;
    Dialog toSetDialog;

    static long  mLastFrameSavedTime = 0;
    static long  mCurrentFrameSavedTime = 0;
    static long  minusTime = 500;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base, container, false);
        initScreen();

        pd = new ProgressDialog(getActivity());
        pd.setCanceledOnTouchOutside(false);
        //pd.setCancelable(false);
        pd1 = new ProgressDialog(getActivity());
        pd1.setCanceledOnTouchOutside(false);
        pd1.setCancelable(false);

        initBaseView(view);
        setTitleViewVisible(false);
        contentView=(RelativeLayout) view.findViewById(R.id.basefrag_contentview);
        View dataView = LayoutInflater.from(getActivity()).inflate(getContentView(), null);
        if (contentView != null) {
            contentView.removeAllViews();
            contentView.addView(dataView);
        }

        setDataViewVisible(true);
        setCheckNetViewVisible(false);
        setEmptyViewVisible(false);

        init();
        initDatas();
        initView(view);
        initParams();
        initEvent();
        initAnimation();

        return view;
    }

    public void init(){

    }
    public void initDatas(){

    }
    public void initView(View view){

    }
    public void initParams(){

    }
    public void initEvent(){

    }
    public void initAnimation(){

    }

    public void toSet(){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, PERMISSION_REQUEST_CODE);
    }

    public void toSetDialog(){
        View diaView = View.inflate(getActivity(), R.layout.notice_dialog, null);
        toSetDialog = new Dialog(getActivity(), R.style.dialog);
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
        contenttv.setText(getString(R.string.Word_noticewrite));

        toSetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSet();
                toSetDialog.dismiss();
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


    public void exitApp(){
        ExitApplication.getInstance().exit(getContext());
    }

    /**
     *
     */
    public void progressDid(){
        if(pd != null && !(pd.isShowing())){
            pd.setMessage(getString(R.string.Word_loading));
            pd.show();
        }
    }

    public void progressDiddismiss(){
        pd.dismiss();
    }
    public void progressDid1(){
        if(!(pd1.isShowing())){
            pd1.setMessage(getString(R.string.Word_loading));
            pd1.show();
        }
    }

    public void progressDiddismiss1(){
        pd1.dismiss();
    }


        /**
         * 获取中间内容显示区
         * @return
         */
    protected abstract int getContentView();

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
     * B转成 KB MB GB
     */
    public String getPrintSize(long size) {
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
     * 屏幕尺寸
     */
    public void initScreen(){
        screenWidth = SharedPrefsUtil.getValue(getActivity(), Constants.SCREEN_WIDTH, Constants.ZERO);
        screenHeight = SharedPrefsUtil.getValue(getActivity(), Constants.SCREEN_HEIGHT, Constants.ZERO);
        oneDp = SharedPrefsUtil.getValue(getActivity(), Constants.One_Dp, Constants.ZERO);

        if(screenWidth == Constants.ZERO || screenHeight == Constants.ZERO || oneDp == Constants.ZERO){
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            screenWidth = dm.widthPixels;
            screenHeight = dm.heightPixels;

            oneDp = DensityUtil.dip2px(getActivity(), 1);

            SharedPrefsUtil.putValue(getActivity(), Constants.SCREEN_WIDTH, screenWidth);
            SharedPrefsUtil.putValue(getActivity(), Constants.SCREEN_HEIGHT, screenHeight);
            SharedPrefsUtil.putValue(getActivity(), Constants.One_Dp, oneDp);
        }

        onlineImgWidth = screenWidth - 20*oneDp;
        localpicimgWidth = (screenWidth - 40*oneDp)/3;
        onelinepicimgWidth = (screenWidth - 2*oneDp)/2;
        tabsmrelaHeight = SharedPrefsUtil.getValue(getActivity(), MainActivity.Tabsrela_Height, Constants.ZERO);
        Log.v("screenWidth","："+screenWidth+"：screenHeight："+screenHeight);
    }

    /**
     *
     */
    private void initBaseView(View view){
        titlerela = (RelativeLayout) view.findViewById(R.id.basefrag_titlerela);
        emptyView = (RelativeLayout) view.findViewById(R.id.basefrag_emptyview);
        emptyiv = (ImageView) view.findViewById(R.id.basefrag_emptyiv);
        checknetView = (RelativeLayout) view.findViewById(R.id.basefrag_neterrorview);
        leftBtn = (Button) view.findViewById(R.id.basefrag_backbtn);
        rightBtn = (Button) view.findViewById(R.id.basefrag_rightbtn);
        titleNametv = (TextView) view.findViewById(R.id.basefrag_titletv);

        ViewGroup.LayoutParams params = titlerela.getLayoutParams();
        params.height = screenWidth * 9/ 72;
        titlerela.setLayoutParams(params);

        int emptyivwidth = screenWidth * 633/ 1080;
        ViewGroup.LayoutParams params1 = emptyiv.getLayoutParams();
        params1.height = emptyivwidth * 420/ 633;
        params1.width = emptyivwidth;
        emptyiv.setLayoutParams(params1);
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

}
