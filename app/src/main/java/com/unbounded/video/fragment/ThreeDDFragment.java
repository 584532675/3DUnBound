package com.unbounded.video.fragment;

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
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.unbounded.video.BaseFragment;
import com.unbounded.video.activity.AllFilmActivity;
import com.unbounded.video.activity.MoviePlayActivity;
import com.unbounded.video.activity.OnlineVideoInfoActivity;
import com.unbounded.video.adapter.NewRecyclerAdapter;
import com.unbounded.video.adapter.NewVipRecyclerAdapter;
import com.unbounded.video.adapter.NewYoutubeRecyclerAdapter;
import com.unbounded.video.adapter.ThreeItemDecoration;
import com.unbounded.video.bean.OnlineVideo;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.constants.HttpConstants;
import com.unbounded.video.utils.HttpGetUtil;
import com.unbounded.video.utils.ToastUtil;
import com.unbounded.video.utils.imageLoad.GlideLogic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zjf on 2017/7/18 0018.
 * 影视视频
 */

public class ThreeDDFragment extends BaseFragment {
    static final int Free_Flag = 1;
    static final int Topimg_Flag = 2;
    static final int NotFree_Flag = 0;
    static final int Free_Vip_Flag = 3;
    static final int YOUTUBE_RESOLVE = 5;
    static final int YOUTUBE = 4;
    SwipeRefreshLayout swipeRefreshLayout;
    RelativeLayout rela1;
    ImageView topiv, freetagtv;
    TextView freemoretv;
    NewRecyclerAdapter rvFreeadapter;
    NewVipRecyclerAdapter rvNewFreeadapter;
    NewYoutubeRecyclerAdapter rvNewYoutubedapter;
    RecyclerView freerv;
    RecyclerView freeYoutuberv;
    List<OnlineVideo> threeddFrees = new ArrayList<OnlineVideo>();
    List<OnlineVideo> threeddNewFrees = new ArrayList<OnlineVideo>();
    List<OnlineVideo> threeddYoutubeFrees = new ArrayList<OnlineVideo>();
    Map<String, String> youTubeUrl = new HashMap<>();
    int isFirst = 1, topivHeight, imgwidth, newimgHeight;
    String topimg, topvideoid;


    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Free_Flag:
                    String freevalue = msg.obj.toString();
                    Log.e("info", "freevalue=" + freevalue);

