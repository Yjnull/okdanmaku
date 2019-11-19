package com.okdanmaku.core.callback;

import com.okdanmaku.core.controller.IDanmakuView;
import com.okdanmaku.core.danmaku.mdoel.BaseDanmaku;

/**
 * Created by yangya on 2019-10-14.
 */
public interface OnDanmakuClickListener {
    /**
     * @param danmaku 能被点击的弹幕，可能为空;
     * @return true if the event was handled, false otherwise.
     */
    boolean onDanmakuClick(BaseDanmaku danmaku);

    boolean onDanmakuLongClick(BaseDanmaku danmaku);

    boolean onViewClick(IDanmakuView danmakuView);
}
