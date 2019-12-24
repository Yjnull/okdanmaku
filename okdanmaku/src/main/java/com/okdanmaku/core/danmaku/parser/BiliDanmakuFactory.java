package com.okdanmaku.core.danmaku.parser;

import com.okdanmaku.core.danmaku.model.BaseDanmaku;
import com.okdanmaku.core.danmaku.model.R2LDanmaku;

/**
 * Created by yangya on 2019-11-19.
 */
public class BiliDanmakuFactory {

    public static float BILI_PLAYER_WIDTH = 539;

    public static float BILI_PLAYER_HEIGHT = 385;

    public static long COMMON_DANMAKU_DURATION = 4000;

    public static long REAL_DANMAKU_DURATION = -1;

    public static BaseDanmaku createDanmaku(int type, float dispWidth) {
        BaseDanmaku instance = null;
        if (type == 1) {
            instance = new R2LDanmaku(calDuration(dispWidth));
        }
        // TODO: more Danmaku type

        return instance;
    }

    public static void updateDanmakuDuration(BaseDanmaku danmaku, float dispWidth) {
        danmaku.duration = calDuration(dispWidth);
    }

    public static long calDuration(float dispWidth) {
        if (REAL_DANMAKU_DURATION == -1) {
            REAL_DANMAKU_DURATION = (long) (COMMON_DANMAKU_DURATION * (dispWidth / BILI_PLAYER_WIDTH));
        }
        return REAL_DANMAKU_DURATION;
    }
}
