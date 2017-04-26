package com.cj.chenj.recyclerview_lib.layoutmanager;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * RecyclerView的布局管理器,RecyclerView如何布局（显示方式）就是LayoutManager来控制的
 * Created by chenj on 2017/1/17.
 */

public class OverLayCardLayoutManeger extends RecyclerView.LayoutManager {
    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
        );
    }

    /**
     * 布局RecyclerView中的子View（条目）
     * @param recycler
     * @param state
     */
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);
        int itemCount = getItemCount();
        if(itemCount >= CardConfig.MAX_SHOW_COUNT){
            //从可见的最底层View开始layout，依次层叠上去
            for(int position = itemCount - CardConfig.MAX_SHOW_COUNT;position < itemCount;position++){
                View view = recycler.getViewForPosition(position);
                addView(view);
                measureChildWithMargins(view,0,0);
                int widthSpace = getWidth() - getDecoratedMeasuredWidth(view);
                int heightSpace = getHeight() - getDecoratedMeasuredHeight(view);
                //我们在布局时，将childView居中显示，这里也可以改为只水平居中
                layoutDecoratedWithMargins(view,widthSpace/2,heightSpace/2,
                        widthSpace/2 + getDecoratedMeasuredWidth(view),
                        heightSpace/2 + getDecoratedMeasuredHeight(view));


                int level = itemCount - position - 1;
                //除了顶层不需要缩小和位移
                if(level > 0){
                    view.setScaleX(1-CardConfig.SCALE_GAP*level);
                    //前N层，依次向下位移和y方向的缩小.最下面两张的缩小平移一样
                    if(level < CardConfig.MAX_SHOW_COUNT - 1){
                        view.setTranslationY(CardConfig.TRANS_Y_GAP * level);
                        view.setScaleY(1-CardConfig.SCALE_GAP*level);
                    }else{
                        view.setTranslationY(CardConfig.TRANS_Y_GAP * (level - 1));
                        view.setScaleY(1-CardConfig.SCALE_GAP*(level - 1));
                    }
                }
            }
        }

    }


}
