package com.cj.imageselector.util;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;

import com.cj.imageselector.activity.ImageLoaderActivity;
import com.cj.imageselector.common.KeyConstants;

/**
 * 图片选择快速上手工具
 * Created by chenj on 2017/8/17.
 */

public class ImageSelector {

    ////选择图片返回的结果的key(返回的是集合)
    public static final String EXTRA_CHOICE_RESULT = ImageLoaderActivity.EXTRA_CHOICE_RESULT;

    private int sMode = ImageLoaderActivity.MODE_MULTI;
    private int sMaxSelectCount = ImageLoaderActivity.EXTRA_DEFAULT_SELECT_MAX_COUNT;
    private boolean sIsSavaStatus = false;
    //进入时就清除状态（如果之前是保存状态，下次进入的时候不要之前选中的状态（图片））
    private boolean sIsEnterClearStatus = false;

    private ImageSelector(){}

    public static ImageSelector create() {
        return new ImageSelector();
    }

    /**
     * 多选
     * @return
     */
    public ImageSelector multi() {
        sMode = ImageLoaderActivity.MODE_MULTI;
        return this;
    }

    /**
     * 单选
     * @return
     */
    public ImageSelector single() {
        sMode = ImageLoaderActivity.MODE_SINGLE;
        return this;
    }

    /**
     * 最大选择个数
     * @param count
     * @return
     */
    public ImageSelector selectCount(int count) {
        if (count < 1) {
            count = 1;
        }
        sMaxSelectCount = count;
        return this;
    }

    /**
     * 是否保存上次的状态（不保存在退出时会清除）
     * @param flag
     * @return
     */
    public ImageSelector saveStatus(boolean flag) {
        sIsSavaStatus = flag;
        return this;
    }

    public ImageSelector enterClearStatus() {
        sIsEnterClearStatus = true;
        return this;
    }

    /**
     * 跳转
     * @param activity
     * @param requestCode
     */
    public void startActivity(Activity activity,int requestCode) {
        Intent intent = new Intent();
        intent.setClass(activity, ImageLoaderActivity.class);
        intent.putExtra(ImageLoaderActivity.EXTRA_SAVE_STATUS,sIsSavaStatus);
        intent.putExtra(ImageLoaderActivity.EXTRA_SELECT_MODE,sMode);
        intent.putExtra(ImageLoaderActivity.EXTRA_SELECT_COUNT,sMaxSelectCount);
        intent.putExtra(ImageLoaderActivity.EXTRA_ENTER_CLEAR_STATUS,sIsEnterClearStatus);
        activity.startActivityForResult(intent,requestCode);
    }

    /**
     * 跳转
     * @param fragment
     * @param requestCode
     */
    public void startActivity(Fragment fragment, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(fragment.getActivity(), ImageLoaderActivity.class);
        intent.putExtra(ImageLoaderActivity.EXTRA_SAVE_STATUS,sIsSavaStatus);
        intent.putExtra(ImageLoaderActivity.EXTRA_SELECT_MODE,sMode);
        intent.putExtra(ImageLoaderActivity.EXTRA_SELECT_COUNT,sMaxSelectCount);
        intent.putExtra(ImageLoaderActivity.EXTRA_ENTER_CLEAR_STATUS,sIsEnterClearStatus);
        fragment.startActivityForResult(intent,requestCode);
    }

}
