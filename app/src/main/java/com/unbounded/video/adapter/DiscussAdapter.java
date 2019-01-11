package com.unbounded.video.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.unbounded.video.bean.Discuss;
import com.unbounded.video.utils.imageLoad.GlideLogic;

import java.util.List;

/**
 * Created by zjf on 2017/7/25 0025.
 */

public class DiscussAdapter extends BaseAdapter {
    Context context;
    List<Discuss> list;
    int oneDp;

    public DiscussAdapter(Context context, List<Discuss> list, int oneDp) {
        this.context = context;
        this.list = list;
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
            view = LayoutInflater.from(context).inflate(com.unbounded.video.R.layout.discuss_item, null);
            viewHolder.headimg = (ImageView)view.findViewById(com.unbounded.video.R.id.discussitem_headiv);
            viewHolder.nametv = (TextView) view.findViewById(com.unbounded.video.R.id.discussitem_nametv);
            viewHolder.contenttv = (TextView) view.findViewById(com.unbounded.video.R.id.discussitem_contenttv);
            viewHolder.timetv = (TextView) view.findViewById(com.unbounded.video.R.id.discussitem_timetv);

            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)view.getTag();
        }

        viewHolder.nametv.setText(list.get(position).getUserName());
        viewHolder.contenttv.setText(list.get(position).getComment());
        viewHolder.timetv.setText(timeTo(list.get(position).getCommentTime()));

        GlideLogic.glideLoadHeadPic(context, list.get(position).getUserImg(), viewHolder.headimg, 48*oneDp, 48*oneDp);

        return view;
    }

    class ViewHolder{
        ImageView headimg;
        TextView nametv;
        TextView contenttv;
        TextView timetv;
    }

    /**
     * 使用String的split 方法
     */
    public String timeTo(String str){
        String str1 = str;
        String time = str1.substring(0, 10);
        time = time.replace("年", "-");
        time = time.replace("月", "-");
        return time;
    }
}
