package com.unbounded.video.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.unbounded.video.R;

import java.util.List;

/**
 * Created by Administrator on 2017/8/10.
 */

public class PoplvAdapter extends BaseAdapter {
    Context context;
    List<String> list;

    public PoplvAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.switchbtnspop_item, null);
            viewHolder.tv = (TextView)convertView.findViewById(R.id.switchbtnsitem_tv);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.tv.setText(list.get(position));

        return convertView;
    }

    class ViewHolder{
        TextView tv;
    }
}
