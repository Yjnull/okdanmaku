package com.okdanmaku.core.danmaku.renderer.android;

import com.okdanmaku.core.danmaku.model.BaseDanmaku;
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
        int index = 0;
        for (BaseDanmaku drawItem : drawItems.items) {
            // measure
            if (!drawItem.isMeasured()) {
                drawItem.measure(disp);
            }

            float topPos = 0;

            // layout
            if (drawItem.isShown()) {
                drawItem.layout(disp, 0, drawItem.index * 10 % disp.getHeight());
                // draw
                drawItem.draw(disp);
            }
        }
    }
}
