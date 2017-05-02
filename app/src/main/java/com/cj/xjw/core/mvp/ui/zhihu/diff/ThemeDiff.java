package com.cj.xjw.core.mvp.ui.zhihu.diff;

import android.support.v7.util.DiffUtil;

import com.cj.xjw.core.mvp.model.bean.ThemeListBean;

import java.util.List;

/**
 * Created by chenj on 2017/5/2.
 */

public class ThemeDiff extends DiffUtil.Callback {

    List<ThemeListBean.OthersBean> mOldData;
    List<ThemeListBean.OthersBean> mNewData;

    public ThemeDiff(List<ThemeListBean.OthersBean> oldData, List<ThemeListBean.OthersBean> newData) {
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
        ThemeListBean.OthersBean oldData = mOldData.get(oldItemPosition);
        ThemeListBean.OthersBean newData = mNewData.get(newItemPosition);
        if (!oldData.getThumbnail().equals(newData.getThumbnail())) {
            return false;
        }
        return true;
    }
}
