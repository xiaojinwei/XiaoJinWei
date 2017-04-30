package com.cj.xjw.core.mvp.model.http.api;

import com.cj.xjw.core.mvp.model.bean.NewsDetail;
import com.cj.xjw.core.mvp.model.bean.NewsSummary;

import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

import static android.R.attr.id;

/**
 * Created by chenj on 2017/4/20.
 */

public interface MyApi {


    @GET("nc/article/{type}/{id}/{startPage}-20.html")
    Flowable<Map<String,List<NewsSummary>>> getNewsList(
            @Path("type") String type,
            @Path("id") String id,
            @Path("startPage") int startPage
    );

    @GET("nc/article/{postId}/full.html")
    Observable<Map<String, NewsDetail>> getNewDetail(
            @Path("postId") String postId);

    @GET
    Observable<ResponseBody> getNewsBodyHtmlPhoto(
            @Url String photoPath);
}
