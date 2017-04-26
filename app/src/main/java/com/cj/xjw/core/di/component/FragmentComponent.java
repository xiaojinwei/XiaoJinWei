package com.cj.xjw.core.di.component;

import android.app.Activity;

import com.cj.xjw.core.di.module.FragmentModule;
import com.cj.xjw.core.di.scope.FragmentScope;
import com.cj.xjw.core.mvp.ui.main.fragment.NewsListFragment;
import com.cj.xjw.core.mvp.ui.zhihu.fragment.DailyFragment;
import com.cj.xjw.core.mvp.ui.zhihu.fragment.ZhiHuFragment;

import dagger.Component;

/**
 * Created by chenj on 2017/4/20.
 */
@FragmentScope
@Component(dependencies = AppComponent.class,modules = FragmentModule.class)
public interface FragmentComponent {
    Activity getActivity();

    void inject(NewsListFragment newsListFragment);
    void inject(ZhiHuFragment fragment);
    void inject(DailyFragment fragment);
}
