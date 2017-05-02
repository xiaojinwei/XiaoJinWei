package com.cj.xjw.core.mvp.ui.zhihu.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.widget.ImageView;
import android.widget.TextView;

import com.cj.chenj.recyclerview_lib.adapter.CommonAdapter;
import com.cj.chenj.recyclerview_lib.adapter.ViewHolder;
import com.cj.xjw.R;
import com.cj.xjw.core.component.ImageLoader;
import com.cj.xjw.core.mvp.model.bean.HotListBean;
import com.cj.xjw.core.mvp.ui.zhihu.diff.HotDiff;

import java.util.List;

/**
 * Created by chenj on 2017/4/27.
 */

public class HotAdapter extends CommonAdapter<HotListBean.RecentBean> {
    public HotAdapter(Context context, int layoutId, List<HotListBean.RecentBean> datas) {
        super(context, layoutId, datas);
    }

    @Override
    public void convert(ViewHolder viewHolder, HotListBean.RecentBean item, int position) {
        viewHolder.setText(R.id.tv_daily_item_title,item.getTitle());
        TextView tv = viewHolder.getView(R.id.tv_daily_item_title);
        if(item.getReadState()){
            tv.setTextColor(ContextCompat.getColor(mContext,R.color.news_read));
        }else{
            tv.setTextColor(ContextCompat.getColor(mContext,R.color.news_unread));
        }


        ImageLoader.load(mContext,item.getThumbnail(),(ImageView) viewHolder.getView(R.id.iv_daily_item_image));
    }

    public void refreshData(List<HotListBean.RecentBean> list) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new HotDiff(mDatas, list), true);
        setDatas(list);
        diffResult.dispatchUpdatesTo(this);
    }
}
