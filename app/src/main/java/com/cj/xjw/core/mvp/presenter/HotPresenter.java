package com.cj.xjw.core.mvp.presenter;

import com.cj.xjw.core.mvp.model.http.RetrofitHelper;
import com.cj.xjw.core.mvp.presenter.base.BasePresenterImpl;
import com.cj.xjw.core.mvp.presenter.contract.HotContract;

import javax.inject.Inject;

/**
 * Created by chenj on 2017/4/24.
 */

public class HotPresenter extends BasePresenterImpl<HotContract.View> implements HotContract.Presenter {

    RetrofitHelper mRetrofitHelper;

    @Inject
    public HotPresenter(RetrofitHelper retrofitHelper) {
        mRetrofitHelper = retrofitHelper;
    }
}
