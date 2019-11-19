package com.okdanmaku.core.danmaku.renderer;

import com.okdanmaku.core.callback.OnDanmakuShownListener;
import com.okdanmaku.core.danmaku.mdoel.DanmakuConfig;
import com.okdanmaku.core.danmaku.mdoel.ICacheManager;
import com.okdanmaku.core.danmaku.mdoel.IDanmakuCollection;
import com.okdanmaku.core.danmaku.mdoel.IDisplayer;

/**
 * Created by yangya on 2019-10-16.
 * 弹幕渲染器
 */
public class DanmakuRenderer extends Renderer {

    private final DanmakuConfig mConfig;

    public DanmakuRenderer(DanmakuConfig config) {
        mConfig = config;
    }

    @Override
    public void draw(IDisplayer disp, IDanmakuCollection danmakus, long startRenderTime, RenderingState renderingState) {
        disp.draw(null);
    }

    @Override
    public void clear() {

    }

    @Override
    public void clearRetainer() {

    }

    @Override
    public void release() {

    }

    @Override
    public void setVerifierEnabled(boolean enabled) {

    }

    @Override
    public void setCacheManager(ICacheManager cacheManager) {

    }

    @Override
    public void setOnDanmakuShownListener(OnDanmakuShownListener listener) {

    }

    @Override
    public void removeOnDanmakuShownListener() {

    }

    @Override
    public void alignBottom(boolean enable) {

    }
}
