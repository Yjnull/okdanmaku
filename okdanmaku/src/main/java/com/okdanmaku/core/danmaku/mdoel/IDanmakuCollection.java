package com.okdanmaku.core.danmaku.mdoel;

/**
 * Created by yangya on 2019-10-14.
 * 弹幕集合约束
 */
public interface IDanmakuCollection {

    boolean add(BaseDanmaku item);

    BaseDanmaku last();

    void forEach();

}
