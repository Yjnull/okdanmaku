package com.okdanmaku.core.danmaku.model;

/**
 * Created by yangya on 2019-11-19.
 */
public abstract class BaseDanmaku {
    public final static int TYPE_SCROLL_RL = 1;
    public final static int TYPE_SCROLL_LR = 2;
    public final static int TYPE_FIX_TOP = 3;
    public final static int TYPE_FIX_BOTTOM = 4;
    public final static int TYPE_MOVEABLE_XXX = 0;  //TODO: add more type

    public final static int INVISIBLE = 0;
    public final static int VISIBLE = 1;

    public int index;               // 索引/编号
    public String text;             // 文本
    public int textColor;           // 文本颜色
    public int textShadowColor;     // 阴影/描边颜色
    public int visibility;          // 是否可见
    public float textSize = -1;     // 字体大小
    public float paintWidth = -1;   // 占位宽度
    public float paintHeight = -1;  // 占位高度
    public long time;               // 出现时间(毫秒)
    public long duration;           // 存活时间(毫秒)
    protected DanmakuTimer mTimer;  // 计时

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

    public boolean isShown() {
        return this.visibility == VISIBLE;
    }

    public boolean isOutside() {
        if (mTimer != null) {
            // 弹幕出现时间 > 当前时间 || 当前时间-弹幕出现时间 > 弹幕存活时间
            // 也就是说该弹幕应该在屏幕外了
            return time > mTimer.curMillisecond || mTimer.curMillisecond - time > duration;
        }
        return true;
    }

    public void setVisibility(boolean b) {
        this.visibility = (b ? VISIBLE : INVISIBLE);
    }

    public abstract void layout(IDisplayer displayer, float x, float y);

    public abstract float[] getRectAtTime(IDisplayer displayer, long time);

    public abstract float getLeft();

    public abstract float getTop();

    public abstract float getRight();

    public abstract float getBottom();

    /**
     * return the type of Danmaku
     *
     * @return TYPE_SCROLL_RL = 0
     * TYPE_SCROLL_RL = 1
     * TYPE_SCROLL_LR = 2
     * TYPE_FIX_TOP = 3;
     * TYPE_FIX_BOTTOM = 4;
     */
    public abstract int getType();

}
