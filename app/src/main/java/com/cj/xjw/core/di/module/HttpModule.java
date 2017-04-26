package com.cj.xjw.core.di.module;

import com.cj.xjw.BuildConfig;
import com.cj.xjw.base.ApiConstans;
import com.cj.xjw.base.Constants;
import com.cj.xjw.core.di.qualifier.MyUrl;
import com.cj.xjw.core.di.qualifier.ZhiHuUrl;
import com.cj.xjw.core.mvp.model.http.api.MyApi;
import com.cj.xjw.core.mvp.model.http.api.ZhiHuApi;
import com.cj.xjw.core.utils.Util;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by chenj on 2017/4/17.
 */
@Module
public class HttpModule {

    @Provides
    @Singleton
    OkHttpClient.Builder provideOkHttpBuilder() {
        return new OkHttpClient.Builder();
    }

    @Provides
    @Singleton
    Retrofit.Builder provideRetrofitBuilder() {
        return new Retrofit.Builder();
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(OkHttpClient.Builder builder) {
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            builder.addInterceptor(httpLoggingInterceptor);
        }
        File cacheFile = new File(Constants.NET_CACHE_PATH);
        Cache cache = new Cache(cacheFile,1024 * 1024 * 50);

        Interceptor cacheInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (!Util.isNetworkConnected()) {
                    request = request.newBuilder()
                            .cacheControl(CacheControl.FORCE_CACHE)
                            .build();
                }
                Response response = chain.proceed(request);
                if (Util.isNetworkConnected()) {
                    int maxAge = 0;
                    //有网络时，不缓存，最大保存时长为0
                    response.newBuilder()
                            .header("Cache-Control","public, max-age="+maxAge)
                            .removeHeader("Pragma")
                            .build();
                }else{
                    //无网络时，设置超时时间为4周
                    int maxStale = 60 * 60 * 24 * 28;
                    response.newBuilder()
                            .header("Cache-Control","public,only-if-cached,max-stale="+maxStale)
                            .removeHeader("Pragma")
                            .build();
                }
                return response;
            }

        };
        //设置缓存
        builder.addInterceptor(cacheInterceptor);
        builder.addNetworkInterceptor(cacheInterceptor);
        builder.cache(cache);
        //设置超时时间
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(20,TimeUnit.SECONDS);
        builder.writeTimeout(20,TimeUnit.SECONDS);
        //错误重连
        builder.retryOnConnectionFailure(true);
        return builder.build();

    }

    @MyUrl
    @Provides
    @Singleton
    Retrofit provideMyRetrofit(Retrofit.Builder builder, OkHttpClient client) {
        return createRetrofit(builder,client, ApiConstans.MY_BASE_URL);
    }

    @ZhiHuUrl
    @Provides
    @Singleton
    Retrofit provideZhiHuRetrofit(Retrofit.Builder builder, OkHttpClient client) {
        return createRetrofit(builder,client,ApiConstans.ZhiHu_BASE_URL);
    }


    private Retrofit createRetrofit(Retrofit.Builder builder, OkHttpClient client, String baseUrl) {
        return builder
                .baseUrl(baseUrl)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Singleton
    @Provides
    MyApi provideMyApi(@MyUrl Retrofit retrofit) {
        return retrofit.create(MyApi.class);
    }

    @Singleton
    @Provides
    ZhiHuApi provideZhiHuApi(@ZhiHuUrl Retrofit retrofit) {
        return retrofit.create(ZhiHuApi.class);
    }


}
