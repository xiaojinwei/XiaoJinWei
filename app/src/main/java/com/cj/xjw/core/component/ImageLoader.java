package com.cj.xjw.core.component;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by chenj on 2017/4/25.
 */

public class ImageLoader {

    public static void load(Context context, String url, ImageView imageView) {
        Glide.with(context).load(url).crossFade().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);
    }

}
