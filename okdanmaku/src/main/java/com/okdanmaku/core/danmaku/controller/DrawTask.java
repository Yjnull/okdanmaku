package com.okdanmaku.core.danmaku.controller;

import android.content.Context;
import android.graphics.Canvas;

import com.okdanmaku.core.R;
import com.okdanmaku.core.danmaku.loader.android.BiliDanmakuLoader;
import com.okdanmaku.core.danmaku.loader.android.DanmakuLoaderFactory;
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

    /**
     * 构造函数初始化，并通过 Loader 和 Parser 将 R.raw.comments 解析成 Danmakus
     */
    public DrawTask(DanmakuTimer timer, Context context, int dispW, int dispH) {
        mTimer = timer;
        mRenderer = new DanmakuRenderer();
        mDisp = new AndroidDisplayer();
        mDisp.width = dispW;
        mDisp.height = dispH;
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
        mDanmakuList = mParser.parse(mTimer);
    }

    /**
     * 通过 Displayer 和 Renderer 绘制弹幕
     */
    public void draw(Canvas canvas) {
        if (mDanmakuList != null) {
            long curMills = mTimer.curMillisecond;
            IDanmakus danmakus = mDanmakuList.sub(curMills - BiliDanmakuFactory.REAL_DANMAKU_DURATION,
                    curMills + BiliDanmakuFactory.REAL_DANMAKU_DURATION);
            if (danmakus != null) {
                // 显示器更新画布
                mDisp.update(canvas);
                // 让渲染器去画该弹幕
                mRenderer.draw(mDisp, danmakus);
            }
        }
    }
}
