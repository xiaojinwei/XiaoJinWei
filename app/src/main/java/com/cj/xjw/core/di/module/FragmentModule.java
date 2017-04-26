package com.cj.xjw.core.di.module;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.cj.xjw.core.di.scope.FragmentScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by chenj on 2017/4/20.
 */
@Module
public class FragmentModule {
    private Fragment mFragment;

    public FragmentModule(Fragment fragment) {
        mFragment = fragment;
    }

    @FragmentScope
    @Provides
    Activity provideActivity() {
        return mFragment.getActivity();
    }
}
