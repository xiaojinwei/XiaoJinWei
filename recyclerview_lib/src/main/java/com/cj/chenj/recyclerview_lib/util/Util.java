package com.cj.chenj.recyclerview_lib.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.lang.reflect.Field;

/**
 * Created by chenj on 2017/1/7.
 */

public class Util {
    /**
     * 根部DecorView中的任意一个view，找到DecorView
     * @param view
     * @return
     */
    public static ViewGroup findSuitableParent(View view) {
        ViewGroup fallback = null;
        do {
            if (view instanceof FrameLayout) {
                if (view.getId() == android.R.id.content) {
                    // If we've hit the decor content view, then we didn't find a CoL in the
                    // hierarchy, so use it.
                    return (ViewGroup) view;
                } else {
                    // It's not the content view but we'll use it as our fallback
                    fallback = (ViewGroup) view;
                }
            }

            if (view != null) {
                // Else, we will loop and crawl up the view hierarchy and try to find a parent
                final ViewParent parent = view.getParent();
                view = parent instanceof View ? (View) parent : null;
            }
        } while (view != null);

        // If we reach here then we didn't find a CoL or a suitable content view so we'll fallback
        return fallback;
    }

    /**
     * 获取通知栏的高度
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;

        Object obj = null;

        Field field = null;

        int x = 0, sbar = 0;

        try {

            c = Class.forName("com.android.internal.R$dimen");

            obj = c.newInstance();

            field = c.getField("status_bar_height");

            x = Integer.parseInt(field.get(obj).toString());

            sbar = context.getResources().getDimensionPixelSize(x);

        } catch (Exception e1) {

            e1.printStackTrace();

        }

        return sbar;
    }

    /**
     * 获取屏幕大小
     * @param context
     * @return
     */
    public static int[] getScreenSize(Context context){
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);

        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        return new int[]{width,height};
    }
}
