package com.jrgames.nummatch;


import android.graphics.Canvas;

public abstract class Animation {
    private int animationDuration;
    private int animationCycle;


    public Animation(int dur) {
        animationDuration = dur;
        animationCycle = 0;
    }

    public abstract void draw(Canvas c);


}
