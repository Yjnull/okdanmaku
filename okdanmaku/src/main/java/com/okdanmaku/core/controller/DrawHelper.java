package com.okdanmaku.core.controller;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

/**
 * Created by yangya on 2019-10-14.
 */
public class DrawHelper {
    private static Paint sPaint, sPaintFPS;

    private static RectF sRectF;

    private static boolean USE_DRAWCOLOR_TO_CLEAR_CANVAS = true;

    private static boolean USE_DRAWCOLOR_MODE_CLEAR = true;

    static {
        sPaint = new Paint();
        sPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        sPaint.setColor(Color.TRANSPARENT);
        sRectF = new RectF();
    }

    public static void useDrawColorToClearCanvas(boolean use, boolean useClearMode) {
        USE_DRAWCOLOR_TO_CLEAR_CANVAS = use;
        USE_DRAWCOLOR_MODE_CLEAR = useClearMode;
    }

    public static void clearCanvas(Canvas canvas) {
        if (USE_DRAWCOLOR_TO_CLEAR_CANVAS) {
            if (USE_DRAWCOLOR_MODE_CLEAR) {
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            } else {
                canvas.drawColor(Color.TRANSPARENT);
            }
        } else {
            sRectF.set(0, 0, canvas.getWidth(), canvas.getHeight());
            if (sRectF.width() > 0 && sRectF.height() > 0) canvas.drawRect(sRectF, sPaint);
        }
    }

    public static void drawFPS(Canvas canvas, String fps) {
        if (sPaintFPS == null) {
            sPaintFPS = new Paint();
            sPaintFPS.setColor(Color.RED);
            sPaintFPS.setTextSize(30);
        }
        int top = canvas.getHeight() - 50;
        canvas.drawText(fps, 10, top, sPaintFPS);
    }

    private static int left = 10, top = 10;
    public static void drawRect(Canvas canvas) {
        if (sPaintFPS == null) {
            sPaintFPS = new Paint();
            sPaintFPS.setColor(Color.RED);
            sPaintFPS.setTextSize(30);
        }
        canvas.drawRect(left, top, left + 60, top + 60, sPaintFPS);
        canvas.drawText("哈哈", left, top, sPaintFPS);
        left+=10;
        top+=10;
        if (left == canvas.getWidth()) {
            left = 10;
        }
        if (top == canvas.getHeight()) {
            top = 10;
        }
    }
}
