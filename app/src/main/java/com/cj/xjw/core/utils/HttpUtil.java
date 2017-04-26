package com.cj.xjw.core.utils;

import com.cj.xjw.R;
import com.cj.xjw.base.App;

import retrofit2.adapter.rxjava2.HttpException;

/**
 * Created by cj_28 on 2017/4/23.
 */

public class HttpUtil {

    public static String analyzeNetworkError(Throwable throwable) {
        String error = App.getApplication().getResources().getString(R.string.load_error);
        if (throwable instanceof HttpException) {
            int code = ((HttpException) throwable).code();
            if (code == 403) {
                error = App.getApplication().getString(R.string.retry_after);
            }
        }
        return error;
    }
}
