package com.cj.chenj.recyclerview_lib.refresh;

/**
 * Created by chenj on 2017/3/15.
 */

public interface Refreshable {
    /**
     * 是否可以下拉刷新
     * @return
     */
    //boolean canRefreshDown();

    /**
     * 是否可以上拉刷新
     * @return
     */
    boolean canRefreshUp();
}
