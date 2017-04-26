package com.cj.xjw.base;

import android.app.Application;

import com.cj.xjw.core.component.InitializeService;
import com.cj.xjw.core.di.component.AppComponent;
import com.cj.xjw.core.di.component.DaggerAppComponent;
import com.cj.xjw.core.di.module.AppModule;

/**
 * Created by chenj on 2017/4/17.
 */

public class App extends Application {

    private static App mApp;

    private AppComponent mAppComponent;

    public static App getApplication() {
        return mApp;
    }

    @Override
    public void onCreate() {
        mApp = this;
        super.onCreate();

        initAppComponent();

        //在子线程中初始化一些全局的配置
        InitializeService.start(this);
    }

    private void initAppComponent() {
        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
