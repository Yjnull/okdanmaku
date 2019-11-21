package com.okdanmaku.core.danmaku.model;

/**
 * Created by yangya on 2019-11-19.
 */
public class Danmaku extends DanmakuBase {

    public Danmaku(String text) {
        this.text = text;
    }

    @Override
    public void layout(IDisplayer displayer, float x, float y) {

    }

    @Override
    public float getLeft() {
        return 0;
    }

    @Override
    public float getTop() {
        return 0;
    }

    @Override
    public float getRight() {
        return 0;
    }

    @Override
    public float getBottom() {
        return 0;
    }

    @Override
    public boolean isShown() {
        return false;
    }
}
