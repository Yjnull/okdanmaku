package com.okdanmaku.core.controller;

import androidx.annotation.IntDef;

import com.okdanmaku.core.danmaku.mdoel.AbsDisplayer;
import com.okdanmaku.core.danmaku.mdoel.BaseDanmaku;
import com.okdanmaku.core.danmaku.mdoel.IDanmakuCollection;
import com.okdanmaku.core.danmaku.parser.BaseDanmakuParser;
import com.okdanmaku.core.danmaku.renderer.RenderingState;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by yangya on 2019-10-15.
 */
public interface IDrawTask {
    @IntDef({PlayState.PLAYING, PlayState.PAUSE})
    @Retention(RetentionPolicy.SOURCE)
    @interface PlayState {
        int PLAYING = 1;
        int PAUSE = 2;
    }

    void setParser(BaseDanmakuParser parser);

    void addDanmaku(BaseDanmaku item);

    void removeAllDanmakus(boolean isClearDanmakusOnScreen);

    void removeAllLiveDanmakus();

    void clearDanmakusOnScreen(long currMillis);

    IDanmakuCollection getVisibleDanmakusOnTime(long currentTime);

    RenderingState draw(AbsDisplayer displayer);

    void prepare();

    void reset();

    void seek(long mills);

    void start();

    void quit();

    void onPlayStateChanged(@PlayState int state);

    void requestSync(long fromTimeMills, long toTimeMills, long offsetMills);

    void requestClear();

    void requestClearRetainer();

    void requestHide();

    void requestRender();

    void invalidateDanmaku(BaseDanmaku item, boolean remeasure);

}
