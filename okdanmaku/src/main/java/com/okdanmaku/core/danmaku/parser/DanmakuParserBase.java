package com.okdanmaku.core.danmaku.parser;

import com.okdanmaku.core.danmaku.model.DanmakuBase;

import java.util.List;

/**
 * Created by yangya on 2019-11-19.
 */
public abstract class DanmakuParserBase {
    protected IDataSource mDataSource;

    public void load(IDataSource source) {
        mDataSource = source;
    }

    public List<DanmakuBase> parse() {
        return parse(mDataSource);
    }

    public abstract List<DanmakuBase> parse(IDataSource source);
}
