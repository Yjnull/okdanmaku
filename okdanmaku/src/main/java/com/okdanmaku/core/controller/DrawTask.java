package com.okdanmaku.core.controller;

import android.graphics.Canvas;

import com.okdanmaku.core.callback.OnTaskListener;
import com.okdanmaku.core.danmaku.mdoel.AbsDisplayer;
import com.okdanmaku.core.danmaku.mdoel.BaseDanmaku;
import com.okdanmaku.core.danmaku.mdoel.DanmakuConfig;
import com.okdanmaku.core.danmaku.mdoel.DanmakuTimer;
import com.okdanmaku.core.danmaku.mdoel.IDanmakuCollection;
import com.okdanmaku.core.danmaku.parser.BaseDanmakuParser;
import com.okdanmaku.core.danmaku.renderer.DanmakuRenderer;
import com.okdanmaku.core.danmaku.renderer.IRenderer;
import com.okdanmaku.core.danmaku.renderer.RenderingState;
import com.okdanmaku.core.utils.SystemClock;

/**
 * Created by yangya on 2019-10-16.
 */
public class DrawTask implements IDrawTask {

    final IRenderer mRenderer;
    private final DanmakuConfig mConfig;
    private final AbsDisplayer mDisplayer;
    private final RenderingState mRenderingState = new RenderingState();
    OnTaskListener mTaskListener;
    DanmakuTimer mTimer;
    private BaseDanmakuParser mParser;
    private IDanmakuCollection mDanmakuCollection;
    private IDanmakuCollection mDanmakus;
    private IDanmakuCollection mRunningDanmakus;
    private BaseDanmaku mLastDanmaku;
    private long mLastBeginMs;
    private long mLastEndMs;
    private boolean isReadyState;
    private boolean isClearRetainerFlag;
    private boolean isRequestRender;
    private boolean isHidden;

    DrawTask(DanmakuTimer timer, DanmakuConfig config, OnTaskListener taskListener) {
        if (config == null) {
            throw new IllegalArgumentException("DanmakuConfig is null");
        }
        mConfig = config;
        mDisplayer = config.getDisplayer();
        mTaskListener = taskListener;
        initTimer(timer);
        mRenderer = new DanmakuRenderer(config);
        // TODO 未完
    }

    protected void initTimer(DanmakuTimer timer) {
        mTimer = timer;
    }

    @Override
    public void setParser(BaseDanmakuParser parser) {
        mParser = parser;
        isReadyState = false;
    }

    @Override
    public void addDanmaku(BaseDanmaku item) {

    }

    @Override
    public void removeAllDanmakus(boolean isClearDanmakusOnScreen) {

    }

    @Override
    public void removeAllLiveDanmakus() {

    }

    @Override
    public void clearDanmakusOnScreen(long currMillis) {

    }

    @Override
    public IDanmakuCollection getVisibleDanmakusOnTime(long currentTime) {
        return null;
    }

    @Override
    public synchronized RenderingState draw(AbsDisplayer displayer) {
        return drawDanmakus(displayer, mTimer);
    }

    @Override
    public void prepare() {
        if (mParser == null) {
            return;
        }
        loadDanmakus(mParser);
        mLastBeginMs = mLastEndMs = 0;
        if (mTaskListener != null) {
            mTaskListener.ready();
            isReadyState = true;
        }
    }

    @Override
    public void reset() {

    }

    @Override
    public void seek(long mills) {

    }

    @Override
    public void start() {

    }

    @Override
    public void quit() {

    }

    @Override
    public void onPlayStateChanged(int state) {

    }

    @Override
    public void requestSync(long fromTimeMills, long toTimeMills, long offsetMills) {

    }

    @Override
    public void requestClear() {

    }

    @Override
    public void requestClearRetainer() {

    }

    @Override
    public void requestHide() {

    }

    @Override
    public void requestRender() {

    }

    @Override
    public void invalidateDanmaku(BaseDanmaku item, boolean remeasure) {

    }

