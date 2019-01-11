package com.unbounded.video.fragment;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.unbounded.video.BaseFragment;
import com.unbounded.video.activity.OnlineVideoInfoActivity;
import com.unbounded.video.adapter.VideoOnlineAdapter;
import com.unbounded.video.bean.OnlineVideo;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.constants.HttpConstants;
import com.unbounded.video.utils.HttpGetUtil;
import com.unbounded.video.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjf on 2017/7/18 0018.
 * 原创视频
 */

public class VideoOriginalFragment extends BaseFragment {
    RelativeLayout rela1;
    SwipeRefreshLayout swipeRefreshLayout;
    ListView lv;
    View footer;
    VideoOnlineAdapter adapter;
    List<OnlineVideo> onlineVideos = new ArrayList<OnlineVideo>();
    int currentPage = 1,sumPage;
    String selectStart;
    int isFirst = 1;

    private boolean isLoading = false;
    private int totalItem;
    private int lastItem;
    private boolean isFirstres = true;

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.INTERNET_SUCCESS_FLAG:
                    String value = msg.obj.toString();

                    Log.e("info", "VideoOriginal value=" + value);

                    if(!(TextUtils.isEmpty(value))){
                        try {
                            JSONObject mainjo = new JSONObject(value);
                            JSONObject page = mainjo.getJSONObject("page");

                            sumPage = Integer.valueOf(page.getString("sumPage"));
                            selectStart = page.getString("selectEnd");

                            JSONArray data = mainjo.getJSONArray("data");
                            if(data != null && data.length() > 0){
//                                Log.e("info", "film data="+data.length());
                                for(int i=0;i<data.length();i++){
                                    JSONObject jo = data.getJSONObject(i);

                                    String userImg =  jo.getString("userImg");
                                    String timeLong = jo.getString("timeLong");
                                    String opencount = jo.getString("num");
                                    String name = jo.getString("name");
                                    String uploadimg =  jo.getString("uploadImg");
                                    String id = jo.getString("id");
                                    String userName = jo.getString("userName");
                                    String userId = jo.getString("userId");
                                    String introduction = jo.getString("introduction");


                                    OnlineVideo onlineVideo = new OnlineVideo(userImg, timeLong, opencount, name, uploadimg, id, userName, userId, introduction);
                                    onlineVideos.add(onlineVideo);

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        swipeRefreshLayout.setRefreshing(false);
                        isLoading = false;

                        if(sumPage < 2){
                            if(lv.getFooterViewsCount() > 0){
                                lv.removeFooterView(footer);
                                footer.setVisibility(View.GONE);

                            }
                        }

                        adapter.notifyDataSetChanged();

                        if(currentPage == 1 && onlineVideos.size() == 0){
                            ToastUtil.showToast(getActivity(), com.unbounded.video.R.string.havenodata, ToastUtil.CENTER);
                            setEmptyViewVisible(true);

                        }else {
                            setEmptyViewVisible(false);
                        }

                        progressDiddismiss();

                    }

                    break;
                case Constants.INTERNET_ERROR_FLAG:
                    swipeRefreshLayout.setRefreshing(false);
                    ToastUtil.showToast(getActivity(), com.unbounded.video.R.string.internet_error, ToastUtil.CENTER);
                    adapter.notifyDataSetChanged();
                    progressDiddismiss();

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
        if (onlineVideos != null) {
            onlineVideos.clear();
        }
        initFilms();
    }
    /**
     * 是否当前可见的生命周期方法
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser == true){
            if(isFirst == 1){
                initFirst();
                isFirst = 0;
            }
        }
    }

    /**
     * 视频列表
     */
    private void initFilms(){
        String url;
        if(!(TextUtils.isEmpty(selectStart)) && !("null".equals(selectStart)) && currentPage != 1){
            url = HttpConstants.SelectInfoByType_VideosUrl + "?type=original" + "&currentPage=" + currentPage + "&selectStart="+selectStart;
        }else {
            url = HttpConstants.SelectInfoByType_VideosUrl + "?type=original" + "&currentPage=" + currentPage;
        }

        HttpGetUtil get = new HttpGetUtil(handler, url, Constants.INTERNET_SUCCESS_FLAG);
        Thread thread = new Thread(get);
        thread.start();
        Log.e("info","原创initFilms......");
    }

    @Override
    public void initDatas() {
        super.initDatas();

    }

    @Override
    public void initView(View view) {
        super.initView(view);

        adapter = new VideoOnlineAdapter(VideoOriginalFragment.this, getActivity(), onlineVideos, onlineImgWidth, oneDp, screenHeight);

        footer = LayoutInflater.from(getActivity()).inflate(com.unbounded.video.R.layout.load_more_footer,null ,false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(com.unbounded.video.R.id.videorecomend_swiperefresh);
        rela1 = (RelativeLayout) view.findViewById(com.unbounded.video.R.id.videosrecommend_rela1);
        lv = (ListView) view.findViewById(com.unbounded.video.R.id.videorecomend_lv);

        rela1.setPadding(0,0,0,tabsmrelaHeight);

        lv.setAdapter(adapter);
        Log.e("info","initView......");

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
                onlineVideos.clear();
                footer.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();

                initFilms();
            }
        });

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

                            initFilms();
                        }else {
                            if(currentPage > 0){
                                isLoading = false;
                                footer.setVisibility(View.GONE);
                                ToastUtil.showToast(getActivity(), com.unbounded.video.R.string.not_have_more, ToastUtil.BOTTOM);
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

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), OnlineVideoInfoActivity.class);
                intent.putExtra("videoId",onlineVideos.get(i).getId());
                intent.putExtra("authorId",onlineVideos.get(i).getUserId());
                startActivity(intent);
            }
        });

    }

}
