package com.cj.xjw.core.mvp.ui.main.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.cj.xjw.R;
import com.cj.xjw.base.Constants;
import com.cj.xjw.core.base.BaseActivity;
import com.cj.xjw.core.di.component.AppComponent;
import com.cj.xjw.core.mvp.presenter.MainPresenter;
import com.cj.xjw.core.mvp.presenter.contract.MainContract;
import com.cj.xjw.core.mvp.ui.main.fragment.NewsFragment;
import com.cj.xjw.core.mvp.ui.zhihu.fragment.ZhiHuFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.yokeyword.fragmentation.SupportFragment;

public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.View {


    @BindView(R.id.content)
    FrameLayout mContent;
    @BindView(R.id.navigation)
    NavigationView mNavigation;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.activity_main)
    RelativeLayout mActivityMain;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    int hideFragment = Constants.TYPE_NEWS;
    int showFragment = Constants.TYPE_NEWS;

    NewsFragment mNewsFragment;
    ZhiHuFragment mZhiHuFragment;

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        //Theme设置回正常的Theme
        setTheme(R.style.AppTheme);
        return super.onCreateView(name, context, attrs);
    }

    @Override
    protected void init() {

        setToolbarTitle();
        initView();
        initEvent();

    }

    private void setToolbarTitle() {
        setToolbar(mToolbar,"新闻");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,mDrawerLayout,mToolbar
        ,R.string.drawer_open,R.string.drawer_close);
        toggle.syncState();
        mDrawerLayout.addDrawerListener(toggle);
    }

    private void initEvent() {
        mNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_news:
                        showFragment = Constants.TYPE_NEWS;
                        break;
                    case R.id.nav_zhifu:
                        showFragment = Constants.TYPE_ZHIHU;
                        break;
                }
                mToolbar.setTitle(item.getTitle());
                mDrawerLayout.closeDrawers();
                showHideFragment(getTargetFragment(showFragment),getTargetFragment(hideFragment));
                hideFragment = showFragment;
                return true;
            }
        });
    }

    private void initView() {
        mNewsFragment = new NewsFragment();
        mZhiHuFragment = new ZhiHuFragment();
        loadMultipleRootFragment(R.id.content,0,mNewsFragment,mZhiHuFragment);
        showHideFragment(getTargetFragment(showFragment));
    }

    private SupportFragment getTargetFragment(int type) {
        switch (type) {
            case Constants.TYPE_NEWS:
                return mNewsFragment;
            case Constants.TYPE_ZHIHU:
                return mZhiHuFragment;
            default:
                return mNewsFragment;
        }
    }

    @Override
    protected void initInject() {
        getActivityComponent().inject(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showMsg(String msg) {

    }

}
