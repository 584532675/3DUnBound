package com.unbounded.video.adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by zjf on 2017/11/20 0020.
 */

public class PicItemDecoration extends RecyclerView.ItemDecoration{
    private int space;

    public PicItemDecoration(int space) {
        this.space=space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //不是第一个的格子都设一个左边和底部的间距
//        int pos = parent.getChildAdapterPosition(view);
        int pos = parent.getChildPosition(view);

        if (pos % 2 == 0) {  //左边
            outRect.top = 2* space;
            outRect.right = space;
        } else if(pos % 2 == 1){ //右边
            outRect.top = 2* space;
            outRect.left = space;
        }

    }
}
