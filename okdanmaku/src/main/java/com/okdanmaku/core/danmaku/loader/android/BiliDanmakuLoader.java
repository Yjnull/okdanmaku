package com.okdanmaku.core.danmaku.loader.android;

import android.net.Uri;

import com.okdanmaku.core.danmaku.loader.ILoader;
import com.okdanmaku.core.danmaku.parser.IDataSource;
import com.okdanmaku.core.danmaku.parser.android.AndroidFileSource;

/**
 * Created by yangya on 2019-11-20.
 */
public class BiliDanmakuLoader implements ILoader {

    private static BiliDanmakuLoader _instance;

    public static BiliDanmakuLoader instance() {
        if (_instance == null) {
            _instance = new BiliDanmakuLoader();
        }
        return _instance;
    }

    private Uri uri;

    private BiliDanmakuLoader() { }

    /**
     * @param uri 弹幕文件地址(http:// file://)
     * @return 数据源
     */
    @Override
    public IDataSource load(String uri) {
        try {
            this.uri = Uri.parse(uri);
            return new AndroidFileSource(this.uri);
        } catch (Exception ignore) {

        }

        return null;
    }

}
