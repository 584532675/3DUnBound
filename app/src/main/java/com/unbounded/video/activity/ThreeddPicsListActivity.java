package com.unbounded.video.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.unbounded.video.BaseActivity;
import com.unbounded.video.adapter.PiclistRecyclerAdapter;
import com.unbounded.video.adapter.SpacesItemDecoration;
import com.unbounded.video.bean.ThreeddPic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjf on 2017/9/2.
 */

public class ThreeddPicsListActivity extends BaseActivity {
    RelativeLayout topimgrela;
    ImageView topimg;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    List<ThreeddPic> piclist = new ArrayList<ThreeddPic>();
    PiclistRecyclerAdapter recyclerAdapter;
    View headview;
    int imgwidth;

    @Override
    protected int getContentView() {
        return com.unbounded.video.R.layout.activity_threeddpiclist;
    }

    @Override
    public void init(Intent intent) {
        super.init(intent);

        piclist.add(new ThreeddPic("1", "蒸发太平洋", ""));
        piclist.add(new ThreeddPic("2", "蒸发太平洋", ""));
        piclist.add(new ThreeddPic("3", "蒸发太平洋", ""));
        piclist.add(new ThreeddPic("4", "蒸发太平洋", ""));
        piclist.add(new ThreeddPic("5", "蒸发太平洋", ""));

        imgwidth = (screenWidth - 32*oneDp)/2;
    }

    @Override
    public void initView() {
        super.initView();

        headview = View.inflate(ThreeddPicsListActivity.this, com.unbounded.video.R.layout.threeddpiclist_head, null);
        topimgrela = (RelativeLayout) headview.findViewById(com.unbounded.video.R.id.threeddpiclisthead_rela1);
        topimg = (ImageView) headview.findViewById(com.unbounded.video.R.id.threeddpiclisthead_img);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(com.unbounded.video.R.id.threeddpiclist_swiperefresh);
        recyclerView = (RecyclerView) findViewById(com.unbounded.video.R.id.threeddpiclist_recyclerView);
        recyclerAdapter = new PiclistRecyclerAdapter(ThreeddPicsListActivity.this, piclist, headview,imgwidth);

    }

    @Override
    public void initParams() {
        super.initParams();

        ViewGroup.LayoutParams params = topimgrela.getLayoutParams();
        params.height = screenWidth * 432/ 1032;
        topimgrela.setLayoutParams(params);

        ViewGroup.LayoutParams params1 = topimg.getLayoutParams();
        params1.height = screenWidth * 432/ 1032;
        topimg.setLayoutParams(params1);

    }

    @Override
    public void initEvent() {
        super.initEvent();

        final GridLayoutManager mGridLayoutManager=new GridLayoutManager(ThreeddPicsListActivity.this,2,GridLayoutManager.VERTICAL,false);//设置为一个2列的纵向网格布局
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(position == 0){
                    return 2;
                }else{
                    return 1;
                }

            }
        });
        //设置布局管理器
        recyclerView.setLayoutManager(mGridLayoutManager);
        SpacesItemDecoration decoration = new SpacesItemDecoration(4*oneDp);
        recyclerView.addItemDecoration(decoration);
        //设置为垂直布局，这也是默认的
        //layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置Adapter
        recyclerView.setAdapter(recyclerAdapter);
        //设置增加或删除条目的动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerAdapter.setOnItemClickListener(new PiclistRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.e("info", "点击第" + position + "个");

            }
        });
    }
}
