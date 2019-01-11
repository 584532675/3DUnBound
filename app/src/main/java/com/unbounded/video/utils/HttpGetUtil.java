package com.unbounded.video.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.unbounded.video.constants.Constants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpGetUtil implements Runnable {
	Handler handler;
	String url;
	int code;
	
	InputStream is;
	ByteArrayOutputStream baos;
	HttpURLConnection urlConnection;
	private long startTime,finishTime;
	int timeOut = 8000;
	public HttpGetUtil(Handler handler, String url, int code) {
		super();
		this.handler = handler;
		this.url = url;
		this.code = code;
	}
	@Override
	public void run() {
		startTime = System.currentTimeMillis();
        try {
        	URL url1 = new URL(url);
        	//打开连接
			urlConnection = (HttpURLConnection) url1.openConnection();
			urlConnection.setConnectTimeout(timeOut);//jdk 1.5换成这个,连接超时
			urlConnection.setReadTimeout(8000);//jdk 1.5换成这个,读操作超时
			Log.v("urlConnectionCode",""+urlConnection.getResponseCode());
			if(200 == urlConnection.getResponseCode()){
                //得到输入流
                is =urlConnection.getInputStream();
                baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while(-1 != (len = is.read(buffer))){
                    baos.write(buffer,0,len);
                    baos.flush();
                }
                
                String value = baos.toString("utf-8");
                Log.e("info", "valueGet="+value);
				finishTime = System.currentTimeMillis();

				Message msg = new Message();
				msg.what = code;
				msg.obj = value;
				handler.sendMessage(msg);

//				if(finishTime - startTime <= 300){
//					handler.sendMessageDelayed(msg,300);
//				}else {
//					handler.sendMessage(msg);
//				}



			}   
			
		} catch (IOException e) {
			/*
			 * 网络访问超时监听
			 */
			Message msg = new Message();
            msg.what = Constants.INTERNET_ERROR_FLAG;
            handler.sendMessage(msg);
			e.printStackTrace();
		}
		
        finally{
        	if(urlConnection != null){
        		urlConnection.disconnect();
        	}
        	
        	if(is != null){
        		try {
    				is.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
        	}
        	
        	if(baos != null){
        		try {
    				baos.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
        	}
        	
        	
        }
       
	}

		
}
