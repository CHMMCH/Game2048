package com.chm.game2048.activity;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chm.game2048.R;
import com.chm.game2048.config.Config;
import com.chm.game2048.view.GameView;

/**
 * @date: 2018/6/18  15:34
 * 可以优化的地方：1.换肤功能(改变小方块的显示背景)
 *                2.金手指(开启超级用户权限的作弊功能，增加游戏娱乐性)
 */
public class Game extends Activity implements OnClickListener {
    private Toast toast;
    //自定义Toast对象
//    private MyToast myToast;
    //重新重开游戏的临时变量
    private long exitTime = 0;
    // Activity的引用
    private static Game mGame;
    // 记录分数
    private TextView mTvScore;
    // 历史记录分数
    private TextView mTvHighScore;
    private int mHighScore;
    // 目标分数
    private TextView mTvGoal;
    private int mGoal;
    // 重新开始按钮
    private Button mBtnRestart;
    // 撤销按钮
    private Button mBtnRevert;
    // 选项按钮
    private Button mBtnOptions;
    // 游戏面板
    private GameView mGameView;

    public Game() {
        mGame = this;
    }


    /**
     * 获取当前Activity的引用
     *
     * @return Activity.this
     */
    public static Game getGameActivity() {
        return mGame;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitleColor(Color.WHITE);
        // 初始化View
        initView();
        mGameView = new GameView(this);
//        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.game_panel);
//        frameLayout.addView(mGameView);
        // 为了GameView能居中
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.game_panel_rl);
        relativeLayout.addView(mGameView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // 菜单的监听方法
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.jsz:
                Toast.makeText(this, "金手指会有bug，所以慎用",Toast.LENGTH_SHORT).show();
                mGameView.jsz();
                break;
            case R.id.hpf:
                Toast.makeText(this, "待开发", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;

    }

    /**
     * 初始化View
     */
    private void initView() {
        mTvScore = (TextView) findViewById(R.id.scroe);
        mTvGoal = (TextView) findViewById(R.id.tv_Goal);
        mTvHighScore = (TextView) findViewById(R.id.record);
        mBtnRestart = (Button) findViewById(R.id.btn_restart);
        mBtnRevert = (Button) findViewById(R.id.btn_revert);
        mBtnOptions = (Button) findViewById(R.id.btn_option);
        mBtnRestart.setOnClickListener(this);
        mBtnRevert.setOnClickListener(this);
        mBtnOptions.setOnClickListener(this);
        mHighScore = Config.mSp.getInt(Config.KEY_HIGH_SCROE, 0);
        mGoal = Config.mSp.getInt(Config.KEY_GAME_GOAL, 2048);//显示板的分数
        mTvHighScore.setText("" + mHighScore);
        mTvGoal.setText("" + mGoal);
        mTvScore.setText("0");
        setScore(0, 0);
    }

    public void setGoal(int num) {
        mTvGoal.setText(String.valueOf(num));
    }

    /**
     * 修改得分
     *
     * @param score score
     * @param flag  0 : score 1 : high score
     */
    public void setScore(int score, int flag) {
        switch (flag) {
            case 0:
                mTvScore.setText("" + score);
                break;
            case 1:
                mTvHighScore.setText("" + score);
                break;
            default: 
                break;
        }
    }

    @Override
    public void onClick(View v) {//主界面三个按钮
        switch (v.getId()) {
            case R.id.btn_restart:
                if ((System.currentTimeMillis() - exitTime) > 2000) {
//	            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "2s内再次点击重开游戏", Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                } else {
//                    myToast.cance();
//                    toast.cancel();
                    mGameView.startGame();
                    setScore(0, 0);
                }
                break;
            case R.id.btn_revert:
                    mGameView.revertGame();
                break;
            case R.id.btn_option:
                this.finish();
                startActivity(new Intent(Game.this,ConfigPreference.class));
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mGoal = Config.mSp.getInt(Config.KEY_GAME_GOAL, 2048);//目标分数
            mTvGoal.setText("" + mGoal);
            getHighScore();
            mGameView.startGame();
        }
    }

    /**
     * 获取最高记录
     */
    private void getHighScore() {
        int score = Config.mSp.getInt(Config.KEY_HIGH_SCROE, 0);
        setScore(score, 1);
    }

//主页面点击返回键弹出 关闭询问框
	  @Override
	    public boolean onKeyDown(int keyCode, KeyEvent event) {
	        // TODO Auto-generated method stub
	        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0)
          {

	        	new android.app.AlertDialog.Builder(Game.this)
	                .setTitle("退出")
	                .setMessage("您确认要退出游戏吗？")
	                .setPositiveButton("确认", new DialogInterface.OnClickListener() {

	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {
	                        // TODO Auto-generated method stub
	                    	finish();
	                    }
	                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {
	                        // TODO Auto-generated method stub
	                        dialog.dismiss();
	                    }
	                }).show();

	        }
	        return super.onKeyDown(keyCode, event);
	    }

	    public void cq(){
            finish();
            startActivity(getIntent());
            (Game.this).overridePendingTransition(0, 0);
        }



}
