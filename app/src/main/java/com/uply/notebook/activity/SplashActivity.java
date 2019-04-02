package com.uply.notebook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.uply.notebook.R;
import com.uply.notebook.UplyNoteBook;

import cn.bmob.v3.BmobUser;

public class SplashActivity extends AppCompatActivity {

    private ImageView mIvSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 取消标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                , WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        mIvSplash = findViewById(R.id.id_iv_splash);
        // 实现渐变效果
        Animation animation = new AlphaAnimation(0.5f, 1f);
        animation.setDuration(1500);
        mIvSplash.startAnimation(animation);
        // 动画结束后启动登陆界面或主界面
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                UplyNoteBook application = (UplyNoteBook) getApplication();
                BmobUser user = application.getUser();
                // 已经登陆
                if (user != null) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    // 启动登陆界面
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
