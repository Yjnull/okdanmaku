package com.okdanmaku.core.danmaku.renderer.android;

import com.okdanmaku.core.danmaku.model.DanmakuBase;
import com.okdanmaku.core.danmaku.model.IDanmakus;
import com.okdanmaku.core.danmaku.model.IDisplayer;
import com.okdanmaku.core.danmaku.model.android.Danmakus;
import com.okdanmaku.core.danmaku.renderer.Renderer;

/**
 * Created by yangya on 2019-11-20.
 */
public class DanmakuRenderer extends Renderer {

    @Override
    public void draw(IDisplayer disp, IDanmakus danmakus) {
        Danmakus drawItems = (Danmakus) danmakus;
        for (DanmakuBase drawItem : drawItems.items) {
            // measure
            if (!drawItem.isMeasured()) {
                drawItem.measure(disp);
            }
            // layout
            drawItem.layout(disp, 0, 100);
            // draw
            drawItem.draw(disp);
        }
    }
}
