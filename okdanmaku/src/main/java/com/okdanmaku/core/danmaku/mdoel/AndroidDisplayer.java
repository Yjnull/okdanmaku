package com.okdanmaku.core.danmaku.mdoel;

import android.graphics.Canvas;
import android.graphics.Typeface;

import com.okdanmaku.core.controller.DrawHelper;
import com.okdanmaku.core.utils.LogUtils;

/**
 * Created by yangya on 2019-10-16.
 */
public class AndroidDisplayer extends AbsDisplayer<Canvas, Typeface> {
    private Canvas canvas;

    @Override
    public Canvas getExtraData() {
        return canvas;
    }

    @Override
    public void setExtraData(Canvas data) {
        this.canvas = data;
    }

    @Override
    public void drawDanmaku(BaseDanmaku danmaku, Canvas canvas, float left, float top, boolean fromWorkerThread) {

    }

    @Override
    public void clearTextHeightCache() {

    }

    @Override
    public void setTypeFace(Typeface font) {

    }

    @Override
    public void setFakeBoldText(boolean bold) {

    }

    @Override
    public void setTransparency(int newTransparency) {

    }

    @Override
    public void setScaleTextSizeFactor(float factor) {

    }

    @Override
    public void setCacheStuffer(BaseCacheStuffer cacheStuffer) {

    }

    @Override
    public BaseCacheStuffer getCacheStuffer() {
        return null;
    }

    // --- IDisplayer ---------------------------------
    @Override
    public void prepare(BaseDanmaku danmaku, boolean fromWorkerThread) {

    }

    @Override
    public void measure(BaseDanmaku danmaku, boolean fromWorkerThread) {

    }

    @Override
    public int draw(BaseDanmaku danmaku) {
        LogUtils.e("AndroidDisplayer |draw = " + danmaku);
        DrawHelper.drawRect(getExtraData());
        return 0;
    }

    @Override
    public void recycle(BaseDanmaku danmaku) {

    }

    @Override
    public int getSlopPixel() {
        return 0;
    }

    @Override
    public float getDensity() {
        return 0;
    }

    @Override
    public int getDensityDpi() {
        return 0;
    }

    @Override
    public float getScaledDensity() {
        return 0;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getMargin() {
        return 0;
    }

    @Override
    public int getAllMarginTop() {
        return 0;
    }

    @Override
    public float getStrokeWidth() {
        return 0;
    }

    @Override
    public int getMaximumCacheWidth() {
        return 0;
    }

    @Override
    public int getMaximumCacheHeight() {
        return 0;
    }

    @Override
    public void resetSlopPixel(float factor) {

    }

    @Override
    public void setDensities(float density, int densityDpi, float scaledDensity) {

    }

    @Override
    public void setSize(int width, int height) {

    }

    @Override
    public void setDanmakuStyle(int style, float[] data) {

    }

    @Override
    public void setMargin(int margin) {

    }

    @Override
    public void setAllMarginTop(int marginTop) {

    }

    @Override
    public void setHardwareAccelerated(boolean enable) {

    }
}
