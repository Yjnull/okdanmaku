package com.okdanmaku.core.controller;

/**
 * Created by yangya on 2019-10-15.
 */
public class UpdateThread extends Thread {

    private volatile boolean isQuited;

    public UpdateThread(String name) {
        super(name);
    }

    public void quit() {
        isQuited = true;
    }

    public boolean isQuited() {
        return isQuited;
    }

}
