package com.example.a2048;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.a2048.view.GameView;

public class Game extends AppCompatActivity implements View.OnClickListener ,View.OnTouchListener{


    private static Game mGame;

    private int mRecordScore;
    //目标分数
    private TextView mTVGoal;

    private int mGoal;

    private GameView mGameView;
    //记录分数
    private TextView mScore;
    //记录最高分数
    private TextView mRecord;
    private FrameLayout mGamePanel;
    private Button mBtnRestart;
    private Button mBtnOption;
    private Button mBtnRevert;

    public Game() {
        mGame = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.game_panel);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.game_panel_rl);
        relativeLayout.addView(mGameView);

    }

    public static Game getGame() {
        return mGame;
    }


    /**
     * 设置分数
     *
     * @param score 方法
     * @param f     0：set score 1:set high score
     */

    public void setScore(int score, int f) {
        if (f == 0) {
            mScore.setText("" + score);
        } else if (f == 1) {
            mRecord.setText("" + score);
        }
    }

    public void setGoal(int goal) {
        mTVGoal.setText("" + goal);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_revert:
                mGameView.revertGame();
                break;
            case R.id.btn_option:
                startActivityForResult(new Intent(Game.this, OptionsActivity.class), 0);
                break;
            case R.id.btn_restart:
                mGameView.startGame();
                setScore(0, 0);
                break;
            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mGoal = Config.sSp.getInt(Config.KEY_GAME_GOAL, 2048);
            mTVGoal.setText(mGoal + "");
            mRecordScore = Config.sSp.getInt(Config.KEY_HIGH_SCORE, 0);
            mRecord.setText(mRecordScore + "");
            setScore(0,0);
            Config.sItemSize= Config.sSp.getInt(Config.KEY_GAME_LINES,4);
            mGameView.startGame();
        }
    }

    private void initView() {
        mTVGoal = (TextView) findViewById(R.id.tv_Goal);
        mScore = (TextView) findViewById(R.id.scroe);
        mRecord = (TextView) findViewById(R.id.record);
        mGamePanel = (FrameLayout) findViewById(R.id.game_panel);
        mBtnRestart = (Button) findViewById(R.id.btn_restart);
        mBtnOption = (Button) findViewById(R.id.btn_option);
        mBtnRevert = (Button) findViewById(R.id.btn_revert);
        mBtnRevert.setOnClickListener(this);
        mBtnRestart.setOnClickListener(this);
        mBtnOption.setOnClickListener(this);

        mRecordScore = Config.sSp.getInt(Config.KEY_HIGH_SCORE, 0);
        mGoal = Config.sSp.getInt(Config.KEY_GAME_GOAL, 2048);

        mTVGoal.setText(mGoal + "");
        mRecord.setText(mRecordScore + "");
        mScore.setText("0");

        mGameView = new GameView(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}