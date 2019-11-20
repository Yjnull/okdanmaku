package com.okdanmaku.core.danmaku.parser;

import com.okdanmaku.core.danmaku.model.android.Danmakus;

/**
 * Created by yangya on 2019-11-19.
 */
public abstract class DanmakuParserBase {
    protected IDataSource mDataSource;

    public void load(IDataSource source) {
        mDataSource = source;
    }

    public Danmakus parse() {
        return parse(mDataSource);
    }

    public abstract Danmakus parse(IDataSource source);
}
