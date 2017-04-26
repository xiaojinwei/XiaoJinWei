package com.cj.xjw.core.utils;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by cj_28 on 2017/4/23.
 */

public class SnackbarUtil {

    public static void show(View view, int resId) {
        Snackbar.make(view,resId,Snackbar.LENGTH_SHORT).show();
    }

    public static void show(View view, String msg) {
        Snackbar.make(view,msg,Snackbar.LENGTH_SHORT).show();
    }
}
