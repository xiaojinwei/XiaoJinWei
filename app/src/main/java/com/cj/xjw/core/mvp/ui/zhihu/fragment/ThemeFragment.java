package com.cj.xjw.core.mvp.ui.zhihu.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cj.xjw.R;
import com.cj.xjw.core.base.BaseFragment;
import com.cj.xjw.core.mvp.model.bean.ThemeListBean;
import com.cj.xjw.core.mvp.presenter.ThemePresenter;
import com.cj.xjw.core.mvp.presenter.contract.ThemeContract;
import com.cj.xjw.core.mvp.ui.zhihu.adapter.ThemeAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by chenj on 2017/4/24.
 */

public class ThemeFragment extends BaseFragment<ThemePresenter> implements ThemeContract.View {
    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.swipe)
    SwipeRefreshLayout mSwipe;

    @Inject
    Activity mActivity;

    List<ThemeListBean.OthersBean> mData = new ArrayList<>();
    private ThemeAdapter mThemeAdapter;

    @Override
    protected void init() {
        mPresenter.getThemeList();
        mThemeAdapter = new ThemeAdapter(mActivity, R.layout.zhihu_theme_item,mData);
        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(new GridLayoutManager(mActivity,2));
        mRecycler.setAdapter(mThemeAdapter);
    }

    @Override
    protected void initInject() {
        getFragmentComponent().inject(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_theme;
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

    @Override
    public void setThemeList(ThemeListBean themeList) {
        if (themeList != null && themeList.getOthers() != null) {
            mThemeAdapter.addMore(themeList.getOthers());
        }

        Log.d("",themeList.toString());
    }


}
