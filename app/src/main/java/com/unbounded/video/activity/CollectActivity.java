package com.unbounded.video.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stereo.Interaction.InteractionManager;
import com.stereo.angle.AngleActivity;
import com.stereo.util.ActionCallback;
import com.unbounded.video.BaseActivity;
import com.unbounded.video.bean.CollectBean;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.constants.HttpConstants;
import com.unbounded.video.utils.HttpGetUtil;
import com.unbounded.video.utils.SharedPrefsUtil;
import com.unbounded.video.utils.ToastUtil;
import com.unbounded.video.utils.imageLoad.GlideLogic;
import com.zhuge.analysis.stat.ZhugeSDK;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjf on 2017/7/20 0020.
 * 收藏界面
 */

public class CollectActivity extends BaseActivity {
    public final static int DeleteCollect_FLAG = 0;
    ListView lv;
    SwipeRefreshLayout swipeRefreshLayout;
    List<CollectBean> list = new ArrayList<CollectBean>();
    CollectAdapter adapter;

    Dialog deleteDialog, activeDialog;
    int currentPage = 1,sumPage,deleteposition = -1;
    private int totalItem;
    private int lastItem;
    String selectStart,phoneNum;
    private boolean isLoading = false;


    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.INTERNET_SUCCESS_FLAG:
                    String value = msg.obj.toString();
                    Log.e("info","collectvalue="+value);

                    if(!(TextUtils.isEmpty(value))){
                        try {
                            JSONObject mainjo = new JSONObject(value);

                            JSONObject page = mainjo.getJSONObject("page");
                            sumPage = Integer.valueOf(page.getString("sumPage"));
                            selectStart = page.getString("selectEnd");

                            JSONArray data = mainjo.getJSONArray("data");
                            if(data != null && data.length() > 0){
                                for(int i=0;i<data.length();i++){
                                    JSONObject jo = data.getJSONObject(i);

                                    String userImg = jo.getString("userImg");
                                    String collectorTime = jo.getString("collectorTime");
                                    String timeLong = jo.getString("timeLong");
                                    String uploadImg =  jo.getString("uploadImg");
                                    String name = jo.getString("name");
                                    String likeCount = jo.getString("likeCount");
                                    String id = jo.getString("id");
                                    String openCount = jo.getString("openCount");
                                    String userName = jo.getString("userName");
                                    String type = jo.getString("type");
                                    String uuid = jo.getString("uuid");
                                    String introduction = jo.getString("introduction");

                                    CollectBean collectBean = new CollectBean(userImg,collectorTime,timeLong,uploadImg,name,likeCount,id,openCount,userName,type,uuid,introduction);
                                    list.add(collectBean);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        isLoading = false;
                        swipeRefreshLayout.setRefreshing(false);
                        adapter.notifyDataSetChanged();

                        emptyViewMethod();

                        progressDiddismiss();


                    }

                    break;
                //
                case DeleteCollect_FLAG:
                    String deletevalue = msg.obj.toString();
                    Log.e("info","deletevalue="+deletevalue);
                    if(!(TextUtils.isEmpty(deletevalue))) {
                        try {
                            JSONObject collectjo = new JSONObject(deletevalue);
                            String result = collectjo.getString("result");
                            if (Constants.SUCCESS_STR.equals(result)) {
                                list.remove(deleteposition);
                                ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_worksuccess, ToastUtil.CENTER);

                                if(list.size() == 0){
                                    setEmptyViewVisible(true);
                                }

                            } else {
                                ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_workerror, ToastUtil.CENTER);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        adapter.notifyDataSetChanged();
                        progressDiddismiss();
                    }

                    break;

                case Constants.INTERNET_ERROR_FLAG:
                    swipeRefreshLayout.setRefreshing(false);
                    ToastUtil.showToast(CollectActivity.this, com.unbounded.video.R.string.internet_error, ToastUtil.CENTER);
                    adapter.notifyDataSetChanged();
                    emptyViewMethod();
                    progressDiddismiss();

                    break;
            }
        }
    };

