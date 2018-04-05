package com.example.a2048;

import android.app.Application;
import android.content.SharedPreferences;

/**
 * Created by Night on 2016/6/6.
 * Desc:
 */

public class Config extends Application {

    public static SharedPreferences sSp;
    //游戏目标
    public static int sGameGoal;
    //记录分数
    public static int sScore=0;

    public static int sItemSize;

    public static int sGameLines;

    public static final String SP_NAME="2048";

    public static final String KEY_HIGH_SCORE="KEY_HIGH_SCORE";

    public static final String KEY_GAME_LINES="KEY_GAME_LINES";

    public static final String KEY_GAME_GOAL="KEY_GAME_GOAL";

    @Override
    public void onCreate() {
        super.onCreate();
        sSp = getSharedPreferences(SP_NAME, 0);
        sItemSize=0;
        sGameLines = sSp.getInt(KEY_GAME_LINES, 4);
        sGameGoal = sSp.getInt(KEY_GAME_GOAL, 2048);

    }

}
