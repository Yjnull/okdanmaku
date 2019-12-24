package com.okdanmaku.core.danmaku.parser;

import com.okdanmaku.core.danmaku.model.DanmakuTimer;
import com.okdanmaku.core.danmaku.model.android.Danmakus;

/**
 * Created by yangya on 2019-11-19.
 */
public abstract class BaseDanmakuParser {
    protected IDataSource mDataSource;

    protected DanmakuTimer mTimer;

    public void load(IDataSource source) {
        mDataSource = source;
    }

    public Danmakus parse(DanmakuTimer timer) {
        this.mTimer = timer;
        return parse(mDataSource);
    }

    public abstract Danmakus parse(IDataSource source);
}
