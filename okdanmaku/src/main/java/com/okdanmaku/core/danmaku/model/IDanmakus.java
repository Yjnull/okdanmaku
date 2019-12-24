package com.okdanmaku.core.danmaku.model;

/**
 * Created by yangya on 2019-11-19.
 */
public interface IDanmakus {

    void addItem(BaseDanmaku item);

    void removeItem(BaseDanmaku item);

    IDanmakus sub(long startTime, long endTime);
}
