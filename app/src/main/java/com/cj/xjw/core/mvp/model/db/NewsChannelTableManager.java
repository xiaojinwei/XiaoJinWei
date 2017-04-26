package com.cj.xjw.core.mvp.model.db;

import com.cj.xjw.R;
import com.cj.xjw.base.ApiConstans;
import com.cj.xjw.base.App;
import com.cj.xjw.core.mvp.model.bean.NewsChannelTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by chenj on 2017/4/22.
 */

public class NewsChannelTableManager {

    public static List<NewsChannelTable> getNewsChannelTable() {
        List<String> newsChannelTableName = getNewsChannelTableName();
        List<String> newsChannelTableId = getNewsChannelTableId();
        List<NewsChannelTable> newsChannelTables = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            newsChannelTables.add(new NewsChannelTable(newsChannelTableName.get(i),newsChannelTableId.get(i),
                    ApiConstans.getType(newsChannelTableId.get(i)),i<=5,i,i==0));
        }
        return newsChannelTables;
    }

    public static List<String> getNewsChannelTableName() {
        String[] stringArray = App.getApplication().getResources().getStringArray(R.array.news_channel_name);
        return Arrays.asList(stringArray);
    }

    public static List<String> getNewsChannelTableId() {
        String[] intArray = App.getApplication().getResources().getStringArray(R.array.news_channel_id);
        return Arrays.asList(intArray);
    }
}
