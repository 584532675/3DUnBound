package com.unbounded.video.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.unbounded.video.BaseActivity;
import com.unbounded.video.bean.ThreeddPic;
import com.unbounded.video.constants.Constants;
import com.unbounded.video.utils.ExitApplication;
import com.unbounded.video.utils.FileDownloaderManager;
import com.unbounded.video.utils.SharedPrefsUtil;
import com.unbounded.video.utils.ToastUtil;
import com.unbounded.video.utils.imageLoad.GlideLogic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjf on 2017/9/2.
 */

public class ThreeddPicsActivity extends BaseActivity {
    RelativeLayout rela1;
    SwipeRefreshLayout swipeRefreshLayout;
    ListView lv;
    ThreeddpicsAdapter adapter;
    int ivWidth,tabsmrelaHeight;

    private List<ThreeddPic> list = new ArrayList<ThreeddPic>();

    private long LastClickTime = 0;

    @Override
    protected int getContentView() {
        return com.unbounded.video.R.layout.activity_threeddpics;
    }

    @Override
    public void init(Intent intent) {
        super.init(intent);

        setTitleName("全部专题");

        list.add(new ThreeddPic("1", "蒸发太平洋", ""));
        list.add(new ThreeddPic("2", "蒸发太平洋", ""));
        list.add(new ThreeddPic("3", "蒸发太平洋", ""));
        list.add(new ThreeddPic("4", "蒸发太平洋", ""));
        list.add(new ThreeddPic("5", "蒸发太平洋", ""));

        ivWidth = screenWidth - 24*oneDp;
        tabsmrelaHeight = SharedPrefsUtil.getValue(ThreeddPicsActivity.this, MainActivity.Tabsrela_Height, Constants.ZERO);
    }

    @Override
    public void initView() {
        super.initView();

        rela1 = (RelativeLayout) findViewById(com.unbounded.video.R.id.threeddpics_rela1);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(com.unbounded.video.R.id.threeddpics_swiperefresh);
        lv = (ListView) findViewById(com.unbounded.video.R.id.threeddpics_lv);
        rela1.setPadding(0,0,0,tabsmrelaHeight);

        adapter = new ThreeddpicsAdapter(ThreeddPicsActivity.this, list, ivWidth);
        lv.setAdapter(adapter);
    }

    public class ThreeddpicsAdapter extends BaseAdapter {
        private Context context;
        private List<ThreeddPic> list;
        private int imgWidth,imgHeight;

        public ThreeddpicsAdapter(Context context, List<ThreeddPic> list, int imgWidth) {
            this.context = context;
            this.list = list;
            this.imgWidth = imgWidth;
            imgHeight = imgWidth * 432/1032;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ThreeddpicsAdapter.ViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new ThreeddpicsAdapter.ViewHolder();
                convertView = LayoutInflater.from(context).inflate(com.unbounded.video.R.layout.threeddpictitle_item, null);
                viewHolder.imgrela = (RelativeLayout) convertView.findViewById(com.unbounded.video.R.id.threeddpictitle_rela);
                viewHolder.img = (ImageView)convertView.findViewById(com.unbounded.video.R.id.threeddpictitle_imgiv);
                viewHolder.nametv = (TextView) convertView.findViewById(com.unbounded.video.R.id.threeddpictitle_nametv);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ThreeddpicsAdapter.ViewHolder)convertView.getTag();
            }

            ViewGroup.LayoutParams params = viewHolder.imgrela.getLayoutParams();
            params.width =imgWidth;
            params.height =imgHeight;
            viewHolder.imgrela.setLayoutParams(params);

            ViewGroup.LayoutParams params1 = viewHolder.img.getLayoutParams();
            params1.width =imgWidth;
            params1.height =imgHeight;
            viewHolder.img.setLayoutParams(params1);


            viewHolder.nametv.setText(list.get(position).getName());
            //指定大小压缩,节省资源
            GlideLogic.glideLoadPic423(context, list.get(position).getImgurl(), viewHolder.img, imgWidth*11/16, imgHeight*11/16);

            viewHolder.img.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    openActivity(ThreeddPicsListActivity.class);
                }
            });

            return convertView;
        }

        class ViewHolder{
            RelativeLayout imgrela;
            ImageView img;
            TextView nametv;
        }
    }

    @Override
    public void onBackPressed()
    {
        if (LastClickTime <= 0)
        {
            ToastUtil.showToast(ThreeddPicsActivity.this, com.unbounded.video.R.string.onemoreexit, ToastUtil.BOTTOM);
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
                ToastUtil.showToast(ThreeddPicsActivity.this, com.unbounded.video.R.string.onemoreexit, ToastUtil.BOTTOM);
                LastClickTime = currentClickTime;
            }
        }
    }
}
