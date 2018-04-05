package com.example.a2048.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.GridLayout;

import com.example.a2048.Config;
import com.example.a2048.Game;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Night on 2016/6/13.
 * Desc:A layout of game
 */

public class GameView extends GridLayout implements View.OnTouchListener {
    //Item Matrix
    private GameItem[][] mGameMatrix;
    //记录空白List
    private List<Point> mBlankList;
    //计算合并时的辅助数组
    private List<Integer> mCalList;
    //History Item Matrix
    private int[][] mItemMatrixHis;
    //Matrix row、column
    private int mItemLines;
    //History score
    private int mScoreHis;
    //Highest score
    private int mScoreHigh;
    //Target score
    private int mScoreTar;
    // 记录坐标
    private int mStartX, mStartY, mEndX, mEndY;
    private boolean flase;

    public GameView(Context context) {
        this(context, null);
    }

    public GameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScoreTar = Config.sSp.getInt(Config.KEY_GAME_GOAL, 2048);
        initGameMatrix();
    }

    private void initGameMatrix() {
        removeAllViews();
        Config.sGameLines = Config.sSp.getInt(Config.KEY_GAME_LINES, 4);
        mItemLines = Config.sGameLines;
        mScoreHis = 0;
        Config.sScore = 0;
        mScoreHigh = Config.sSp.getInt(Config.KEY_HIGH_SCORE, 4);
        mGameMatrix = new GameItem[mItemLines][mItemLines];
        mItemMatrixHis = new int[mItemLines][mItemLines];
        mCalList = new ArrayList<Integer>();
        mBlankList = new ArrayList<Point>();
        setColumnCount(mItemLines);
        setRowCount(mItemLines);
        setOnTouchListener(this);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getMetrics(displayMetrics);
        Config.sItemSize = displayMetrics.widthPixels / mItemLines;
        initGameView(Config.sItemSize);
    }

    private void initGameView(int itemSize) {
        removeAllViews();
        GameItem item;
        for (int i = 0; i < mItemLines; i++) {
            for (int j = 0; j < mItemLines; j++) {
                item = new GameItem(getContext(), 0);
                addView(item, itemSize, itemSize);
                mGameMatrix[i][j] = item;
                mBlankList.add(new Point(i, j));
            }
        }
        addRandomNum();
    }

    private void addRandomNum() {
        getBlanks();
        if (mBlankList.size() > 0) {
            int randomNum = (int) (Math.random() * mBlankList.size());
            Point randomPoint = mBlankList.get(randomNum);
            mGameMatrix[randomPoint.x][randomPoint.y].setItemNum(Math.random() > 0.25d ? 2 : 4);
            animateCreate(mGameMatrix[randomPoint.x][randomPoint.y]);
        }
    }

    private void animateCreate(GameItem gameItem) {
        ScaleAnimation sa = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(100);
        gameItem.setAnimation(null);
        gameItem.getItemView().startAnimation(sa);
    }

    private void getBlanks() {
        mBlankList.clear();
        for (int i = 0; i < mItemLines; i++) {
            for (int j = 0; j < mItemLines; j++) {
                if (mGameMatrix[i][j].getItemNum() == 0) {
                    mBlankList.add(new Point(i, j));
                }
            }
        }
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                saveHistoryMatrix();
                mStartX = (int) motionEvent.getX();
                mStartY = (int) motionEvent.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                mEndX = (int) motionEvent.getX();
                mEndY = (int) motionEvent.getY();
                judgeDirection(mEndX - mStartX, mEndY - mStartY);
                if (isMoved()) {
                    addRandomNum();
                    Game.getGame().setScore(Config.sScore, 0);
                }
                checkCompleted();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 检测是否有目标数字
     *
     * @return 0结束 1继续 2成功
     */
    private int checkNums() {
        getBlanks();
        if (mBlankList.size() != 0) {
            for (int i = 0; i < mItemLines; i++) {
                for (int j = 0; j < mItemLines; j++) {
                    if (mGameMatrix[i][j].getItemNum() == mScoreTar) {
                        return 2;
                    }
                }
            }
            return 1;
        } else {
            for (int i = 0; i < mItemLines; i++) {
                for (int j = 0; j < mItemLines; j++) {
                    if (j < mItemLines - 1) {
                        if (mGameMatrix[i][j].getItemNum() == mGameMatrix[i][j + 1]
                                .getItemNum()) {
                            return 1;
                        }
                    }
                    if (i < mItemLines - 1) {
                        if (mGameMatrix[i][j].getItemNum() == mGameMatrix[i + 1][j]
                                .getItemNum()) {
                            return 1;
                        }
                    }
                }
            }
            return 0;
        }
    }

    private void checkCompleted() {
        int r = checkNums();
        if (r == 0) {
            if (Config.sScore > mScoreHigh) {
                SharedPreferences.Editor editor = Config.sSp.edit();
                editor.putInt(Config.KEY_HIGH_SCORE, Config.sScore);
                editor.apply();
                Game.getGame().setScore(Config.sScore, 1);
                Config.sScore = 0;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Game Over").setPositiveButton("Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startGame();
                    Game.getGame().setScore(0, 0);
                }
            }).create().show();

        } else if (r == 2) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Congratulations!!!").setPositiveButton("Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startGame();
                }
            }).setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SharedPreferences.Editor editor = Config.sSp.edit();
                    if (mScoreTar == 1024) {
                        editor.putInt(Config.KEY_GAME_GOAL, 2048);
                        mScoreTar = 2048;
                        Game.getGame().setGoal(mScoreTar);
                    } else if (mScoreTar == 2048) {
                        editor.putInt(Config.KEY_GAME_GOAL, 4096);
                        mScoreTar = 4096;
                        Game.getGame().setGoal(mScoreTar);
                    } else {
                        editor.putInt(Config.KEY_GAME_GOAL, 4096);
                        mScoreTar = 4096;
                        Game.getGame().setGoal(mScoreTar);
                    }
                    editor.apply();
                }
            }).create().show();

        }
    }

    public void startGame() {
        initGameMatrix();
        initGameView(Config.sItemSize);
    }

    private void saveHistoryMatrix() {
        mScoreHis = Config.sScore;
        for (int i = 0; i < mItemLines; i++) {
            for (int j = 0; j < mItemLines; j++) {
                mItemMatrixHis[i][j] = mGameMatrix[i][j].getItemNum();
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

    private void judgeDirection(int offsetX, int offsetY) {
        int density = getDeviceDensity();
        int slideDis = 5 * density;
        int maxDis = 500 * density;
        boolean flagNormal = (Math.abs(offsetX) > slideDis || Math.abs(offsetY) > slideDis) &&
                (Math.abs(offsetX) < maxDis) && (Math.abs(offsetY) < maxDis);
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
        } else if (flagSuper) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Back Door");
            final EditText editText = new EditText(getContext());
            builder.setView(editText).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!TextUtils.isEmpty(editText.getText().toString().trim())) {
                        addSuperNumber(Integer.parseInt(editText.getText().toString().trim()));
                        checkCompleted();
                    }
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        }
    }

    private void addSuperNumber(int num) {
        if (checkNumber(num)) {
            getBlanks();
            if (mBlankList.size() > 0) {
                int randomNum = (int) (Math.random() * mBlankList.size());
                Point randomPoint = mBlankList.get(randomNum);
                mGameMatrix[randomPoint.x][randomPoint.y].setItemNum(num);
                animateCreate(mGameMatrix[randomPoint.x][randomPoint.y]);
            }
        }
    }

    private boolean checkNumber(int num) {
        if (num == 2 || num == 4 || num == 8 || num == 16 || num == 32 || num == 64
                || num == 128 || num == 256 ||num == 512 || num == 1024 || num == 2048 || num == 4096)
            return true;
        return false;
    }

    int mKeyItemNum = -1;

    //下滑
    private void swipeDown() {
        int currentItem;
        for (int i = mItemLines - 1; i >= 0; i--) {
            for (int j = mItemLines - 1; j >= 0; j--) {
                currentItem = mGameMatrix[j][i].getItemNum();
                if (currentItem != 0) {
                    if (mKeyItemNum == -1) {
                        mKeyItemNum = currentItem;
                    } else if (mKeyItemNum == currentItem) {
                        mCalList.add(mKeyItemNum * 2);
                        Config.sScore += mKeyItemNum * 2;
                        mKeyItemNum = -1;
                    } else {
                        mCalList.add(mKeyItemNum);
                        mKeyItemNum = currentItem;
                    }
                } else {
                    continue;
                }
            }
            if (mKeyItemNum != -1) {
                mCalList.add(mKeyItemNum);
            }
            int index = mCalList.size() - 1;
            for (int j = mItemLines - mCalList.size(); j < mItemLines; j++) {
                mGameMatrix[j][i].setItemNum(mCalList.get(index));
                index--;
            }
            for (int j = 0; j < mItemLines - mCalList.size(); j++) {
                mGameMatrix[j][i].setItemNum(0);
            }
            mKeyItemNum = -1;
            mCalList.clear();
        }
    }

    //上滑
    private void swipeUp() {
        int currentItem;
        for (int i = 0; i < mItemLines; i++) {
            for (int j = 0; j < mItemLines; j++) {
                currentItem = mGameMatrix[j][i].getItemNum();
                if (currentItem != 0) {
                    if (mKeyItemNum == -1) {
                        mKeyItemNum = currentItem;
                    } else if (mKeyItemNum == currentItem) {
                        mCalList.add(mKeyItemNum * 2);
                        Config.sScore += mKeyItemNum * 2;
                        mKeyItemNum = -1;
                    } else {
                        mCalList.add(mKeyItemNum);
                        mKeyItemNum = currentItem;
                    }
                } else {
                    continue;
                }
            }
            if (mKeyItemNum != -1) {
                mCalList.add(mKeyItemNum);
            }
            for (int j = 0; j < mCalList.size(); j++) {
                mGameMatrix[j][i].setItemNum(mCalList.get(j));
            }
            for (int j = mCalList.size(); j < mItemLines; j++) {
                mGameMatrix[j][i].setItemNum(0);
            }
            mKeyItemNum = -1;
            mCalList.clear();
        }
    }

    //向左滑
    private void swipeLeft() {
        int currentItem;
        for (int i = 0; i < mItemLines; i++) {
            for (int j = 0; j < mItemLines; j++) {
                currentItem = mGameMatrix[i][j].getItemNum();
                if (currentItem != 0) {
                    if (mKeyItemNum == -1) {
                        mKeyItemNum = currentItem;
                    } else if (mKeyItemNum == currentItem) {
                        mCalList.add(mKeyItemNum * 2);
                        Config.sScore += mKeyItemNum * 2;
                        mKeyItemNum = -1;
                    } else {
                        mCalList.add(mKeyItemNum);
                        mKeyItemNum = currentItem;
                    }
                } else {
                    continue;
                }
            }
            if (mKeyItemNum != -1) {
                mCalList.add(mKeyItemNum);
            }
            for (int j = 0; j < mCalList.size(); j++) {
                mGameMatrix[i][j].setItemNum(mCalList.get(j));
            }
            for (int j = mCalList.size(); j < mItemLines; j++) {
                mGameMatrix[i][j].setItemNum(0);
            }
            mKeyItemNum = -1;
            mCalList.clear();
        }
    }

    //向右滑
    private void swipeRight() {
        int currentItem;
        for (int i = mItemLines - 1; i >= 0; i--) {
            for (int j = mItemLines - 1; j >= 0; j--) {
                currentItem = mGameMatrix[i][j].getItemNum();
                if (currentItem != 0) {
                    if (mKeyItemNum == -1) {
                        mKeyItemNum = currentItem;
                    } else if (mKeyItemNum == currentItem) {
                        mCalList.add(mKeyItemNum * 2);
                        Config.sScore += mKeyItemNum * 2;
                        mKeyItemNum = -1;
                    } else {
                        mCalList.add(mKeyItemNum);
                        mKeyItemNum = currentItem;
                    }
                } else {
                    continue;
                }
            }
            if (mKeyItemNum != -1) {
                mCalList.add(mKeyItemNum);
            }
            int index = mCalList.size() - 1;
            for (int j = mItemLines - mCalList.size(); j < mItemLines; j++) {
                mGameMatrix[i][j].setItemNum(mCalList.get(index));
                index--;
            }
            for (int j = 0; j < mItemLines - mCalList.size(); j++) {
                mGameMatrix[i][j].setItemNum(0);
            }
            mKeyItemNum = -1;
            mCalList.clear();
        }
    }

    private boolean isMoved() {
        for (int i = 0; i < mItemLines; i++) {
            for (int j = 0; j < mItemLines; j++) {
                if (mItemMatrixHis[i][j] != mGameMatrix[i][j].getItemNum()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 游戏撤回
     */
    public void revertGame() {
        //首次不可撤回
        int sum = 0;
        for (int[] element : mItemMatrixHis) {
            for (int i : element) {
                sum += i;
            }
        }
        Log.e("TAG", sum + ":sum");
        if (sum != 0) {
            Game.getGame().setScore(mScoreHis, 0);
            Config.sScore = mScoreHis;
            for (int i = 0; i < mItemLines; i++) {
                for (int j = 0; j < mItemLines; j++) {
                    mGameMatrix[i][j].setItemNum(mItemMatrixHis[i][j]);
                }
            }
        }
    }
}
