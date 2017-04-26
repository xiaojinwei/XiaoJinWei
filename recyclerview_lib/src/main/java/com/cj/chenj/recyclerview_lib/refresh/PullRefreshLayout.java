package com.cj.chenj.recyclerview_lib.refresh;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.ColorRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * 兼容RecyclerView的LinearLayoutManager（条目列表），GridLayoutManager（表格列表），StaggeredGridLayoutManager（瀑布流）上拉加载更多
 * Created by chenj on 2016/9/28.
 */
public class PullRefreshLayout extends RelativeLayout {

    private int[] colors = {
            0xFFFF0000, 0xFFFF7F00, 0xFFFFFF00, 0xFF00FF00
            , 0xFF00FFFF, 0xFF0000FF, 0xFF8B00FF};

    private final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;

    private MaterialProgressDrawable mProgress;

    private ValueAnimator mValueAnimator;

    private boolean mStart = false;//开始刷新

    private boolean mVisable = false;

    private static final int PULL_IMAGE_SIZE = 40;
    private static int PULL_IMAGE_SIZE_PX;//上拉View的大小（像素）
    private static int PULL_IMAGE_SIZE_PX_MAX ;//最大拉动的距离
    private static int PULL_IMAGE_SIZE_PX_EXECUTE ;//拉动到什么位置开始执行
    private static int PULL_IMAGE_SIZE_PX_EXECUTE_REFRESH ;//刷新是所在的位置
    private static float ROTATE_ANIM_ANGLE_PER;//根据最大距离计算旋转角度的比列

    private ImageView mImageView;
    private boolean mIsFirst;

    private int mStartY, mLastY;
    private RecyclerView mRecyclerView;
    //private int mFirstVisiblePosition;
    private int mLastVisiblePosition;

    private boolean mIsCanScoll;

    private boolean mVisibleCanScoll;

    private boolean mPrepareAnimation;//准备执行动画

    private boolean mIsAllowLoadMore = true;//是否可以上拉刷新

    private boolean mIsDispatch = true;//是否分发事件

    private boolean mScrollLoadEnabled = false;//滑动到最下面时候触发自动加载更多，默认需上拉才可以

    private boolean mIsRefreshing;//正在刷新中

    private SwipeRefreshLayout mSwipeRefreshLayout;

    public PullRefreshLayout(Context context) {
        this(context, null);
    }

    public PullRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    /**
     * 是否允许下拉加载更多
     * @param allowLoadMore
     */
    public void setAllowLoadMore(boolean allowLoadMore) {
        mIsAllowLoadMore = allowLoadMore;
    }

    public boolean isAllowLoadMore() {
        return mIsAllowLoadMore;
    }

