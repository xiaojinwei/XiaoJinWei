package com.cj.xjw.core.mvp.ui.zhihu.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cj.xjw.R;
import com.cj.xjw.core.base.BaseFragment;
import com.cj.xjw.core.mvp.model.bean.HotListBean;
import com.cj.xjw.core.mvp.presenter.HotPresenter;
import com.cj.xjw.core.mvp.presenter.contract.HotContract;
import com.cj.xjw.core.mvp.ui.zhihu.adapter.HotAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chenj on 2017/4/24.
 */

public class HotFragment extends BaseFragment<HotPresenter> implements HotContract.View {
    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.swipe)
    SwipeRefreshLayout mSwipe;

    @Inject
    Activity mActivity;

    List<HotListBean.RecentBean> mData = new ArrayList<>();
    private HotAdapter mHotAdapter;

    @Override
    protected void init() {
        mPresenter.getHotList();

        mHotAdapter = new HotAdapter(
                mActivity, R.layout.zhihu_hot_item,   mData
        );
        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mRecycler.setAdapter(mHotAdapter);

        initEvent();

    }

    private void initEvent() {
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getHotList();
            }
        });
    }

    @Override
    protected void initInject() {
        getFragmentComponent().inject(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_hot;
    }

    @Override
    public void showProgress() {
        mStatusLayoutManager.showLoading();
    }

    @Override
    public void hideProgress() {
        mStatusLayoutManager.showContent();
    }

    @Override
    public void showMsg(String msg) {
        mStatusLayoutManager.showError();
    }

    @Override
    public void setHotList(HotListBean hotListBean) {
        if (mSwipe.isRefreshing()) {
            mSwipe.setRefreshing(false);
        }
        if (hotListBean != null && hotListBean.getRecent() != null) {
            //mHotAdapter.addMore(hotListBean.getRecent());
            mHotAdapter.refreshData(hotListBean.getRecent());
        }
    }
}
