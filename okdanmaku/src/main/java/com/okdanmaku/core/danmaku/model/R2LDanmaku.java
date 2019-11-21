package com.okdanmaku.core.danmaku.model;

/**
 * Created by yangya on 2019-11-19.
 */
public class R2LDanmaku extends DanmakuBase {

    private float x = 0;

    private float y = -1;

    public R2LDanmaku() {
        duration = 4000;
    }

    @Override
    public void layout(IDisplayer displayer, float x, float y) {
        if (mTimer != null) {
            // time 2000 curM 3000   5000
            //if (time <= mTimer.curMillisecond && mTimer.curMillisecond - time <= duration) {
                this.x = (1 - (mTimer.curMillisecond - time) / (float) duration)
                        * (displayer.getWidth() + paintWidth) - paintWidth;
                this.y = y;
            //}
        }
    }

    @Override
    public float getLeft() {
        return x;
    }

    @Override
    public float getTop() {
        return y;
    }

    @Override
    public float getRight() {
        return x + paintWidth;
    }

    @Override
    public float getBottom() {
        return y + paintHeight;
    }
}
