package com.cj.xjw.core.mvp.presenter.contract;

import com.cj.xjw.core.mvp.model.bean.HotListBean;
import com.cj.xjw.core.mvp.presenter.base.BasePresenter;
import com.cj.xjw.core.mvp.view.BaseView;

/**
 * Created by chenj on 2017/4/24.
 */

public interface HotContract {
    interface View extends BaseView {
        void setHotList(HotListBean hotListBean);
    }

    interface Presenter extends BasePresenter<View> {
        void getHotList();
    }
}
