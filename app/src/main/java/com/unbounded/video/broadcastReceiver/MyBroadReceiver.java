package com.unbounded.video.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.unbounded.video.constants.Constants;
import com.unbounded.video.utils.SharedPrefsUtil;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by zjf on 2017/8/1 0001.
 */

public class MyBroadReceiver extends BroadcastReceiver {
    private static final String TAG ="info";
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();

        Log.e(TAG, "id1="+ JPushInterface.getRegistrationID(context));
        String Regid = JPushInterface.getRegistrationID(context);

        if(!(TextUtils.isEmpty(Regid))){
            SharedPrefsUtil.putValue(context, Constants.Regid_FLAG, Regid);
        }

//        Log.d(TAG, "onReceive - " + intent.getAction());
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
        }else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            System.out.println("收到了自定义消息。消息内容是：" + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            Log.e(TAG, "收到了自定义消息。消息内容是："+bundle.getString(JPushInterface.EXTRA_MESSAGE));
            Log.e(TAG, "收到了="+bundle.getString(JPushInterface.EXTRA_EXTRA));
            // 自定义消息不会展示在通知栏，完全要开发者写代码去处理
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
//            System.out.println("收到了通知");
            Log.e(TAG, "收到了通知");
            // 在这里可以做些统计，或者做些其他工作
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Toast.makeText(context, "点开了通知", Toast.LENGTH_SHORT).show();
            // 在这里可以自己写代码去定义用户点击后的行为
            Log.e(TAG, "点开了通知");

//            Intent i = new Intent(context, MainActivity.class);  //自定义打开的界面
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(i);
        }else {
            Log.e(TAG, "Unhandled intent - " + intent.getAction());
        }

    }

}
