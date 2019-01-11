package com.unbounded.video.activity;

import android.app.Dialog;
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

import com.unbounded.video.BaseActivity;
import com.unbounded.video.R;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.constants.HttpConstants;
import com.unbounded.video.utils.ExitApplication;
import com.unbounded.video.utils.HttpPostUtil;
import com.unbounded.video.utils.SharedPrefsUtil;
import com.unbounded.video.utils.ToastUtil;
import com.unbounded.video.view.banner.containsEmojiEditText.ContainsEmojiEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjf on 2017/7/19 0019.
 */

public class RegisterActivity extends BaseActivity {
    public final static int Register_Flag = 0;
    ContainsEmojiEditText mobilenumedt,pswedt,psw2edt,recodeedt;
    Button registerbtn;
    ImageView pswvisiblebtn,psw2visiblebtn,deletephoneiv;
    TextView contenttv2,getcodebtn;
    boolean pswvisible = false,psw2visible = false;
    String mobileStr,pswStr,psw2Str,recodeStr,Regid,msgId,phoneNum;
    TimeCount time;
    Dialog contentDialog;

    List<String> keyList = new ArrayList<String>();
    List<String> valueList = new ArrayList<String>();

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //验证码
                case Constants.INTERNET_SUCCESS_FLAG:
                    String codevalue = msg.obj.toString();
                    if(!(TextUtils.isEmpty(codevalue))){
                        try {
                            JSONObject codejo = new JSONObject(codevalue);
                            String result = codejo.getString("result");
                            String message = codejo.getString("msg");

                            if(Constants.Result_Success.equals(result)){
                                ToastUtil.showToast(RegisterActivity.this, com.unbounded.video.R.string.Word_codesuccess, ToastUtil.CENTER);
                                getcodebtn.setClickable(false);
                                time = new TimeCount(60000, 1000);
                                time.start();

                                msgId = codejo.getString("msgId");
                            }else if(Constants.Result_Fail.equals(result)){
                                ToastUtil.showToast(RegisterActivity.this, message, ToastUtil.CENTER);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    break;
                //下一步
                case Register_Flag:
                    String registervalue = msg.obj.toString();
                    Log.e("info","registernextvalue="+registervalue);
                    if (!(TextUtils.isEmpty(registervalue))) {
                        if("true".equals(registervalue)){
                            Intent intent = new Intent();
                            intent.setClass(getApplicationContext(),Register1Activity.class);
                            intent.putExtra("mobileStr",mobileStr);
                            startActivity(intent);

                        }else if("false".equals(registervalue)){
                            ToastUtil.showToast(RegisterActivity.this, com.unbounded.video.R.string.Word_codefail, ToastUtil.CENTER);
                        }
                    }

                    progressDiddismiss();

                    break;
                case Constants.INTERNET_ERROR_FLAG:
                    ToastUtil.showToast(RegisterActivity.this, com.unbounded.video.R.string.internet_error, ToastUtil.CENTER);
                    progressDiddismiss();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        whiteBar();
    }

    @Override
    protected int getContentView() {
        return com.unbounded.video.R.layout.activity_register;
    }

    @Override
    public void init(Intent intent) {
        super.init(intent);

        setTitleViewVisible(true);
        setTitleBg(com.unbounded.video.R.color.white);
        setLeftBtnBg(com.unbounded.video.R.mipmap.black_back);
        ExitApplication.getInstance().addLoginActivity(this);
//        setTitleName(getResources().getString(R.string.Word_mobileregister));
        setRightBtnVisible(true);
        setRightBtnTitle(getString(R.string.Word_login));
        setRightBtnTitlecolor(R.drawable.black2gray_textcolor);
        Regid = SharedPrefsUtil.getValue(getApplicationContext(), Constants.Regid_FLAG, "");
    }

    @Override
    public void onRightClick(View v) {
        super.onRightClick(v);
        hideInputMode();
        openActivity(LoginActivity.class);
    }

    @Override
    public void initView() {
        super.initView();

        mobilenumedt = (ContainsEmojiEditText) findViewById(com.unbounded.video.R.id.register_mobilenumedt);
        pswedt = (ContainsEmojiEditText) findViewById(com.unbounded.video.R.id.register_pswedt);
        psw2edt = (ContainsEmojiEditText) findViewById(com.unbounded.video.R.id.register_repswedt);
        recodeedt = (ContainsEmojiEditText) findViewById(com.unbounded.video.R.id.register_verificationedt);
        getcodebtn = (TextView) findViewById(com.unbounded.video.R.id.register_timebtn);
        contenttv2 = (TextView) findViewById(com.unbounded.video.R.id.register_contenttv2);
        registerbtn = (Button) findViewById(com.unbounded.video.R.id.register_registerbtn);
        pswvisiblebtn = (ImageView) findViewById(com.unbounded.video.R.id.register_pswvisible);
        psw2visiblebtn = (ImageView) findViewById(com.unbounded.video.R.id.register_repswvisible);
        deletephoneiv = (ImageView) findViewById(com.unbounded.video.R.id.register_deletephoneiv);
    }

    @Override
    public void initEvent() {
        super.initEvent();
        getcodebtn.setOnClickListener(this);
        registerbtn.setOnClickListener(this);
        pswvisiblebtn.setOnClickListener(this);
        psw2visiblebtn.setOnClickListener(this);
        contenttv2.setOnClickListener(this);
        deletephoneiv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

        switch(view.getId()){
            //
            case com.unbounded.video.R.id.register_deletephoneiv:
                mobilenumedt.setText("");
                mobileStr = "";

                break;
            //用户协议
            case com.unbounded.video.R.id.register_contenttv2:
                break;

            //密码
            case com.unbounded.video.R.id.register_pswvisible:
                pswView();

                break;
            //确认密码
            case com.unbounded.video.R.id.register_repswvisible:
                rePswView();

                break;
            case com.unbounded.video.R.id.register_timebtn:
                hideInputMode();
                getCodeMethod();

                break;
            case com.unbounded.video.R.id.register_registerbtn:
                hideInputMode();
                registerMethod();

                break;
        }
    }

    /**
     * 下一步
     */
    private void registerMethod(){
        recodeStr = recodeedt.getText().toString().trim();

        if(TextUtils.isEmpty(mobileStr)){
            mobileStr = mobilenumedt.getText().toString().trim();
        }

        if(TextUtils.isEmpty(mobileStr)){
            ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_loginnamehint, ToastUtil.CENTER);
            return;
        }
        if(TextUtils.isEmpty(recodeStr)){
            ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_inputverification, ToastUtil.CENTER);
            return;
        }
        if(mobileStr.length() != 11){
            ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_mobilelengtherror, ToastUtil.CENTER);
            return;
        }
        if(recodeStr.length() < 6){
            ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_verificationsize, ToastUtil.CENTER);
            return;
        }

        postRegister();


    }
    //
    private void postRegister(){
        progressDid();

        keyList.clear();
        valueList.clear();

        keyList.add("code");
        keyList.add("msgId");
        valueList.add(recodeStr);
        valueList.add(msgId);

        String forgeturl = HttpConstants.CheckCode_Url;
        HttpPostUtil post = new HttpPostUtil(handler, forgeturl, keyList, valueList, Register_Flag);
        Thread thread = new Thread(post);
        thread.start();
    }

    /**
     * 获取验证码
     */
    private void getCodeMethod(){
        mobileStr = mobilenumedt.getText().toString().trim();
        pswStr = pswedt.getText().toString().trim();
        psw2Str = psw2edt.getText().toString().trim();
        recodeStr = recodeedt.getText().toString().trim();

        if(TextUtils.isEmpty(mobileStr)){
            ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_loginnamehint, ToastUtil.CENTER);
            return;
        }
        if(mobileStr.length() != 11){
            ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_mobilelengtherror, ToastUtil.CENTER);
            return;
        }

        postCode();

    }
    //
    private void postCode(){
        keyList.clear();
        valueList.clear();

        keyList.add("phone");
        valueList.add(mobileStr);

        String codeurl = HttpConstants.GetCode_Url;
        HttpPostUtil post = new HttpPostUtil(handler, codeurl, keyList, valueList, Constants.INTERNET_SUCCESS_FLAG);
        Thread thread = new Thread(post);
        thread.start();
    }

    /**
     * 密码可见切换
     */
    private void pswView(){
        pswStr = pswedt.getText().toString().trim();

        if(pswvisible == false){
            if(!(TextUtils.isEmpty(pswStr))){
                //选择状态 显示明文--设置为可见的密码
                pswedt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                pswvisiblebtn.setImageResource(com.unbounded.video.R.mipmap.psweyecloseiv);
                pswvisible = true;
            }
        }else {
            //默认状态显示密码--设置文本 要一起写才能起作用
            pswedt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            pswvisiblebtn.setImageResource(com.unbounded.video.R.mipmap.eyeicon);
            pswvisible = false;
        }
        pswedt.setSelection(pswedt.getText().toString().length());
    }
    /**
     * 确认密码可见切换
     */
    private void rePswView(){
        psw2Str = psw2edt.getText().toString().trim();

        if(psw2visible == false){
            if(!(TextUtils.isEmpty(psw2Str))){
                //选择状态 显示明文--设置为可见的密码
                psw2edt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                psw2visiblebtn.setImageResource(com.unbounded.video.R.mipmap.psweyecloseiv);
                psw2visible = true;
            }
        }else {
            //默认状态显示密码--设置文本 要一起写才能起作用
            psw2edt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            psw2visiblebtn.setImageResource(com.unbounded.video.R.mipmap.psweyecloseiv);
            psw2visible = false;
        }
        psw2edt.setSelection(psw2edt.getText().toString().length());
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
            if (getcodebtn != null) {
                getcodebtn.setText(com.unbounded.video.R.string.Word_getverification);
                getcodebtn.setClickable(true);
            }
        }
        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            if (getcodebtn != null) {
                getcodebtn.setText("剩余" + millisUntilFinished / 1000 + "秒");
            }
        }
    }

}
