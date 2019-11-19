package com.okdanmaku.core.danmaku.renderer;

import com.okdanmaku.core.callback.OnDanmakuShownListener;
import com.okdanmaku.core.danmaku.mdoel.ICacheManager;
import com.okdanmaku.core.danmaku.mdoel.IDanmakuCollection;
import com.okdanmaku.core.danmaku.mdoel.IDisplayer;

/**
 * Created by yangya on 2019-10-14.
 * 渲染器
 */
public interface IRenderer {

    void draw(IDisplayer disp, IDanmakuCollection danmakus, long startRenderTime, RenderingState renderingState);

    void clear();

    void clearRetainer();

    void release();

    void setVerifierEnabled(boolean enabled);

    void setCacheManager(ICacheManager cacheManager);

    void setOnDanmakuShownListener(OnDanmakuShownListener listener);

    void removeOnDanmakuShownListener();

    void alignBottom(boolean enable);

}
