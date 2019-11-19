package com.okdanmaku.core.danmaku.parser;

/**
 * Created by yangya on 2019-10-14.
 */
public interface IDataSource<T> {
    String SCHEME_HTTP = "http";
    String SCHEME_HTTPS = "https";
    String SCHEME_FILE = "file";

    public T data();

    public void release();
}
