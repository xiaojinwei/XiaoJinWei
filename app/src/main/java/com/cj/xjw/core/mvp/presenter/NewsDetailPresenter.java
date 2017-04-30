package com.cj.xjw.core.mvp.presenter;

import com.cj.xjw.base.App;
import com.cj.xjw.core.component.RxUtil;
import com.cj.xjw.core.mvp.model.bean.NewsDetail;
import com.cj.xjw.core.mvp.model.http.RetrofitHelper;
import com.cj.xjw.core.mvp.presenter.base.BasePresenterImpl;
import com.cj.xjw.core.mvp.presenter.contract.NewsDetailContract;
import com.cj.xjw.core.utils.HttpUtil;
import com.cj.xjw.core.utils.Util;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

/**
 * Created by cj_28 on 2017/4/29.
 */

public class NewsDetailPresenter extends BasePresenterImpl<NewsDetailContract.View> implements NewsDetailContract.Presenter {


    RetrofitHelper mRetrofitHelper;

    @Inject
    public NewsDetailPresenter(RetrofitHelper retrofitHelper) {
        mRetrofitHelper = retrofitHelper;
    }


    @Override
    public void loadNewsDetail(final String postId) {
        mRetrofitHelper.getNewDetail(postId)
                .map(new Function<Map<String,NewsDetail>, NewsDetail>() {
                    @Override
                    public NewsDetail apply(@NonNull Map<String, NewsDetail> map) throws Exception {
                        NewsDetail newsDetail = map.get(postId);
                        changeNewsDetail(newsDetail);
                        return newsDetail;
                    }
                }).compose(RxUtil.<NewsDetail>defalutObservableSchedule())
                .subscribe(new Observer<NewsDetail>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addSubscribe(d);
                    }

                    @Override
                    public void onNext(NewsDetail newsDetail) {
                        mView.setNewsDetail(newsDetail);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showMsg(HttpUtil.analyzeNetworkError(e));
                        mView.hideProgress();
                    }

                    @Override
                    public void onComplete() {
                        mView.hideProgress();
                    }
                });
    }

    private void changeNewsDetail(NewsDetail newsDetail) {
        List<NewsDetail.ImgBean> imgSrcs = newsDetail.getImg();
        if (isChange(imgSrcs)) {
            String newsBody = newsDetail.getBody();
            newsBody = changeNewsBody(imgSrcs, newsBody);
            newsDetail.setBody(newsBody);
        }
    }

    private boolean isChange(List<NewsDetail.ImgBean> imgSrcs) {
        return imgSrcs != null && imgSrcs.size() >= 2 && App.isHavePhoto();
    }


    private String changeNewsBody(List<NewsDetail.ImgBean> imgSrcs, String newsBody) {
        for (int i = 0; i < imgSrcs.size(); i++) {
            String oldChars = "<!--IMG#" + i + "-->";
            String newChars;
            if (i == 0) {
                newChars = "";
            } else {
                newChars = "<img src=\"" + imgSrcs.get(i).getSrc() + "\" />";
            }
            newsBody = newsBody.replace(oldChars, newChars);
        }
        return newsBody;
    }
}
