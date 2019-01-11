package com.unbounded.video.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.unbounded.video.BaseActivity;
import com.unbounded.video.R;
import com.unbounded.video.adapter.NewRecyclerAdapter;
import com.unbounded.video.adapter.ThreeItemDecoration;
import com.unbounded.video.bean.OnlineVideo;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.constants.HttpConstants;
import com.unbounded.video.utils.HttpPostUtil;
import com.unbounded.video.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjf on 2017/11/21 0021.
 */

public class VideoTypeActivity extends BaseActivity {
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    NewRecyclerAdapter adapter;

    List<OnlineVideo> filmlist = new ArrayList<OnlineVideo>();
    List<String> keyList = new ArrayList<String>();
    List<String> valueList = new ArrayList<String>();
    int currentPage = 1,imgwidth,sumPage;
    String selectStart,videoType;
    boolean isLoading = false;
    int totalItem;
    int lastItem;

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.INTERNET_SUCCESS_FLAG:
                    String value = msg.obj.toString();
                    Log.e("info","value="+value);

                    if(!(TextUtils.isEmpty(value))){
                        try {
                            JSONObject filmjo = new JSONObject(value);

                            JSONObject page = filmjo.getJSONObject("page");
                            sumPage = Integer.valueOf(page.getString("sumPage"));
                            selectStart = page.getString("selectEnd");

                            JSONArray data = filmjo.getJSONArray("data");
                            if(data != null && data.length() > 0){
                                for(int i=0;i<data.length();i++){
                                    JSONObject jo = data.getJSONObject(i);

                                    String name = jo.getString("name");
                                    String uploadimg = jo.getString("uploadImg");
                                    String id = jo.getString("id");
                                    String userid = jo.getString("userId");
                                    String introduction = jo.getString("introduction");

                                    OnlineVideo onlineVideo = new OnlineVideo(name, uploadimg, id, userid, introduction);
                                    filmlist.add(onlineVideo);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        isLoading = false;
                        swipeRefreshLayout.setRefreshing(false);
                        adapter.notifyDataSetChanged();

                        if(currentPage == 1 && filmlist.size() == 0){
                            ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.havenodata, ToastUtil.CENTER);
                            setEmptyViewVisible(true);

                        }else {
                            setEmptyViewVisible(false);
                        }

                        progressDiddismiss();
                    }

                    break;
                case Constants.INTERNET_ERROR_FLAG:
                    swipeRefreshLayout.setRefreshing(false);
                    ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.internet_error, ToastUtil.CENTER);
                    adapter.notifyDataSetChanged();
                    progressDiddismiss();

                    break;
            }
        }
    };

    @Override
    protected int getContentView() {
        return com.unbounded.video.R.layout.activity_allfilm;
    }

    @Override
    public void init(Intent intent) {
        super.init(intent);
        videoType = intent.getStringExtra("videoType");

        setRightImgVisible(true);
        setRightImgBg(com.unbounded.video.R.mipmap.mainsearchiv);
        setTitleName(videoType);

        initFirst();
        imgwidth = screenWidth/3;
    }

    /**
     * 首次加载
     */
    private void initFirst(){
        progressDid();

        if(filmlist != null){
            filmlist.clear();
        }
        initFilms();
    }

    /**
     * 视频列表
     */
    private void initFilms(){
        keyList.clear();
        valueList.clear();

        keyList.add("comeFrom");
        keyList.add("videoType");
        keyList.add("currentPage");

        valueList.add("gm");
        valueList.add(videoType);
        valueList.add(currentPage + "");

        if(!(TextUtils.isEmpty(selectStart)) && !("null".equals(selectStart)) && currentPage != 1){
            keyList.add("selectStart");
            valueList.add(selectStart);
        }
        String url = HttpConstants.VideoType_VideosUrl;
        if(videoType.equals(getString(R.string.word_recommend_video))){
            url = HttpConstants.VideoType_getRecommend_VideosUrl;
        }
        Log.v("handlerUrl","url:"+url+":::keyList"+keyList.toString()+":::valueList"+valueList.toString());
        HttpPostUtil post = new HttpPostUtil(handler, url, keyList, valueList, Constants.INTERNET_SUCCESS_FLAG);
        Thread thread = new Thread(post);
        thread.start();

    }

    @Override
    public void initView() {
        super.initView();

        imgwidth = screenWidth/3;

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(com.unbounded.video.R.id.allfilm_swiperefresh);
        recyclerView = (RecyclerView) findViewById(com.unbounded.video.R.id.allfilm_recyclerView);
        adapter = new NewRecyclerAdapter(getApplicationContext(), filmlist, null, imgwidth);

    }
    @Override
    public void initParams() {
        super.initParams();
    }

    /**
     * 搜索按钮
     */
    @Override
    public void onRightClick(View v) {
        super.onRightClick(v);

        openActivity(SearchFilmActivity.class);
    }

    @Override
    public void initEvent() {
        super.initEvent();

        final GridLayoutManager mGridLayoutManager=new GridLayoutManager(getApplicationContext(),3,GridLayoutManager.VERTICAL,false);//设置为一个2列的纵向网格布局
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        });
        //设置布局管理器
        recyclerView.setLayoutManager(mGridLayoutManager);
//        SpacesItemDecoration newdecoration = new SpacesItemDecoration(2*oneDp);
        ThreeItemDecoration newdecoration = new ThreeItemDecoration(1*oneDp);
        recyclerView.addItemDecoration(newdecoration);
        //设置为垂直布局，这也是默认的
        //layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置Adapter
        recyclerView.setAdapter(adapter);
        //设置增加或删除条目的动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter.setOnItemClickListener(new NewRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.e("info", "点击第" + position + "个");
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), OnlineVideoInfoActivity.class);
                intent.putExtra("videoId",filmlist.get(position).getId());
                intent.putExtra("authorId",filmlist.get(position).getUserId());
                startActivity(intent);

            }
        });

        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                isLoading = true;
                swipeRefreshLayout.setRefreshing(true);
                filmlist.clear();
                adapter.notifyDataSetChanged();

                initFilms();
            }
        });
        //
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisiableItemPosition = mGridLayoutManager.findLastVisibleItemPosition();
                lastItem = lastVisiableItemPosition+1;
                totalItem = mGridLayoutManager.getItemCount();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(totalItem == lastItem&&newState == 0){
                    if(isLoading == false){
                        if(currentPage < sumPage){
                            isLoading = true;
                            currentPage = currentPage + 1;

                            progressDid();
                            initFilms();
                        }else {
                            if(currentPage > 0){
                                isLoading = false;
                                ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.not_have_more, ToastUtil.BOTTOM);
                            }
                        }
                    }
                }

            }

        });
    }
}
