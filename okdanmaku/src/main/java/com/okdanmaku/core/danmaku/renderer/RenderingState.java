package com.okdanmaku.core.danmaku.renderer;

/**
 * Created by yangya on 2019-10-15.
 */
public class RenderingState {
    public long cacheHitCount;
    public long cacheMissCount;
    public long consumingTime;
    public long endTime;
    public boolean nothingRendered;
    public boolean isRunningDanmakus;

    public void reset() {

    }

    public void set(RenderingState other) {
        if (other == null) {
            return;
        }

    }
}
