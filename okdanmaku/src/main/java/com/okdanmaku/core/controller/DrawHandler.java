package com.okdanmaku.core.controller;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Choreographer;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import com.okdanmaku.core.callback.DrawHandlerCallback;
import com.okdanmaku.core.callback.OnTaskListener;
import com.okdanmaku.core.danmaku.mdoel.AbsDisplayer;
import com.okdanmaku.core.danmaku.mdoel.BaseDanmaku;
import com.okdanmaku.core.danmaku.mdoel.DanmakuConfig;
import com.okdanmaku.core.danmaku.mdoel.DanmakuTimer;
import com.okdanmaku.core.danmaku.mdoel.IDanmakuCollection;
import com.okdanmaku.core.danmaku.mdoel.IDisplayer;
import com.okdanmaku.core.danmaku.parser.BaseDanmakuParser;
import com.okdanmaku.core.danmaku.renderer.RenderingState;
import com.okdanmaku.core.utils.DeviceUtils;
import com.okdanmaku.core.utils.LogUtils;
import com.okdanmaku.core.utils.SystemClock;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.LinkedList;

/**
 * Created by yangya on 2019-10-14.
 * 运行在 DFM_Handler_Thread#priority 线程 或 主线程
 */
public class DrawHandler extends Handler {
    public static final long INDEFINITE_TIME = 10000000;
    public static final int MAX_RECORD_SIZE = 500;

    private final Object mLock = new Object();
    private final RenderingState mRenderingState = new RenderingState(); // 渲染状态
    private FrameCallback mFrameCallback;
    private DanmakuConfig mConfig;
    private BaseDanmakuParser mParser;
    private DrawHandlerCallback mCallback;
    private IDrawTask mDrawTask;
    private IDanmakuViewController mDanmakuView;
    private AbsDisplayer mDisplayer;                            // AndroidDisplayer
    private DanmakuTimer mTimer = new DanmakuTimer();
    private LinkedList<Long> mDrawTimes = new LinkedList<>();
    private UpdateThread mUpdateThread;
    private long mPausedPosition = 0;
    private long mTimeBase;                                    // prepare 的时间点
    private long mCordonTime = 30;
    private long mCordonTime2 = 60;
    private long mFrameUpdateRate = 16;
    private long mLastDeltaTime;
    private long mDesireSeekingTime;
    private long mRemainingTime;
    private boolean isQuitFlag = true;
    private boolean isReady;
    private boolean isDanmakusVisible;
    private boolean isInSeekingAction;
    private boolean isInSyncAction;
    private boolean isInWaitingState;
    private boolean isIdleSleep;
    private boolean isNonBlockModeEnable;

