package com.cj.xjw.core.mvp.ui.zhihu.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cj.xjw.R;
import com.cj.xjw.core.base.BaseFragment;
import com.cj.xjw.core.mvp.presenter.SectionPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chenj on 2017/4/24.
 */

public class SectionFragment extends BaseFragment<SectionPresenter> {
    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.swipe)
    SwipeRefreshLayout mSwipe;

    @Override
    protected void init() {

    }

    @Override
    protected void initInject() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_section;
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showMsg(String msg) {

    }

}
