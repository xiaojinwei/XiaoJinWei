package com.cj.xjw.core.component;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;

/**
 * Created by chenj on 2017/4/25.
 */

public class RxBus {

    private static RxBus mInstance;

    // 主题
    private final FlowableProcessor<Object> mBus;

    public RxBus() {
        // PublishSubject只会把在订阅发生的时间点之后来自原始Flowable的数据发射给观察者
        mBus = PublishProcessor.create().toSerialized();
    }

    public static RxBus getInstance() {
        if (mInstance == null) {
            synchronized (RxBus.class) {
                if (mInstance == null) {
                    mInstance = new RxBus();
                }
            }
        }
        return mInstance;
    }

    // 提供了一个新的事件
    public void post(Object o) {
        mBus.onNext(o);
    }

    // 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
    public <T> Flowable<T> toFlowable(Class<T> eventType) {
        return mBus.ofType(eventType);
    }

    // 封装默认订阅
    public <T> Disposable toDefalutFlowable(Class<T> eventType, Consumer<T> consumer) {
        return mBus.ofType(eventType).compose(RxUtil.<T>defalutSchedule()).subscribe(consumer);
    }
}
