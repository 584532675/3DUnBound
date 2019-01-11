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
import com.unbounded.video.BaseActivity;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.constants.HttpConstants;
import com.unbounded.video.utils.ExitApplication;
import com.unbounded.video.utils.HttpPostUtil;
import com.unbounded.video.utils.ToastUtil;
import com.unbounded.video.view.banner.containsEmojiEditText.ContainsEmojiEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjf on 2017/7/19 0019.
 */

public class ForgetPswActivity extends BaseActivity {
    public final static int ForgetPsw_Flag = 0;
    ContainsEmojiEditText mobilenumedt,pswedt,psw2edt,recodeedt;
    Button commitbtn;
    TextView getcodebtn;
    ImageView pswvisiblebtn,psw2visiblebtn,deletephoneiv;
    boolean pswvisible = false,psw2visible = false;
    String pswStr,psw2Str,mobileStr,recodeStr,msgId;
    TimeCount time;

    List<String> keyList = new ArrayList<String>();
    List<String> valueList = new ArrayList<String>();

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //验证码
                case Constants.INTERNET_SUCCESS_FLAG:
                    String codevalue = msg.obj.toString();
                    Log.e("info","codevalue="+codevalue);
                    if (!(TextUtils.isEmpty(codevalue))) {
                        try {
                            JSONObject codejo = new JSONObject(codevalue);
                            String result = codejo.getString("result");
                            String message = codejo.getString("msg");

                            if(Constants.Result_Success.equals(result)){
                                ToastUtil.showToast(ForgetPswActivity.this, com.unbounded.video.R.string.Word_codesuccess, ToastUtil.CENTER);
                                getcodebtn.setClickable(false);
                                time = new TimeCount(60000, 1000);
                                time.start();

                                msgId = codejo.getString("msgId");
                            }else if(Constants.Result_Fail.equals(result)){
                                ToastUtil.showToast(ForgetPswActivity.this, com.unbounded.video.R.string.Word_noregister, ToastUtil.CENTER);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    break;
                //忘记密码
                case ForgetPsw_Flag:
                    String forgetvalue = msg.obj.toString();
                    Log.e("info","forgetvalue="+forgetvalue);
                    progressDiddismiss();
                    if (!(TextUtils.isEmpty(forgetvalue))) {
                        if("true".equals(forgetvalue)){
//                            ToastUtil.showToast(ForgetPswActivity.this, R.string.Word_forgetpswsuccess, ToastUtil.CENTER);
                                Intent intent = new Intent();
                                intent.setClass(getApplicationContext(),ForgetPsw2Activity.class);
                                intent.putExtra("mobileStr",mobileStr);
                                intent.putExtra("recodeStr",recodeStr);
                                intent.putExtra("msgId",msgId);
                                startActivity(intent);

                        }else if("false".equals(forgetvalue)){
                            ToastUtil.showToast(ForgetPswActivity.this, com.unbounded.video.R.string.Word_codefail, ToastUtil.CENTER);
                        }
                    }

                    break;
                case Constants.INTERNET_ERROR_FLAG:
                    ToastUtil.showToast(ForgetPswActivity.this, com.unbounded.video.R.string.internet_error, ToastUtil.CENTER);
                    progressDid1dismiss();
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
        return com.unbounded.video.R.layout.activity_forget1;
    }

    @Override
    public void init(Intent intent) {
        super.init(intent);

        setTitleViewVisible(true);
        setTitleBg(com.unbounded.video.R.color.white);
        setLeftBtnBg(com.unbounded.video.R.mipmap.black_back);

        ExitApplication.getInstance().addLoginActivity(this);
        ExitApplication.getInstance().addForgetActivity(this);

    }

    @Override
    public void initView() {
        super.initView();

        mobilenumedt = (ContainsEmojiEditText) findViewById(com.unbounded.video.R.id.register_mobilenumedt);
        pswedt = (ContainsEmojiEditText) findViewById(com.unbounded.video.R.id.register_pswedt);
        psw2edt = (ContainsEmojiEditText) findViewById(com.unbounded.video.R.id.register_repswedt);
        recodeedt = (ContainsEmojiEditText) findViewById(com.unbounded.video.R.id.register_verificationedt);
        getcodebtn = (TextView) findViewById(com.unbounded.video.R.id.register_timebtn);
        commitbtn = (Button) findViewById(com.unbounded.video.R.id.register_registerbtn);
        pswvisiblebtn = (ImageView) findViewById(com.unbounded.video.R.id.register_pswvisible);
        psw2visiblebtn = (ImageView) findViewById(com.unbounded.video.R.id.register_repswvisible);
        deletephoneiv = (ImageView) findViewById(com.unbounded.video.R.id.register_deletephoneiv);

    }

    @Override
    public void initEvent() {
        super.initEvent();

        getcodebtn.setOnClickListener(this);
        commitbtn.setOnClickListener(this);
        pswvisiblebtn.setOnClickListener(this);
        psw2visiblebtn.setOnClickListener(this);
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
            //密码
            case com.unbounded.video.R.id.register_pswvisible:
                pswView();
                break;
            //确认密码
            case com.unbounded.video.R.id.register_repswvisible:
                rePswView();
                break;
            //获取验证码
            case com.unbounded.video.R.id.register_timebtn:
                hideInputMode();
                getCodeMethod();
                break;
            //提交
            case com.unbounded.video.R.id.register_registerbtn:
                hideInputMode();
                commitMethod();
                break;
        }
    }

    /**
     * 提交
     */
    private void commitMethod(){
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

        postCommit();

//        Intent intent = new Intent();
//        intent.setClass(getApplicationContext(),ForgetPsw2Activity.class);
//        intent.putExtra("mobileStr",mobileStr);
//        intent.putExtra("recodeStr",recodeStr);
//        intent.putExtra("msgId",msgId);
//        startActivity(intent);
    }
    //
    private void postCommit(){
        progressDid();

        keyList.clear();
        valueList.clear();

        keyList.add("code");
        keyList.add("msgId");
        valueList.add(recodeStr);
        valueList.add(msgId);

        String forgeturl = HttpConstants.CheckCode_Url;
        HttpPostUtil post = new HttpPostUtil(handler, forgeturl, keyList, valueList, ForgetPsw_Flag);
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

        String codeurl = HttpConstants.ForgetpswGetCode_Url;
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
                pswvisible = true;
            }
        }else {
            //默认状态显示密码--设置文本 要一起写才能起作用
            pswedt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
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
                psw2visible = true;
            }
        }else {
            //默认状态显示密码--设置文本 要一起写才能起作用
            psw2edt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
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
