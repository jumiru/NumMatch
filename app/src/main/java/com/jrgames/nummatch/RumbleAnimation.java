package com.jrgames.nummatch;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class RumbleAnimation extends Animation {

    private double t;
    private double amplitude;
    private double offset;
    private double damping;
    private double freq;
    private double dT;

    private boolean shake;

    public RumbleAnimation(GameBoard gb, int dur, int amplitude, boolean shake) {
        super(gb, dur);
        this.shake = shake;
        this.amplitude = (float) amplitude;
        damping = 0.2;
        freq = 1;
        dT = 8.0*Math.PI/((double)animationDuration);
    }

    @Override
    public void draw(Canvas c) {
        Paint p = new Paint();
        p.setTextSize(80);
        p.setColor(Color.BLUE);

        /*c.drawText( "ampli_max="+Double.toString(amplitude), 100, 1000, p);
        c.drawText( "damping="+Double.toString(damping), 100, 1100, p);
        c.drawText( "freq="+Double.toString(freq), 100, 1200, p);
        c.drawText( "t="+Double.toString(Math.round(t*100.0)/100.0), 100, 1300, p);
        c.drawText( "dT="+Double.toString(Math.round(dT*10000.0)/10000.0), 100, 1400, p);
        c.drawText( "sin(t*1.0/freq)="+Double.toString(Math.round(10.0*Math.sin(t/freq))/10.0), 100, 1500, p);
        c.drawText( "exp((damping*t)="+Double.toString(Math.round(10.0*Math.exp((-damping*t)))/10.0), 100, 1600, p);
        c.drawText( "offset="+Double.toString(Math.round(offset*100.0d)/100.0d), 100, 1700, p);
        */

    }

    @Override
    public boolean update() {

        offset = amplitude*Math.exp(-damping*t)*Math.sin(t/freq);
        if (shake) gb.game.setShakerOffset((float)offset);
        else gb.game.setRumbleOffset((float)offset);
        t += dT;

        animationCycle++;
        if (animationCycle >= animationDuration) {
            gb.game.setRumbleOffset(0);
            return true;
        }
        else return false;

    }

}
