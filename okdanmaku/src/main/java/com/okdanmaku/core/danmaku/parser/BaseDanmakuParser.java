package com.okdanmaku.core.danmaku.parser;

import com.okdanmaku.core.danmaku.mdoel.DanmakuTimer;
import com.okdanmaku.core.danmaku.mdoel.IDanmakuCollection;

/**
 * Created by yangya on 2019-10-14.
 */
public abstract class BaseDanmakuParser {
    protected IDataSource<?> mDataSource;
    protected DanmakuTimer mTimer;
    private IDanmakuCollection mDanmakuCollection;

    protected abstract IDanmakuCollection parse();

    public DanmakuTimer getTimer() {
        return mTimer;
    }

    public IDanmakuCollection getDanmakuCollection() {
        if (mDanmakuCollection != null) {
            return mDanmakuCollection;
        }
        mDanmakuCollection = parse();
        releaseDataSource();
        return mDanmakuCollection;
    }

    protected void releaseDataSource() {
        if (mDataSource != null) {
            mDataSource.release();
            mDataSource = null;
        }
    }
}
