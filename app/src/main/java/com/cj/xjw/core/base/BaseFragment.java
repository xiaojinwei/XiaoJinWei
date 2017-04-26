package com.cj.xjw.core.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cj.xjw.base.App;
import com.cj.xjw.core.di.component.DaggerFragmentComponent;
import com.cj.xjw.core.di.component.FragmentComponent;
import com.cj.xjw.core.di.module.FragmentModule;
import com.cj.xjw.core.mvp.presenter.base.BasePresenter;
import com.cj.xjw.core.mvp.view.BaseView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by chenj on 2017/4/20.
 */

public abstract class BaseFragment<T extends BasePresenter> extends SupportFragment implements BaseView {
    @Inject
    protected T mPresenter;
    private Unbinder mUnbinder;

    protected  View mView;

    protected boolean mIsInited ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(getLayoutId(),container,false);
        initInject();
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
        mUnbinder = ButterKnife.bind(this,view);
        if (savedInstanceState == null) {
            if (!isHidden()) {
                mIsInited = true;
                init();
            }
        }else{
            if (!isSupportVisible()) {
                mIsInited = true;
                init();
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!mIsInited && !hidden) {
            mIsInited = true;
            init();
        }
    }



    protected FragmentComponent getFragmentComponent() {
        return DaggerFragmentComponent.builder()
                .appComponent(App.getApplication().getAppComponent())
                .fragmentModule(getFragmentModule())
                .build();
    }

    protected FragmentModule getFragmentModule() {
        return new FragmentModule(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

    protected abstract void init();

    protected abstract void initInject();

    protected abstract int getLayoutId();
}
