package com.okdanmaku.core.controller;

import android.view.View;

import androidx.annotation.IntDef;

import com.okdanmaku.core.callback.DrawHandlerCallback;
import com.okdanmaku.core.callback.OnDanmakuClickListener;
import com.okdanmaku.core.danmaku.mdoel.BaseDanmaku;
import com.okdanmaku.core.danmaku.mdoel.DanmakuConfig;
import com.okdanmaku.core.danmaku.mdoel.IDanmakuCollection;
import com.okdanmaku.core.danmaku.parser.BaseDanmakuParser;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by yangya on 2019-10-14.
 * 描述一个弹幕 View
 */
public interface IDanmakuView {

    @IntDef({ThreadType.MAIN_THREAD, ThreadType.LOW_PRIORITY, ThreadType.NORMAL_PRIORITY, ThreadType.HIGH_PRIORITY})
    @Retention(RetentionPolicy.SOURCE)
    @interface ThreadType {
        int MAIN_THREAD = 1;
        int LOW_PRIORITY = 2;
        int NORMAL_PRIORITY = 3;
        int HIGH_PRIORITY = 4;
    }

    // --- Base ------------------------------------------------------------------------------------
    void prepare(BaseDanmakuParser parser, DanmakuConfig config);

    /** danmaku.isLive == true的情况下,请在非UI线程中使用此方法,避免可能卡住主线程 **/
    void addDanmaku(BaseDanmaku item);

    void invalidateDanmaku(BaseDanmaku item, boolean remeasure);

    void removeAllDanmakus(boolean isClearDanmakusOnScreen);

    void removeAllLiveDanmakus();

    IDanmakuCollection getCurVisibleDanmakuSet();

    void setCallback(DrawHandlerCallback callback);

    void setDrawingThreadType(@ThreadType int type);

    void enableDanmakuDrawingCache(boolean enable);

    /** for getting the accurate play-time. use this method intead of parser.getTimer().currMillisecond **/
    long getCurrentTime();

    DanmakuConfig getConfig();

    // --- 播放控制 ---------------------------------------------------------------------------------
    void start();

    void start(long position);

    void stop();

    void pause();

    void seekTo(long ms);

    void resume();

    void toggle();

    void show();

    void showAndResumeDrawTask(long position);

    void hide();

    long hideAndPauseDrawTask();

    void release();

    void clearDanmakusOnScreen();

    boolean isPrepared();

    boolean isPaused();

    // --- Android ---------------------------------------------------------------------------------
    View getView();

    int getWidth();

    int getHeight();

    void setVisibility(int visibility);

    boolean isShown();

    void showFPS(boolean show);

    // --- Click Listener ---
    void setOnDanmakuClickListener(OnDanmakuClickListener listener);

    void setOnDanmakuClickListener(OnDanmakuClickListener listener, float xOff, float yOff);

    public OnDanmakuClickListener getOnDanmakuClickListener();

    public float getXOff();

    public float getYOff();

    void forceRender();
}
