package com.cj.xjw.core.di.component;

import android.app.Activity;

import com.cj.xjw.core.di.module.URLImageModule;
import com.cj.xjw.core.di.scope.URLImageScope;
import com.cj.xjw.core.widget.URLImageGetter;

import dagger.Component;

/**
 * Created by cj_28 on 2017/4/29.
 */
@URLImageScope
@Component(dependencies = AppComponent.class,modules = URLImageModule.class)
public interface URLImageComponent {


    void inject(URLImageGetter urlImageGetter);
}
