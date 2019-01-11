package com.unbounded.video.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mopic3d.mplayer3d.tune.TuningActivity;
import com.stereo.Interaction.InteractionManager;
import com.stereo.angle.AngleActivity;
import com.stereo.util.ActionCallback;
import com.unbounded.video.BaseFragment;
import com.unbounded.video.activity.ExternalPlayerActivity;
import com.unbounded.video.activity.LoginActivity;
import com.unbounded.video.activity.MoviePlayActivity;
import com.unbounded.video.activity.VideoUpLoadActivity;
import com.unbounded.video.bean.LocalFilm;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.utils.LocalFilmRun;
import com.unbounded.video.utils.SharedPrefsUtil;
import com.unbounded.video.utils.ToastUtil;
import com.unbounded.video.utils.imageLoad.GlideLogic;
import com.unbounded.video.utils.upload.OkHttp3Utils;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.zhuge.analysis.stat.ZhugeSDK;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zjf on 2017/7/18 0018.
 */

public class VideoLocalFragment extends BaseFragment {

    RelativeLayout rela1;
    SwipeRefreshLayout swipeRefreshLayout;
    private ListView lv;
    VideoLocalAdapter adapter;
    List<LocalFilm> films = new ArrayList<LocalFilm>();
    int isFirst = 1;
    Dialog goSetcameraDialog, activeDialog;
    RxPermissions rxPermissions;

    MyReceiver myReceiver;

    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.SUCCESS_FLAG:
//                    films = (List<LocalFilm>) msg.obj;
                    films.addAll((List<LocalFilm>) msg.obj);
                    adapter.notifyDataSetChanged();

                    swipeRefreshLayout.setRefreshing(false);
                    progressDiddismiss();

                    if(films.size() == 0){
//                        ToastUtil.showToast(getActivity(), R.string.havenodata, ToastUtil.CENTER);
                        setEmptyViewVisible(true);

                    }else {
                        setEmptyViewVisible(false);
                    }

                    break;

