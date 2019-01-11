package com.unbounded.video.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stereo.Interaction.InteractionManager;
import com.stereo.angle.AngleActivity;
import com.stereo.util.ActionCallback;
import com.unbounded.video.BaseActivity;
import com.unbounded.video.adapter.PicItemDecoration;
import com.unbounded.video.adapter.RecyclerAdapter;
import com.unbounded.video.bean.OnlinePic;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.constants.HttpConstants;
import com.unbounded.video.utils.HttpPostUtil;
import com.unbounded.video.utils.SharedPrefsUtil;
import com.unbounded.video.utils.ToastUtil;
import com.unbounded.video.view.banner.containsEmojiEditText.ContainsEmojiEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zhuge.analysis.stat.ZhugeSDK;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjf on 2017/7/21 0021.
 */

public class SearchPicActivity extends BaseActivity{
    RelativeLayout titlerela;
    Button backbtn;
    ImageView seekiv;
    ContainsEmojiEditText seekedt;
    RelativeLayout recyclerela;
    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    String keyword;
    List<String> keyList = new ArrayList<String>();
    List<String> valueList = new ArrayList<String>();
    List<OnlinePic> onlinePics = new ArrayList<OnlinePic>();

    Dialog activeDialog;
    String selectStart,phoneNum;
    int currentPage=1,sumPage;
    private boolean isLoading = false;
    private int totalItem;
    private int lastItem;

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

                                    OnlinePic onlinePic = new OnlinePic(thumbnail, userimg,opencount,type3d,name,report,uploadimg,id,likecount,introduction,username,uploadtime);
                                    onlinePics.add(onlinePic);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        isLoading = false;
                        recyclerAdapter.notifyDataSetChanged();

                        if(currentPage == 1 && onlinePics.size() == 0){
                            ToastUtil.showToast(SearchPicActivity.this, com.unbounded.video.R.string.searchhavenodata, ToastUtil.CENTER);
                            setSearchEmptyViewVisible(true);

                        }else {
                            setSearchEmptyViewVisible(false);
                        }

                        progressDiddismiss();

                    }

                    break;
                case Constants.INTERNET_ERROR_FLAG:
                    ToastUtil.showToast(SearchPicActivity.this, com.unbounded.video.R.string.internet_error, ToastUtil.CENTER);
                    recyclerAdapter.notifyDataSetChanged();
                    progressDiddismiss();

                    break;
            }
        }

    };

    @Override
    protected int getContentView() {
        return com.unbounded.video.R.layout.activity_picsearch;
    }

    @Override
    public void init(Intent intent) {
        super.init(intent);

        setTitleViewVisible(false);
        setSearchEmptyViewVisible(true);
    }

    @Override
    public void initView() {
        super.initView();

        titlerela = (RelativeLayout) findViewById(com.unbounded.video.R.id.fimlsearch_titlerela);
        backbtn = (Button) findViewById(com.unbounded.video.R.id.fimlsearch_backbtn);
        seekiv = (ImageView) findViewById(com.unbounded.video.R.id.fimlsearch_searchflag);
        seekedt = (ContainsEmojiEditText) findViewById(com.unbounded.video.R.id.fimlsearch_searchedt);
        recyclerela = (RelativeLayout) findViewById(com.unbounded.video.R.id.picsearch_recyclerrela);
        recyclerView = (RecyclerView) findViewById(com.unbounded.video.R.id.picsearch_recyclerView);
        recyclerAdapter = new RecyclerAdapter(SearchPicActivity.this ,onlinePics, null,onelinepicimgWidth);

        recyclerela.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);

    }

    @Override
    public void initParams() {
        super.initParams();

        ViewGroup.LayoutParams params = titlerela.getLayoutParams();
        params.height = screenHeight * 9/ 121;
        titlerela.setLayoutParams(params);
    }

    @Override
    public void initEvent() {
        super.initEvent();

        //
        final GridLayoutManager mGridLayoutManager=new GridLayoutManager(SearchPicActivity.this,2,GridLayoutManager.VERTICAL,false);//设置为一个2列的纵向网格布局
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        });
        //设置布局管理器
        recyclerView.setLayoutManager(mGridLayoutManager);
