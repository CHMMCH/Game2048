package com.chm.game2048.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.chm.game2048.R;
import com.chm.game2048.config.Config;

/**
 * @date: 2018/6/18  18:43
 */
public class ConfigPreference extends Activity implements OnClickListener {

    private Button mBtnGameLines;

    private Button mBtnGoal;

    private Button mBtnBack;

    private Button mBtnDone;

    private String[] mGameLinesList;

    private String[] mGameGoalList;

    private AlertDialog.Builder mBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_preference);
        initView();
    }

    private void initView() {
        mBtnGameLines =  findViewById(R.id.btn_gamelines);
        mBtnGoal =  findViewById(R.id.btn_goal);
        mBtnBack =  findViewById(R.id.btn_back);
        mBtnDone =  findViewById(R.id.btn_done);
        mBtnGameLines.setText("" + Config.mSp.getInt(Config.KEY_GAME_LINES, 4));
        mBtnGoal.setText("" + Config.mSp.getInt(Config.KEY_GAME_GOAL, 2048));
        mBtnGameLines.setOnClickListener(this);
        mBtnGoal.setOnClickListener(this);
        mBtnBack.setOnClickListener(this);
        mBtnDone.setOnClickListener(this);
        mGameLinesList = new String[]{"4", "5", "6"};
        mGameGoalList = new String[]{"1024", "2048", "4096"};
    }

    private void saveConfig() {//修改配置方法
        Editor editor = Config.mSp.edit();
        editor.putInt(Config.KEY_GAME_LINES,
                Integer.parseInt(mBtnGameLines.getText().toString()));
        editor.putInt(Config.KEY_GAME_GOAL,
                Integer.parseInt(mBtnGoal.getText().toString()));
        editor.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_gamelines:
                mBuilder = new AlertDialog.Builder(this);
                mBuilder.setTitle("修改格子数");
                mBuilder.setItems(mGameLinesList,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mBtnGameLines.setText(mGameLinesList[which]);
                            }
                        });
                mBuilder.create().show();
                break;
            case R.id.btn_goal:
                mBuilder = new AlertDialog.Builder(this);
                mBuilder.setTitle("修改游戏目标");
                mBuilder.setItems(mGameGoalList,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mBtnGoal.setText(mGameGoalList[which]);
                            }
                        });
                mBuilder.create().show();
                break;
            case R.id.btn_back:
                this.finish();
                startActivity(new Intent(ConfigPreference.this,Game.class));
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                break;
            case R.id.btn_done:
                saveConfig();
                setResult(RESULT_OK);
                this.finish();
                startActivity(new Intent(ConfigPreference.this,Game.class));
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                break;
            default:
                break;
        }
    }

    public void lxzz(View v){

        Toast.makeText(ConfigPreference.this,"16计算机2班陈泓谋",Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0)
        {
            this.finish();
            startActivity(new Intent(ConfigPreference.this,Game.class));
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);//改变Activity跳转动画效果
        }
        return super.onKeyDown(keyCode, event);
    }
}
