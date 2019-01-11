package com.unbounded.video.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.unbounded.video.BaseActivity;
import com.unbounded.video.bean.AttentionBean;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.constants.HttpConstants;
import com.unbounded.video.utils.HttpGetUtil;
import com.unbounded.video.utils.SharedPrefsUtil;
import com.unbounded.video.utils.ToastUtil;
import com.unbounded.video.utils.imageLoad.GlideLogic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjf on 2017/7/20 0020.
 */

public class AttentionActivity extends BaseActivity {
    public final static int DeleteAttend_FLAG = 1;
    int currentPage=1,sumPage,deleteposition=-1;
    String selectStart;
    View footer;
    ListView lv;
    List<AttentionBean> attentions = new ArrayList<AttentionBean>();
    AttentionAdapter adapter;
    LinearLayout spacelinear;

    private int totalItem;
    private int lastItem;
    private boolean isLoading = false;

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //关注列表
                case Constants.INTERNET_SUCCESS_FLAG:
                    String attentionlistvalue = msg.obj.toString();
//                    Log.e("info","attentionlistvalue="+attentionlistvalue);

                    if(!(TextUtils.isEmpty(attentionlistvalue))){
                        try {
                            JSONObject attentionjo = new JSONObject(attentionlistvalue);
                            JSONObject page = attentionjo.getJSONObject("page");

                            sumPage = Integer.valueOf(page.getString("sumPage"));
                            selectStart = page.getString("selectEnd");

                            JSONArray data = attentionjo.getJSONArray("data");
                            if(data != null && data.length() > 0){
//                                Log.e("info","data.length()="+data.length());

                                for(int i=0;i<data.length();i++){
                                    JSONObject jo = data.getJSONObject(i);

                                    String id = jo.getString("id");
                                    String userimg = jo.getString("userimg");
                                    String username = jo.getString("username");

                                    AttentionBean attentionBean = new AttentionBean(id, userimg, username);
                                    attentions.add(attentionBean);

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

//                        Log.e("info","attentions.size="+attentions.size());

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
                        emptyViewMethod();
                        progressDiddismiss();


                    }

                    break;
                //取消关注
                case DeleteAttend_FLAG:
                    String deleteattentvalue = msg.obj.toString();
//                    Log.e("info","daleteattentvalue="+daleteattentvalue);
                    if(!(TextUtils.isEmpty(deleteattentvalue))){
                        try {
                            JSONObject deleteattentjo = new JSONObject(deleteattentvalue);

                            String result = deleteattentjo.getString("result");
                            if(Constants.SUCCESS_STR.equals(result)){
                                if(deleteposition != -1){
                                    attentions.remove(deleteposition);
                                }

                                ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_worksuccess, ToastUtil.BOTTOM);

                                if(attentions.size() == 0){
                                    setEmptyViewVisible(true);
                                }

                            }else{
                                ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_workerror, ToastUtil.BOTTOM);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    adapter.notifyDataSetChanged();
                    progressDiddismiss();

                    break;

                case Constants.INTERNET_ERROR_FLAG:
                    ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.internet_error, ToastUtil.CENTER);
                    adapter.notifyDataSetChanged();
                    emptyViewMethod();
                    progressDiddismiss();

                    break;

            }
        }
    };

    @Override
    protected int getContentView() {
        return com.unbounded.video.R.layout.activity_playerhistory;
    }

    @Override
    public void init(Intent intent) {
        super.init(intent);

        setTitleName(getString(com.unbounded.video.R.string.Word_Attention));

        userId = SharedPrefsUtil.getValue(getApplicationContext(), Constants.UserId_FLAG, "");

    }

    /**
     * 数据为空
     */
    private void emptyViewMethod(){
        if(currentPage == 1 && attentions.size() == 0){
            ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.havenodata, ToastUtil.CENTER);
            setEmptyViewVisible(true);
        }else {
            setEmptyViewVisible(false);
        }
    }

    /**
     * 关注列表
     */
    private void attentionList(){
        String url;
        if(!(TextUtils.isEmpty(selectStart)) && currentPage != 1){
            url = HttpConstants.Followlist_Url + "?userId="+userId+"&currentPage="+currentPage + "&selectStart="+selectStart;
        }else {
            url = HttpConstants.Followlist_Url + "?userId="+userId+"&currentPage="+currentPage;
        }

        HttpGetUtil get = new HttpGetUtil(handler, url, Constants.INTERNET_SUCCESS_FLAG);
        Thread thread = new Thread(get);
        thread.start();
    }

    @Override
    public void initDatas() {
        super.initDatas();

        progressDid();
        if(attentions != null){
            attentions.clear();
        }
        attentionList();

    }

    @Override
    public void initView() {
        super.initView();

        adapter = new AttentionAdapter(getApplicationContext(), attentions, oneDp);
        lv = (ListView) findViewById(com.unbounded.video.R.id.playerhistory_lv);
        footer = LayoutInflater.from(getApplicationContext()).inflate(com.unbounded.video.R.layout.load_more_footer,null ,false);
        spacelinear = (LinearLayout) findViewById(com.unbounded.video.R.id.history_spacelinear);

        lv.setAdapter(adapter);

        spacelinear.setVisibility(View.GONE);
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

                            attentionList();
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
    }


    /**
     * adapter适配器
     */
    class AttentionAdapter extends BaseAdapter {
        Context context;
        List<AttentionBean> itemlist;
        int oneDp;

        public AttentionAdapter(Context context, List<AttentionBean> itemlist, int oneDp) {
            this.context = context;
            this.itemlist = itemlist;
            this.oneDp = oneDp;
        }

        @Override
        public int getCount() {
            return itemlist.size();
        }

        @Override
        public Object getItem(int i) {
            return itemlist.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if(view == null){
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(context).inflate(com.unbounded.video.R.layout.attention_item, null);
                viewHolder.headimg = (ImageView)view.findViewById(com.unbounded.video.R.id.attention_headviewiv);
                viewHolder.nametv = (TextView) view.findViewById(com.unbounded.video.R.id.attention_nicknametv);
                viewHolder.cancletv = (TextView) view.findViewById(com.unbounded.video.R.id.attention_canclebtn);

                view.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)view.getTag();
            }

            viewHolder.nametv.setText(itemlist.get(position).getUsername());
            //头像
            GlideLogic.glideLoadHeadPic(context, itemlist.get(position).getUserimg(), viewHolder.headimg, 64*oneDp, 64*oneDp);

            viewHolder.cancletv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //取消关注
                    cancleAttention(itemlist.get(position).getId());
                    deleteposition = position;
                    progressDid();
                }
            });

            return view;
        }

        class ViewHolder{
            ImageView headimg;
            TextView nametv;
            TextView cancletv;
        }
    }

    /**
     * 取消关注
     */
    void cancleAttention(String id){
        String url = HttpConstants.Deletefollowlist_Url + "?id=" + id;
        HttpGetUtil get = new HttpGetUtil(handler, url, DeleteAttend_FLAG);
        Thread thread = new Thread(get);
        thread.start();

    }

}