    @Override
    protected int getContentView() {
        return com.unbounded.video.R.layout.activity_collect;
    }

    @Override
    public void init(Intent intent) {
        super.init(intent);

        setTitleName(getString(com.unbounded.video.R.string.Word_Collect));

        userId = SharedPrefsUtil.getValue(getApplicationContext(), Constants.UserId_FLAG, "");
    }

    @Override
    public void initDatas() {
        super.initDatas();

        progressDid();
        if(list != null){
            list.clear();
        }
        initCollects();
    }

    /**
     * 数据为空
     */
    private void emptyViewMethod(){
        if(currentPage == 1 && list.size() == 0){
            ToastUtil.showToast(CollectActivity.this, com.unbounded.video.R.string.havenodata, ToastUtil.CENTER);
            setEmptyViewVisible(true);

        }else {
            setEmptyViewVisible(false);
        }
    }

    /**
     * 初始化收藏列表
     */
    private void initCollects(){
        String url;
        if(!(TextUtils.isEmpty(selectStart)) && !("null".equals(selectStart)) && currentPage != 1){
            url = HttpConstants.Collectlist_Url + "?userId="+userId + "&currentPage=" + currentPage + "&selectStart="+selectStart;
        }else {
            url = HttpConstants.Collectlist_Url + "?userId="+userId + "&currentPage=" + currentPage;
        }

        HttpGetUtil get = new HttpGetUtil(handler, url, Constants.INTERNET_SUCCESS_FLAG);
        Thread thread = new Thread(get);
        thread.start();
    }


    @Override
    public void initView() {
        super.initView();

        adapter = new CollectAdapter(CollectActivity.this);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(com.unbounded.video.R.id.collect_swiperefresh);
        lv = (ListView) findViewById(com.unbounded.video.R.id.collect_lv);

        lv.setAdapter(adapter);
    }