    /**
     * 设置进度圈的颜色
     * @param colors 如：0xFFFF0000
     */
    public void setColorSchemeColors(int... colors){
        this.colors = colors;
    }
    /**
     * 设置进度圈的颜色
     * @param colorResIds 如：R.color.red
     */
    public void setColorSchemeResources(@ColorRes int... colorResIds) {
        final Resources res = getResources();
        int[] colorRes = new int[colorResIds.length];
        for (int i = 0; i < colorResIds.length; i++) {
            colorRes[i] = res.getColor(colorResIds[i]);
        }
        setColorSchemeColors(colorRes);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (!mIsFirst) {

            createProgressView();

            over:
            for (int i = 0; i < getChildCount(); i++) {
                View childView = getChildAt(i);
                if (childView instanceof SwipeRefreshLayout) {
                    ViewGroup viewGroup = (ViewGroup) childView;
                    mSwipeRefreshLayout = (SwipeRefreshLayout) viewGroup;
                    for (int j = 0; j < viewGroup.getChildCount(); j++) {
                        View childViewJ = viewGroup.getChildAt(j);
                        if (childViewJ instanceof RecyclerView) {
                            mRecyclerView = (RecyclerView) childViewJ;
                            break over;
                        }
                    }
                }
                if(childView instanceof RecyclerView){
                    mRecyclerView = (RecyclerView) childView;
                    break over;
                }
            }
            mIsFirst = true;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

       /* if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isNestedScrollingEnabled()) {
            return super.dispatchTouchEvent(event);
        }*/

        if(!mIsAllowLoadMore) return super.dispatchTouchEvent(event);//时候允许刷新

        if(mIsRefreshing) return super.dispatchTouchEvent(event);//是否正在刷新

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:

                if (!mStart) {
                    //如果不满足上拉的条件就直接分发事件
                    if(!canPullUp()){
                        return super.dispatchTouchEvent(event);
                    }else{
                        if(mScrollLoadEnabled){
                            //自动加载更多（无需上拉）
                            setRefreshing(true);
                            return super.dispatchTouchEvent(event);
                        }
                    }
                    if (canPullUp() && !mIsCanScoll) {
                        showRefreshArrow();
                        mStartY = (int) event.getRawY();
                        mIsCanScoll = true;
                    } else {
                        //mStartY = (int) event.getRawY();
                        //hideRefreshArrow();
                        //hide();
                    }
                    if (mVisibleCanScoll) {
                        int endY = (int) event.getRawY();
                        int offset = mStartY - endY;
                        //System.out.println("----------------------offset:" + offset);
                        LayoutParams lp = (LayoutParams) mImageView.getLayoutParams();
                        int bottomMargin = lp.bottomMargin;
                        bottomMargin += offset;
                        if (bottomMargin >= PULL_IMAGE_SIZE_PX_MAX) {
                            bottomMargin = PULL_IMAGE_SIZE_PX_MAX;
                        }

                        if (bottomMargin <= -PULL_IMAGE_SIZE_PX) {
                            bottomMargin = -PULL_IMAGE_SIZE_PX;

                        }
                        lp.setMargins(lp.leftMargin, lp.topMargin, lp.rightMargin, bottomMargin);
                        mImageView.setLayoutParams(lp);

                        rotateAniamtor(bottomMargin * ROTATE_ANIM_ANGLE_PER);

                        mStartY = endY;
                    }

                    LayoutParams lp = (LayoutParams) mImageView.getLayoutParams();
                    //如果按住上拉时，上拉箭头向下滑动的时候事件不应分发
                    if(mVisable && lp.bottomMargin > -PULL_IMAGE_SIZE_PX){
                        mIsDispatch = false;
                    }else if(mVisable && lp.bottomMargin == -PULL_IMAGE_SIZE_PX){//等到上拉箭头被隐藏掉的时候在分发事件
                        mIsDispatch = true;
                    }

                    System.out.println("------------------------P-mImageView--left:"+mImageView.getLeft()
                            +",top:"+mImageView.getTop()+",right:"+mImageView.getRight()
                            +",bottom:"+mImageView.getBottom());

                    //是否分发事件
                    if(!mIsDispatch) {
                        return false;
                    }
                    else {
                        return super.dispatchTouchEvent(event);
                    }
                }

                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                if (!mStart) {

                    if (mVisibleCanScoll) {
                        LayoutParams lp = (LayoutParams) mImageView.getLayoutParams();
                        if (lp.bottomMargin >= PULL_IMAGE_SIZE_PX_EXECUTE) {
                            //lp.setMargins(lp.leftMargin, lp.topMargin, lp.rightMargin, PULL_IMAGE_SIZE_PX / 3 * 2);
                            //mImageView.setLayoutParams(lp);
                            //start();
                            getValueToTranslation();
                            mPrepareAnimation = true;

                            mIsRefreshing = true;//正在刷新

                            if (mOnPullListener != null) {
                                mOnPullListener.onLoadMore(this);
                            }
                        } else {

                            hideArrow();

                        }
                    }
                    if (!mStart && !mPrepareAnimation)
                        hideArrow();
                }

                mIsCanScoll = false;

                break;
        }

        return super.dispatchTouchEvent(event);
    }

    private void hideArrow() {
        LayoutParams lp = (LayoutParams) mImageView.getLayoutParams();
        //lp.setMargins(lp.leftMargin, lp.topMargin, lp.rightMargin, -PULL_IMAGE_SIZE_PX);
        //mImageView.setLayoutParams(lp);

        translationTo(lp.bottomMargin,-PULL_IMAGE_SIZE_PX,false);

    }

    private void showRefreshArrow() {
        mImageView.setVisibility(View.VISIBLE);

        visable();
    }

    /**
     * 隐藏箭头显示的载体ImageView
     */
    private void hideRefreshArrow() {
        mImageView.setVisibility(View.GONE);
    }


