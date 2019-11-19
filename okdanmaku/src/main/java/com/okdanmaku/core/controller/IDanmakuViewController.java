package com.okdanmaku.core.controller;

import android.content.Context;

/**
 * Created by yangya on 2019-10-15.
 */
public interface IDanmakuViewController {
    /** DanmakuView 经历 onLayout 即为准备好了**/
    boolean isViewReady();

    int getViewWidth();

    int getViewHeight();

    Context getContext();

    /**
     * 绘制 Danmaku，阻塞操作
     * @return 绘制时长，即 DanmakuView 的 onDraw 方法结束时长
     */
    long drawDanmakusSync();

    void clear();

    boolean isHardwareAccelerated();

    boolean isEnableDanmakuDrawingCache();
}
