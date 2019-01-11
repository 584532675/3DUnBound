package com.unbounded.video.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.unbounded.video.BaseActivity;
import com.unbounded.video.R;
import com.unbounded.video.bean.VideoHistory;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.utils.JsonUtil;
import com.unbounded.video.utils.SharedPrefsUtil;
import com.unbounded.video.utils.ToastUtil;
import com.unbounded.video.utils.imageLoad.GlideLogic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zjf on 2017/7/18 0018.
 */

public class PlayerHistoryActivity extends BaseActivity {
    LinearLayout btnslinear,spacelinear;
    Button deletebtn,allselectbtn;
    View footer;
    ListView lv;
    String historyJson;
    VideoHistoryAdapter adapter;
    List<VideoHistory> list = new ArrayList<VideoHistory>();

    Dialog deleteDia;

    @Override
    protected int getContentView() {
        return R.layout.activity_playerhistory;
    }

    @Override
    public void init(Intent intent) {
        super.init(intent);

        setTitleName(getResources().getString(R.string.Word_history));

    }

    @Override
    public void initView() {
        super.initView();

        footer = LayoutInflater.from(PlayerHistoryActivity.this).inflate(R.layout.transparent_footer,null ,false);
        adapter = new VideoHistoryAdapter(PlayerHistoryActivity.this);
        btnslinear = (LinearLayout) findViewById(R.id.history_btnslinear);
        deletebtn = (Button) findViewById(R.id.history_deletebtn);
        allselectbtn = (Button) findViewById(R.id.history_allselectbtn);
        lv = (ListView) findViewById(R.id.playerhistory_lv);
        spacelinear = (LinearLayout) findViewById(R.id.history_spacelinear);

        lv.addFooterView(footer);
        lv.setAdapter(adapter);

        spacelinear.setVisibility(View.GONE);
    }

