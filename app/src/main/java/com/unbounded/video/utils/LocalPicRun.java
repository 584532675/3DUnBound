package com.unbounded.video.utils;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import com.unbounded.video.bean.LocalPic;
import com.unbounded.video.constants.Constants;

import java.util.List;

/**
 * Created by zjf on 2017/7/19.
 */

public class LocalPicRun implements Runnable {
    Context context;
    Handler handler;
    int code,localType;
    List<LocalPic> pics;

    public LocalPicRun(Context context, Handler handler, int code , List<LocalPic> pics) {
        super();
        this.context = context;
        this.handler = handler;
        this.code = code;
        this.pics = pics;
    }

    @Override
    public void run() {
        if (context != null) {
            try{
                Cursor cursor = context.getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null,
                        null, null);

                if(cursor != null){
                    while (cursor.moveToNext()) {
                        int id = cursor.getInt(cursor
                                .getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                        String title = cursor
                                .getString(cursor
                                        .getColumnIndexOrThrow(MediaStore.Images.Media.TITLE));
                        String displayName = cursor
                                .getString(cursor
                                        .getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                        String mimeType = cursor
                                .getString(cursor
                                        .getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE));
                        String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                        long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));

                        LocalPic pic = new LocalPic(id, title, displayName, mimeType, path, size);
                        pics.add(pic);
                    }

                    Message msg = new Message();
                    msg.what = code;
                    msg.obj = pics;
                    handler.sendMessage(msg);

                }
            }catch (SecurityException e){
                //没权限的错误
                Message msg = new Message();
                msg.what = Constants.PERMISSION_ERROR_FLAG;
                handler.sendMessage(msg);
                e.printStackTrace();
            }
        }
    }
}
