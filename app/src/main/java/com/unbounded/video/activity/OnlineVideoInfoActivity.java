package com.unbounded.video.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stereo.Interaction.InteractionManager;
import com.stereo.angle.AngleActivity;
import com.stereo.util.ActionCallback;
import com.unbounded.video.BaseActivity;
import com.unbounded.video.R;
import com.unbounded.video.adapter.DiscussAdapter;
import com.unbounded.video.bean.Discuss;
import com.unbounded.video.bean.VideoDownBean;
import com.unbounded.video.bean.VideoHistory;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.constants.HttpConstants;
import com.unbounded.video.service.DownloadService;
import com.unbounded.video.utils.ACache;
import com.unbounded.video.utils.DensityUtil;
import com.unbounded.video.utils.HttpGetUtil;
import com.unbounded.video.utils.HttpPostUtil;
import com.unbounded.video.utils.JsonUtil;
import com.unbounded.video.utils.SharedPrefsUtil;
import com.unbounded.video.utils.ToastUtil;
import com.unbounded.video.utils.imageLoad.GlideLogic;
import com.unbounded.video.view.banner.containsEmojiEditText.ContainsEmojiEditText;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.zhuge.analysis.stat.ZhugeSDK;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.TimeZone;

import cn.sharesdk.onekeyshare.OnekeyShare;
import rx.functions.Action1;


/**
 * Created by zjf on 2017/7/24 0024.
 */

public class OnlineVideoInfoActivity extends BaseActivity implements View.OnLayoutChangeListener{
    public final static int VIDEOINFO_SUCCESS_FLAG = 1;
    public final static int DISCUSS_FLAG = 2;
    public final static int COLLECT_FLAG = 3;
    public final static int FOLLOW_FLAG = 4;
    public final static int LIKE_FLAG = 5;
    public final static int CANCLELIKE_FLAG = 6;
    public final static int ADDNUM_FLAG = 7;
    public final static int JUBAO_FLAG = 8;
    public final static int DELETECOLLECT_FLAG = 9;
    public final static int INTERNET_SEND_DISCUSS = 10;
    public String TAG = "OnlineVideoInfoActivity";
    String videoId,authorId,phoneNum;
    RelativeLayout titlerela,imgrela,infomationrela,discussrela, authorrela,threerela;
    LinearLayout bottomlinear;
    Button backbtn;
    ImageView playbtn,imageiv,visibleInfoiv,zannumiv,collectiv,tousuiv,downloadiv;
    Button finishtv;
    TextView timeLongtv,filmnametv,scoretv,line1,line2,infomationtv,playcounttv,zannumtv,authornametv,zuopnumtv,fansnumtv,attentiontv,nodiscusstv;
    TextView filmTypetv,directortv,performertv,releaseTimetv;
    ListView lv;
    View headview,footer;
    ImageView authorhead;
    List<Discuss> discussList = new ArrayList<Discuss>();
    List<Discuss> discussSaveList = new ArrayList<Discuss>();
    DiscussAdapter adapter;
    int currentPage=1,sumPage,videoInfoUpdate = 0;
    String selectStart;
    private static final int PLAYER_NUMBER = 6;
    private int totalItem;
    private int lastItem;
    private boolean isLoading = false;

    String uploadSize,type,userImg,timeLong,likeCount,userName,uploadTime,userCase,uploadUrl,uploadImg,name,uploadType,openCount,userFans,introduction,type3d,report,subtitle;
    String genre,director,performer,releaseTime,score;
    int type2dor3d;
    boolean follow,collector,like;

    List<VideoHistory> historylist = new ArrayList<VideoHistory>();
    String historyJson;

    Dialog gprDialog,goSetcameraDialog,activeDialog;
    String discusscontent;

    EditText bottomdiscussedt;
    //Activity最外层的Layout视图
    private View activityRootView,jubaoview;
    PopupWindow jubaopop;

    private RxPermissions rxPermissions;
    List<String> keyList = new ArrayList<String>();
    List<String> valueList = new ArrayList<String>();
    private ImageView btPlay;

    java.text.DecimalFormat df = new java.text.DecimalFormat("0.0");
    int sixteensp,fourteensp;

    Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                //评论列表
                case Constants.INTERNET_SUCCESS_FLAG:
                    String discusslistvalue = msg.obj.toString();
                    Log.e("info","discusslistvalue="+discusslistvalue);

