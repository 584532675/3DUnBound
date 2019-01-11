package com.unbounded.video.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.stereo.Interaction.InteractionManager;
import com.stereo.angle.AngleActivity;
import com.stereo.util.ActionCallback;
import com.unbounded.video.BaseFragment;
import com.unbounded.video.R;
import com.unbounded.video.activity.AllFilmActivity;
import com.unbounded.video.activity.OnlinePicInfoActivity;
import com.unbounded.video.activity.OnlineVideoInfoActivity;
import com.unbounded.video.activity.VideoTypeActivity;
import com.unbounded.video.adapter.HotVideoOnlineAdapter;
import com.unbounded.video.adapter.NewRecyclerAdapter;
import com.unbounded.video.adapter.ThreeItemDecoration;
import com.unbounded.video.adapter.TwoItemDecoration;
import com.unbounded.video.bean.BannerImg;
import com.unbounded.video.bean.OnlineVideo;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.constants.HttpConstants;
import com.unbounded.video.utils.DensityUtil;
import com.unbounded.video.utils.HttpGetUtil;
import com.unbounded.video.utils.HttpPostUtil;
import com.unbounded.video.utils.SharedPrefsUtil;
import com.unbounded.video.utils.ToastUtil;
import com.unbounded.video.utils.rotate3DAnimation.RotateAnimationUtil;
import com.unbounded.video.view.banner.gallerybanner.GalleryFlow;
import com.unbounded.video.view.banner.gallerybanner.ImageAdapter;
import com.unbounded.video.view.banner.ui.BannerSwipeRefreshLayout;
import com.zhuge.analysis.stat.ZhugeSDK;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by zjf on 2017/7/18 0018.
 * 推荐视频
 *
 * 首页界面的fragment
 */

public class VideoRecommendFragment extends BaseFragment implements View.OnClickListener {
    /**
     * 广告接口访问正确返回
     */
    public final static int BANNER_SUCCESS_FLAG = 1;
    public final static int NEXTHOT_FLAG = 2;
    public final static int Viewpager_BANNER_FLAG = 3;
    public final static int NEWLIST_FLAG = 4;
    public final static String Video_FLAG = "video";
    public final static String Image_FLAG = "image";
    public final static String ThreeDDVideo_FLAG = "threeDD";
    RelativeLayout rela1,btnsrela,hottvrela;
    RotateAnimationUtil rotateAnimationUtil;
    FrameLayout hotFrameLayout;
    TextView hotmoretv,newmoretv,btnsline1,btnsline2,btnsline3,btnsline4;
    RecyclerView hotrv,hotrv2,newrv;
    List<OnlineVideo> hotVideos = new ArrayList<OnlineVideo>();
    List<OnlineVideo> newVideos = new ArrayList<OnlineVideo>();
    BannerSwipeRefreshLayout swipeRefreshLayout;
    HotVideoOnlineAdapter hotAdapter,hotAdapter2;
    NewRecyclerAdapter newAdapter;
    List<BannerImg> banners = new ArrayList<BannerImg>();

    ImageView dianyiv,donghiv,zongyiv,pianhiv,fengjiv,meiniv,youxiv,qitiv,hotnextiv,newmoretag;

    int isFirst = 1;
    int hotChange = 0;

    Dialog activeDialog;

    int hotimgwidth,hotimgHeight, newimgwidth,newimgHeight;
    String phoneNum;
    List<String> keyList = new ArrayList<String>();
    List<String> valueList = new ArrayList<String>();

    int currentitem = 21;

    GalleryFlow gallerybanner;
    ImageAdapter imageAdapter;
    int bannerHeight,bannerimgWidth;
    public static int bannerSize;


    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //热播4个
                case Constants.INTERNET_SUCCESS_FLAG:
                    String hotvalue = msg.obj.toString();

                    Log.e("info","hotvalue="+hotvalue);

