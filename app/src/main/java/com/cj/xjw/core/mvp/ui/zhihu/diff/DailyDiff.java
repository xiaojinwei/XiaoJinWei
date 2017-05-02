package com.cj.xjw.core.mvp.ui.zhihu.diff;

import android.support.v7.util.DiffUtil;

import com.cj.xjw.core.mvp.model.bean.DailyListBean;

import java.util.List;

/**
 * Created by chenj on 2017/5/2.
 */

public class DailyDiff extends DiffUtil.Callback {

    private List<DailyListBean.StoriesBean> mOldData;
    private List<DailyListBean.StoriesBean> mNewData;

    public DailyDiff(List<DailyListBean.StoriesBean> oldData, List<DailyListBean.StoriesBean> newData) {
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
        DailyListBean.StoriesBean beanOld = mOldData.get(oldItemPosition);
        DailyListBean.StoriesBean beanNew = mNewData.get(newItemPosition);
        if (!beanOld.getTitle().equals(beanNew.getTitle())) {
            return false;
        }
        if (beanOld.getReadState() != beanNew.getReadState()) {
            return false;
        }
        if (beanOld.getImages().size() > 0 && beanNew.getImages().size() > 0) {
            if (!beanOld.getImages().get(0).equals(beanNew.getImages().get(0))) {
                return false;
            }
        }
        return true;
    }
}
