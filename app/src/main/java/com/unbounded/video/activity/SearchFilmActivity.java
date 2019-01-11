package com.unbounded.video.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.unbounded.video.BaseActivity;
import com.unbounded.video.R;
import com.unbounded.video.adapter.SearchFilmAdapter;
import com.unbounded.video.bean.OnlineVideo;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.constants.HttpConstants;
import com.unbounded.video.utils.HttpPostUtil;
import com.unbounded.video.utils.SharedPrefsUtil;
import com.unbounded.video.utils.ToastUtil;
import com.unbounded.video.view.banner.containsEmojiEditText.ContainsEmojiEditText;
import com.zhuge.analysis.stat.ZhugeSDK;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjf on 2017/7/21 0021.
 * 搜索界面
 */

public class SearchFilmActivity extends BaseActivity{
    RelativeLayout titlerela;
    Button backbtn;
    ImageView seekiv;
    ContainsEmojiEditText seekedt;
    ListView lv;
    String keyword,phoneNum;
    List<String> keyList = new ArrayList<String>();
    List<String> valueList = new ArrayList<String>();
    List<OnlineVideo> onlineVideos = new ArrayList<OnlineVideo>();

    SearchFilmAdapter adapter;
    View footer;
    int currentPage = 1,sumPage;
    String selectStart;

    private int totalItem;
    private int lastItem;
    private boolean isLoading = false;

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.INTERNET_SUCCESS_FLAG:
                    String value = msg.obj.toString();

                    Log.e("info","搜索 value="+value);

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
                                    String timeLong = jo.getString("timeLong");
                                    String opencount = jo.getString("num");
                                    String name = jo.getString("name");
                                    String uploadimg = jo.getString("uploadImg");
                                    String id = jo.getString("id");
                                    String userName = jo.getString("userName");
                                    String userId = jo.getString("userId");
//                                    String introduction = jo.getString("introduction");
                                    String genre = jo.getString("genre");

                                    OnlineVideo onlineVideo = new OnlineVideo(userImg, timeLong, opencount, name, uploadimg, id, userName, userId, genre);
                                    onlineVideos.add(onlineVideo);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(currentPage == 1 && onlineVideos.size() == 0){
                            ToastUtil.showToast(getApplicationContext(), R.string.searchhavenodata, ToastUtil.CENTER);
                            setSearchEmptyViewVisible(true);

                        }else {
                            setSearchEmptyViewVisible(false);
                        }

                        isLoading = false;

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

                        progressDiddismiss();
                    }

                    break;

                case Constants.INTERNET_ERROR_FLAG:
                    ToastUtil.showToast(getApplicationContext(), R.string.internet_error, ToastUtil.CENTER);
                    adapter.notifyDataSetChanged();
                    progressDiddismiss();

                    break;

            }
        }
    };

    @Override
    protected int getContentView() {
        return R.layout.activity_filmsearch;
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

        adapter = new SearchFilmAdapter(getApplicationContext(),onlineVideos, screenWidth);
        footer = LayoutInflater.from(getApplicationContext()).inflate(R.layout.load_more_footer,null ,false);
        titlerela = (RelativeLayout) findViewById(R.id.fimlsearch_titlerela);
        backbtn = (Button) findViewById(R.id.fimlsearch_backbtn);
        seekiv = (ImageView) findViewById(R.id.fimlsearch_searchflag);
        seekedt = (ContainsEmojiEditText) findViewById(R.id.fimlsearch_searchedt);
        lv = (ListView) findViewById(R.id.fimlsearch_lv);

//        lv.addFooterView(footer);
        footer.setVisibility(View.GONE);
        lv.setAdapter(adapter);

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
        //搜索输入框焦点
        seekedt.setOnFocusChangeListener(new android.view.View.
                OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    Log.e("info","得到焦点");
                } else {
                    // 此处为失去焦点时的处理内容
                    Log.e("info","失去焦点");
                }
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
                            footer.setVisibility(View.VISIBLE);
                            currentPage = currentPage + 1;

                            if(adapter != null){
                                adapter.notifyDataSetChanged();
                            }

                            searchMethod();
                        }else {
                            if(currentPage > 0){
                                isLoading = false;
                                footer.setVisibility(View.GONE);
                                ToastUtil.showToast(getApplicationContext(), R.string.not_have_more, ToastUtil.BOTTOM);
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

        //
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), OnlineVideoInfoActivity.class);
                intent.putExtra("videoId",onlineVideos.get(i).getId());
                intent.putExtra("authorId",onlineVideos.get(i).getUserId());
                startActivity(intent);
            }
        });

        backbtn.setOnClickListener(this);
        seekiv.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.fimlsearch_backbtn:
                hideInputMode();
                finish();
                break;
            case R.id.fimlsearch_searchflag:
                keyword = seekedt.getText().toString().trim();

                /**
                 * 搜索次数
                 */
                phoneNum = SharedPrefsUtil.getValue(getApplicationContext(), Constants.Phone_FLAG, Constants.Phone_default);
                JSONObject personObject = null;
                try {
                    personObject = new JSONObject();
                    personObject.put("phoneNum", phoneNum);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ZhugeSDK.getInstance().track(getApplicationContext(),"搜索视频次数",personObject);

                if(TextUtils.isEmpty(keyword)){
                    ToastUtil.showToast(getApplicationContext(), R.string.Word_searchhint, ToastUtil.CENTER);
                    return;
                }
//                    setEmptyViewVisible(false);
                hideInputMode();
                isLoading = true;
                currentPage = 1;
                selectStart = "";
                onlineVideos.clear();
                footer.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                progressDidresid(R.string.Word_searching);

                searchMethod();



                break;

        }
    }

    /**
     * 搜索
     */
    private void searchMethod(){
        keyList.clear();
        valueList.clear();

        keyList.add("comeFrom");
//        keyList.add("version");
        keyList.add("currentPage");
        keyList.add("name");

        valueList.add("gm");
//        valueList.add("");
        valueList.add(currentPage+"");
        valueList.add(keyword);

        if(!(TextUtils.isEmpty(selectStart)) && currentPage != 1){
            keyList.add("selectStart");
            valueList.add(selectStart);
        }

        String seekurl = HttpConstants.Select_VideosUrl;
        HttpPostUtil post = new HttpPostUtil(handler, seekurl, keyList, valueList, Constants.INTERNET_SUCCESS_FLAG);
        Thread thread = new Thread(post);
        thread.start();

    }

}