                    if(!(TextUtils.isEmpty(hotvalue))){
                        try {
                            JSONObject mainjo = new JSONObject(hotvalue);

//                            JSONObject shelvesData = mainjo.getJSONObject("jinshanyunPage");

                            JSONArray jinshanyunData = mainjo.getJSONArray("jinshanyunDate");

//                            if(shelvesData != null && shelvesData.length() > 0){
//                                for(int i=0;i<shelvesData.length();i++){
//                                    JSONObject shelvesDatajo = shelvesData.getJSONObject(i);
//                                    String name = shelvesDatajo.getString("name");
//                                    String uploadimg = shelvesDatajo.getString("uploadImg");
//                                    String id = shelvesDatajo.getString("id");
//                                    String userid = shelvesDatajo.getString("userId");
//                                    String introduction = shelvesDatajo.getString("introduction");
//
//                                    OnlineVideo hotVideo = new OnlineVideo(name, uploadimg, id, userid, introduction);
//                                    hotVideos.add(hotVideo);
//                                }
//                            }

                            if(jinshanyunData != null && jinshanyunData.length() > 0){
                                for(int a=0;a<jinshanyunData.length();a++){
                                    JSONObject jinshanyunDatajo = jinshanyunData.getJSONObject(a);
                                    String name = jinshanyunDatajo.getString("name");
                                    String uploadimg = jinshanyunDatajo.getString("uploadImg");
                                    String id = jinshanyunDatajo.getString("id");
                                    String userid = jinshanyunDatajo.getString("userId");
                                    String introduction = jinshanyunDatajo.getString("introduction");

                                    OnlineVideo newVideo = new OnlineVideo(name, uploadimg, id, userid, introduction);
                                    if(newVideos.size()<9){
                                        newVideos.add(newVideo);
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        swipeRefreshLayout.setRefreshing(false);
                        hotAdapter.notifyDataSetChanged();
                        hotAdapter2.notifyDataSetChanged();
                        newAdapter.notifyDataSetChanged();
                        progressDiddismiss();

                    }

                    break;
                //9个
                case NEWLIST_FLAG:
                    String newvalue = msg.obj.toString();

                    Log.e("info","newvalue="+newvalue);

                    if(!(TextUtils.isEmpty(newvalue))){
                        try {
                            JSONObject newjo = new JSONObject(newvalue);

                            JSONArray jinshanyunData = newjo.getJSONArray("jinshanyunDate");

                            if(jinshanyunData != null && jinshanyunData.length() > 0){
                                for(int a=0;a<jinshanyunData.length();a++){
                                    JSONObject jinshanyunDatajo = jinshanyunData.getJSONObject(a);
                                    String name = jinshanyunDatajo.getString("name");
                                    String uploadimg = jinshanyunDatajo.getString("uploadImg");
                                    String id = jinshanyunDatajo.getString("id");
                                    String userid = jinshanyunDatajo.getString("userId");
                                    String introduction = jinshanyunDatajo.getString("introduction");

                                    OnlineVideo newVideo = new OnlineVideo(name, uploadimg, id, userid, introduction);
                                    newVideos.add(newVideo);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        swipeRefreshLayout.setRefreshing(false);
                        newAdapter.notifyDataSetChanged();
                        progressDiddismiss();

                    }

                    break;

                case NEXTHOT_FLAG:
                    String nexthotvalue = msg.obj.toString();
                    Log.e("info","nexthotvalue="+nexthotvalue);

                    if(!(TextUtils.isEmpty(nexthotvalue))){
                        try {
                            JSONObject nexthotjo = new JSONObject(nexthotvalue);

                            JSONArray shelvesData = nexthotjo.getJSONArray("shelvesData");

                            if(shelvesData != null && shelvesData.length() > 0){
                                if(hotVideos != null){
                                    hotVideos.clear();
                                }

                                for(int i=0;i<shelvesData.length();i++){
                                    JSONObject shelvesDatajo = shelvesData.getJSONObject(i);
                                    String name = shelvesDatajo.getString("name");
                                    String uploadimg = shelvesDatajo.getString("uploadImg");
                                    String id = shelvesDatajo.getString("id");
                                    String userid = shelvesDatajo.getString("userId");
                                    String introduction = shelvesDatajo.getString("introduction");

                                    OnlineVideo hotVideo = new OnlineVideo(name, uploadimg, id, userid, introduction);
                                    hotVideos.add(hotVideo);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(hotChange == 0){
                            hotAdapter2.notifyDataSetChanged();
                            rotateAnimationUtil.applyRotateAnimation(1, 0, 90);
                            hotChange = 1;
                        }else{
                            hotAdapter.notifyDataSetChanged();
                            rotateAnimationUtil.applyRotateAnimation(-1, 0, -90);
                            hotChange = 0;
                        }


                    }

                    break;
                //广告
                case BANNER_SUCCESS_FLAG:
                    String valueBanner = msg.obj.toString();

                    Log.e("info","valueBanner="+valueBanner);

                    if(!(TextUtils.isEmpty(valueBanner))){
                        JSONObject bannermainjo = null;
                        try {
                            bannermainjo = new JSONObject(valueBanner);

                            JSONArray img = bannermainjo.getJSONArray("img");
                            JSONArray text = bannermainjo.getJSONArray("text");

                            //
                            if(text != null && text.length() > 0){


                            }

                            //
                            if(img != null && img.length() > 0){
                                if(banners != null){
                                    banners.clear();
                                }
                                //rjz 修改，3D无界不需要从服务器拿图片
                                for(int i=0;i<img.length();i++){
                                    JSONObject jo = img.getJSONObject(i);

                                    String imgurl = jo.getString("imgurl");
                                    String type = jo.getString("type");
                                    String url = jo.getString("url");

                                    BannerImg bannerImg = new BannerImg(imgurl, type, url);
                                    banners.add(bannerImg);
                                }
                                //rjz 修改，3D无界不需要从服务器拿图片 end
//                                BannerImg bannerImg = new BannerImg(R.mipmap.recomment_banner_one, "image", "");
//                                banners.add(bannerImg);
//                                BannerImg bannerImg1 = new BannerImg(R.mipmap.recomment_banner_two, "image", "");
//                                banners.add(bannerImg1);
//                                BannerImg bannerImg2 = new BannerImg(R.mipmap.recomment_banner_three, "image", "");
//                                banners.add(bannerImg2);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(imageAdapter != null){
                            imageAdapter = null;
                        }
                        imageAdapter = new ImageAdapter(getActivity(), banners, bannerimgWidth, bannerHeight , screenWidth);
                        gallerybanner.setAdapter(imageAdapter);
//                        gallerybanner.setSelection(currentitem);
                        imageAdapter.notifyDataSetChanged();

                        bannerSize = banners.size();
                        handler.sendEmptyMessageDelayed(Viewpager_BANNER_FLAG, 3000);
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    break;

                case Viewpager_BANNER_FLAG:
                    currentitem = currentitem + 1;
                    gallerybanner.setSelection(currentitem);
                    handler.sendEmptyMessageDelayed(Viewpager_BANNER_FLAG, 3000);

                    break;

                case Constants.INTERNET_ERROR_FLAG:
                    ToastUtil.showToast(getActivity(), R.string.internet_error, ToastUtil.CENTER);
                    swipeRefreshLayout.setRefreshing(false);
                    progressDiddismiss();

                    break;
            }
        }
    };
    private ScrollView videoRecommendSl;
    private FrameLayout bigMovieFl;
    private FrameLayout newMovieFv;
    private FrameLayout fashionShowFv;
    private FrameLayout shortVideoFv;
    private FrameLayout recommendVideoFv;

    @Override
    protected int getContentView() {
        return R.layout.fragment_videorecommend1;
    }

    /**
     * 首次加载
     */
    private void initFirst(){
        progressDid();

        if(hotVideos != null){
            hotVideos.clear();
        }
        if(newVideos != null){
            newVideos.clear();
        }

        initFilms();
        initBannerList();
    }

    @Override
    public void onResume() {
//        Log.e("info","推荐 onResume ");
        super.onResume();
        if(isFirst == 1){
            initFirst();
            isFirst = 0;
        }else{
            if(banners != null && banners.size() > 0){
                currentitem = 21;
                handler.sendEmptyMessageDelayed(Viewpager_BANNER_FLAG, 3000);
            }
        }
    }
    @Override
    public void onPause() {
        super.onPause();
//        Log.e("info","推荐 onPause ");
        handler.removeMessages(Viewpager_BANNER_FLAG);
//        if(banner != null){
//            banner.stopAutoPlay();
//        }
    }

    @Override
    public void onStop() {
        super.onStop();
        handler.removeMessages(Viewpager_BANNER_FLAG);
//        if(banner != null){
//            banner.stopAutoPlay();
//        }
    }
    /**
     * 是否当前可见的生命周期方法
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser == true){
            if(banners != null && banners.size() > 0){
                currentitem = 21;
                handler.sendEmptyMessageDelayed(Viewpager_BANNER_FLAG, 3000);
            }
        }else{
            Log.e("info","isVisibleToUser == false");
            handler.removeMessages(Viewpager_BANNER_FLAG);
        }
    }

    @Override
    public void initDatas() {
        super.initDatas();

    }
    /**
     * 广告控件
     */
    private void initBannerView(){
//        headview = View.inflate(getActivity(), R.layout.fragment_videorecommendheadview, null);
//        banner = (Banner) headview.findViewById(R.id.videorecommend_banner);

    }
    /**
     * 视频列表
     */
    private void initFilms(){
        Log.e("info","initFilms=");
        keyList.clear();
        valueList.clear();

//        keyList.add("comeFrom");
////        keyList.add("version");
//        keyList.add("shelvesCurrentPage");
//        keyList.add("jinshanyunCurrentPage");
//
//        valueList.add("gm");
////        valueList.add("versioncode");
//        valueList.add("1");
//        valueList.add("1");

        String url = HttpConstants.NewandHot_VideosUrl+"?currentPage=1&selectStart=";
        HttpPostUtil post = new HttpPostUtil(handler, url, keyList, valueList, Constants.INTERNET_SUCCESS_FLAG);
        Thread thread = new Thread(post);
        thread.start();

        //下面9个
//        String newurl = HttpConstants.NewandHot_VideosUrl + "&shelvesCurrentPage=1" + "&jinshanyunCurrentPage=1";
//        HttpGetUtil get = new HttpGetUtil(handler, newurl, NEWLIST_FLAG);
//        Thread newthread = new Thread(get);
//        newthread.start();
    }
    /**
     * 广告数据
     */
    private void initBannerList(){
        String url = HttpConstants.Banner_Url;
        HttpGetUtil get = new HttpGetUtil(handler, url, BANNER_SUCCESS_FLAG);
        Thread thread = new Thread(get);
        thread.start();

    }

    @Override
    public void initView(View view) {
        super.initView(view);

        hotimgwidth = screenWidth/2;
        newimgwidth = screenWidth/3;

        rela1 = (RelativeLayout) view.findViewById(R.id.videosrecommend_rela1);
        btnsrela = (RelativeLayout) view.findViewById(R.id.videorecommend_btnsrela);
        hottvrela = (RelativeLayout) view.findViewById(R.id.videorecommend_hotrela);
        swipeRefreshLayout = (BannerSwipeRefreshLayout) view.findViewById(R.id.videosrecommend_swiperefresh);
        hotmoretv = (TextView) view.findViewById(R.id.videorecommend_hotmoretv);
        newmoretv = (TextView) view.findViewById(R.id.videorecommend_newmoretv);
        hotrv = (RecyclerView) view.findViewById(R.id.videorecommend_hotrecyclerView);
        newrv = (RecyclerView) view.findViewById(R.id.videorecommend_newrecyclerView);
        gallerybanner = (GalleryFlow) view.findViewById(R.id.videorecommend_gallerybanner);

        dianyiv = (ImageView) view.findViewById(R.id.videorecommend_btnsiv1);
        donghiv = (ImageView) view.findViewById(R.id.videorecommend_btnsiv2);
        zongyiv = (ImageView) view.findViewById(R.id.videorecommend_btnsiv3);
        pianhiv = (ImageView) view.findViewById(R.id.videorecommend_btnsiv4);
        fengjiv = (ImageView) view.findViewById(R.id.videorecommend_btnsiv5);
        meiniv = (ImageView) view.findViewById(R.id.videorecommend_btnsiv6);
        youxiv = (ImageView) view.findViewById(R.id.videorecommend_btnsiv7);
        qitiv = (ImageView) view.findViewById(R.id.videorecommend_btnsiv8);
        hotnextiv = (ImageView) view.findViewById(R.id.videorecommend_hottag);
        newmoretag = (ImageView) view.findViewById(R.id.videorecommend_newtag);

        btnsline1 = (TextView) view.findViewById(R.id.videorecommend_btnsline1);
        btnsline2 = (TextView) view.findViewById(R.id.videorecommend_btnsline2);
        btnsline3 = (TextView) view.findViewById(R.id.videorecommend_btnsline3);
        btnsline4 = (TextView) view.findViewById(R.id.videorecommend_btnsline4);

        hotFrameLayout = (FrameLayout) view.findViewById(R.id.videorecommend_hotrecyclerViewcontainer);
        hotrv2 = (RecyclerView) view.findViewById(R.id.videorecommend_hotrecyclerView2);




        /*新增列表*/
        TextView bigMovieTv = (TextView) view.findViewById(R.id.big_movie_tv);
        TextView newMovieTv = (TextView) view.findViewById(R.id.new_movie_tv);
        TextView fashionShowTv = (TextView) view.findViewById(R.id.fashion_show_tv);
        TextView shortVideoTv = (TextView) view.findViewById(R.id.short_video_tv);
        TextView recommendVideoTv = (TextView) view.findViewById(R.id.recommend_video_tv);
        bigMovieFl = (FrameLayout) view.findViewById(R.id.big_movie_iv);
        newMovieFv = (FrameLayout) view.findViewById(R.id.new_movie_iv);
        fashionShowFv = (FrameLayout) view.findViewById(R.id.fashion_show_iv);
        shortVideoFv = (FrameLayout) view.findViewById(R.id.short_video_iv);
        recommendVideoFv = (FrameLayout) view.findViewById(R.id.recommend_video_iv);

        LinearLayout nextMore = (LinearLayout) view.findViewById(R.id.next_more);
        videoRecommendSl = (ScrollView) view.findViewById(R.id.video_recommend_sl);
        bigMovieFl.setOnClickListener(this);
        newMovieFv.setOnClickListener(this);
        fashionShowFv.setOnClickListener(this);
        shortVideoFv.setOnClickListener(this);
        recommendVideoFv.setOnClickListener(this);
        nextMore.setOnClickListener(this);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "siyuanheiti.ttf");
        bigMovieTv.setTypeface(typeface);
        newMovieTv.setTypeface(typeface);
        fashionShowTv.setTypeface(typeface);
        shortVideoTv.setTypeface(typeface);
        recommendVideoTv.setTypeface(typeface);

        bannerimgWidth = screenWidth - 20/* - 28*oneDp*/;
        bannerHeight = screenHeight * 520/ 1920;

        hotAdapter = new HotVideoOnlineAdapter(getActivity(), hotVideos, null, hotimgwidth, 1);
        hotAdapter2 = new HotVideoOnlineAdapter(getActivity(), hotVideos, null, hotimgwidth, 1);
        newAdapter = new NewRecyclerAdapter(getActivity(), newVideos, null, newimgwidth, 1);

        gallerybanner.setSpacing(10);

        rela1.setPadding(0,0,0,tabsmrelaHeight);
        btnsrela.setPadding(74*screenWidth/1080, 50*screenHeight/1920, 74*screenWidth/1080, 30*screenHeight/1920);

        rotateAnimationUtil = new RotateAnimationUtil(hotFrameLayout, hotrv, hotrv2);

    }

    @Override
    public void initParams() {
        super.initParams();
        hotimgwidth = (screenWidth - 2*oneDp)/2;
        hotimgHeight = hotimgwidth * 310/536;

        newimgwidth = (screenWidth - 4*oneDp)/3;
        newimgHeight = newimgwidth * 490/355;

        int btnwidth = screenWidth*146/1080;
        bannerHeight = screenHeight * 520/ 1920;

        ViewGroup.LayoutParams params = gallerybanner.getLayoutParams();
        params.height = bannerHeight;
        gallerybanner.setLayoutParams(params);



//        ViewGroup.LayoutParams hotrvparams = (ViewGroup.LayoutParams) hotrv.getLayoutParams();
//        hotrvparams.width = screenWidth;
//        hotrvparams.height = (hotimgHeight + 40*oneDp) * 2;
//        hotrv.setLayoutParams(hotrvparams);
//
//        ViewGroup.LayoutParams hotrv2params = (ViewGroup.LayoutParams) hotrv2.getLayoutParams();
//        hotrv2params.width = screenWidth;
//        hotrv2params.height = (hotimgHeight + 40*oneDp) * 2;
//        hotrv2.setLayoutParams(hotrv2params);


        ViewGroup.LayoutParams hotFrameLayoutparams = hotFrameLayout.getLayoutParams();
        hotFrameLayoutparams.width = screenWidth;
        hotFrameLayoutparams.height = (hotimgHeight + 40*oneDp) * 2;
        hotFrameLayout.setLayoutParams(hotFrameLayoutparams);

        RelativeLayout.LayoutParams newrvparams = (RelativeLayout.LayoutParams) newrv.getLayoutParams();
        newrvparams.width = screenWidth;
        newrvparams.height = (newimgHeight + 40*oneDp) * 3 + 15*oneDp;
        newrvparams.setMargins(DensityUtil.dip2px(getContext(),10),0,DensityUtil.dip2px(getContext(),10)
        ,0);
        newrv.setLayoutParams(newrvparams);

        //
        ViewGroup.LayoutParams dianyivparams = dianyiv.getLayoutParams();
        dianyivparams.width = btnwidth;
        dianyivparams.height = btnwidth;
        dianyiv.setLayoutParams(dianyivparams);
        //
        ViewGroup.LayoutParams donghivparams = donghiv.getLayoutParams();
        donghivparams.width = btnwidth;
        donghivparams.height = btnwidth;
        donghiv.setLayoutParams(donghivparams);

        ViewGroup.LayoutParams zongyivparams = zongyiv.getLayoutParams();
        zongyivparams.width = btnwidth;
        zongyivparams.height = btnwidth;
        zongyiv.setLayoutParams(zongyivparams);

        ViewGroup.LayoutParams pianhivparams = pianhiv.getLayoutParams();
        pianhivparams.width = btnwidth;
        pianhivparams.height = btnwidth;
        pianhiv.setLayoutParams(pianhivparams);

        ViewGroup.LayoutParams fengjivparams = fengjiv.getLayoutParams();
        fengjivparams.width = btnwidth;
        fengjivparams.height = btnwidth;
        fengjiv.setLayoutParams(fengjivparams);
        //
        ViewGroup.LayoutParams meinivparams = meiniv.getLayoutParams();
        meinivparams.width = btnwidth;
        meinivparams.height = btnwidth;
        meiniv.setLayoutParams(meinivparams);

        ViewGroup.LayoutParams youxivparams = youxiv.getLayoutParams();
        youxivparams.width = btnwidth;
        youxivparams.height = btnwidth;
        youxiv.setLayoutParams(youxivparams);

        ViewGroup.LayoutParams qitivparams = qitiv.getLayoutParams();
        qitivparams.width = btnwidth;
        qitivparams.height = btnwidth;
        qitiv.setLayoutParams(qitivparams);

        ViewGroup.LayoutParams btnsline1params = btnsline1.getLayoutParams();
        btnsline1params.width = screenWidth*116/1080;
        btnsline1.setLayoutParams(btnsline1params);

        ViewGroup.LayoutParams btnsline2params = btnsline2.getLayoutParams();
        btnsline2params.width = screenWidth*116/1080;
        btnsline2.setLayoutParams(btnsline2params);

        ViewGroup.LayoutParams btnsline3params = btnsline3.getLayoutParams();
        btnsline3params.width = screenWidth*116/1080;
        btnsline3.setLayoutParams(btnsline3params);

        ViewGroup.LayoutParams btnsline4params = btnsline4.getLayoutParams();
        btnsline4params.width = screenWidth*116/1080;
        btnsline4.setLayoutParams(btnsline4params);

    }

    /**
     * 换一批
     */
    void nextHotMethod(){
        String url = HttpConstants.NextHot_VideosUrl + "&shelvesCurrentPage=1" + "&jinshanyunCurrentPage=1";
        HttpGetUtil get = new HttpGetUtil(handler, url, NEXTHOT_FLAG);
        Thread thread = new Thread(get);
        thread.start();
    }

    @Override
    public void initEvent() {
        super.initEvent();

        final GridLayoutManager mHotGridLayoutManager = new GridLayoutManager(getActivity(),2,GridLayoutManager.VERTICAL,false);//设置为一个2列的纵向网格布局
        mHotGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        });
        //设置布局管理器
        hotrv.setLayoutManager(mHotGridLayoutManager);
//        SpacesItemDecoration decoration = new SpacesItemDecoration(2*oneDp);
        TwoItemDecoration twodecoration = new TwoItemDecoration(1*oneDp);
        hotrv.addItemDecoration(twodecoration);
        //设置为垂直布局，这也是默认的
        //layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置Adapter
        hotrv.setAdapter(hotAdapter);
        //设置增加或删除条目的动画
        hotrv.setItemAnimator(new DefaultItemAnimator());

        hotAdapter.setOnItemClickListener(new HotVideoOnlineAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.e("info", "点击第" + position + "个");
                Intent intent = new Intent();
                intent.setClass(getActivity(), OnlineVideoInfoActivity.class);
                intent.putExtra("videoId",hotVideos.get(position).getId());
                intent.putExtra("authorId",hotVideos.get(position).getUserId());
                startActivity(intent);
            }
        });

        //
        final GridLayoutManager mHotGridLayoutManager2 = new GridLayoutManager(getActivity(),2,GridLayoutManager.VERTICAL,false);//设置为一个2列的纵向网格布局
        mHotGridLayoutManager2.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        });
        //设置布局管理器
        hotrv2.setLayoutManager(mHotGridLayoutManager2);
//        SpacesItemDecoration decoration = new SpacesItemDecoration(2*oneDp);
        TwoItemDecoration twodecoration2 = new TwoItemDecoration(1*oneDp);
        hotrv2.addItemDecoration(twodecoration2);
        //设置为垂直布局，这也是默认的
        //layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置Adapter
        hotrv2.setAdapter(hotAdapter2);
        //设置增加或删除条目的动画
        hotrv2.setItemAnimator(new DefaultItemAnimator());

        hotAdapter2.setOnItemClickListener(new HotVideoOnlineAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.e("info", "点击第" + position + "个");
                Intent intent = new Intent();
                intent.setClass(getActivity(), OnlineVideoInfoActivity.class);
                intent.putExtra("videoId",hotVideos.get(position).getId());
                intent.putExtra("authorId",hotVideos.get(position).getUserId());
                startActivity(intent);
            }
        });


        //精选影片
        final GridLayoutManager mGridLayoutManager=new GridLayoutManager(getActivity(),3,GridLayoutManager.VERTICAL,false);//设置为一个2列的纵向网格布局
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        });
        //设置布局管理器
        newrv.setLayoutManager(mGridLayoutManager);
