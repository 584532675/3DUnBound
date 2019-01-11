package com.unbounded.video.utils.upload;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.unbounded.video.activity.FeedBackActivity;
import com.unbounded.video.activity.PersonInfoActivity;
import com.unbounded.video.activity.PicUploadActivity;
import com.unbounded.video.activity.ReportMovieActivity;
import com.unbounded.video.activity.VideoUpLoadActivity;
import com.unbounded.video.bean.LocalFilm;
import com.unbounded.video.constants.HttpConstants;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * Created by Administrator on 2017/7/13 0013.
 */

public class OkHttp3Utils {
    //        private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");//图片
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("application/octet-stream");//文件

    public static OkHttp3Utils instance = null;
    static OkHttpClient client;

    long  mLastFrameSavedTime = 0;
    long  mCurrentFrameSavedTime = 0;
    long  minusTime = 500;

    static Map<Integer,Call> map = new HashMap<Integer,Call>();
    static Map<String,Call> recodemap = new HashMap<String,Call>();

    private OkHttp3Utils() {
        // 避免类在外部被实例化
        client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Response response = chain.proceed(request);
                        return response;
                    }
                })
                .connectTimeout(15000, TimeUnit.MILLISECONDS)
                .readTimeout(15000,TimeUnit.MILLISECONDS)
                .writeTimeout(15000, TimeUnit.MILLISECONDS)
                .build();

    }


    public static OkHttp3Utils getInstance() {
        if (null == instance) {
            instance = new OkHttp3Utils();
        }
        return instance;
    }


    /**
     * 取消上传录制视频
     */
    public static void cancleRecodeUpFile(String userid){
        Call call = recodemap.get(userid);
        call.cancel();
    }
    /**
     * 录制视频上传
     */
    public static void upFileRecode(final Handler handler, final String userid, String recodePath, String picPath, String videoname, String introduction){
        File fileimg = new File(picPath);
        RequestBody fileimgBody = RequestBody.create(MEDIA_TYPE_PNG , fileimg);
        String fileimgName = getFileName(picPath);

        File filerecode = new File(recodePath);
        RequestBody filerecodeBody = RequestBody.create(MEDIA_TYPE_PNG , filerecode);
        String filerecodeName = getFileName(recodePath);

         /* form的分割线,自己定义 */
        String boundary = "xx--------------------------------------------------------------xx";

        MultipartBody mBody = new MultipartBody.Builder(boundary).setType(MultipartBody.FORM)
                /* 上传一个普通的String参数 , key 叫 "p" */
                .addFormDataPart("userId" , userid)
                .addFormDataPart("name" , videoname)
                .addFormDataPart("uploadType" , "视频")
                .addFormDataPart("introduction" , introduction)
                .addFormDataPart("type" , "original")
                /* 底下是上传了两个文件 */
                .addFormDataPart("imgFile" , fileimgName , fileimgBody)
                .addFormDataPart("videoFile" , filerecodeName , filerecodeBody)
                .build();

        /* 下边的就和post一样了 */
        Request request = new Request.Builder().url(HttpConstants.insertVideo_Url).post(mBody).build();
        Call call = client.newCall(request);
        recodemap.put(userid, call);

        call.enqueue(new Callback() {
            public void onResponse(Call call, Response response) throws IOException {
                final  String bodyStr = response.body().string();
                final boolean ok = response.isSuccessful();
//                Log.e("info", "更新个人信息bodyStr= "+bodyStr);
                handler.sendEmptyMessage(VideoUpLoadActivity.Commit_RecodeUpload);

            }
            public void onFailure(Call call, final IOException e) {
//                Log.e("info","onFailure");
                handler.sendEmptyMessage(VideoUpLoadActivity.Commit_RecodeUploaderror);
            }
        });

    }

    /**
     * 取消上传视频
     */
    public static void cancleUpFile(int filmId){
        Call call = map.get(filmId);
        call.cancel();
    }
    /**
     * 上传视频方法
     */
    public static void upFile(VideoUpLoadActivity.MyProgressListener myProgressListener, final Context context, String userId, final int localfilmId, String videoPath, String picPath, String videoname, String introduction){
        Log.e("info","upFile ....");
        /* 第一个要上传的file,视频的 */
        File filevideo = new File(videoPath);
        RequestBody fileBody1 = RequestBody.create(MEDIA_TYPE_PNG , filevideo);
//        String file1Name = "new.mp4";
        String file1Name = getFileName(videoPath);

        /* 第二个要上传的文件,视频图片 */
        File fileimg = new File(picPath);
        RequestBody fileBody2 = RequestBody.create(MEDIA_TYPE_PNG , fileimg);
//        String file2Name = "new.png";
        String file2Name = getFileName(picPath);


        /* form的分割线,自己定义 */
        String boundary = "xx--------------------------------------------------------------xx";

        MultipartBody mBody = new MultipartBody.Builder(boundary).setType(MultipartBody.FORM)
                /* 上传一个普通的String参数 , key 叫 "p" */
                .addFormDataPart("name" , videoname)
                .addFormDataPart("userId" , userId)
                .addFormDataPart("uploadType" , "视频")
                .addFormDataPart("introduction" , introduction)
                .addFormDataPart("type" , "original")
                /* 底下是上传了两个文件 */
                .addFormDataPart("imgFile" , file2Name , fileBody2)
                .addFormDataPart("videoFile" , file1Name , createCustomRequestBody(MEDIA_TYPE_PNG, filevideo, myProgressListener))
                .build();

        /* 下边的就和post一样了 */
        Request request = new Request.Builder().url(HttpConstants.insertVideo_Url).post(mBody).build();
        Call call = client.newCall(request);
        map.put(localfilmId, call);

        call.enqueue(new Callback() {
            public void onResponse(Call call, Response response) throws IOException {
                final  String bodyStr = response.body().string();
                final boolean ok = response.isSuccessful();
                Log.e("info", "util"+localfilmId + "bodyStr= "+bodyStr);

                Intent intent = new Intent();
                intent.setAction("com.myBroadcast1");//设置意图
                intent.putExtra("localFilmId", localfilmId);//设置所需发送的消息标签以及内容    视频id
                intent.putExtra("localFilmFlag", LocalFilm.UPLOADED);//设置所需发送的消息标签以及内容    本视频对应进度
                context.sendBroadcast(intent);//发送普通广播

            }
            public void onFailure(Call call, final IOException e) {
                Log.e("info","util"+localfilmId+ "onFailure");

                Intent intent = new Intent();
                intent.setAction("com.myBroadcast1");//设置意图
                intent.putExtra("localFilmId", localfilmId);//设置所需发送的消息标签以及内容    视频id
                intent.putExtra("localFilmFlag", LocalFilm.UPLOADERROR);//设置所需发送的消息标签以及内容    本视频对应进度
                context.sendBroadcast(intent);//发送普通广播
            }
        });
    }

    /**
     * 上传图片
     */
    public static void upFilePic(final Handler handler, String userId, String picname, String introduction, String picPath){
        File filepic = new File(picPath);
        RequestBody fileBody = RequestBody.create(MEDIA_TYPE_PNG , filepic);
        String fileName = getFileName(picPath);

         /* form的分割线,自己定义 */
        String boundary = "xx--------------------------------------------------------------xx";

        MultipartBody mBody = new MultipartBody.Builder(boundary).setType(MultipartBody.FORM)
                /* 上传一个普通的String参数 , key 叫 "p" */
                .addFormDataPart("name" , picname)
                .addFormDataPart("userId" , userId)
                .addFormDataPart("uploadType" , "视频")
                .addFormDataPart("introduction" , introduction)
                .addFormDataPart("type" , "original")
                /* 底下是上传了文件 */
                .addFormDataPart("imgFile" , fileName , fileBody)
                .build();

        /* 下边的就和post一样了 */
        Request request = new Request.Builder().url(HttpConstants.insertImg_Url).post(mBody).build();
        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            public void onResponse(Call call, Response response) throws IOException {
                final  String bodyStr = response.body().string();
                final boolean ok = response.isSuccessful();
//                Log.e("info", "util"+localfilmId + "bodyStr= "+bodyStr);
                handler.sendEmptyMessage(PicUploadActivity.Commit_Upload);

            }
            public void onFailure(Call call, final IOException e) {
//                Log.e("info","util"+localfilmId+ "onFailure");
                handler.sendEmptyMessage(PicUploadActivity.Commit_Uploaderror);
            }
        });
    }
    /**
     * 上传举报图片
     */
    public static void upReportFilePic(final Handler handler, String videoId ,String userId, String type, String reportType,
                                       String content,String phone ,List<ImageView> picPath){


                /* form的分割线,自己定义 */
        String boundary = "xx--------------------------------------------------------------xx";
        MultipartBody.Builder mBody = new MultipartBody.Builder(boundary).setType(MultipartBody.FORM)
                /* 上传一个普通的String参数 , key 叫 "p" */
                .addFormDataPart("videoId" , videoId)
                .addFormDataPart("userId" , userId)
                .addFormDataPart("type" , type)
                .addFormDataPart("reportType" , reportType)
                .addFormDataPart("content" , content)
                .addFormDataPart("phone" , phone);

        for (int i =0 ;i<picPath.size();i++){
            File filepic = new File((String) picPath.get(i).getTag());
            RequestBody fileBody = RequestBody.create(MEDIA_TYPE_PNG , filepic);
            String fileName = getFileName((String) picPath.get(i).getTag());
            /* 添加上传了文件 */
            mBody.addFormDataPart("imgFiles",fileName,fileBody);
        }
//        MultipartBody mBody = new MultipartBody.Builder(boundary).setType(MultipartBody.FORM)
//                /* 上传一个普通的String参数 , key 叫 "p" */
//                .addFormDataPart("videoId" , videoId)
//                .addFormDataPart("userId" , userId)
//                .addFormDataPart("type" , type)
//                .addFormDataPart("reportType" , reportType)
//                .addFormDataPart("content" , content)
//                .addFormDataPart("phone" , phone)
//                /* 底下是上传了文件 */
//                .addFormDataPart("imgFiles" , fileName , fileBody)
//                .build();
        /* 下边的就和post一样了 */
        Request request = new Request.Builder().url(HttpConstants.report_Img_Url).post(mBody.build()).build();
        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            public void onResponse(Call call, Response response) throws IOException {
                final  String bodyStr = response.body().string();
                final boolean ok = response.isSuccessful();
//                Log.e("info", "util"+localfilmId + "bodyStr= "+bodyStr);
                handler.sendEmptyMessage(ReportMovieActivity.Commit_Upload);

            }
            public void onFailure(Call call, final IOException e) {
//                Log.e("info","util"+localfilmId+ "onFailure");
                handler.sendEmptyMessage(ReportMovieActivity.Commit_Uploaderror);
            }
        });
    }

    /**
     * 更新个人信息(头像)
     */
    public static void upUserInfo(final Handler handler, String userimgPath, String userid){
        File fileheadimg = new File(userimgPath);
        RequestBody fileheadBody = RequestBody.create(MEDIA_TYPE_PNG , fileheadimg);
        String fileName = getFileName(userimgPath);

         /* form的分割线,自己定义 */
        String boundary = "xx--------------------------------------------------------------xx";

        MultipartBody mBody = new MultipartBody.Builder(boundary).setType(MultipartBody.FORM)
                /* 上传一个普通的String参数 , key 叫 "p" */
                .addFormDataPart("userid" , userid)
                /* 底下是上传了两个文件 */
                .addFormDataPart("imgFile" , fileName , fileheadBody)
                .build();

        /* 下边的就和post一样了 */
        Request request = new Request.Builder().url(HttpConstants.UpdateUserImg_Url).post(mBody).build();
        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            public void onResponse(Call call, Response response) throws IOException {
                final  String bodyStr = response.body().string();
                final boolean ok = response.isSuccessful();
                Log.e("info", "更新个人信息bodyStr= "+bodyStr);
                if("true".equals(bodyStr)){
                    handler.sendEmptyMessage(PersonInfoActivity.Update_UserImg);
                }else if("false".equals(bodyStr)){
                    handler.sendEmptyMessage(PersonInfoActivity.Update_UserImgerror);
                }
            }
            public void onFailure(Call call, final IOException e) {
//                Log.e("info","onFailure");
                handler.sendEmptyMessage(PersonInfoActivity.Update_UserImgerror);
            }
        });
    }

    /**
     *
     */
    private static String getFileName(String str){
        String s = str.substring(str.lastIndexOf("/") + 1);
        return s;
    }

    /**
     * 传多张图片(意见反馈)
     * @param params
     */
    public static void uploadPFile(final Handler handler, List<String> list,Map<String, String> params) {
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
        multipartBodyBuilder.setType(MultipartBody.FORM);
        //遍历map中所有参数到builder
        if (params != null){
            for (String key : params.keySet()) {
                multipartBodyBuilder.addFormDataPart(key, params.get(key));
                Log.e("info","key=" + key +",value="+ params.get(key));
            }
        }
        //遍历paths中所有图片绝对路径到builder，并约定key如“imgFiles”作为后台接受多张图片的key
        if (list != null){
            Log.e("info","list.get(0)="+list.get(0));
            for(int i=0;i<list.size() - 1;i++){
                File f = new File(list.get(i));
                multipartBodyBuilder.addFormDataPart("imgFiles", getFileName(list.get(i)), RequestBody.create(MEDIA_TYPE_PNG, f));
            }
        }

        //构建请求体
        RequestBody mBody = multipartBodyBuilder.build();
        Request request = new Request.Builder().url(HttpConstants.FeedBack_Url).post(mBody).build();
        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            public void onResponse(Call call, Response response) throws IOException {
                final  String bodyStr = response.body().string();
                final boolean ok = response.isSuccessful();
//                Log.e("info", "反馈bodyStr =" + bodyStr);
                if("true".equals(bodyStr)){
                    handler.sendEmptyMessage(FeedBackActivity.HaveImgsuccess_FLAG);
                }else {
                    handler.sendEmptyMessage(FeedBackActivity.HaveImgfail_FLAG);
                }
            }
            public void onFailure(Call call, final IOException e) {
//                Log.e("info", "反馈onFailure =" + e.getMessage());
                handler.sendEmptyMessage(FeedBackActivity.HaveImgfail_FLAG);
            }
        });
    }

    /**
     * 根据前后两次时间显示下载速度，下载速度变化太快，只是显示没必要刷那么快
     */
    public boolean startReco(){
        boolean save = false;
        //第一次,成功
        if(mLastFrameSavedTime == 0){
            save = true;
            mCurrentFrameSavedTime = System.currentTimeMillis();
            mLastFrameSavedTime = mCurrentFrameSavedTime;

            //第二次以后,判断时间
        }else {
            mCurrentFrameSavedTime = System.currentTimeMillis();
            if(mCurrentFrameSavedTime - mLastFrameSavedTime > minusTime){

                save = true;
                mLastFrameSavedTime = mCurrentFrameSavedTime;
            }else {

                save = false;
            }
        }

        return save;
    }



    public static RequestBody createCustomRequestBody(final MediaType contentType, final File file, final VideoUpLoadActivity.ProgressListener listener) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return contentType;
            }

            @Override
            public long contentLength() {
                return file.length();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Source source;
                try {
                    source = Okio.source(file);
                    //sink.writeAll(source);
                    Buffer buf = new Buffer();
                    Long remaining = contentLength();
                    for (long readCount; (readCount = source.read(buf, 2048)) != -1; ) {
                        sink.write(buf, readCount);
                        listener.onProgress(contentLength(), remaining -= readCount, remaining == 0);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    /*
    interface ProgressListener {
        void onProgress(long totalBytes, long remainingBytes, boolean done);
    }
    */



}
