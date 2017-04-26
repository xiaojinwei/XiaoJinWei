package com.cj.xjw.core.mvp.presenter;

import com.cj.xjw.core.mvp.model.bean.DailyListBean;
import com.cj.xjw.core.mvp.model.http.RetrofitHelper;
import com.cj.xjw.core.mvp.presenter.base.BasePresenterImpl;
import com.cj.xjw.core.mvp.presenter.contract.DailyContract;
import com.cj.xjw.core.component.RxUtil;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.subscribers.ResourceSubscriber;

/**
 * Created by chenj on 2017/4/24.
 */

public class DailyPresenter extends BasePresenterImpl<DailyContract.View> implements DailyContract.Presenter {

    RetrofitHelper mRetrofitHelper;

    @Inject
    public DailyPresenter(RetrofitHelper retrofitHelper) {
        mRetrofitHelper = retrofitHelper;
    }

    @Override
    public void getContentData() {
        Flowable<DailyListBean> dailyList = mRetrofitHelper.getDailyList();
        ResourceSubscriber<DailyListBean> resourceSubscriber = dailyList.compose(RxUtil.<DailyListBean>defalutSchedule())
                .subscribeWith(new ResourceSubscriber<DailyListBean>() {
                    @Override
                    public void onNext(DailyListBean dailyListBean) {
                        mView.hideProgress();
                        mView.setContentData(dailyListBean);
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        addSubscribe(resourceSubscriber);
    }
}