//        SpacesItemDecoration newdecoration = new SpacesItemDecoration(2*oneDp);
        ThreeItemDecoration newdecoration = new ThreeItemDecoration(1*oneDp);
        newrv.addItemDecoration(newdecoration);
        //设置为垂直布局，这也是默认的
        //layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置Adapter
        newrv.setAdapter(newAdapter);
        //设置增加或删除条目的动画
        newrv.setItemAnimator(new DefaultItemAnimator());
        newAdapter.setOnItemClickListener(new NewRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.e("info", "点击第" + position + "个");
                Intent intent = new Intent();
                intent.setClass(getActivity(), OnlineVideoInfoActivity.class);
                intent.putExtra("videoId",newVideos.get(position).getId());
                intent.putExtra("authorId",newVideos.get(position).getUserId());
                startActivity(intent);
            }
        });
        //
        gallerybanner.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        handler.removeMessages(Viewpager_BANNER_FLAG);
                        currentitem = gallerybanner.getSelectedItemPosition();

                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        handler.sendEmptyMessageDelayed(Viewpager_BANNER_FLAG, 3000);

                        break;
                }
                return false;
            }
        });
        //广告栏
        gallerybanner.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                int nowposition = (position % banners.size());// 算出真正的位置
//                ToastUtil.showToast(getActivity(), "position:" + nowposition, ToastUtil.CENTER);
                    /*rjz 修改 注释掉点击事件，不需要此点击事件 */
                String typeStr = banners.get(nowposition).getType();
                String urlStr = banners.get(nowposition).getUrl();

                //视频
                if(Video_FLAG.equals(typeStr)) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), OnlineVideoInfoActivity.class);
                    intent.putExtra("videoId", urlStr);
                    startActivity(intent);
                //3D冬冬视频
                }else if(ThreeDDVideo_FLAG.equals(typeStr)){
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), OnlineVideoInfoActivity.class);
                    intent.putExtra("videoId", urlStr);
                    startActivity(intent);

                //图片
                }else if(Image_FLAG.equals(typeStr)){
                    boolean isJiaoZhun = SharedPrefsUtil.getValue(getActivity(), com.stereo.util.Constants.JIAOZHUNSUCCESS, false);

                    if(isJiaoZhun == true){
                        Intent intent1 = new Intent();
                        intent1.setClass(getActivity(), OnlinePicInfoActivity.class);
                        intent1.putExtra("picId",urlStr);
                        startActivity(intent1);
                    }else{
                        activeDia(urlStr);
                    }
                }

            }
        });
        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentitem = 21;
                swipeRefreshLayout.setRefreshing(true);
                hotVideos.clear();
                newVideos.clear();
                hotAdapter.notifyDataSetChanged();
                newAdapter.notifyDataSetChanged();

                initFilms();
                initBannerList();
            }
        });
        //更多
        newmoretv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(),AllFilmActivity.class);
                intent.putExtra("allFilmType","home");
                startActivity(intent);

            }
        });
        newmoretag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(),AllFilmActivity.class);
                intent.putExtra("allFilmType","home");
                startActivity(intent);

            }
        });

        dianyiv.setOnClickListener(new MyOnClickListener());
        donghiv.setOnClickListener(new MyOnClickListener());
        zongyiv.setOnClickListener(new MyOnClickListener());
        pianhiv.setOnClickListener(new MyOnClickListener());
        fengjiv.setOnClickListener(new MyOnClickListener());
        meiniv.setOnClickListener(new MyOnClickListener());
        youxiv.setOnClickListener(new MyOnClickListener());
        qitiv.setOnClickListener(new MyOnClickListener());

        //扩大换一批的点击区域
        hottvrela.post(new Runnable() {
            @Override
            public void run() {
                // 构造一个 "矩型" 对象
                Rect delegateArea = new Rect();
                TextView delegate = hotmoretv;
                // Hit rectangle in parent's coordinates
                delegate.getHitRect(delegateArea);

                // 扩大触摸区域矩阵值
                delegateArea.left -= 90;
                delegateArea.top -= 90;
                delegateArea.right += 90;
                delegateArea.bottom += 90;
                /**
                 * 构造扩大后的触摸区域对象
                 * 第一个构造参数表示  矩形面积
                 * 第二个构造参数表示 被扩大的触摸视图对象
                 * <也是本demo最核心用到的类之一>
                 */
                TouchDelegate expandedArea = new TouchDelegate(delegateArea, delegate);

                if(View.class.isInstance(delegate.getParent())){
                    // 设置视图扩大后的触摸区域
                    ((View)delegate.getParent()).setTouchDelegate(expandedArea);
                }
            }
        });

        //换一批
        hotmoretv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextHotMethod();