    // --- 辅助 ------------------------------------------------------------------------------------
    protected void loadDanmakus(BaseDanmakuParser parser) {
        mDanmakuCollection = parser.getDanmakuCollection();
        if (mDanmakuCollection != null) {
            mLastDanmaku = mDanmakuCollection.last();
        }
    }

    protected RenderingState drawDanmakus(AbsDisplayer displayer, DanmakuTimer timer) {
        if (isClearRetainerFlag) {
            mRenderer.clearRetainer();
            isClearRetainerFlag = false;
        }
        if (mDanmakuCollection != null) {
            Canvas canvas = displayer.getExtraData();
            DrawHelper.clearCanvas(canvas);
            if (isHidden && !isRequestRender) {
                return mRenderingState;
            }
            isRequestRender = false;
            RenderingState renderingState = mRenderingState;
            // prepare screenDanmakus
            long begainMs = timer.currMillisecond - mConfig.mDanmakuFactory.MAX_DANMAKU_DURATION - 100;
            long endMs = timer.currMillisecond + mConfig.mDanmakuFactory.MAX_DANMAKU_DURATION;
            IDanmakuCollection screenDanmakus = mDanmakus;
            if (mLastBeginMs > begainMs || timer.currMillisecond > mLastEndMs) {
                screenDanmakus = mDanmakus.sub(begainMs, endMs);
                if (screenDanmakus != null) {
                    mDanmakus = screenDanmakus;
                }
                mLastBeginMs = begainMs;
                mLastEndMs = endMs;
            } else {
                begainMs = mLastBeginMs;
                endMs = mLastEndMs;
            }

            // prepare runningDanmakus to draw (in sync-mode)
            IDanmakuCollection runningDanmakus = mRunningDanmakus;
            beginTracing(renderingState, runningDanmakus, screenDanmakus);
            if (runningDanmakus != null && !runningDanmakus.isEmpty()) {
                mRenderingState.isRunningDanmakus = true;
                mRenderer.draw(displayer, runningDanmakus, 0, mRenderingState);
            }

            // draw screenDanmakus
            mRenderingState.isRunningDanmakus = false;
            if (screenDanmakus != null && !screenDanmakus.isEmpty()) {
                mRenderer.draw(displayer, screenDanmakus, mStartRenderTime, renderingState);
                endTracing(renderingState);
                if (renderingState.nothingRendered) {
                    if(mLastDanmaku != null && mLastDanmaku.isTimeOut()) {
                        mLastDanmaku = null;
                        if (mTaskListener != null) {
                            mTaskListener.onDanmakusDrawingFinished();
                        }
                    }
                    if (renderingState.beginTime == RenderingState.UNKNOWN_TIME) {
                        renderingState.beginTime = beginMills;
                    }
                    if (renderingState.endTime == RenderingState.UNKNOWN_TIME) {
                        renderingState.endTime = endMills;
                    }
                }
                return renderingState;
            } else {
                renderingState.nothingRendered = true;
                renderingState.beginTime = beginMills;
                renderingState.endTime = endMills;
                return renderingState;
            }
        }
        return null;
    }

    private void beginTracing(RenderingState renderingState, IDanmakuCollection runningDanmakus, IDanmakuCollection screenDanmakus) {
        renderingState.reset();
        renderingState.timer.update(SystemClock.uptimeMillis());
        renderingState.indexInScreen = 0;
        renderingState.totalSizeInScreen = (runningDanmakus != null ? runningDanmakus.size() : 0) + (screenDanmakus != null ? screenDanmakus.size() : 0);
    }

    private void endTracing(RenderingState renderingState) {
        renderingState.nothingRendered = (renderingState.totalDanmakuCount == 0);
        if (renderingState.nothingRendered) {
            renderingState.beginTime = RenderingState.UNKNOWN_TIME;
        }
        BaseDanmaku lastDanmaku = renderingState.lastDanmaku;
        renderingState.lastDanmaku = null;
        renderingState.endTime = lastDanmaku != null ? lastDanmaku.getActualTime() : RenderingState.UNKNOWN_TIME;
        renderingState.consumingTime = renderingState.timer.update(SystemClock.uptimeMillis());
    }
}