    private boolean canPullUp() {
        if(mRecyclerView == null || mRecyclerView.getAdapter() == null) return false;
        RecyclerView.LayoutManager lm = mRecyclerView.getLayoutManager();
        int[] lastPos = new int[lm.getColumnCountForAccessibility(null,null)];//如果是瀑布流就返回最后n个（n为瀑布流列数）
        mLastVisiblePosition = getLastVisibleItemPosition(lastPos);
        int count = mRecyclerView.getAdapter().getItemCount();
        if (0 == count) {
            // 没有item的时候也可以上拉加载
            return true;
        } else if (mLastVisiblePosition == (count - 1)) {
            if(lastPos.length > 0 && lastPos[0] != 0){
                boolean canPullUp = staggeredCanPullUp(lm, lastPos);
                return canPullUp;
            }
            // 滑到底部了
            if (lm.findViewByPosition(count - 1).getBottom() <= getMeasuredHeight()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断最后几个有一个到底部了就说明到底部了
     * @param layoutManagers
     * @param lastPos
     * @return
     */
    private boolean staggeredCanPullUp(RecyclerView.LayoutManager layoutManagers, int[] lastPos){
        if(!(layoutManagers instanceof StaggeredGridLayoutManager)) return false;
        int bottom = 0;
        for(int i=0;i<lastPos.length ; i++){
            /**
             * 判断lastItem的底边到recyclerView顶部的距离
             * 是否小于recyclerView的高度
             * 如果小于或等于 说明滚动到了底部
             * 这样有一个弊端，可能有某一列显示不全就加载更多了
             */
//            if(layoutManagers.findViewByPosition(lastPos[i]).getBottom() <= getHeight() ){
//                /**
//                 * 已到最后条目的底部了
//                 */
//                return true;
//            }
            /**
             * 这个就不存在弊端了
             */
            //取出每一列的最后一个条目的mBottom,并并取出最大的一个与getHeight()比较
            if(layoutManagers.findViewByPosition(lastPos[i]).getBottom() > bottom) {
                bottom = layoutManagers.findViewByPosition(lastPos[i]).getBottom();
            }

        }
        //return false;
        return bottom <= getHeight();
    }


    /**
     * 获取底部可见项的位置
     * 获取最后一个条目的索引
     * @param lastPos 如果是瀑布流的话就返回每一列最后一个条目的索引
     * @return
     */
    private int getLastVisibleItemPosition(int[] lastPos) {
        RecyclerView.LayoutManager lm = mRecyclerView.getLayoutManager();
        int lastVisibleItemPosition = 0;
        if (lm instanceof GridLayoutManager) {
            lastVisibleItemPosition = ((GridLayoutManager) lm).findLastVisibleItemPosition();
        } else if (lm instanceof LinearLayoutManager) {
            lastVisibleItemPosition = ((LinearLayoutManager) lm).findLastVisibleItemPosition();
        }else if(lm instanceof StaggeredGridLayoutManager){
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager)lm;
            int columnCount = layoutManager.getColumnCountForAccessibility(null, null);//获取瀑布流的列数
            //int columnCount = layoutManager.getSpanCount();//获取瀑布流的列数
            int[] positions = new int[columnCount];
            layoutManager.findLastVisibleItemPositions(positions);//获取瀑布流的每一列的最下面的条目的索引（并不是最后n个(n为瀑布流的列数)），有的条目可能会很长
            lastVisibleItemPosition = getArrayMax(positions);//返回其中最大的一个（它是乱序的，并不是按顺序保存的）
            System.arraycopy(positions,0,lastPos,0,positions.length);
            //瀑布流的布局方式是哪一列的高度最小，下一个条目就排到哪一列的后面
        }
        return lastVisibleItemPosition;
    }

    /**
     * 返回最大的一个
     * @param array
     * @return
     */
    private int getArrayMax(int[] array){
        if(array == null && array.length == 0) return 0;
        int max = array[0];
        for(int i=0;i<array.length;i++){
            if(array[i] > max){
                max = array[i];
            }
        }
        return max;
    }


    /**
     * 创建刷新View和初始化一些数据
     */
    private void createProgressView() {
        mImageView = new ImageView(getContext());

        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PULL_IMAGE_SIZE, getContext().getResources().getDisplayMetrics());
        PULL_IMAGE_SIZE_PX = size;
        PULL_IMAGE_SIZE_PX_MAX = PULL_IMAGE_SIZE_PX * 2;
        PULL_IMAGE_SIZE_PX_EXECUTE = PULL_IMAGE_SIZE_PX;
        PULL_IMAGE_SIZE_PX_EXECUTE_REFRESH = PULL_IMAGE_SIZE_PX / 3 * 2;
        ROTATE_ANIM_ANGLE_PER = (360.0f / PULL_IMAGE_SIZE_PX_MAX);
        LayoutParams lp = new LayoutParams(size, size);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.setMargins(lp.leftMargin, lp.topMargin, lp.rightMargin, -PULL_IMAGE_SIZE_PX);
        mImageView.setLayoutParams(lp);
        mImageView.setBackground(getShapeDrawable());

        addView(mImageView);
        mImageView.setVisibility(View.GONE);

        mProgress = new MaterialProgressDrawable(getContext(), mImageView);

        mProgress.setBackgroundColor(CIRCLE_BG_LIGHT);
        //圈圈颜色,可以是多种颜色
        mProgress.setColorSchemeColors(colors);
        //设置圈圈的各种大小
        mProgress.updateSizes(MaterialProgressDrawable.LARGE);

        mImageView.setImageDrawable(mProgress);
    }

    /**
     * mImageView的背景
     */
    private Drawable getShapeDrawable() {
        /**
         * <layer-list xmlns:android="http://schemas.android.com/apk/res/android" >
         <!-- 第一层  上部和左部偏移一定距离-->
         <item
         >
         <shape android:shape="oval">
         <solid android:color="#f5f5f5" />
         <!-- 描边 -->
         <stroke
         android:width="0.5dp"
         android:color="#99f5f5f5" />
         </shape>
         </item>
         <!-- 第二层 下部和有部偏移一定距离-->
         <item
         android:left="2dp"
         android:top="2dp"
         android:bottom="2dp"
         android:right="2dp">
         <shape android:shape="oval">
         <solid android:color="#ffffff" />
         <!-- 描边 -->
         <!--<stroke android:width="0.33dp" android:color="#dedede" />-->
         </shape>
         </item>
         </layer-list>
         */
        //代码实现
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.OVAL);
        gradientDrawable.setColor(Color.parseColor("#f5f5f5"));
        int stroke = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.5f, getContext().getResources().getDisplayMetrics());
        gradientDrawable.setStroke(stroke,Color.parseColor("#99f5f5f5"));
        GradientDrawable gradientDrawable2 = new GradientDrawable();
        gradientDrawable2.setShape(GradientDrawable.OVAL);
        gradientDrawable2.setColor(Color.parseColor("#ffffff"));
        LayerDrawable drawable = new LayerDrawable(new Drawable[]{gradientDrawable,gradientDrawable2});
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getContext().getResources().getDisplayMetrics());
        drawable.setLayerInset(1,padding,padding,padding,padding);////第一个参数1代表数组的第二个元素，为白色
        return drawable;
    }

    /**
     * 隐藏箭头
     */
    private void hide() {
        if (mValueAnimator != null) {
            mValueAnimator.cancel();
            mVisable = false;
            mVisibleCanScoll = false;
        }

    }

    private void visable() {
        if (mValueAnimator == null) {
            mValueAnimator = mValueAnimator.ofFloat(0f, 1f);
            mValueAnimator.setDuration(10);
            mValueAnimator.setInterpolator(new DecelerateInterpolator());
            mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float n = (float) animation.getAnimatedValue();
                    //圈圈的旋转角度
                    mProgress.setProgressRotation(n * 0.5f);
                    //圈圈周长，0f-1F
                    mProgress.setStartEndTrim(0f, n * 0.8f);
                    //箭头大小，0f-1F
                    mProgress.setArrowScale(n);
                    //透明度，0-255
                    mProgress.setAlpha((int) (255 * n));
                }
            });
            mValueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mVisable = true;

                }
            });
        }

        if (!mValueAnimator.isRunning()) {
            if (!mVisable) {
                //是否显示箭头
                mProgress.showArrow(true);
                mValueAnimator.start();
                mVisibleCanScoll = true;
            }
        }
    }

    private void start() {
        if (mVisable) {
            if (!mStart) {
                mProgress.start();

                mStart = true;

            }
        }
    }

    /**
     * 计算执行动画的距离参数
     */
    private void getValueToTranslation() {
        //如果mImageView还没有被创建出来是不会执行的
        if(mImageView != null) {
            LayoutParams lp = (LayoutParams) mImageView.getLayoutParams();
            int bottomMargin = lp.bottomMargin;
            //执行平移
            translationTo(bottomMargin, PULL_IMAGE_SIZE_PX_EXECUTE_REFRESH, true);
        }
    }

    private void stop() {
        if (mStart) {
            mProgress.stop();
            mStart = false;
            mVisable = false;
            mVisibleCanScoll = false;
        }
    }


    /**
     * 执行平移动画
     * @param from
     * @param to
     */
    private void translationTo(int from,int to,final boolean isShow){
        //1.调用ofInt(int...values)方法创建ValueAnimator对象
        ValueAnimator mAnimator = ValueAnimator.ofInt(from,to);
        //2.为目标对象的属性变化设置监听器
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 3.为目标对象的属性设置计算好的属性值
                int animatorValue = (int)animation.getAnimatedValue();
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams) mImageView.getLayoutParams();
                marginLayoutParams.bottomMargin = animatorValue;
                mImageView.setLayoutParams(marginLayoutParams);
            }
        });
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(isShow){
                    start();
                    mPrepareAnimation = false;
                    //设置准备刷新的回调，防止在没有到刷新的位置，就已经调用了setRefreshing(false)来停止刷新，这样就会出现问题
                    //内部使用
                    if(mPrepareAnimationListener != null){
                        mPrepareAnimationListener.finishAnimation();
                    }
                }else{
                    hideRefreshArrow();
                    hide();
                }

            }
        });
        //4.设置动画的持续时间、是否重复及重复次数等属性
        mAnimator.setDuration(100);
        //mAnimator.setRepeatCount(3);
        mAnimator.setRepeatMode(ValueAnimator.INFINITE);
        //5.为ValueAnimator设置目标对象并开始执行动画
        mAnimator.setTarget(mImageView);
        mAnimator.start();
    }

    /**
     * 旋转动画效果
     */
    private void rotateAniamtor(float from){

        ObjectAnimator mAnimatorRotate = ObjectAnimator.ofFloat(mImageView, "rotation",from,from + 1);
        mAnimatorRotate.setRepeatMode(Animation.INFINITE);
        mAnimatorRotate.setRepeatCount(1);
        mAnimatorRotate.setDuration(10);
        mAnimatorRotate.start();
    }


    /**
     * 加载更多或停止加载更多
     * @param refreshing
     */
    public void setRefreshing(boolean refreshing) {
        if(!mIsAllowLoadMore) return;
        if(refreshing){
            if(mStart) return;
            showRefreshArrow();
            getValueToTranslation();
            mPrepareAnimation = true;

            mIsRefreshing = true;//正在刷新

            if (mOnPullListener != null) {
                mOnPullListener.onLoadMore(this);
            }
            mIsCanScoll = false;
        }else {
            //如果准备执行动画（平移到刷新转圈的位置）结束后才能停止
            if(!mPrepareAnimation) {
                setRefreshStop();
            }else{
                //上拉加载的View还没有到刷新转圈的地方，就停止了刷新，所以要设置准备刷新的监听，
                //等到刷新的地方了，再调用停止刷新
                setPrepareAnimationListener(new PrepareAnimationListener() {
                    @Override
                    public void finishAnimation() {
                        setRefreshStop();
                    }
                });
            }
        }
    }

    /**
     * 停止刷新
     */
    private void setRefreshStop(){
        stop();
        hideArrow();

        mIsRefreshing = false;//取消正在刷新
    }

    /**
     * 上拉到最下面的时候时候触发加载更多
     * 是否需要上拉加载更多
     * @param enabled
     */
    public void setScrollLoadEnabled(boolean enabled){
        mScrollLoadEnabled = enabled;
    }

    /**
     * 当前是否在上拉刷新
     * @return
     */
    public boolean isRefreshing(){
        return mStart;
    }


    /**
     * 刷新加载回调接口
     *
     */
    public interface OnPullListener {

        /**
         * 加载操作
         */
        void onLoadMore(PullRefreshLayout pullRefreshLayout);
    }

    private OnPullListener mOnPullListener;

    public void setOnPullListener(OnPullListener listener) {
        mOnPullListener = listener;
    }


    /**
     * 准备刷新动画的监听，就是上拉后，移动到刷新位置的监听
     */
    interface PrepareAnimationListener{
        void finishAnimation();
    }

    private PrepareAnimationListener mPrepareAnimationListener;

    private void setPrepareAnimationListener(PrepareAnimationListener listener){
        mPrepareAnimationListener = listener;
    }

}