//                rotateAnim();
            }
        });

        //
        hotFrameLayout.setPersistentDrawingCache(ViewGroup.PERSISTENT_ANIMATION_CACHE);

    }

    /**
     *
     */
    public void rotateAnim() {
        Animation anim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setFillAfter(true); // 设置保持动画最后的状态
        anim.setDuration(1000); // 设置动画时间
        anim.setInterpolator(new AccelerateInterpolator()); // 设置插入器
        hotrv.startAnimation(anim);
    }
//switch (view.getId()){
//        case R.id.videorecommend_btnsiv1:
//            Intent intent1 = new Intent();
//            intent1.setClass(getActivity(), VideoTypeActivity.class);
//            intent1.putExtra("videoType",getString(R.string.word_film));
//            startActivity(intent1);
//
//            break;
//        case R.id.videorecommend_btnsiv2:
//            Intent intent2 = new Intent();
//            intent2.setClass(getActivity(), VideoTypeActivity.class);
//            intent2.putExtra("videoType",getString(R.string.word_donghua));
//            startActivity(intent2);
//
//            break;
//        case R.id.videorecommend_btnsiv3:
//            Intent intent3 = new Intent();
//            intent3.setClass(getActivity(), VideoTypeActivity.class);
//            intent3.putExtra("videoType",getString(R.string.word_zongyi));
//            startActivity(intent3);
//
//            break;
//        case R.id.videorecommend_btnsiv4:
//            Intent intent4 = new Intent();
//            intent4.setClass(getActivity(), VideoTypeActivity.class);
//            intent4.putExtra("videoType",getString(R.string.word_pianhua));
//            startActivity(intent4);
//
//            break;
//        case R.id.videorecommend_btnsiv5:
//            Intent intent5 = new Intent();
//            intent5.setClass(getActivity(), VideoTypeActivity.class);
//            intent5.putExtra("videoType",getString(R.string.word_fengjing));
//            startActivity(intent5);
//
//            break;
//        case R.id.videorecommend_btnsiv6:
//            Intent intent6 = new Intent();
//            intent6.setClass(getActivity(), VideoTypeActivity.class);
//            intent6.putExtra("videoType",getString(R.string.word_meinv));
//            startActivity(intent6);
//
//            break;
//        case R.id.videorecommend_btnsiv7:
//            Intent intent7 = new Intent();
//            intent7.setClass(getActivity(), VideoTypeActivity.class);
//            intent7.putExtra("videoType",getString(R.string.word_youxi));
//            startActivity(intent7);
//
//            break;
//        case R.id.videorecommend_btnsiv8:
//            Intent intent8 = new Intent();
//            intent8.setClass(getActivity(), VideoTypeActivity.class);
//            intent8.putExtra("videoType",getString(R.string.word_qita));
//            startActivity(intent8);
//
//            break;

