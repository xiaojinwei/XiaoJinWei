package com.cj.xjw.core.mvp.ui.zhihu.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.ImageView;

import com.cj.chenj.recyclerview_lib.adapter.MultiItemCommonAdapter;
import com.cj.chenj.recyclerview_lib.adapter.MultiItemTypeSupport;
import com.cj.chenj.recyclerview_lib.adapter.ViewHolder;
import com.cj.xjw.R;
import com.cj.xjw.core.component.ImageLoader;
import com.cj.xjw.core.mvp.model.bean.DailyListBean;

import java.util.List;

/**
 * Created by chenj on 2017/4/25.
 */

public class DailyAdapter extends MultiItemCommonAdapter<DailyListBean.StoriesBean> {

    DailyListBean mDailyListBean;
    private TopPagerAdapter mTopPagerAdapter;

    private String currentTitle = "今日热闻";

    public void setDailyListBean(DailyListBean contentData) {
        mDailyListBean = contentData;
        mTopPagerAdapter = new TopPagerAdapter(mContext,contentData.getTop_stories());
        Log.d("DAILY-TOP",contentData.getTop_stories().toString());
    }

    public enum Type{
        BANNER,
        TITLE,
        DAILY
    }

    public DailyAdapter(Context context, List<DailyListBean.StoriesBean> datas, MultiItemTypeSupport multiItemTypeSupport) {
        super(context, datas, multiItemTypeSupport);

    }

    @Override
    protected void convert(ViewHolder holder, DailyListBean.StoriesBean item, int position, int itemType) {
        if (itemType == Type.BANNER.ordinal()) {
            ViewPager viewPager = holder.getView(R.id.view_page);
            viewPager.setAdapter(mTopPagerAdapter);
        } else if (itemType == Type.TITLE.ordinal()) {
            holder.setText(R.id.tv_daily_date,currentTitle);
        }else{
            holder.setText(R.id.tv_daily_item_title, item.getTitle());
            ImageView imageView = holder.getView(R.id.iv_daily_item_image);
            ImageLoader.load(mContext,item.getImages().get(0),imageView);
        }

        setItemAppearAnimation(holder,position,R.anim.anim_bottom_in);
    }


}
