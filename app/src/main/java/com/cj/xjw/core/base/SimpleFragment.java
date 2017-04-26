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

import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.yokeyword.fragmentation.SupportFragment;

/**
 *
 * 无MVP模式
 * Created by chenj on 2017/4/22.
 *
 */

public abstract class SimpleFragment extends SupportFragment {

    protected View mView;

    protected Unbinder mUnbinder;

    protected boolean mIsInited ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(getLayoutId(),container,false);
        initInject();
        return mView;
    }

    protected void initInject() {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
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

    protected abstract void init();

    protected abstract int getLayoutId();
}
