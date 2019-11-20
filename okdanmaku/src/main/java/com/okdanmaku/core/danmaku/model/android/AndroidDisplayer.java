package com.okdanmaku.core.danmaku.model.android;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.okdanmaku.core.danmaku.model.DanmakuBase;
import com.okdanmaku.core.danmaku.model.IDisplayer;

/**
 * Created by yangya on 2019-11-19.
 */
public class AndroidDisplayer implements IDisplayer {

    private static Paint sPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private static Paint getPaint(DanmakuBase danmaku) {
        sPaint.setTextSize(danmaku.textSize);
        sPaint.setColor(danmaku.textColor);
        return sPaint;
    }

    public Canvas mCanvas;

    public AndroidDisplayer() {
        this(null);
    }

    public AndroidDisplayer(Canvas canvas) {
        mCanvas = canvas;
    }

    @Override
    public int getWidth() {
        if (mCanvas != null) {
            return mCanvas.getWidth();
        }
        return 0;
    }

    @Override
    public int getHeight() {
        if (mCanvas != null) {
            return mCanvas.getHeight();
        }
        return 0;
    }

    @Override
    public void draw(DanmakuBase danmaku) {
        if (mCanvas != null) {
            Paint paint = getPaint(danmaku);
            mCanvas.drawText(danmaku.text, danmaku.getLeft(), danmaku.getTop() - paint.ascent(), paint);
        }
    }

    @Override
    public void measure(DanmakuBase danmaku) {
        Paint paint = getPaint(danmaku);
        danmaku.paintWidth = paint.measureText(danmaku.text);
        danmaku.paintHeight = paint.getTextSize();
    }

}
