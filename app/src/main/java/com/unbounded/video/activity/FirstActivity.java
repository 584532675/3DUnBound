package com.unbounded.video.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Created by Administrator on 2017/12/13 0013.
 */

public class FirstActivity extends Activity {
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(com.unbounded.video.R.layout.activity_splash);


        img = (ImageView) findViewById(com.unbounded.video.R.id.splash_logo);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putBoolean("update", true);
                intent.putExtras(bundle);
                intent.setClass(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

    }
}
