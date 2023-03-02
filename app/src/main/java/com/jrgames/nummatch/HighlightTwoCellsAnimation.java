package com.jrgames.nummatch;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class HighlightTwoCellsAnimation extends Animation {

    private Rect r1;
    private Rect r2;
    private int v1;
    private int v2;
    private float s1X;
    private float s1Y;
    private float s2X;
    private float s2Y;


    private Paint p;

    private float alpha;
    private float dAlpha;

    private boolean phase1;

    public HighlightTwoCellsAnimation(GameBoard gb, int dur, Cell c1, Cell c2) {
        super(gb, dur);

        r1 = new Rect(
                (int)gb.getCanvasXPos(c1.getX()),
                (int)gb.getCanvasYPos(c1.getY()),
                (int)(gb.getCanvasXPos(c1.getX()) + gb.getCellWidth()),
                (int)(gb.getCanvasYPos(c1.getY()) + gb.getCellHeight())
        );
        r2 = new Rect(
                (int)gb.getCanvasXPos(c2.getX()),
                (int)gb.getCanvasYPos(c2.getY()),
                (int)(gb.getCanvasXPos(c2.getX()) + gb.getCellWidth()),
                (int)(gb.getCanvasYPos(c2.getY()) + gb.getCellHeight())
        );

        s1X = gb.getCanvasTextXPos(c1.getX());
        s1Y = gb.getCanvasTextXPos(c1.getY());
        s2X = gb.getCanvasTextXPos(c2.getX());
        s2Y = gb.getCanvasTextXPos(c2.getY());

        v1 = gb.content[c1.getX()][c1.getY()];
        v2 = gb.content[c2.getX()][c2.getY()];

        alpha = 0;
        dAlpha = 255.0f/(float)dur/2.0f;

        p = new Paint();
        p.setColor(Color.rgb(200,0,0));
        p.setAlpha((int)alpha);

        phase1 = true;
    }

    @Override
    public void draw(Canvas c) {
        p.setAlpha((int)alpha);
        c.drawRect(r1, p);
        c.drawRect(r2, p);
        //c.drawText(Integer.toString(v1), s1X, s1Y, gb.digitPaint);
        //c.drawText(Integer.toString(v2), s2X, s2Y, gb.digitPaint);
    }

    @Override
    public boolean update() {

        alpha += dAlpha;

        if (phase1) {
            if ( alpha > 255) {
                alpha = 255;
                dAlpha = -dAlpha;
                phase1 = false;
            }
        }

        animationCycle++;
        if (animationCycle >= animationDuration) return true;
        else return false;

    }
}
