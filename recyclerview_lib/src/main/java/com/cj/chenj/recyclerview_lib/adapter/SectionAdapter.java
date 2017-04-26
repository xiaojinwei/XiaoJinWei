package com.cj.chenj.recyclerview_lib.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.ActivityChooserView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cj.chenj.recyclerview_lib.R;
import com.cj.chenj.recyclerview_lib.util.Util;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * RecyclerView给相同一类的条目数据添加头布局的设配器Adapter
 * Created by cj_28 on 2016/7/28.
 */
public abstract class SectionAdapter<T> extends MultiItemCommonAdapter<T> {

    private SectionSupport mSectionSupport;
    public static final int TYPE_SECTION = 0;
    private LinkedHashMap<String,Integer> mSections;//链式Map，顺序链表
    private LinkedHashMap<String,List<T>> mSectionsData;//将数据按照getTitle()返回的String值作为值进行存储

    public int getTitleSize() {
        return mSections.size();
    }

    /**
     * 根据标题分类的key返回分类后的集合
     * @param title
     * @return
     */
    public List<T> getSectionForTitle(String title){
        if (mSectionsData.size() != mDatas.size()) {
            findSections();
        }
        return mSectionsData.get(title);
    }

    /**
     * 根据全部数据集合的索引，获取其所在分类后的集合
     * @param position
     * @return
     */
    public List<T> getSectionForTitle(int position){
        //多了分类条目，所以条目页相应的变大了
        position = getIndexForPosition(position);
        String title = mSectionSupport.getTitle(mDatas.get(position));//获取分类的标题
        return getSectionForTitle(title);
    }

    /**
     * 获取该条目所在分组的title
     * @param position
     * @return
     */
    public String getSectionTitle(int position) {
        position = getIndexForPosition(position);
        return mSectionSupport.getTitle(mDatas.get(position));//获取分类的标题
    }

    private MultiItemTypeSupport<T> headerItemTypeSupport = new MultiItemTypeSupport<T>() {
        @Override
        public int getItemViewType(int position, T item) {
            return mSections.values().contains(position) ? TYPE_SECTION : 1;
        }

        @Override
        public int getItemLayoutId(int itemType) {
            if(itemType == TYPE_SECTION){
                return mSectionSupport.sectionHeaderLayoutId();
            }else {
                return mLayoutId;
            }
        }
    };

    @Override
    public int getItemViewType(int position) {
        return mMultiItemTypeSupport.getItemViewType(position,null);
    }

