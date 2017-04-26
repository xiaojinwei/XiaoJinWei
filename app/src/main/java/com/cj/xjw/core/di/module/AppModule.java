package com.cj.xjw.core.di.module;

import com.cj.xjw.base.App;
import com.cj.xjw.core.mvp.model.http.RetrofitHelper;
import com.cj.xjw.core.mvp.model.http.api.MyApi;
import com.cj.xjw.core.mvp.model.http.api.ZhiHuApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by chenj on 2017/4/17.
 */

@Module
public class AppModule {
    private final App mApp;

    public AppModule(App app) {
        mApp = app;
    }

    @Singleton
    @Provides
    App provideApplicationContext() {
        return mApp;
    }

    @Singleton
    @Provides
    RetrofitHelper provideRetrofitHelper(MyApi myApi, ZhiHuApi zhiHuApi) {
        return new RetrofitHelper(myApi,zhiHuApi);
    }
}
