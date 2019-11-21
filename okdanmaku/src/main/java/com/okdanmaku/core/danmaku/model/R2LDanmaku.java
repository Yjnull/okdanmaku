package com.okdanmaku.core.danmaku.model;

/**
 * Created by yangya on 2019-11-19.
 */
public class R2LDanmaku extends DanmakuBase {

    private float x = 0;

    private float y = -1;

    public R2LDanmaku(long duration) {
        this.duration = duration;
    }

    @Override
    public void layout(IDisplayer displayer, float x, float y) {
        if (mTimer != null) {
            // duration = 4000 存活时间
            // time = 2000  出现时间
            // curM = mTimer.curMillisecond = 2000  当前时间
            // 应该在 2s 时出现，当前时间正好到了 2s，因此 x 坐标应该是 displayer 宽度
            // time = 2000  出现时间
            // curM = 4000  当前时间
            // 应该在 2s 时出现，当前时间正好到了 4s，可以存活 4s，现在才存活了 2s，所以正好是 displayer 宽度的一半
            // x 的坐标 = 时间比例 * (displayer 宽度 + 占位宽度) - 占位宽度
            // 为什么要 加上 paintWidth 再减 paintWidth？
            // 考虑时间比例为 0 时，x 的坐标应该是 -paintWidth 而不是 0，这样才能有飞出的效果

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

    @Override
    public boolean isShown() {
        if (mTimer != null) {
            // 弹幕出现时间 <= 当前时间 && 当前时间-弹幕出现时间 <= 弹幕存活时间
            // 也就是轮到 该弹幕出现并且在存活时间内即算作 shown
            return (time <= mTimer.curMillisecond) && (mTimer.curMillisecond - time <= duration);
        }
        return false;
    }
}
