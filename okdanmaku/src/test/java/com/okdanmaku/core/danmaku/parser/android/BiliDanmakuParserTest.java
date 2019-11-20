package com.okdanmaku.core.danmaku.parser.android;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by yangya on 2019-11-20.
 */
public class BiliDanmakuParserTest {

    private String[] values;

    @Before
    public void setUp() throws Exception {
        String pValue = "83.508003234863,1,25,16777215,1372902409,0,D078d359,249041505";
        values = pValue.split(",");
    }

    @After
    public void tearDown() throws Exception {
        System.out.println(values[0]);
    }

    @Test
    public void parse() {
        float f = Float.parseFloat(values[0]);
        long time = (long) (f * 1000); // 出现时间
        assertEquals(83508, time);
    }
}