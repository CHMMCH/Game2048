package com.chm.game2048.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.GridLayout;

import com.chm.game2048.activity.Game;
import com.chm.game2048.bean.GameItem;
import com.chm.game2048.config.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * @date: 2018/6/18  15:32
 */
public class GameView extends GridLayout implements OnTouchListener {

    // GameView对应矩阵
    private GameItem[][] mGameMatrix;
    // 空格List
    private List<Point> mBlanks;
    // 矩阵行列数
    private int mGameLines;
    // 记录坐标
    private int mStartX, mStartY, mEndX, mEndY;
    // 辅助数组
    private List<Integer> mCalList;
    //该标识来区分是否已经进行过一次合并
    private int mKeyItemNum = -1;
    // 历史记录数组
    private int[][] mGameMatrixHistory;
    // 历史记录分数
    private int mScoreHistory;
    // 最高记录
    private int mHighScore;
    // 目标分数
    private int mTarget;

    public GameView(Context context) {
        super(context);
        mTarget = Config.mSp.getInt(Config.KEY_GAME_GOAL, 2048);
        initGameMatrix();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGameMatrix();
    }

    public void startGame() {
        initGameMatrix();//创建View
        initGameView(Config.mItemSize);
    }

    private void initGameView(int cardSize) {  //cardSize是整个游戏界面的大小
        removeAllViews();
        GameItem card;
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                card = new GameItem(getContext(), 0);
                addView(card, cardSize, cardSize);
                // 初始化GameMatrix全部为0 空格List为所有
                mGameMatrix[i][j] = card;
                mBlanks.add(new Point(i, j));
            }
        }
        // 添加随机数字
        addRandomNum();
        addRandomNum();
    }

    /**
     * 撤销上次移动
     */
    public void revertGame() {
        // 第一次不能撤销
        int sum = 0;
        for (int[] element : mGameMatrixHistory) {
            for (int i : element) {
                sum += i;
            }
        }
        if (sum != 0) {
            Game.getGameActivity().setScore(mScoreHistory, 0);
            Config.SCROE = mScoreHistory;
            for (int i = 0; i < mGameLines; i++) {
                for (int j = 0; j < mGameLines; j++) {
                    mGameMatrix[i][j].setNum(mGameMatrixHistory[i][j]);
                }
            }
        }
    }

    /**
     * 添加随机数字
     */
    private void addRandomNum() {
        getBlanks();
        if (mBlanks.size() > 0) {
            int randomNum = (int) (Math.random() * mBlanks.size());
            Point randomPoint = mBlanks.get(randomNum);
            mGameMatrix[randomPoint.x][randomPoint.y]
                    .setNum(Math.random() > 0.2d ? 2 : 4);
            //实现动画效果，对新生成的小方块做一个简单的Scale变换
            animCreate(mGameMatrix[randomPoint.x][randomPoint.y]);
        }
    }

    /**
     * 生成动画
     *
     * @param target GameItem
     */
    private void animCreate(GameItem target) {
        ScaleAnimation sa = new ScaleAnimation(0.1f, 1, 1f, 1,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(100);
        target.setAnimation(null);
        target.getItemView().startAnimation(sa);
    }

    /**
     * super模式下添加一个指定数字
     */
    private void addSuperNum(int num) {
        if (checkSuperNum(num)) {
            getBlanks();
            if (mBlanks.size() > 0) {
                int randomNum = (int) (Math.random() * mBlanks.size());
                Point randomPoint = mBlanks.get(randomNum);
                mGameMatrix[randomPoint.x][randomPoint.y].setNum(num);
                animCreate(mGameMatrix[randomPoint.x][randomPoint.y]);
            }
        }
    }

    /**
     * 检查添加的数是否是指定的数
     *
     * @param num num
     * @return 添加的数
     */
    private boolean checkSuperNum(int num) {
        boolean flag = (num == 2 || num == 4 || num == 8 || num == 16
                || num == 32 || num == 64 || num == 128 || num == 256
                || num == 512 || num == 1024|| num == 2048);
        return flag;
    }

    /**
     * 获取空格Item数组
     */
    private void getBlanks() {
        mBlanks.clear();
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                if (mGameMatrix[i][j].getNum() == 0) {
                    mBlanks.add(new Point(i, j));
                }
            }
        }
    }

    /**
     * 初始化View
     */
    private void initGameMatrix() {
        // 初始化矩阵
        removeAllViews();
        mScoreHistory = 0;
        Config.SCROE = 0;
        Config.mGameLines = Config.mSp.getInt(Config.KEY_GAME_LINES, 4);//修改初始格子数量
        mGameLines = Config.mGameLines;
        mGameMatrix = new GameItem[mGameLines][mGameLines];
        mGameMatrixHistory = new int[mGameLines][mGameLines];
        mCalList = new ArrayList<Integer>();
        mBlanks = new ArrayList<Point>();
        mHighScore = Config.mSp.getInt(Config.KEY_HIGH_SCROE, 0);
        setColumnCount(mGameLines);
        setRowCount(mGameLines);
        setOnTouchListener(this);
        // 初始化View参数
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getMetrics(metrics);
        Config.mItemSize = metrics.widthPixels / Config.mGameLines;
        initGameView(Config.mItemSize);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {//触摸事件
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                saveHistoryMatrix();
                mStartX = (int) event.getX();
                mStartY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                mEndX = (int) event.getX();
                mEndY = (int) event.getY();
                judgeDirection(mEndX - mStartX, mEndY - mStartY);//里面调用移动方法判断
                if (isMoved()) {
                    addRandomNum();
                    // 修改显示分数
                    Game.getGameActivity().setScore(Config.SCROE, 0);
                }
                checkCompleted();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 保存历史记录
     */
    private void saveHistoryMatrix() {
        mScoreHistory = Config.SCROE;
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                mGameMatrixHistory[i][j] = mGameMatrix[i][j].getNum();
            }
        }
    }

    private int getDeviceDensity() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        return (int) metrics.density;
    }

    /**
     * 根据偏移量判断移动方向
     *
     * @param offsetX offsetX
     * @param offsetY offsetY
     */
    private void judgeDirection(int offsetX, int offsetY) {
        int density = getDeviceDensity();
        int slideDis = 5 * density;
        int maxDis = 200 * density;
        boolean flagNormal =
                (Math.abs(offsetX) > slideDis ||
                        Math.abs(offsetY) > slideDis) &&
                        (Math.abs(offsetX) < maxDis) &&
                        (Math.abs(offsetY) < maxDis);
        boolean flagSuper = Math.abs(offsetX) > maxDis ||
                Math.abs(offsetY) > maxDis;
        if (flagNormal && !flagSuper) {
            if (Math.abs(offsetX) > Math.abs(offsetY)) {
                if (offsetX > slideDis) {
                    swipeRight();
                } else {
                    swipeLeft();
                }
            } else {
                if (offsetY > slideDis) {
                    swipeDown();
                } else {
                    swipeUp();
                }
            }
        }
//        else if (flagSuper) { // 启动超级用户权限来添加自定义数字
//            AlertDialog.Builder builder =
//                    new AlertDialog.Builder(getContext());
//            final EditText et = new EditText(getContext());
//            builder.setTitle("Back Door")
//                    .setView(et)
//                    .setPositiveButton("OK",
//                            new DialogInterface.OnClickListener() {
//
//                                @Override
//                                public void onClick(DialogInterface arg0,
//                                                    int arg1) {
//                                    if (!TextUtils.isEmpty(et.getText())) {
//                                        addSuperNum(Integer.parseInt(et
//                                                .getText().toString()));
//                                        checkCompleted();
//                                    }
//                                }
//                            })
//                    .setNegativeButton("ByeBye",
//                            new DialogInterface.OnClickListener() {
//
//                                @Override
//                                public void onClick(DialogInterface arg0,
//                                                    int arg1) {
//                                    arg0.dismiss();
//                                }
//                            }).create().show();
//        }
    }

    /**
     * 检测所有数字 看是否有满足条件的
     * 检测游戏状态方法
     * @return 0:结束 1:正常 2:成功
     */
    private int checkNums() {

        getBlanks();
        if (mBlanks.size() == 0) {
            for (int i = 0; i < mGameLines; i++) {
                for (int j = 0; j < mGameLines; j++) {
                    if (j < mGameLines - 1) {
                        if (mGameMatrix[i][j].getNum() == mGameMatrix[i][j + 1]
                                .getNum()) {
                            return 1;
                        }
                    }
                    if (i < mGameLines - 1) {
                        if (mGameMatrix[i][j].getNum() == mGameMatrix[i + 1][j]
                                .getNum()) {
                            return 1;
                        }
                    }
                }
            }
            return 0;
        }//判断分数有无达到4096
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                if (mGameMatrix[i][j].getNum() == 4096) {
                     if(mGameLines == 4){
                         return 4;
                     }else if(mGameLines == 5){
                         return 5;
                     }else{
                         return 3;
                     }
                }
            }
        }//判断有无达到目标分数
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                if (mGameMatrix[i][j].getNum() == mTarget) {
                    return 2;
                }
            }
        }

        return 1;
    }

    /**
     * 判断是否结束
     *
     * 0:结束 1:正常 2:成功
     */
    private void checkCompleted() {
        int result = checkNums();
        if (Config.SCROE > mHighScore) {
            Editor editor = Config.mSp.edit();
            editor.putInt(Config.KEY_HIGH_SCROE, Config.SCROE);
            editor.apply();
            Game.getGameActivity().setScore(Config.SCROE, 1);
//            Config.SCROE = 0;
        }
        if (result == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("游戏结束")
                    .setPositiveButton("重新开始",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    startGame();
                                }
                            }).create().show();
            Config.SCROE = 0;
        } else if (result == 2) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("恭喜完成！")
                    .setNegativeButton("重来",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    // 重新开始
                                    startGame();
                                    Config.SCROE = 0;
                                }
                            })
                    .setPositiveButton("挑战下一等级",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    // 继续游戏 修改target
                                    Editor editor = Config.mSp.edit();
                                    if (mTarget == 1024) {
                                        editor.putInt(Config.KEY_GAME_GOAL, 2048);
                                        mTarget = 2048;
                                        Game.getGameActivity().setGoal(2048);
                                    } else if (mTarget == 2048) {
                                        editor.putInt(Config.KEY_GAME_GOAL, 4096);
                                        mTarget = 4096;
                                        Game.getGameActivity().setGoal(4096);
                                    } else {
                                        editor.putInt(Config.KEY_GAME_GOAL, 4096);
                                        mTarget = 4096;
                                        Game.getGameActivity().setGoal(4096);
                                    }
                                    editor.apply();
                                }
                            }).create().show();
        } else if (result == 3){
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("恭喜全部通关！")
                        .setPositiveButton("重新开始",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0,
                                                        int arg1) {
                                        Editor editor = Config.mSp.edit();
                                        editor.putInt(Config.KEY_GAME_LINES,
                                                Integer.parseInt("4"));
                                        editor.putInt(Config.KEY_GAME_GOAL,
                                                Integer.parseInt("1024"));
                                        editor.apply();
                                        startGame();
                                        Config.SCROE = 0;
                                        Game.getGameActivity().cq();
                                    }
                                }).create().show();
        }else if (result == 4){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("恭喜通关4格模式！")
                    .setNegativeButton("重新开始",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    Editor editor = Config.mSp.edit();
                                    editor.putInt(Config.KEY_GAME_LINES,
                                            Integer.parseInt("4"));
                                    editor.putInt(Config.KEY_GAME_GOAL,
                                            Integer.parseInt("1024"));
                                    editor.apply();
                                    startGame();
                                    Config.SCROE = 0;
                                    Game.getGameActivity().cq();
                                }
                            })

                    .setPositiveButton("进入5格模式！",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0,
                                            int arg1) {
                            Editor editor = Config.mSp.edit();
                            editor.putInt(Config.KEY_GAME_LINES,
                                    Integer.parseInt("5"));
                            editor.putInt(Config.KEY_GAME_GOAL,
                                    Integer.parseInt("1024"));
                            editor.apply();
                            startGame();
                            Game.getGameActivity().cq();
                        }
                    }).create().show();
        }else if (result == 5){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("恭喜通关5格模式！")
                    .setNegativeButton("重新开始",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    Editor editor = Config.mSp.edit();
                                    editor.putInt(Config.KEY_GAME_LINES,
                                            Integer.parseInt("5"));
                                    editor.putInt(Config.KEY_GAME_GOAL,
                                            Integer.parseInt("1024"));
                                    editor.apply();
                                    startGame();
                                    Config.SCROE = 0;
                                    Game.getGameActivity().cq();
                                }
                            })

                    .setPositiveButton("进入6格模式！",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    Editor editor = Config.mSp.edit();
                                    editor.putInt(Config.KEY_GAME_LINES,
                                            Integer.parseInt("6"));
                                    editor.putInt(Config.KEY_GAME_GOAL,
                                            Integer.parseInt("1024"));
                                    editor.apply();
                                    startGame();
                                    Game.getGameActivity().cq();
                                }
                            }).create().show();
        }
    }

    /**
     * 判断是否移动过(是否需要新增Item)
     *
     * @return 是否移动
     */
    private boolean isMoved() {
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                if (mGameMatrixHistory[i][j] != mGameMatrix[i][j].getNum()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 滑动事件：上
     */
    private void swipeUp() {
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                int currentNum = mGameMatrix[j][i].getNum();
                if (currentNum != 0) {
                    if (mKeyItemNum == -1) {
                        mKeyItemNum = currentNum;
                    } else {
                        if (mKeyItemNum == currentNum) {
                            mCalList.add(mKeyItemNum * 2);
                            Config.SCROE += mKeyItemNum * 2;
                            mKeyItemNum = -1;
                        } else {
                            mCalList.add(mKeyItemNum);
                            mKeyItemNum = currentNum;
                        }
                    }
                } else {
                    continue;
                }
            }
            if (mKeyItemNum != -1) {
                mCalList.add(mKeyItemNum);
            }
            // 改变Item值
            for (int j = 0; j < mCalList.size(); j++) {
                mGameMatrix[j][i].setNum(mCalList.get(j));

            }
            for (int m = mCalList.size(); m < mGameLines; m++) {
                mGameMatrix[m][i].setNum(0);
            }
            // 重置行参数
            mKeyItemNum = -1;
            mCalList.clear();
        }
    }

    /**
     * 滑动事件：下
     */
    private void swipeDown() {
        for (int i = mGameLines - 1; i >= 0; i--) {
            for (int j = mGameLines - 1; j >= 0; j--) {
                int currentNum = mGameMatrix[j][i].getNum();
                if (currentNum != 0) {
                    if (mKeyItemNum == -1) {
                        mKeyItemNum = currentNum;
                    } else {
                        if (mKeyItemNum == currentNum) {
                            mCalList.add(mKeyItemNum * 2);
                            Config.SCROE += mKeyItemNum * 2;
                            mKeyItemNum = -1;
                        } else {
                            mCalList.add(mKeyItemNum);
                            mKeyItemNum = currentNum;
                        }
                    }
                } else {
                    continue;
                }
            }
            if (mKeyItemNum != -1) {
                mCalList.add(mKeyItemNum);
            }
            // 改变Item值
            for (int j = 0; j < mGameLines - mCalList.size(); j++) {
                mGameMatrix[j][i].setNum(0);
            }
            int index = mCalList.size() - 1;
            for (int m = mGameLines - mCalList.size(); m < mGameLines; m++) {
                mGameMatrix[m][i].setNum(mCalList.get(index));
                index--;
            }
            // 重置行参数
            mKeyItemNum = -1;
            mCalList.clear();
            index = 0;
        }
    }

    /**
     * 滑动事件：左
     */
    private void swipeLeft() {
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                int currentNum = mGameMatrix[i][j].getNum();
                if (currentNum != 0) {
                    if (mKeyItemNum == -1) {
                        mKeyItemNum = currentNum;
                    } else {
                        if (mKeyItemNum == currentNum) {
                            mCalList.add(mKeyItemNum * 2);
                            Config.SCROE += mKeyItemNum * 2;
                            mKeyItemNum = -1;
                        } else {
                            mCalList.add(mKeyItemNum);
                            mKeyItemNum = currentNum;
                        }
                    }
                } else {
                    continue;
                }
            }
            if (mKeyItemNum != -1) {
                mCalList.add(mKeyItemNum);
            }
            // 改变Item值
            for (int j = 0; j < mCalList.size(); j++) {
                mGameMatrix[i][j].setNum(mCalList.get(j));
            }
            for (int m = mCalList.size(); m < mGameLines; m++) {
                mGameMatrix[i][m].setNum(0);
            }
            // 重置行参数
            mKeyItemNum = -1;
            mCalList.clear();
        }
    }

    /**
     * 滑动事件：右
     */
    private void swipeRight() {
        for (int i = mGameLines - 1; i >= 0; i--) {
            for (int j = mGameLines - 1; j >= 0; j--) {
                int currentNum = mGameMatrix[i][j].getNum();
                if (currentNum != 0) {
                    if (mKeyItemNum == -1) {
                        mKeyItemNum = currentNum;
                    } else {
                        if (mKeyItemNum == currentNum) {
                            mCalList.add(mKeyItemNum * 2);
                            Config.SCROE += mKeyItemNum * 2;
                            mKeyItemNum = -1;
                        } else {
                            mCalList.add(mKeyItemNum);
                            mKeyItemNum = currentNum;
                        }
                    }
                } else {
                    continue;
                }
            }
            if (mKeyItemNum != -1) {
                mCalList.add(mKeyItemNum);
            }
            // 改变Item值
            for (int j = 0; j < mGameLines - mCalList.size(); j++) {
                mGameMatrix[i][j].setNum(0);
            }
            int index = mCalList.size() - 1;
            for (int m = mGameLines - mCalList.size(); m < mGameLines; m++) {
                mGameMatrix[i][m].setNum(mCalList.get(index));
                index--;
            }
            // 重置行参数
            mKeyItemNum = -1;
            mCalList.clear();
            index = 0;
        }
    }

    public void jsz(){
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getContext());
        final EditText et = new EditText(getContext());
        builder.setTitle("请输入您想要的数随机添加")
                .setView(et)
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0,
                                                int arg1) {
                                if (!TextUtils.isEmpty(et.getText())) {
                                    addSuperNum(Integer.parseInt(et
                                            .getText().toString()));
                                    checkCompleted();
                                }
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0,
                                                int arg1) {
                                arg0.dismiss();
                            }
                        }).create().show();
    }

}
