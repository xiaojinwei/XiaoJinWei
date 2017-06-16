package com.cj.xjw.base;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.cj.xjw.core.component.InitializeService;
import com.cj.xjw.core.di.component.AppComponent;
import com.cj.xjw.core.di.component.DaggerAppComponent;
import com.cj.xjw.core.di.module.AppModule;
import com.cj.xjw.core.di.module.HttpModule;

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
                .httpModule(new HttpModule())
                .build();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }


    public static boolean isHavePhoto() {
        return
        getSharedPreferences().getBoolean(Constants.SHOW_NEWS_PHOTO, true);
    }

    public static SharedPreferences getSharedPreferences() {
        return App.getApplication()
                .getSharedPreferences(Constants.SHARES_COLOURFUL_NEWS,
                        Context.MODE_PRIVATE);
    }
}