    public DrawHandler(Looper looper, IDanmakuViewController viewController, boolean danmakusVisible) {
        super(looper);
        isIdleSleep = !DeviceUtils.isProblemBoxDevice();
        mDanmakuView = viewController;
        if (danmakusVisible) {
            showDanmakus(-1);
        } else {
            hideDanmakus(false);
        }
        isDanmakusVisible = danmakusVisible;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        int what = msg.what;
        switch (what) {
            case What.PREPARE:
                mTimeBase = SystemClock.uptimeMillis();
                if (mParser == null || !mDanmakuView.isViewReady()) {
                    sendEmptyMessageDelayed(What.PREPARE, 100);
                } else {
                    createDrawTask();
                }
                break;
            case What.SHOW_DANMAKUS:
                break;
            case What.START:
                Long startTime = (Long) msg.obj;
                if (startTime != null && startTime > 0) {
                    mPausedPosition = startTime;
                } else {
                    mPausedPosition = 0;
                }
            case What.SEEK_POS:
                if (what == What.SEEK_POS) {
                    isQuitFlag = true;
                    quitUpdateThread();
                    Long position = (Long) msg.obj;
                    long deltaMs = position - mTimer.currMillisecond;
                    mTimeBase -= deltaMs;
                    mTimer.update(position);
                    mConfig.mGlobalFlagValues.updateMeasureFlag();
                    if (mDrawTask != null) {
                        mDrawTask.seek(position);
                    }
                    mPausedPosition = position;
                }
            case What.RESUME:
                removeMessages(What.PAUSE);
                isQuitFlag = false;
                if (isReady) {
                    mRenderingState.reset();
                    mDrawTimes.clear();
                    mTimeBase = SystemClock.uptimeMillis() - mPausedPosition;
                    mTimer.update(mPausedPosition);
                    removeMessages(What.RESUME);
                    sendEmptyMessage(What.UPDATE);
                    mDrawTask.start();
                    notifyRendering();
                    isInSeekingAction = false;
                    mDrawTask.onPlayStateChanged(IDrawTask.PlayState.PLAYING);
                } else {
                    sendEmptyMessageDelayed(What.RESUME, 100);
                }
                break;
            case What.UPDATE:
                if (mConfig.isUpdateInDefault()) {
                    updateInChoreographer();
                } else if (mConfig.isUpdateInSeparate()) {
                    updateInNewThread();
                } else if (mConfig.isUpdateInSelfDriver()) {
                    updateInCurrentThread();
                }
                break;
            case What.DISP_SIZE_CHANGE:
                break;
            case What.HIDE_DANMAKUS:
                break;
            case What.PAUSE:
                break;
            case What.QUIT:
                break;
            case What.NOTIFY_RENDERING:
                break;
            case What.UPDATE_WHEN_PAUSED:
                break;
            case What.CLEAR_DANMAKUS_ON_SCREEN:
                break;
            case What.NOTIFY_FORCE_RENDER:
                break;
        }
    }

    // --- draw,sync ------------------------------------------------------------
    private void initRenderingConfigs() {

    }

    private void createDrawTask() {
        if (mDrawTask == null) {
            // createDrawTask
            //     1. create Displayer
            DisplayMetrics displayMetrics = mDanmakuView.getContext().getResources().getDisplayMetrics();
            mDisplayer = mConfig.getDisplayer();
            LogUtils.d("createDrawTask = " + mDisplayer);
            mDisplayer.setSize(mDanmakuView.getViewWidth(), mDanmakuView.getViewHeight());
            mDisplayer.setDensities(displayMetrics.density, displayMetrics.densityDpi,
                    displayMetrics.scaledDensity);
            mDisplayer.resetSlopPixel(mConfig.scaleTextSize);
            mDisplayer.setHardwareAccelerated(mDanmakuView.isHardwareAccelerated());
            //     2. create OnTaskListener
            OnTaskListener taskListener = new OnTaskListener() {
                @Override
                public void ready() {
                    initRenderingConfigs();
                    mPausedPosition = 0;
                    isReady = true;
                    if (mCallback != null) mCallback.prepared();
                }

                @Override
                public void onDanmakuAdd(BaseDanmaku danmaku) {
                    /*if (danmaku.isTimeOut()) {
                        return;
                    }*/

                }

                @Override
                public void onDanmakuShown(BaseDanmaku danmaku) {
                    if (mCallback != null) {
                        mCallback.danmakuShown(danmaku);
                    }
                }

                @Override
                public void onDanmakuConfigChanged() {
                    redrawIfNeeded();
                }

                @Override
                public void onDanmakusDrawingFinished() {
                    if (mCallback != null) mCallback.drawingFinished();
                }
            };
            IDrawTask task = mDanmakuView.isEnableDanmakuDrawingCache() ?
                    new CacheManagingDrawTask(mTimer, mConfig, taskListener)
                    : new DrawTask(mTimer, mConfig, taskListener);
            task.setParser(mParser);
            task.prepare();
            mDrawTask = task;
            obtainMessage(What.DISP_SIZE_CHANGE, false).sendToTarget();

        } else {
            mPausedPosition = 0;
            isReady = true;
            if (mCallback != null) mCallback.prepared();
        }
    }

