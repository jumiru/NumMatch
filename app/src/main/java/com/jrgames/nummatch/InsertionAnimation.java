package com.jrgames.nummatch;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class InsertionAnimation extends Animation {
    private final Rect rect;
    private Paint paint;
    private final int val;
    private final Cell target;

    private boolean speedUp;
    private float speed = 20.0f;
    private float initialSpeed = 20.0f;
    private float acc = 1.1f;
    private float dAlpha;
    private float alpha;
    private boolean dissolvePhase;

    private int initDly;


    public InsertionAnimation(GameBoard gb, int dur, int initDly, Rect rect, int val, Cell target) {
        super(gb, dur );

        this.rect = rect;
        this.val = val;
        this.target = target;
        this.initDly = initDly;
        dissolvePhase = false;
        dAlpha = 255.0f/animationDuration;

        paint = new Paint(gb.highlightCellPaint);

        speed = initialSpeed;
        speedUp = true;

    }

    @Override
    public void draw(Canvas c) {
        if (initDly<=0) {
            c.drawRect(rect, paint);
        }
    }

    @Override
    public boolean update() {
        if (initDly > 0) {
            initDly--;
        } else if (!dissolvePhase) {
            rect.left -= (int)speed;
            rect.right -= (int)speed;

            if ( speedUp ) {
                speed *= acc;

                if ( speed > 3*initialSpeed ) {
                    speedUp = false;
                }
            } else {
                if ( speed > initialSpeed/2.0f) {
                    speed *= (1.0f / acc);
                }
            }

            if (rect.left <= (int)gb.getCanvasXPos(target.getX())) {
                dissolvePhase = true;
                gb.content[target.getX()][target.getY()] = val;
                alpha = 255.0f;
                rect.left = (int)gb.getCanvasXPos(target.getX());
                rect.right = rect.left+(int)gb.getCellWidth();
            }
        } else {
            alpha -= dAlpha;
            paint.setAlpha((int)alpha);
            animationCycle++;
            if ( animationCycle>=animationDuration) {
                return true;
            }
        }
        return false;
    }
}
