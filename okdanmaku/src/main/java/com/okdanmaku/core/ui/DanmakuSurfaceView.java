package com.okdanmaku.core.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

/**
 * Created by yangya on 2019-11-19.
 */
public class DanmakuSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mSurfaceHolder;
    private Paint mPaint;
    private HandlerThread mDrawThread;
    private DrawHandler mHandler;
    private long mStartTime;

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

    private void drawTime() {
        drawCanvas(System.currentTimeMillis() + "ms");
    }

    void drawCanvas(String text) {
        Canvas canvas = mSurfaceHolder.lockCanvas();
        if (canvas != null) {
            e("cycle:" + (System.currentTimeMillis() - mStartTime));
            mStartTime = System.currentTimeMillis();
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            drawText(canvas, text);
            e("draw time:" + (System.currentTimeMillis() - mStartTime));
            mSurfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    void drawText(Canvas canvas, String text) {
        canvas.drawText(text, 50, 50, mPaint);
    }

    private void startDraw() {
        mDrawThread = new HandlerThread("Thread#draw");
        mDrawThread.start();
        mHandler = new DrawHandler(mDrawThread.getLooper());
        mHandler.sendEmptyMessage(DrawHandler.START);
    }

    private void quitDrawThread() {
        if (mHandler != null) {
            mHandler.quit();
            mHandler = null;
        }
        if (mDrawThread != null) {
            mDrawThread.quit();
            mDrawThread = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        d("surfaceCreated");
        startDraw();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        d("surfaceChanged = " + format + ", " + width + ", " + height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        quitDrawThread();
        return super.onTouchEvent(event);
    }

    private class DrawHandler extends Handler {
        private static final int START = 1;

        private static final int UPDATE = 2;

        private boolean isQuit;

        DrawHandler(@NonNull Looper looper) {
            super(looper);
        }

        void quit() {
            isQuit = true;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            int what = msg.what;
            switch (what) {
                case START:
                    isQuit = false;
                    sendEmptyMessage(UPDATE);
                    break;
                case UPDATE:
                    if (!isQuit) {
                        drawTime();
                        sendEmptyMessageDelayed(UPDATE, 500);
                    }
                    break;
            }
        }
    }

    private void d(String text) {
        Log.d("yjnull", Thread.currentThread().getName() + "-> " + text);
    }
    private void e(String text) {
        Log.e("yjnull", Thread.currentThread().getName() + " -> " + text);
    }

}
