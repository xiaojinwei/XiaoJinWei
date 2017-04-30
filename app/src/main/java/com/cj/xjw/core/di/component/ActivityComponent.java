package com.cj.xjw.core.di.component;

import android.app.Activity;

import com.cj.xjw.core.di.module.ActivityModule;
import com.cj.xjw.core.di.scope.ActivityScope;
import com.cj.xjw.core.mvp.ui.main.activity.MainActivity;
import com.cj.xjw.core.mvp.ui.main.activity.NewsDetailActivity;

import dagger.Component;

/**
 * Created by chenj on 2017/4/20.
 */
@ActivityScope
@Component(dependencies = AppComponent.class,modules = ActivityModule.class)
public interface ActivityComponent {
    Activity getActivity();

    void inject(MainActivity mainActivity);
    void inject(NewsDetailActivity activity);
}
