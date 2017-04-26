package com.cj.chenj.recyclerview_lib.layoutmanager;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by chenj on 2017/1/17.
 */

public class CardConfig {
    //屏幕上最多同时显示几个Item
    public static int MAX_SHOW_COUNT;
    //每一个Scale相差0.05f
    public static float SCALE_GAP;
    //translationY相差7dp左右
    public static int TRANS_Y_GAP;
    //最大旋转角度
    public static int MAX_ROTATION;

    public static void config(Context context){
        MAX_SHOW_COUNT = 6;
        SCALE_GAP = 0.05F;
        MAX_ROTATION = 30;
        TRANS_Y_GAP = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                15,context.getResources().getDisplayMetrics());
    }
}
