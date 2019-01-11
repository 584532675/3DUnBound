package com.unbounded.video.fragment;

import android.app.Dialog;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stereo.Interaction.InteractionManager;
import com.stereo.angle.AngleActivity;
import com.stereo.util.ActionCallback;
import com.unbounded.video.BaseFragment;
import com.unbounded.video.activity.OnlinePicInfoActivity;
import com.unbounded.video.adapter.PicItemDecoration;
import com.unbounded.video.adapter.RecyclerAdapter;
import com.unbounded.video.bean.OnlinePic;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.constants.HttpConstants;
import com.unbounded.video.utils.HttpGetUtil;
import com.unbounded.video.utils.SharedPrefsUtil;
import com.unbounded.video.utils.ToastUtil;
import com.zhuge.analysis.stat.ZhugeSDK;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjf on 2017/7/18 0018.
 */

public class PicOriginalFragment extends BaseFragment {
    RelativeLayout rela1;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    List<OnlinePic> pics = new ArrayList<OnlinePic>();
    int isFirst = 1;

    String selectStart,phoneNum;
    int currentPage=1,sumPage;
    private boolean isLoading = false;
    private int totalItem;
    private int lastItem;

    Dialog activeDialog;

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.INTERNET_SUCCESS_FLAG:
                    String value = msg.obj.toString();

                    if (!(TextUtils.isEmpty(value))) {
                        try {
                            JSONObject mainjo = new JSONObject(value);

                            JSONObject page = mainjo.getJSONObject("page");
                            sumPage = Integer.valueOf(page.getString("sumPage"));
                            selectStart = page.getString("selectEnd");

                            JSONArray data = mainjo.getJSONArray("data");
                            if(data != null && data.length() > 0){
                                for(int i=0;i<data.length();i++){
                                    JSONObject jo = data.getJSONObject(i);

                                    String thumbnail = jo.getString("thumbnail");
                                    String userimg = jo.getString("userimg");
                                    String opencount = jo.getString("opencount");
                                    String type3d = jo.getString("type3d");
                                    String name = jo.getString("name");
                                    String report = jo.getString("report");
                                    String uploadimg = jo.getString("uploadimg");
                                    String id = jo.getString("id");
                                    String likecount = jo.getString("likecount");
                                    String introduction = jo.getString("introduction");
                                    String username = jo.getString("username");
                                    String uploadtime = jo.getString("uploadtime");

                                    OnlinePic onlinePic = new OnlinePic(thumbnail,userimg,opencount,type3d,name,report,uploadimg,id,likecount,introduction,username,uploadtime);
                                    pics.add(onlinePic);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        isLoading = false;
                        swipeRefreshLayout.setRefreshing(false);
                        recyclerAdapter.notifyDataSetChanged();

                        if(currentPage == 1 && pics.size() == 0){
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
                    recyclerAdapter.notifyDataSetChanged();
                    progressDiddismiss();

                    break;
            }
        }

    };

    @Override
    protected int getContentView() {
        return com.unbounded.video.R.layout.fragment_picrecommend;
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
     * 首次加载
     */
    private void initFirst(){
        progressDid();
        if(pics != null){
            pics.clear();
        }
        initPics();
    }

    @Override
    public void initDatas() {
        super.initDatas();

    }

    /**
     * 初始化影视图片
     */
    private void initPics(){
        String url;
        if(!(TextUtils.isEmpty(selectStart)) && !("null".equals(selectStart)) && currentPage != 1){
            url = HttpConstants.selectInfoByTypeImg_Url + "?type=original" + "&currentPage="+currentPage + "&selectStart="+selectStart;
        }else {
            url = HttpConstants.selectInfoByTypeImg_Url + "?type=original" + "&currentPage=" + currentPage;
        }

        HttpGetUtil get = new HttpGetUtil(handler, url, Constants.INTERNET_SUCCESS_FLAG);
        Thread thread = new Thread(get);
        thread.start();

    }

    @Override
    public void initView(View view) {
        super.initView(view);
        rela1 = (RelativeLayout) view.findViewById(com.unbounded.video.R.id.picrecommend_rela1);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(com.unbounded.video.R.id.picrecommend_swiperefresh);
        recyclerView =(RecyclerView) view.findViewById(com.unbounded.video.R.id.picrecommend_recyclerView);
        rela1.setPadding(0, 0, 0, tabsmrelaHeight);
        recyclerAdapter = new RecyclerAdapter(getActivity() ,pics, null,onelinepicimgWidth);

    }

    @Override
    public void initEvent() {
        super.initEvent();

        //
        final GridLayoutManager mGridLayoutManager=new GridLayoutManager(getActivity(),2,GridLayoutManager.VERTICAL,false);//设置为一个2列的纵向网格布局
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        });
        //设置布局管理器
        recyclerView.setLayoutManager(mGridLayoutManager);
        PicItemDecoration decoration = new PicItemDecoration(1*oneDp);
        recyclerView.addItemDecoration(decoration);
        //设置为垂直布局，这也是默认的
        //layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置Adapter
        recyclerView.setAdapter(recyclerAdapter);
        //设置增加或删除条目的动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.e("info", "点击第" + position + "个");
                boolean isJiaoZhun = SharedPrefsUtil.getValue(getActivity(), com.stereo.util.Constants.JIAOZHUNSUCCESS, false);

                if(isJiaoZhun == true){
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), OnlinePicInfoActivity.class);
                    intent.putExtra("picId",pics.get(position).getId());
                    startActivity(intent);

                }else{
                    activeDia(pics.get(position).getId());
                }
            }
        });

        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isLoading = true;
                currentPage = 1;
                swipeRefreshLayout.setRefreshing(true);
                pics.clear();
                recyclerAdapter.notifyDataSetChanged();

                initPics();
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
                            initPics();
                        }else {
                            if(currentPage > 0){
                                isLoading = false;
                                ToastUtil.showToast(getActivity(), com.unbounded.video.R.string.not_have_more, ToastUtil.BOTTOM);
                            }
                        }
                    }
                }

            }

        });
    }

    /**
     * 激活对话框
     */
    private void activeDia(final String id){
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
//                                    ToastUtil.showToast(getActivity(), "激活成功,请开始眼球追踪校准",ToastUtil.BOTTOM);

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

}