//    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.big_movie_iv:
                Intent intent1 = new Intent();
                intent1.setClass(getActivity(), VideoTypeActivity.class);
                intent1.putExtra("videoType",getString(R.string.word_movie));
                startActivity(intent1);
                break;
            case R.id.new_movie_iv:
                Intent intentNew = new Intent();
                intentNew.setClass(getActivity(),VideoTypeActivity.class);
                intentNew.putExtra("videoType",getString(R.string.word_new_studio));
                startActivity(intentNew);
                break;
            case R.id.fashion_show_iv:
                Intent intentShow = new Intent();
                intentShow.setClass(getActivity(),VideoTypeActivity.class);
                intentShow.putExtra("videoType",getString(R.string.word_fashion_show));
                startActivity(intentShow);
                break;
            case R.id.short_video_iv:
                Intent intentShort = new Intent();
                intentShort.setClass(getActivity(),VideoTypeActivity.class);
                intentShort.putExtra("videoType",getString(R.string.word_short_movie));
                startActivity(intentShort);
                break;
            case R.id.recommend_video_iv:
                Intent intentRecommend = new Intent();
                intentRecommend.setClass(getActivity(), VideoTypeActivity.class);
                intentRecommend.putExtra("videoType", getString(R.string.word_recommend_video));
                startActivity(intentRecommend);
                break;
            case R.id.next_more:
