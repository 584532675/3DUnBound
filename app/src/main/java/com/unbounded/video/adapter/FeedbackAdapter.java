package com.unbounded.video.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.unbounded.video.utils.imageLoad.GlideLogic;

import java.util.List;

/**
 * Created by zjf on 2017/7/28 0028.
 */

public class FeedbackAdapter extends BaseAdapter {
    Context context;
    List<String> list;
    int imgWidth;

    public FeedbackAdapter(Context context, List<String> list, int imgWidth) {
        this.context = context;
        this.list = list;
        this.imgWidth = imgWidth;
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
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
      ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(com.unbounded.video.R.layout.piclocal_item, null);
            viewHolder.img = (ImageView)convertView.findViewById(com.unbounded.video.R.id.piclocalitem_imgiv);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }


        ViewGroup.LayoutParams params = viewHolder.img.getLayoutParams();
        params.width =imgWidth;
        params.height =imgWidth;
        viewHolder.img.setLayoutParams(params);

        if(!(TextUtils.isEmpty(list.get(position)))){
            if(position == list.size() - 1){
                Glide.with(context).load(com.unbounded.video.R.mipmap.post_pitcture).diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .override(imgWidth*11/16,imgWidth*11/16)
                        .fitCenter()
                    /*.crossFade()
                    .centerCrop()*/
                        .into(viewHolder.img);
            }else{
                //指定大小压缩,节省资源
                GlideLogic.glideLoadPic423(context, list.get(position), viewHolder.img, imgWidth*11/16, imgWidth*11/16);
            }
        }


        return convertView;
    }

    class ViewHolder{
        ImageView img;
    }
}
