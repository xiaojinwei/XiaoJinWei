package com.cj.xjw.core.mvp.ui.main.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.cj.xjw.R;
import com.cj.xjw.base.Constants;
import com.cj.xjw.core.base.SimpleFragment;
import com.cj.xjw.core.component.RxBus;
import com.cj.xjw.core.mvp.model.bean.NewsChannelTable;
import com.cj.xjw.core.mvp.model.db.NewsChannelTableManager;
import com.cj.xjw.core.mvp.model.event.TopEvent;
import com.cj.xjw.core.mvp.ui.main.adapter.NewsFragmentPagerAdapter;
import com.cj.xjw.core.utils.Util;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by chenj on 2017/4/21.
 */

public class NewsFragment extends SimpleFragment {


    @BindView(R.id.tab_news_main)
    TabLayout mTabNewsMain;
    @BindView(R.id.vp_news_main)
    ViewPager mVpNewsMain;

    List<Fragment> mNewsListFragment = new ArrayList<>();
    @BindView(R.id.fab)
    FloatingActionButton mFab;

    @Override
    protected void init() {
        List<NewsChannelTable> newsChannelTable = NewsChannelTableManager.getNewsChannelTable();
        initViewPager(newsChannelTable);
    }

    private void initViewPager(List<NewsChannelTable> newsChannelTable) {
        List<String> channelName = new ArrayList<>();
        if (newsChannelTable != null) {
            setNewsList(newsChannelTable, channelName);
            setViewPager(channelName);
        }
    }

    private void setViewPager(List<String> channelName) {
        NewsFragmentPagerAdapter pagerAdapter = new NewsFragmentPagerAdapter(
                getChildFragmentManager(), mNewsListFragment, channelName
        );
        mVpNewsMain.setAdapter(pagerAdapter);
        mTabNewsMain.setupWithViewPager(mVpNewsMain);
        Util.dynamicSetTabLayoutMode(mTabNewsMain);
    }

    private void setNewsList(List<NewsChannelTable> newsChannelTable, List<String> channelName) {
        mNewsListFragment.clear();
        for (NewsChannelTable channelTable : newsChannelTable) {
            NewsListFragment newsListFragment = createNewsListFragment(channelTable);
            mNewsListFragment.add(newsListFragment);
            channelName.add(channelTable.getNewsChannelName());
        }
    }

    private NewsListFragment createNewsListFragment(NewsChannelTable channelTable) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.NEWS_ID, channelTable.getNewsChannelId());
        bundle.putString(Constants.NEWS_TYPE, channelTable.getNewsChannelType());
        bundle.putInt(Constants.CHANNEL_POSITION, channelTable.getNewsChannelIndex());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_news_main;
    }



    @OnClick(R.id.fab)
    public void onCClick() {
        RxBus.getInstance().post(new TopEvent());
    }
}
