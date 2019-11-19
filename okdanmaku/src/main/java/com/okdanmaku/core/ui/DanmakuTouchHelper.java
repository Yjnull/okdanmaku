package com.okdanmaku.core.ui;

import android.graphics.RectF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.okdanmaku.core.controller.IDanmakuView;

/**
 * Created by yangya on 2015/1/25.
 */
public class DanmakuTouchHelper {
    private final GestureDetector mTouchDelegate;
    private IDanmakuView mDanmakuView;
    private RectF mDanmakuBounds;
    private float mXOff;
    private float mYOff;

    private GestureDetector.OnGestureListener mOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return super.onDown(e);
        }
    };

    private DanmakuTouchHelper(IDanmakuView danmakuView) {
        this.mDanmakuView = danmakuView;
        this.mDanmakuBounds = new RectF();
        this.mTouchDelegate = new GestureDetector(((View) danmakuView).getContext(), mOnGestureListener);
    }

    public static DanmakuTouchHelper instance(IDanmakuView danmakuView) {
        return new DanmakuTouchHelper(danmakuView);
    }

    public boolean onTouchEvent(MotionEvent event) {
        return mTouchDelegate.onTouchEvent(event);
    }
}
