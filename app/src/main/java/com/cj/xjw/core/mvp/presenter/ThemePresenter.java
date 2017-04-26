package com.cj.xjw.core.mvp.presenter;

import com.cj.xjw.core.mvp.model.http.RetrofitHelper;
import com.cj.xjw.core.mvp.presenter.base.BasePresenterImpl;
import com.cj.xjw.core.mvp.presenter.contract.ThemeContract;

import javax.inject.Inject;

/**
 * Created by chenj on 2017/4/24.
 */

public class ThemePresenter extends BasePresenterImpl<ThemeContract.View> implements ThemeContract.Presenter {

    RetrofitHelper mRetrofitHelper;

    @Inject
    public ThemePresenter(RetrofitHelper retrofitHelper) {
        mRetrofitHelper = retrofitHelper;
    }
}
