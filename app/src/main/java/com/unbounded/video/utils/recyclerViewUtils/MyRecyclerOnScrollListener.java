package com.unbounded.video.utils.recyclerViewUtils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by zjf on 2017/7/31 0031.
 */

public abstract class MyRecyclerOnScrollListener extends RecyclerView.OnScrollListener{
    int lastItem, totalItem;
    private int currentPage = 1;

    private LinearLayoutManager mLinearLayoutManager;

    public MyRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager, int currentPage) {
        this.mLinearLayoutManager = linearLayoutManager;
        this.currentPage = currentPage;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int lastVisiableItemPosition = mLinearLayoutManager.findLastVisibleItemPosition();
        lastItem = lastVisiableItemPosition+1;
        totalItem = mLinearLayoutManager.getItemCount();


    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        if(totalItem == lastItem&&newState == 0){
            onLoadMore(currentPage);
        }

    }

    public abstract void onLoadMore(int currentP);
}
