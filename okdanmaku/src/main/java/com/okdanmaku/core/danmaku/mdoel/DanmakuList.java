package com.okdanmaku.core.danmaku.mdoel;

/**
 * Created by yangya on 2019-10-14.
 */
public class DanmakuList implements IDanmakuCollection {

    @Override
    public boolean add(BaseDanmaku item) {
        return false;
    }

    @Override
    public BaseDanmaku last() {
        return null;
    }

    @Override
    public void forEach() {

    }
}