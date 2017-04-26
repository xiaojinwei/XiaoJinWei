package com.cj.chenj.recyclerview_lib.layoutmanager;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import java.util.Collections;
import java.util.List;

/**
 * Created by chenj on 2017/1/18.
 */

public class CardDragUtil {
    public static <T> void cardDrag(final RecyclerView rv, final List<T> datas){
        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(0,
                //ItemTouchHelper.DOWN | ItemTouchHelper.UP |
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
        ) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int layoutPosition = viewHolder.getLayoutPosition();

                T remove = datas.remove(layoutPosition);
                datas.add(0,remove);

                rv.getAdapter().notifyDataSetChanged();//让其重新走LayoutManager中的onLayoutChild()


                viewHolder.itemView.setRotation(0);
//                if(viewHolder instanceof ViewHolder){
//                    ViewHolder holder = (ViewHolder)viewHolder;
//                    holder.getView(R.id.view);//如需要操作相应布局中的控件，获取布局中的控件进行操作
//                    //...
//                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                //先根据滑动的dx dy算出现在动画的比列系数fraction
                double swipValue = Math.sqrt(dX * dX + dY * dY);
                double fraction = swipValue /getThreshold(viewHolder);
                //边界修正，最大为1
                if(fraction > 1){
                    fraction = 1;
                }
                //对每个ChildView进行播放，位移
                int childCount = rv.getChildCount();
                for(int i=0;i<childCount;i++){
                    View child = rv.getChildAt(i);
                    int level = childCount - i - 1;
                    if(level > 0){
                        child.setScaleX((float)(1-CardConfig.SCALE_GAP*level+fraction*CardConfig.SCALE_GAP));
                        if(level < CardConfig.MAX_SHOW_COUNT - 1){
                            child.setScaleY((float)(1-CardConfig.SCALE_GAP*level+fraction*CardConfig.SCALE_GAP));
                            child.setTranslationY((float)(CardConfig.TRANS_Y_GAP*level-fraction*CardConfig.TRANS_Y_GAP));
                        }
                    }else{
                        float xFraction = dX / getThreshold(viewHolder);
                        if(xFraction > 1){
                            xFraction = 1;
                        }else if(xFraction < -1){
                            xFraction = -1;
                        }
                        child.setRotation(xFraction*CardConfig.MAX_ROTATION);
//                        if(viewHolder instanceof ViewHolder){
//                            ViewHolder holder = (ViewHolder)viewHolder;
//                            holder.getView(R.id.view);//如需要操作相应布局中的控件，获取布局中的控件进行操作
//                            //...
//                        }
                    }
                }
            }

            /**
             * 删除移动过程中的阈值
             * @param viewHolder
             * @return
             */
            private float getThreshold(RecyclerView.ViewHolder viewHolder){
                return rv.getWidth() * getSwipeThreshold(viewHolder);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(rv);
    }
}
