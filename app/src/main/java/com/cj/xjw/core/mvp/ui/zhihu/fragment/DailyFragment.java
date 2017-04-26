package com.cj.xjw.core.mvp.ui.zhihu.fragment;

import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.cj.chenj.recyclerview_lib.adapter.MultiItemTypeSupport;
import com.cj.xjw.R;
import com.cj.xjw.core.base.BaseFragment;
import com.cj.xjw.core.mvp.model.bean.DailyListBean;
import com.cj.xjw.core.mvp.presenter.DailyPresenter;
import com.cj.xjw.core.mvp.presenter.contract.DailyContract;
import com.cj.xjw.core.mvp.ui.zhihu.adapter.DailyAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by chenj on 2017/4/24.
 */

public class DailyFragment extends BaseFragment<DailyPresenter> implements DailyContract.View{

    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.swipe)
    SwipeRefreshLayout mSwipe;

    List<DailyListBean.StoriesBean> mDatas = new ArrayList<>();

    @Inject
    Activity mActivity;
    private DailyAdapter mDailyAdapter;

    @Override
    protected void init() {
        mPresenter.getContentData();

        mDailyAdapter = new DailyAdapter(mActivity, mDatas, mTypeSupport);
        mRecycler.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecycler.setHasFixedSize(true);
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mRecycler.setAdapter(mDailyAdapter);
    }

    @Override
    protected void initInject() {
        getFragmentComponent().inject(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_daily;
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


    @Override
    public void setContentData(DailyListBean contentData) {
        Log.d("DAILY",contentData.toString());
        if (mDailyAdapter != null) {
            mDailyAdapter.setDailyListBean(contentData);
            mDailyAdapter.setDatas(contentData.getStories());
            mDailyAdapter.notifyDataSetChanged();
        }
    }


    MultiItemTypeSupport mTypeSupport = new MultiItemTypeSupport() {
        @Override
        public int getItemViewType(int position, Object item) {
            if (position == 0) {
                return DailyAdapter.Type.BANNER.ordinal();
            }else if(position == 1){
                return DailyAdapter.Type.TITLE.ordinal();
            }else{
                return DailyAdapter.Type.DAILY.ordinal();
            }

        }

        @Override
        public int getItemLayoutId(int itemType) {
            if (itemType == DailyAdapter.Type.BANNER.ordinal()) {
                return R.layout.zhihu_daily_top_item;
            } else if (itemType == DailyAdapter.Type.TITLE.ordinal()) {
                return R.layout.zhihu_daily_date_item;
            }else{
                return R.layout.zhihu_daily_main_item;
            }
        }
    };
}