//                videoRecommendSl.scrollTo(0,1920);
                scrollToPosition(0, DensityUtil.dip2px(getContext(), 534));
                break;

        }
    }
    /*rjz add 点击*/
    public void scrollToPosition(int x,int y) {

        ObjectAnimator yTranslate = ObjectAnimator.ofInt(videoRecommendSl, "scrollY", y);
        AnimatorSet animators = new AnimatorSet();
        animators.setDuration(500);
        animators.playTogether(yTranslate);
        animators.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationRepeat(Animator arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationCancel(Animator arg0) {
                // TODO Auto-generated method stub

            }
        });
        animators.start();
    }

    class MyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.videorecommend_btnsiv1:
                    Intent intent1 = new Intent();
                    intent1.setClass(getActivity(), VideoTypeActivity.class);
                    intent1.putExtra("videoType",getString(R.string.word_film));
                    startActivity(intent1);

                    break;
                case R.id.videorecommend_btnsiv2:
                    Intent intent2 = new Intent();
                    intent2.setClass(getActivity(), VideoTypeActivity.class);
                    intent2.putExtra("videoType",getString(R.string.word_donghua));
                    startActivity(intent2);

                    break;
                case R.id.videorecommend_btnsiv3:
                    Intent intent3 = new Intent();
                    intent3.setClass(getActivity(), VideoTypeActivity.class);
                    intent3.putExtra("videoType",getString(R.string.word_zongyi));
                    startActivity(intent3);

                    break;
                case R.id.videorecommend_btnsiv4:
                    Intent intent4 = new Intent();
                    intent4.setClass(getActivity(), VideoTypeActivity.class);
                    intent4.putExtra("videoType",getString(R.string.word_pianhua));
                    startActivity(intent4);

                    break;
                case R.id.videorecommend_btnsiv5:
                    Intent intent5 = new Intent();
                    intent5.setClass(getActivity(), VideoTypeActivity.class);
                    intent5.putExtra("videoType",getString(R.string.word_fengjing));
                    startActivity(intent5);

                    break;
                case R.id.videorecommend_btnsiv6:
                    Intent intent6 = new Intent();
                    intent6.setClass(getActivity(), VideoTypeActivity.class);
                    intent6.putExtra("videoType",getString(R.string.word_meinv));
                    startActivity(intent6);

                    break;
                case R.id.videorecommend_btnsiv7:
                    Intent intent7 = new Intent();
                    intent7.setClass(getActivity(), VideoTypeActivity.class);
                    intent7.putExtra("videoType",getString(R.string.word_youxi));
                    startActivity(intent7);

                    break;
                case R.id.videorecommend_btnsiv8:
                    Intent intent8 = new Intent();
                    intent8.setClass(getActivity(), VideoTypeActivity.class);
                    intent8.putExtra("videoType",getString(R.string.word_qita));
                    startActivity(intent8);

                    break;

            }
        }
    }


    /**
     * 激活对话框
     */
    private void activeDia(final String id){
        View diaView = View.inflate(getActivity(), R.layout.notice_dialog, null);
        activeDialog = new Dialog(getActivity(), R.style.dialog);
        activeDialog.setContentView(diaView);
        activeDialog.setCanceledOnTouchOutside(false);
        activeDialog.setCancelable(false);

        Button setafterbtn = (Button) activeDialog.findViewById(R.id.notice_yesbtn);
        Button tonowbtn = (Button) activeDialog.findViewById(R.id.notice_canclebtn);
        TextView titletv = (TextView) activeDialog.findViewById(R.id.notice_tv1);
        TextView contenttv = (TextView) activeDialog.findViewById(R.id.notice_tv2);

        titletv.setVisibility(View.GONE);

        contenttv.setText(R.string.activebefore);
        setafterbtn.setText(R.string.setafter);
        tonowbtn.setText(R.string.nowactive);

        setafterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activeDialog.dismiss();

                Intent intent = new Intent();
                intent.setClass(getActivity(), OnlinePicInfoActivity.class);
                intent.putExtra("picId",id);
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
                 * 去校准
                 */
                phoneNum = SharedPrefsUtil.getValue(getActivity(), Constants.Phone_FLAG, Constants.Phone_default);
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
                    progressDid();
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
                            phoneNum = SharedPrefsUtil.getValue(getActivity(), Constants.Phone_FLAG, Constants.Phone_default);
                            JSONObject personObject = null;
                            try {
                                personObject = new JSONObject();
                                personObject.put("phoneNum", phoneNum);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ZhugeSDK.getInstance().track(getActivity(),"激活结果总次数",personObject);

                            switch (returnCode){
                                case 1:
//                                    ToastUtil.showToast(getActivity(), "激活成功,请开始眼球追踪校准", ToastUtil.BOTTOM);

                                    Intent intent = new Intent();
                                    intent.putExtra("activeSuccess",true);
                                    intent.setClass(getActivity(), AngleActivity.class);
                                    startActivity(intent);

                                    /**
                                     * 激活成功
                                     */
                                    ZhugeSDK.getInstance().track(getActivity(),"激活成功",personObject);
                                    ZhugeSDK.getInstance().track(getActivity(),"进入校准界面总次数",personObject);

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

    /**
     *
     */
//    private class PageAdapter extends PagerAdapter {
//        private List<View> mList;
//
//        private AsyncImageLoader asyncImageLoader;
//
//        public PageAdapter(List<View> list) {
//            mList = list;
//            asyncImageLoader = new AsyncImageLoader();
//        }
//        /**
//         * Return the number of views available.
//         */
//        @Override
//        public int getCount() {
//            return mList.size();
//        }
//        /**
//         */
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            container.removeView(mList.get(position));
//
//        }
//
//        @Override
//        public boolean isViewFromObject(View arg0, Object arg1) {
//            return arg0==arg1;
//        }
//        /**
//         * Create the page for the given position.
//         */
//        @Override
//        public Object instantiateItem(final ViewGroup container, final int position) {
//            Drawable cachedImage = asyncImageLoader.loadDrawable(
//                    banners.get(position).getImgurl(), new AsyncImageLoader.ImageCallback() {
//
//                        public void imageLoaded(Drawable imageDrawable,
//                                                String imageUrl) {
//
//                            View view = mList.get(position);
//                            image = ((ImageView) view.findViewById(R.id.banneritem_image));
////                            image.setBackground(imageDrawable);
//                            if (imageDrawable != null) {
//                                image.setImageDrawable(imageDrawable);
//                            }
//
//                            tv = (TextView) view.findViewById(R.id.banneritem_textview);
//
//                            container.removeView(mList.get(position));
//                            container.addView(mList.get(position));
//
//                        }
//                    });
//
//            View view = mList.get(position);
//            image = ((ImageView) view.findViewById(R.id.banneritem_image));
////            image.setBackground(cachedImage);
//
//            if (cachedImage != null) {
//                image.setImageDrawable(cachedImage);
//
//            } else {
//                image.setImageResource(R.mipmap.defaultloading_fourthreeimg);
//
//            }
//
//
//            tv = (TextView) view.findViewById(R.id.banneritem_textview);
//
//            container.removeView(mList.get(position));
//            container.addView(mList.get(position));
//
//            image.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Log.e("info","viewpager image......position=" + position);
//
//                    String typeStr = banners.get(position).getType();
//                    String urlStr = banners.get(position).getUrl();
//
//                    //视频
//                    if(Video_FLAG.equals(typeStr)) {
//                        Intent intent = new Intent();
//                        intent.setClass(getActivity(), OnlineVideoInfoActivity.class);
//                        intent.putExtra("videoId", urlStr);
//                        startActivity(intent);
//                    //3D冬冬视频
//                    }else if(ThreeDDVideo_FLAG.equals(typeStr)){
//                        Intent intent = new Intent();
//                        intent.setClass(getActivity(), OnlineVideoInfoActivity.class);
//                        intent.putExtra("videoId", urlStr);
//                        startActivity(intent);
//
//                    //图片
//                    }else if(Image_FLAG.equals(typeStr)){
//                        boolean isJiaoZhun = SharedPrefsUtil.getValue(getActivity(), com.stereo.util.Constants.JIAOZHUNSUCCESS, false);
//
//                        if(isJiaoZhun == true){
//                            Intent intent1 = new Intent();
//                            intent1.setClass(getActivity(), OnlinePicInfoActivity.class);
//                            intent1.putExtra("picId",urlStr);
//                            startActivity(intent1);
//                        }else{
//                            activeDia(urlStr);
//                        }
//                    }
//                }
//            });
//
//            return mList.get(position);
//        }
//    }

}
