package com.cj.xjw.core.mvp.presenter;

import com.cj.xjw.core.component.RxUtil;
import com.cj.xjw.core.mvp.model.bean.ThemeListBean;
import com.cj.xjw.core.mvp.model.http.RetrofitHelper;
import com.cj.xjw.core.mvp.presenter.base.BasePresenterImpl;
import com.cj.xjw.core.mvp.presenter.contract.ThemeContract;
import com.cj.xjw.core.mvp.presenter.subscriber.CommonSubscriber;

import javax.inject.Inject;

import io.reactivex.Flowable;

/**
 * Created by chenj on 2017/4/24.
 */

public class ThemePresenter extends BasePresenterImpl<ThemeContract.View> implements ThemeContract.Presenter {

    RetrofitHelper mRetrofitHelper;

    @Inject
    public ThemePresenter(RetrofitHelper retrofitHelper) {
        mRetrofitHelper = retrofitHelper;
    }

    @Override
    public void getThemeList() {
        Flowable<ThemeListBean> themeList = mRetrofitHelper.getThemeList();
        themeList.compose(RxUtil.<ThemeListBean>defalutSchedule())
                .subscribeWith(new CommonSubscriber<ThemeListBean>(mView) {
                    @Override
                    public void onNext(ThemeListBean themeListBean) {
                        mView.setThemeList(themeListBean);
                    }
                });
    }
}
