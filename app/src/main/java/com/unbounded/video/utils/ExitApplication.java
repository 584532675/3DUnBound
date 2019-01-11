package com.unbounded.video.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.unbounded.video.constants.Constants;

import java.util.LinkedList;
import java.util.List;

public class ExitApplication/* extends Application */{
	private List<Activity> activityList = new LinkedList<Activity>();
	private List<Activity> loginactivityList = new LinkedList<Activity>();
	private List<Activity> forgetactivityList = new LinkedList<Activity>();
	private static ExitApplication instance;
	
	 private ExitApplication(){}
	 
	//单例模式中获取唯一的ExitApplication实例
	 public static ExitApplication getInstance(){
		 if(null == instance){
			 instance = new ExitApplication();

		 }
		 return instance; 

	 }
	//添加Activity到容器中
	 public void addActivity(Activity activity){
		 activityList.add(activity);
	 }
	//添加Activity到容器中
	 public void addLoginActivity(Activity activity){
		 loginactivityList.add(activity);
	 }
	 public void addForgetActivity(Activity activity){
		 forgetactivityList.add(activity);
	 }
	//遍历所有Activity并finish,退出整个app
	public void exit(Context context) {
		String userId = SharedPrefsUtil.getValue(context, Constants.UserId_FLAG, "");
		if (!TextUtils.isEmpty(userId)) {
			ACache aCache = ACache.get(context);
			long endTime = System.currentTimeMillis();
			long startTime = (long) aCache.getAsObject(Constants.START_USER_TIME);
			int time = (int) (endTime - startTime);
			time = time / 1000 / 60;
			Log.v("userTime:", "" + time);
			if (time > 0) {
				aCache.put(Constants.TIME_USER_ID, userId);
				aCache.put(Constants.USER_REDUCE_TIME, time);
			}
		}

		 for(Activity activity:activityList){
			 activity.finish();
		 }
		 System.exit(/*-1*/0);

	 }
	 
	 //销毁前面的activity
	 public void exitAllActivitys(){
		 for(Activity activity:activityList){
			 activity.finish();
		 }
		 System.gc();
	 }
	 //销毁前面登录相关的activity
	 public void exitLoginActivitys(){
		 for(Activity activity:loginactivityList){
			 activity.finish();
		 }
		 System.gc();
	 }
	 //销毁前面登录相关的activity
	 public void exitForgetActivitys(){
		 for(Activity activity:forgetactivityList){
			 activity.finish();
		 }
		 System.gc();
	 }


	 
}
