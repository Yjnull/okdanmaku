package com.okdanmaku.core.callback;

import com.okdanmaku.core.danmaku.mdoel.BaseDanmaku;
import com.okdanmaku.core.danmaku.mdoel.DanmakuTimer;

/**
 * Created by yangya on 2019-10-14.
 */
public interface DrawHandlerCallback {

    void prepared();

    void updateTimer(DanmakuTimer timer);

    void danmakuShown(BaseDanmaku danmaku);

    void drawingFinished();

}