                    if(!(TextUtils.isEmpty(discusslistvalue))) {
                        try {
                            JSONObject discusslistjo = new JSONObject(discusslistvalue);

                            JSONObject page = discusslistjo.getJSONObject("page");
                            sumPage = Integer.valueOf(page.getString("sumPage"));
                            selectStart = page.getString("selectEnd");
                            commentCount = page.getString("sumCount");
                            Log.v("sumpage",""+sumPage);
                            JSONArray data = discusslistjo.getJSONArray("data");
                            if(data != null && data.length() > 0){
//                                if(currentPage == 1 && data.length() >= 2){
//                                    for(int i=0;i<2;i++) {
//                                        JSONObject jo = data.getJSONObject(i);
//
//                                        String userImg = jo.getString("userImg");
//                                        String comment = jo.getString("comment");
//                                        String id = jo.getString("id");
//                                        String userName = jo.getString("userName");
//                                        String userId = jo.getString("userId");
//                                        String commentTime = jo.getString("commentTime");
//
//                                        Discuss discuss = new Discuss(userImg, comment, id, userName, userId, commentTime);
//                                        discussList.add(discuss);
//                                    }
//                                    for (int i = 2; i < data.length(); i++) {
//                                        JSONObject jo = data.getJSONObject(i);
//                                        String userImg = jo.getString("userImg");
//                                        String comment = jo.getString("comment");
//                                        String id = jo.getString("id");
//                                        String userName = jo.getString("userName");
//                                        String userId = jo.getString("userId");
//                                        String commentTime = jo.getString("commentTime");
//                                        Discuss discuss = new Discuss(userImg, comment, id, userName, userId, commentTime);
//                                        discussSaveList.add(discuss);
//                                    }
//                                } else {
//                                    if (currentPage == 2) {
//                                        for (int i = 0; i < discussSaveList.size(); i++) {
//                                            discussList.add(discussSaveList.get(i));
//                                        }
//                                    }
                                    if(currentPage==1){
                                        moreDiscussgoneTv.setVisibility(View.VISIBLE);
                                    }
                                    for(int i=0;i<data.length();i++){
                                        JSONObject jo = data.getJSONObject(i);

                                        String userImg = jo.getString("userImg");
                                        String comment = jo.getString("comment");
                                        String id = jo.getString("id");
                                        String userName = jo.getString("userName");
                                        String userId = jo.getString("userId");
                                        String commentTime = jo.getString("commentTime");

                                        Discuss discuss = new Discuss(userImg, comment, id, userName, userId, commentTime);
                                        discussList.add(discuss);
                                    }
//                                }

                            }
                            Log.v("discussList",""+discussList.size()+"currentPage:"+currentPage);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        onlinevideoinfoNodiscusswordtv.setText(getString(R.string.Word_peoplediscuss,commentCount));
                        moreDiscussgoneTv.setText(getString(R.string.more_comment,commentCount));
                        Log.e("info1","discussList.size()="+commentCount);
                        isLoading = false;

                        if(sumPage < 2){
                            if(lv.getFooterViewsCount() > 0){
                                lv.removeFooterView(footer);
                                footer.setVisibility(View.GONE);

                            }
                        }

                        if(adapter != null){
                            adapter.notifyDataSetChanged();
                        }

                        if(currentPage == 1 && discussList.size() == 0){
                            nodiscusstv.setVisibility(View.VISIBLE);

                        }

                        //
                        if(discussList.size() == 0){
                            nodiscusstv.setVisibility(View.VISIBLE);
                        }else {
                            nodiscusstv.setVisibility(View.GONE);
                        }
                        progressDiddismiss();
                    }

                    break;
                //视频信息
                case VIDEOINFO_SUCCESS_FLAG:
                    String infovalue = msg.obj.toString();

                    Log.e("info","infovalue="+infovalue);

                    if(!(TextUtils.isEmpty(infovalue))) {
                        try {
                            JSONObject infojo = new JSONObject(infovalue);

                            follow = infojo.getBoolean("follow");
                            collector = infojo.getBoolean("collector");
                            like = infojo.getBoolean("like");

                            JSONArray data = infojo.getJSONArray("data");
                            if (data != null && data.length() > 0) {
                                JSONObject jo = data.getJSONObject(0);

                                uploadSize = jo.getString("uploadSize");
                                userImg = jo.getString("userImg");
                                type3d = jo.getString("3DType");
                                timeLong = jo.getString("timeLong");
                                likeCount = jo.getString("likeCount");
                                userName = jo.getString("userName");
                                uploadTime = jo.getString("uploadTime");
                                authorId = jo.getString("userId");
                                userCase = jo.getString("userCase");
                                uploadUrl = jo.getString("uploadUrl");
                                uploadImg = jo.getString("uploadImg");
                                name = jo.getString("name");
                                uploadType = jo.getString("uploadType");
                                openCount = jo.getString("num");
                                userFans = jo.getString("userFans");
                                introduction = jo.getString("introduction");
                                report = jo.getString("report");
                                type = jo.getString("type");

                                genre = jo.getString("genre");//视频类型
                                director = jo.getString("director");//导演
                                performer = jo.getString("performer");//主演
                                releaseTime = jo.getString("releaseTime");//上映时间
                                score = jo.getString("score");//评分
                                subtitle = jo.getString("subtitle");//字幕

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        setDataViewVisible(true);

                        //评分，类型，上映时间
                        if((TextUtils.isEmpty(score) || "null".equals(score) || "0".equals(score)) &&  (TextUtils.isEmpty(genre) || "null".equals(genre)) && (TextUtils.isEmpty(releaseTime) || "null".equals(releaseTime))){
                            threerela.setVisibility(View.GONE);

                        }else{
                            if((TextUtils.isEmpty(score) || "null".equals(score) || "0".equals(score))){
                                scoretv.setVisibility(View.GONE);
                                line1.setVisibility(View.GONE);

                                if(TextUtils.isEmpty(genre) || "null".equals(genre)){
                                    line2.setVisibility(View.GONE);
                                    filmTypetv.setVisibility(View.GONE);
                                    releaseTimetv.setText(releaseTime + getString(com.unbounded.video.R.string.word_year));
                                }else if(TextUtils.isEmpty(releaseTime) || "null".equals(releaseTime)){
                                    line2.setVisibility(View.GONE);
                                    releaseTimetv.setVisibility(View.GONE);
                                    filmTypetv.setText(genre);
                                }else{
                                    line2.setVisibility(View.VISIBLE);
                                    filmTypetv.setText(genre);
                                    releaseTimetv.setText(releaseTime + getString(com.unbounded.video.R.string.word_year));
                                }
                            }else{
                                scoretv.setVisibility(View.VISIBLE);
                                scoretv.setText(score + getString(com.unbounded.video.R.string.word_fen));

                                if((TextUtils.isEmpty(genre) || "null".equals(genre)) && (TextUtils.isEmpty(releaseTime) || "null".equals(releaseTime))){
                                    line1.setVisibility(View.GONE);
                                    line2.setVisibility(View.GONE);
                                }else{
                                    if((TextUtils.isEmpty(genre) || "null".equals(genre))){
                                        line1.setVisibility(View.VISIBLE);
                                        line2.setVisibility(View.GONE);
                                        filmTypetv.setVisibility(View.GONE);
                                        releaseTimetv.setText(releaseTime + getString(com.unbounded.video.R.string.word_year));
                                    }else if(TextUtils.isEmpty(releaseTime) || "null".equals(releaseTime)){
                                        line1.setVisibility(View.VISIBLE);
                                        line2.setVisibility(View.GONE);
                                        releaseTimetv.setVisibility(View.GONE);
                                        filmTypetv.setText(genre);
                                    }else{
                                        line1.setVisibility(View.VISIBLE);
                                        line2.setVisibility(View.VISIBLE);
                                        filmTypetv.setText(genre);
                                        releaseTimetv.setText(releaseTime + getString(com.unbounded.video.R.string.word_year));
                                    }
                                }
                            }
                        }


                        //导演
                        if(TextUtils.isEmpty(director) || "null".equals(director)){
                            directortv.setVisibility(View.GONE);
                        }else{
                            directortv.setText(getString(com.unbounded.video.R.string.word_director) + director);
                        }
                        //主演
                        if(TextUtils.isEmpty(performer) || "null".equals(performer)){
                            performertv.setVisibility(View.GONE);
                        }else{
                            performertv.setText(getString(com.unbounded.video.R.string.word_performer) + performer);
                        }
                        //简介
                        if (infomationtv != null) {
                            if(TextUtils.isEmpty(introduction) || "null".equals(introduction)){
                                infomationtv.setVisibility(View.GONE);
                            }else{
                                infomationtv.setText(getString(com.unbounded.video.R.string.word_infomation) + introduction);
                            }
                        }

                        //视频名称
                        if (filmnametv != null) {
                            filmnametv.setText(name);
                        }
//                            String allStr = name +"   " + score;
//                            ColorStateList redColors = ColorStateList.valueOf(0xffef713a);
//                            SpannableStringBuilder spanBuilder = new SpannableStringBuilder(allStr);
                            //style 为0 即是正常的，还有Typeface.BOLD(粗体) Typeface.ITALIC(斜体)等
                            //size  为0 即采用原始的正常的 size大小
//                            spanBuilder.setSpan(new TextAppearanceSpan(null, 0, fourteensp, redColors, null), allStr.length()-1, allStr.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
//                            spanBuilder.setSpan(new TextAppearanceSpan(null, 0, sixteensp, redColors, null), allStr.length()-3, allStr.length()-1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);


//                            filmnametv.setText(spanBuilder);
//
//                            Bitmap bitmap= BitmapFactory.decodeResource(OnlineVideoInfoActivity.this.getResources(), R.mipmap.scoreicon);
//                            ImageSpan imgSpan = new ImageSpan(OnlineVideoInfoActivity.this,bitmap);
//                            SpannableString spanString = new SpannableString("   i");
//                            spanString.setSpan(imgSpan, 3, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                            filmnametv.append(spanString);
//                        }


                        //格式
                        if(TextUtils.isEmpty(type3d)){
                            type2dor3d = 0;
                        }else {
                            if("0".equals(type3d)){
                                type2dor3d = 0;
                            }else if("1".equals(type3d)){
                                type2dor3d = 1;
                            }else if("2".equals(type3d)){
                                type2dor3d = 2;
                            }

                        }
                        //判断类型
//                        if(downloadiv != null){
//                            if("3d".equals(type)){
//                                downloadiv.setEnabled(false);
//                                downloadiv.setClickable(false);
//                            }else{
//                                downloadiv.setEnabled(true);
//                                downloadiv.setClickable(true);
//                            }
//                        }
                        //作者区域
                        if(authorrela != null){
                            if("3d".equals(type)){
                                authorrela.setVisibility(View.GONE);
                            }else{
                                authorrela.setVisibility(View.GONE);
                            }
                        }
                        //时长
                        if (timeLongtv != null) {
                            if(TextUtils.isEmpty(timeLong) || "null".equals(timeLong)){
                                timeLongtv.setText(com.unbounded.video.R.string.Word_timelong);
                                timeLong = "00'00\"";

                            }else{
                                if(timeLong.contains("'")){
                                    String[] line = timeLong.split("'");
                                    timeLongtv.setText(getString(R.string.long_time,line[0]));
                                }else{
                                    timeLongtv.setText(getString(R.string.long_time,timeLong));
                                }

                            }
                        }

                        //播放次数
                        if (playcounttv != null) {
                            playcounttv.setText(openCount + getString(com.unbounded.video.R.string.Word_playernum));
                        }
                        //点赞人数
                        if (zannumtv != null) {
                            zannumtv.setText(likeCount);
                        }
                        //作者名称
                        if (authornametv != null) {
                            authornametv.setText(userName);
                        }
                        //作品数
                        if (zuopnumtv != null) {
                            zuopnumtv.setText(getString(com.unbounded.video.R.string.Word_zuopnum) + userCase);
                        }
                        //粉丝
                        if (fansnumtv != null) {
                            fansnumtv.setText(getString(com.unbounded.video.R.string.Word_fansnum) + userFans);
                        }
                        //是否收费
                        if("1".equals(report) || Constants.Free_Flag.equals(report) || "".equals(report) || "null".equals(report)){
                            //免费
                            report = Constants.Free_Flag;

                        }else{
                            //收费
                            report = Constants.NotFree_Flag;

                        }

//                        //rjz 去掉点赞，不需要跟换点赞图标
//                        if(like == false){
//                            zannumiv.setImageResource(com.unbounded.video.R.mipmap.zanbtn1);
//                        }else{
//                            zannumiv.setImageResource(com.unbounded.video.R.mipmap.zanbtn2);
//                        }
                        //关注
                        if (follow == false) {
                            attentiontv.setText(getString(com.unbounded.video.R.string.Word_Attention));
                            attentiontv.setClickable(true);
                        } else {
                            attentiontv.setText(getString(com.unbounded.video.R.string.Word_Attentioned));
                            attentiontv.setClickable(false);
                        }
                        //收藏
                        if (collector == false) {
                            collectiv.setImageResource(com.unbounded.video.R.mipmap.infocollectbtn1);

                        } else {
                            collectiv.setImageResource(com.unbounded.video.R.mipmap.infocollectbtn2);
                        }
                        //视频图片
                        if (imageiv != null) {
                            GlideLogic.glideLoadPic423(OnlineVideoInfoActivity.this, uploadImg, imageiv, screenWidth * 13 / 16, screenWidth * 39 / 64);
                        }
                        //作者头像
                        if (authorhead != null) {
                            GlideLogic.glideLoadHeadPic(OnlineVideoInfoActivity.this, userImg, authorhead, 60 * oneDp, 60 * oneDp);
                        }

                        progressDiddismiss();
                    }

                    break;
                //发表评论返回
                case DISCUSS_FLAG:
                    String discussvalue = msg.obj.toString();

                    Log.e("info","discussvalue="+discussvalue);

                    if(!(TextUtils.isEmpty(discussvalue))){
                        try {
                            JSONObject discussjo = new JSONObject(discussvalue);

                            String result = discussjo.getString("result");

                            if(Constants.SUCCESS_STR.equals(result)){
                                ToastUtil.showToast(OnlineVideoInfoActivity.this, com.unbounded.video.R.string.Word_discusssuccess, ToastUtil.CENTER);
//                                discussdialog.dismiss();
                                bottomdiscussedt.setText("");
                                currentPage = 1;
                                if(discussList != null){
                                    discussList.clear();
                                }
                                if(adapter != null){
                                    adapter.notifyDataSetChanged();
                                }

                                initDiscussList();
//                                sendDiscussList();

                            }else if(Constants.ERROR_STR.equals(result)){
                                ToastUtil.showToast(OnlineVideoInfoActivity.this, com.unbounded.video.R.string.Word_discusserror, ToastUtil.CENTER);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        progressDiddismiss();

                    }

                    break;

                //收藏
                case COLLECT_FLAG:
                    String collectvalue = msg.obj.toString();
//                    Log.e("info","collectvalue="+collectvalue);

                    if(!(TextUtils.isEmpty(collectvalue))){
                        try {
                            JSONObject collectjo = new JSONObject(collectvalue);
                            String result = collectjo.getString("result");
                            Log.v("collect_flag",""+result);
                            if(Constants.SUCCESS_STR.equals(result)){
                                ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_collectsuccess, ToastUtil.CENTER);
//                                collectionNumberTv.setText("收藏");
                                collectiv.setImageResource(com.unbounded.video.R.mipmap.infocollectbtn2);
                                collector = true;
                            }else {
                                ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_collecterror, ToastUtil.CENTER);
//                                collectionNumberTv.setText("收藏");
                                collectiv.setImageResource(com.unbounded.video.R.mipmap.infocollectbtn1);
                                collector = false;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        progressDiddismiss();
                    }

                    break;
                //取消收藏
                case DELETECOLLECT_FLAG:
                    String deletecollectvalue = msg.obj.toString();
//                    Log.e("info","collectvalue="+collectvalue);

                    if(!(TextUtils.isEmpty(deletecollectvalue))){
                        try {
                            JSONObject collectjo = new JSONObject(deletecollectvalue);
                            String result = collectjo.getString("result");
                            if(Constants.SUCCESS_STR.equals(result)){
//                                collectionNumberTv.setText(getString(R.string.collection));
                                ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_deletecollectsuccess, ToastUtil.CENTER);
                                collectiv.setImageResource(com.unbounded.video.R.mipmap.infocollectbtn1);
                                collector = false;
                            }else {
                                ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_workerror, ToastUtil.CENTER);
//                                collectionNumberTv.setText(getString(R.string.collectioned));
                                collectiv.setImageResource(com.unbounded.video.R.mipmap.infocollectbtn2);
                                collector = true;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        progressDiddismiss();
                    }

                    break;
                //关注
                case FOLLOW_FLAG:
                    String followvalue = msg.obj.toString();
//                    Log.e("info","followvalue="+followvalue);
                    if(!(TextUtils.isEmpty(followvalue))){
                        try {
                            JSONObject collectjo = new JSONObject(followvalue);
                            String result = collectjo.getString("result");
                            if(Constants.SUCCESS_STR.equals(result)){
                                ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_followsuccess, ToastUtil.CENTER);
                                attentiontv.setText(com.unbounded.video.R.string.Word_followsuccess);
                                attentiontv.setClickable(false);
                            }else {
                                ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_followerror, ToastUtil.CENTER);
                                attentiontv.setText(com.unbounded.video.R.string.Word_Attention);
                                attentiontv.setClickable(true);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        progressDiddismiss();
                    }

                    break;
                //举报
                case JUBAO_FLAG:
                    String jubaovalue = msg.obj.toString();
                    Log.e("info","jubaovalue="+jubaovalue);
                    if(!(TextUtils.isEmpty(jubaovalue))){
                        try {
                            JSONObject collectjo = new JSONObject(jubaovalue);
                            String result = collectjo.getString("result");
                            if(Constants.SUCCESS_STR.equals(result)){
                                ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_worksuccess, ToastUtil.CENTER);
                                tousuiv.setImageResource(com.unbounded.video.R.mipmap.jubaobtn2);

                            }else {
                                ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_workerror, ToastUtil.CENTER);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        progressDiddismiss();
                    }

                    break;

                //点赞
                case LIKE_FLAG:
                    String likevalue = msg.obj.toString();
//                    Log.e("info","likevalue="+likevalue);
                    if(!(TextUtils.isEmpty(likevalue))){
                        try {
                            JSONObject collectjo = new JSONObject(likevalue);
                            String result = collectjo.getString("result");
                            if(Constants.SUCCESS_STR.equals(result)){
                                ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_likesuccess, ToastUtil.CENTER);
//                                zannumiv.setImageResource(com.unbounded.video.R.mipmap.zanbtn2);
                                zannumtv.setText(String.valueOf(Integer.valueOf(zannumtv.getText().toString())+1));
                                like = true;
                            }else {
                                ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.Word_likeerror, ToastUtil.CENTER);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        progressDiddismiss();
                    }

                    break;
                //取消点赞
                case CANCLELIKE_FLAG:
                    String canclelikevalue = msg.obj.toString();
                    Log.e("info","canclelikevalue="+canclelikevalue);
                    if(!(TextUtils.isEmpty(canclelikevalue))) {
                        try {
                            JSONObject collectjo = new JSONObject(canclelikevalue);
                            String result = collectjo.getString("result");
                            if (Constants.SUCCESS_STR.equals(result)) {
                                ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.canclezan_success, ToastUtil.CENTER);
//                                zannumiv.setImageResource(com.unbounded.video.R.mipmap.zanbtn1);
                                zannumtv.setText(String.valueOf(Integer.valueOf(zannumtv.getText().toString())-1));
                                like = false;
                            } else {
                                ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.canclezan_error, ToastUtil.CENTER);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        progressDiddismiss();
                    }

                    break;
                //增加播放次数
                case ADDNUM_FLAG:
                    String addnumvalue = msg.obj.toString();
                    Log.e("info","addnumvalue="+addnumvalue);



                    break;

                case Constants.INTERNET_ERROR_FLAG:
                    ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.internet_error, ToastUtil.CENTER);
                    progressDiddismiss();

                    break;
                case INTERNET_SEND_DISCUSS:
                    String updateDiscusslistvalue = msg.obj.toString();
                    Log.e("info","discusslistvalue="+updateDiscusslistvalue);

                    if(!(TextUtils.isEmpty(updateDiscusslistvalue))) {
                        try {
                            JSONObject discusslistjo = new JSONObject(updateDiscusslistvalue);

                            JSONObject page = discusslistjo.getJSONObject("page");
                            sumPage = Integer.valueOf(page.getString("sumPage"));
                            selectStart = page.getString("selectEnd");
                            commentCount = page.getString("sumCount");
                            Log.v("sumpage",""+sumPage);
                            JSONArray data = discusslistjo.getJSONArray("data");
                            if (data != null && data.length() > 0) {
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject jo = data.getJSONObject(i);

                                    String userImg = jo.getString("userImg");
                                    String comment = jo.getString("comment");
                                    String id = jo.getString("id");
                                    String userName = jo.getString("userName");
                                    String userId = jo.getString("userId");
                                    String commentTime = jo.getString("commentTime");

                                    Discuss discuss = new Discuss(userImg, comment, id, userName, userId, commentTime);
                                    discussList.add(discuss);
                                }

                            }
                            Log.v("discussList",""+discussList.size()+"currentPage:"+currentPage);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        onlinevideoinfoNodiscusswordtv.setText(getString(R.string.Word_peoplediscuss,commentCount));
                        moreDiscussgoneTv.setText(getString(R.string.more_comment,commentCount));
                        Log.e("info1","discussList.size()="+commentCount);
                        isLoading = false;

                        if(sumPage < 2){
                            if(lv.getFooterViewsCount() > 0){
                                lv.removeFooterView(footer);
                                footer.setVisibility(View.GONE);

                            }
                        }

                        if(adapter != null){
                            adapter.notifyDataSetChanged();
                        }

                        if(currentPage == 1 && discussList.size() == 0){
                            nodiscusstv.setVisibility(View.VISIBLE);

                        }

                        //
                        if(discussList.size() == 0){
                            nodiscusstv.setVisibility(View.VISIBLE);
                        }else {
                            nodiscusstv.setVisibility(View.GONE);
                        }
                        progressDiddismiss();
                    }
                    break;
            }
        }
    };
    private TextView onlinevideoinfoNodiscusswordtv;
    private TextView moreDiscussgoneTv;
    private String commentCount;
    private LinearLayout loadMoreLl;
    private boolean isScroll = false;
    private RelativeLayout onlinevideoinfoDiscussgonerela;
    private TextView remainingWords;
    private LinearLayout onlinevideoinfoReportMovieLl;
    private TextView collectionNumberTv;
    private LinearLayout onlinevideoinfoCommMovieLl;
    private LinearLayout onlinevideoinfoZancountll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentView() {
        return com.unbounded.video.R.layout.activity_onlinevideoinfo;
    }

    @Override
    public void init(Intent intent) {
        super.init(intent);
        setTitleViewVisible(false);
        videoId = intent.getStringExtra("videoId");
        authorId = intent.getStringExtra("authorId");
        rxPermissions = new RxPermissions(this);

        sixteensp = DensityUtil.sp2px(getApplicationContext(), 16);
        fourteensp = DensityUtil.sp2px(getApplicationContext(), 14);

    }

    @Override
    public void initDatas() {
        super.initDatas();

        userId = SharedPrefsUtil.getValue(getApplicationContext(), Constants.UserId_FLAG, "");
        videoInfoUpdate = SharedPrefsUtil.getValue(getApplicationContext(), Constants.VideoInfoUpdate, 0);

        setDataViewVisible(false);

        if(videoInfoUpdate == 0){
            if(discussList != null){
                discussList.clear();
            }

            progressDid();
            initDiscussList();
            initVideoInfo();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        userId = SharedPrefsUtil.getValue(getApplicationContext(), Constants.UserId_FLAG, "");
        videoInfoUpdate = SharedPrefsUtil.getValue(getApplicationContext(), Constants.VideoInfoUpdate, 0);

        if(videoInfoUpdate == 1){
            if(discussList != null){
                discussList.clear();
                discussSaveList.clear();
            }
            if(adapter != null){
                adapter.notifyDataSetChanged();
            }
            progressDid();
            initDiscussList();
            initVideoInfo();
            SharedPrefsUtil.putValue(getApplicationContext(), Constants.VideoInfoUpdate, 0);
        }

        //添加layout大小发生改变监听器
        activityRootView.addOnLayoutChangeListener(OnlineVideoInfoActivity.this);
    }

    /**
     * 评论列表
     */
    private void initDiscussList(){
        String url;
        if(!(TextUtils.isEmpty(selectStart)) && currentPage != 1){
            url = HttpConstants.Discuss_listUrl + "?currentPage=" + currentPage + "&id="+videoId + "&selectStart="+selectStart;
        }else {
            url = HttpConstants.Discuss_listUrl + "?currentPage=" + currentPage + "&id="+videoId;
        }
        Log.v("initDiscussList","initDiscussList"+url);
        HttpGetUtil get = new HttpGetUtil(handler, url, Constants.INTERNET_SUCCESS_FLAG);
        Thread thread = new Thread(get);
        thread.start();

    }
    /**
     * 发表评论
     */
    private void sendDiscussList(){
        String url;
        if(!(TextUtils.isEmpty(selectStart)) && currentPage != 1){
            url = HttpConstants.Discuss_listUrl + "?currentPage=" + currentPage + "&id="+videoId + "&selectStart="+selectStart;
        }else {
            url = HttpConstants.Discuss_listUrl + "?currentPage=" + currentPage + "&id="+videoId;
        }
        Log.v("initDiscussList","initDiscussList"+url);
        HttpGetUtil get = new HttpGetUtil(handler, url, INTERNET_SEND_DISCUSS);
        Thread thread = new Thread(get);
        thread.start();

    }
    /**
     * 视频信息
     */
    private void initVideoInfo(){
        String url = HttpConstants.selectInfoByID_VideosUrl + "?id=" + videoId + "&userId=" + userId;
        HttpGetUtil get = new HttpGetUtil(handler, url, VIDEOINFO_SUCCESS_FLAG);
        Thread thread = new Thread(get);
        thread.start();
    }

    @Override
    public void initView() {
        super.initView();

        initJubaoPop();

        footer = LayoutInflater.from(getApplicationContext()).inflate(com.unbounded.video.R.layout.load_more_footer,null ,false);
        headview = View.inflate(OnlineVideoInfoActivity.this, com.unbounded.video.R.layout.activity_onlinevideoinfohead, null);
        titlerela = (RelativeLayout) findViewById(com.unbounded.video.R.id.OnlineVideoInfo_titlerela);
        imgrela = (RelativeLayout) findViewById(com.unbounded.video.R.id.OnlineVideoInfo_imgrela);
        imageiv = (ImageView) findViewById(com.unbounded.video.R.id.OnlineVideoInfo_imgiv);
        backbtn = (Button) findViewById(com.unbounded.video.R.id.OnlineVideoInfo_backbtn);
        playbtn = (ImageView) findViewById(com.unbounded.video.R.id.OnlineVideoInfo_playbtn);
        timeLongtv = (TextView) findViewById(com.unbounded.video.R.id.OnlineVideoInfo_timelongtv);
        discussrela = (RelativeLayout) findViewById(com.unbounded.video.R.id.onlinevideoinfo_discussrela);
        bottomdiscussedt = (EditText) findViewById(com.unbounded.video.R.id.bottompop_discussedt);
        finishtv = (Button) findViewById(com.unbounded.video.R.id.bottompop_finishtv);
        bottomlinear = (LinearLayout) findViewById(com.unbounded.video.R.id.onlinevideoinfo_bottomlinear);
        lv = (ListView) findViewById(com.unbounded.video.R.id.onlinevideoinfo_lv);
        activityRootView = findViewById(com.unbounded.video.R.id.OnlineVideoInfo_main);



        collectiv = (ImageView) headview.findViewById(com.unbounded.video.R.id.onlinevideoinfo_collectbtn);
        tousuiv = (ImageView) headview.findViewById(com.unbounded.video.R.id.onlinevideoinfo_tousuiv);
        downloadiv = (ImageView) findViewById(com.unbounded.video.R.id.onlinevideoinfo_downloadbtn);
        filmnametv = (TextView) findViewById(com.unbounded.video.R.id.onlinevideoinfo_filmnametv);
        visibleInfoiv = (ImageView) headview.findViewById(com.unbounded.video.R.id.onlinevideoinfo_filmnameiv);
        infomationrela = (RelativeLayout) headview.findViewById(com.unbounded.video.R.id.onlinevideoinfo_infomationrela);
        infomationtv = (TextView) headview.findViewById(com.unbounded.video.R.id.onlinevideoinfo_infomationtv);
        playcounttv = (TextView) findViewById(com.unbounded.video.R.id.onlinevideoinfo_playcounttv);
        zannumtv = (TextView) findViewById(com.unbounded.video.R.id.onlinevideoinfo_zancounttv);
        zannumiv = (ImageView) findViewById(com.unbounded.video.R.id.onlinevideoinfo_zancountiv);
        authorrela = (RelativeLayout) headview.findViewById(com.unbounded.video.R.id.onlinevideoinfo_authorrela);
        authorhead = (ImageView) headview.findViewById(com.unbounded.video.R.id.onlinevideoinfo_authoriv);
        authornametv = (TextView) headview.findViewById(com.unbounded.video.R.id.onlinevideoinfo_authornametv);
        zuopnumtv = (TextView) headview.findViewById(com.unbounded.video.R.id.onlinevideoinfo_zuopnumtv);
        fansnumtv = (TextView) headview.findViewById(com.unbounded.video.R.id.onlinevideoinfo_fansnumtv);
        attentiontv = (TextView) headview.findViewById(com.unbounded.video.R.id.onlinevideoinfo_attentionbtn);
        nodiscusstv = (TextView) headview.findViewById(com.unbounded.video.R.id.onlinevideoinfo_nodiscusstv);

        //,,,,scoretv;
        filmTypetv = (TextView) headview.findViewById(com.unbounded.video.R.id.onlinevideoinfo_filmtypetv);
        directortv = (TextView) headview.findViewById(com.unbounded.video.R.id.onlinevideoinfo_directortv);
        performertv = (TextView) headview.findViewById(com.unbounded.video.R.id.onlinevideoinfo_actortv);
        releaseTimetv = (TextView) headview.findViewById(com.unbounded.video.R.id.onlinevideoinfo_releaseertv);

        scoretv = (TextView) headview.findViewById(com.unbounded.video.R.id.onlinevideoinfo_filmscoretv);
        line1 = (TextView) headview.findViewById(com.unbounded.video.R.id.onlinevideoinfo_line1);
        line2 = (TextView) headview.findViewById(com.unbounded.video.R.id.onlinevideoinfo_line2);
        threerela = (RelativeLayout) headview.findViewById(com.unbounded.video.R.id.onlinevideoinfo_threelxrela);

        onlinevideoinfoNodiscusswordtv = (TextView) headview.findViewById(R.id.onlinevideoinfo_nodiscusswordtv);
        btPlay=(ImageView)findViewById(com.unbounded.video.R.id.OnlineVideoInfo_playbtn);
        onlinevideoinfoDiscussgonerela = (RelativeLayout) findViewById(R.id.onlinevideoinfo_discussgonerela);
        moreDiscussgoneTv = (TextView) footer.findViewById(R.id.more_discussgone_tv);
        loadMoreLl = (LinearLayout) footer.findViewById(R.id.load_more_ll);
        remainingWords = (TextView) findViewById(R.id.remaining_words);
        onlinevideoinfoReportMovieLl = (LinearLayout) findViewById(R.id.onlinevideoinfo_report_movie_ll);
        collectionNumberTv = (TextView) findViewById(R.id.collection_number_tv);
        onlinevideoinfoCommMovieLl = (LinearLayout) findViewById(R.id.onlinevideoinfo_comm_movie_ll);
        onlinevideoinfoZancountll = (LinearLayout) findViewById(R.id.onlinevideoinfo_zancountll);
        lv.addHeaderView(headview);
        lv.addFooterView(footer);

        nodiscusstv.setVisibility(View.GONE);
//        footer.setVisibility(View.GONE);
        loadMoreLl.setVisibility(View.GONE);
        adapter = new DiscussAdapter(OnlineVideoInfoActivity.this, discussList, oneDp);
        lv.setAdapter(adapter);


    }

    @Override
    public void initParams() {
        super.initParams();
//
//        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) titlerela.getLayoutParams();
//        params.height = screenHeight * 65/ 1210;
//        titlerela.setLayoutParams(params);
//
//        ViewGroup.LayoutParams imgrelaparams = (ViewGroup.LayoutParams) imgrela.getLayoutParams();
//        imgrelaparams.height = screenHeight * 30/ 100;
//        imgrela.setLayoutParams(imgrelaparams);
//
//        ViewGroup.LayoutParams imageivparams = (ViewGroup.LayoutParams) imageiv.getLayoutParams();
//        imageivparams.height = screenHeight * 30/ 100;
//        imageiv.setLayoutParams(imageivparams);
    }

    @Override
    public void initEvent() {
        super.initEvent();

        lv.setOnScrollListener(new AbsListView.OnScrollListener(){
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (isScroll) {
                    if (totalItem == lastItem && scrollState == SCROLL_STATE_IDLE) {
                        if (isLoading == false) {
                            if (currentPage < sumPage) {
                                isLoading = true;
                                footer.setVisibility(View.VISIBLE);
                                nodiscusstv.setVisibility(View.VISIBLE);
                                currentPage = currentPage + 1;

                                if (adapter != null) {
                                    adapter.notifyDataSetChanged();
                                }

                                initDiscussList();
                            } else {
                                if (currentPage > 0) {
                                    isLoading = false;
                                    footer.setVisibility(View.GONE);
//                                ToastUtil.showToast(getApplicationContext(), R.string.not_have_more, ToastUtil.BOTTOM);
                                }
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

        attentiontv.setOnClickListener(this);
        collectiv.setOnClickListener(this);
        discussrela.setOnClickListener(this);
        visibleInfoiv.setOnClickListener(this);
        zannumiv.setOnClickListener(this);
        downloadiv.setOnClickListener(this);
        backbtn.setOnClickListener(this);
        btPlay.setOnClickListener(this);
        finishtv.setOnClickListener(this);
        tousuiv.setOnClickListener(this);
        moreDiscussgoneTv.setOnClickListener(this);
        onlinevideoinfoDiscussgonerela.setOnClickListener(this);
        onlinevideoinfoReportMovieLl.setOnClickListener(this);
        onlinevideoinfoCommMovieLl.setOnClickListener(this);
        onlinevideoinfoZancountll.setOnClickListener(this);
        bottomdiscussedt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int  number = s.length();
                /*最多输入350个字符，显示剩余字符数*/
                remainingWords.setText((350-number)+"");
            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case com.unbounded.video.R.id.OnlineVideoInfo_backbtn:
                finish();
                break;
            //下载
            case com.unbounded.video.R.id.onlinevideoinfo_downloadbtn:
                Log.v("downloadbtn","downloadbtn");
                userId = SharedPrefsUtil.getValue(getApplicationContext(), Constants.UserId_FLAG, "");

                int downloadnetType = getNetState();

                if(downloadnetType == Constants.WIFI_FLAG){
                    downloadMethod();
                }else if(downloadnetType == Constants.GPR_FLAG){
                    //数据流量弹出对话框
                    gprDia();

                }else {
                    ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.checkbet_word, ToastUtil.CENTER);
                }

                break;
            //点赞和取消点赞
            case com.unbounded.video.R.id.onlinevideoinfo_zancountll:
                userId = SharedPrefsUtil.getValue(getApplicationContext(), Constants.UserId_FLAG, "");
                if(TextUtils.isEmpty(userId)){
                    openActivity(LoginActivity.class);
                }else{
                    if(like == false){
                        likeMethod();
                    }else {
                        daleteLike();
                    }
                    progressDid();
                }

                break;

            //关注
            case com.unbounded.video.R.id.onlinevideoinfo_attentionbtn:
                userId = SharedPrefsUtil.getValue(getApplicationContext(), Constants.UserId_FLAG, "");
                if(TextUtils.isEmpty(userId)){
                    openActivity(LoginActivity.class);
                }else{
                    followMethod();
                    progressDid();
                }

                break;
            //投诉视频
            case com.unbounded.video.R.id.onlinevideoinfo_tousuiv:
                userId = SharedPrefsUtil.getValue(getApplicationContext(), Constants.UserId_FLAG, "");
                if(TextUtils.isEmpty(userId)){
                    openActivity(LoginActivity.class);
                }else{
                    hideInputMode();
                    jubaopop.showAtLocation(findViewById(com.unbounded.video.R.id.OnlineVideoInfo_main), Gravity.BOTTOM, 0, 0);
                    backgroundAlpha(0.4f);
                }

                break;
            //收藏
            case com.unbounded.video.R.id.onlinevideoinfo_comm_movie_ll:
                userId = SharedPrefsUtil.getValue(getApplicationContext(), Constants.UserId_FLAG, "");

                if(TextUtils.isEmpty(userId)){
                    openActivity(LoginActivity.class);
                }else{
                    if(collector == false){
                        collectMethod();
                    }else{
                        deleteCollectMethod();
                    }

                    progressDid();
                }

                break;
            //评论对话框
            case com.unbounded.video.R.id.onlinevideoinfo_discussgonerela:
                userId = SharedPrefsUtil.getValue(getApplicationContext(), Constants.UserId_FLAG, "");
                if(TextUtils.isEmpty(userId)){
                    openActivity(LoginActivity.class);
                }else{
                    discussrelaMethod();

                }

                break;
            //显示隐藏简介
            case com.unbounded.video.R.id.onlinevideoinfo_filmnameiv:
                if(infomationrela.getVisibility() == View.GONE){
                    infomationrela.setVisibility(View.VISIBLE);
                    visibleInfoiv.setImageResource(com.unbounded.video.R.mipmap.downflag);
                }else {
                    infomationrela.setVisibility(View.GONE);
                    visibleInfoiv.setImageResource(com.unbounded.video.R.mipmap.main_setting_enter);
                }

                break;

            //播放
            case com.unbounded.video.R.id.OnlineVideoInfo_playbtn:
                Log.e("info","点击......");
                playPerssion();

                break;
            //评论发表
            case com.unbounded.video.R.id.bottompop_finishtv:
                discusscontent = bottomdiscussedt.getText().toString().trim();

                hideInputMode();
                if(TextUtils.isEmpty(discusscontent)){
                    return;
                }

                progressDid();
                discussMethod();

                break;
            case R.id.more_discussgone_tv:
                isScroll = true;
                moreDiscussgoneTv.setVisibility(View.GONE);
                if (currentPage < sumPage) {
                    currentPage = currentPage + 1;
                    initDiscussList();
                }

                break;
            case R.id.onlinevideoinfo_report_movie_ll:
                Intent intent = new Intent(this,ReportMovieActivity.class);
                intent.putExtra("videoId",videoId);
                intent.putExtra("userid",authorId);
                startActivity(intent);
                break;
        }
    }

    /**
     * 举报视频
     *
     */
    private void initJubaoPop(){
        jubaoview = this.getLayoutInflater().inflate(com.unbounded.video.R.layout.jubaopop, null);
        jubaopop = new PopupWindow(jubaoview,screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        jubaopop.setFocusable(true);
        jubaopop.setOutsideTouchable(true);
        jubaopop.setBackgroundDrawable(new BitmapDrawable());
        jubaopop.setOnDismissListener(new PopupWindow.OnDismissListener()
        {
            @Override
            public void onDismiss()
            {
                backgroundAlpha(1f);
            }

        });

        ImageView jubaobtn = (ImageView) jubaoview.findViewById(com.unbounded.video.R.id.jubaopop_jubaoiv);
        ImageView closeiv = (ImageView) jubaoview.findViewById(com.unbounded.video.R.id.jubaopop_closeiv);

        //
        jubaobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jubaoMethod();
                jubaopop.dismiss();
            }
        });
        //
        closeiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jubaopop.dismiss();
            }
        });
    }

    /**
     * 举报视频方法
     */
    private void jubaoMethod(){
        progressDid();

        keyList.clear();
        valueList.clear();

        keyList.add("userId");
        keyList.add("videoId");
        keyList.add("type");

        valueList.add(userId);
        valueList.add(videoId);
        valueList.add("video");

        String likeurl = HttpConstants.UpdateReport_Url;
        HttpPostUtil post = new HttpPostUtil(handler, likeurl,keyList , valueList, JUBAO_FLAG);
        Thread thread = new Thread(post);
        thread.start();

    }

    /**
     * 点击底部评论
     */
    private void discussrelaMethod(){
        bottomlinear.setVisibility(View.GONE);

        bottomdiscussedt.setFocusable(true);
        bottomdiscussedt.setFocusableInTouchMode(true);
        bottomdiscussedt.requestFocus();
        bottomdiscussedt.setCursorVisible(true);
        //打开软键盘
        InputMethodManager imm = (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right,
                               int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//        //现在认为只要控件将Activity向上推的高度超过了1/3屏幕高，就认为软键盘弹起
//        if(oldBottom != 0 && bottom != 0 &&(oldBottom - bottom > keyHeight)){
//
//
//        }else if(oldBottom != 0 && bottom != 0 &&(bottom - oldBottom > keyHeight)){
//            bottomlinear.setVisibility(View.GONE);
//            bottomdiscussedt.setText("");
//
//        }

    }

    /**
     * 播放所需权限判断权限
     */
    private void playPerssion(){
        Log.e("info","playPerssion......");

        rxPermissions.request(Manifest.permission.CAMERA)//这里填写所需要的权限
            .subscribe(new Action1<Boolean>() {
                @Override
                public void call(Boolean aBoolean) {
                    if (aBoolean) {//true表示获取权限成功（注意这里在android6.0以下默认为true）

                        Log.e("info","call......");

                        //先弹出激活框
                        boolean isJiaoZhun = SharedPrefsUtil.getValue(OnlineVideoInfoActivity.this, com.stereo.util.Constants.JIAOZHUNSUCCESS, false);
                        boolean isMopic = SharedPrefsUtil.getValue(OnlineVideoInfoActivity.this, Constants.Mopic, false);
                        Log.i("info", "isJiaoZhun " + isJiaoZhun);
                        if (isJiaoZhun||isMopic) {
                            playNetCheck();

                        }else{
                            activeDia();
                        }
                    } else {
                        cameradiaShow();
                    }
                }
            });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
//                    showProgressDialog();
                    progressDid();
                    //显示扫描到的内容
                    String sanString = data.getStringExtra("result").trim();
                    //调用激活接口激活
                    InteractionManager.getInstance(this, new ActionCallback() {
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
                             * 激活结果总次数
                             */
                            phoneNum = SharedPrefsUtil.getValue(getApplicationContext(), Constants.Phone_FLAG, Constants.Phone_default);
                            JSONObject personObject = null;
                            try {
                                personObject = new JSONObject();
                                personObject.put("phoneNum", phoneNum);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            ZhugeSDK.getInstance().track(getApplicationContext(),"激活结果总次数", personObject);

                            switch (returnCode){
                                case 1:
//                                    ToastUtil.showToast(getApplicationContext(), "激活成功,请开始眼球追踪校准", ToastUtil.CENTER);

                                    Intent intent = new Intent();
                                    intent.putExtra("activeSuccess",true);
                                    intent.setClass(getApplicationContext(), AngleActivity.class);
                                    startActivity(intent);

                                    /**
                                     * 激活成功
                                     */
                                    ZhugeSDK.getInstance().track(getApplicationContext(),"激活成功", personObject);
                                    ZhugeSDK.getInstance().track(getApplicationContext(),"进入校准界面总次数", personObject);

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
     * 激活对话框
     */
    private void activeDia(){
        View diaView = View.inflate(OnlineVideoInfoActivity.this, com.unbounded.video.R.layout.notice_dialog, null);
        activeDialog = new Dialog(OnlineVideoInfoActivity.this, com.unbounded.video.R.style.dialog);
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
        tonowbtn.setText(com.unbounded.video.R.string.tonow);

        setafterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activeDialog.dismiss();
                playNetCheck();
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
                phoneNum = SharedPrefsUtil.getValue(getApplicationContext(), Constants.Phone_FLAG, Constants.Phone_default);
                JSONObject personObject = null;
                try {
                    personObject = new JSONObject();
                    personObject.put("phoneNum", phoneNum);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ZhugeSDK.getInstance().track(OnlineVideoInfoActivity.this, "去校准", personObject);
            }
        });


        activeDialog.show();

        WindowManager.LayoutParams params = activeDialog.getWindow().getAttributes();
        params.width = screenWidth * 3 / 4;
        // params.height = 200 ;
        activeDialog.getWindow().setAttributes(params);
    }

    /**
     *
     */
    private void playNetCheck(){
        int netType = getNetState();

        if(netType == Constants.WIFI_FLAG){
            playbtnMethod();
        }else if(netType == Constants.GPR_FLAG){
            //数据流量弹出对话框
            gprDia();

        }else {
            ToastUtil.showToast(getApplicationContext(), com.unbounded.video.R.string.checkbet_word, ToastUtil.CENTER);
        }
    }

    /**
     * 下载方法
     */
    private void downloadMethod(){
        String path = DownloadService.getVideoDownPath(uploadUrl);

        if(TextUtils.isEmpty(userId)){
            openActivity(LoginActivity.class);
        }else {
            if (!(TextUtils.isEmpty(path))) {
                File file = new File(path);
                if (file.exists()) {
                    ToastUtil.showToast(OnlineVideoInfoActivity.this, com.unbounded.video.R.string.Word_havedownload, ToastUtil.CENTER);
                } else {
                    VideoDownBean videoDownBean = new VideoDownBean(videoId, uploadImg, name, uploadUrl, 0, 0, VideoDownBean.UNDOWNLOAD, uploadSize);
                    Intent intent = new Intent(OnlineVideoInfoActivity.this, DownloadService.class);
                    intent.putExtra("videoDownBean", videoDownBean);
                    startService(intent);
                }

            } else {
                VideoDownBean videoDownBean = new VideoDownBean(videoId, uploadImg, name, uploadUrl, 0, 0, VideoDownBean.UNDOWNLOAD, uploadSize);
                Intent intent = new Intent(OnlineVideoInfoActivity.this, DownloadService.class);
                intent.putExtra("videoDownBean", videoDownBean);
                startService(intent);
            }
        }

        /**
         * 下载
         */
        phoneNum = SharedPrefsUtil.getValue(getApplicationContext(), Constants.Phone_FLAG, Constants.Phone_default);
        JSONObject personObject = null;
        try {
            personObject = new JSONObject();
            personObject.put("phoneNum", phoneNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ZhugeSDK.getInstance().track(getApplicationContext(),"下载",personObject);
    }

    /**
     * 播放按钮
     */
    private void playbtnMethod(){
        long current = System.currentTimeMillis();
        String today = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(current);
        /*是否超过次数直接跳转注册*/
        boolean isAlertPlayerDialog = isAlertPlayerDialog();
        if(isAlertPlayerDialog){
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }else{
            boolean isMopic = SharedPrefsUtil.getValue(OnlineVideoInfoActivity.this, Constants.Mopic, false);
            if (isMopic) {
                Intent intent =new Intent(this,ExternalPlayerActivity.class);
                intent.putExtra("playType", "online");
                intent.putExtra("type2dor3d", type2dor3d);
                intent.putExtra("urlOrPath", uploadUrl);
                intent.putExtra("playName", name);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                addPlayNum();
            }else{
                VideoHistory videoHistory = new VideoHistory(videoId, userImg, userName, uploadImg, name, today, uploadUrl, 0, 4, 0);
//        addHistory(videoHistory);

                Intent intent = new Intent(this, MoviePlayActivity.class);
                intent.putExtra("playType", "online");
                intent.putExtra("type2dor3d", type2dor3d);
                intent.putExtra("urlOrPath", uploadUrl);
                intent.putExtra("playName", name);
                intent.putExtra("report", report);
                intent.putExtra("subtitle", subtitle);
                intent.putExtra("videoHistory", videoHistory);
                startActivity(intent);
                addPlayNum();
            }
        }
    }

    /**
     * 数据流量询问对话框
     */
    private void gprDia(){
        View diaView = View.inflate(OnlineVideoInfoActivity.this, com.unbounded.video.R.layout.notice_dialog, null);
        gprDialog = new Dialog(OnlineVideoInfoActivity.this, com.unbounded.video.R.style.dialog);
        gprDialog.setContentView(diaView);
        gprDialog.setCanceledOnTouchOutside(false);
        gprDialog.setCancelable(false);

        Button yesbtn = (Button) gprDialog.findViewById(com.unbounded.video.R.id.notice_yesbtn);
        Button exitbtn = (Button) gprDialog.findViewById(com.unbounded.video.R.id.notice_canclebtn);
        TextView titletv = (TextView) gprDialog.findViewById(com.unbounded.video.R.id.notice_tv1);
        TextView contenttv = (TextView) gprDialog.findViewById(com.unbounded.video.R.id.notice_tv2);

        yesbtn.setText(getString(com.unbounded.video.R.string.sure_btn));
        exitbtn.setText(getString(com.unbounded.video.R.string.cancle_btn));
        titletv.setText(getString(com.unbounded.video.R.string.Word_noticetitle));
        contenttv.setText(getString(com.unbounded.video.R.string.Word_gprdownload));


        yesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playbtnMethod();
                gprDialog.dismiss();
            }
        });
        //
        exitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gprDialog.dismiss();
            }
        });

        gprDialog.show();

        WindowManager.LayoutParams params = gprDialog.getWindow().getAttributes();
        params.width = screenWidth * 3 / 4;
        // params.height = 200 ;
        gprDialog.getWindow().setAttributes(params);

    }

    /**
     *
     */
    public void cameradiaShow(){
        if(goSetcameraDialog != null){
            if(!(goSetcameraDialog.isShowing())){
                goSetcameraDialog.show();
            }
        }else {
            goSetCameraDia();
        }
    }

    /**
     * 用户拒绝后，继续弹出对话框
     */
    public void goSetCameraDia(){
        View diaView = View.inflate(OnlineVideoInfoActivity.this, com.unbounded.video.R.layout.notice_dialog, null);
        goSetcameraDialog = new Dialog(OnlineVideoInfoActivity.this, com.unbounded.video.R.style.dialog);
        goSetcameraDialog.setContentView(diaView);
        goSetcameraDialog.setCanceledOnTouchOutside(false);
        goSetcameraDialog.setCancelable(false);

        Button toSetbtn = (Button) goSetcameraDialog.findViewById(com.unbounded.video.R.id.notice_yesbtn);
        Button exitbtn = (Button) goSetcameraDialog.findViewById(com.unbounded.video.R.id.notice_canclebtn);
        TextView titletv = (TextView) goSetcameraDialog.findViewById(com.unbounded.video.R.id.notice_tv1);
        TextView contenttv = (TextView) goSetcameraDialog.findViewById(com.unbounded.video.R.id.notice_tv2);

        toSetbtn.setText(getString(com.unbounded.video.R.string.Word_Set));
        exitbtn.setText(getString(com.unbounded.video.R.string.cancle_btn));
        titletv.setText(getString(com.unbounded.video.R.string.Word_noticetitle));
        contenttv.setText(getString(com.unbounded.video.R.string.Word_noticecamera));


        toSetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSet();
                goSetcameraDialog.dismiss();
            }
        });
        //
        exitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSetcameraDialog.dismiss();
            }
        });

        goSetcameraDialog.show();

        WindowManager.LayoutParams params = goSetcameraDialog.getWindow().getAttributes();
        params.width = screenWidth * 3 / 4;
        // params.height = 200 ;
        goSetcameraDialog.getWindow().setAttributes(params);
    }

    /**
     * 添加播放次数
     */
    private void addPlayNum(){
        String addnumurl = HttpConstants.addnumVideo_Url + "?id=" + videoId;
        HttpGetUtil get = new HttpGetUtil(handler, addnumurl, ADDNUM_FLAG);
        Thread thread = new Thread(get);
        thread.start();
    }

    /**
     * 添加进历史记录
     */
    private void addHistory(VideoHistory videoHistory){
        //首先播放要判断该视频是否在记录列表中
        historyJson = SharedPrefsUtil.getValue(getApplicationContext(), Constants.VIDEOHISTORY_FLAG, "");
        Log.e("info","历史记录111="+ historyJson);
        //首先没有记录
        if(TextUtils.isEmpty(historyJson)){
            //直接把记录加进去
            historylist.add(videoHistory);

            //如果有记录，要判断是否有一样的
        }else {
            historylist = JsonUtil.historyJsonToList(historyJson);
            for(int i=0;i<historylist.size();i++){
                //记录中有,删除以前的，插入新的
                if(videoId.equals(historylist.get(i).getId())){
                    historylist.remove(i);

                    //新的插到第一个
                    historylist.add(0, videoHistory);
                    break;
                    //当遍历到最后一个都没有相同记录时
                }else if(i == historylist.size() - 1){
                    //直接把记录加进去，插到第一个
                    historylist.add(0, videoHistory);
                    break;
                }
            }
        }
        //list存好后，转成json存起来
        historyJson = JsonUtil.objectToJson(historylist);
        SharedPrefsUtil.putValue(OnlineVideoInfoActivity.this, Constants.VIDEOHISTORY_FLAG, historyJson);
    }

    /**
     * 取消点赞
     */
    private void daleteLike(){
        keyList.clear();
        valueList.clear();

        keyList.add("userId");
        keyList.add("likeid");
        keyList.add("type");

        valueList.add(userId);
        valueList.add(videoId);
        valueList.add("video");

        String deletelikeurl = HttpConstants.deleteLike_Url;
        HttpPostUtil post = new HttpPostUtil(handler, deletelikeurl,keyList , valueList, CANCLELIKE_FLAG);
        Thread thread = new Thread(post);
        thread.start();
    }

    /**
     * 点赞
     */
    private void likeMethod(){
        keyList.clear();
        valueList.clear();

        keyList.add("userId");
        keyList.add("likeid");
        keyList.add("type");

        valueList.add(userId);
        valueList.add(videoId);
        valueList.add("video");

        String likeurl = HttpConstants.Like_Url;
        HttpPostUtil post = new HttpPostUtil(handler, likeurl,keyList , valueList, LIKE_FLAG);
        Thread thread = new Thread(post);
        thread.start();

        /**
         * 点赞
         */
        phoneNum = SharedPrefsUtil.getValue(getApplicationContext(), Constants.Phone_FLAG, Constants.Phone_default);
        JSONObject personObject = null;
        try {
            personObject = new JSONObject();
            personObject.put("phoneNum", phoneNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ZhugeSDK.getInstance().track(getApplicationContext(),"点赞",personObject);

    }

    /**
     * 关注
     */
    private void followMethod(){
        keyList.clear();
        valueList.clear();

        keyList.add("userId");
        keyList.add("followId");

        valueList.add(userId);
        valueList.add(authorId);

        String fillowurl = HttpConstants.Follow_Url;
        HttpPostUtil post = new HttpPostUtil(handler, fillowurl,keyList , valueList, FOLLOW_FLAG);
        Thread thread = new Thread(post);
        thread.start();
    }

    /**
     * 收藏
     */
    private void collectMethod(){
        keyList.clear();
        valueList.clear();

        keyList.add("collectorId");
        keyList.add("userId");
        keyList.add("collectorType");
        keyList.add("type");

        valueList.add(videoId);
        valueList.add(userId);
        valueList.add("视频");
        valueList.add("video");

        String collecturl = HttpConstants.Collect_Url;
        HttpPostUtil post = new HttpPostUtil(handler, collecturl,keyList , valueList, COLLECT_FLAG);
        Thread thread = new Thread(post);
        thread.start();

        /**
         * 收藏
         */
        phoneNum = SharedPrefsUtil.getValue(getApplicationContext(), Constants.Phone_FLAG, Constants.Phone_default);
        JSONObject personObject = null;
        try {
            personObject = new JSONObject();
            personObject.put("phoneNum", phoneNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ZhugeSDK.getInstance().track(getApplicationContext(),"收藏",personObject);

    }

    /**
     * 取消收藏
     */
    private void deleteCollectMethod(){
        keyList.clear();
        valueList.clear();

        keyList.add("collectorId");
        keyList.add("userId");

        valueList.add(videoId);
        valueList.add(userId);

        String deletecollecturl = HttpConstants.Deletecollect_Url;
        HttpPostUtil post = new HttpPostUtil(handler, deletecollecturl,keyList , valueList, DELETECOLLECT_FLAG);
        Thread thread = new Thread(post);
        thread.start();

        /**
         * 取消收藏
         */
        phoneNum = SharedPrefsUtil.getValue(getApplicationContext(), Constants.Phone_FLAG, Constants.Phone_default);
        JSONObject personObject = null;
        try {
            personObject = new JSONObject();
            personObject.put("phoneNum", phoneNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ZhugeSDK.getInstance().track(getApplicationContext(),"取消收藏",personObject);

    }
    /**
     * 评论
     */
    private void discussMethod(){
        keyList.clear();
        valueList.clear();

        keyList.add("userId");
        keyList.add("workId");
        keyList.add("type");
        keyList.add("comment");

        valueList.add(userId);
        valueList.add(videoId);
        valueList.add("video");
        valueList.add(discusscontent);

        String discussurl = HttpConstants.Discuss_Url;
        HttpPostUtil post = new HttpPostUtil(handler, discussurl, keyList, valueList, DISCUSS_FLAG);
        Thread thread = new Thread(post);
        thread.start();

    }


    /**
     * 一键分享方法
     */
    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle("标题");
        // titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(com.unbounded.video.R.string.app_name));

        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

        // 启动分享GUI
        oks.show(this);
    }

    public boolean isAlertPlayerDialog() {
        /*如果登录了，不需要跳转*/
        boolean isLogin = SharedPrefsUtil.getValue(this,
                Constants.IS_LOGIN, false);
        if(isLogin){
            return false;
        }
        long current = System.currentTimeMillis();
        int plyerNumber = 0;
        ACache aCache = ACache.get(this);
        String number = aCache.getAsString(Constants.PLAYER_NUMBER);
        if (number == null) {
            plyerNumber = 0;
        } else {
            plyerNumber = Integer.valueOf(number) + 1;
        }
        Log.v(TAG, "playbtnMethod() plyerNumber:" + plyerNumber);

        long zero = current / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset() + 1000 * 3600 * 24;//今天零点零分零秒的毫秒数
        int saveTime = (int) (zero - current) / 1000;
        Log.v(TAG, "playbtnMethod() saveTime:" + saveTime);
        aCache.put(Constants.PLAYER_NUMBER, plyerNumber + "", saveTime);
        return plyerNumber == PLAYER_NUMBER;

    }

    //执行授权,获取用户信息
//    private void authorize(Platform plat) {
//        if (plat.isAuthValid()) {
//            String userId = plat.getDb().getUserId();
//            if (!TextUtils.isEmpty(userId)) {
//                UIHandler.sendEmptyMessage(MSG_USERID_FOUND, this);
//                login(plat.getName(), userId, null);
//                return;
//            }
//        }
//        plat.setPlatformActionListener(this);
//        //true不使用SSO授权，false使用SSO授权
//        plat.SSOSetting(false);
//        plat.showUser(null);
//    }
//
//    private void login(String plat, String userId, HashMap<String, Object> userInfo) {
//        Message msg = new Message();
//        msg.what = MSG_LOGIN;
//        msg.obj = plat;
//        UIHandler.sendMessage(msg, this);
//    }

}
