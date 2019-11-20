package com.okdanmaku.core.danmaku.parser;

import com.okdanmaku.core.danmaku.model.DanmakuBase;
import com.okdanmaku.core.danmaku.model.R2LDanmaku;

/**
 * Created by yangya on 2019-11-19.
 */
public class BiliDanmakuFactory {

    public static DanmakuBase createDanmaku(int type) {
        DanmakuBase instance = null;
        if (type == 1) {
            instance = new R2LDanmaku();
        }
        // TODO: more Danmaku type

        return instance;
    }

}
