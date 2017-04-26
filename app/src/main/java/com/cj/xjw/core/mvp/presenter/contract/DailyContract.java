package com.cj.xjw.core.mvp.presenter.contract;

import com.cj.xjw.core.mvp.model.bean.DailyListBean;
import com.cj.xjw.core.mvp.presenter.base.BasePresenter;
import com.cj.xjw.core.mvp.view.BaseView;

/**
 * Created by chenj on 2017/4/24.
 */

public interface DailyContract {
    interface View extends BaseView{
        void setContentData(DailyListBean contentData);
    }

    interface Presenter extends BasePresenter<View>{
        void getContentData();
    }
}
