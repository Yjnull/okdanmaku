package com.okdanmaku.core.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.okdanmaku.core.callback.DrawHandlerCallback;
import com.okdanmaku.core.callback.OnDanmakuClickListener;
import com.okdanmaku.core.controller.DrawHandler;
import com.okdanmaku.core.controller.DrawHelper;
import com.okdanmaku.core.controller.IDanmakuView;
import com.okdanmaku.core.controller.IDanmakuViewController;
import com.okdanmaku.core.danmaku.mdoel.BaseDanmaku;
import com.okdanmaku.core.danmaku.mdoel.DanmakuConfig;
import com.okdanmaku.core.danmaku.mdoel.IDanmakuCollection;
import com.okdanmaku.core.danmaku.parser.BaseDanmakuParser;
import com.okdanmaku.core.danmaku.renderer.RenderingState;
import com.okdanmaku.core.utils.LogUtils;
import com.okdanmaku.core.utils.SystemClock;

import java.util.Formatter;
import java.util.LinkedList;
import java.util.Locale;

/**
 * Created by yangya on 2019-10-14.
 */

public class DanmakuView extends View implements IDanmakuView, IDanmakuViewController {
    public static final int MAX_RECORD_SIZE = 50;
    public static final int ONE_SECOND = 1000;

    private final Object mDrawMonitor = new Object();
    @ThreadType
    protected int mDrawingThreadType = ThreadType.NORMAL_PRIORITY;
    private volatile DrawHandler mHandler;
    private HandlerThread mHandlerThread;
    private DanmakuTouchHelper mTouchHelper;
    private DrawHandlerCallback mCallback;
    private OnDanmakuClickListener mOnDanmakuClickListener;
    private boolean isEnableDanmakuDrawingCache = true;
    private boolean isSurfaceCreated;
    private boolean isShowFps = false;
    private boolean isDanmakuVisible = true;
    private boolean isDrawFinished = false;
    private boolean isRequestRender = false;  // DrawHandler 驱动渲染
    private boolean isClearFlag = false;
    private float mXOff;
    private float mYOff;
    private long mUiThreadId;
    private int mResumeTryCount = 0;
    private Runnable mResumeRunnable = new Runnable() {
        @Override
        public void run() {
            DrawHandler drawHandler = mHandler;
            if (drawHandler == null) {
                return;
            }
            mResumeTryCount++;
            if (mResumeTryCount > 4 || DanmakuView.super.isShown()) {
                drawHandler.resume();
            } else {
                drawHandler.postDelayed(this, 100 * mResumeTryCount);
            }
        }
    };

    public DanmakuView(Context context) {
        super(context);
        init();
    }

