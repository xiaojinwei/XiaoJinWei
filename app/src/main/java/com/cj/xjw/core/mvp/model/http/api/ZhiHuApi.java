package com.cj.xjw.core.mvp.model.http.api;

import com.cj.xjw.core.mvp.model.bean.DailyListBean;

import io.reactivex.Flowable;
import retrofit2.http.GET;

/**
 * Created by chenj on 2017/4/24.
 */

public interface ZhiHuApi {

    /**
     * 最新日报
     * @return
     */
    @GET("news/latest")
    Flowable<DailyListBean> getDailyList();

}
