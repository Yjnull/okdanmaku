package com.okdanmaku.core.danmaku.mdoel;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by yangya on 2019-10-14.
 */
public class DanmakuConfig {
    // --- 配置字段 --------------------------------------------------------------------------------
    @UpdateMethod
    public int updateMethod = UpdateMethod.DEFAULT;
    public float scaleTextSize = 1.0f;
    // ---------------------------------------------------------------------------------------------

    private AbsDisplayer mDisplayer = new AndroidDisplayer();
    public GlobalFlagValues mGlobalFlagValues = new GlobalFlagValues();
    public DanmakuFactory mDanmakuFactory = DanmakuFactory.create();

    public AbsDisplayer getDisplayer() {
        return mDisplayer;
    }

    public boolean isUpdateInDefault() {
        return updateMethod == UpdateMethod.DEFAULT;
    }
    public boolean isUpdateInSeparate() {
        return updateMethod == UpdateMethod.DFM_UPDATE;
    }
    public boolean isUpdateInSelfDriver() {
        return updateMethod == UpdateMethod.SELF_DRIVER;
    }

    /**
     * DEFAULT: 默认Choreographer驱动DrawHandler线程刷新 <br />
     * DFM_UPDATE: "DFM Update"单独线程刷新 <br />
     * SELF_DRIVER: DrawHandler线程自驱动刷新
     * <p>
     * Note: 在系统{@link android.os.Build.VERSION_CODES#JELLY_BEAN}以下, DEFAULT方式会被SELF_DRIVER方式代替
     */
    @IntDef({UpdateMethod.DEFAULT, UpdateMethod.DFM_UPDATE, UpdateMethod.SELF_DRIVER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface UpdateMethod {
        int DEFAULT = 0;
        int DFM_UPDATE = 1;
        int SELF_DRIVER = 2;
    }
}
