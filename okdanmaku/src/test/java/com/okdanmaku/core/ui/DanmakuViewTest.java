package com.okdanmaku.core.ui;

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

/**
 * Created by yangya on 2019-10-15.
 */
public class DanmakuViewTest {

    @Test
    public void fps() {
        fps2();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(0, fps2(), 0.1);
    }

    private LinkedList<Long> mDrawTimes;
    @Before
    public void init() {
        mDrawTimes = new LinkedList<>();
    }

    private float fps2() {
        long curTime = System.currentTimeMillis();
        mDrawTimes.addLast(curTime);
        Long first = mDrawTimes.peekFirst();
        if (first == null) return 0.0f;
        float dTime = curTime - first;
        int frames = mDrawTimes.size();
        if (frames > 50) {
            mDrawTimes.removeFirst();
        }
        return dTime > 0 ? frames * 1000 / dTime : 0.0f;
    }
}