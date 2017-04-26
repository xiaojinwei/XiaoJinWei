package com.cj.xjw.core.mvp.presenter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.cj.xjw.base.ApiConstans;
import com.cj.xjw.common.LoadNewsType;
import com.cj.xjw.core.component.RxBus;
import com.cj.xjw.core.mvp.model.bean.NewsSummary;
import com.cj.xjw.core.mvp.model.event.TopEvent;
import com.cj.xjw.core.mvp.model.http.RetrofitHelper;
import com.cj.xjw.core.mvp.presenter.base.BasePresenterImpl;
import com.cj.xjw.core.mvp.presenter.contract.NewsListContract;
import com.cj.xjw.core.utils.HttpUtil;
import com.cj.xjw.core.component.RxUtil;

import org.reactivestreams.Publisher;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.subscribers.ResourceSubscriber;

/**
 * Created by chenj on 2017/4/22.
 */

public class NewsListPresenter extends BasePresenterImpl<NewsListContract.View> implements NewsListContract.Presenter {

    RetrofitHelper mRetrofitHelper;

    String mType;
    String mId;

    int mPageIndex;

    boolean mIsRefresh;

    @Inject
    public NewsListPresenter(RetrofitHelper retrofitHelper) {
        mRetrofitHelper = retrofitHelper;
        if (mView != null) {
            mView.showProgress();
        }
    }


    public void loadNews(String type, final String id, int startPage) {

        final Flowable<Map<String, List<NewsSummary>>> newsList = mRetrofitHelper.getNewsList(type, id, startPage);

        ResourceSubscriber<List<NewsSummary>> resourceSubscriber = newsList.flatMap(new Function<Map<String, List<NewsSummary>>, Publisher<List<NewsSummary>>>() {
                                                                                        @Override
                                                                                        public Publisher<List<NewsSummary>> apply(@NonNull Map<String, List<NewsSummary>> stringListMap) throws Exception {
                                                                                            if (id.endsWith(ApiConstans.HOUSE_ID)) {
                                                                                                // 房产实际上针对地区的它的id与返回key不同
                                                                                                return Flowable.fromArray(stringListMap.get("北京"));
                                                                                            }
                                                                                            return Flowable.fromArray(stringListMap.get(id));
                                                                                        }
                                                                                    }
        )
//                .flatMap(new Function<List<NewsSummary>, Publisher<NewsSummary>>() {
//            @Override
//            public Publisher<NewsSummary> apply(@NonNull List<NewsSummary> newsSummaries) throws Exception {
//                NewsSummary[] newsSummaries1 = newsSummaries.toArray(new NewsSummary[newsSummaries.size()]);
//                return Flowable.fromArray(newsSummaries1);
//            }
//        })
//                .map(new Function<NewsSummary, NewsSummary>() {
//            @Override
//            public NewsSummary apply(@NonNull NewsSummary newsSummary) throws Exception {
//                String pTime = DateUtil.formatDate(newsSummary.getPtime());
//                newsSummary.setPtime(pTime);
//                return newsSummary;
//            }
//        })
                .compose(RxUtil.<List<NewsSummary>>defalutSchedule())

//                .distinct()
//                .toSortedList(new Comparator<NewsSummary>() {
//                    @Override
//                    public int compare(NewsSummary o1, NewsSummary o2) {
//                        return o2.getPtime().compareTo(o1.getPtime());
//                    }
//                })

                .subscribeWith(new ResourceSubscriber<List<NewsSummary>>() {
                    @Override
                    public void onNext(List<NewsSummary> newsSummary) {
                        Log.d("NEWS", newsSummary.toString());
                        success(newsSummary);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.d("NEWS", t.toString());
                        NewsListPresenter.this.onError(HttpUtil.analyzeNetworkError(t));
                    }

                    @Override
                    public void onComplete() {
                        Log.d("NEWS", "onComplete");
                    }
                });
        addSubscribe(resourceSubscriber);


    }

    protected void onError(String e) {
        if (mView != null) {
            int loadType = mIsRefresh ? LoadNewsType.TYPE_REFRESH_ERROR : LoadNewsType.TYPE_LOAD_MORE_ERROR;
            mView.hideProgress();
            mView.showMsg(e);
            mView.setNewsList(null, loadType);
        }
    }

    protected void success(List<NewsSummary> newsSummary) {
        if (newsSummary != null && newsSummary.size() > 0) {
            mPageIndex += 20;

            int loadType = mIsRefresh ? LoadNewsType.TYPE_REFRESH_SUCCESS : LoadNewsType.TYPE_LOAD_MORE_SUCCESS;

            if (mView != null) {
                mView.hideProgress();
                mView.setNewsList(newsSummary,loadType);
            }

        }else{

        }
    }

    @Override
    public void setNewsTypeAndId(String type, String id) {
        this.mType = type;
        this.mId = id;
    }

    @Override
    public void refresh() {
        mPageIndex = 0;
        mIsRefresh = true;
        loadNews(mType,mId,mPageIndex);
    }

    @Override
    public void loadMore() {
        mIsRefresh = false;
        loadNews(mType,mId,mPageIndex);
    }

    /**
     * 滚动到顶部
     * @param recycler
     */
    public void scrollTop(final RecyclerView recycler) {
        Disposable subscribe = RxBus.getInstance().toFlowable(TopEvent.class)
                .subscribe(new Consumer<TopEvent>() {
                    @Override
                    public void accept(@NonNull TopEvent topEvent) throws Exception {
                        recycler.scrollToPosition(0);
                    }
                });
        addSubscribe(subscribe);
    }
}