                case Constants.PERMISSION_ERROR_FLAG:
                    swipeRefreshLayout.setRefreshing(false);
                    progressDiddismiss();
                    toSetDialog();
                    break;
            }
        }
    };

    @Override
    protected int getContentView() {
        return com.unbounded.video.R.layout.fragment_videorecommend;
    }
    /**
     * 首次加载
     */
    private void initFirst(){
        progressDid();
        if (films != null) {
            films.clear();
        }
        initFilms();
    }

    /**
     * 是否当前可见的生命周期方法
     */
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//
//        if(isVisibleToUser == true){
//            if(isFirst == 1){
//                initFirst();
//                isFirst = 0;
//            }
//        }
//    }

    @Override
    public void init() {
        super.init();

    }

    @Override
    public void onResume() {
        super.onResume();

        if(isFirst == 1){
            initFirst();
            isFirst = 0;
        }

        /*
        //实例化过滤器；
        IntentFilter intentFilter = new IntentFilter();
        //添加过滤的Action值；
        intentFilter.addAction("com.myBroadcast1");
        //实例化广播监听器；
        myReceiver = new MyReceiver();
        //将广播监听器和过滤器注册在一起；
        getActivity().registerReceiver(myReceiver, intentFilter);
        */

    }

    @Override
    public void initDatas() {
        super.initDatas();

        rxPermissions = new RxPermissions(getActivity());
    }


    /**
     * 本地视频
     */
    private void initFilms(){
        LocalFilmRun run = new LocalFilmRun(getActivity(), mHandler, Constants.SUCCESS_FLAG/*, films*/);
        Thread thread = new Thread(run);
        thread.start();
    }

    @Override
    public void initView(View view) {
        super.initView(view);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(com.unbounded.video.R.id.videorecomend_swiperefresh);
        lv = (ListView) view.findViewById(com.unbounded.video.R.id.videorecomend_lv);
        rela1 = (RelativeLayout) view.findViewById(com.unbounded.video.R.id.videosrecommend_rela1);

        rela1.setPadding(0,0,0,tabsmrelaHeight);

        adapter = new VideoLocalAdapter(VideoLocalFragment.this, getActivity());
        lv.setAdapter(adapter);

    }

    @Override
    public void initEvent() {
        super.initEvent();

        //
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                films.clear();
                adapter.notifyDataSetChanged();

                initFilms();
            }
        });

        //
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.e("info","position=" + position);

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //销毁Activity时取消注册广播监听器；
//        getActivity().unregisterReceiver(myReceiver);
    }

    /**
     * adapter适配器
     */
    class VideoLocalAdapter extends BaseAdapter {
        Fragment fragment;
        Context context;
        int imgWidth, imgHeight;

        public VideoLocalAdapter(Fragment fragment, Context context) {
            this.fragment = fragment;
            this.context = context;
        }

        @Override
        public int getCount() {
            return films.size();
        }

        @Override
        public Object getItem(int position) {
            return films.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(com.unbounded.video.R.layout.videolocal_item, null);
                viewHolder.imgrela = (RelativeLayout) convertView.findViewById(com.unbounded.video.R.id.videolocalitem_imgrela);
                viewHolder.img = (ImageView) convertView.findViewById(com.unbounded.video.R.id.videolocalitem_imgiv);
                viewHolder.playiv = (ImageView) convertView.findViewById(com.unbounded.video.R.id.videolocalitem_playiv);
                viewHolder.nametv = (TextView) convertView.findViewById(com.unbounded.video.R.id.videolocalitem_nametv);
                viewHolder.timeAndsizetv = (TextView) convertView.findViewById(com.unbounded.video.R.id.videolocalitem_timeAndsizetv);
                viewHolder.flagtv = (TextView) convertView.findViewById(com.unbounded.video.R.id.videolocalitem_flagtv);
                viewHolder.pb = (ProgressBar) convertView.findViewById(com.unbounded.video.R.id.videolocalitem_progress1);
                viewHolder.uploadbtn = (TextView) convertView.findViewById(com.unbounded.video.R.id.videolocalitem_uploadbtn);
                viewHolder.canclebtn = (TextView) convertView.findViewById(com.unbounded.video.R.id.videolocalitem_cancleuploadbtn);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (imgWidth == Constants.ZERO || imgHeight == Constants.ZERO) {
                imgWidth = screenWidth * 3 / 10;
                imgHeight = imgWidth * 3 / 4;
            }

            final LocalFilm film = films.get(position);

            ViewGroup.LayoutParams params = viewHolder.imgrela.getLayoutParams();
            params.width = imgWidth;
            params.height = imgHeight;
            viewHolder.imgrela.setLayoutParams(params);

            ViewGroup.LayoutParams params1 = viewHolder.img.getLayoutParams();
            params1.width = imgWidth;
            params1.height = imgHeight;
            viewHolder.img.setLayoutParams(params1);

            //没上传
            if(film.getFlag() == LocalFilm.UNUPLOAD){
                viewHolder.pb.setVisibility(View.GONE);
                viewHolder.canclebtn.setVisibility(View.GONE);
                viewHolder.flagtv.setVisibility(View.GONE);

                viewHolder.uploadbtn.setVisibility(View.VISIBLE);
                viewHolder.timeAndsizetv.setVisibility(View.VISIBLE);
            //正在上传
            }else if(film.getFlag() == LocalFilm.UPLOADING){
                viewHolder.pb.setVisibility(View.VISIBLE);
                viewHolder.canclebtn.setVisibility(View.VISIBLE);
                viewHolder.flagtv.setVisibility(View.VISIBLE);
                viewHolder.flagtv.setText(com.unbounded.video.R.string.uploading);

                viewHolder.uploadbtn.setVisibility(View.GONE);
                viewHolder.timeAndsizetv.setVisibility(View.VISIBLE);
            //上传完成
            }else if(film.getFlag() == LocalFilm.UPLOADED){
                viewHolder.pb.setVisibility(View.GONE);
                viewHolder.canclebtn.setVisibility(View.GONE);
                viewHolder.flagtv.setVisibility(View.VISIBLE);
                viewHolder.flagtv.setText(com.unbounded.video.R.string.uploaded);

                viewHolder.uploadbtn.setVisibility(View.GONE);
                viewHolder.timeAndsizetv.setVisibility(View.VISIBLE);
            //上传失败
            }else if(film.getFlag() == LocalFilm.UPLOADERROR){
                viewHolder.pb.setVisibility(View.GONE);
                viewHolder.canclebtn.setVisibility(View.GONE);
                viewHolder.flagtv.setVisibility(View.VISIBLE);
                viewHolder.flagtv.setText(com.unbounded.video.R.string.uploaderror);

                viewHolder.uploadbtn.setVisibility(View.VISIBLE);
                viewHolder.timeAndsizetv.setVisibility(View.VISIBLE);
            }


            viewHolder.nametv.setText(film.getFilmTitle());
            viewHolder.timeAndsizetv.setText(formatDuring(film.getDuration()) + "/" + getPrintSize(film.getSize()));

            File mVideoFile = new File(film.getPath());
            //指定大小压缩,节省资源
            //GlideLogic.glideLoadVideoImgWithFragment423(fragment, mVideoFile, viewHolder.img, imgWidth, imgHeight);
            GlideLogic.glideLoadPic423(context, film.getPath(), viewHolder.img, imgWidth, imgHeight);

            //上传
            viewHolder.uploadbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                userId = SharedPrefsUtil.getValue(getActivity(), Constants.UserId_FLAG, "");

                if(TextUtils.isEmpty(userId)){
                    Intent intent = new Intent();
                    intent.setClass(context, LoginActivity.class);
                    startActivity(intent);

                }else {
                    Intent intent = new Intent();
                    intent.setClass(context, VideoUpLoadActivity.class);
                    intent.putExtra("localVideo",film);
//                    intent.putExtra("localVideoPath",film.getPath());
//                    intent.putExtra("localVideoId",film.getId());
                    startActivity(intent);
                }
                }
            });
            //
            viewHolder.canclebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OkHttp3Utils.cancleUpFile(film.getId());
                }
            });
            //
            viewHolder.playiv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setCamera(film);
                }
            });


            return convertView;
        }

        class ViewHolder {
            RelativeLayout imgrela;
            ImageView img;
            ImageView playiv;
            TextView nametv;
            TextView timeAndsizetv;
            TextView flagtv;
            ProgressBar pb;
            TextView uploadbtn;
            TextView canclebtn;
        }
    }

    /**
     * 更新进度
     */
    public void publishProgress(int filmId, int filmFlag, int progress){
        int position=0;
        for(int i=0;i<films.size();i++){
            if(filmId == films.get(i).getId()){
                position = i;
            }
        }


        films.get(position).setFlag(filmFlag);
        films.get(position).setProgress(progress);

        //当该项在屏幕可见范围
        if(position >= lv.getFirstVisiblePosition() && position <= lv.getLastVisiblePosition()) {
            int positionInListView = position - lv.getFirstVisiblePosition();
            ProgressBar pb = (ProgressBar) lv.getChildAt(positionInListView).findViewById(com.unbounded.video.R.id.videolocalitem_progress1);//listview加了headerview，position就要加1
            TextView canclebtn = (TextView) lv.getChildAt(positionInListView).findViewById(com.unbounded.video.R.id.videolocalitem_cancleuploadbtn);
            TextView flagtv = (TextView) lv.getChildAt(positionInListView).findViewById(com.unbounded.video.R.id.videolocalitem_flagtv);
            TextView uploadbtn = (TextView) lv.getChildAt(positionInListView).findViewById(com.unbounded.video.R.id.videolocalitem_uploadbtn);
            TextView timeAndsizetv = (TextView) lv.getChildAt(positionInListView).findViewById(com.unbounded.video.R.id.videolocalitem_timeAndsizetv);

            //没上传
            if(filmFlag == LocalFilm.UNUPLOAD){
                pb.setVisibility(View.GONE);
                canclebtn.setVisibility(View.GONE);
                flagtv.setVisibility(View.GONE);

                uploadbtn.setVisibility(View.VISIBLE);
                timeAndsizetv.setVisibility(View.VISIBLE);
                timeAndsizetv.setText(formatDuring(films.get(position).getDuration()) + "/" + getPrintSize(films.get(position).getSize()));
                //正在上传
            }else if(filmFlag == LocalFilm.UPLOADING){
                pb.setVisibility(View.VISIBLE);
                canclebtn.setVisibility(View.VISIBLE);
                flagtv.setVisibility(View.VISIBLE);
                flagtv.setText(com.unbounded.video.R.string.uploading);

                uploadbtn.setVisibility(View.GONE);
                timeAndsizetv.setVisibility(View.VISIBLE);
//                timeAndsizetv.setText(progress);
                if(progress > 0){
                    pb.setProgress(progress);
                }
                //上传完成
            }else if(filmFlag == LocalFilm.UPLOADED){
                pb.setVisibility(View.GONE);
                canclebtn.setVisibility(View.GONE);
                flagtv.setVisibility(View.VISIBLE);
                flagtv.setText(com.unbounded.video.R.string.uploaded);

                uploadbtn.setVisibility(View.GONE);
                timeAndsizetv.setVisibility(View.VISIBLE);
                timeAndsizetv.setText(formatDuring(films.get(position).getDuration()) + "/" + getPrintSize(films.get(position).getSize()));
                //上传失败
            }else if(filmFlag == LocalFilm.UPLOADERROR){
                pb.setVisibility(View.GONE);
                canclebtn.setVisibility(View.GONE);
                flagtv.setVisibility(View.VISIBLE);
                flagtv.setText(com.unbounded.video.R.string.uploaderror);

                uploadbtn.setVisibility(View.VISIBLE);
                timeAndsizetv.setVisibility(View.VISIBLE);
                timeAndsizetv.setText(formatDuring(films.get(position).getDuration()) + "/" + getPrintSize(films.get(position).getSize()));
            }

        }
    }


    /**
     * 接收进度的广播
     */
    public class MyReceiver extends BroadcastReceiver {
        int localFilmId;
        int localFilmFlag;
        int progress;



        public MyReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            localFilmId = intent.getExtras().getInt("localFilmId");//获取BroadCastActivity中的name标签下的值
            localFilmFlag = intent.getExtras().getInt("localFilmFlag");//获取BroadCastActivity中的name标签下的值
            progress = intent.getExtras().getInt("localFilmProgress");//获取BroadCastActivity中的name标签下的值


            publishProgress(localFilmId, localFilmFlag, progress);

//            Log.e("info","接收 localFilmId=" + localFilmId);
//            Log.e("info","接收 progress=" + progress);
//            Log.e("info","接收 localFilmFlag=" + localFilmFlag);
        }
    }

    /**
     * 相机
     */
    public void setCamera(final LocalFilm film){
        rxPermissions.request(android.Manifest.permission.CAMERA)//这里填写所需要的权限
            .subscribe(new Action1<Boolean>() {
                @Override
                public void call(Boolean aBoolean) {
                    if (aBoolean) {//true表示获取权限成功（注意这里在android6.0以下默认为true）
                        boolean isJiaoZhun = SharedPrefsUtil.getValue(getActivity(), com.stereo.util.Constants.JIAOZHUNSUCCESS, false);
                        boolean isMopic = SharedPrefsUtil.getValue(getContext(), Constants.Mopic, false);
                        if (isMopic) {
                            Intent intent =new Intent(getActivity(),ExternalPlayerActivity.class);
                            intent.putExtra("playType", "local");
                            intent.putExtra("urlOrPath", film.getPath());
                            intent.putExtra("playName", film.getFilmTitle());
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                        } else {
                            if (isJiaoZhun == true) {
                                Intent intent = new Intent(getActivity(), MoviePlayActivity.class);
                                intent.putExtra("playType", "local");
                                intent.putExtra("urlOrPath", film.getPath());
                                intent.putExtra("playName", film.getFilmTitle());
                                startActivity(intent);
                            } else {
                                activeDia(film);
                            }
                        }
                    } else {
                        diaShow();
                    }
                }
            });
    }
    /**
     * 激活对话框
     */
    private void activeDia(final LocalFilm film){
        View diaView = View.inflate(getActivity(), com.unbounded.video.R.layout.notice_dialog, null);
        activeDialog = new Dialog(getActivity(), com.unbounded.video.R.style.dialog);
        activeDialog.setContentView(diaView);
        activeDialog.setCanceledOnTouchOutside(false);
        activeDialog.setCancelable(false);

        Button setafterbtn = (Button) activeDialog.findViewById(com.unbounded.video.R.id.notice_yesbtn);
        Button tonowbtn = (Button) activeDialog.findViewById(com.unbounded.video.R.id.notice_canclebtn);
        TextView titletv = (TextView) activeDialog.findViewById(com.unbounded.video.R.id.notice_tv1);
        TextView contenttv = (TextView) activeDialog.findViewById(com.unbounded.video.R.id.notice_tv2);

        titletv.setVisibility(View.GONE);

        contenttv.setText(com.unbounded.video.R.string.activebefore);
        setafterbtn.setText(com.unbounded.video.R.string.setafter);
        tonowbtn.setText(com.unbounded.video.R.string.nowactive);

        setafterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activeDialog.dismiss();

                Intent intent = new Intent(getActivity(), MoviePlayActivity.class);
                intent.putExtra("playType", "local");
                intent.putExtra("urlOrPath", film.getPath());
                intent.putExtra("playName", film.getFilmTitle());
                startActivity(intent);
            }
        });
        //
        tonowbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activeDialog.dismiss();
                Intent intent = new Intent();
                intent.setClass(getActivity(), AngleActivity.class);
                startActivity(intent);

                /**
                 * 弹窗激活
                 */
                /**
                 * 去校准
                 */
                String phoneNum = SharedPrefsUtil.getValue(getActivity(), Constants.Phone_FLAG, Constants.Phone_default);
                JSONObject personObject = null;
                try {
                    personObject = new JSONObject();
                    personObject.put("phoneNum", phoneNum);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ZhugeSDK.getInstance().track(getActivity(),"去校准",personObject);
            }
        });


        activeDialog.show();

        WindowManager.LayoutParams params = activeDialog.getWindow().getAttributes();
        params.width = screenWidth * 3 / 4;
        // params.height = 200 ;
        activeDialog.getWindow().setAttributes(params);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == -1) {
                    progressDid1();
                    //显示扫描到的内容
                    String sanString = data.getStringExtra("result").trim();
                    //调用激活接口激活
                    InteractionManager.getInstance(getActivity(), new ActionCallback() {
                        /**
                         * 激活请求接口
                         *  resultCode
                         *  1.激活成功
                         *  2.解密失败
                         *  3.网络超时
                         *  4.接受的参数为空
                         *  5.解密失败，解密后的序列号非全字符串
                         *  6.序列号拆分失败
                         *  7.根据解密后机型号没有获取到对应的机型
                         *  8.机型不匹配
                         *  9.根据拆分后的序列号没有查到对应的参数
                         *  10.调用加密算法失败
                         *  11.序列号不存在
                         *  12.json解析异常
                         *
                         */
                        @Override
                        public void onRequset(int returnCode) {
                            Log.i("PlayActivity", "onRequset: " + returnCode);

                            /**
                             * 激活结果
                             */
                            ZhugeSDK.getInstance().track(getActivity(),"激活结果");

                            switch (returnCode){
                                case 1:
                                    ToastUtil.showToast(getActivity(), "激活成功,请开始眼球追踪校准",ToastUtil.BOTTOM);

                                    Intent intent = new Intent();
                                    intent.setClass(getActivity(), AngleActivity.class);
                                    startActivity(intent);

                                    /**
                                     * 激活成功
                                     */
                                    ZhugeSDK.getInstance().track(getActivity(),"激活成功");

                                    break;
                                case 2:
//                                    ToastUtil.showToast(getActivity(), "解密失败", ToastUtil.CENTER);
                                    Log.v("InteractionManager 2","解密失败");
                                    break;
                                case 3:
//                                    ToastUtil.showToast(getActivity(), "网络超时", ToastUtil.CENTER);
                                    Log.v("InteractionManager 3","网络超时");
                                    break;
                                case 4:
                                    Log.v("InteractionManager 4","接受的参数为空");
//                                    ToastUtil.showToast(getActivity(), "接受的参数为空", ToastUtil.CENTER);
                                    break;
                                case 5:
                                    Log.v("InteractionManager 5","解密失败，解密后的序列号非全字符串");
//                                    ToastUtil.showToast(getActivity(), "解密失败，解密后的序列号非全字符串", ToastUtil.CENTER);
                                    break;
                                case 6:
                                    Log.v("InteractionManager 6","序列号拆分失败");
//                                    ToastUtil.showToast(getActivity(), "序列号拆分失败", ToastUtil.CENTER);
                                    break;
                                case 7:
                                    Log.v("InteractionManager 7","根据解密后机型号没有获取到对应的机型");
//                                    ToastUtil.showToast(getActivity(), "根据解密后机型号没有获取到对应的机型", ToastUtil.CENTER);
                                    break;
                                case 8:
                                    Log.v("InteractionManager 8","机型不匹配");
//                                    ToastUtil.showToast(getActivity(), "机型不匹配", ToastUtil.CENTER);
                                    break;
                                case 9:
                                    Log.v("InteractionManager 9","根据拆分后的序列号没有查到对应的参数");
//                                    ToastUtil.showToast(getActivity(), "根据拆分后的序列号没有查到对应的参数", ToastUtil.CENTER);
                                    break;
                                case 10:
                                    Log.v("InteractionManager 10","调用加密算法失败");
//                                    ToastUtil.showToast(getActivity(), "调用加密算法失败", ToastUtil.CENTER);
                                    break;
                                case 11:
                                    Log.v("InteractionManager 11","序列号不存在");
//                                    ToastUtil.showToast(getActivity(), "序列号不存在", ToastUtil.CENTER);
                                    break;
                                case 12:
                                    Log.v("InteractionManager 12","json解析异常");
//                                    ToastUtil.showToast(getActivity(), "json解析异常", ToastUtil.CENTER);
                                    break;
                                case 13:
                                    Log.v("InteractionManager 13","暂不支持此机型");
//                                    ToastUtil.showToast(getActivity(), "暂不支持此机型", ToastUtil.CENTER);
                                    break;
                            }

                            progressDiddismiss1();
                        }
                    }).interAction(sanString);
                }
                break;
        }
    }

    /**
     *
     */
    private void diaShow(){
        if(goSetcameraDialog != null){
            if(!(goSetcameraDialog.isShowing())){
                goSetcameraDialog.show();
            }
        }else {
            goSetCameraDia();
        }
    }
    /**
     * 用户拒绝后，继续弹出对话框
     */
    public void goSetCameraDia(){
        View diaView = View.inflate(getActivity(), com.unbounded.video.R.layout.notice_dialog, null);
        goSetcameraDialog = new Dialog(getActivity(), com.unbounded.video.R.style.dialog);
        goSetcameraDialog.setContentView(diaView);
        goSetcameraDialog.setCanceledOnTouchOutside(false);
        goSetcameraDialog.setCancelable(false);

        Button toSetbtn = (Button) goSetcameraDialog.findViewById(com.unbounded.video.R.id.notice_yesbtn);
        Button exitbtn = (Button) goSetcameraDialog.findViewById(com.unbounded.video.R.id.notice_canclebtn);
        TextView titletv = (TextView) goSetcameraDialog.findViewById(com.unbounded.video.R.id.notice_tv1);
        TextView contenttv = (TextView) goSetcameraDialog.findViewById(com.unbounded.video.R.id.notice_tv2);

        toSetbtn.setText(getString(com.unbounded.video.R.string.Word_Set));
        exitbtn.setText(getString(com.unbounded.video.R.string.cancle_btn));
        titletv.setText(getString(com.unbounded.video.R.string.Word_noticetitle));
        contenttv.setText(getString(com.unbounded.video.R.string.Word_noticecamera));


        toSetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSet();
                goSetcameraDialog.dismiss();
            }
        });
        //
        exitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSetcameraDialog.dismiss();
            }
        });

        goSetcameraDialog.show();

        WindowManager.LayoutParams params = goSetcameraDialog.getWindow().getAttributes();
        params.width = screenWidth * 3 / 4;
        // params.height = 200 ;
        goSetcameraDialog.getWindow().setAttributes(params);
    }

}