    public DanmakuView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DanmakuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mUiThreadId = Thread.currentThread().getId();
        setBackgroundColor(Color.TRANSPARENT);
        setDrawingCacheBackgroundColor(Color.TRANSPARENT);
        DrawHelper.useDrawColorToClearCanvas(true, false);
        mTouchHelper = DanmakuTouchHelper.instance(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mHandler != null) {
            mHandler.notifyDispSizeChanged(right - left, bottom - top);
        }
        isSurfaceCreated = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        LogUtils.d("DanmakuView onDraw start = " + isDanmakuVisible + ", " + isRequestRender + ", " + isClearFlag + ", " + mHandlerThread);
        if (!isDanmakuVisible && !isRequestRender) {
            super.onDraw(canvas);
            return;
        }
        if (isClearFlag) {
            DrawHelper.clearCanvas(canvas);
            isClearFlag = false;
        } else {
            if (mHandler != null) {
                RenderingState rs = mHandler.draw(canvas);
                if (isShowFps) {
                    String fps = mFormatter.format("fps: %.2f, time: %d, cache: %d, miss: %d", fps(),
                            getCurrentTime() / 1000, rs.cacheHitCount, rs.cacheMissCount).toString();
                    DrawHelper.drawFPS(canvas, fps);
                }
            }
        }
        isRequestRender = false;
        unlockCanvasAndPost();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mTouchHelper.onTouchEvent(event) || super.onTouchEvent(event);
    }

    // --- IDanmakuView ---------------------------------------------------------------------------------
    @Override
    public void prepare(BaseDanmakuParser parser, DanmakuConfig config) {
        if (parser == null || config == null) {
            throw new NullPointerException("BaseDanmakuParser or DanmakuConfig is null!");
        }
        prepare();
        mHandler.setConfig(config);
        mHandler.setParser(parser);
        mHandler.setCallback(mCallback);
        mHandler.prepare();
    }

    @Override
    public void addDanmaku(BaseDanmaku item) {
        if (mHandler != null) {
            mHandler.addDanmaku(item);
        }
    }

    @Override
    public void invalidateDanmaku(BaseDanmaku item, boolean remeasure) {
        if (mHandler != null) {
            mHandler.invalidateDanmaku(item, remeasure);
        }
    }

    @Override
    public void removeAllDanmakus(boolean isClearDanmakusOnScreen) {
        if (mHandler != null) {
            mHandler.removeAllDanmakus(isClearDanmakusOnScreen);
        }
    }

    @Override
    public void removeAllLiveDanmakus() {
        if (mHandler != null) {
            mHandler.removeAllLiveDanmakus();
        }
    }

    @Override
    public IDanmakuCollection getCurVisibleDanmakuSet() {
        if (mHandler != null) {
            return mHandler.getCurVisibleDanmakuSet();
        }
        return null;
    }

    @Override
    public void setCallback(DrawHandlerCallback callback) {
        mCallback = callback;
        if (mHandler != null) {
            mHandler.setCallback(callback);
        }
    }

    @Override
    public void setDrawingThreadType(int drawingThreadType) {
        mDrawingThreadType = drawingThreadType;
    }

    @Override
    public void enableDanmakuDrawingCache(boolean enable) {
        isEnableDanmakuDrawingCache = enable;
    }

    @Override
    public long getCurrentTime() {
        if (mHandler != null) {
            return mHandler.getCurrentTime();
        }
        return 0;
    }

    @Override
    public DanmakuConfig getConfig() {
        if (mHandler != null) {
            return mHandler.getConfig();
        }
        return null;
    }

    @Override
    public void start() {
        start(0);
    }

    @Override
    public void start(long position) {
        Handler handler = this.mHandler;
        if (handler == null) {
            prepare();
            handler = this.mHandler;
        } else {
            handler.removeCallbacksAndMessages(null);
        }

        if (handler != null) {
            handler.obtainMessage(DrawHandler.What.START, position).sendToTarget();
        }
    }

    @Override
    public void stop() {
        stopDraw();
    }

    @Override
    public void pause() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mResumeRunnable);
            mHandler.pause();
        }
    }

    @Override
    public void seekTo(long ms) {
        if (mHandler != null) {
            mHandler.seekTo(ms);
        }
    }

    @Override
    public void resume() {
        if (mHandler == null) {
            restart();
        } else if (mHandler.isPrepared()) {
            mResumeTryCount = 0;
            mHandler.post(mResumeRunnable);
        }
    }

    @Override
    public void toggle() {
        if (isSurfaceCreated) {
            if (mHandler == null) {
                start();
            } else if (mHandler.isStop()) {
                resume();
            } else {
                pause();
            }
        }
    }

    @Override
    public void show() {
        showAndResumeDrawTask(-1);
    }

    @Override
    public void showAndResumeDrawTask(long position) {
        isDanmakuVisible = true;
        isClearFlag = false;
        if (mHandler == null) {
            return;
        }
        mHandler.showDanmakus(position);
    }

    @Override
    public void hide() {
        isDanmakuVisible = false;
        if (mHandler == null) return;
        mHandler.hideDanmakus(false);
    }

    @Override
    public long hideAndPauseDrawTask() {
        isDanmakuVisible = false;
        if (mHandler == null) return 0;
        return mHandler.hideDanmakus(true);
    }

    @Override
    public void release() {
        stop();
        if (mDrawTimes != null) {
            mDrawTimes.clear();
        }
    }

    @Override
    public void clearDanmakusOnScreen() {
        if (mHandler != null) {
            mHandler.clearDanmakusOnScreen();
        }
    }

    @Override
    public boolean isPrepared() {
        return mHandler != null && mHandler.isPrepared();
    }

    @Override
    public boolean isPaused() {
        if (mHandler != null) {
            return mHandler.isStop();
        }
        return false;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public boolean isShown() {
        return isDanmakuVisible && super.isShown();
    }

    @Override
    public void showFPS(boolean show) {
        isShowFps = show;
        if (show && mDrawTimes == null) {
            mDrawTimes = new LinkedList<>();
            mFormatter = new Formatter(Locale.getDefault());
        }
    }

    @Override
    public void setOnDanmakuClickListener(OnDanmakuClickListener listener) {
        mOnDanmakuClickListener = listener;
    }

    @Override
    public void setOnDanmakuClickListener(OnDanmakuClickListener listener, float xOff, float yOff) {
        mOnDanmakuClickListener = listener;
        mXOff = xOff;
        mYOff = yOff;
    }

    @Override
    public OnDanmakuClickListener getOnDanmakuClickListener() {
        return mOnDanmakuClickListener;
    }

    @Override
    public float getXOff() {
        return mXOff;
    }

    @Override
    public float getYOff() {
        return mYOff;
    }

    @Override
    public void forceRender() {
        isRequestRender = true;
        if (mHandler != null) {
            mHandler.forceRender();
        }
    }

    // --- IDanmakuViewController ------------------------------------------------------------------
    @Override
    public boolean isViewReady() {
        return isSurfaceCreated;
    }

    @Override
    public boolean isEnableDanmakuDrawingCache() {
        return isEnableDanmakuDrawingCache;
    }

    @Override
    public int getViewWidth() {
        return super.getWidth();
    }

    @Override
    public int getViewHeight() {
        return super.getHeight();
    }

    @Override
    public long drawDanmakusSync() {
        if (!isSurfaceCreated) {
            return 0;
        }
        if (!isShown()) {
            return -1;
        }
        long sTime = SystemClock.uptimeMillis();
        lockCanvas();
        return SystemClock.uptimeMillis() - sTime;
    }

    @Override
    public void clear() {
        if (!isViewReady()) {
            return;
        }
        if (!isDanmakuVisible || Thread.currentThread().getId() == mUiThreadId) {
            isClearFlag = true;
            postInvalidateCompat();
        } else {
            lockCanvasAndClear();
        }
    }


    // --- 辅助方法 ---------------------------------------------------------------------------------
    private void prepare() {
        if (mHandler == null) {
            mHandler = new DrawHandler(getLooper(mDrawingThreadType), this, isDanmakuVisible);
        }
    }

    private void restart() {
        stop();
        start();
    }

    private synchronized void stopDraw() {
        if (mHandler == null) {
            return;
        }

        DrawHandler handler = mHandler;
        mHandler = null;
        unlockCanvasAndPost();
        if (handler != null) {
            handler.quit();
        }

        HandlerThread handlerThread = mHandlerThread;
        mHandlerThread = null;
        if (handlerThread != null) {
            try {
                // TODO 主线程等待它死亡才进行下一步，会阻塞主线程
                // Waits for this thread to die.
                handlerThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            handlerThread.quit();
        }

    }

    protected synchronized Looper getLooper(@ThreadType int type) {
        if (mHandlerThread != null) {
            mHandlerThread.quit();
            mHandlerThread = null;
        }

        int priority;
        switch (type) {
            case ThreadType.MAIN_THREAD:
                return Looper.getMainLooper();
            case ThreadType.LOW_PRIORITY:
                priority = Process.THREAD_PRIORITY_LOWEST;
                break;
            case ThreadType.HIGH_PRIORITY:
                priority = Process.THREAD_PRIORITY_URGENT_DISPLAY;
                break;
            case ThreadType.NORMAL_PRIORITY:
            default:
                priority = Process.THREAD_PRIORITY_DEFAULT;
                break;
        }
        String threadName = "DFM_Handler_Thread#" + priority;
        mHandlerThread = new HandlerThread(threadName, priority);
        mHandlerThread.start();
        return mHandlerThread.getLooper();
    }

    private void postInvalidateCompat() {
        isRequestRender = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            postInvalidateOnAnimation();
        } else {
            postInvalidate();
        }
    }

    private void lockCanvasAndClear() {
        isClearFlag = true;
        lockCanvas();
    }

    private void lockCanvas() {
        if (!isDanmakuVisible) {
            return;
        }

        postInvalidateCompat();
        synchronized (mDrawMonitor) {
            while (!isDrawFinished && mHandler != null) {
                try {
                    mDrawMonitor.wait(200);
                } catch (InterruptedException e) {
                    // 弹幕不可见 || handler 为空 || handler stop
                    // 弹幕可见 && handler 不为空 && handler没在 stop 状态
                    if (isDanmakuVisible && mHandler != null && !mHandler.isStop()) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            isDrawFinished = false;
        }
    }

    private void unlockCanvasAndPost() {
        synchronized (mDrawMonitor) {
            isDrawFinished = true;
            mDrawMonitor.notifyAll();
        }
    }

    // for fps()
    private Formatter mFormatter;
    private LinkedList<Long> mDrawTimes;
    public float fps() {
        long curTime = SystemClock.uptimeMillis();
        mDrawTimes.addLast(curTime);
        Long first = mDrawTimes.peekFirst();
        if (first == null) return 0.0f;
        float dTime = curTime - first;
        int frames = mDrawTimes.size();
        if (frames > MAX_RECORD_SIZE) {
            mDrawTimes.removeFirst();
        }
        return dTime > 0 ? frames * ONE_SECOND / dTime : 0.0f;
    }

}
