package com.unbounded.video.utils;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import com.unbounded.video.bean.LocalFilm;
import com.unbounded.video.constants.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjf on 2017/7/19.
 */

public class LocalFilmRun implements Runnable {
    Context context;
    Handler handler;
    int code,localType;
    List<LocalFilm> films = new ArrayList<LocalFilm>();
    public LocalFilmRun(Context context, Handler handler, int code/* ,List<LocalFilm> films*/) {
        super();
        this.context = context;
        this.handler = handler;
        this.code = code;
//        this.films = films;
    }

    @Override
    public void run() {
        if (context != null) {

            try {
                Cursor cursor = context.getContentResolver().query(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null,
                        null, null);

                if(cursor != null){
                    while (cursor.moveToNext()) {
                        String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM));
                        String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST));
                        long  duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));

                        int id = cursor.getInt(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                        String title = cursor
                                .getString(cursor
                                        .getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                        String displayName = cursor
                                .getString(cursor
                                        .getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                        String mimeType = cursor
                                .getString(cursor
                                        .getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                        String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                        long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));


                        LocalFilm film = new LocalFilm(id, title, album, artist, displayName, mimeType, path, duration, size, -1, 0);
                        films.add(film);
                    }

                    Message msg = new Message();
                    msg.what = code;
                    msg.obj = films;
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
