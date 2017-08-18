package com.cj.imageselector.adpter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cj.imageselector.R;
import com.cj.imageselector.util.ImageLoader;

import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by chenj on 2017/8/16.
 */

public class ImageDetailAdapter extends PagerAdapter {

    private String mDirPath;////图片的父路径
    private List<String> mImgPaths;
    private Context mContext;

    public ImageDetailAdapter(Context context,String dirPath, List<String> imgPaths) {
        mDirPath = dirPath;
        mImgPaths = imgPaths;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mImgPaths == null ? 0 : mImgPaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView photoView = new PhotoView(mContext);
        photoView.setImageResource(R.drawable.pictures_no);
        String picPath ;
        if (!TextUtils.isEmpty(mDirPath)) {
            picPath = mDirPath + "/" + mImgPaths.get(position);
        }else {
            picPath = mImgPaths.get(position);
        }
        //ImageLoader.getInstance().loadImage(picPath,photoView);
        Glide.with(mContext).load(picPath).placeholder(R.drawable.pictures_no).into(photoView);
        container.addView(photoView);

        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float v, float v1) {
                if (mOnClickListener != null) {
                    mOnClickListener.onclick(view);
                }
            }
        });

        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    private OnClickListener mOnClickListener;

    public void setOnClickListener(OnClickListener listener) {
        mOnClickListener = listener;
    }

    public interface OnClickListener{
        void onclick(View view);
    }
}
