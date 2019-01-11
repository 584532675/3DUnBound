package com.unbounded.video.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.unbounded.video.BaseActivity;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.constants.HttpConstants;
import com.unbounded.video.utils.HttpPostUtil;
import com.unbounded.video.utils.SharedPrefsUtil;
import com.unbounded.video.utils.ToastUtil;
import com.unbounded.video.view.banner.containsEmojiEditText.ContainsEmojiEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/17 0017.
 */

public class UpdatePhoneActivity extends BaseActivity {
    public final static int Checkcode_Flag = 0;
    TextView getcodebtn;
    Button nextbtn;
    ContainsEmojiEditText oldphoneedt,codeedt;
    String mobileStr,codeStr,msgId;
    TimeCount time;

    List<String> keyList = new ArrayList<String>();
    List<String> valueList = new ArrayList<String>();

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //获取验证码
                case Constants.INTERNET_SUCCESS_FLAG:
                    String codevalue = msg.obj.toString();
                    Log.e("info","codevalue="+codevalue);
                    if (!(TextUtils.isEmpty(codevalue))) {
                        try {
                            JSONObject codejo = new JSONObject(codevalue);
                            String result = codejo.getString("result");
                            String message = codejo.getString("msg");

                            if(Constants.Result_Success.equals(result)){
                                ToastUtil.showToast(UpdatePhoneActivity.this, com.unbounded.video.R.string.Word_codesuccess, ToastUtil.CENTER);
                                getcodebtn.setClickable(false);
                                time = new TimeCount(60000, 1000);
                                time.start();

                                msgId = codejo.getString("msgId");
                            }else if(Constants.Result_Fail.equals(result)){
                                ToastUtil.showToast(UpdatePhoneActivity.this, com.unbounded.video.R.string.Word_noregister, ToastUtil.CENTER);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    break;
                //验证验证码
                case Checkcode_Flag:
                    String checkvalue = msg.obj.toString();
                    Log.e("info","checkvalue="+checkvalue);
                    progressDiddismiss();
                    if("true".equals(checkvalue)){
                        openActivity(UpdatePhone2Activity.class);
                        finish();

                    }else if("false".equals(checkvalue)){
                        ToastUtil.showToast(UpdatePhoneActivity.this, com.unbounded.video.R.string.Word_codefail, ToastUtil.CENTER);

                    }

                    break;
                case Constants.INTERNET_ERROR_FLAG:
                    ToastUtil.showToast(UpdatePhoneActivity.this, com.unbounded.video.R.string.internet_error, ToastUtil.CENTER);
                    progressDiddismiss();
                    break;


            }
        }
    };

    @Override
    protected int getContentView() {
        return com.unbounded.video.R.layout.activity_changephone;
    }

    @Override
    public void init(Intent intent) {
        super.init(intent);

        setTitleName(getResources().getString(com.unbounded.video.R.string.Word_sureown));
    }

    @Override
    public void initView() {
        super.initView();

        getcodebtn = (TextView) findViewById(com.unbounded.video.R.id.updatepsw_timebtn);
        nextbtn = (Button) findViewById(com.unbounded.video.R.id.updatepsw_nextbtn);
        oldphoneedt = (ContainsEmojiEditText) findViewById(com.unbounded.video.R.id.updatepsw_oldphoneedt);
        codeedt = (ContainsEmojiEditText) findViewById(com.unbounded.video.R.id.updatepsw_codeedt);
    }

    @Override
    public void initEvent() {
        super.initEvent();

        mobileStr = SharedPrefsUtil.getValue(getApplicationContext(), Constants.Phone_FLAG, userId);
        oldphoneedt.setText(mobileStr);

        getcodebtn.setOnClickListener(this);
        nextbtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            //验证码
            case com.unbounded.video.R.id.updatepsw_timebtn:
                hideInputMode();
                getCodeMethod();

                break;
            //下一步
            case com.unbounded.video.R.id.updatepsw_nextbtn:
                hideInputMode();
                checkcodeMethod();

                break;

        }
    }

    /**
     * 下一步验证
     */
    private void checkcodeMethod(){
        codeStr = codeedt.getText().toString().trim();

        if(TextUtils.isEmpty(codeStr)){
            ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_inputverification, ToastUtil.CENTER);
            return;
        }
        if(codeStr.length() != 6){
            ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_verificationsize, ToastUtil.CENTER);
            return;
        }

        postCheck();
    }
    //
    private void postCheck(){
        progressDid();
        keyList.clear();
        valueList.clear();

        keyList.add("code");
        keyList.add("msgId");
        valueList.add(codeStr);
        valueList.add(msgId);

        String codeurl = HttpConstants.UpdatePhonecheckCode_Url;
        HttpPostUtil post = new HttpPostUtil(handler, codeurl, keyList, valueList, Checkcode_Flag);
        Thread thread = new Thread(post);
        thread.start();
    }

    /**
     * 获取验证码
     */
    private void getCodeMethod(){
        mobileStr = oldphoneedt.getText().toString().trim();

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
