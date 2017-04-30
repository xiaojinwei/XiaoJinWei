package com.cj.xjw.core.mvp.ui.main.fragment;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cj.chenj.recyclerview_lib.adapter.CommonAdapter;
import com.cj.chenj.recyclerview_lib.adapter.LoadMoreWrapper;
import com.cj.chenj.recyclerview_lib.adapter.MultiItemTypeSupport;
import com.cj.xjw.R;
import com.cj.xjw.base.Constants;
import com.cj.xjw.common.LoadNewsType;
import com.cj.xjw.core.base.BaseFragment;
import com.cj.xjw.core.component.RxBus;
import com.cj.xjw.core.di.component.ActivityComponent;
import com.cj.xjw.core.mvp.model.bean.NewsSummary;
import com.cj.xjw.core.mvp.model.event.TopEvent;
import com.cj.xjw.core.mvp.presenter.NewsListPresenter;
import com.cj.xjw.core.mvp.presenter.contract.NewsListContract;
import com.cj.xjw.core.mvp.ui.main.activity.NewsDetailActivity;
import com.cj.xjw.core.mvp.ui.main.adapter.NewsListAdapter;
import com.cj.xjw.core.utils.SnackbarUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by chenj on 2017/4/22.
 */

public class NewsListFragment extends BaseFragment<NewsListPresenter> implements NewsListContract.View, CommonAdapter.OnItemClickListener {

    String mType;
    String mId;
    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.swipe)
    SwipeRefreshLayout mSwipe;
    private NewsListAdapter mNewsListAdapter;

    private List<NewsSummary> mData = new ArrayList<>();

    @Inject
    Activity mActivity;
    //private LoadMoreWrapper mLoadMoreWrapper;

    @Inject
    Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initArguments();
    }

    private void registerScrollToTopEvent() {

        mPresenter.scrollTop(mRecycler);
    }

    private void initArguments() {
        if (getArguments() != null) {
            mId = getArguments().getString(Constants.NEWS_ID);
            mType = getArguments().getString(Constants.NEWS_TYPE);
        }
    }

    @Override
    protected void init() {
        registerScrollToTopEvent();

        mPresenter.setNewsTypeAndId(mType, mId);
        mPresenter.refresh();

        if (mNewsListAdapter == null) {

            mNewsListAdapter = new NewsListAdapter(_mActivity, mData, mTypeSupport);
            mRecycler.setHasFixedSize(true);
            mRecycler.setLayoutManager(new LinearLayoutManager(mActivity));
            mRecycler.setItemAnimator(new DefaultItemAnimator());
            mRecycler.setAdapter(mNewsListAdapter);
//            mLoadMoreWrapper = new LoadMoreWrapper(mNewsListAdapter);
//            mLoadMoreWrapper.setLoadMoreView(R.layout.default_loading);
//            mRecycler.setAdapter(mLoadMoreWrapper);
//            mLoadMoreWrapper.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
//                @Override
//                public void onLoadMoreRequested() {
//                    mPresenter.loadMore();
//                }
//            });
            mNewsListAdapter.setOnItemClickListener(this);
        }
        setOnListener();

        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.refresh();
            }
        });
    }

    private void setOnListener() {
        mRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    int lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                    int itemCount = layoutManager.getItemCount();
                    int childCount = layoutManager.getChildCount();
                    if (childCount > 0 && lastVisibleItemPosition >= itemCount - 1 && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        mPresenter.loadMore();
                    }
                }
            }
        });
    }

    private void completeRefresh() {
        mSwipe.setRefreshing(false);
    }

    @Override
    protected void initInject() {
        getFragmentComponent().inject(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_news_list;
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showMsg(String msg) {
        SnackbarUtil.show(mRecycler,msg);
    }


    @Override
    public void setNewsList(List<NewsSummary> newsSummaries, @LoadNewsType.Checker int type) {
        completeRefresh();
        switch (type) {
            case LoadNewsType.TYPE_REFRESH_SUCCESS:

                if (mNewsListAdapter != null) {
                    mNewsListAdapter.setDatas(newsSummaries);
                    mNewsListAdapter.notifyDataSetChanged();
                }

                Log.d("NEWS-SUCCESS",Thread.currentThread().getName());
                break;
            case LoadNewsType.TYPE_LOAD_MORE_SUCCESS:
                if (mNewsListAdapter != null) {

                    mNewsListAdapter.addMore(newsSummaries);
                }
                Log.d("NEWS-MORE",Thread.currentThread().getName());
                break;

        }

    }



    MultiItemTypeSupport mTypeSupport = new MultiItemTypeSupport<NewsSummary>() {
        @Override
        public int getItemViewType(int position, NewsSummary item) {
            int type;
            if (!TextUtils.isEmpty(item.getDigest())) {
                type = NewsListAdapter.TYPE_ITEM;
            }else{
                type = NewsListAdapter.TYPE_PHOTO_ITEM;
            }
            return type;
        }

        @Override
        public int getItemLayoutId(int itemType) {
            int layoutId ;
            if (itemType == NewsListAdapter.TYPE_ITEM) {
                layoutId = R.layout.news_list_item;
            }else{
                layoutId = R.layout.news_list_photo_item;
            }
            return layoutId;
        }
    };

    @Override
    public void onItemClick(RecyclerView.Adapter<?> parent, RecyclerView.ViewHolder viewHolder, View view, int position, int viewType) {
        if (viewType == NewsListAdapter.TYPE_ITEM) {
            goToNewsDetailActivity(view,position);
        }else{

        }
    }

    private void goToNewsDetailActivity(View view, int position) {
        Intent intent = getIntent(position);
        startActivity(view,intent);
    }

    private void startActivity(View view, Intent intent) {
        ImageView newsSummaryPhotoIv = (ImageView) view.findViewById(R.id.news_summary_photo_iv);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(mActivity, newsSummaryPhotoIv, Constants.TRANSITION_ANIMATION_NEWS_PHOTOS);
            startActivity(intent,options.toBundle());
        }else{
            ActivityOptionsCompat compat = ActivityOptionsCompat
                    .makeScaleUpAnimation(view,view.getWidth() / 2,view.getHeight() / 2,0,0);
            ActivityCompat.startActivity(mActivity,intent,compat.toBundle());
        }
    }


    private Intent getIntent(int position) {
        NewsSummary newsSummary = mData.get(position);
        Intent intent = new Intent(mActivity, NewsDetailActivity.class);
        intent.putExtra(Constants.NEWS_POST_ID, newsSummary.getPostid());
        intent.putExtra(Constants.NEWS_IMG_RES,newsSummary.getImgsrc());

        return intent;
    }
}
