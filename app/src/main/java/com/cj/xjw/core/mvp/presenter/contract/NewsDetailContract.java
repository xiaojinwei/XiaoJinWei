package com.cj.xjw.core.mvp.presenter.contract;

import com.cj.xjw.core.mvp.model.bean.NewsDetail;
import com.cj.xjw.core.mvp.presenter.base.BasePresenter;
import com.cj.xjw.core.mvp.view.BaseView;

/**
 * Created by cj_28 on 2017/4/29.
 */

public interface NewsDetailContract {
    interface View extends BaseView{
        void setNewsDetail(NewsDetail newsDetail);
    }
    interface Presenter extends BasePresenter<View>{
        void loadNewsDetail(String postId);
    }
}
