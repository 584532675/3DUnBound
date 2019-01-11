package com.unbounded.video.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.unbounded.video.BaseActivity;
import com.unbounded.video.BaseFragment;
import com.unbounded.video.fragment.PicLocalFragment;
import com.unbounded.video.fragment.VideoLocalFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjf on 2017/8/31 0031.
 */

public class LocalVideoActivity extends BaseActivity {
    TextView videotv;
    RelativeLayout titlerela;
    ViewPager viewPager;
    private int currenttab;
    private List<Fragment> fragmentList;
    private BaseFragment localfilmfrag,localimagefrag;
    MyFrageStatePagerAdapter pagerAdapter;

    @Override
    protected int getContentView() {
        return com.unbounded.video.R.layout.activity_video_local;
    }

    @Override
    public void init(Intent intent) {
        super.init(intent);

        setTitleViewVisible(false);

        fragmentList = new ArrayList<Fragment>();
        localfilmfrag = new VideoLocalFragment();
        fragmentList.add(localfilmfrag);

    }

    @Override
    public void initView() {
        super.initView();

        titlerela = (RelativeLayout) findViewById(com.unbounded.video.R.id.local_titlerela);
        videotv = (TextView) findViewById(com.unbounded.video.R.id.local_videotv);
        viewPager = (ViewPager) findViewById(com.unbounded.video.R.id.local_viewpager);
    }

    @Override
    public void initParams() {
        super.initParams();
//
//        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) titlerela.getLayoutParams();
//        params.height = screenHeight * 9/ 121;
//        titlerela.setLayoutParams(params);

    }

    @Override
    public void initEvent() {
        super.initEvent();

        pagerAdapter = new MyFrageStatePagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(fragmentList.size());
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());//页面变化时的监听器

        videotv.setOnClickListener(this);
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

    /**
     * 分类选项点击方法
     */
    private void changeCurrentItem(int desTab) {
        viewPager.setCurrentItem(desTab, true);
        changeView(desTab);
    }

    private void changeView(int desTab) {
        videotv.setTextColor(getResources().getColor(com.unbounded.video.R.color.word_six_four));


        if(desTab == 0){
            videotv.setTextColor(getResources().getColor(com.unbounded.video.R.color.titleblue));
        }else if(desTab == 1){
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

        switch (view.getId()){
            case com.unbounded.video.R.id.local_backbtn:
                finish();
                break;

            case com.unbounded.video.R.id.local_videotv:
                changeCurrentItem(0);
                break;
            case com.unbounded.video.R.id.local_imagetv:
                changeCurrentItem(1);
                break;

        }
    }
}
