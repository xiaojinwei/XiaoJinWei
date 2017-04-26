package com.cj.xjw.core.component;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by chenj on 2017/4/22.
 */

public class RxUtil {

    /**
     * 统一线程处理
     * @param <T>
     * @return
     */
    public static <T> FlowableTransformer<T,T> defalutSchedule() {//compose简化线程
        return new FlowableTransformer<T, T>() {
            @Override
            public Flowable<T> apply(@NonNull Flowable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io());
            }
        };
    }
}
