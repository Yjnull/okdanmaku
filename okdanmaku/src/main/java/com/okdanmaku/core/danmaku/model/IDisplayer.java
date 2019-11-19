package com.okdanmaku.core.danmaku.model;

/**
 * Created by yangya on 2019-11-19.
 */
public interface IDisplayer {

    int getWidth();

    int getHeight();

    void drawDanmaku(DanmakuBase danmaku);

}
