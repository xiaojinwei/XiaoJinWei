package com.cj.xjw.core.mvp.ui.main.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by chenj on 2017/4/22.
 */

public class NewsFragmentPagerAdapter extends FragmentPagerAdapter {

    List<Fragment> mNewsFragments;

    List<String> mTitles;

    public NewsFragmentPagerAdapter(FragmentManager fm
    , List<Fragment> fragments,List<String> titles) {
        super(fm);
        this.mNewsFragments = fragments;
        this.mTitles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return mNewsFragments.get(position);
    }

    @Override
    public int getCount() {
        return mNewsFragments == null ? 0 : mNewsFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }

    @Override
    public long getItemId(int position) {
        super.getItemId(position);
        if (mNewsFragments != null) {
            if (position < mNewsFragments.size()) {
                //不同的Fragment分配的HashCode不同，从而实现刷新adapter中的fragment
                return mNewsFragments.get(position).hashCode();
            }
        }
        return super.getItemId(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
    }
}
