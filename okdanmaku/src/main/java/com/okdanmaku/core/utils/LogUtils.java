package com.okdanmaku.core.utils;

import android.util.Log;

/**
 * Created by yangya on 2019-10-17.
 */
public class LogUtils {
    public static void d(String str) {
        Log.d("yjnull", Thread.currentThread().getName() + ", " + str);
    }
    public static void e(String str) {
        Log.e("yjnull", Thread.currentThread().getName() + ", " + str);
    }
}
