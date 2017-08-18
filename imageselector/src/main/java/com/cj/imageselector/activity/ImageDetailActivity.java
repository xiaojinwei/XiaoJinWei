package com.cj.imageselector.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cj.imageselector.R;
import com.cj.imageselector.adpter.ImageAdapter;
import com.cj.imageselector.adpter.ImageDetailAdapter;
import com.cj.imageselector.common.KeyConstants;
import com.cj.imageselector.util.StatusBarUtil;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.List;

/**
 * Created by chenj on 2017/8/16.
 */

public class ImageDetailActivity extends AppCompatActivity {

    private String mDirPath;//图片的父路径
    private List<String> mImgPaths;
    private ViewPager mViewPager;
    private ImageView mSelectButton;
    private Toolbar mToolbar;

    private int mCurrentIndex ;

    private boolean mIsShowContnet = true;
    private RelativeLayout mDetailBottomContainer;

    int mStatusBarHeight;
    private RelativeLayout mToolbarRl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);
        //StatusBarUtil.statusBarTintColor(this,R.color.status_bg);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mSelectButton = (ImageView) findViewById(R.id.select_item);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mDetailBottomContainer = (RelativeLayout) findViewById(R.id.detail_bottom_container);
        mToolbarRl = (RelativeLayout) findViewById(R.id.toolbar_rl);

        initLayoutToolbar();

        Intent intent = getIntent();
        mDirPath = intent.getStringExtra(KeyConstants.DIR_PATH);
        mImgPaths = intent.getStringArrayListExtra(KeyConstants.IMG_PATHS);
        int imgIndex = intent.getIntExtra(KeyConstants.IMG_INDEX, 0);
        mCurrentIndex = imgIndex;
        if (mImgPaths == null) {
            finish();
            return;
        }

        ImageDetailAdapter detailAdapter = new ImageDetailAdapter(this,mDirPath,mImgPaths);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(detailAdapter);
        mViewPager.setCurrentItem(imgIndex);

        detailAdapter.setOnClickListener(new ImageDetailAdapter.OnClickListener() {
            @Override
            public void onclick(View view) {
                if (mIsShowContnet) {
                    //隐藏
                    ViewPropertyAnimator.animate(mToolbarRl).translationY(-(mToolbarRl.getMeasuredHeight())).setDuration(300);
                    ViewPropertyAnimator.animate(mDetailBottomContainer).translationY(mDetailBottomContainer.getMeasuredHeight()).setDuration(300);
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
                }else{
                    //显示
                    ViewPropertyAnimator.animate(mToolbarRl).translationY(0).setDuration(300);
                    ViewPropertyAnimator.animate(mDetailBottomContainer).translationY(0).setDuration(300);
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                }
                mIsShowContnet = !mIsShowContnet;
            }
        });

        initEvent();
        setToolBar(mToolbar,(mCurrentIndex + 1 )+ "/" + mImgPaths.size());
    }

    private void initLayoutToolbar() {
        int statusBarHeight = StatusBarUtil.getStatusBarHeight(this);
        mStatusBarHeight = statusBarHeight;
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mToolbar.getLayoutParams();
        lp.topMargin = statusBarHeight;
        mToolbar.setLayoutParams(lp);
    }

    protected void setToolBar(Toolbar toolbar, String title) {
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void changeTitle(Toolbar toolbar, String title) {
        toolbar.setTitle(title);
    }

    private void initEvent() {

        mSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String picPath ;
                if (!TextUtils.isEmpty(mDirPath)) {
                    picPath = mDirPath + "/" + mImgPaths.get(mCurrentIndex);
                }else {
                    picPath = mImgPaths.get(mCurrentIndex);
                }

                if (ImageLoaderActivity.containsImg(picPath)) {
                    ImageLoaderActivity.removeImg(ImageDetailActivity.this,picPath);
                    mSelectButton.setImageResource(R.drawable.picture_unselected);

                }else{
                    boolean flag = ImageLoaderActivity.addImg(ImageDetailActivity.this, picPath);
                    if (flag) {
                        mSelectButton.setImageResource(R.drawable.pictures_selected);
                    }
                }
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mCurrentIndex = position;
                String picPath ;
                if (!TextUtils.isEmpty(mDirPath)) {
                    picPath = mDirPath + "/" + mImgPaths.get(mCurrentIndex);
                }else {
                    picPath = mImgPaths.get(mCurrentIndex);
                }
                if (ImageLoaderActivity.containsImg(picPath)) {
                    mSelectButton.setImageResource(R.drawable.pictures_selected);
                }else{
                    mSelectButton.setImageResource(R.drawable.picture_unselected);
                }

                changeTitle(mToolbar,(mCurrentIndex + 1 ) + "/" + mImgPaths.size());
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        setResult(RESULT_OK,intent);

        super.onBackPressed();

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}
