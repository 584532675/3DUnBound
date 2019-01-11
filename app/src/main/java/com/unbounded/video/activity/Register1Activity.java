package com.unbounded.video.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.unbounded.video.BaseActivity;
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
import java.util.List;

/**
 * Created by zjf on 2017/7/19 0019.
 * 注册第二步
 */

public class Register1Activity extends BaseActivity {
    public final static int Register_Flag = 0;
    public final static int AutoLogon_Flag = 1;
    String mobileStr,psw1,psw2,Regid,phoneNum;
    ContainsEmojiEditText pswedt1,pswedt2;
    TextView contenttv2;
    Button commitbtn;
    ImageView pswvisibleiv,psw2visibleiv;
    boolean pswvisible = false,psw2visible = false;
    Dialog contentDialog;

    List<String> keyList = new ArrayList<String>();
    List<String> valueList = new ArrayList<String>();

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //注册
                case Register_Flag:
                    String Registervalue = msg.obj.toString();
                    Log.e("info","Registervalue="+Registervalue);
                    progressDiddismiss();

                    if (!(TextUtils.isEmpty(Registervalue))){
                        try {
                            JSONObject registerjo = new JSONObject(Registervalue);
                            String result = registerjo.getString("result");
                            String message = registerjo.getString("msg");

                            if(Constants.Result_Success.equals(result)){
                                progressDid1dismiss();
                                //ToastUtil.showToast(RegisterActivity.this, R.string.Word_registersuccess, ToastUtil.CENTER);
                                /**
                                 * 注册成功
                                 */
                                JSONObject personObject = null;
                                try {
                                    personObject = new JSONObject();
                                    personObject.put("phoneNum", mobileStr);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ZhugeSDK.getInstance().track(getApplicationContext(),"注册成功次数",personObject);

                                //自动登录
                                postMobilePsw();

                            }else if(Constants.Result_Fail.equals(result)){
                                progressDid1dismiss();
                                ToastUtil.showToast(Register1Activity.this, message, ToastUtil.CENTER);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                    break;
                //
                case AutoLogon_Flag:
                    String loginvalue = msg.obj.toString();
                    Log.e("info","loginvalue="+loginvalue);

                    if(!(TextUtils.isEmpty(loginvalue))){
                        autoLoginCallback(loginvalue);
                    }

                    break;
                case Constants.INTERNET_ERROR_FLAG:
                    ToastUtil.showToast(Register1Activity.this, com.unbounded.video.R.string.internet_error, ToastUtil.CENTER);
                    progressDid1dismiss();
                    break;
            }
        }
    };

    /**
     * 自动登录回调
     */
    private void autoLoginCallback(String value){
        try {
            JSONObject loginjo = new JSONObject(value);
            String result = loginjo.getString("result");
            String message = loginjo.getString("msg");
            //成功
            if(Constants.Result_Success.equals(result)){
                progressDid1dismiss();
//                ToastUtil.showToast(Register1Activity.this, R.string.Word_loginsuccess, ToastUtil.BOTTOM);

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


//                ExitApplication.getInstance().exitLoginActivitys();
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), Register2Activity.class);
                startActivity(intent);
                finish();

                //失败
            }else if(Constants.Result_Fail.equals(result)){
                progressDid1dismiss();
                ToastUtil.showToast(Register1Activity.this, message, ToastUtil.BOTTOM);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 自动登录发起
     */
    private void postMobilePsw(){
        progressDid1resid(com.unbounded.video.R.string.Word_autologining);
        keyList.clear();
        valueList.clear();

        keyList.add("phone");
        keyList.add("password");
        keyList.add("regid");
        valueList.add(mobileStr);
        valueList.add(psw1);
        valueList.add(Regid);

        String loginurl = HttpConstants.Login_Url;
        HttpPostUtil post = new HttpPostUtil(handler, loginurl, keyList, valueList, AutoLogon_Flag);
        Thread thread = new Thread(post);
        thread.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        whiteBar();
    }

    @Override
    protected int getContentView() {
        return com.unbounded.video.R.layout.activity_register1;
    }

    @Override
    public void init(Intent intent) {
        super.init(intent);

        setTitleViewVisible(true);
        setTitleBg(com.unbounded.video.R.color.white);
        setLeftBtnBg(com.unbounded.video.R.mipmap.black_back);

        ExitApplication.getInstance().addLoginActivity(this);

        Regid = SharedPrefsUtil.getValue(getApplicationContext(), Constants.Regid_FLAG, "");
        mobileStr = intent.getStringExtra("mobileStr");
    }

    @Override
    public void initView() {
        super.initView();

        pswedt1 = (ContainsEmojiEditText) findViewById(com.unbounded.video.R.id.register_pswedt);
        pswedt2 = (ContainsEmojiEditText) findViewById(com.unbounded.video.R.id.register_repswedt);
        contenttv2 = (TextView) findViewById(com.unbounded.video.R.id.register_contenttv2);
        commitbtn = (Button) findViewById(com.unbounded.video.R.id.register_registerbtn);
        pswvisibleiv = (ImageView) findViewById(com.unbounded.video.R.id.register_pswvisible);
        psw2visibleiv = (ImageView) findViewById(com.unbounded.video.R.id.register_repswvisible);

    }

    @Override
    public void initEvent() {
        super.initEvent();

        commitbtn.setOnClickListener(this);
        pswvisibleiv.setOnClickListener(this);
        psw2visibleiv.setOnClickListener(this);
        contenttv2.setOnClickListener(this);
    }

    //提交
    private void commitMethod(){
        psw1 = pswedt1.getText().toString().trim();
        psw2 = pswedt2.getText().toString().trim();

        if(TextUtils.isEmpty(psw1)){
            ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_pswhint, ToastUtil.CENTER);
            return;
        }
        if(TextUtils.isEmpty(psw2)){
            ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_psw2hint, ToastUtil.CENTER);
            return;
        }
        if(psw1.length() < 6){
            ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_pswlength, ToastUtil.CENTER);
            return;
        }

