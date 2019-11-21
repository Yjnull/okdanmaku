package com.okdanmaku.core.danmaku.controller;

import android.content.Context;
import android.graphics.Canvas;

import com.okdanmaku.core.R;
import com.okdanmaku.core.danmaku.loader.android.BiliDanmakuLoader;
import com.okdanmaku.core.danmaku.loader.android.DanmakuLoaderFactory;
import com.okdanmaku.core.danmaku.model.DanmakuBase;
import com.okdanmaku.core.danmaku.model.DanmakuTimer;
import com.okdanmaku.core.danmaku.model.IDanmakus;
import com.okdanmaku.core.danmaku.model.android.AndroidDisplayer;
import com.okdanmaku.core.danmaku.model.android.Danmakus;
import com.okdanmaku.core.danmaku.parser.BiliDanmakuFactory;
import com.okdanmaku.core.danmaku.parser.IDataSource;
import com.okdanmaku.core.danmaku.parser.android.BiliDanmakuParser;
import com.okdanmaku.core.danmaku.renderer.IRenderer;
import com.okdanmaku.core.danmaku.renderer.android.DanmakuRenderer;

import java.io.InputStream;

/**
 * Created by yangya on 2019-11-21.
 */
public class DrawTask {

    private final AndroidDisplayer mDisp;  // 显示器
    private Context mContext;
    private IRenderer mRenderer;  // 渲染器
    private DanmakuTimer mTimer;
    private InputStream mInputStream;
    private BiliDanmakuLoader mLoader;
    private IDataSource mDataSource;
    private BiliDanmakuParser mParser;
    private Danmakus mDanmakuList;
    private final long duration;

    /**
     * 构造函数初始化，并通过 Loader 和 Parser 将 R.raw.comments 解析成 Danmakus
     */
    public DrawTask(DanmakuTimer timer, Context context, int dispW, int dispH) {
        mTimer = timer;
        mRenderer = new DanmakuRenderer();
        mDisp = new AndroidDisplayer();
        mDisp.width = dispW;
        mDisp.height = dispH;
        duration = BiliDanmakuFactory.calDuration(dispW);
        loadBiliDanmakus(context.getResources().openRawResource(R.raw.comments));
    }

    private void loadBiliDanmakus(InputStream stream) {
        mLoader = (BiliDanmakuLoader) DanmakuLoaderFactory.create("bili");
        if (mLoader == null) {
            throw new NullPointerException("ILoader must not be null!");
        }
        mLoader.load(stream);
        mDataSource = mLoader.getDataSource();
        mParser = new BiliDanmakuParser(mDisp.width);
        mParser.load(mDataSource);
        mDanmakuList = mParser.parse();

        // 给所有 DanmakuBase 设置 DanmakuSurfaceView 的 Timer
        for (DanmakuBase itr : mDanmakuList.items) {
            itr.setTimer(mTimer);
        }
    }

    /**
     * 通过 Displayer 和 Renderer 绘制弹幕
     */
    public void draw(Canvas canvas) {
        if (mDanmakuList != null) {
            long curMills = mTimer.curMillisecond;
            IDanmakus danmakus = mDanmakuList.sub(curMills - duration, curMills + duration);
            // 显示器有了画布
            mDisp.init(canvas);
            if (danmakus != null) {
                // 让渲染器去画该弹幕
                mRenderer.draw(mDisp, danmakus);
            }
        }
    }
}
