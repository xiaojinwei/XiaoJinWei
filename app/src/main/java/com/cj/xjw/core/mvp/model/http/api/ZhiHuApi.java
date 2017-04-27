package com.cj.xjw.core.mvp.model.http.api;

import com.cj.xjw.core.mvp.model.bean.DailyListBean;
import com.cj.xjw.core.mvp.model.bean.HotListBean;
import com.cj.xjw.core.mvp.model.bean.SectionListBean;
import com.cj.xjw.core.mvp.model.bean.ThemeListBean;

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

    /**
     * 主题日报
     * @return
     */
    @GET("themes")
    Flowable<ThemeListBean> getThemeList();

    /**
     * 专栏日报
     * @return
     */
    @GET("sections")
    Flowable<SectionListBean> getSectionList();

    /**
     * 热门日报
     * @return
     */
    @GET("news/hot")
    Flowable<HotListBean> getHotList();
}
