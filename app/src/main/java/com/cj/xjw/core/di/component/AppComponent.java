package com.cj.xjw.core.di.component;

import android.content.Context;

import com.cj.xjw.base.App;
import com.cj.xjw.core.di.module.AppModule;
import com.cj.xjw.core.di.module.HttpModule;
import com.cj.xjw.core.mvp.model.http.RetrofitHelper;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by chenj on 2017/4/17.
 */

@Singleton
@Component(modules = {AppModule.class, HttpModule.class})
public interface AppComponent {

    //这几个向外提供类型对象的方法不能有参数

    App getApplication(); //提供上下文

    Context getContext();

    RetrofitHelper getRetrofitHelper();//提供Http帮助类
}
