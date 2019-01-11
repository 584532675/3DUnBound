package com.unbounded.video.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.unbounded.video.BaseActivity;
import com.unbounded.video.bean.Mymsg;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.constants.HttpConstants;
import com.unbounded.video.utils.HttpGetUtil;
import com.unbounded.video.utils.JsonUtil;
import com.unbounded.video.utils.SharedPrefsUtil;
import com.unbounded.video.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjf on 2017/7/27 0027.
 */

public class MessageCenterActivity extends BaseActivity {
    static final String Video_Flag = "video";
    static final String Image_Flag = "image";
    static final String Web_Flag = "web";
    static final String Evaluate_Flag = "evaluate";
    static final int VideoInfo_Flag = 0;
    static final int PicInfo_Flag = 1;

    SwipeRefreshLayout swipeRefreshLayout;
    ListView lv;
    View footer;
    MyAdapter adapter;
    List<Mymsg> myMsgs = new ArrayList<Mymsg>();
    String urlId;

    int currentPage = 1,sumPage;
    String selectStart;

    private int totalItem;
    private int lastItem;
    private boolean isLoading = false;

    String haveReadedjson;
    List<String> readedIds = new ArrayList<String>();

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.INTERNET_SUCCESS_FLAG:
                    String mymsgvalue = msg.obj.toString();
                    Log.e("info","mymsgvalue="+mymsgvalue);

