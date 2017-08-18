package com.cj.imageselector.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cj.imageselector.R;
import com.cj.imageselector.bean.FolderBean;
import com.cj.imageselector.util.ImageLoader;

import java.util.List;

/**
 * Created by chenj on 2017/8/15.
 */

public class ListImageDirPopupWindow extends PopupWindow {

    private int mWidth;
    private int mHeight;
    private View mConvertView;
    private ListView mListView;
    private List<FolderBean> mDatas;

    public ListImageDirPopupWindow(Context context, List<FolderBean> datas) {
        calWidthAndHeight(context);
        mConvertView = LayoutInflater.from(context).inflate(R.layout.popup_folder,null);
        mDatas = datas;

        setContentView(mConvertView);
        setWidth(mWidth);
        setHeight(mHeight);

        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());

        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });

        initView(context);
        initEvent();
    }

    private void initEvent() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mOnDirSelectedListener != null) {
                    mOnDirSelectedListener.onSelected(mDatas.get(position));
                }
            }
        });
    }

    private void initView(Context context) {
        mListView = (ListView) mConvertView.findViewById(R.id.list_dir);

        mListView.setAdapter(new ListDirAdapter(context,mDatas));
    }

    private void calWidthAndHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        mWidth = metrics.widthPixels;
        mHeight = (int) (metrics.heightPixels * 0.7);
    }

    private class ListDirAdapter extends ArrayAdapter<FolderBean>{

        private LayoutInflater mInflater;
        private List<FolderBean> mDatas;

        public ListDirAdapter(Context context, List<FolderBean> objects) {
            super(context, 0, objects);
            mInflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.popup_folder_item,parent,false);
                viewHolder = new ViewHolder();
                viewHolder.mImg = (ImageView) convertView.findViewById(R.id.dir_item_image);
                viewHolder.mDirName = (TextView) convertView.findViewById(R.id.dir_item_name);
                viewHolder.mDirCount = (TextView) convertView.findViewById(R.id.dir_item_count);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            FolderBean item = getItem(position);
            //重置图片
            viewHolder.mImg.setImageResource(R.drawable.pictures_no);
            //加载图片
            ImageLoader.getInstance().loadImage(item.getFirstImgPath(), viewHolder.mImg);
            viewHolder.mDirName.setText(item.getName());
            viewHolder.mDirCount.setText(item.getCount() + "张");
            return convertView;
        }

        private class ViewHolder{
            ImageView mImg;
            TextView mDirName;
            TextView mDirCount;
        }
    }

    public interface OnDirSelectedListener{
        void onSelected(FolderBean folderBean);
    }

    private OnDirSelectedListener mOnDirSelectedListener;

    public void setOnDirSelectedListener(OnDirSelectedListener onDirSelectedListener) {
        mOnDirSelectedListener = onDirSelectedListener;
    }
}