    /**
     * 由 DanmakuView 驱动 draw
     */
    public RenderingState draw(Canvas canvas) {
        if (mDrawTask == null) return mRenderingState;

        mDisplayer.setExtraData(canvas);
        mRenderingState.set(mDrawTask.draw(mDisplayer));
        recordRenderingTime();
        return mRenderingState;
    }

    private void redrawIfNeeded() {

    }

    private void notifyRendering() {
        // 唤醒渲染层，如果渲染层在等待中，才唤醒
        if (!isInWaitingState) return;
        mDrawTask.requestClear();
        if (mConfig.isUpdateInSeparate()) {
            synchronized (this) {
                mDrawTimes.clear();
            }
            synchronized (mLock) {
                mLock.notifyAll();
            }
        } else {
            mDrawTimes.clear();
            removeMessages(What.UPDATE);
            sendEmptyMessage(What.UPDATE);
        }
        isInWaitingState = false;
    }

    private void waitRendering(long dTime) {

    }

    private synchronized long getAverageRenderingTime() {
        int frames = mDrawTimes.size();
        if (frames <= 0) return 0;
        Long first = mDrawTimes.peekFirst();
        Long last = mDrawTimes.peekLast();
        if (first == null || last == null) {
            return 0;
        }
        long dTime = last - first;
        return dTime / frames;
    }

    /**
     * 记录渲染时间
     */
    private synchronized void recordRenderingTime() {
        mDrawTimes.addLast(SystemClock.uptimeMillis());
        if (mDrawTimes.size() > MAX_RECORD_SIZE) {
            mDrawTimes.removeFirst();
        }
    }

    /**
     * 同步 Timer
     * updateInChoreographer 用到了
     * @param startMs 当前调用的时间
     * @return FIXME ?
     */
    private long syncTimer(long startMs) {
        if (isInSeekingAction || isInSyncAction) {
            return 0;
        }
        isInSyncAction = true;
        long d = 0;
        long time = startMs - mTimeBase;  // 当前与上一个时间的间隔
        if (isNonBlockModeEnable) {
            // 非阻塞模式开启
            if (mCallback != null) {
                mCallback.updateTimer(mTimer);
                d = mTimer.lastInterval();
            }
        } else if (!isDanmakusVisible || mRenderingState.nothingRendered || isInWaitingState) {
            // 弹幕不可见 || 没有在渲染 || 渲染层等待中
            mTimer.update(time);
            mRemainingTime = 0;
            if (mCallback != null) {
                mCallback.updateTimer(mTimer);
            }
        } else {
            long gapTime = time - mTimer.currMillisecond;
            long averageTime = Math.max(mFrameUpdateRate, getAverageRenderingTime());
            if (gapTime > 2000 || mRenderingState.consumingTime > mCordonTime || averageTime > mCordonTime) {
                d = gapTime;
                gapTime = 0;
            } else {
                d = averageTime + gapTime / mFrameUpdateRate;
                d = Math.max(mFrameUpdateRate, d);
                d = Math.min(mCordonTime, d);
                long a = d - mLastDeltaTime;
                if (a > 3 && a < 8 && mLastDeltaTime >= mFrameUpdateRate && mLastDeltaTime <= mCordonTime) {
                    d = mLastDeltaTime;
                }
                gapTime -= d;
                mLastDeltaTime = d;
            }
            mRemainingTime = gapTime;
            mTimer.add(d);
            if (mCallback != null) {
                mCallback.updateTimer(mTimer);
            }
        }

        isInSyncAction = false;
        return d;
    }

    private void syncTimerIfNeeded() {

    }

