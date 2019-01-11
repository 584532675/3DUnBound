package com.unbounded.video.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.unbounded.video.constants.Constants;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class HttpPostUtil implements Runnable {
	Handler handler;
	String url;
	Map<String,Object> map;
	List<String> keylist;
	List<?> valuelist;
	int code;
	HttpURLConnection httpURLConnection;
	PrintWriter printWriter;
	BufferedInputStream bis;
	ByteArrayOutputStream bos;
	private long startTime,finishTime;
	
	public HttpPostUtil(Handler handler, String url, Map map, int code) {
		super();
		this.handler = handler;
		this.url = url;
		this.map = map;
		this.code = code;
	}

	public HttpPostUtil(Handler handler, String url, List<String> keylist,
						List<?> valuelist, int code,int object) {
		super();
		this.handler = handler;
		this.url = url;
		this.keylist = keylist;
		this.valuelist = valuelist;
		this.code = code;
	}
	public HttpPostUtil(Handler handler, String url, List<String> keylist,
						List<String> valuelist, int code) {
		super();
		this.handler = handler;
		this.url = url;
		this.keylist = keylist;
		this.valuelist = valuelist;
		this.code = code;
	}
	@Override
	public void run() {
		startTime = System.currentTimeMillis();

		URL url1 = null;
		try {
			url1 = new URL(url);
			httpURLConnection = (HttpURLConnection) url1.openConnection();
            httpURLConnection.setRequestMethod("POST");// 提交模式
        	httpURLConnection.setConnectTimeout(10000);//jdk 1.5换成这个,连接超时
            httpURLConnection.setReadTimeout(10000);//jdk 1.5换成这个,读操作超时
            // 发送POST请求必须设置如下两行
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();
            // 获取URLConnection对象对应的输出流
            printWriter = new PrintWriter(httpURLConnection.getOutputStream());
            // 发送请求参数
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < keylist.size(); i++) {
            	if(i < keylist.size() - 1){
            		stringBuilder.append(keylist.get(i) + "=" + valuelist.get(i) + "&");
            	}

            	if(i == keylist.size() - 1){
            		stringBuilder.append(keylist.get(i) + "=" + valuelist.get(i));
            	}
			}
            
            Log.e("info", "stringBuilder =" + stringBuilder.toString());
            printWriter.write(stringBuilder.toString());
            
            // flush输出流的缓冲
            printWriter.flush();
			Log.e("info", "6");

            
            if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
				Log.e("info", "66");
            	//开始获取数据
                bis = new BufferedInputStream(httpURLConnection.getInputStream());
                bos = new ByteArrayOutputStream();
                int len;
                byte[] arr = new byte[1024];
                while((len=bis.read(arr))!= -1){
                    bos.write(arr,0,len);
                    bos.flush();
                }
                
                String value = bos.toString("utf-8");
                
                Log.e("info", "valuepost="+value);
				finishTime = System.currentTimeMillis();


                Message msg = new Message();
                msg.what = code;
                msg.obj = value;
				handler.sendMessage(msg);

            }
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			//网络访问超时监听
			Message msg = new Message();
            msg.what = Constants.INTERNET_ERROR_FLAG;
            handler.sendMessage(msg);
			e.printStackTrace();
		}
		
		finally{
			if(httpURLConnection != null){
				httpURLConnection.disconnect();
			}
			
			if(printWriter != null){
				printWriter.close();
			}
			
			if(bis != null){
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(bos != null){
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}       
		
	}
	
	
	
}
