package com.cj.xjw.core.widget;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.widget.TextView;


import com.cj.xjw.R;
import com.cj.xjw.base.App;
import com.cj.xjw.core.component.RxUtil;
import com.cj.xjw.core.di.component.DaggerURLImageComponent;
import com.cj.xjw.core.mvp.model.http.RetrofitHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;


/**
 * Created by Eric on 2017/1/18.
 */

public class URLImageGetter implements Html.ImageGetter {
    private TextView mTextView;
    private int mPicWidth;
    private String mNewsBody;
    private int mPicCount;
    private int mPicTotal;
    private static final String mFilePath = App.getApplication().getCacheDir().getAbsolutePath();

    public Disposable mDisposable;

    @Inject
    RetrofitHelper mRetrofitHelper ;

    public URLImageGetter(TextView textView, String newsBody, int picTotal) {
        mTextView = textView;
        mPicWidth = mTextView.getWidth();
        mNewsBody = newsBody;
        mPicTotal = picTotal;
        component();

    }

    private void component() {
        DaggerURLImageComponent.builder()
                .appComponent(App.getApplication().getAppComponent())
                .build().inject(this);
    }

    @Override
    public Drawable getDrawable(final String source) {
        Drawable drawable;
        File file = new File(mFilePath, source.hashCode() + "");
        if (file.exists()) {
            mPicCount++;
            drawable = getDrawableFromDisk(file);
        } else {
            drawable = getDrawableFromNet(source);
        }
        return drawable;
    }

    @Nullable
    private Drawable getDrawableFromDisk(File file) {
        Drawable drawable = Drawable.createFromPath(file.getAbsolutePath());
        if (drawable != null) {
            int picHeight = calculatePicHeight(drawable);
            drawable.setBounds(0, 0, mPicWidth, picHeight);
        }
        return drawable;
    }

    private int calculatePicHeight(Drawable drawable) {
        float imgWidth = drawable.getIntrinsicWidth();
        float imgHeight = drawable.getIntrinsicHeight();
        float rate = imgHeight / imgWidth;
        return (int) (mPicWidth * rate);
    }

    @NonNull
    private Drawable getDrawableFromNet(final String source) {
        mRetrofitHelper.getNewsBodyHtmlPhoto(source)
                .compose(RxUtil.<ResponseBody>defalutObservableSchedule())
                .map(new Function<ResponseBody, Boolean>() {
                    @Override
                    public Boolean apply(@io.reactivex.annotations.NonNull ResponseBody responseBody) throws Exception {
                        return WritePicToDisk(responseBody, source);
                    }
                }).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
                mDisposable = d;
            }

            @Override
            public void onNext(Boolean aBoolean) {
                mPicCount++;
                if (aBoolean && (mPicCount == mPicTotal - 1)) {
                    mTextView.setText(Html.fromHtml(mNewsBody, URLImageGetter.this, null));
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        return createPicPlaceholder();
    }

    @NonNull
    private Boolean WritePicToDisk(ResponseBody response, String source) {
        File file = new File(mFilePath, source.hashCode() + "");
        InputStream in = null;
        FileOutputStream out = null;
        try {
            in = response.byteStream();
            out = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("deprecation")
    @NonNull
    private Drawable createPicPlaceholder() {
        Drawable drawable;
        //int color = MyUtils.getColor(R.color.image_place_holder, R.color.image_place_holder_night);
        drawable = new ColorDrawable(App.getApplication().getResources().getColor(R.color.image_place_holder));
        drawable.setBounds(0, 0, mPicWidth, mPicWidth / 3);
        return drawable;
    }

}
