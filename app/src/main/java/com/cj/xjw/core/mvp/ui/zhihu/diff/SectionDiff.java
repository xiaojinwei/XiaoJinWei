package com.cj.xjw.core.mvp.ui.zhihu.diff;

import android.support.v7.util.DiffUtil;

import com.cj.xjw.core.mvp.model.bean.SectionListBean;

import java.util.List;

/**
 * Created by chenj on 2017/5/2.
 */

public class SectionDiff extends DiffUtil.Callback {

    private List<SectionListBean.DataBean> mOldData;
    private List<SectionListBean.DataBean> mNewData;

    public SectionDiff(List<SectionListBean.DataBean> oldData, List<SectionListBean.DataBean> newData) {
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
        return mOldData.get(oldItemPosition).getId() == mNewData.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        if (!mOldData.get(oldItemPosition).getThumbnail().equals(mNewData.get(newItemPosition).getThumbnail())) {
            return false;
        }
        return true;
    }
}
