package com.cj.chenj.recyclerview_lib.glide.glide;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

public class GlideRoundTransform extends BitmapTransformation {

    private static float radius = 0f;

    public GlideRoundTransform(Context context) {
        this(context, 4);
    }

    public GlideRoundTransform(Context context, int dp) {
        super(context);
        this.radius = Resources.getSystem().getDisplayMetrics().density * dp;
    }

    @Override protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return roundCrop(pool, toTransform);
    }

    private static Bitmap roundCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;
        
        int min = Math.min(source.getWidth(), source.getHeight());

        Bitmap result = pool.get(min, min, Bitmap.Config.ARGB_8888);
        //根据图片的不同分辨率，来计算切除的圆角
        radius = (Math.min(source.getWidth(), source.getHeight()) / 100f * 10f);
        
        if (result == null) {
            result = Bitmap.createBitmap(min,min, Bitmap.Config.ARGB_8888);
        }
        
        if(Math.abs(source.getWidth() - source.getHeight()) > 50){
        	source = Bitmap.createBitmap(source, (source.getWidth() - min)/2, (source.getHeight() - min)/2, min, min, null, false);
        }
        
        Canvas canvas = new Canvas(result);
        //canvas.translate(-(source.getWidth() - min) / 2, -(source.getHeight() - min) / 2);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        //RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
        //RectF rectF = new RectF((source.getWidth() - min)/2, (source.getHeight() - min)/2, min + (source.getWidth() - min)/2, min + (source.getHeight() - min)/2);
        RectF rectF = new RectF(0f, 0f, min, min);
        canvas.drawRoundRect(rectF, radius, radius, paint);
        return result;
    }

    @Override public String getId() {
        return getClass().getName() + Math.round(radius);
    }
}

