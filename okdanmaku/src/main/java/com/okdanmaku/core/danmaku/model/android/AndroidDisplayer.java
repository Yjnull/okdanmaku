package com.okdanmaku.core.danmaku.model.android;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.okdanmaku.core.danmaku.model.DanmakuBase;
import com.okdanmaku.core.danmaku.model.IDisplayer;

/**
 * Created by yangya on 2019-11-19.
 */
public class AndroidDisplayer implements IDisplayer {

    public static Paint sPaint;

    static {
        sPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sPaint.setColor(Color.RED);
        sPaint.setTextSize(50);
    }

    public Canvas mCanvas;
    public int width;
    public int height;

    private static Paint getPaint(DanmakuBase danmaku) {
        sPaint.setTextSize(danmaku.textSize * 2);
        sPaint.setColor(danmaku.textColor);
        return sPaint;
    }

    public void init(Canvas c) {
        mCanvas = c;
        if (c != null) {
            width = c.getWidth();
            height = c.getHeight();
        }
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void measure(DanmakuBase danmaku) {
        Paint paint = getPaint(danmaku);
        danmaku.paintWidth = paint.measureText(danmaku.text);
        danmaku.paintHeight = paint.getTextSize();
    }

    @Override
    public void draw(DanmakuBase danmaku) {
        if (mCanvas != null) {
            Paint paint = getPaint(danmaku);
            // paint.ascent = -46.38672 when paint.textSize = 50
            mCanvas.drawText(danmaku.text, danmaku.getLeft(), danmaku.getTop() - paint.ascent(), paint);
        }
    }

}