    @Override
    public void initEvent() {
        super.initEvent();

        deletebtn.setOnClickListener(this);
        allselectbtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.history_deletebtn:
                for(int a=0;a<list.size();a++){
                    if(list.get(a).getIsselect() == VideoHistory.Select){
                        deleteDialog();

                        break;
                    }else if(a == list.size() - 1){
                        ToastUtil.showToast(getApplicationContext(), R.string.Word_selectdelete, ToastUtil.CENTER);

                    }
                }

                break;

            case R.id.history_allselectbtn:
                if(getString(R.string.Word_allselect).equals(allselectbtn.getText())){
                    for(int i=0;i<list.size();i++){
                        list.get(i).setIsselect(VideoHistory.Select);
                    }
                    allselectbtn.setText(getString(R.string.Word_allunselect));
                }else {
                    for(int i=0;i<list.size();i++){
                        list.get(i).setIsselect(VideoHistory.UnSelect);
                    }
                    allselectbtn.setText(getString(R.string.Word_allselect));
                }

                adapter.notifyDataSetChanged();

                break;

        }
    }

    /**
     * 删除询问对话框
     */
    private void deleteDialog(){
        View diaView = View.inflate(PlayerHistoryActivity.this, R.layout.notice_dialog, null);
        deleteDia = new Dialog(PlayerHistoryActivity.this, R.style.dialog);
        deleteDia.setContentView(diaView);
//        deleteDia.setCanceledOnTouchOutside(false);

        TextView titletv = (TextView) deleteDia.findViewById(R.id.notice_tv1);
        TextView noticetv = (TextView) deleteDia.findViewById(R.id.notice_tv2);
        Button yesbtn = (Button) deleteDia.findViewById(R.id.notice_yesbtn);
        Button canclebtn = (Button) deleteDia.findViewById(R.id.notice_canclebtn);

        titletv.setText(getString(R.string.Word_noticetitle));
        noticetv.setText(getString(R.string.Word_askdelete));
        //
        canclebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDia.dismiss();
            }
        });
        //
        yesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteMethod();
                deleteDia.dismiss();
            }
        });

        deleteDia.show();

        WindowManager.LayoutParams params = deleteDia.getWindow().getAttributes();
        params.width = screenWidth * 3 / 4;
        // params.height = 200 ;
        deleteDia.getWindow().setAttributes(params);

    }

    /**
     * 删除方法
     */
    private void deleteMethod(){
        for (Iterator it = list.iterator(); it.hasNext();) {
            VideoHistory videoHistory = (VideoHistory)it.next();
            if(videoHistory.getIsselect() == VideoHistory.Select){
                it.remove();

            }
        }

        adapter.notifyDataSetChanged();

        if(list.size() == 0){
            btnslinear.setVisibility(View.GONE);
            setRightBtnVisible(false);
            setRightBtnTitle(getString(R.string.Word_manager));
            allselectbtn.setText(getString(R.string.Word_allselect));
            canViewFoot(false);

            SharedPrefsUtil.putValue(PlayerHistoryActivity.this, Constants.VIDEOHISTORY_FLAG, "");
            setEmptyViewVisible(true);
            setEmptyTvVisible(true);

        }else {
            btnslinear.setVisibility(View.VISIBLE);
            setRightBtnTitle(getString(R.string.cancle_btn));
            allselectbtn.setText(getString(R.string.Word_allselect));

            if(list.size() > 3){
                canViewFoot(true);
            }else {
                canViewFoot(false);
            }

            historyJson = JsonUtil.objectToJson(list);
            SharedPrefsUtil.putValue(PlayerHistoryActivity.this, Constants.VIDEOHISTORY_FLAG, historyJson);
            setEmptyViewVisible(false);
        }


    }

    @Override
    public void onRightClick(View v) {
        super.onRightClick(v);

        if(list.size() == 0){
            return;
        }

        if(btnslinear.getVisibility() == View.GONE){
            btnslinear.setVisibility(View.VISIBLE);
            setRightBtnTitle(getString(R.string.cancle_btn));

            for(int i=0;i<list.size();i++){
                list.get(i).setIscheckVisible(VideoHistory.CanView);
            }
        }else {
            btnslinear.setVisibility(View.GONE);
            setRightBtnTitle(getString(R.string.Word_manager));

            for(int i=0;i<list.size();i++){
                list.get(i).setIscheckVisible(VideoHistory.UnView);

                list.get(i).setIsselect(VideoHistory.UnSelect);//当隐藏checkbox，都设为不选中
            }

            allselectbtn.setText(getString(R.string.Word_allselect));
        }

        adapter.notifyDataSetChanged();

    }

    @Override
    public void initParams() {
        super.initParams();

        ViewGroup.LayoutParams btnslinearparams = btnslinear.getLayoutParams();
        btnslinearparams.height = screenHeight * 146/1920;
        btnslinear.setLayoutParams(btnslinearparams);
    }

    @Override
    protected void onResume() {
        super.onResume();

        initHistorys();

    }

    /**
     * 初始化历史记录
     */
    private void initHistorys(){
        historyJson = SharedPrefsUtil.getValue(getApplicationContext(), Constants.VIDEOHISTORY_FLAG, "");
        Log.e("info","历史记录="+ historyJson);

        //历史记录为空
        if(TextUtils.isEmpty(historyJson)){
            ToastUtil.showToast(getApplicationContext(), R.string.havenodata, ToastUtil.CENTER);
            setEmptyViewVisible(true);
            setEmptyTvVisible(true);
            setRightBtnVisible(false);
            setRightBtnTitle(getString(R.string.Word_manager));
            canViewFoot(false);

        }else {
            list = JsonUtil.historyJsonToList(historyJson);

            setEmptyViewVisible(false);
            setRightBtnVisible(true);
            setRightBtnTitle(getString(R.string.Word_manager));

            if(list.size() > 3){
                canViewFoot(true);
            }else {
                canViewFoot(false);
            }

        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 隐藏foot
     */
    private void canViewFoot(boolean can){
        if(can == true){
            if(lv.getFooterViewsCount() < 1){
                lv.addFooterView(footer);
                footer.setVisibility(View.VISIBLE);

            }
        }else {
            if(lv.getFooterViewsCount() > 0){
                lv.removeFooterView(footer);
                footer.setVisibility(View.GONE);

            }
        }

    }

    /**
     *  adapter适配器
     */
    class VideoHistoryAdapter extends BaseAdapter {
        private Context context;
        private int imgWidth, imgHeight, headviewWidth;

        public VideoHistoryAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.history_item, null);

                viewHolder.cb = (CheckBox) convertView.findViewById(R.id.historyitem_cb);
                viewHolder.headimgiv = (ImageView) convertView.findViewById(R.id.historyitem_headviewiv);
                viewHolder.playiv = (ImageView) convertView.findViewById(R.id.historyitem_playiv);
                viewHolder.authornametv = (TextView) convertView.findViewById(R.id.historyitem_authornametv);
                viewHolder.playtimetv = (TextView) convertView.findViewById(R.id.historyitem_playtimetv);
                viewHolder.imgrela = (RelativeLayout) convertView.findViewById(R.id.historyitem_imgrela);
                viewHolder.videoimgiv = (ImageView) convertView.findViewById(R.id.historyitem_imgiv);
                viewHolder.filmnametv = (TextView) convertView.findViewById(R.id.historyitem_filmnametv);
                viewHolder.playpositiontv = (TextView) convertView.findViewById(R.id.historyitem_playpositiontv);

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

            final VideoHistory videoHistory = list.get(position);

            ViewGroup.LayoutParams params = viewHolder.videoimgiv.getLayoutParams();
            params.width = imgWidth;
            params.height = imgHeight;
            viewHolder.videoimgiv.setLayoutParams(params);

            ViewGroup.LayoutParams imgrelaparams = viewHolder.imgrela.getLayoutParams();
            imgrelaparams.width = imgWidth;
            imgrelaparams.height = imgHeight;
            viewHolder.imgrela.setLayoutParams(imgrelaparams);

            //
            viewHolder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        videoHistory.setIsselect(VideoHistory.Select);
                    }else{
                        videoHistory.setIsselect(VideoHistory.UnSelect);
                    }
                }
            });
            //
            viewHolder.playiv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(PlayerHistoryActivity.this, OnlineVideoInfoActivity.class);
                    intent.putExtra("videoId", videoHistory.getId());
                    startActivity(intent);

                }
            });

            if(videoHistory.getIscheckVisible() == VideoHistory.CanView){
                viewHolder.cb.setVisibility(View.VISIBLE);
            }else {
                viewHolder.cb.setVisibility(View.GONE);
            }

            viewHolder.authornametv.setText(videoHistory.getAuthorName());
            viewHolder.playtimetv.setText(videoHistory.getPlayTime());
            viewHolder.filmnametv.setText(videoHistory.getVideoName());

            //播放位置
            if(videoHistory.getPlayPosition() == -1){
                viewHolder.playpositiontv.setText(R.string.word_viewover);
            }else{
                viewHolder.playpositiontv.setText(getString(R.string.word_viewpositiontv) + formatTime(videoHistory.getPlayPosition()));

            }

            //指定大小压缩,节省资源
            GlideLogic.glideLoadHeadPic(context, videoHistory.getHeadImg(), viewHolder.headimgiv, headviewWidth * 11 / 16, headviewWidth * 11 / 16);
            GlideLogic.glideLoadPic423(context, videoHistory.getVideoImg(), viewHolder.videoimgiv, imgWidth * 11 / 16, imgHeight * 11 / 16);

            if(videoHistory.getIsselect() == VideoHistory.Select){
                viewHolder.cb.setChecked(true);
            }else{
                viewHolder.cb.setChecked(false);
            }

            return convertView;
        }

        class ViewHolder {
            CheckBox cb;
            ImageView headimgiv;
            ImageView playiv;
            TextView authornametv;
            TextView playtimetv;
            RelativeLayout imgrela;
            ImageView videoimgiv;
            TextView filmnametv;
            TextView playpositiontv;
        }
    }

    /**
     * 毫秒转换为  天 时  分  秒
     */
    public String formatTime(long mss) {
        String daysStr,hoursStr,minutesStr,secondsStr;

        long minutes = mss / (1000 * 60);
        if(minutes < 10){
            minutesStr = "0" + minutes;
        }else {
            minutesStr = minutes+"";
        }

        long seconds = (mss % (1000 * 60)) / 1000;
        if(seconds < 10){
            secondsStr = "0" + seconds;
        }else {
            secondsStr = seconds+"";
        }

        return minutesStr + "'" + secondsStr + "\"";
    }


}
