package com.cj.xjw.core.mvp.presenter;

import com.cj.xjw.core.component.RxUtil;
import com.cj.xjw.core.mvp.model.bean.SectionListBean;
import com.cj.xjw.core.mvp.model.http.RetrofitHelper;
import com.cj.xjw.core.mvp.presenter.base.BasePresenterImpl;
import com.cj.xjw.core.mvp.presenter.contract.SectionContract;
import com.cj.xjw.core.mvp.presenter.subscriber.CommonSubscriber;

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

    @Override
    public void getSectionList() {
        addSubscribe(mRetrofitHelper.getSectionList()
                .compose(RxUtil.<SectionListBean>defalutSchedule())
                .subscribeWith(new CommonSubscriber<SectionListBean>(mView) {
                    @Override
                    public void onNext(SectionListBean sectionListBean) {
                        mView.hideProgress();
                        mView.setSectionList(sectionListBean);
                    }
                }));
    }
}
