package com.unbounded.video.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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

public class ThreeddVideoOnlineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;

    Context context;
    int imgWidth, imgHeight;
    String freetype;
    private List<OnlineVideo> list;
    private View mHeaderView;
    private OnItemClickListener mListener;

    public ThreeddVideoOnlineAdapter(Context context, List<OnlineVideo> list, View mHeaderView, int imgWidth, String freetype) {
        super();
        this.context = context;
        this.list = list;
        this.mHeaderView = mHeaderView;
        this.imgWidth = imgWidth;
        this.freetype = freetype;

        imgHeight = imgWidth * 300/508;
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
        View layout = LayoutInflater.from(parent.getContext()).inflate(com.unbounded.video.R.layout.threeddvideoonline_item, parent, false);
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

            if("null".equals(list.get(pos).getName())){
                ((Holder) viewHolder).nametv.setText(list.get(pos).getName());
            }else{
                ((Holder) viewHolder).nametv.setText(list.get(pos).getName());
            }

            if(TextUtils.isEmpty(list.get(pos).getTimeLong()) || "null".equals(list.get(pos).getTimeLong())){
                ((Holder) viewHolder).timeLongtv.setText(com.unbounded.video.R.string.Word_timelong);
            }else{
                if(list.get(pos).getTimeLong().endsWith("\"")){
                    ((Holder) viewHolder).timeLongtv.setText(list.get(pos).getTimeLong());
                }else{
                    ((Holder) viewHolder).timeLongtv.setText(list.get(pos).getTimeLong() + "\"");
                }

            }

            GlideLogic.glideLoadPic324(context, imgurl, ((Holder) viewHolder).img, imgWidth*12/16, imgHeight*12/16);


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
        TextView nametv,timeLongtv,trytv;

        public Holder(View itemView) {
            super(itemView);
            if(itemView == mHeaderView) return;

            imgrela = (RelativeLayout) itemView.findViewById(com.unbounded.video.R.id.threeddfragitem_ivrela);
            img = (ImageView) itemView.findViewById(com.unbounded.video.R.id.threeddfragitem_iv);
            nametv = (TextView) itemView.findViewById(com.unbounded.video.R.id.threeddfragitem_nametv);
            timeLongtv = (TextView) itemView.findViewById(com.unbounded.video.R.id.threeddfragitem_timelongtv);
            trytv = (TextView) itemView.findViewById(com.unbounded.video.R.id.threeddfragitem_trytv);

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
