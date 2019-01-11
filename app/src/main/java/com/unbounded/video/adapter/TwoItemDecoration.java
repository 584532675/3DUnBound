package com.unbounded.video.adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by zjf on 2017/11/20 0020.
 */

public class TwoItemDecoration extends RecyclerView.ItemDecoration{
    private int space;

    public TwoItemDecoration(int space) {
        this.space=space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //不是第一个的格子都设一个左边和底部的间距
//        int pos = parent.getChildAdapterPosition(view);
        int pos = parent.getChildPosition(view);

        if (pos % 2 == 0) {  //下面一行
            outRect.right = space;
        } else if(pos % 2 == 1){ //上面一行
            outRect.left = space;
        }

    }
}
