package com.cj.xjw.base;

/**
 * Created by chenj on 2017/4/17.
 */

public class ApiConstans {

    public static final String MY_BASE_URL = "http://c.m.163.com/";//网易新闻
    public static final String MY_PIC_BASE_URL = "http://kaku.com/";//网易新闻
    public static final String ZhiHu_BASE_URL = "http://news-at.zhihu.com/api/4/";//知乎





    // 头条TYPE
    public static final String HEADLINE_TYPE = "headline";
    // 房产TYPE
    public static final String HOUSE_TYPE = "house";
    // 其他TYPE
    public static final String OTHER_TYPE = "list";


    // 头条id
    public static final String HEADLINE_ID = "T1348647909107";
    // 房产id
    public static final String HOUSE_ID = "5YyX5Lqs";

    /**
     * 新闻id获取类型
     *
     * @param id 新闻id
     * @return 新闻类型
     */
    public static String getType(String id) {
        switch (id) {
            case HEADLINE_ID:
                return HEADLINE_TYPE;
            case HOUSE_ID:
                return HOUSE_TYPE;
            default:
                break;
        }
        return OTHER_TYPE;
    }
}
