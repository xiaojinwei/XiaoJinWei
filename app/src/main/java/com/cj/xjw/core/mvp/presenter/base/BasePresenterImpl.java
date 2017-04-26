package com.cj.xjw.core.mvp.presenter.base;

import android.support.annotation.NonNull;

import com.cj.xjw.core.mvp.presenter.base.BasePresenter;
import com.cj.xjw.core.mvp.view.BaseView;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by chenj on 2017/4/11.
 */

public class BasePresenterImpl<T extends BaseView> implements BasePresenter<T> {

    protected T mView;
    protected CompositeDisposable mDisposable;

    protected void unSubscribe() {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    public void addSubscribe(Disposable disposable) {
        if(disposable == null) return;
        if (mDisposable == null) {
            mDisposable = new CompositeDisposable();
        }
        mDisposable.add(disposable);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void attachView(@NonNull T view) {
        this.mView = view;
        this.mView.showProgress();
    }

    @Override
    public void onDestroy() {
        this.mView = null;
        unSubscribe();
    }
}
