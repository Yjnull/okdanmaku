package com.okdanmaku.core.danmaku.controller;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;

/**
 * Created by yangya on 2019-11-21.
 */
public class DrawHelper {

    public static final Paint sPaint;

    static {
        sPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sPaint.setColor(Color.RED);
        sPaint.setTextSize(50);
    }

    public static void drawText(Canvas canvas, String text) {
        canvas.drawText(text, 50, 50, sPaint);
    }

    public static void drawDuration(Canvas canvas, String text) {
        canvas.drawText(text, 100, 100, sPaint);
    }

    public static void drawCircle(Canvas canvas, float cx, float cy) {
        canvas.drawCircle(cx, cy, 50, sPaint);
    }

    public static void clearCanvas(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }

}