                    if (!(TextUtils.isEmpty(freevalue))) {
                        try {
                            JSONObject mainjo = new JSONObject(freevalue);
                            JSONObject page = mainjo.getJSONObject("page");

                            JSONArray data = mainjo.getJSONArray("data");
                            if (data != null && data.length() > 0) {
//                                Log.e("info", "film data="+data.length());
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject jo = data.getJSONObject(i);

                                    String name = jo.getString("name");
                                    String uploadimg = jo.getString("uploadImg");
                                    String id = jo.getString("id");
                                    String userid = jo.getString("userId");
                                    String introduction = jo.getString("introduction");
                                    String uploadUrl = jo.getString("uploadUrl");
                                    OnlineVideo onlineVideo = new OnlineVideo(name, uploadimg, id, userid, introduction, uploadUrl);

                                    if (threeddFrees.size() < 9) {
                                        threeddFrees.add(onlineVideo);
                                    }

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        swipeRefreshLayout.setRefreshing(false);
                        rvFreeadapter.notifyDataSetChanged();
                        progressDiddismiss();
                    }

                    break;

                case Topimg_Flag:
                    String topimgvalue = msg.obj.toString();
                    Log.e("info", "topimgvalue=" + topimgvalue);
                    if (!(TextUtils.isEmpty(topimgvalue))) {
                        try {
                            JSONObject topimgjo = new JSONObject(topimgvalue);

                            topimg = topimgjo.getString("image");
                            topvideoid = topimgjo.getString("videoid");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        GlideLogic.glideLoadPic324(getActivity(), topimg, topiv, screenWidth * 11 / 16, topivHeight * 11 / 16);

                    }

                    break;
                //rjz add vip电影数据
                case Free_Vip_Flag:
                    String freeVipValue = msg.obj.toString();
                    Log.e("info", "freevalue=" + freeVipValue);

                    if (!(TextUtils.isEmpty(freeVipValue))) {
                        try {
                            JSONObject mainjo = new JSONObject(freeVipValue);
                            JSONObject page = mainjo.getJSONObject("page");

                            JSONArray data = mainjo.getJSONArray("data");
                            if (data != null && data.length() > 0) {
//                                Log.e("info", "film data="+data.length());
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject jo = data.getJSONObject(i);

                                    String name = jo.getString("name");
                                    String uploadimg = jo.getString("uploadImg");
                                    String id = jo.getString("id");
                                    String userid = jo.getString("userId");
                                    String introduction = jo.getString("introduction");
                                    OnlineVideo onlineVideo = new OnlineVideo(name, uploadimg, id, userid, introduction);

                                    if (threeddNewFrees.size() < 9) {
                                        threeddNewFrees.add(onlineVideo);
                                    }

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        swipeRefreshLayout.setRefreshing(false);
                        rvNewFreeadapter.notifyDataSetChanged();
                        progressDiddismiss();
                    }
                    //rjz end vip电影数据
                    break;
                case YOUTUBE_RESOLVE:
                    String freeYoutube = msg.obj.toString();
                    if (!(TextUtils.isEmpty(freeYoutube))) {
                        try {
                            JSONObject mainjo = new JSONObject(freeYoutube);
                            String youTubeUrl = mainjo.getString("url");
                            if (!TextUtils.isEmpty(youTubeUrl)) {
                                Log.v("ThreeDDFragment", "youTubeResolveUrl:" + youTubeUrl);
                                Intent intent = new Intent(getActivity(), MoviePlayActivity.class);
                                intent.putExtra("urlOrPath", youTubeUrl);
                                intent.putExtra("playName", "ddadas");
                                startActivity(intent);
                            } else {
                                ToastUtil.showToast(getContext(), "解析失败，请重试，或者跟换视频", Toast.LENGTH_LONG);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    break;
                case YOUTUBE:
                    String freeYoutubeValue = msg.obj.toString();
                    Log.e("info", "freeYoutubeValue=" + freeYoutubeValue);

                    if (!(TextUtils.isEmpty(freeYoutubeValue))) {
                        try {
                            JSONObject mainjo = new JSONObject(freeYoutubeValue);
                            JSONObject page = mainjo.getJSONObject("page");

                            JSONArray data = mainjo.getJSONArray("data");
                            if (data != null && data.length() > 0) {
//                                Log.e("info", "film data="+data.length());
                                youTubeUrl.clear();
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject jo = data.getJSONObject(i);

                                    String name = jo.getString("name");
                                    String uploadimg = jo.getString("uploadImg");
                                    String id = jo.getString("id");
                                    String userid = jo.getString("userId");
                                    String introduction = jo.getString("introduction");
                                    String urlYouTube = jo.getString("uploadUrl");
                                    OnlineVideo onlineVideo = new OnlineVideo(name, uploadimg, id, userid, introduction, urlYouTube);
                                    if (threeddYoutubeFrees.size() < 9) {
                                        threeddYoutubeFrees.add(onlineVideo);
                                    }

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        swipeRefreshLayout.setRefreshing(false);
                        rvNewYoutubedapter.notifyDataSetChanged();
                        progressDiddismiss();
                    }
                    break;
                case Constants.INTERNET_ERROR_FLAG:
                    swipeRefreshLayout.setRefreshing(false);
                    ToastUtil.showToast(getActivity(), com.unbounded.video.R.string.internet_error, ToastUtil.CENTER);
                    rvFreeadapter.notifyDataSetChanged();
                    rvNewFreeadapter.notifyDataSetChanged();
                    rvNewYoutubedapter.notifyDataSetChanged();
                    progressDiddismiss();

                    break;


            }
        }
    };
    private RecyclerView newRecyclerView;
    private TextView threeddfragNewTv;
    private ImageView threeddfragNewNext;

    @Override
    protected int getContentView() {
        return com.unbounded.video.R.layout.fragment_threedd;
    }

    /**
     * 是否当前可见的生命周期方法
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser == true) {
            if (isFirst == 1) {
                initFirst();
                isFirst = 0;
            }
        }
    }

    /**
     * 首次加载
     */
    private void initFirst() {
        progressDid();

        topivHeight = screenHeight * 3 / 10;

        if (threeddFrees != null) {
            threeddFrees.clear();
        }
        //rjz add 新增vip电影
        if (threeddNewFrees != null) {
            threeddNewFrees.clear();
        }
        if (threeddYoutubeFrees != null) {
            threeddYoutubeFrees.clear();
        }
        //rjz end 新增vip电影
        initFilms();
    }

    /**
     * 视频列表
     */
    private void initFilms() {
        String freeurl = HttpConstants.SelectInfoByType_VideosUrl + "?type=3d" + "&currentPage=1" + "&report=1";
        HttpGetUtil freeget = new HttpGetUtil(handler, freeurl, Free_Flag);
        Thread freethread = new Thread(freeget);
        freethread.start();

        String url = HttpConstants.SelectBanner_Url;
        HttpGetUtil get = new HttpGetUtil(handler, url, Topimg_Flag);
        Thread thread = new Thread(get);
        thread.start();

        String vipUrl = HttpConstants.SelectInfoByType_VideosUrl + "?type=3d" + "&currentPage=1" + "&report=0";
        HttpGetUtil getVip = new HttpGetUtil(handler, vipUrl, Free_Vip_Flag);
        Thread threadVip = new Thread(getVip);
        threadVip.start();

        String youtubeUrl = HttpConstants.YouTube_VideosUrl + "?type=youtube" + "&currentPage=1" + "&report=1";
        HttpGetUtil getyoutube = new HttpGetUtil(handler, youtubeUrl, YOUTUBE);
        Thread threadYoutube = new Thread(getyoutube);
        threadYoutube.start();
    }


    @Override
    public void initDatas() {
        super.initDatas();
    }

    @Override
    public void initView(View view) {
        super.initView(view);

        imgwidth = screenWidth / 3;

        topiv = (ImageView) view.findViewById(com.unbounded.video.R.id.threeddfrag_topiv);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(com.unbounded.video.R.id.threeddfrag_swiperefresh);
        rela1 = (RelativeLayout) view.findViewById(com.unbounded.video.R.id.threeddfrag_rela1);
        freemoretv = (TextView) view.findViewById(com.unbounded.video.R.id.threeddfrag_freemoretv);
        freetagtv = (ImageView) view.findViewById(com.unbounded.video.R.id.threeddfrag_freetag);
        freerv = (RecyclerView) view.findViewById(com.unbounded.video.R.id.threeddfrag_freerecyclerView);
        newRecyclerView = (RecyclerView) view.findViewById(com.unbounded.video.R.id.threeddfrag_new_recyclerView);
        freeYoutuberv = (RecyclerView) view.findViewById(com.unbounded.video.R.id.free_youtube_rv);
        threeddfragNewTv = (TextView) view.findViewById(com.unbounded.video.R.id.threeddfrag_new_tv);
        threeddfragNewNext = (ImageView) view.findViewById(com.unbounded.video.R.id.threeddfrag_new_next);
//        rvFreeadapter = new ThreeddVideoOnlineAdapter(getActivity(), threeddFrees, null, imgwidth, "free");
        rvFreeadapter = new NewRecyclerAdapter(getActivity(), threeddFrees, null, imgwidth, 1);
        rvNewFreeadapter = new NewVipRecyclerAdapter(getActivity(), threeddNewFrees, null, imgwidth, 1);
        rvNewYoutubedapter = new NewYoutubeRecyclerAdapter(getActivity(), threeddYoutubeFrees, null, imgwidth, 1);
        rela1.setPadding(0, 0, 0, tabsmrelaHeight);

    }

    @Override
    public void initParams() {
        super.initParams();

        topivHeight = screenHeight * 3 / 10;

        imgwidth = (screenWidth - 4 * oneDp) / 3;
        newimgHeight = imgwidth * 490 / 355;

        ViewGroup.LayoutParams params = topiv.getLayoutParams();
        params.width = screenWidth;
        params.height = topivHeight;
        topiv.setLayoutParams(params);

//        ViewGroup.LayoutParams rvFreeparams = (ViewGroup.LayoutParams) freerv.getLayoutParams();
//        rvFreeparams.height = (imgHeight + 48*oneDp) * 2;
//        freerv.setLayoutParams(rvFreeparams);
        ViewGroup.LayoutParams rvFreeparams = freerv.getLayoutParams();
        rvFreeparams.width = screenWidth;
        rvFreeparams.height = (newimgHeight + 40 * oneDp) * 3 + 15 * oneDp;
        freerv.setLayoutParams(rvFreeparams);

        freeYoutuberv.setLayoutParams(rvFreeparams);
        newRecyclerView.setLayoutParams(rvFreeparams);


    }

    @Override
    public void initEvent() {
        super.initEvent();

        final GridLayoutManager mfreeGridLayoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);//设置为一个2列的纵向网格布局
        mfreeGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        });
        //设置布局管理器
        freerv.setLayoutManager(mfreeGridLayoutManager);
        ThreeItemDecoration decoration = new ThreeItemDecoration(1 * oneDp);
        freerv.addItemDecoration(decoration);
        //设置为垂直布局，这也是默认的
        //layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置Adapter
        freerv.setAdapter(rvFreeadapter);
        //设置增加或删除条目的动画
        freerv.setItemAnimator(new DefaultItemAnimator());


        final GridLayoutManager mNewGridLayoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);//设置为一个2列的纵向网格布局
        mfreeGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        });
        //设置布局管理器
        newRecyclerView.setLayoutManager(mNewGridLayoutManager);
        ThreeItemDecoration newDecoration = new ThreeItemDecoration(1 * oneDp);
        newRecyclerView.addItemDecoration(newDecoration);
        //设置为垂直布局，这也是默认的
        //layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置Adapter
        newRecyclerView.setAdapter(rvNewFreeadapter);
        //设置增加或删除条目的动画
        newRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //youtube新增视频

        final GridLayoutManager mYoutubeGridLayoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);//设置为一个2列的纵向网格布局
        mfreeGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        });
        //设置布局管理器
        freeYoutuberv.setLayoutManager(mYoutubeGridLayoutManager);
        //设置布局管理器
        ThreeItemDecoration youTubeDecoration = new ThreeItemDecoration(1 * oneDp);
        freeYoutuberv.addItemDecoration(youTubeDecoration);
        //设置为垂直布局，这也是默认的
        //layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置Adapter
        freeYoutuberv.setAdapter(rvNewYoutubedapter);
        //设置增加或删除条目的动画
        freeYoutuberv.setItemAnimator(new DefaultItemAnimator());


        rvFreeadapter.setOnItemClickListener(new NewRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.e("info", "点击第" + position + "个");
                Intent intent = new Intent();
                intent.setClass(getActivity(), OnlineVideoInfoActivity.class);
                intent.putExtra("videoId", threeddFrees.get(position).getId());
                intent.putExtra("authorId", threeddFrees.get(position).getUserId());
                startActivity(intent);

            }
        });
        //rjz add 新增vip电影
        rvNewFreeadapter.setOnItemClickListener(new NewVipRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), OnlineVideoInfoActivity.class);
                intent.putExtra("videoId", threeddNewFrees.get(position).getId());
                intent.putExtra("authorId", threeddNewFrees.get(position).getUserId());
                startActivity(intent);
            }
        });
        //rjz 添加，新增youtube电影
        rvNewYoutubedapter.setOnItemClickListener(new NewYoutubeRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
//                ToastUtil.showToast(getContext(), "解析地址中,请稍等", Toast.LENGTH_LONG);
//                String youtubeUrl = HttpConstants.Resolve_video + threeddYoutubeFrees.get(position).getUploadUrl();
//                Log.v("ThreeDDFragment", "youtubeUrl：" + youtubeUrl);
//                HttpGetUtil getyoutube = new HttpGetUtil(handler, youtubeUrl, YOUTUBE_RESOLVE);
//                Thread threadYoutube = new Thread(getyoutube);
//                threadYoutube.start();

                Intent intent = new Intent(getActivity(), MoviePlayActivity.class);
                intent.putExtra("urlOrPath", threeddYoutubeFrees.get(position).getUploadUrl());
                intent.putExtra("playName", threeddYoutubeFrees.get(position).getName());
                intent.putExtra("isYouTube", true);
                startActivity(intent);
            }
        });
        //rjz end
        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                threeddFrees.clear();
                rvFreeadapter.notifyDataSetChanged();
                topvideoid = "";
                //rjz add 新增vip数据
                threeddNewFrees.clear();
                rvNewFreeadapter.notifyDataSetChanged();
                //rjz end 新增vip数据
                //rjz add Youtube视频
                rvNewYoutubedapter.notifyDataSetChanged();
                threeddYoutubeFrees.clear();
                //rjz end Youtube视频
                initFilms();
            }
        });

        //顶部
        topiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(TextUtils.isEmpty(topvideoid))) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), OnlineVideoInfoActivity.class);
                    intent.putExtra("videoId", topvideoid);
                    startActivity(intent);
                }
            }
        });
        //免费更多
        freemoretv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), AllFilmActivity.class);
                intent.putExtra("isVip", 1);
                intent.putExtra("allFilmType", "threedd");
                startActivity(intent);
            }
        });

        freetagtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), AllFilmActivity.class);
                intent.putExtra("isVip", 1);
                intent.putExtra("allFilmType", "threedd");
                startActivity(intent);
            }
        });
        threeddfragNewTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), AllFilmActivity.class);
                intent.putExtra("isVip", 0);
                intent.putExtra("allFilmType", "threedd");
                startActivity(intent);
            }
        });
        threeddfragNewNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), AllFilmActivity.class);
                intent.putExtra("allFilmType", "threedd");
                intent.putExtra("isVip", 0);
                startActivity(intent);
            }
        });

    }
}
