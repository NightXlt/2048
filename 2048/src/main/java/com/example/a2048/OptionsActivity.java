package com.example.a2048;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Night on 2016/8/15.
 * Desc:
 */

public class OptionsActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtTargetGoal;
    private Button mBtGameLines;
    private Button mBtBack;
    private Button mBtDone;

    private TextView mTvContactMe;
    private String[] gameLines;
    private String[] targetGoal;
    private AlertDialog.Builder mBuilder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        initView();
    }

    private void initView() {
        mBtTargetGoal = (Button) findViewById(R.id.bt_target_goal);
        mBtGameLines = (Button) findViewById(R.id.bt_game_lines);
        mBtBack = (Button) findViewById(R.id.bt_back);
        mBtDone = (Button) findViewById(R.id.bt_done);
        mTvContactMe = (TextView) findViewById(R.id.tv_contact_me);

        mBtTargetGoal.setOnClickListener(this);
        mBtGameLines.setOnClickListener(this);
        mBtBack.setOnClickListener(this);
        mBtDone.setOnClickListener(this);
        gameLines = new String[]{"4", "5", "6"};
        targetGoal = new String[]{"1024", "2048", "4096"};
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_target_goal:
                mBuilder = new AlertDialog.Builder(this);
                mBuilder.setTitle("Choose the goal of the game");
                mBuilder.setItems(targetGoal, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mBtTargetGoal.setText(targetGoal[which]);
                    }
                });
                mBuilder.show();
                break;
            case R.id.bt_game_lines:
                mBuilder = new AlertDialog.Builder(this);
                mBuilder.setTitle("Choose the game lines of the game");
                mBuilder.setItems(gameLines, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mBtGameLines.setText(gameLines[which]);
                    }
                });
                mBuilder.show();
                break;
            case R.id.bt_back:
                finish();
                break;
            case R.id.bt_done:
                saveConfig();
                setResult(RESULT_OK);
                finish();
                break;
        }
    }

    private void saveConfig() {
        SharedPreferences.Editor editor = Config.sSp.edit();
        editor.putInt(Config.KEY_GAME_GOAL, Integer.parseInt(mBtTargetGoal.getText().toString().trim()));
        editor.putInt(Config.KEY_GAME_LINES, Integer.parseInt(mBtGameLines.getText().toString().trim()));
        editor.apply();
    }
}
