package com.cj.imageselector.adpter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cj.imageselector.R;
import com.cj.imageselector.activity.ImageLoaderActivity;
import com.cj.imageselector.util.ImageLoader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by chenj on 2017/8/15.
 */

public class ImageAdapter extends BaseAdapter {

    private String mDirPath;//图片的父路径
    private List<String> mImgPaths;
    private LayoutInflater mInflater;
    private Context mContext;

    public void setDirPath(String dirPath) {
        mDirPath = dirPath;
    }

    public ImageAdapter(Context context, String dirPath, List<String> datas) {
        this.mDirPath = dirPath;
        this.mImgPaths = datas;
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mImgPaths == null ? 0 : mImgPaths.size();
    }

    @Override
    public Object getItem(int position) {
        return mImgPaths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;//final 不能两次被初始化，所以这里不能赋null
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.gridview_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.mImg = (ImageView) convertView.findViewById(R.id.image_item);
            viewHolder.mSelect = (ImageView) convertView.findViewById(R.id.select_item);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //重置状态
        viewHolder.mImg.setImageResource(R.drawable.pictures_no);
        viewHolder.mSelect.setImageResource(R.drawable.picture_unselected);

        final String picPath = mDirPath + "/" + mImgPaths.get(position);
        //ImageLoader.getInstance(4, ImageLoader.Type.LIFO)
        //        .loadImage(picPath,viewHolder.mImg);

        Glide.with(mContext).load(picPath).placeholder(R.drawable.pictures_no).into(viewHolder.mImg);

        viewHolder.mSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ImageLoaderActivity.containsImg(picPath)) {
                    ImageLoaderActivity.removeImg(mContext,picPath);
                    viewHolder.mImg.setColorFilter(null);
                    viewHolder.mSelect.setImageResource(R.drawable.picture_unselected);
                }else{
                    boolean flag = ImageLoaderActivity.addImg(mContext, picPath);
                    if (flag) {
                        viewHolder.mImg.setColorFilter(Color.parseColor("#77000000"));
                        viewHolder.mSelect.setImageResource(R.drawable.pictures_selected);
                    }
                }
            }
        });

        //是否选中
        if (ImageLoaderActivity.containsImg(picPath)) {
            viewHolder.mImg.setColorFilter(Color.parseColor("#77000000"));
            viewHolder.mSelect.setImageResource(R.drawable.pictures_selected);
        }else{
            viewHolder.mImg.setColorFilter(null);
            viewHolder.mSelect.setImageResource(R.drawable.picture_unselected);
        }

        return convertView;
    }

    private class ViewHolder{
        ImageView mImg;
        ImageView mSelect;
    }
}
