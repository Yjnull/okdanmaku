package com.okdanmaku.core.danmaku.mdoel;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by yangya on 2019-10-15.
 */
public interface IDisplayer {
    @IntDef({Style.DEFAULT, Style.NONE, Style.SHADOW, Style.STROKE, Style.PROJECTION})
    @Retention(RetentionPolicy.SOURCE)
    @interface Style {
        int DEFAULT = -1;    // 自动
        int NONE = 0;        // 无
        int SHADOW = 1;      // 阴影
        int STROKE = 2;      // 描边
        int PROJECTION = 3;  // 投影
    }

    void prepare(BaseDanmaku danmaku, boolean fromWorkerThread);

    void measure(BaseDanmaku danmaku, boolean fromWorkerThread);

    int draw(BaseDanmaku danmaku);

    void recycle(BaseDanmaku danmaku);

    // --- getter ----------------------------------------------------------------------------------
    int getSlopPixel();

    float getDensity();

    int getDensityDpi();

    float getScaledDensity();

    int getWidth();

    int getHeight();

    int getMargin();

    int getAllMarginTop();

    float getStrokeWidth();

    int getMaximumCacheWidth();

    int getMaximumCacheHeight();

    boolean isHardwareAccelerated();

    // -- setter -----------------------------------------------------------------------------------
    void resetSlopPixel(float factor);

    void setDensities(float density, int densityDpi, float scaledDensity);

    void setSize(int width, int height);

    void setDanmakuStyle(int style, float[] data);

    void setMargin(int margin);

    void setAllMarginTop(int marginTop);

    void setHardwareAccelerated(boolean enable);

}
