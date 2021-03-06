package com.cj.xjw.core.mvp.model.http;

import com.cj.xjw.core.di.qualifier.MyKaKuApi;
import com.cj.xjw.core.mvp.model.bean.DailyListBean;
import com.cj.xjw.core.mvp.model.bean.HotListBean;
import com.cj.xjw.core.mvp.model.bean.NewsDetail;
import com.cj.xjw.core.mvp.model.bean.NewsSummary;
import com.cj.xjw.core.mvp.model.bean.SectionListBean;
import com.cj.xjw.core.mvp.model.bean.ThemeListBean;
import com.cj.xjw.core.mvp.model.http.api.MyApi;
import com.cj.xjw.core.mvp.model.http.api.ZhiHuApi;

import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * Created by chenj on 2017/4/17.
 */

public class RetrofitHelper {

    private MyApi mMyApi;
    private MyApi mMyKaKuApi;
    private ZhiHuApi mZhuHuApi;

    public RetrofitHelper(@com.cj.xjw.core.di.qualifier.MyApi MyApi myApi, @MyKaKuApi MyApi myKaKuApi, ZhiHuApi zhuHuApi) {
        mMyApi = myApi;
        mMyKaKuApi = myKaKuApi;
        mZhuHuApi = zhuHuApi;
    }

    public Flowable<Map<String,List<NewsSummary>>> getNewsList( String type,
                                                                String id,
                                                                 int startPage){
        return mMyApi.getNewsList(type,id,startPage);
    }

    public Observable<Map<String, NewsDetail>> getNewDetail(String postId) {
        return mMyApi.getNewDetail(postId);
    }

    public Observable<ResponseBody> getNewsBodyHtmlPhoto(String photoPath) {
        return mMyKaKuApi.getNewsBodyHtmlPhoto(photoPath);
    }

    public Flowable<DailyListBean> getDailyList() {
        return mZhuHuApi.getDailyList();
    }

    public Flowable<ThemeListBean> getThemeList() {
        return mZhuHuApi.getThemeList();
    }

    public Flowable<SectionListBean> getSectionList() {
        return mZhuHuApi.getSectionList();
    }

    public Flowable<HotListBean> getHotList(){
        return mZhuHuApi.getHotList();
    }
}
