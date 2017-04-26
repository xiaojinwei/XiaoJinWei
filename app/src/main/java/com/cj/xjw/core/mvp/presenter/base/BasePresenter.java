package com.cj.xjw.core.mvp.presenter.base;

import android.support.annotation.NonNull;

import com.cj.xjw.core.mvp.view.BaseView;

/**
 * Created by chenj on 2017/4/7.
 */

public interface BasePresenter<V extends BaseView> {

    void onCreate();

    void attachView(@NonNull V view);

    void onDestroy();
}
