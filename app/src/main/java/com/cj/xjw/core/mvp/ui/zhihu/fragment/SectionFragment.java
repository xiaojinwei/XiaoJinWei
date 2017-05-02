package com.cj.xjw.core.mvp.ui.zhihu.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cj.xjw.R;
import com.cj.xjw.core.base.BaseFragment;
import com.cj.xjw.core.mvp.model.bean.SectionListBean;
import com.cj.xjw.core.mvp.presenter.SectionPresenter;
import com.cj.xjw.core.mvp.presenter.contract.SectionContract;
import com.cj.xjw.core.mvp.ui.zhihu.adapter.SectionAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chenj on 2017/4/24.
 */

public class SectionFragment extends BaseFragment<SectionPresenter> implements SectionContract.View {
    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.swipe)
    SwipeRefreshLayout mSwipe;

    @Inject
    Activity mActivity;

    List<SectionListBean.DataBean> mData = new ArrayList<>();
    private SectionAdapter mSectionAdapter;

    @Override
    protected void init() {
        mPresenter.getSectionList();

        mSectionAdapter = new SectionAdapter(
               mActivity, R.layout.zhihu_section_item, mData
        );
        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(new GridLayoutManager(mActivity,2));
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mRecycler.setAdapter(mSectionAdapter);

        initEvent();

    }

    private void initEvent() {
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getSectionList();
            }
        });
    }

    @Override
    protected void initInject() {
        getFragmentComponent().inject(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_section;
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
    public void setSectionList(SectionListBean sectionList) {
        if (mSwipe.isRefreshing()) {
            mSwipe.setRefreshing(false);
        }
        if (sectionList != null && sectionList.getData() != null) {
            //mSectionAdapter.addMore(sectionList.getData());
            mSectionAdapter.refreshData(sectionList.getData());
        }
    }
}
