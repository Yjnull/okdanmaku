package com.okdanmaku.core.danmaku.model;

/**
 * Created by yangya on 2019-11-19.
 */
public abstract class DanmakuBase {

    /**
     * 索引/编号
     */
    public int index;

    /**
     * 文本
     */
    public String text;

    /**
     * 文本颜色
     */
    public int textColor;

    /**
     * 阴影/描边颜色
     */
    public int textShadowColor;

    /**
     * 字体大小
     */
    public float textSize = -1;

    /**
     * 占位宽度
     */
    public float paintWidth = -1;

    /**
     * 占位高度
     */
    public float paintHeight = -1;

    /**
     * 出现时间(毫秒)
     */
    public long time;

    /**
     * 存活时间(毫秒)
     */
    public long duration;

    /**
     * 计时
     */
    protected DanmakuTimer mTimer;

    public void setTime(long time) {
        this.time = time;
    }

    public void setTimer(DanmakuTimer timer) {
        mTimer = timer;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void draw(IDisplayer displayer) {
        displayer.draw(this);
    }

    public boolean isMeasured() {
        return paintWidth >= 0 && paintHeight >= 0;
    }

    public void measure(IDisplayer displayer) {
        displayer.measure(this);
    }

    public abstract void layout(IDisplayer displayer, float x, float y);

    public abstract float getLeft();

    public abstract float getTop();

    public abstract float getRight();

    public abstract float getBottom();

    public abstract boolean isShown();

}
