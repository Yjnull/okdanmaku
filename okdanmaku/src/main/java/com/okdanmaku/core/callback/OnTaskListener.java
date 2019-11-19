package com.okdanmaku.core.callback;

import com.okdanmaku.core.danmaku.mdoel.BaseDanmaku;

/**
 * Created by yangya on 2019-10-16.
 * DrawTask 任务回调
 */
public interface OnTaskListener {
    void ready();

    void onDanmakuAdd(BaseDanmaku danmaku);

    void onDanmakuShown(BaseDanmaku danmaku);

    void onDanmakuConfigChanged();

    void onDanmakusDrawingFinished();
}
