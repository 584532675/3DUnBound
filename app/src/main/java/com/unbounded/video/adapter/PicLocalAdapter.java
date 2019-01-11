package com.unbounded.video.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.unbounded.video.bean.LocalPic;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.utils.imageLoad.GlideLogic;

import java.util.List;

/**
 * Created by zjf on 2017/7/21.
 */

public class PicLocalAdapter extends BaseAdapter {
    private Fragment fragment;
    private Context context;
    private List<LocalPic> list;
    private int imgWidth,imgHeight;

    public PicLocalAdapter(Fragment fragment, Context context, List<LocalPic> list, int imgWidth) {
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
            viewHolder.imgrela = (RelativeLayout) convertView.findViewById(com.unbounded.video.R.id.piclocalitem_imgrela);
            viewHolder.img = (ImageView)convertView.findViewById(com.unbounded.video.R.id.piclocalitem_imgiv);
            viewHolder.cameraiv = (ImageView)convertView.findViewById(com.unbounded.video.R.id.piclocalitem_cameraiv);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        if(imgHeight == Constants.ZERO){
            imgHeight = imgWidth * 3/4;
        }

        ViewGroup.LayoutParams params = viewHolder.imgrela.getLayoutParams();
        params.width =imgWidth;
        params.height =imgHeight;
        viewHolder.imgrela.setLayoutParams(params);

        ViewGroup.LayoutParams params1 = viewHolder.img.getLayoutParams();
        params1.width =imgWidth;
        params1.height =imgHeight;
        viewHolder.img.setLayoutParams(params1);

        if(position == Constants.ZERO){
            viewHolder.img.setVisibility(View.GONE);
            viewHolder.cameraiv.setVisibility(View.VISIBLE);

        }else {
            viewHolder.img.setVisibility(View.VISIBLE);
            viewHolder.cameraiv.setVisibility(View.GONE);

            //指定大小压缩,节省资源
            GlideLogic.glideLoadPicWithFragment423(fragment, list.get(position).getPath(), viewHolder.img, imgWidth*13/16, imgHeight*13/16);
        }

        return convertView;
    }

    class ViewHolder{
        RelativeLayout imgrela;
        ImageView img;
        ImageView cameraiv;
    }
}