        if(psw1.equals(psw2)){
            postCommit();
        }else {
            ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_twopswdiff, ToastUtil.CENTER);
        }

    }

    private void postCommit(){
        progressDid();

        keyList.clear();
        valueList.clear();

        keyList.add("phone");
        keyList.add("password");
        keyList.add("regid");
        valueList.add(mobileStr);
        valueList.add(psw1);
        valueList.add(Regid);

        String forgeturl = HttpConstants.InsertNoCode_Url;
        HttpPostUtil post = new HttpPostUtil(handler, forgeturl, keyList, valueList, Register_Flag);
        Thread thread = new Thread(post);
        thread.start();

        /**
         * 注册
         */
        phoneNum = SharedPrefsUtil.getValue(getApplicationContext(), Constants.Phone_FLAG, "");
        ZhugeSDK.getInstance().track(getApplicationContext(),"注册次数"+phoneNum);
    }

    /**
     * 密码可见切换
     */
    private void pswView(){
        psw1 = pswedt1.getText().toString().trim();

        if(pswvisible == false){
            if(!(TextUtils.isEmpty(psw1))){
                //选择状态 显示明文--设置为可见的密码
                pswedt1.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                pswvisibleiv.setImageResource(com.unbounded.video.R.mipmap.psweyecloseiv);
                pswvisible = true;
            }
        }else {
            //默认状态显示密码--设置文本 要一起写才能起作用
            pswedt1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            pswvisibleiv.setImageResource(com.unbounded.video.R.mipmap.eyeicon);
            pswvisible = false;
        }
        pswedt1.setSelection(pswedt1.getText().toString().length());
    }
    /**
     * 确认密码可见切换
     */
    private void psw2View(){
        psw2 = pswedt2.getText().toString().trim();

        if(psw2visible == false){
            if(!(TextUtils.isEmpty(psw2))){
                //选择状态 显示明文--设置为可见的密码
                pswedt2.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                psw2visibleiv.setImageResource(com.unbounded.video.R.mipmap.psweyecloseiv);
                psw2visible = true;
            }
        }else {
            //默认状态显示密码--设置文本 要一起写才能起作用
            pswedt2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            psw2visibleiv.setImageResource(com.unbounded.video.R.mipmap.eyeicon);
            psw2visible = false;
        }
        pswedt2.setSelection(pswedt2.getText().toString().length());
    }

    /**
     * 用户协议
     */
    private void contentDia(){
        View diaView = View.inflate(Register1Activity.this, com.unbounded.video.R.layout.content_dialog, null);
        contentDialog = new Dialog(Register1Activity.this, com.unbounded.video.R.style.dialog);
        contentDialog.setContentView(diaView);
        contentDialog.setCanceledOnTouchOutside(false);
        contentDialog.setCancelable(false);

        Button yesbtn = (Button) contentDialog.findViewById(com.unbounded.video.R.id.content_yesbtn);
        TextView titletv = (TextView) contentDialog.findViewById(com.unbounded.video.R.id.content_tv1);
        TextView contenttv = (TextView) contentDialog.findViewById(com.unbounded.video.R.id.content_tv2);

        yesbtn.setText(getString(com.unbounded.video.R.string.sure_btn));
        titletv.setText(getString(com.unbounded.video.R.string.Word_contenttitle1));
        contenttv.setText(getString(com.unbounded.video.R.string.Word_usercontent));

        yesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentDialog.dismiss();
            }
        });

        contentDialog.show();

        WindowManager.LayoutParams params = contentDialog.getWindow().getAttributes();
        params.width = screenWidth * 3 / 4;
        params.height = screenHeight * 3 / 4 ;
        contentDialog.getWindow().setAttributes(params);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            //提交
            case com.unbounded.video.R.id.register_registerbtn:
                commitMethod();

                break;
            //隐藏新密码
            case com.unbounded.video.R.id.register_pswvisible:
                pswView();
                break;
            //隐藏确认密码
            case com.unbounded.video.R.id.register_repswvisible:
                psw2View();
                break;
            //用户协议
            case com.unbounded.video.R.id.register_contenttv2:
                hideInputMode();
                contentDia();

                break;
        }
    }
}
