package com.cj.xjw.core.mvp.presenter;

import com.cj.xjw.core.mvp.model.http.RetrofitHelper;
import com.cj.xjw.core.mvp.presenter.base.BasePresenterImpl;
import com.cj.xjw.core.mvp.presenter.contract.MainContract;

import org.openjdk.tools.javadoc.internal.tool.Main;

import javax.inject.Inject;

/**
 * Created by chenj on 2017/4/21.
 */

public class MainPresenter extends BasePresenterImpl<MainContract.View> implements MainContract.Presenter {

    private RetrofitHelper mRetrofitHelper;

    @Inject
    public MainPresenter(RetrofitHelper retrofitHelper) {
        mRetrofitHelper = retrofitHelper;
    }


}
