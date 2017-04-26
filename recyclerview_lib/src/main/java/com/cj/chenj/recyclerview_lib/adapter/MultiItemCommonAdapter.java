package com.cj.chenj.recyclerview_lib.adapter;

import android.content.Context;
import android.view.ViewGroup;

import java.util.List;

/**
 * RecyclerView多条目类型的设配器Adapter
 * Created by chenj on 2016/7/28.
 */
public abstract class MultiItemCommonAdapter<T> extends CommonAdapter<T> {

    protected MultiItemTypeSupport mMultiItemTypeSupport;

    public MultiItemCommonAdapter(Context context, List<T> datas ,MultiItemTypeSupport multiItemTypeSupport) {
        //layoutId是在onCreateViewHolder中加载布局的，因为这里还要重写该方法，所以不需要传layoutId
        super(context, -1, datas);
        this.mMultiItemTypeSupport = multiItemTypeSupport;
    }

    @Override
    public int getItemViewType(int position) {
        //根据position和java bean对象中的某个字段值就可以该条目是什么类型的了
        //类型可以从0开始，依次往上加，每个数字对应一种类型
        return mMultiItemTypeSupport.getItemViewType(position,mDatas.get(position));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //通过viewType获取不同条目的布局id
        int itemLayoutId = mMultiItemTypeSupport.getItemLayoutId(viewType);
        //通过布局id去创建ViewHolder
        ViewHolder viewHolder = ViewHolder.get(mContext, parent, itemLayoutId);
        //ViewHolder创建后的调用和设置监听
        setViewHolderListener(viewHolder,viewType);
        return viewHolder;
    }

    @Override
    public void convert(ViewHolder viewHolder, T item, int position) {

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //暴露接口，使用viewHoler.getView(viewId)获取对应的控件，并且设置相应的数据
        convert(holder,mDatas.get(position),position,getItemViewType(position));
    }

    protected void convert(ViewHolder holder, T item, int position, int itemType){};
}
