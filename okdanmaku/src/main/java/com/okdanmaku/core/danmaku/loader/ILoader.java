package com.okdanmaku.core.danmaku.loader;

import com.okdanmaku.core.danmaku.parser.IDataSource;

import java.io.InputStream;

/**
 * Created by yangya on 2019-10-14.
 */
public interface ILoader {
    /**
     * @return data source
     */
    IDataSource<?> getDataSource();

    /**
     * @param uri 弹幕文件地址(http:// https:// file://)
     */
    void load(String uri) throws IllegalDataException;

    /**
     *
     * @param in stream from Internet or local file
     */
    void load(InputStream in) throws IllegalDataException;
}
