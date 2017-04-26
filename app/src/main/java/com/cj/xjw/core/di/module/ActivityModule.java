package com.cj.xjw.core.di.module;

import android.app.Activity;

import com.cj.xjw.core.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by chenj on 2017/4/20.
 */

@Module
public class ActivityModule {
    private Activity mActivity;

    public ActivityModule(Activity activity) {
        mActivity = activity;
    }

    @ActivityScope
    @Provides
    Activity provideActivity() {
        return mActivity;
    }
}
