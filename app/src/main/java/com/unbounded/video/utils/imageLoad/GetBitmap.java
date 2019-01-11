package com.unbounded.video.utils.imageLoad;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2017/8/11.
 */

public class GetBitmap implements Runnable {
    Handler handler;
    String url;
    int code,screenWidth,screenHeight;

    HttpURLConnection conn;
    InputStream is;


    public GetBitmap(Handler handler, String url, int code, int screenWidth, int screenHeight) {
        this.handler = handler;
        this.url = url;
        this.code = code;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    @Override
    public void run() {
        // 显示网络上的图片
        Bitmap bitmap = null;
        int scale = 1;
        try {
            URL myFileUrl = new URL(url);
            conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setConnectTimeout(10000);//jdk 1.5换成这个,连接超时
            conn.setReadTimeout(10000);//jdk 1.5换成这个,读操作超时
            conn.setDoInput(true);
            conn.connect();

            if(200 == conn.getResponseCode()){
                is = conn.getInputStream();
                //得到图片的宽高。
                BitmapFactory.Options opts = new BitmapFactory.Options();// 解析位图的附加条件
                opts.inJustDecodeBounds = true;// 不去解析真实的位图，只是获取这个位图的头文件信息
                opts.inPurgeable = true;
                opts.inInputShareable = true;
                BitmapFactory.decodeStream(is, null, opts);
                int bitmapWidth = opts.outWidth;
                int bitmapHeight = opts.outHeight;

                //计算缩放比例
                int dx = bitmapWidth / screenHeight;
                int dy = bitmapHeight / screenWidth;

                if (dx > dy && dy >= 1) {
                    scale = dx;
                }

                if (dy >= dx && dx >= 1) {
                    scale = dy;
                }

                is.close();
                initConnection();

                opts.inSampleSize = scale;
                opts.inJustDecodeBounds = false;// 真正的去解析这个位图。
                opts.inPurgeable = true;
                opts.inInputShareable = true;
                try {
                    bitmap = BitmapFactory.decodeStream(is, null, opts);

                }catch (Exception e){

                }

                Message msg = handler.obtainMessage();
                msg.what = code;
                msg.obj = bitmap;
                handler.sendMessage(msg);


            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            bitmap = null;
        } catch (IOException e) {
            e.printStackTrace();
            bitmap = null;
        }finally{
            if(conn != null){
                conn.disconnect();
            }

            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private InputStream initConnection() throws IOException {
        URL myFileUrl = new URL(url);
        conn = (HttpURLConnection) myFileUrl.openConnection();
        conn.setConnectTimeout(10000);//jdk 1.5换成这个,连接超时
        conn.setReadTimeout(10000);//jdk 1.5换成这个,读操作超时
        conn.setDoInput(true);
        conn.connect();
        is = conn.getInputStream();
        return is;
    }

}
