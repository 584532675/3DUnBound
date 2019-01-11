package com.unbounded.video.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.unbounded.video.bean.OnlinePic;
import com.unbounded.video.bean.OnlineVideo;
import com.unbounded.video.utils.imageLoad.GlideLogic;

import java.util.ArrayList;
import java.util.List;

public class AllfilmRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
	public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;

    Context context;
    int imgWidth, imgHeight;
    String freetype;
    private List<OnlineVideo> list;
    private View mHeaderView;
    private OnItemClickListener mListener;

    public AllfilmRecyclerAdapter(Context context, List<OnlineVideo> list, View mHeaderView, int imgWidth, String freetype) {
		super();
		this.context = context;
		this.list = list;
		this.mHeaderView = mHeaderView;
		this.imgWidth = imgWidth;
        this.freetype = freetype;

        imgHeight = imgWidth * 434/333;
	}

	public void setOnItemClickListener(OnItemClickListener li) {
        mListener = li;
    }

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public void addDatas(ArrayList<OnlinePic> picList) {
		picList.addAll(picList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(mHeaderView == null) return TYPE_NORMAL;
        if(position == 0) return TYPE_HEADER;
        return TYPE_NORMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mHeaderView != null && viewType == TYPE_HEADER) return new Holder(mHeaderView);
        View layout = LayoutInflater.from(parent.getContext()).inflate(com.unbounded.video.R.layout.threeddallfilm_item, parent, false);
        return new Holder(layout);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if(getItemViewType(position) == TYPE_HEADER) return;

        if("free".equals(freetype)){
            //免费
            ((Holder) viewHolder).trytv.setVisibility(View.GONE);
        }else if("notFree".equals(freetype)){
            //收费
            ((Holder) viewHolder).trytv.setVisibility(View.VISIBLE);
        }

        final int pos = getRealPosition(viewHolder);
        String imgurl;

        imgurl = list.get(pos).getUploadimg();
        Log.e("info","imgurl="+imgurl);

        if(viewHolder instanceof Holder) {
            if(mListener == null) return;
            viewHolder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(pos);
                }
            });
            
            LayoutParams params = (LayoutParams) ((Holder) viewHolder).img.getLayoutParams();
    		params.height=imgHeight;
    		params.width =imgWidth;
    		((Holder) viewHolder).img.setLayoutParams(params);

            LayoutParams params1 = (LayoutParams) ((Holder) viewHolder).imgrela.getLayoutParams();
            params1.height=imgHeight;
            params1.width =imgWidth;
            ((Holder) viewHolder).imgrela.setLayoutParams(params1);

            ((Holder) viewHolder).nametv.setText(list.get(pos).getName());
			GlideLogic.glideLoadPic324(context, imgurl, ((Holder) viewHolder).img, imgWidth*11/16, imgHeight*11/16);

    		
        }
    }

    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getPosition();
        return mHeaderView == null ? position : position - 1;
    }

    @Override
    public int getItemCount() {
        return mHeaderView == null ? list.size() : list.size() + 1;
    }

    class Holder extends RecyclerView.ViewHolder {
        RelativeLayout imgrela;
    	ImageView img;
        TextView nametv,trytv;

        public Holder(View itemView) {
            super(itemView);
            if(itemView == mHeaderView) return;
            
    		imgrela = (RelativeLayout) itemView.findViewById(com.unbounded.video.R.id.threeddallfilmitem_imgrela);
    		img = (ImageView) itemView.findViewById(com.unbounded.video.R.id.threeddallfilmitem_imgiv);
            nametv = (TextView) itemView.findViewById(com.unbounded.video.R.id.threeddallfilmitem_fimanametv);
            trytv = (TextView) itemView.findViewById(com.unbounded.video.R.id.threeddallfilmitem_trytv);

    		LayoutParams params = (LayoutParams) img.getLayoutParams();
    		params.height=imgHeight;
    		params.width =imgWidth;
			img.setLayoutParams(params);

            LayoutParams params1 = (LayoutParams) imgrela.getLayoutParams();
            params1.height=imgHeight;
            params1.width =imgWidth;
            imgrela.setLayoutParams(params1);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