                    if(!(TextUtils.isEmpty(mymsgvalue))){
                        try {
                            JSONObject mymsgjo = new JSONObject(mymsgvalue);
                            JSONObject page = mymsgjo.getJSONObject("page");
                            sumPage = Integer.valueOf(page.getString("sumPage"));
                            selectStart = page.getString("selectEnd");
                            JSONArray data = mymsgjo.getJSONArray("data");
                            if(data != null && data.length() > 0){
                                for(int i=0;i<data.length();i++){
                                    JSONObject jo = data.getJSONObject(i);

                                    String message = jo.getString("msg");
                                    String msgtitle = jo.getString("msgtitle");
                                    String extras = jo.getString("extras");
                                    String id = jo.getString("id");
                                    String title = jo.getString("title");
                                    String sendtime = jo.getString("sendtime");
                                    String type = jo.getString("type");

                                    Mymsg mymsg = new Mymsg(message, msgtitle, extras, id, title, sendtime, type);
                                    myMsgs.add(mymsg);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        isLoading = false;
                        swipeRefreshLayout.setRefreshing(false);

                        if(sumPage < 2){
                            if(lv.getFooterViewsCount() > 0){
                                lv.removeFooterView(footer);
                                footer.setVisibility(View.GONE);

                            }
                        }else {
                            if(lv.getFooterViewsCount() < 1){
                                lv.addFooterView(footer);
                                footer.setVisibility(View.VISIBLE);

                            }
                        }

                        adapter.notifyDataSetChanged();
                        emptyViewMethod();
                        progressDiddismiss();
                    }

                    break;
                case VideoInfo_Flag:
                    String videoinfovalue = msg.obj.toString();
                    if(!(TextUtils.isEmpty(videoinfovalue))) {
                        try {
                            JSONObject infojo = new JSONObject(videoinfovalue);
                            JSONArray data = infojo.getJSONArray("data");
                            if (data != null && data.length() > 0) {
                                Intent intent = new Intent();
                                intent.setClass(MessageCenterActivity.this, OnlineVideoInfoActivity.class);
                                intent.putExtra("videoId",urlId);
                                startActivity(intent);
                            }else{
                                ToastUtil.showToast(getApplicationContext(), "该资源已删除", ToastUtil.CENTER);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    break;
                case PicInfo_Flag:
                    String picinfovalue = msg.obj.toString();
                    if(!(TextUtils.isEmpty(picinfovalue))) {
                        try {
                            JSONObject infojo = new JSONObject(picinfovalue);
                            JSONArray data = infojo.getJSONArray("data");
                            if (data != null && data.length() > 0) {
                                Intent intent = new Intent();
                                intent.setClass(MessageCenterActivity.this, OnlinePicInfoActivity.class);
                                intent.putExtra("picId",urlId);
                                startActivity(intent);
                            }else{
                                ToastUtil.showToast(getApplicationContext(), "该资源已删除", ToastUtil.CENTER);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    break;

                case Constants.INTERNET_ERROR_FLAG:
                    ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.internet_error, ToastUtil.CENTER);
                    swipeRefreshLayout.setRefreshing(false);
                    adapter.notifyDataSetChanged();
                    emptyViewMethod();
                    progressDiddismiss();

                    break;
            }
        }
    };

    @Override
    protected int getContentView() {
        return com.unbounded.video.R.layout.activity_mymsg;
    }

    @Override
    public void init(Intent intent) {
        super.init(intent);

        setTitleName(getString(com.unbounded.video.R.string.Word_mymsg));
        readedIds.clear();
        userId = SharedPrefsUtil.getValue(getApplicationContext(), Constants.UserId_FLAG, "");
        haveReadedjson = SharedPrefsUtil.getValue(getApplicationContext(), Constants.HaveReaded_Flag, "");
        if(TextUtils.isEmpty(haveReadedjson)){

        }else{
            readedIds = JsonUtil.msgIdsJsonToList(haveReadedjson);
        }
    }

    @Override
    public void initDatas() {
        super.initDatas();

        progressDid();
        if(myMsgs != null){
            myMsgs.clear();
        }
        initMsgList();

    }

    /**
     * 数据为空
     */
    private void emptyViewMethod(){
        if(currentPage == 1 && myMsgs.size() == 0){
            ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.havenodata, ToastUtil.CENTER);
            setEmptyViewVisible(true);
        }else {
            setEmptyViewVisible(false);
        }
    }

    /**
     *
     */
    private void initMsgList(){
        String url;
        if(!(TextUtils.isEmpty(selectStart)) && !("null".equals(selectStart)) && currentPage != 1){
            url = HttpConstants.PushMsg_Url + "?currentPage=" + currentPage + "&userId=" + userId + "&selectStart="+selectStart;
        }else {
            url = HttpConstants.PushMsg_Url + "?currentPage=" + currentPage + "&userId=" + userId;
        }

        Log.e("info","url"+url);
        HttpGetUtil get = new HttpGetUtil(handler, url, Constants.INTERNET_SUCCESS_FLAG);
        Thread thread = new Thread(get);
        thread.start();
    }

    @Override
    public void initView() {
        super.initView();

        adapter = new MyAdapter();
        footer = LayoutInflater.from(getApplicationContext()).inflate(com.unbounded.video.R.layout.load_more_footer,null ,false);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(com.unbounded.video.R.id.mymsg_swiperefresh);
        lv = (ListView) findViewById(com.unbounded.video.R.id.mymsg_lv);

        lv.setAdapter(adapter);
    }

    @Override
    public void initEvent() {
        super.initEvent();

        lv.setOnScrollListener(new AbsListView.OnScrollListener(){
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(totalItem == lastItem&&scrollState == SCROLL_STATE_IDLE){
                    if(isLoading == false){
                        if(currentPage < sumPage){
                            isLoading = true;
                            footer.setVisibility(View.VISIBLE);
                            currentPage = currentPage + 1;

                            if(adapter != null){
                                adapter.notifyDataSetChanged();
                            }

                            initMsgList();
                        }else {
                            if(currentPage > 0){
                                isLoading = false;
                                footer.setVisibility(View.GONE);
                                ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.not_have_more, ToastUtil.BOTTOM);
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

        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isLoading = true;
                currentPage = 1;
                swipeRefreshLayout.setRefreshing(true);
                myMsgs.clear();
                adapter.notifyDataSetChanged();

                initMsgList();
            }
        });
        //
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Mymsg mymsg = myMsgs.get(position);

                if(!(readedIds.contains(mymsg.getId()))){
                    readedIds.add(mymsg.getId());
                    String s = JsonUtil.objectToJson(readedIds);
                    SharedPrefsUtil.putValue(getApplicationContext(), Constants.HaveReaded_Flag, s);
                }

                String[] strings = convertStrToArray(mymsg.getExtras());
                String urlType = strings[0].substring("urlType=".length(), strings[0].length());
                String type = strings[1].substring("type=".length()+1, strings[1].length());
                urlId = strings[2].substring("url=".length()+1, strings[2].length());


                if(Video_Flag.equals(urlType) && !(TextUtils.isEmpty(urlId))){
                    initVideoInfo(urlId);

                }else if(Image_Flag.equals(urlType)  && !(TextUtils.isEmpty(urlId))){
                    initPicInfo(urlId);

                }else if(Web_Flag.equals(urlType)  && !(TextUtils.isEmpty(urlId))){


                }

                int positionInListView = position - lv.getFirstVisiblePosition();
                ImageView noReadiv = (ImageView) lv.getChildAt(positionInListView).findViewById(com.unbounded.video.R.id.mymsg_noreadiv);
                noReadiv.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }
        });

    }

    /**
     * 图片信息
     */
    private void initPicInfo(String id){
        String url = HttpConstants.selectByIdImg_Url + "?id=" + id + "&userId=" + userId;
        HttpGetUtil get = new HttpGetUtil(handler, url, PicInfo_Flag);
        Thread thread = new Thread(get);
        thread.start();
    }

    /**
     * 视频信息
     */
    private void initVideoInfo(String id){
        String url = HttpConstants.selectInfoByID_VideosUrl + "?id=" + id + "&userId=" + userId;
        HttpGetUtil get = new HttpGetUtil(handler, url, VideoInfo_Flag);
        Thread thread = new Thread(get);
        thread.start();
    }
    /**
     *
     */
    class MyAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return myMsgs.size();
        }

        @Override
        public Object getItem(int i) {
            return myMsgs.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(MessageCenterActivity.this).inflate(com.unbounded.video.R.layout.mymsg_item, null);
                viewHolder.nametv = (TextView) convertView.findViewById(com.unbounded.video.R.id.mymsg_nametv);
                viewHolder.timetv = (TextView) convertView.findViewById(com.unbounded.video.R.id.mymsg_timetv);
                viewHolder.noReadiv = (ImageView) convertView.findViewById(com.unbounded.video.R.id.mymsg_noreadiv);
                viewHolder.msgcontenttv = (TextView) convertView.findViewById(com.unbounded.video.R.id.mymsg_contenttv);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }

            Mymsg mymsg = myMsgs.get(position);

            if(readedIds != null && readedIds.size() > 0){
                if(readedIds.contains(mymsg.getId())){
                    viewHolder.noReadiv.setVisibility(View.GONE);
                }else{
                    viewHolder.noReadiv.setVisibility(View.VISIBLE);
                }
            }

            viewHolder.msgcontenttv.setText(mymsg.getMsg());
            viewHolder.timetv.setText(getTime(mymsg.getSendtime()));

            if(Evaluate_Flag.equals(mymsg.getMsgtitle())){
                viewHolder.nametv.setText("评论消息");
            }else{
                viewHolder.nametv.setText(mymsg.getMsgtitle());
            }


            return convertView;
        }

        class ViewHolder{
            TextView timetv;
            TextView nametv;
            TextView msgcontenttv;
            ImageView noReadiv;
        }
    }

    /**
     * 使用String的split 方法
     */
    public String[] convertStrToArray(String str){
        String s = str.substring(1,str.length()-1);
//        Log.e("info","s=" + s);
        String[] strArray = null;
        strArray = s.split(",");
        return strArray;
    }

    /**
     * 转换时间格式
     */
    private String getTime(String time){
        String str = time.substring(0, 10);
        str = str.replace("年", "/");
        str = str.replace("月", "/");
        return str;
    }



}
