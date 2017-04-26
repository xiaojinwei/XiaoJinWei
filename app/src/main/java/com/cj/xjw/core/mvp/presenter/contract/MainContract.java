package com.cj.xjw.core.mvp.presenter.contract;

import android.view.View;

import com.cj.xjw.core.mvp.presenter.base.BasePresenter;
import com.cj.xjw.core.mvp.view.BaseView;

/**
 * Created by chenj on 2017/4/21.
 */

public interface MainContract {

    interface View extends BaseView{

    }

    interface Presenter extends BasePresenter<View>{

    }

}
