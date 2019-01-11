package com.unbounded.video.constants;

/**
 * Created by sunhz on 2017/5/27.
 */

public enum tuning {
    AREA(1);
    tuning(int state)
    {
        this.state = state;
    }

    private final int state;

    public int getstate() {
        return state;
    }
}
