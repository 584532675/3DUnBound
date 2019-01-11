package com.unbounded.video.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.unbounded.video.bean.OnlineVideo;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.utils.imageLoad.GlideLogic;

import java.util.List;

/**
 * Created by zjf on 2017/7/24 0024.
 */

public class VideoOnlineAdapter extends BaseAdapter{
    Fragment fragment;
    Context context;
    List<OnlineVideo> list;
    int imgWidth,imgHeight,oneDp,headviewWidth,screenHeight;

    public VideoOnlineAdapter(Fragment fragment, Context context, List<OnlineVideo> list, int imgWidth, int oneDp, int screenHeight) {
        this.fragment = fragment;
        this.context = context;
        this.list = list;
        this.imgWidth = imgWidth;
        this.oneDp = oneDp;
        this.screenHeight = screenHeight;
    }
    public VideoOnlineAdapter(Context context, List<OnlineVideo> list, int imgWidth, int oneDp) {
        this.context = context;
        this.list = list;
        this.imgWidth = imgWidth;
        this.oneDp = oneDp;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
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
            view = LayoutInflater.from(context).inflate(com.unbounded.video.R.layout.videoonline_item, null);
            viewHolder.filmimgrela = (RelativeLayout) view.findViewById(com.unbounded.video.R.id.videoonlineitem_imgrela);
            viewHolder.filmimg = (ImageView)view.findViewById(com.unbounded.video.R.id.videolocalitem_imgiv);
            viewHolder.timelong = (TextView) view.findViewById(com.unbounded.video.R.id.videoonlineitem_timelongtv);
            viewHolder.headview = (ImageView)view.findViewById(com.unbounded.video.R.id.videoonlineitem_headviewiv);
            viewHolder.filmnametv = (TextView) view.findViewById(com.unbounded.video.R.id.videoonlineitem_filmnametv);
            viewHolder.authornametv = (TextView) view.findViewById(com.unbounded.video.R.id.videoonlineitem_authornametv);
            viewHolder.playnumtv = (TextView) view.findViewById(com.unbounded.video.R.id.videoonlineitem_playcounttv);

            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)view.getTag();
        }

        if(imgHeight == Constants.ZERO){
            imgHeight = screenHeight * 29/100;
        }

        if(headviewWidth == Constants.ZERO){
            headviewWidth = 44*oneDp*11/16;
        }

        ViewGroup.LayoutParams params = viewHolder.filmimgrela.getLayoutParams();
        params.width =imgWidth;
        params.height =imgHeight;
        viewHolder.filmimgrela.setLayoutParams(params);

        ViewGroup.LayoutParams params1 = viewHolder.filmimg.getLayoutParams();
        params1.width =imgWidth;
        params1.height =imgHeight;
        viewHolder.filmimg.setLayoutParams(params1);

        if(TextUtils.isEmpty(list.get(position).getTimeLong())){
            viewHolder.timelong.setText(com.unbounded.video.R.string.Word_timelong);
        }else {
            if(list.get(position).getTimeLong().endsWith("\"")){
                viewHolder.timelong.setText(list.get(position).getTimeLong());
            }else{
                viewHolder.timelong.setText(list.get(position).getTimeLong() + "\"");
            }

        }

        viewHolder.filmnametv.setText(list.get(position).getName());
        viewHolder.authornametv.setText(list.get(position).getUserName());
        viewHolder.playnumtv.setText(list.get(position).getOpencount() + context.getString(com.unbounded.video.R.string.Word_playernum));

        //头像
//        GlideLogic.glideLoadHeadPicWithFragment(fragment, list.get(position).getUploadimg(), viewHolder.headview, headviewWidth, headviewWidth);
        GlideLogic.glideLoadHeadPic(context, list.get(position).getUserImg(), viewHolder.headview, headviewWidth, headviewWidth);

        //视频图
//        GlideLogic.glideLoadPicWithFragment423(fragment, list.get(position).getUploadimg(), viewHolder.filmimg, imgWidth * 11/16, imgHeight * 11/16);
        GlideLogic.glideLoadPic423(context, list.get(position).getUploadimg(), viewHolder.filmimg, imgWidth * 13/16, imgHeight * 13/16);

        return view;
    }

    class ViewHolder{
        RelativeLayout filmimgrela;
        ImageView filmimg;
        TextView timelong;
        ImageView headview;
        TextView filmnametv;
        TextView authornametv;
        TextView playnumtv;
    }
}
