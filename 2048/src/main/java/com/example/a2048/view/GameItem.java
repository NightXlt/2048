package com.example.a2048.view;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by Night on 2016/6/13.
 * Desc:Game Item
 */

public class GameItem extends FrameLayout {


    private int mItemNum;
    private TextView mTVNum;

    public GameItem(Context context, int itemNum) {
        super(context);
        mItemNum = itemNum;
        initGameItem();
    }

    private void initGameItem() {
        //设置背景色
        setBackgroundColor(Color.GRAY);
        mTVNum = new TextView(getContext());
        setItemNum(mItemNum);
        if (mItemNum == 4) {
            mTVNum.setTextSize(35);
        } else if (mItemNum == 5) {
            mTVNum.setTextSize(25);
        } else {
            mTVNum.setTextSize(20);
        }
        mTVNum.getPaint().setFakeBoldText(true);
        mTVNum.setGravity(Gravity.CENTER);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.setMargins(5, 5, 5, 5);
        addView(mTVNum, lp);
    }


    public void setItemNum(int itemNum) {
        mItemNum = itemNum;
        if (itemNum == 0) {
            mTVNum.setText("");
        } else {
            mTVNum.setText("" + itemNum);
        }

        switch (itemNum) {
            case 0:
                mTVNum.setBackgroundColor(0xCCBFB3);
                break;
            case 2:
                mTVNum.setBackgroundColor(Color.argb(255,237,227,217));
                break;
            case 4:
                mTVNum.setBackgroundColor(Color.argb(255,236 ,223 ,199));
                break;
            case 8:
                mTVNum.setBackgroundColor(Color.argb(255,241,176,121));
                break;
            case 16:
                mTVNum.setBackgroundColor(Color.argb(255,244,148,99));
                break;
            case 32:
                mTVNum.setBackgroundColor(Color.argb(255,245,124,95));
                break;
            case 64:
                mTVNum.setBackgroundColor(Color.argb(255,245,94,59));
                break;
            case 128:
                mTVNum.setBackgroundColor(Color.argb(255,236,206,114));
                break;
            case 256:
                mTVNum.setBackgroundColor(Color.argb(255,236,203,97));
                break;
            case 512:
                mTVNum.setBackgroundColor(Color.argb(255,236,199,80));
                break;
            case 1024:
                mTVNum.setBackgroundColor(0xffedc54f);
                break;
            case 2048:
                mTVNum.setBackgroundColor(0xffedc32e);
                break;
            default:
                mTVNum.setBackgroundColor(0xff3c4a34);
                break;
        }
    }

    public int getItemNum() {
        return mItemNum;
    }
    public View getItemView() {
        return mTVNum;
    }
}
