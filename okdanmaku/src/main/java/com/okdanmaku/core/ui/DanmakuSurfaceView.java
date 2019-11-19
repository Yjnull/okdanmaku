package com.okdanmaku.core.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by yangya on 2019-11-19.
 */
public class DanmakuSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mSurfaceHolder;
    private Paint mPaint;

    public DanmakuSurfaceView(Context context) {
        super(context);
        init();
    }

    public DanmakuSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DanmakuSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setTextSize(50);
        setZOrderOnTop(true);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
    }

    void drawText(Canvas canvas) {
        canvas.drawText("hello", 50, 50, mPaint);
    }

    void drawCanvas() {
        Canvas canvas = mSurfaceHolder.lockCanvas();
        if (canvas != null) {
            drawText(canvas);
            mSurfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        drawCanvas();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        drawCanvas();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
