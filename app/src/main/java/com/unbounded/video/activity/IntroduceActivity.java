package com.unbounded.video.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.unbounded.video.BaseActivity;
import com.unbounded.video.fragment.Introduce2Fragment;
import com.unbounded.video.fragment.IntroduceFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjf on 2017/7/22 0022.
 */

public class IntroduceActivity extends BaseActivity{
    public final static String STEP_ONE = "1";
    public final static String STEP_TWO = "2";
    int currenttab;
    ViewPager viewPager;
    ImageView point1,point2;
    List<Fragment> fragmentList;
    Fragment step1frag,step2frag;

    @Override
    protected int getContentView() {
        return com.unbounded.video.R.layout.activity_introduce;
    }

    @Override
    public void init(Intent intent) {
        super.init(intent);

        setTitleName(getString(com.unbounded.video.R.string.Word_Introduce));

        fragmentList = new ArrayList<Fragment>();
        step1frag = new IntroduceFragment();
        step2frag = new Introduce2Fragment();

        fragmentList.add(step1frag);
        fragmentList.add(step2frag);

    }

    @Override
    public void initView() {
        super.initView();

        viewPager = (ViewPager) findViewById(com.unbounded.video.R.id.introduce_viewpager);
        point1 = (ImageView) findViewById(com.unbounded.video.R.id.introduce_point1);
        point2 = (ImageView) findViewById(com.unbounded.video.R.id.introduce_point2);
    }

    @Override
    public void initEvent() {
        super.initEvent();

        viewPager.setAdapter(new MyFrageStatePagerAdapter(getSupportFragmentManager()));
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());//页面变化时的监听器
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
     * viewpager变化
     */
    private void changeView(int des){
        if(des == 0){
            point1.setImageResource(com.unbounded.video.R.mipmap.select);
            point2.setImageResource(com.unbounded.video.R.mipmap.unselect);

        }else if(des == 1){
            point1.setImageResource(com.unbounded.video.R.mipmap.unselect);
            point2.setImageResource(com.unbounded.video.R.mipmap.select);
        }
    }

}
