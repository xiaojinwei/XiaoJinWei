package com.cj.chenj.recyclerview_lib.adapter;

import android.content.Context;
import android.support.annotation.AnimRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.List;

/**
 * 通用的RecyclerView设配器Adapter
 * Created by chenj on 2016/7/28.
 */
public abstract class CommonAdapter<T> extends RecyclerView.Adapter<ViewHolder> {

    protected Context mContext;
    protected int mLayoutId;
    protected List<T> mDatas;
    protected LayoutInflater mLayoutInflater;

    public CommonAdapter(Context context,int layoutId,List<T> datas){
        this.mContext = context;
        this.mLayoutId = layoutId;
        this.mDatas = datas;
        mLayoutInflater = LayoutInflater.from(context);

    }

    /**
     * 先调onCreateViewHolder创建ViewHolder，再调用onBindViewHolder，当有缓存（复用）的时候就只会调用onBindViewHolder绑定数据到ViewHolder（条目View）
     * 只有在RecyclerView没有缓存条目的时候才会调用，有缓存的时候就会复用条目View，每个条目View对应一个ViewHolder，
     * 所以有缓存的时候就不会再去创建ViewHolder，也就是不会再走onCreateViewHolder
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = ViewHolder.get(mContext, parent, mLayoutId);
        setViewHolderListener(viewHolder,viewType);
        return viewHolder;
    }

    /**
     * ViewHolder创建后调用和设置监听
     * @param viewHolder
     * @param viewType
     */
    protected void setViewHolderListener(ViewHolder viewHolder,int viewType){
        onViewHolderCreated(viewHolder,viewHolder.getConvertView());
        setItemListener(viewHolder,viewType);
    }

    /**
     * ViewHolder创建好了之后调用，在convert之前调用，做一些其他操作
     * @param viewHolder
     * @param convertView
     */
    private void onViewHolderCreated(ViewHolder viewHolder, View convertView) {

    }

    /**
     * 是否设置条目监听
     * @param viewType
     * @return
     */
    protected boolean isEnabled(int viewType){
        return true;
    }

    protected void setItemListener(final ViewHolder viewHolder, final int viewType){
        if(!isEnabled(viewType)) return;
        View convertView = viewHolder.getConvertView();
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemClickListener != null){
                    int adapterPosition = viewHolder.getAdapterPosition();
                    mOnItemClickListener.onItemClick(CommonAdapter.this,viewHolder,viewHolder.getConvertView(),adapterPosition,viewType);
                }
            }
        });
    }

    /**
     * 每个条目出现在屏幕的时候都会去调用onBindViewHolder
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //暴露接口，使用viewHoler.getView(viewId)获取对应的控件，并且设置相应的数据
        convert(holder,mDatas.get(position),position);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    public abstract void convert(ViewHolder viewHolder, T item, int position);

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    /**
     * 条目点击事件接口
     */
    public interface OnItemClickListener{
        /**
         * 条目点击事件响应方法
         * @param parent 设陪器
         * @param viewHolder ViewHolder
         * @param view 条目布局View
         * @param position 条目索引（如果添加了头，索引包括头）
         */
        void onItemClick(RecyclerView.Adapter<?> parent, RecyclerView.ViewHolder viewHolder, View view, int position,int viewType);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mOnItemClickListener = listener;
    }

    public List<T> getDatas(){
        return mDatas;
    }

    public void setDatas(List<T> datas) {
        mDatas = datas;
    }

    public void addMore(List<T> data) {
        if (data != null && !data.isEmpty()) {
            int startPosition = mDatas.size();
            mDatas.addAll(data);
            int endPosition = mDatas.size();
            notifyItemRangeInserted(startPosition,endPosition);
        }
    }

    public void deleteItem(int position) {
        if (position < mDatas.size()) {
            mDatas.remove(position);
            notifyItemRemoved(position);
        }
    }

    private int mLastShowPositioin = -1;

    /**
     * 给条目添加动画
     * @param holder
     * @param position
     * @param id
     */
    protected void setItemAppearAnimation(RecyclerView.ViewHolder holder, int position, @AnimRes int id) {
        if (position > mLastShowPositioin) {
            Animation animation = AnimationUtils.loadAnimation(mContext, id);
            holder.itemView.startAnimation(animation);
            mLastShowPositioin = position;
        }
    }


}
