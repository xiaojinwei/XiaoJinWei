package com.cj.xjw.core.component;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

/**
 * 启动服务，在子线程中做初始化工作
 * Created by chenj on 2017/4/20.
 */

public class InitializeService extends IntentService {

    private static final String ACTION_INIT = "initApplication";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * name Used to name the worker thread, important only for debugging.
     */
    public InitializeService() {
        super("InitializeService");
    }

    public static void start(Context context) {
        Intent intent = new Intent(context,InitializeService.class);
        intent.setAction(ACTION_INIT);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            if (TextUtils.equals(intent.getAction(), ACTION_INIT)) {
                initApplication();
            }
        }
    }

    /**
     * 做一些全局的初始化操作
     */
    private void initApplication() {

    }
}
