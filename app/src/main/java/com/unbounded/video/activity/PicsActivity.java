package com.unbounded.video.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unbounded.video.BaseActivity;
import com.unbounded.video.BaseFragment;
import com.unbounded.video.fragment.PicFilmFragment;
import com.unbounded.video.fragment.PicRecommendFragment;
import com.unbounded.video.utils.ExitApplication;
import com.unbounded.video.utils.FileDownloaderManager;
import com.unbounded.video.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjf on 2017/7/17.
 */

public class PicsActivity extends BaseActivity {
    private TextView recommendtv,filmtv,originaltv,recommendtagtv,filmtagtv,originaltagtv;
    Button historybtn,seekbtn;
    LinearLayout tabslinear;
    private ViewPager viewPager;
    private int currenttab;
    private List<Fragment> fragmentList;
    BaseFragment recommendFrag,filmFrag,originalFrag;
    private long LastClickTime = 0;

    @Override
    protected int getContentView() {
        return com.unbounded.video.R.layout.activity_pics;
    }

    @Override
    public void init(Intent intent) {
        super.init(intent);

        setTitleViewVisible(false);

        fragmentList = new ArrayList<Fragment>();
        recommendFrag = new PicRecommendFragment();
        filmFrag = new PicFilmFragment();
//        originalFrag = new PicOriginalFragment();
//        originalFrag = new PicLocalFragment();
        fragmentList.add(recommendFrag);
        fragmentList.add(filmFrag);
//        fragmentList.add(originalFrag);
    }

    @Override
    public void initView() {
        super.initView();

        tabslinear = (LinearLayout) findViewById(com.unbounded.video.R.id.videos_tabs);
        recommendtv = (TextView) findViewById(com.unbounded.video.R.id.videos_recommendtv);
        filmtv = (TextView) findViewById(com.unbounded.video.R.id.videos_filmtv);
        originaltv = (TextView) findViewById(com.unbounded.video.R.id.videos_originaltv);
        viewPager = (ViewPager) findViewById(com.unbounded.video.R.id.videos_viewpager);

        recommendtagtv = (TextView) findViewById(com.unbounded.video.R.id.videos_recommendtagtv);
        filmtagtv = (TextView) findViewById(com.unbounded.video.R.id.videos_filmtagtv);
        originaltagtv = (TextView) findViewById(com.unbounded.video.R.id.videos_originaltagtv);
        historybtn = (Button) findViewById(com.unbounded.video.R.id.videos_historybtn);
        seekbtn = (Button) findViewById(com.unbounded.video.R.id.videos_seekbtn);


        historybtn.setVisibility(View.VISIBLE);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            ((RelativeLayout)findViewById(R.id.activity_pics)).setPadding(0, SharedPrefsUtil.getStatusHeight(this), 0,0);
//        }
    }

    @Override
    public void initParams() {
        super.initParams();

        ViewGroup.LayoutParams params = tabslinear.getLayoutParams();
        params.height = screenHeight * 65/ 1210;
        tabslinear.setLayoutParams(params);

    }

    @Override
    public void initEvent() {
        super.initEvent();
        viewPager.setAdapter(new MyFrageStatePagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(fragmentList.size());
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());//页面变化时的监听器

        recommendtv.setOnClickListener(this);
        filmtv.setOnClickListener(this);
        originaltv.setOnClickListener(this);
        seekbtn.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * viewpager适配器
     */
    class MyFrageStatePagerAdapter extends FragmentStatePagerAdapter {

        public MyFrageStatePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public Object instantiateItem(View arg0, int arg1) {

            return fragmentList.get(arg1);
        }

        /*
         *
         */
        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);
            //
            int currentItem = viewPager.getCurrentItem();
            if (currentItem == currenttab) {
                return;
            }
        }
    }

    /**
     * 监听viewpager变化，当前页
     */
    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageSelected(int arg0) {
            changeView(arg0);
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case com.unbounded.video.R.id.videos_recommendtv:
                changeCurrentItem(0);
                break;
            case com.unbounded.video.R.id.videos_filmtv:
                changeCurrentItem(1);
                break;
            case com.unbounded.video.R.id.videos_originaltv:
                changeCurrentItem(2);
                break;
            case com.unbounded.video.R.id.videos_seekbtn:
                openActivity(SearchPicActivity.class);
                break;
        }
    }

    /**
     * 分类选项点击方法
     */
    private void changeCurrentItem(int desTab) {
        viewPager.setCurrentItem(desTab, true);
        changeView(desTab);
    }

    private void changeView(int desTab) {
        recommendtv.setTextColor(getResources().getColor(com.unbounded.video.R.color.white));
        filmtv.setTextColor(getResources().getColor(com.unbounded.video.R.color.white));
        originaltv.setTextColor(getResources().getColor(com.unbounded.video.R.color.white));

        recommendtagtv.setBackgroundResource(com.unbounded.video.R.color.transparent);
        filmtagtv.setBackgroundResource(com.unbounded.video.R.color.transparent);
        originaltagtv.setBackgroundResource(com.unbounded.video.R.color.transparent);

        if(desTab == 0){
            recommendtv.setTextColor(getResources().getColor(com.unbounded.video.R.color.white));
            recommendtagtv.setBackgroundResource(com.unbounded.video.R.drawable.yuanjiaowhite2_border);
        }else if(desTab == 1){
            filmtv.setTextColor(getResources().getColor(com.unbounded.video.R.color.white));
            filmtagtv.setBackgroundResource(com.unbounded.video.R.drawable.yuanjiaowhite2_border);
        }else if(desTab == 2){
            originaltv.setTextColor(getResources().getColor(com.unbounded.video.R.color.white));
            originaltagtv.setBackgroundResource(com.unbounded.video.R.drawable.yuanjiaowhite2_border);
        }
    }

    @Override
    public void onBackPressed()
    {
        if (LastClickTime <= 0)
        {
            ToastUtil.showToast(PicsActivity.this, com.unbounded.video.R.string.onemoreexit, ToastUtil.BOTTOM);
            LastClickTime = System.currentTimeMillis();

        }
        else
        {
            long currentClickTime = System.currentTimeMillis();
            if ((currentClickTime - LastClickTime) < 3000)
            {
                //暂停所有下载
                FileDownloaderManager.getInstance().pauseDownLoadAllFile();
                ExitApplication.getInstance().exit(this);

            }
            else
            {
                ToastUtil.showToast(PicsActivity.this, com.unbounded.video.R.string.onemoreexit, ToastUtil.BOTTOM);
                LastClickTime = currentClickTime;
            }
        }
    }
}
