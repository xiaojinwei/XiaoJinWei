package com.cj.chenj.recyclerview_lib.layout;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by cj_28 on 2017/1/14.
 */

public class DraggingLayout extends FrameLayout {

    private ViewDragHelper mViewDragHelper;

    public DraggingLayout(Context context) {
        this(context,null);
    }

    public DraggingLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    //执行顺序 1
    //在构造中不能获取子View，因为此时父容器本身还没有创建出来
    public DraggingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //1.初始化托拽辅助类
        mViewDragHelper = ViewDragHelper.create(this,mCallback);

    }

    private int mStartX;
    private int mStartY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mStartX = (int)ev.getRawX();
                mStartY = (int)ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int _endX = (int)ev.getRawX();
                int _endY = (int)ev.getRawY();
                int _offsetX = Math.abs(_endX - mStartX);
                int _offsetY = Math.abs(_endY - mStartY);
                if(_offsetX > _offsetY){
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_UP:
                int endX = (int)ev.getRawX();
                int endY = (int)ev.getRawY();
                int offsetX = Math.abs(endX - mStartX);
                int offsetY = Math.abs(endY - mStartY);
                if(offsetX > offsetY){
                    getParent().requestDisallowInterceptTouchEvent(true);
                }

                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        //System.out.println("-------------------onInterceptTouchEvent");
        //2.让ViewDragHelper决定是否拦截触摸事件
        return mViewDragHelper.shouldInterceptTouchEvent(event);
    }

    private int mStartTX;
    private int mStartTY;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //System.out.println("-------------------onTouchEvent");

        //3.处理触摸事件 ,如果是向上活动就不消费（冲突处理）
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mStartTX = (int)ev.getRawX();
                mStartTY = (int)ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int _endX = (int)ev.getRawX();
                int _endY = (int)ev.getRawY();
                int _offsetX = Math.abs(_endX - mStartTX);
                int _offsetY = Math.abs(_endY - mStartTY);
                if(_offsetX < _offsetY){
                    return false;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                return false;

        }

        //如果是左右滑动就处理（侧滑）
        mViewDragHelper.processTouchEvent(ev);

        return true;
    }

    //4.处理回调事件
    private ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {

        private boolean mIsClick = true;//模拟点击事件
        private long mStartDownTime;//按下的时间,200以内为单击

        private int mSlidingDistance;

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            mIsClick = true;
            mStartDownTime = System.currentTimeMillis();
            mSlidingDistance = 0;
            //返回值决定手指拖拽的子控件是否可以被做拽
            return true;
        }

        /**
         * 返回一个大于0 的值，水平方向上拖拽的范围，它不会真正的限制这个大小
         * @param child
         * @return
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return mDraggingViewWidth;
        }

        /**
         * 返回值决定被拖拽的子控件将要移动的位置，此时还没有发生真正的移动，
         * 修正水平方向上要移动的位置
         * @param child
         * @param left
         * @param dx
         * @return
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            //child : 拖拽的子控件
            //left : 建议移动的位置，child的mLeft属性的值
            //dx : 新的位置和旧的位置的差值
            //限定前布局拖拽的范围
            if(child == mFrontView){
                if(left > 0){
                    left = 0;
                }else if(left < -mDraggingViewWidth){
                    left = - mDraggingViewWidth;
                }
            }
            //限制后布局拖拽的范围
            if(child == mDraggingView){
                int range = mFrontViewWidth - mDraggingViewWidth;
                if(left < range){
                    left = range;
                }else if(left > mFrontViewWidth){
                    left = mFrontViewWidth;
                }
            }
            return left;
        }

        /**
         * 移动后调用，伴随动画，更新控件状态，相当于action_move
         * @param changedView
         * @param left
         * @param top
         * @param dx
         * @param dy
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            //*******
            //如果向左移动了超过5个像素，就不算点击
            if(left < -5){
                mIsClick = false;
            }
            mSlidingDistance = Math.abs(dx);
            //*******
            if(changedView == mFrontView){
                //把前布局移动的变化量累加给后布局
                mDraggingView.offsetLeftAndRight(dx);
            }else if(changedView == mDraggingView){
                //把后布局移动的变化量累加给前布局
                mFrontView.offsetLeftAndRight(dx);
            }

            dispatchDraggingEvent();

            //重绘，
            invalidate();
        }

        /**
         * 松手时的回调
         * @param releasedChild
         * @param xvel
         * @param yvel
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            //xvel: 水平方向上的速度，向右为+，向左为-
            if(xvel == 0 && mFrontView.getLeft() == 0 && mIsClick && (System.currentTimeMillis() - mStartDownTime) < 150){
                //没有速度，并且是闭合状态，此状态当点击处理
                if(mDraggingContentClickListener != null){
                    mDraggingContentClickListener.onContentClick(mFrontView);
                }
            }else if(/**xvel == 0 && */mFrontView.getLeft() < -mDraggingViewWidth * 0.5f){
                open();
            }else if(xvel < 0 && mSlidingDistance > 10){//速度小于0并且滑动了10个像素的距离
                open();
            }else{
                close();
            }

            mSlidingDistance = 0;
        }
    };



    private View mDraggingView;
    private View mFrontView;
    private int mDraggingViewWidth;
    private int mDraggingViewHeight;
    private int mFrontViewWidth;
    private int mFrontViewHeight;
    private int mRange;//拖动的范围，（侧滑出的菜单的宽度）

    private Status mLastStatus;


    private void dispatchDraggingEvent() {
        //更新重绘
        int left = mFrontView.getLeft();
        mLastStatus = mStatus;
        if(left == 0){
            mStatus = Status.CLOSE;
        }else if(left == -mRange){
            mStatus = Status.OPEN;
        }else {
            mStatus = Status.DRAGGING;
        }

        //回调接口
        if(mDraggingUpdateListener != null) {
            if (mStatus == Status.OPEN) {
                mDraggingUpdateListener.onOpen(this);
            }else if(mStatus == Status.CLOSE){
                mDraggingUpdateListener.onClose(this);
            }else if(mLastStatus == Status.OPEN){
                //如果上一次是开打的，并且此时在拖拽中，就说明将要做关闭动作
                mDraggingUpdateListener.onWillColse(this);
            }else if(mLastStatus == Status.CLOSE){
                //如果上一次是关闭的，并且此时在拖拽中，就说明将要做打开动作
                mDraggingUpdateListener.onWillOpen(this);
            }

        }
    }

    public void open(){
        open(true);
    }

    private void open(boolean isSmooth){
        if(isSmooth){
            int finalLeft = -mRange;
            //开始动画
            if(mViewDragHelper.smoothSlideViewTo(mFrontView,finalLeft,0)){
                //重新绘制
                invalidate();
            }
        }else {
            layoutChildView(true);
        }
    }

    public void close(){
        close(true);
    }

    private void close(boolean isSmooth){
        if(isSmooth){
            int finalLeft = 0;
            //开始动画
            if(mViewDragHelper.smoothSlideViewTo(mFrontView,finalLeft,0)){
                //重绘界面
                invalidate();
            }
        }else {
            layoutChildView(false);
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        //维持动画
        if(mViewDragHelper.continueSettling(true)){
            invalidate();
        }
    }

    //执行顺序 2
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //mDraggingView = getChildAt(0);
        //mFrontView = getChildAt(1);

        //************************************

        if(getChildCount() > 2){
            throw new RuntimeException("DraggingLayout 中只能存在一个拖动的菜单布局和一个条目布局");
        }

        if(getChildCount() == 1){
            if(mDraggingView == null) {
                mDraggingView = getDraggingView();
            }
            mFrontView = getChildAt(0);
            return;
        }

        if(getChildCount() == 2){
            mDraggingView = getChildAt(0);
            mFrontView = getChildAt(1);
        }

    }

    private View getDraggingView(){
        LinearLayout linearLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(lp);
        addView(linearLayout);
        return linearLayout;
    }

    /**
     * 添加托动条目布局
     * @param texts
     */
    public synchronized void addDraggingItemLayout(String ... texts){

        if(mDraggingView == null){
            mDraggingView = getDraggingView();
        }
        if(mDraggingView instanceof ViewGroup){
            if(texts == null || texts.length > 3){
                throw new RuntimeException("texts不能为null,或最多只能设置3个拖拽的侧滑按钮");
            }
            ((ViewGroup) mDraggingView).removeAllViews();

            for(int i=0;i<texts.length;i++){
                View itemTextView = getItemTextView(texts[i], mTextBackground[i]);
                ((ViewGroup)mDraggingView).addView(itemTextView,0);
                setDraggingItemListener(itemTextView,i,texts[i]);
            }
            //因为条目布局是复用的，所以每次都要重新测量和布局，不然会出现拖拽布局还是之前的大小

            ((ViewGroup) mDraggingView).addOnLayoutChangeListener(new OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    //移除监听
                    ((ViewGroup) mDraggingView).removeOnLayoutChangeListener(this);
                    //这里一定要重新计算托拽菜单的大小
                    calculateLayoutSize();
                }
            });

        }
    }

    private void setDraggingItemListener(View itemTextView, final int i, final String text) {
        itemTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDraggingItemClickListener != null){
                    mDraggingItemClickListener.onItemClick(i,text);
                }
            }
        });
    }

    private int[] mTextBackground = {Color.RED,Color.CYAN,Color.GRAY};

    private View getItemTextView(String text,int color){
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setTextSize(16);
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundColor(color);
        textView.setPadding(dp2px(16),0,dp2px(16),0);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.MATCH_PARENT
        );
        lp.gravity = Gravity.CENTER;
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(lp);
        return textView;
    }

    private int dp2px(int dp){
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,getContext().getResources().getDisplayMetrics());
    }

    //执行顺序 4
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);

        calculateLayoutSize();
    }

    private void calculateLayoutSize(){
        mDraggingViewWidth = mDraggingView.getMeasuredWidth();
        mDraggingViewHeight = mDraggingView.getMeasuredHeight();
        mRange = mDraggingViewWidth;

        mFrontViewWidth = mFrontView.getMeasuredWidth();
        mFrontViewHeight = mFrontView.getMeasuredHeight();
    }

    private int mWidthMeasureSpec;
    private int mHeightMeasureSpec;

    //执行顺序 3
    //此方法结束，子View的大小就已经可以获取到了
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidthMeasureSpec = widthMeasureSpec;
        mHeightMeasureSpec = heightMeasureSpec;
    }


    //执行顺序 5
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        layoutChildView(false);
        calculateLayoutSize();
    }

    private void layoutChildView(boolean isOpen) {
        if(isOpen){
            mFrontView.layout(-mRange,0,mFrontViewWidth - mRange,mFrontViewHeight);
        }else{
            mFrontView.layout(0,0,mFrontViewWidth,mFrontViewHeight);
        }
        int right = mFrontView.getRight();
        mDraggingView.layout(right,0,right+mDraggingViewWidth,mDraggingViewHeight);
    }


    public interface DraggingUpdateListener{
        void onOpen(DraggingLayout draggingLayout);
        void onClose(DraggingLayout draggingLayout);
        void onWillOpen(DraggingLayout draggingLayout);
        void onWillColse(DraggingLayout draggingLayout);
    }

    /**
     * 拖拽时条目的状态
     * @param mDraggingUpdateListener
     */
    public void setDraggingUpdateListener(DraggingUpdateListener mDraggingUpdateListener) {
        this.mDraggingUpdateListener = mDraggingUpdateListener;
    }

    protected void setStatus(Status mStatus) {
        this.mStatus = mStatus;
    }

    public Status getStatus() {
        return mStatus;
    }

    private DraggingUpdateListener mDraggingUpdateListener;

    enum Status{
        OPEN,CLOSE,DRAGGING
    }

    private Status mStatus = Status.CLOSE;

    public interface DraggingItemClickListener{
        void onItemClick(int index, String text);
    }

    private DraggingItemClickListener mDraggingItemClickListener;

    public void setDraggingItemClickListener(DraggingItemClickListener listener){
        mDraggingItemClickListener = listener;
    }

    public interface DraggingContentClickListener{
        void onContentClick(View view);
    }

    private DraggingContentClickListener mDraggingContentClickListener;

    /**
     * 条目点击事件
     * @param listener
     */
    public void setDraggingContentClickListener(DraggingContentClickListener listener){
        mDraggingContentClickListener = listener;
    }
}
