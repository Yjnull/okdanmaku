package com.okdanmaku.core.danmaku.loader.android;

import com.okdanmaku.core.danmaku.loader.ILoader;

/**
 * Created by yangya on 2019-11-20.
 */
public class DanmakuLoaderFactory {

    public static String TAG_BILI = "bili";

    public static ILoader create(String tag) {
        if (TAG_BILI.equalsIgnoreCase(tag)) {
            return BiliDanmakuLoader.instance();
        }
        return null;
    }

}
