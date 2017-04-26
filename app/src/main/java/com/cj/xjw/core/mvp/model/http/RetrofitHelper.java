package com.cj.xjw.core.mvp.model.http;

import com.cj.xjw.core.mvp.model.bean.DailyListBean;
import com.cj.xjw.core.mvp.model.bean.NewsSummary;
import com.cj.xjw.core.mvp.model.bean.ThemeListBean;
import com.cj.xjw.core.mvp.model.http.api.MyApi;
import com.cj.xjw.core.mvp.model.http.api.ZhiHuApi;

import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;

/**
 * Created by chenj on 2017/4/17.
 */

public class RetrofitHelper {

    private MyApi mMyApi;
    private ZhiHuApi mZhuHuApi;

    public RetrofitHelper(MyApi myApi,ZhiHuApi zhuHuApi) {
        mMyApi = myApi;
        mZhuHuApi = zhuHuApi;
    }

    public Flowable<Map<String,List<NewsSummary>>> getNewsList( String type,
                                                                String id,
                                                                 int startPage){
        return mMyApi.getNewsList(type,id,startPage);
    }

    public Flowable<DailyListBean> getDailyList() {
        return mZhuHuApi.getDailyList();
    }

    public Flowable<ThemeListBean> getThemeList() {
        return mZhuHuApi.getThemeList();
    }
}
