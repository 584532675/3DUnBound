package com.unbounded.video.fragment;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.unbounded.video.BaseFragment;

/**
 * Created by zjf on 2017/7/18 0018.
 */

public class Introduce2Fragment extends BaseFragment {
    private String stepId;
    TextView steptagtv,stepinfotv;
    ImageView stepiv;
    int imgWidth,imgHeight;

    public Introduce2Fragment() {
    }

    @Override
    protected int getContentView() {
        return com.unbounded.video.R.layout.fragment_introduce;
    }

    @Override
    public void initView(View view) {
        super.initView(view);

        steptagtv = (TextView) view.findViewById(com.unbounded.video.R.id.introducefragment_steptagtv);
        stepinfotv = (TextView) view.findViewById(com.unbounded.video.R.id.introducefragment_stepinfotv);
        stepiv = (ImageView) view.findViewById(com.unbounded.video.R.id.introducefragment_stepiv);


        steptagtv.setText(com.unbounded.video.R.string.Word_steptwo);
        stepinfotv.setText(com.unbounded.video.R.string.Word_steptwoinfo);
        stepiv.setImageResource(com.unbounded.video.R.mipmap.introduce2);


        imgWidth = screenWidth - 32*oneDp;
        imgHeight = imgWidth* 496/1006;
    }

    @Override
    public void initParams() {
        super.initParams();

        ViewGroup.LayoutParams stepivparams = stepiv.getLayoutParams();
        stepivparams.height = imgHeight;
        stepivparams.width = imgWidth;
        stepiv.setLayoutParams(stepivparams);
    }

    @Override
    public void initEvent() {
        super.initEvent();




    }
}
