package com.okdanmaku.core.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.okdanmaku.core.danmaku.controller.DrawHelper;
import com.okdanmaku.core.danmaku.controller.DrawTask;
import com.okdanmaku.core.danmaku.model.DanmakuTimer;
import com.okdanmaku.core.danmaku.renderer.android.DanmakuRenderer;
import com.okdanmaku.core.utils.LogUtils;

/**
 * Created by yangya on 2019-11-19.
 */
public class DanmakuSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mSurfaceHolder;
    private HandlerThread mDrawThread;
    private DrawHandler mHandler;
    private long mStartTime;
    private float cx, cy;
    private long avgDuration;
    private long maxDuration;
    private DanmakuTimer mTimer;
    private DanmakuRenderer mRenderer;
    private DrawTask mDrawTask;

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
        setZOrderOnTop(true);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        if (mTimer == null) {
            mTimer = new DanmakuTimer();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtils.d("surfaceCreated");
        startDraw();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        LogUtils.d("surfaceChanged = " + format + ", " + width + ", " + height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    protected void onDetachedFromWindow() {
        LogUtils.e("surfaceDestroyed");
        quitDrawThread();
        super.onDetachedFromWindow();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        quitDrawThread();
        return true;
    }

    private void updateTimer() {
        mTimer.curMillisecond += 100;
    }

    private void updateCxCy(float x, float y) {
        cx = x;
        cy = y;
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

    /**
     * 工作在 Thread#draw 线程
     */
    void drawSomething() {
        Canvas canvas = mSurfaceHolder.lockCanvas();
        if (canvas != null) {
            DrawHelper.clearCanvas(canvas);
            DrawHelper.drawDuration(canvas, "sssss");
            if (mDrawTask == null) {
                mDrawTask = new DrawTask(mTimer, getContext(), canvas.getWidth(), canvas.getHeight());
            }
            mDrawTask.draw(canvas);

            DrawHelper.drawCircle(canvas, 100, 100);
            mSurfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private class DrawHandler extends Handler {
        private static final int START = 1;

        private static final int UPDATE = 2;

        private boolean isQuit;

        DrawHandler(@NonNull Looper looper) {
            super(looper);
        }

        void quit() {
            removeCallbacksAndMessages(null);
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
                        drawSomething();
                        updateTimer();
                        sendEmptyMessageDelayed(UPDATE, 16);
                    }
                    break;
            }
        }
    }

}
