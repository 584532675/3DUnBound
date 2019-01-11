package com.unbounded.video.adapter;

import java.util.ArrayList;
import java.util.List;
import com.unbounded.video.bean.OnlinePic;
import com.unbounded.video.utils.imageLoad.GlideLogic;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
	public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;

    Context context;
    int imgWidth;
    private List<OnlinePic> picList;
    private View mHeaderView;
    private OnItemClickListener mListener;

    public RecyclerAdapter(Context context,List<OnlinePic> picList, View mHeaderView, int imgWidth) {
		super();
		this.context = context;
		this.picList = picList;
		this.mHeaderView = mHeaderView;
		this.imgWidth = imgWidth;
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
        View layout = LayoutInflater.from(parent.getContext()).inflate(com.unbounded.video.R.layout.piclocal_item, parent, false);
        return new Holder(layout);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if(getItemViewType(position) == TYPE_HEADER) return;

        final int pos = getRealPosition(viewHolder);
        String imgurl;
        if(TextUtils.isEmpty(picList.get(pos).getThumbnail()) || "null".equals(picList.get(pos).getThumbnail())){
            imgurl = picList.get(pos).getUploadimg();
        }else{
            imgurl = picList.get(pos).getThumbnail();
        }


        
        if(viewHolder instanceof Holder) {
            if(mListener == null) return;
            viewHolder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(pos);
                }
            });
            
            LayoutParams params = (LayoutParams) ((Holder) viewHolder).img.getLayoutParams();
    		params.height=imgWidth * 3/4;
    		params.width =imgWidth;
    		((Holder) viewHolder).img.setLayoutParams(params);

//            Log.e("info","imgurl="+imgurl);
			GlideLogic.glideLoadPic423(context, imgurl, ((Holder) viewHolder).img, imgWidth*13/16, (imgWidth * 3/4)*13/16);

    		
        }
    }

    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getPosition();
        return mHeaderView == null ? position : position - 1;
    }

    @Override
    public int getItemCount() {
        return mHeaderView == null ? picList.size() : picList.size() + 1;
    }

    class Holder extends RecyclerView.ViewHolder {
    	ImageView img;

        public Holder(View itemView) {
            super(itemView);
            if(itemView == mHeaderView) return;
            
    		img = (ImageView) itemView.findViewById(com.unbounded.video.R.id.piclocalitem_imgiv);

    		LayoutParams params = (LayoutParams) img.getLayoutParams();
    		params.height=imgWidth * 3/4;
    		params.width =imgWidth;
			img.setLayoutParams(params);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
