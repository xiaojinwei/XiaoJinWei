package com.cj.chenj.recyclerview_lib.adapter;

import android.view.View;

import java.util.List;

/**
 * RecyclerView给相同一类的条目数据添加头布局的接口支持
 * Created by cj_28 on 2016/7/28.
 */
public interface SectionSupport<T> {
    int sectionHeaderLayoutId();//返回头布局
    int sectionTitleTextViewId();//返回头布局中的View
    void onBindSectionHeaderLayout(View parent,int position, List<T> items);//onBindViewHolder中调用，如果要改变头布局中的布局的话
    String getTitle(T item);//获取头显示的文本
}
