package com.okdanmaku.core.danmaku.renderer;

import com.okdanmaku.core.danmaku.model.IDanmakus;
import com.okdanmaku.core.danmaku.model.IDisplayer;

/**
 * Created by yangya on 2019-11-20.
 */
public interface IRenderer {

    void draw(IDisplayer disp, IDanmakus danmakus);

}
