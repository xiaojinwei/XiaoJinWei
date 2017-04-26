package com.cj.xjw.core.mvp.presenter;

import com.cj.xjw.core.mvp.model.http.RetrofitHelper;
import com.cj.xjw.core.mvp.presenter.base.BasePresenterImpl;
import com.cj.xjw.core.mvp.presenter.contract.SectionContract;

import javax.inject.Inject;

/**
 * Created by chenj on 2017/4/24.
 */

public class SectionPresenter extends BasePresenterImpl<SectionContract.View> implements SectionContract.Presenter {

    RetrofitHelper mRetrofitHelper;

    @Inject
    public SectionPresenter(RetrofitHelper retrofitHelper) {
        mRetrofitHelper = retrofitHelper;
    }
}
