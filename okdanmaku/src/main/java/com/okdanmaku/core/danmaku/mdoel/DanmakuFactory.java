package com.okdanmaku.core.danmaku.mdoel;

/**
 * Created by yangya on 2019-10-18.
 */
public class DanmakuFactory {
    public final static float OLD_BILI_PLAYER_WIDTH = 539;

    public final static float BILI_PLAYER_WIDTH = 682;

    public final static float OLD_BILI_PLAYER_HEIGHT = 385;

    public final static float BILI_PLAYER_HEIGHT = 438;

    public final static long COMMON_DANMAKU_DURATION = 3800; // B站原始分辨率下弹幕存活时间

    public final static int DANMAKU_MEDIUM_TEXTSIZE = 25;

    public final static long MIN_DANMAKU_DURATION = 4000;

    public final static long MAX_DANMAKU_DURATION_HIGH_DENSITY = 9000;

    public int CURRENT_DISP_WIDTH = 0, CURRENT_DISP_HEIGHT = 0;

    //private SpecialDanmaku.ScaleFactor mScaleFactor = null;
    public long REAL_DANMAKU_DURATION = COMMON_DANMAKU_DURATION;
    public long MAX_DANMAKU_DURATION = MIN_DANMAKU_DURATION;
    public Duration MAX_Duration_Scroll_Danmaku;
    public Duration MAX_Duration_Fix_Danmaku;
    public Duration MAX_Duration_Special_Danmaku;
    public IDisplayer sLastDisp;
    private float CURRENT_DISP_SIZE_FACTOR = 1.0f;
    private DanmakuConfig sLastConfig;

    protected DanmakuFactory() {

    }

    public static DanmakuFactory create() {
        return new DanmakuFactory();
    }
}