    final RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            findSections();
        }
    };

    /**
     * 筛选头，保存的头信息是去重复的，如有重复，只会保存第一份
     * 也会将在整个列表中的索引保存下来（i+nSections）
     */
    public void findSections(){
        int n = mDatas.size();
        int nSections = 0;
        mSections.clear();
        List<T> section = null;
        for(int i=0;i<n;i++){
            T t = mDatas.get(i);
            String sectionName = mSectionSupport.getTitle(t);

            if(!mSections.containsKey(sectionName)){
                mSections.put(sectionName,i+nSections);//i+nSections列表索引position
                nSections++;
                //数据分类
                section = new ArrayList<>();
                mSectionsData.put(sectionName,section);
            }
            ////数据分类添加
            section.add(t);
        }
        printSection();
    }

    public void printSection() {
        for (Map.Entry<String, List<T>> stringListEntry : mSectionsData.entrySet()) {
            System.out.println("------------------"+stringListEntry.getKey());
            for (T t : stringListEntry.getValue()) {
                System.out.println("--------"+t);
            }
        }
    }


    public SectionAdapter(Context context, int layoutId, List<T> datas, SectionSupport<T> sectionSupport) {
        super(context, datas, null);
        mLayoutId = layoutId;
        mMultiItemTypeSupport = headerItemTypeSupport;
        mSectionSupport = sectionSupport;
        mSections = new LinkedHashMap<>();
        mSectionsData = new LinkedHashMap<>();
        findSections();
        registerAdapterDataObserver(observer);

    }


    /**
     * 是否给条目布局设置点击事件
     * 在CommonAdapter中给条目布局设置点击事件，但是isEnabled（int）返回的必须是true
     * @param viewType
     * @return
     */
    @Override
    protected boolean isEnabled(int viewType)
    {
        if (viewType == TYPE_SECTION)
            return false;
        return super.isEnabled(viewType);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView)
    {
        super.onDetachedFromRecyclerView(recyclerView);
        unregisterAdapterDataObserver(observer);
    }

    @Override
    public int getItemCount()
    {
        return super.getItemCount() + mSections.size();
    }

    /**
     * 根据条目位置position减去该条目位置的之前的头布局的个数，就是剩下的条目（不含头，因为头信息的数据没有在数据集合中）的position
     * 在设置点击事件的时候，可以在条目点击事件中，通过position调用该方法，让其返回出去头布局所在位置的position
     * @param position
     * @return
     */
    public int getIndexForPosition(int position)
    {
        int nSections = 0;

        Set<Map.Entry<String, Integer>> entrySet = mSections.entrySet();
        for (Map.Entry<String, Integer> entry : entrySet)
        {
            if (entry.getValue() < position)
            {
                nSections++;
            }
        }
        return position - nSections;
    }



    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        System.out.println("-----------------------------position:"+position);
        System.out.println("-----------------------------holder.getLayoutPosition():"+holder.getLayoutPosition());
        int layoutPosition = position;
        //多了分类条目，所以条目页相应的变大了
        position = getIndexForPosition(position);
        if (holder.getItemViewType() == TYPE_SECTION)
        {
            String title = mSectionSupport.getTitle(mDatas.get(position));//获取分类的标题
            System.out.println("--------------------------title:"+title);
            //回调头布局
            mSectionSupport.onBindSectionHeaderLayout(holder.getConvertView(),layoutPosition,getSectionForTitle(title));
            //根据接口返回头布局中的textview，和接口中返回的相应条目的头文本，进行设置
            holder.setText(mSectionSupport.sectionTitleTextViewId(), title);
            return;
        }
        super.onBindViewHolder(holder, position);
    }



    //**********************************************************************




    public LinkedHashMap<Integer,Object> getTitleOrData(List<T> data) {
        LinkedHashMap<String,Integer> sections = new LinkedHashMap<>();//链式Map，顺序链表
        LinkedHashMap<String,List<T>> sectionsData = new LinkedHashMap<>();//将数据按照getTitle()返回的String值作为值进行存储
        LinkedHashMap<Integer,Object> listTitleAndData = new LinkedHashMap<>();
        int listPosition = 0;
        int n = data.size();
        int nSections = 0;
        sections.clear();
        List<T> section = null;
        for(int i=0;i<n;i++){
            T t = data.get(i);
            String sectionName = mSectionSupport.getTitle(t);

            if(!sections.containsKey(sectionName)){
                sections.put(sectionName,i+nSections);//i+nSections列表索引position
                listTitleAndData.put(listPosition++,sectionName);
                nSections++;
                //数据分类
                section = new ArrayList<>();
                sectionsData.put(sectionName,section);
            }
            ////数据分类添加
            section.add(t);
            listTitleAndData.put(listPosition++,t);
        }
        return listTitleAndData;
    }





    //****************************************************************************




    private int mTitleHeight;//标题头布局的高度
    private FrameLayout titleLayoutGroup;//标题头布局的父容器
    private TextView titleView;//标题布局中的文本控件
    private boolean mIsShowTitle = false;//是否显示头布局
    private Activity mActivity;//RecyclerView所在的Activity

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        if(isLinearLayoutRecyclerView(recyclerView) && mIsShowTitle) {
            onAttachedTitleToRecyclerView(recyclerView);
        }
    }

    /**
     * 设置是否显示粘性头布局
     * @param flag
     */
    public void setShowStickyTitle(Activity activity,boolean flag){
        mActivity = activity;
        mIsShowTitle = flag;
    }

    /**
     * 判断是否是线性条目布局类型
     * @param rv
     * @return
     */
    private boolean isLinearLayoutRecyclerView(RecyclerView rv){
        RecyclerView.LayoutManager layoutManager = rv.getLayoutManager();
        if(layoutManager instanceof LinearLayoutManager){
            return true;
        }
        return false;
    }

    /**
     * 给RecyclerView添加粘性头布局
     * @param rv
     */
    private void onAttachedTitleToRecyclerView(RecyclerView rv){
        addTitleToDecorView(rv);
        addTitleListener(rv);
    }

    /**
     * 从指定索引范围内，找出第一个条目分类的布局
     * @param rv
     * @param start
     * @param end
     * @return
     */
    private View getFirstVisibleTitleLayout(RecyclerView rv ,int start,int end){
        Set<Map.Entry<String, Integer>> entrySet = mSections.entrySet();
        for (Map.Entry<String, Integer> entry : entrySet)
        {
            int positionValue = entry.getValue();
            if (positionValue >= start && positionValue <= end)
            {
                //要减去第一个可见条目之前的条目数量
                View childAt = rv.getChildAt(positionValue - start);//因为是面向ViewHolder编程，条目复用，所以第一个可见条目页就是第0个孩子
                return childAt;
            }
        }
        return null;
    }

    /**
     * 获取第一个可见的索引
     * @param rv
     * @return
     */
    private int findFirstVisibleItemPosition(RecyclerView rv){
        RecyclerView.LayoutManager layoutManager = rv.getLayoutManager();
        if(layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
            return firstVisibleItemPosition;
        }
        return -1;
    }

    /**
     * 获取最后一个可见的索引
     * @param rv
     * @return
     */
    private int findLastVisibleItemPosition(RecyclerView rv){
        RecyclerView.LayoutManager layoutManager = rv.getLayoutManager();
        if(layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            int findLastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
            return findLastVisibleItemPosition;
        }
        return -1;
    }

    /**
     * 返回view离屏幕的top距离
     * @param view
     * @return
     */
    private int getViewTopOnScreen(View view){
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        boolean flag = (((Activity)mContext).getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN)==WindowManager.LayoutParams.FLAG_FULLSCREEN;
        if(flag ){//全屏
            return location[1];
        }
        int screenHeight = Util.getScreenSize(mActivity)[1];
        int decorViewHeight = Util.findSuitableParent(mActivity.getWindow().getDecorView()).getMeasuredHeight();
        System.out.println("----------------------screenHeight:"+screenHeight);
        System.out.println("----------------------decorViewHeight:"+decorViewHeight);
        if(screenHeight == decorViewHeight){
            return location[1] - (screenHeight - decorViewHeight);
        }
        return location[1] - Util.getStatusBarHeight(mContext);
    }

    /**
     * 获取View离屏幕的left距离
     * @param view
     * @return
     */
    private int getViewLeftOnScreen(View view){
        int[] location = new int[2];
        view.getLocationInWindow(location);
        return location[0];
    }

    /**
     * 将条目分类的头布局添加到根布局DecorView中
     * @param view 根布局中的任意一个View，主要是用其找到DecorView
     */
    private void addTitleToDecorView(View view){
        ViewGroup suitableParent = Util.findSuitableParent(mActivity.getWindow().getDecorView());
        LayoutInflater inflater = LayoutInflater.from(mContext);
        int titleLayoutId = mSectionSupport.sectionHeaderLayoutId();
        //因为条目分类的头是粘性的，实现的原理是使用scrollTo()移动布局中的内容，但是布局的背景是不动的，
        //要想解决头布局的背景也跟着动，就是将其头布局再添加到另一个布局中（这里使用FrameLayout，没有设置背景或是透明的），移动该布局容器（FrameLayout）即可，
        titleLayoutGroup = new FrameLayout(mContext);
        titleLayoutGroup.setBackgroundColor(Color.TRANSPARENT);//设置透明背景
        final View titleLayout = inflater.inflate(titleLayoutId, suitableParent, false);
        titleView = (TextView) titleLayout.findViewById(mSectionSupport.sectionTitleTextViewId());

        FrameLayout.LayoutParams groupLp = new FrameLayout.LayoutParams(titleLayout.getLayoutParams().width,titleLayout.getLayoutParams().height);
        titleLayoutGroup.setLayoutParams(groupLp);

        titleLayoutGroup.removeAllViews();
        titleLayoutGroup.addView(titleLayout);

        suitableParent.addView(titleLayoutGroup);
    }

    /**
     * 头布局设置监听，
     */
    private void addTitleListener(final RecyclerView rv) {
        //设置RecyclerView的布局监听，使其RecyclerView布局加载好之后，进行计算RecyclerView的Top和计算titleLayoutGroup的高度
        rv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            //在这里获取到的布局的宽高都是真实的，因为这时的布局已经布局好了

            @Override
            public void onGlobalLayout() {
                View titleClone = getFirstVisibleTitleLayout(rv,0,rv.getChildCount());
                //titleClone.measure(0,0);
                FrameLayout.LayoutParams groupLp = new FrameLayout.LayoutParams(titleClone.getMeasuredWidth(),titleClone.getMeasuredHeight());
                groupLp.leftMargin = getViewLeftOnScreen(titleClone);
                titleLayoutGroup.setLayoutParams(groupLp);
                //FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(titleLayout.getLayoutParams().width,titleLayout.getLayoutParams().height);
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) titleLayoutGroup.getLayoutParams();
                lp.topMargin = getViewTopOnScreen(rv);
                titleLayoutGroup.setLayoutParams(lp);
                rv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mTitleHeight =  titleClone.getHeight();
            }
        });

        //用于分类条目头布局的粘性滑动的滚动监听，改变头布局的移动距离
        rv.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisiblePosition = findFirstVisibleItemPosition(recyclerView);
                int lastVisiblePosition = findLastVisibleItemPosition(recyclerView);
                //获取屏幕中第一个可见的条目中的分类布局
                View firstVisibleTitleLayout = getFirstVisibleTitleLayout(recyclerView, firstVisiblePosition, lastVisiblePosition);
                if(firstVisibleTitleLayout != null) {
                    int top = firstVisibleTitleLayout.getTop();//获取top，就是离父容器的距离
                    if(top < mTitleHeight && top > 0){
                        //滚动布局内容，产生粘动的效果
                        titleLayoutGroup.scrollTo(0,(mTitleHeight - top));
                    }else if(top <= 0 || top >= mTitleHeight){
                        //布局内容恢复要原位置
                        titleLayoutGroup.scrollTo(0,0);
                    }

                }
                if( recyclerView != null){
                    int firstVisibleItemPosition = findFirstVisibleItemPosition(recyclerView);
                    if(firstVisibleItemPosition >= 0) {
                        //根据RecyclerView的position获取mData数据集合（也就是除去条目布局的）的position
                        int indexForPosition = getIndexForPosition(firstVisibleItemPosition);
                        titleView.setText(mSectionSupport.getTitle(mDatas.get(indexForPosition)));
                    }

                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }


        });
    }

}
