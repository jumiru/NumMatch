package com.jrgames.nummatch;

import android.graphics.Canvas;

public class ExplodeAnimation extends Animation {

    public int blockPositionX;
    public int blockPositionY;

    public ExplodeAnimation(int x, int y, int dur) {
        super(dur);
        blockPositionX = x;
        blockPositionY = y;

    }

    public void draw(Canvas c) {

    }
}
