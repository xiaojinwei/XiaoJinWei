package com.cj.xjw.core.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.design.widget.TabLayout;
import android.view.View;

import com.cj.xjw.base.App;

/**
 * Created by chenj on 2017/4/17.
 */

public class Util {

    /**
     * 检查是否有可用网络
     */
    public static boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) App.getApplication().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null;
    }

    public static void dynamicSetTabLayoutMode(TabLayout tabLayout) {
        int tabWidth = calculateTabWidth(tabLayout);
        int screenWidth = getScreenWidth();
        if (tabWidth <= screenWidth) {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        } else {
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
    }

    private static int getScreenWidth() {
        return App.getApplication().getResources().getDisplayMetrics().widthPixels;
    }

    private static int calculateTabWidth(TabLayout tabLayout) {
        int tabWidth = 0;
        for (int i = 0; i < tabLayout.getChildCount(); i++) {
            View childAt = tabLayout.getChildAt(i);
            // 通知父view测量，以便于能够保证获取到宽高
            childAt.measure(0,0);
            tabWidth += tabLayout.getMeasuredWidth();
        }
        return tabWidth;
    }



}