    private void updateInChoreographer() {
        if (isQuitFlag) return;
        Choreographer.getInstance().postFrameCallback(mFrameCallback);
        long startMs = SystemClock.uptimeMillis();
        long d = syncTimer(startMs);
        if (d < 0) {
            removeMessages(What.UPDATE);
            return;
        }
        // 等待 danmaku 绘制完毕
        d = mDanmakuView.drawDanmakusSync();
        removeMessages(What.UPDATE);
        if (d > mCordonTime2) {
            mTimer.add(d);
            mDrawTimes.clear();
        }
        if (!isDanmakusVisible) {
            waitRendering(INDEFINITE_TIME);
        } else if (mRenderingState.nothingRendered && isIdleSleep) {
            long dTime = mRenderingState.endTime - mTimer.currMillisecond;
            if (dTime > 500) {
                waitRendering(dTime - 10);
            }
        }
    }

    private void updateInNewThread() {

    }

    private void updateInCurrentThread() {

    }

    private synchronized void quitUpdateThread() {
        UpdateThread thread = mUpdateThread;
        mUpdateThread = null;
        if (thread != null) {
            synchronized (mLock) {
                mLock.notifyAll();
            }
            thread.quit();
            try {
                // 等待该线程死亡，最多等待 2s
                thread.join(2000);
            } catch (InterruptedException ignore) {

            }
        }
    }

    // --- 弹幕操作 ----------------------------------------
    public void seekTo(Long ms) {
        isInSeekingAction = true;
        mDesireSeekingTime = ms;
        removeMessages(What.UPDATE);
        removeMessages(What.RESUME);
        removeMessages(What.SEEK_POS);
        obtainMessage(What.SEEK_POS, ms).sendToTarget();
    }

    public void addDanmaku(BaseDanmaku item) {
        if (mDrawTask != null) {
            //item.flags = mContext.mGlobalFlagValues;
            //item.setTimer(mTimer);
            mDrawTask.addDanmaku(item);
            obtainMessage(What.NOTIFY_RENDERING).sendToTarget();
        }
    }

    public void invalidateDanmaku(BaseDanmaku item, boolean remeasure) {
        if (mDrawTask != null && item != null) {
            mDrawTask.invalidateDanmaku(item, remeasure);
        }
        redrawIfNeeded();
    }

    public void resume() {
        removeMessages(What.PAUSE);
        sendEmptyMessage(What.RESUME);
    }

    public void prepare() {
        isReady = false;
        /*if (Build.VERSION.SDK_INT < 16 && mConfig.updateMethod == 0) {
            mConfig.updateMethod = 2;
        }*/
        if (mConfig.updateMethod == DanmakuConfig.UpdateMethod.DEFAULT) {
            mFrameCallback = new FrameCallback();
        }
        sendEmptyMessage(What.PREPARE);
    }

    public void pause() {
        removeMessages(What.RESUME);
        syncTimerIfNeeded();
        sendEmptyMessage(What.PAUSE);
    }

    public void showDanmakus(long position) {
        if (isVisible()) {
            return;
        }
        isDanmakusVisible = true;
        removeMessages(What.SHOW_DANMAKUS);
        removeMessages(What.HIDE_DANMAKUS);
        obtainMessage(What.SHOW_DANMAKUS, position).sendToTarget();
    }

    public long hideDanmakus(boolean quitDrawTask) {
        if (!isVisible()) {
            return mTimer.currMillisecond;
        }
        isDanmakusVisible = false;
        removeMessages(What.SHOW_DANMAKUS);
        removeMessages(What.HIDE_DANMAKUS);
        obtainMessage(What.HIDE_DANMAKUS, quitDrawTask).sendToTarget();
        return mTimer.currMillisecond;
    }

    public void forceRender() {
        removeMessages(What.NOTIFY_FORCE_RENDER);
        obtainMessage(What.NOTIFY_FORCE_RENDER).sendToTarget();
    }

