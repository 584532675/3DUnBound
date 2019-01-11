package com.unbounded.video.adapter;

import android.content.Context;
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
 * Created by zjf on 2017/8/3 0003.
 */

public class SearchFilmAdapter extends BaseAdapter {
    Context context;
    List<OnlineVideo> list;
    int screenWidth, imgWidth, imgHeight;

    public SearchFilmAdapter(Context context, List<OnlineVideo> list, int screenWidth) {
        this.context = context;
        this.list = list;
        this.screenWidth = screenWidth;
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
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(com.unbounded.video.R.layout.searchfilm_item, null);
            viewHolder.imgrela = (RelativeLayout) convertView.findViewById(com.unbounded.video.R.id.searchfilmitem_imgrela);
            viewHolder.img = (ImageView) convertView.findViewById(com.unbounded.video.R.id.searchfilmitem_imgiv);
            viewHolder.playiv = (ImageView) convertView.findViewById(com.unbounded.video.R.id.searchfilmitem_playiv);
            viewHolder.nametv = (TextView) convertView.findViewById(com.unbounded.video.R.id.searchfilmitem_nametv);
            viewHolder.timetv = (TextView) convertView.findViewById(com.unbounded.video.R.id.searchfilmitem_timetv);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (imgWidth == Constants.ZERO || imgHeight == Constants.ZERO) {
            imgWidth = screenWidth * 3 / 10;
            imgHeight = imgWidth * 3 / 4;
        }

        ViewGroup.LayoutParams params = viewHolder.img.getLayoutParams();
        params.width = imgWidth;
        params.height = imgHeight;
        viewHolder.img.setLayoutParams(params);

        ViewGroup.LayoutParams params1 = viewHolder.imgrela.getLayoutParams();
        params1.width = imgWidth;
        params1.height = imgHeight;
        viewHolder.imgrela.setLayoutParams(params1);

        OnlineVideo onlineVideo = list.get(position);

        viewHolder.nametv.setText(onlineVideo.getName());
        if(!(TextUtils.isEmpty(onlineVideo.getIntroduction()))){
            viewHolder.timetv.setText(context.getString(com.unbounded.video.R.string.word_filmtype) + onlineVideo.getIntroduction());
        }

        GlideLogic.glideLoadPic423(context, onlineVideo.getUploadimg(), viewHolder.img, imgWidth, imgHeight);

        return convertView;
    }

    class ViewHolder {
        RelativeLayout imgrela;
        ImageView img;
        ImageView playiv;
        TextView nametv;
        TextView timetv;

    }
}
