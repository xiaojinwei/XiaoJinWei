package com.cj.xjw.core.mvp.ui.zhihu.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cj.xjw.R;
import com.cj.xjw.core.base.SimpleFragment;
import com.cj.xjw.core.mvp.ui.zhihu.adapter.ZhiHuAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 因为无需访问网络，所以可以不用MVP
 * Created by chenj on 2017/4/24.
 */

public class ZhiHuFragment extends SimpleFragment {

    @Inject
    Activity mActivity;

    List<Fragment> mFragments = new ArrayList<>();
    List<String> mTitles = new ArrayList<>();
    @BindView(R.id.tab_zhihu_main)
    TabLayout mTabZhihuMain;
    @BindView(R.id.vp_zhihu_main)
    ViewPager mVpZhihuMain;

    @Override
    protected void init() {
        setFragments();
        setTitles();
        setAdapter();
    }

    @Override
    protected void initInject() {
        getFragmentComponent().inject(this);
    }

    private void setAdapter() {
        ZhiHuAdapter zhiHuAdapter = new ZhiHuAdapter(
                getChildFragmentManager(),mFragments,mTitles
        );

        mVpZhihuMain.setAdapter(zhiHuAdapter);
        mTabZhihuMain.setupWithViewPager(mVpZhihuMain);
    }

    private void setTitles() {
        String[] zhihuArray = mActivity.getResources().getStringArray(R.array.zhihu_channel_name);
        mTitles.addAll(Arrays.asList(zhihuArray));
    }

    private void setFragments() {
        mFragments.add(new DailyFragment());
        mFragments.add(new ThemeFragment());
        mFragments.add(new SectionFragment());
        mFragments.add(new HotFragment());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_zhihu_main;
    }

}
