package com.unbounded.video.view.banner.gallerybanner;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;  
import android.widget.BaseAdapter;  
import android.widget.Gallery;
import android.widget.ImageView;  
import android.widget.ImageView.ScaleType;

import com.unbounded.video.bean.BannerImg;
import com.unbounded.video.utils.imageLoad.GlideLogic;
import com.unbounded.video.view.banner.RoundImageView;

import java.util.List;

public class ImageAdapter extends BaseAdapter {
	private Context mContext;
    List<BannerImg> banners;
    int screenWidth,bannerimgWidth,bannerHeight;
  
    public ImageAdapter(Context c, List<BannerImg> banners, int bannerimgWidth, int bannerHeight, int screenWidth)
    {  
        this.mContext = c;
        this.banners = banners;
        this.bannerimgWidth = bannerimgWidth;
        this.bannerHeight = bannerHeight;
        this.screenWidth = screenWidth;
    }
  
    public int getCount()  
    {  
        return Integer.MAX_VALUE;  
    }
  
    public Object getItem(int position)  
    {  
        return position;  
    }  
  
    public long getItemId(int position)  
    {  
        return position;  
    }  
  

    public View getView(int position, View convertView, ViewGroup parent)  
    {  
        if (convertView == null) {
            convertView = new ImageView(mContext);
            ((ImageView) convertView).setScaleType(ScaleType.FIT_XY);
            convertView.setLayoutParams(new Gallery.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        }

        GlideLogic.glideLoadPic423(mContext, banners.get(position % banners.size()).getImgurl(), ((ImageView) convertView), bannerimgWidth * 13/16, bannerHeight * 13/16);
        return convertView;
    }  
  
    public float getScale(boolean focused, int offset)  
    {  
    	return 1f;
    }  
}
