package com.cj.chenj.recyclerview_lib.adapter;

import android.content.Context;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.cj.chenj.recyclerview_lib.util.WrapperUtils;

import java.util.List;


/**
 * 给RecyclerView设置头布局和脚布局的设配器Adapter
 * Created by chenj on 2016/7/28.
 */
public abstract class HeaderAndFooterAdapter<T> extends CommonAdapter<T>
{
    private static final int BASE_ITEM_TYPE_HEADER = 100000;
    private static final int BASE_ITEM_TYPE_FOOTER = 200000;

    private SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<>();
    private SparseArrayCompat<View> mFooterViews = new SparseArrayCompat<>();

    //private RecyclerView.Adapter mInnerAdapter;

    public HeaderAndFooterAdapter(Context context, int layoutId, List<T> datas) {
        super(context, layoutId, datas);
    }

    //public HeaderAndFooterAdapter(RecyclerView.Adapter adapter)
    //{
    //    mInnerAdapter = adapter;
    //}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if (mHeaderViews.get(viewType) != null)
        {
            ViewHolder holder = ViewHolder.get(parent.getContext(),parent, mHeaderViews.get(viewType));
            return holder;

        } else if (mFooterViews.get(viewType) != null)
        {
            ViewHolder holder = ViewHolder.get(parent.getContext(),parent, mFooterViews.get(viewType));
            return holder;
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public int getItemViewType(int position)
    {
        if (isHeaderViewPos(position))
        {
            return mHeaderViews.keyAt(position);
        } else if (isFooterViewPos(position))
        {
            return mFooterViews.keyAt(position - getHeadersCount() - getRealItemCount());
        }
        return super.getItemViewType(position - getHeadersCount());
    }

    private int getRealItemCount()
    {
        return super.getItemCount();
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        if (isHeaderViewPos(position))
        {
            return;
        }
        if (isFooterViewPos(position))
        {
            return;
        }
        super.onBindViewHolder(holder, position - getHeadersCount());
    }

    @Override
    public int getItemCount()
    {
        return getHeadersCount() + getFootersCount() + getRealItemCount();
    }

    /**
     * 设配器依附到RecyclerView的时候调用,只会调用一次
     * @param recyclerView  每个条目对应着一个ViewHolder
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        WrapperUtils.onAttachedToRecyclerView2(this, recyclerView, new WrapperUtils.SpanSizeCallback()
        {
            /**
             * 每显示一个条目的时候，都会调用，决定该条目占用几个跨度（列）
             * @param layoutManager
             * @param oldLookup
             * @param position
             * @return
             */
            @Override
            public int getSpanSize(GridLayoutManager layoutManager, GridLayoutManager.SpanSizeLookup oldLookup, int position)
            {
                //返回的数不能大于GridLayout的列数n
                int viewType = getItemViewType(position);
                if (mHeaderViews.get(viewType) != null)
                {
                    //System.out.println("---------------------mHeaderViews:"+layoutManager.getSpanCount()+",position:"+position);
                    return layoutManager.getSpanCount();//返回的是GridLayout的列数n，使其占用n列
                } else if (mFooterViews.get(viewType) != null)
                {
                    //System.out.println("---------------------mFooterViews:"+layoutManager.getSpanCount()+",position:"+position);
                    return layoutManager.getSpanCount();//返回的是GridLayout的列数n，使其占用n列
                }
                if (oldLookup != null) {
                    //System.out.println("---------------------mViews:"+oldLookup.getSpanSize(position)+",position:"+position);
                    //if(position == 100) return 3;//这样position是100的就占其一行，如果100不是在一行的开始（上一行原本的位置空着），就换到下一行独占3个位置，
                    //每个item的span size为1
                    return oldLookup.getSpanSize(position);//返回的是1，使其占用1列
                }
                //System.out.println("-------------------------getSpanSize:position:"+position);
                return 1;
            }
        });
    }


    /**
     * 当每个条目View出现在屏幕上的时候调用
     * @param holder
     */
    @Override
    public void onViewAttachedToWindow(ViewHolder holder)
    {
        super.onViewAttachedToWindow(holder);
        int position = holder.getLayoutPosition();
        if (isHeaderViewPos(position) || isFooterViewPos(position))
        {
            WrapperUtils.setFullSpan(holder);
        }
    }

    private boolean isHeaderViewPos(int position)
    {
        return position < getHeadersCount();
    }

    private boolean isFooterViewPos(int position)
    {
        return position >= getHeadersCount() + getRealItemCount();
    }


    public void addHeaderView(View view)
    {
        mHeaderViews.put(mHeaderViews.size() + BASE_ITEM_TYPE_HEADER, view);
    }

    public void addFooterView(View view)
    {
        mFooterViews.put(mFooterViews.size() + BASE_ITEM_TYPE_FOOTER, view);
    }

    public int getHeadersCount()
    {
        return mHeaderViews.size();
    }

    public int getFootersCount()
    {
        return mFooterViews.size();
    }


    /*public void addMore(List<T> data) {
        if (data != null && !data.isEmpty() && mInnerAdapter != null) {
            List<T> datas = ((CommonAdapter<T>) mInnerAdapter).getDatas();
            int startPosition = datas.size();
            datas.addAll(data);
            int endPosition = datas.size() + getFootersCount();
            notifyItemRangeInserted(startPosition,endPosition);
        }
    }*/
}
