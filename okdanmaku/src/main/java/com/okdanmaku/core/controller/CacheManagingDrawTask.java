package com.okdanmaku.core.controller;

import com.okdanmaku.core.callback.OnTaskListener;
import com.okdanmaku.core.danmaku.mdoel.DanmakuConfig;
import com.okdanmaku.core.danmaku.mdoel.DanmakuTimer;

/**
 * Created by yangya on 2019-10-16.
 */
public class CacheManagingDrawTask extends DrawTask {
    CacheManagingDrawTask(DanmakuTimer timer, DanmakuConfig config, OnTaskListener taskListener) {
        super(timer, config, taskListener);
    }
}
