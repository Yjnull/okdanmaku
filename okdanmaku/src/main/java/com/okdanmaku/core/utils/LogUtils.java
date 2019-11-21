package com.okdanmaku.core.utils;

import android.util.Log;

/**
 * Created by yangya on 2019-11-21.
 */
public class LogUtils {
    public static void d(String text) {
        Log.d("yjnull", Thread.currentThread().getName() + "-> " + text);
    }

    public static void e(String text) {
        Log.e("yjnull", Thread.currentThread().getName() + " -> " + text);
    }
}
