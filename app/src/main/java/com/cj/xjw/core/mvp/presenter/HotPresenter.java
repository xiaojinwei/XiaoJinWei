package com.cj.xjw.core.mvp.presenter;

import com.cj.xjw.core.component.RxUtil;
import com.cj.xjw.core.mvp.model.bean.HotListBean;
import com.cj.xjw.core.mvp.model.http.RetrofitHelper;
import com.cj.xjw.core.mvp.presenter.base.BasePresenterImpl;
import com.cj.xjw.core.mvp.presenter.contract.HotContract;
import com.cj.xjw.core.mvp.presenter.subscriber.CommonSubscriber;

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

    @Override
    public void getHotList() {
        addSubscribe(mRetrofitHelper.getHotList()
                    .compose(RxUtil.<HotListBean>defalutSchedule())
                    .subscribeWith(new CommonSubscriber<HotListBean>(mView){

                        @Override
                        public void onNext(HotListBean hotListBean) {
                            mView.hideProgress();
                            mView.setHotList(hotListBean);
                        }
                    }));
    }
}
