package com.cj.xjw.core.mvp.presenter.contract;

import com.cj.xjw.core.mvp.model.bean.ThemeListBean;
import com.cj.xjw.core.mvp.presenter.base.BasePresenter;
import com.cj.xjw.core.mvp.view.BaseView;

/**
 * Created by chenj on 2017/4/24.
 */

public interface ThemeContract {
    interface View extends BaseView {
        void setThemeList(ThemeListBean themeList);
    }

    interface Presenter extends BasePresenter<View> {
        void getThemeList();
    }
}
