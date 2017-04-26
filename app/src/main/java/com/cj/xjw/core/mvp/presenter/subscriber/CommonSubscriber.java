package com.cj.xjw.core.mvp.presenter.subscriber;

import android.text.TextUtils;

import com.cj.xjw.core.mvp.model.http.exception.ApiException;
import com.cj.xjw.core.mvp.view.BaseView;

import io.reactivex.subscribers.ResourceSubscriber;
import retrofit2.HttpException;

/**
 * Created by chenj on 2017/4/26.
 */

public abstract class CommonSubscriber<T> extends ResourceSubscriber<T> {

    private BaseView mBaseView;

    private String mErrorMsg;

    public CommonSubscriber(BaseView baseView) {
        mBaseView = baseView;
    }

    public CommonSubscriber(BaseView baseView, String errorMsg) {
        mBaseView = baseView;
        mErrorMsg = errorMsg;
    }

    @Override
    public void onError(Throwable t) {
        if (mBaseView == null)
            return;
        if (mErrorMsg != null && !TextUtils.isEmpty(mErrorMsg)) {
            mBaseView.showMsg(mErrorMsg);
        } else if (t instanceof ApiException) {
            mBaseView.showMsg(t.toString());
        } else if (t instanceof HttpException) {
            mBaseView.showMsg("数据加载失败");
        } else {
            mBaseView.showMsg("未知错误");

        }
        mBaseView.hideProgress();
    }

    @Override
    public void onComplete() {
        if (mBaseView != null) {
            mBaseView.hideProgress();
        }
    }
}
