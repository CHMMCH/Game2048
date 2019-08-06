package com.chm.game2048.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.chm.game2048.R;

//闪屏页
public class SplashActivity extends AppCompatActivity implements Animation.AnimationListener{

    Button bt1;
    int i=5;
    int j=0;
    Handler he = new Handler();
    Handler he2 = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //控制状态栏消失
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash2);

        //动态动画
        ImageView splashImage = findViewById(R.id.imgLogo);
        Animation animation = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f); // 将图片放大1.2倍，从中心开始缩放
        animation.setDuration(5000); // 动画持续时间
        animation.setFillAfter(true); // 动画结束后停留在结束的位置
        animation.setAnimationListener(this);
        splashImage.startAnimation(animation);


//        隐藏状态栏方法二
//        RelativeLayout layout = (RelativeLayout) findViewById(R.id.lay_shanping);
//        layout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);


        bt1= findViewById(R.id.bt1);
        new Thread(new MyCountDownTimer()).start();    //开始执行10s方法


        //点击按钮就关闭
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                j=1;
                startActivity(new Intent(SplashActivity.this,Game.class));
                finish();//关闭activity页面
//                System.exit(0);//彻底关闭进程
            }
        });



        //广告十秒自动关闭
        he.postDelayed(new Runnable(){
            @Override
            public void run() {
                if(j==0){
                    startActivity(new Intent(SplashActivity.this,Game.class));
                    finish();
                }
            }

        }, 5000);
    }

    //倒计时10s关闭广告内部类
    class MyCountDownTimer implements Runnable {

        @Override
        public void run() {

            //倒计时开始，循环
            while (i > -1) {
                he2.post(new Runnable() {
                    @Override
                    public void run() {
                        bt1.setText(i + "跳过");
                    }
                });
                try {
                    Thread.sleep(1000); //强制线程休眠1秒，就是设置倒计时的间隔时间为1秒。
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i--;
            }
        }
    }


    //广告页点击返回键直接退出app（onKeyDown）
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            j=1;
        }
        return super.onKeyDown(keyCode, event);
    }


    //动画方法
    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
//        startActivity(new Intent(SplashActivity.this,MainActivity.class));
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

}
