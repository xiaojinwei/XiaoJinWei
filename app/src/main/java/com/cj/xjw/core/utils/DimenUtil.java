package com.cj.xjw.core.utils;

import com.cj.xjw.base.App;

/**
 * Created by chenj on 2017/4/22.
 */

public class DimenUtil {
    public static float dp2px(float dp) {
        final float scale = App.getApplication().getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static float sp2px(float sp) {
        final float scale = App.getApplication().getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    public static int getScreenSize() {
        return App.getApplication().getResources().getDisplayMetrics().widthPixels;
    }
}
