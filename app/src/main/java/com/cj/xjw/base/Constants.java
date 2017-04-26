package com.cj.xjw.base;

import java.io.File;
import java.lang.reflect.Field;

/**
 * Created by chenj on 2017/4/17.
 */

public class Constants {

    public static final int TYPE_NEWS = 1001;
    public static final int TYPE_ZHIHU = 1002;


    public static final String BASE_CACHE_PATH = App.getApplication().getCacheDir().getAbsolutePath();
    public static final String CACHE_DATA_PATH = BASE_CACHE_PATH + File.separator + "data";
    public static final String NET_CACHE_PATH = CACHE_DATA_PATH + File.separator + "NetCache";


    public static final String NEWS_ID = "news_id";
    public static final String NEWS_TYPE = "news_type";
    public static final String CHANNEL_POSITION = "channel_position";
}
