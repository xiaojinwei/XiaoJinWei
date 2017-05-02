package com.cj.xjw.core.mvp.ui.zhihu.diff;

import android.support.v7.util.DiffUtil;

import com.cj.xjw.core.mvp.model.bean.HotListBean;

import java.util.List;

/**
 * Created by chenj on 2017/5/2.
 */

public class HotDiff extends DiffUtil.Callback {

    private List<HotListBean.RecentBean> mOldData;
    private List<HotListBean.RecentBean> mNewData;

    public HotDiff(List<HotListBean.RecentBean> oldData, List<HotListBean.RecentBean> newData) {
        mOldData = oldData;
        mNewData = newData;
    }

    @Override
    public int getOldListSize() {
        return mOldData == null ? 0 : mOldData.size();
    }

    @Override
    public int getNewListSize() {
        return mNewData == null ? 0 : mNewData.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldData.get(oldItemPosition).getNews_id() == mNewData.get(newItemPosition).getNews_id();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        if (!mOldData.get(oldItemPosition).getThumbnail().equals(mNewData.get(newItemPosition).getThumbnail())) {
            return false;
        }
        return true;
    }
}
