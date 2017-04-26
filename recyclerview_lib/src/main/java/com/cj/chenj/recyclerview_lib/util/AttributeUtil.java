package com.cj.chenj.recyclerview_lib.util;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.OverScroller;

/**
 * Created by chenj on 2016/12/24.
 */

public class AttributeUtil {
    public static final int OVER_SCROLL_ALWAYS = RecyclerView.OVER_SCROLL_ALWAYS;//有下（上）拉阴影
    public static final int OVER_SCROLL_IF_CONTENT_SCROLLS = RecyclerView.OVER_SCROLL_IF_CONTENT_SCROLLS;//
    public static final int OVER_SCROLL_NEVER = RecyclerView.OVER_SCROLL_NEVER;//无下（上）拉阴影

    /**
     * 设置边缘拉动阴影
     * @param rv
     * @param scrollMode
     */
    public static void setOverScrollMode(RecyclerView rv,int scrollMode){
        rv.setOverScrollMode(scrollMode);
    }

    public static void setLinearLayoutManager(RecyclerView rv){
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext(),LinearLayoutManager.VERTICAL,false));
    }

    public static void setGridLayoutManager(RecyclerView rv,int spanCount){
        GridLayoutManager layoutManager = new GridLayoutManager(rv.getContext(),spanCount);
        rv.setLayoutManager(layoutManager);
    }

    public static void setStaggeredLinearLayoutManager(RecyclerView rv,int spanCount){
        GridLayoutManager layoutManager = new GridLayoutManager(rv.getContext(),spanCount);
        rv.setLayoutManager(layoutManager);
    }
}