    public void notifyDispSizeChanged(int width, int height) {
        if (mDisplayer == null) {
            return;
        }
        if (mDisplayer.getWidth() != width || mDisplayer.getHeight() != height) {
            mDisplayer.setSize(width, height);
            obtainMessage(What.DISP_SIZE_CHANGE, true).sendToTarget();
        }
    }

    public void removeAllDanmakus(boolean isClearDanmakusOnScreen) {
        if (mDrawTask != null) {
            mDrawTask.removeAllDanmakus(isClearDanmakusOnScreen);
        }
    }

    public void removeAllLiveDanmakus() {
        if (mDrawTask != null) {
            mDrawTask.removeAllLiveDanmakus();
        }
    }

    public void quit() {
        isQuitFlag = true;
        sendEmptyMessage(What.QUIT);
    }

    // --- set,get --------------------------------------------------------
    public void setIdleSleep(boolean enable) {
        isIdleSleep = enable;
    }

    public void setNonBlockModeEnable(boolean nonBlockModeEnable) {
        isNonBlockModeEnable = nonBlockModeEnable;
    }

    public void setParser(BaseDanmakuParser parser) {
        mParser = parser;
        DanmakuTimer timer = parser.getTimer();
        if (timer != null) {
            this.mTimer = timer;
        }
    }

    public void setCallback(DrawHandlerCallback cb) {
        mCallback = cb;
    }

    public boolean isPrepared() {
        return isReady;
    }

    public boolean isStop() {
        return isQuitFlag;
    }

    public boolean isVisible() {
        return isDanmakusVisible;
    }

    public IDisplayer getDisplayer() {
        return mDisplayer;
    }

    public IDanmakuCollection getCurVisibleDanmakuSet() {
        if (mDrawTask != null) {
            return mDrawTask.getVisibleDanmakusOnTime(getCurrentTime());
        }
        return null;
    }

    public long getCurrentTime() {
        if (!isReady) {
            return 0;
        }
        if (isInSeekingAction) {
            return mDesireSeekingTime;
        }
        if (isQuitFlag || !isInWaitingState) {
            return mTimer.currMillisecond - mRemainingTime;
        }
        return SystemClock.uptimeMillis() - mTimeBase;
    }

    public void clearDanmakusOnScreen() {
        obtainMessage(What.CLEAR_DANMAKUS_ON_SCREEN).sendToTarget();
    }

    public DanmakuConfig getConfig() {
        return mConfig;
    }

    public void setConfig(DanmakuConfig config) {
        mConfig = config;
    }

    // --- class ------------------------------------------------------------------------------
    @IntDef({What.START, What.UPDATE, What.RESUME, What.SEEK_POS, What.PREPARE,
            What.QUIT, What.PAUSE, What.SHOW_DANMAKUS, What.HIDE_DANMAKUS,
            What.DISP_SIZE_CHANGE, What.NOTIFY_RENDERING, What.UPDATE_WHEN_PAUSED,
            What.CLEAR_DANMAKUS_ON_SCREEN, What.NOTIFY_FORCE_RENDER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface What {
        int START = 1;
        int UPDATE = 2;
        int RESUME = 3;
        int SEEK_POS = 4;
        int PREPARE = 5;
        int QUIT = 6;
        int PAUSE = 7;
        int SHOW_DANMAKUS = 8;
        int HIDE_DANMAKUS = 9;
        int DISP_SIZE_CHANGE = 10;
        int NOTIFY_RENDERING = 11;
        int UPDATE_WHEN_PAUSED = 12;
        int CLEAR_DANMAKUS_ON_SCREEN = 13;
        int NOTIFY_FORCE_RENDER = 14;
    }

    @TargetApi(16)
    private class FrameCallback implements Choreographer.FrameCallback {
        private long start = 0;

        @Override
        public void doFrame(long frameTimeNanos) {
            LogUtils.d("frame : " + (frameTimeNanos - start) / 1000000);
            start = frameTimeNanos;
            //sendEmptyMessage(What.UPDATE);
        }
    }


}
