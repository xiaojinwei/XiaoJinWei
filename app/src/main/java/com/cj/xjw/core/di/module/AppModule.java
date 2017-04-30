package com.cj.xjw.core.di.module;

import android.content.Context;

import com.cj.xjw.base.App;
import com.cj.xjw.core.di.qualifier.MyKaKuApi;
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
    private final Context mContext;

    public AppModule(App app) {
        mApp = app;
        mContext = mApp.getApplicationContext();
    }

    @Singleton
    @Provides
    App provideApplicationContext() {
        return mApp;
    }

    @Singleton
    @Provides
    Context provideContext(){
        return mContext;
    }

    @Singleton
    @Provides
    RetrofitHelper provideRetrofitHelper(@com.cj.xjw.core.di.qualifier.MyApi MyApi myApi, @MyKaKuApi MyApi myKaKuApi, ZhiHuApi zhiHuApi) {
        return new RetrofitHelper(myApi,myKaKuApi,zhiHuApi);
    }
}