//        SpacesItemDecoration decoration = new SpacesItemDecoration(1*oneDp);
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

                boolean isJiaoZhun = SharedPrefsUtil.getValue(SearchPicActivity.this, com.stereo.util.Constants.JIAOZHUNSUCCESS, false);

                if(isJiaoZhun == true){
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), OnlinePicInfoActivity.class);
                    intent.putExtra("picId",onlinePics.get(position).getId());
                    startActivity(intent);
                }else{
                    activeDia(onlinePics.get(position).getId());
                }


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
                            searchMethod();

                        }else {
                            if(currentPage > 0){
                                isLoading = false;
                                ToastUtil.showToast(SearchPicActivity.this, com.unbounded.video.R.string.not_have_more, ToastUtil.BOTTOM);
                            }
                        }
                    }
                }

            }
        });

        backbtn.setOnClickListener(this);
        seekiv.setOnClickListener(this);
    }

    /**
     * 激活对话框
     */
    private void activeDia(final String id){
        View diaView = View.inflate(SearchPicActivity.this, com.unbounded.video.R.layout.notice_dialog, null);
        activeDialog = new Dialog(SearchPicActivity.this, com.unbounded.video.R.style.dialog);
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
                intent.setClass(SearchPicActivity.this, OnlinePicInfoActivity.class);
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
                intent.setClass(getApplicationContext(), AngleActivity.class);
                startActivity(intent);

                /**
                 * 去校准
                 */
                phoneNum = SharedPrefsUtil.getValue(SearchPicActivity.this, Constants.Phone_FLAG, Constants.Phone_default);
                JSONObject personObject = null;
                try {
                    personObject = new JSONObject();
                    personObject.put("phoneNum", phoneNum);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ZhugeSDK.getInstance().track(SearchPicActivity.this,"去校准", personObject);
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
                    InteractionManager.getInstance(SearchPicActivity.this, new ActionCallback() {
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
                            switch (returnCode){
                                case 1:
//                                    ToastUtil.showToast(SearchPicActivity.this, "激活成功,请开始眼球追踪校准",ToastUtil.BOTTOM);

                                    Intent intent = new Intent();
                                    intent.putExtra("activeSuccess",true);
                                    intent.setClass(SearchPicActivity.this, AngleActivity.class);
                                    startActivity(intent);
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

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case com.unbounded.video.R.id.fimlsearch_backbtn:
                hideInputMode();
                finish();
                break;
            case com.unbounded.video.R.id.fimlsearch_searchflag:
                keyword = seekedt.getText().toString().trim();
                hideInputMode();

                if(TextUtils.isEmpty(keyword)){
                    ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_searchhint, ToastUtil.CENTER);
                    return;
                }

                isLoading = true;
                currentPage = 1;
                selectStart = "";
                onlinePics.clear();
                recyclerAdapter.notifyDataSetChanged();
                progressDidresid(com.unbounded.video.R.string.Word_searching);

                searchMethod();

                break;

        }
    }

    /**
     * 搜索图片
     */
    private void searchMethod(){
        keyList.clear();
        valueList.clear();

        keyList.add("currentPage");
        keyList.add("name");
        valueList.add(currentPage+"");
        valueList.add(keyword);

        if(!(TextUtils.isEmpty(selectStart)) && currentPage != 1){
            keyList.add("selectStart");
            valueList.add(selectStart);
        }

        String seekurl = HttpConstants.selectByNameImg_Url;
        HttpPostUtil post = new HttpPostUtil(handler, seekurl, keyList, valueList, Constants.INTERNET_SUCCESS_FLAG);
        Thread thread = new Thread(post);
        thread.start();

    }

}
