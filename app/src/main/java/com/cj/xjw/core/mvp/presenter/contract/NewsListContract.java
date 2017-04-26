package com.cj.xjw.core.mvp.presenter.contract;

import com.cj.xjw.common.LoadNewsType;
import com.cj.xjw.core.base.BaseFragment;
import com.cj.xjw.core.mvp.model.bean.NewsSummary;
import com.cj.xjw.core.mvp.presenter.base.BasePresenter;
import com.cj.xjw.core.mvp.view.BaseView;

import java.util.List;

/**
 * Created by chenj on 2017/4/22.
 */

public interface NewsListContract {
    interface View extends BaseView{
        void setNewsList(List<NewsSummary> newsSummaries, @LoadNewsType.Checker int type);
    }
    interface Presenter extends BasePresenter<View>{

        //void loadNews(String type, String id, int startPage);

        void setNewsTypeAndId(String type, String id);
        void refresh();
        void loadMore();
    }
}
