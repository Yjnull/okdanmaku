package com.okdanmaku.core.danmaku.mdoel;

import android.graphics.Canvas;
import android.graphics.Typeface;

/**
 * Created by yangya on 2019-10-15.
 * TODO T, F 泛型暂不知道有啥用处(原本是 Canvas 和 TypeFace)，先留着
 */
public abstract class AbsDisplayer<T, F> implements IDisplayer {

    public abstract Canvas getExtraData();

    public abstract void setExtraData(Canvas data);

    @Override
    public boolean isHardwareAccelerated() {
        return false;
    }

    public abstract void drawDanmaku(BaseDanmaku danmaku, Canvas canvas, float left, float top, boolean fromWorkerThread);

    public abstract void clearTextHeightCache();

    public abstract void setTypeFace(Typeface font);

    public abstract void setFakeBoldText(boolean bold);

    public abstract void setTransparency(int newTransparency);

    public abstract void setScaleTextSizeFactor(float factor);

    public abstract void setCacheStuffer(BaseCacheStuffer cacheStuffer);

    public abstract BaseCacheStuffer getCacheStuffer();
}
