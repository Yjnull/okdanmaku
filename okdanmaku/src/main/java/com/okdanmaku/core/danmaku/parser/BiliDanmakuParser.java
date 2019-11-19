package com.okdanmaku.core.danmaku.parser;

import com.okdanmaku.core.danmaku.model.DanmakuBase;
import com.okdanmaku.core.danmaku.parser.android.AndroidFileSource;

import java.util.List;

/**
 * Created by yangya on 2019-11-19.
 */
public class BiliDanmakuParser extends DanmakuParserBase {

    @Override
    public List<DanmakuBase> parse(IDataSource dataSource) {
        if (dataSource != null) {
            AndroidFileSource source = (AndroidFileSource) dataSource;
        }

        return null;
    }

}
