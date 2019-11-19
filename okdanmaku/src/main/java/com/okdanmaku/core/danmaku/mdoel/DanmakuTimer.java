package com.okdanmaku.core.danmaku.mdoel;

/**
 * 记录当前时间，和上次间隔时间
 */
public class DanmakuTimer {

    public long currMillisecond;  // 当前时间
    private long lastInterval;    // 间隔时间

    public DanmakuTimer() { }

    public DanmakuTimer(long curr) {
        update(curr);
    }

    public long add(long mills) {
        return update(currMillisecond + mills);
    }

    public long lastInterval() {
        return lastInterval;
    }

    public long update(long curr) {
        lastInterval = curr - currMillisecond;
        currMillisecond = curr;
        return lastInterval;
    }

}
