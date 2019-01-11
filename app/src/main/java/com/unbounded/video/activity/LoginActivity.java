package com.unbounded.video.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.unbounded.video.BaseActivity;
import com.unbounded.video.R;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.constants.HttpConstants;
import com.unbounded.video.utils.ExitApplication;
import com.unbounded.video.utils.HttpPostUtil;
import com.unbounded.video.utils.SharedPrefsUtil;
import com.unbounded.video.utils.ToastUtil;
import com.unbounded.video.view.banner.containsEmojiEditText.ContainsEmojiEditText;
import com.zhuge.analysis.stat.ZhugeSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zjf on 2017/7/19 0019.
 */

public class LoginActivity extends BaseActivity {
    ContainsEmojiEditText mobilenumedt, pswedt;
    ImageView pswVisibleiv, WeChativ, qqiv, sinaiv, deletephoneiv;
    TextView forgetpswtv;
    Button loginbtn;
    private TextView verificationCode;
    boolean pswvisible = false;
    String mobileStr, pswStr, Regid, phoneNum;
    public static final int YOU_ZAN_LOGIN = 1;
    List<String> keyList = new ArrayList<String>();
    List<String> valueList = new ArrayList<String>();

    List<String> youZanLoginKeyList = new ArrayList<String>();
    List<Object> youZanLoginValueList = new ArrayList<Object>();
    List<String> loginKeyList = new ArrayList<>();
    List<String> logValueList = new ArrayList<>();
    public static final String SUCCESS = "1";
    public static final String FAIL = "2";
    public static final String PHONE_FAIL = "3";
    public static final int LOGIN_STATUE = 2;
    private String bizid;
    TimeCount time;
    Handler handler = new Handler() {



        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.INTERNET_SUCCESS_FLAG:
                    String sendCodevalue = msg.obj.toString();
                    Log.e("info", "loginvalue=" + sendCodevalue);
                    if (!(TextUtils.isEmpty(sendCodevalue))) {
                        try {
                            JSONObject loginjo = new JSONObject(sendCodevalue);
                            String state = loginjo.getString("state");
                            if(state.equals(SUCCESS)){
                                verificationCode.setClickable(false);
                                time = new TimeCount(60000, 1000);
                                time.start();
                                bizid = loginjo.getString("bizid");
                                ToastUtil.showToast(LoginActivity.this,"已发送",Toast.LENGTH_LONG);
                            }else if(state.equals(FAIL)){
                                ToastUtil.showToast(LoginActivity.this,"发送失败",Toast.LENGTH_LONG);

                            }else if(state.equals(PHONE_FAIL)){
                                ToastUtil.showToast(LoginActivity.this,"手机号码不正确" +
                                        "或者操作过快，请稍等",Toast.LENGTH_LONG);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    break;
                case Constants.INTERNET_ERROR_FLAG:
                    ToastUtil.showToast(LoginActivity.this, R.string.internet_error, ToastUtil.CENTER);
                    progressDid1dismiss();
                    break;
                case YOU_ZAN_LOGIN:
                    String youZanLogin = msg.obj.toString();
                    Log.e("info", "youZanLogin=" + youZanLogin);

                    if (!(TextUtils.isEmpty(youZanLogin))) {


                        try {
                            JSONObject loginjo = new JSONObject(youZanLogin);
                            int code = loginjo.getInt("code");
                            String loginMsg = loginjo.getString("msg");
                            if (code == 0) {
                                /*登陆成功*/
                                JSONObject data = loginjo.getJSONObject("data");
                                String access_token = data.getString("access_token");
                                String cookie_key = data.getString("cookie_key");
                                String cookie_value = data.getString("cookie_value");
                                SharedPrefsUtil.putValue(getApplicationContext(), Constants.ACCESS_TOKEN, access_token);
                                SharedPrefsUtil.putValue(getApplicationContext(), Constants.COOKIE_KEY, cookie_key);
                                SharedPrefsUtil.putValue(getApplicationContext(), Constants.COOKIE_VALUE, cookie_value);

                                SharedPrefsUtil.putValue(getApplicationContext(), Constants.IS_LOGIN, true);

                                progressDid1dismiss();
                                ToastUtil.showToast(LoginActivity.this, R.string.Word_loginsuccess, ToastUtil.CENTER);
                                ExitApplication.getInstance().exitLoginActivitys();
                                String action = getIntent().getAction();
                                //当用户使用自有账号登录时，可以这样统计：
                                MobclickAgent.onProfileSignIn(userId);

                                if(action!=null&&action.equals(Constants.MALLACTIVITY_TYPE)){
                                    Intent intent = new Intent();
                                    intent.putExtra("isLogin",true);
                                    setResult(1,intent);
                                    finish();
                                }
                            }else{
                                progressDid1dismiss();
                                ToastUtil.showToast(LoginActivity.this, loginMsg, ToastUtil.CENTER);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case LOGIN_STATUE:
                    String loginvalue = msg.obj.toString();
                    Log.e("info", "loginvalue=" + loginvalue);

                    if (!(TextUtils.isEmpty(loginvalue))) {
                        try {
                            JSONObject loginjo = new JSONObject(loginvalue);
                    //成功
                                /*rjz 修改由于接入了有赞商城，需要等有赞登陆成功才算成功*/
//                                progressDid1dismiss();
//                                ToastUtil.showToast(LoginActivity.this, R.string.Word_loginsuccess, ToastUtil.CENTER);
                        userImg = loginjo.getString("userimg");
                        String phone = loginjo.getString("phone");
                        String sex = loginjo.getString("sex");
                        Regid = loginjo.getString("regid");
                        String autograph = loginjo.getString("autograph");
                        userId = loginjo.getString("userid");
                        String userCase = loginjo.getString("userCase");
                        String userfans = loginjo.getString("userfans");
                        String account = loginjo.getString("account");
                        userName = loginjo.getString("username");

                        SharedPrefsUtil.putValue(getApplicationContext(), Constants.UserImg_FLAG, userImg);
                        SharedPrefsUtil.putValue(getApplicationContext(), Constants.Phone_FLAG, phone);
                        SharedPrefsUtil.putValue(getApplicationContext(), Constants.Sex_FLAG, sex);
                        SharedPrefsUtil.putValue(getApplicationContext(), Constants.Regid_FLAG, Regid);
                        SharedPrefsUtil.putValue(getApplicationContext(), Constants.Autograph_FLAG, autograph);
                        SharedPrefsUtil.putValue(getApplicationContext(), Constants.UserId_FLAG, userId);
                        SharedPrefsUtil.putValue(getApplicationContext(), Constants.UserCase_FLAG, userCase);
                        SharedPrefsUtil.putValue(getApplicationContext(), Constants.Userfans_FLAG, userfans);
                        SharedPrefsUtil.putValue(getApplicationContext(), Constants.Account_FLAG, account);
                        SharedPrefsUtil.putValue(getApplicationContext(), Constants.Username_FLAG, userName);

                        SharedPrefsUtil.putValue(getApplicationContext(), Constants.VideoInfoUpdate, 1);
                        SharedPrefsUtil.putValue(getApplicationContext(), Constants.PicInfoUpdate, 1);
                        /**
                         * 登录成次数
                         */
                        phoneNum = SharedPrefsUtil.getValue(getApplicationContext(), Constants.Phone_FLAG, Constants.Phone_default);
                        JSONObject personObject = null;
                        try {
                            personObject = new JSONObject();
                            personObject.put("phoneNum", phoneNum);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ZhugeSDK.getInstance().track(getApplicationContext(), "登录成功次数", personObject);
                                /*rjz 修改由于接入了有赞商城，需要等有赞成功才算成功*/
//                                ExitApplication.getInstance().exitLoginActivitys();


                                /*有赞商城登陆*/
//                                youZanLoginData.put("kdt_id", Constants.YOU_ZAN_MALL_SHOP_ID);
//                                youZanLoginData.put("client_id", Constants.YOU_ZAN_MALL_CLIENT_ID);
//                                youZanLoginData.put("client_secret", Constants.YOU_ZAN_MALL_CLIENT_SECRE);
//                                youZanLoginData.put("open_user_id", userId);
//                                youZanLoginData.put("nick_name", userName);
//                                youZanLoginData.put("avatar", userImg);
                        youZanLoginKeyList.add("kdt_id");
                        youZanLoginValueList.add(Constants.YOU_ZAN_MALL_SHOP_ID);
                        youZanLoginKeyList.add("client_id");
                        youZanLoginValueList.add(Constants.YOU_ZAN_MALL_CLIENT_ID);
                        youZanLoginKeyList.add("client_secret");
                        youZanLoginValueList.add(Constants.YOU_ZAN_MALL_CLIENT_SECRE);
                        youZanLoginKeyList.add("open_user_id");
                        youZanLoginValueList.add(userId);
                        youZanLoginKeyList.add("nick_name");
                        youZanLoginValueList.add(userName);
                        youZanLoginKeyList.add("avatar");
                        youZanLoginValueList.add(userImg);
                        HttpPostUtil post = new HttpPostUtil(handler, HttpConstants.YouZan_Mall_Login, youZanLoginKeyList, youZanLoginValueList
                                , YOU_ZAN_LOGIN,0);
                        Thread thread = new Thread(post);
                        thread.start();
                        //失败
                    }  catch (JSONException e) {
                            ToastUtil.showToast(LoginActivity.this,"登陆失败",Toast.LENGTH_LONG);
                            progressDid1dismiss();
                        }

                    }
                    break;
            }

        }
    };
    private ContainsEmojiEditText loginVerificationedt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        statusColor(false);
//        whiteBar();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    public void init(Intent intent) {
        super.init(intent);

        setTitleViewVisible(true);
        setTitleBg(R.color.white);
//        setRightBtnVisible(true);
        setLeftBtnBg(R.mipmap.logincloseiv);
//        setRightBtnTitle(getString(R.string.Word_register));
//        setRightBtnTitlecolor(R.drawable.black2gray_textcolor);
        ExitApplication.getInstance().addLoginActivity(this);
        Regid = SharedPrefsUtil.getValue(getApplicationContext(), Constants.Regid_FLAG, "");
    }

    @Override
    public void initView() {
        super.initView();

        mobilenumedt = (ContainsEmojiEditText) findViewById(R.id.login_mobilenumedt);
        pswedt = (ContainsEmojiEditText) findViewById(R.id.login_pswedt);
        pswVisibleiv = (ImageView) findViewById(R.id.login_pswvisible);
        loginbtn = (Button) findViewById(R.id.login_loginbtn);
        forgetpswtv = (TextView) findViewById(R.id.login_forgetpswtv);
        WeChativ = (ImageView) findViewById(R.id.login_weixiniv);
        qqiv = (ImageView) findViewById(R.id.login_qqiv);
        sinaiv = (ImageView) findViewById(R.id.login_weiboiv);
        deletephoneiv = (ImageView) findViewById(R.id.login_deletephoneiv);
        verificationCode = (TextView) findViewById(R.id.verification_code);
        loginVerificationedt = (ContainsEmojiEditText) findViewById(R.id.login_verificationedt);
    }

    @Override
    public void onLeftClick(View v) {
        super.onLeftClick(v);
        ExitApplication.getInstance().exitLoginActivitys();
    }

//    @Override
//    public void onRightClick(View v) {
//        super.onRightClick(v);
//        hideInputMode();
//        openActivity(RegisterActivity.class);
//    }

    @Override
    public void initEvent() {
        super.initEvent();

        pswVisibleiv.setOnClickListener(this);
        forgetpswtv.setOnClickListener(this);
        loginbtn.setOnClickListener(this);
        deletephoneiv.setOnClickListener(this);
        verificationCode.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.login_deletephoneiv:
                mobilenumedt.setText("");
                mobileStr = "";

                break;

            case R.id.verification_code:
//                pswView();
                senCode();
                break;

            case R.id.login_forgetpswtv:
                hideInputMode();
                openActivity(ForgetPswActivity.class);

                break;
            //登录
            case R.id.login_loginbtn:
                hideInputMode();
                loginMethod();

                break;

        }
    }

    /**
     * 密码可见切换
     */
    private void pswView() {
        pswStr = pswedt.getText().toString().trim();

        if (pswvisible == false) {
            if (!(TextUtils.isEmpty(pswStr))) {
                //选择状态 显示明文--设置为可见的密码
                pswedt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                pswVisibleiv.setImageResource(R.mipmap.psweyecloseiv);
                pswvisible = true;
            }
        } else {
            //默认状态显示密码--设置文本 要一起写才能起作用
            pswedt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            pswVisibleiv.setImageResource(R.mipmap.eyeicon);
            pswvisible = false;
        }
        pswedt.setSelection(pswedt.getText().toString().length());
    }

    /**
     * 登录方法
     */
    private void loginMethod() {
        mobileStr = mobilenumedt.getText().toString().trim();
//        pswStr = pswedt.getText().toString().trim();
        pswStr =loginVerificationedt.getText().toString().trim();
        if (TextUtils.isEmpty(mobileStr)) {
            ToastUtil.showToast(getApplicationContext(), R.string.Word_loginnamehint, ToastUtil.CENTER);
            return;
        }
        if (TextUtils.isEmpty(pswStr)) {
            ToastUtil.showToast(getApplicationContext(), R.string.Word_pswhint, ToastUtil.CENTER);
            return;
        }
        if (mobileStr.length() != 11) {
            ToastUtil.showToast(getApplicationContext(), R.string.Word_mobilelengtherror, ToastUtil.CENTER);
            return;
        }
        if (pswStr.length() < 6) {
            ToastUtil.showToast(getApplicationContext(), R.string.Word_pswlength, ToastUtil.CENTER);
            return;
        }
        login();

        /**
         * 登录
         */
        phoneNum = SharedPrefsUtil.getValue(getApplicationContext(), Constants.Phone_FLAG, "");
        ZhugeSDK.getInstance().track(getApplicationContext(), "登录次数" + phoneNum);
    }

    private void login() {
        progressDid1resid(R.string.Word_logining);
        loginKeyList.clear();
        logValueList.clear();
        loginKeyList.add("phone");
        loginKeyList.add("code");
        loginKeyList.add("bizid");
        loginKeyList.add("regid");
        logValueList.add(mobileStr);
        logValueList.add(pswStr);
        logValueList.add(bizid);
        logValueList.add("");

        Log.v("login str","loginKeyList:"+loginKeyList.toString()+"logValueList:"+logValueList.toString()+"url:"+HttpConstants.NEW_LOGIN);
        HttpPostUtil post = new HttpPostUtil(handler, HttpConstants.NEW_LOGIN, loginKeyList, logValueList
                , LOGIN_STATUE,0);
        Thread thread = new Thread(post);
        thread.start();
    }

    /**
     * 发送验证码
     */
    private void senCode() {
        mobileStr = mobilenumedt.getText().toString().trim();
        keyList.clear();
        valueList.clear();

        keyList.add("phone");
//        keyList.add("password");
//        keyList.add("regid");
        valueList.add(mobileStr);
//        valueList.add(pswStr);
//        valueList.add(Regid);
        Log.v("mobileStr",""+mobileStr);
        if(!TextUtils.isEmpty(mobileStr)){
            String loginurl = HttpConstants./*Login_Url*/SEND_CODE;
            HttpPostUtil post = new HttpPostUtil(handler, loginurl, keyList, valueList, Constants.INTERNET_SUCCESS_FLAG);
            Thread thread = new Thread(post);
            thread.start();
        }else{
            ToastUtil.showToast(LoginActivity.this,"手机号码为空",Toast.LENGTH_LONG);
        }

    }


    /**
     * 倒计时
     */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }
        @Override
        public void onFinish() {// 计时完毕时触发
            if (verificationCode != null) {
                verificationCode.setText(com.unbounded.video.R.string.Word_getverification);
                verificationCode.setClickable(true);
            }
        }
        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            if (verificationCode != null) {
                verificationCode.setText("剩余" + millisUntilFinished / 1000 + "秒");
            }
        }
    }
}