    @Override
    public void initEvent() {
        super.initEvent();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isLoading = true;
                currentPage = 1;
                swipeRefreshLayout.setRefreshing(true);
                list.clear();
                adapter.notifyDataSetChanged();

                initCollects();
            }
        });
        //
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                deleteposition = position;
                deleteDia(list.get(position).getUuid());

                return false;
            }
        });

        //
        lv.setOnScrollListener(new AbsListView.OnScrollListener(){
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(totalItem == lastItem&&scrollState == SCROLL_STATE_IDLE){
                    if(isLoading == false){
                        if(currentPage < sumPage){
                            isLoading = true;
                            currentPage = currentPage + 1;

                            if(adapter != null){
                                adapter.notifyDataSetChanged();
                            }

                            initCollects();
                        }else {
                            if(currentPage > 0){
                                isLoading = false;
                                ToastUtil.showToast(CollectActivity.this, com.unbounded.video.R.string.not_have_more, ToastUtil.BOTTOM);
                            }
                        }
                    }
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                lastItem = firstVisibleItem+visibleItemCount;
                totalItem = totalItemCount;
            }
        });
    }

    /**
     *删除收藏条目
     */
    public void deleteDia(final String id){
        View diaView = View.inflate(CollectActivity.this, com.unbounded.video.R.layout.notice_dialog, null);
        deleteDialog = new Dialog(CollectActivity.this, com.unbounded.video.R.style.dialog);
        deleteDialog.setContentView(diaView);
        deleteDialog.setCanceledOnTouchOutside(false);
        deleteDialog.setCancelable(false);

        Button toSetbtn = (Button) deleteDialog.findViewById(com.unbounded.video.R.id.notice_yesbtn);//确定
        Button exitbtn = (Button) deleteDialog.findViewById(com.unbounded.video.R.id.notice_canclebtn);//取消
        TextView titletv = (TextView) deleteDialog.findViewById(com.unbounded.video.R.id.notice_tv1);
        TextView contenttv = (TextView) deleteDialog.findViewById(com.unbounded.video.R.id.notice_tv2);

        toSetbtn.setText(getString(com.unbounded.video.R.string.sure_btn));
        exitbtn.setText(getString(com.unbounded.video.R.string.cancle_btn));
        titletv.setText(getString(com.unbounded.video.R.string.Word_noticetitle));
        contenttv.setText(getString(com.unbounded.video.R.string.Word_deletetitle));


        toSetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCollect(id);
                deleteDialog.dismiss();
                progressDid();
            }
        });
        //
        exitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
            }
        });

        deleteDialog.show();

        WindowManager.LayoutParams params = deleteDialog.getWindow().getAttributes();
        params.width = screenWidth * 3 / 4;
        // params.height = 200 ;
        deleteDialog.getWindow().setAttributes(params);
    }

    /**
     * 取消收藏
     */
    private void deleteCollect(String id){
        String url = HttpConstants.Deletecollect_Url + "?id=" + id;
        HttpGetUtil get = new HttpGetUtil(handler, url, DeleteCollect_FLAG);
        Thread thread = new Thread(get);
        thread.start();
    }

    /**
     * 适配器 adapter
     */
    class CollectAdapter extends BaseAdapter{
        Context context;
        private int imgWidth, imgHeight, headviewWidth;

        public CollectAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(com.unbounded.video.R.layout.collect_item, null);

                viewHolder.cb = (CheckBox) convertView.findViewById(com.unbounded.video.R.id.historyitem_cb);
                viewHolder.headimgiv = (ImageView) convertView.findViewById(com.unbounded.video.R.id.historyitem_headviewiv);
                viewHolder.authornametv = (TextView) convertView.findViewById(com.unbounded.video.R.id.historyitem_authornametv);
                viewHolder.collecttimetv = (TextView) convertView.findViewById(com.unbounded.video.R.id.historyitem_playtimetv);
                viewHolder.imgrela = (RelativeLayout) convertView.findViewById(com.unbounded.video.R.id.historyitem_imgrela);
                viewHolder.videoimgiv = (ImageView) convertView.findViewById(com.unbounded.video.R.id.historyitem_imgiv);
                viewHolder.playiv = (ImageView) convertView.findViewById(com.unbounded.video.R.id.historyitem_playiv);
                viewHolder.filmnametv = (TextView) convertView.findViewById(com.unbounded.video.R.id.historyitem_filmnametv);
                viewHolder.playpositiontv = (TextView) convertView.findViewById(com.unbounded.video.R.id.historyitem_playpositiontv);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (imgWidth == Constants.ZERO) {
                imgWidth = screenWidth * 3 / 10;
                imgHeight = imgWidth * 3 / 4;
            }

            if (headviewWidth == Constants.ZERO) {
                headviewWidth = 44 * oneDp;
            }

            ViewGroup.LayoutParams params = viewHolder.videoimgiv.getLayoutParams();
            params.width = imgWidth;
            params.height = imgHeight;
            viewHolder.videoimgiv.setLayoutParams(params);

//            ViewGroup.LayoutParams imgrelaparams = (ViewGroup.LayoutParams) viewHolder.imgrela.getLayoutParams();
//            imgrelaparams.width = imgWidth;
//            imgrelaparams.height = imgHeight;
//            viewHolder.imgrela.setLayoutParams(imgrelaparams);

            final CollectBean collect = list.get(position);
            //判断是图片还是视频
            if("image".equals(collect.getType())){
                viewHolder.playiv.setVisibility(View.GONE);

            }else {
                viewHolder.playiv.setVisibility(View.VISIBLE);

            }
            //
//            viewHolder.playiv.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if("video".equals(collect.getType())){
//                        Intent intent = new Intent();
//                        intent.setClass(CollectActivity.this, OnlineVideoInfoActivity.class);
//                        intent.putExtra("videoId", collect.getId());
//                        startActivity(intent);
//
//                    }
//                }
//            });

            viewHolder.videoimgiv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if("image".equals(collect.getType())) {
                        boolean isJiaoZhun = SharedPrefsUtil.getValue(CollectActivity.this, com.stereo.util.Constants.JIAOZHUNSUCCESS, false);

                        if(isJiaoZhun == true){
                            Intent intent1 = new Intent();
                            intent1.setClass(CollectActivity.this, OnlinePicInfoActivity.class);
                            intent1.putExtra("picId", collect.getId());
                            startActivity(intent1);
                        }else{
                            activeDia(collect.getId());
                        }
                    }else if("video".equals(collect.getType())){
                        Intent intent = new Intent();
                        intent.setClass(CollectActivity.this, OnlineVideoInfoActivity.class);
                        intent.putExtra("videoId", collect.getId());
                        startActivity(intent);

                    }
                }
            });


            viewHolder.authornametv.setText(collect.getUserName());
            viewHolder.collecttimetv.setText(collect.getCollectorTime());
            viewHolder.filmnametv.setText(collect.getName());

            //指定大小压缩,节省资源
            GlideLogic.glideLoadHeadPic(context, collect.getUserImg(), viewHolder.headimgiv, headviewWidth * 11 / 16, headviewWidth * 11 / 16);
            GlideLogic.glideLoadPic423(context, collect.getUploadImg(), viewHolder.videoimgiv, imgWidth * 11 / 16, imgHeight * 11 / 16);


            return convertView;
        }

        class ViewHolder {
            CheckBox cb;
            ImageView headimgiv;
            TextView authornametv;
            TextView collecttimetv;
            RelativeLayout imgrela;
            ImageView videoimgiv;
            ImageView playiv;
            TextView filmnametv;
            TextView playpositiontv;
        }
    }

    /**
     * 激活对话框
     */
    private void activeDia(final String id){
        View diaView = View.inflate(CollectActivity.this, com.unbounded.video.R.layout.notice_dialog, null);
        activeDialog = new Dialog(CollectActivity.this, com.unbounded.video.R.style.dialog);
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

                Intent intent1 = new Intent();
                intent1.setClass(CollectActivity.this, OnlinePicInfoActivity.class);
                intent1.putExtra("picId", id);
                startActivity(intent1);
            }
        });
        //
        tonowbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activeDialog.dismiss();

                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), AngleActivity.class);
                startActivity(intent);

                /**
                 * 去校准
                 */
                phoneNum = SharedPrefsUtil.getValue(getApplicationContext(), Constants.Phone_FLAG, Constants.Phone_default);
                JSONObject personObject = null;
                try {
                    personObject = new JSONObject();
                    personObject.put("phoneNum", phoneNum);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ZhugeSDK.getInstance().track(CollectActivity.this,"去校准", personObject);
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
                    progressDid();
                    //显示扫描到的内容
                    String sanString = data.getStringExtra("result").trim();
                    //调用激活接口激活
                    InteractionManager.getInstance(CollectActivity.this, new ActionCallback() {
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
                             * 激活结果总次数
                             */
                            phoneNum = SharedPrefsUtil.getValue(getApplicationContext(), Constants.Phone_FLAG, Constants.Phone_default);
                            JSONObject personObject = null;
                            try {
                                personObject = new JSONObject();
                                personObject.put("phoneNum", phoneNum);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            ZhugeSDK.getInstance().track(getApplicationContext(),"激活结果总次数", personObject);

                            switch (returnCode){
                                case 1:
//                                    ToastUtil.showToast(CollectActivity.this, "激活成功,请开始眼球追踪校准",ToastUtil.BOTTOM);

                                    Intent intent = new Intent();
                                    intent.putExtra("activeSuccess",true);
                                    intent.setClass(CollectActivity.this, AngleActivity.class);
                                    startActivity(intent);

                                    /**
                                     * 激活成功
                                     */
                                    ZhugeSDK.getInstance().track(getApplicationContext(),"激活成功",personObject);
                                    ZhugeSDK.getInstance().track(getApplicationContext(),"进入校准界面总次数",personObject);

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

                            progressDiddismiss();
                        }
                    }).interAction(sanString);
                }
                break;
        }
    }
}
