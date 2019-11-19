package com.okdanmaku.core.utils;

/**
 * Created by yangya on 2019-10-15.
 */
public class SystemClock {

    public static long uptimeMillis() {
        // 返回自启动以来的毫秒数，包括睡眠时间。
        return android.os.SystemClock.elapsedRealtime();
    }

    public static void sleep(long mills) {
        android.os.SystemClock.sleep(mills);
    }

}
