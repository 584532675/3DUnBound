package com.unbounded.video.subtitles;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Pattern;


/**
 * @Auther dwt
 * @date 2017/11/22.
 * @descraption 用于解析字幕
 */

public class SubtitlesCoding implements Runnable{
    private  static final String TAG=SubtitlesCoding.class.getSimpleName();
    /**
     * 一秒=1000毫秒
     */
    private final static int oneSecond = 1000;

    private final static int oneMinute = 60 * oneSecond;

    private final static int oneHour = 60 * oneMinute;

    /**
     * 每一个数据节点
     */
    public ArrayList<SubtitlesModel> list = new ArrayList<>();

    /**
     * 正则表达式，判断是否是时间的格式
     */
    private final static String equalStringExpress = "\\d\\d:\\d\\d:\\d\\d,\\d\\d\\d --> \\d\\d:\\d\\d:\\d\\d,\\d\\d\\d";
    private SubtitlesModel sm;


    Handler handler;
    String url;

    public SubtitlesCoding(Handler handler, String url) {
        this.handler = handler;
        this.url = url;
    }

    @Override
    public void run() {
        String line;
        BufferedReader in = null;
        /**
         * 读取文件，转流，方便读取行
         */
        try {
            URL url1 = new URL(url);
            //打开连接
            HttpURLConnection urlConnection = (HttpURLConnection) url1.openConnection();
            urlConnection.setConnectTimeout(8000);//jdk 1.5换成这个,连接超时
            urlConnection.setReadTimeout(8000);//jdk 1.5换成这个,读操作超时
            InputStream is = urlConnection.getInputStream();

            byte[] b = new byte[3];
            is.read(b);
            if (b[0] == -17 && b[1] == -69 && b[2] == -65) {
//                Log.e("info","编码为UTF-8");
                in = new BufferedReader(new InputStreamReader(is, "UTF-8"));//UTF-8
            }else{
//                Log.e("info","可能是GBK，也可能是其他编码");
                in = new BufferedReader(new InputStreamReader(is, "GBK"));//UTF-8
            }

//            in = new BufferedReader(new InputStreamReader(is, "GBK"));//UTF-8

            assert in != null;
            while ((line = in.readLine()) != null)
            {
                sm = new SubtitlesModel();
                // 匹配正则表达式，不符合提前结束当前行；
                if (Pattern.matches(equalStringExpress, line))
                {
                    // 填充开始时间数据
                    sm.star = getTime(line.substring(0, 12));
                    // 填充结束时间数据
                    sm.end = getTime(line.substring(17, 29));
                    // 填充中文数据
                    sm.contextC = in.readLine();
                    Log.i(TAG, "contextC: "+sm.contextC);
                    // 填充英文数据
                    sm.contextE = in.readLine();
                    // 当前字幕的节点位置
                    sm.node = list.size() + 1;
                    list.add(sm);
                    //Log.e("info", "contextC ="+sm.contextC);
                }
            }

            Message msg = new Message();
//            msg.what = MoviePlayActivity.SRT_FLAG;
            msg.what = 12;
            msg.obj = list;
            handler.sendMessage(msg);

        }
        catch (UnsupportedEncodingException Encoding)
        {
            Log.i(TAG, "Encoding "+Encoding.getMessage());
        }catch (IOException io)
        {
            Log.i(TAG, "io "+io.getMessage());
        }
    }
    /**
     * 读取本地文件
     *
     * @param is
     */
    public void readFileD(InputStream is)
    {
        String line;
        BufferedReader in = null;
        /**
         * 读取文件，转流，方便读取行
         */
            try
            {
                in = new BufferedReader(new InputStreamReader(is, "GBK"));//UTF-8
                assert in != null;
                while ((line = in.readLine()) != null)
                {
                    sm = new SubtitlesModel();
                    // 匹配正则表达式，不符合提前结束当前行；
                    if (Pattern.matches(equalStringExpress, line))
                    {
                        // 填充开始时间数据
                        sm.star = getTime(line.substring(0, 12));
                        // 填充结束时间数据
                        sm.end = getTime(line.substring(17, 29));
                        // 填充中文数据
                        sm.contextC = in.readLine();
                        Log.i(TAG, "contextC: "+sm.contextC);
                        // 填充英文数据
                        sm.contextE = in.readLine();
                        // 当前字幕的节点位置
                        sm.node = list.size() + 1;
                        list.add(sm);
                        Log.e("info", "contextC ="+sm.contextC);
                    }
                }
            }
            catch (UnsupportedEncodingException Encoding)
            {
                Log.i(TAG, "Encoding "+Encoding.getMessage());
            }catch (IOException io)
            {
                Log.i(TAG, "io "+io.getMessage());
            }
    }

    /**
     * 读取本地文件
     *
     * @param path
     */
    public void readFile(String path)
    {
        String line;
        FileInputStream is;
        File subtitlesFile = new File(path);
        BufferedReader in = null;

        if (!subtitlesFile.exists() || !subtitlesFile.isFile())
        {
            return;
        }
        /**
         * 读取文件，转流，方便读取行
         */
        try
        {
            is = new FileInputStream(subtitlesFile);
            try
            {
                in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        try
        {
            assert in != null;
            while ((line = in.readLine()) != null)
            {
                SubtitlesModel sm = new SubtitlesModel();
                // 匹配正则表达式，不符合提前结束当前行；
                if (Pattern.matches(equalStringExpress, line))
                {
                    // 填充开始时间数据
                    sm.star = getTime(line.substring(0, 12));
                    // 填充结束时间数据
                    sm.end = getTime(line.substring(17, 29));


                    // 填充中文数据
                    sm.contextC = in.readLine();
                    // 填充英文数据
                    sm.contextE = in.readLine();
                    // 当前字幕的节点位置
                    sm.node = list.size() + 1;
                    list.add(sm);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }



    /**
     * @param line
     * @return 字幕所在的时间节点
     * @descraption 将String类型的时间转换成int的时间类型
     */
    private static int getTime(String line)
    {
        try
        {
            return Integer.parseInt(line.substring(0, 2)) * oneHour// 时
                    + Integer.parseInt(line.substring(3, 5)) * oneMinute// 分
                    + Integer.parseInt(line.substring(6, 8)) * oneSecond// 秒
                    + Integer.parseInt(line.substring(9, line.length()));// 毫秒
        }
        catch (NumberFormatException e)
        {
            Log.e("getTime"," Integer.parseInt error");
            e.printStackTrace();
        }
        return -1;
    }
}
