package com.unbounded.video.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.unbounded.video.bean.OnlinePic;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.utils.imageLoad.GlideLogic;

import java.util.List;

/**
 * Created by zjf on 2017/7/21.
 */

public class PicOnlineAdapter extends BaseAdapter {
    private Fragment fragment;
    private Context context;
    private List<OnlinePic> list;
    private int imgWidth,imgHeight;

    public PicOnlineAdapter(Fragment fragment, Context context, List<OnlinePic> list, int imgWidth) {
        this.context = context;
        this.fragment = fragment;
        this.list = list;
        this.imgWidth = imgWidth;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(com.unbounded.video.R.layout.piclocal_item, null);
            viewHolder.img = (ImageView)convertView.findViewById(com.unbounded.video.R.id.piclocalitem_imgiv);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        if(imgHeight == Constants.ZERO){
            imgHeight = imgWidth * 3/4;
        }

        ViewGroup.LayoutParams params = viewHolder.img.getLayoutParams();
        params.width =imgWidth;
        params.height =imgHeight;
        viewHolder.img.setLayoutParams(params);

        //指定大小压缩,节省资源
//        GlideLogic.glideLoadPic(context, list.get(position).getPath(), viewHolder.img, imgWidth, imgHeight);
        GlideLogic.glideLoadPicWithFragment423(fragment, list.get(position).getUploadimg(), viewHolder.img, imgWidth*11/16, imgHeight*11/16);

        return convertView;
    }

    class ViewHolder{
        ImageView img;
    }
}
