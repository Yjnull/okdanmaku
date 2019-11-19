package com.okdanmaku.core.danmaku.loader;

/**
 * Created by yangya on 2019-10-14.
 * 当数据无法处理时抛出
 */
public class IllegalDataException extends Exception {
    private static final long serialVersionUID = -5769819486653048187L;

    public IllegalDataException() {
    }

    public IllegalDataException(String message) {
        super(message);
    }

    public IllegalDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalDataException(Throwable cause) {
        super(cause);
    }
}
