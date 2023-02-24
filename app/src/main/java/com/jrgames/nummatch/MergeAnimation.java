package com.jrgames.nummatch;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.List;
import java.util.Random;

public class MergeAnimation extends Animation {

    private static float fractionOfMovement = 0.1f;
    private static int divider = 5;

    public Cell c1;
    public Cell c2;

    private int val1;
    private int val2;



    private float pos1X;
    private float pos1Y;
    private float pos2X;
    private float pos2Y;

    private float textPos1X;
    private float textPos1Y;
    private float textPos2X;
    private float textPos2Y;

    private float angle1;
    private float angle2;

    float dx;
    float dy;
    float drot;

    private boolean phase2;
    Paint p;
    private float dividedBlockWidth;
    private float dividedBlockHeight;

    private float dividedBlocksX[][];
    private float dividedBlocksY[][];
    private float dividedBlocksDx[][];
    private float dividedBlocksDy[][];
    private float dAlpha;
    private float alpha;

    Random rand;

    public MergeAnimation(GameBoard gb, int dur, Cell c1, Cell c2,
                          int v1, int v2 ) {
        super(gb, dur);
        this.gb = gb;
        this.c1 = c1;
        this.c2 = c2;

        val1 = v1;
        val2 = v2;

        pos1X = gb.getCanvasXPos(c1.getX());
        pos1Y = gb.getCanvasYPos(c1.getY());
        pos2X = gb.getCanvasXPos(c2.getX());
        pos2Y = gb.getCanvasYPos(c2.getY());
        textPos1X = gb.getCanvasTextXPos(c1.getX());
        textPos1Y = gb.getCanvasTextYPos(c1.getY());
        textPos2X = gb.getCanvasTextXPos(c2.getX());
        textPos2Y = gb.getCanvasTextYPos(c2.getY());

        angle1 = 0;
        angle2 = 0;

        dx = (pos2X-pos1X) / 2 / (animationDuration*fractionOfMovement);
        dy = (pos2Y-pos1Y) / 2 / (animationDuration*fractionOfMovement);
        drot = 360/(animationDuration*fractionOfMovement);

        phase2 = false;

        p = new Paint(gb.highlightCellPaint);
        rand = new Random();

    }

    public void draw(Canvas c) {
        if ( !phase2 ) {
            c.save();
            c.rotate(angle1, pos1X+gb.getCellWidth()/2, pos1Y+gb.getCellHeight()/2);
            c.drawRect(pos1X,pos1Y, pos1X+gb.getCellWidth(), pos1Y+gb.getCellHeight(), gb.highlightCellPaint);
            c.drawText(Integer.toString(val1), textPos1X, textPos1Y, gb.digitPaint);
            c.restore();

            c.save();
            c.rotate(angle2, pos2X+gb.getCellWidth()/2, pos2Y+gb.getCellHeight()/2);
            c.drawRect(pos2X,pos2Y, pos2X+gb.getCellWidth(), pos2Y+gb.getCellHeight(), gb.highlightCellPaint);
            c.drawText(Integer.toString(val2),textPos2X, textPos2Y, gb.digitPaint);
            c.restore();
        } else {
            for (int x = 0; x < divider; x++) {
                for (int y = 0; y < divider; y++) {
                    p.setAlpha((int)alpha);
                    c.drawRect(dividedBlocksX[x][y],dividedBlocksY[x][y],
                            dividedBlocksX[x][y]+dividedBlockWidth,
                            dividedBlocksY[x][y]+dividedBlockHeight, p );
                }
            }
        }
    }

    // returns true if animation is completed
    public boolean update() {
        if ( !phase2 ) {
            pos1X += dx;
            pos1Y += dy;
            pos2X -= dx;
            pos2Y -= dy;
            textPos1X += dx;
            textPos1Y += dy;
            textPos2X -= dx;
            textPos2Y -= dy;
            angle1 += drot;
            angle2 -= drot;

            if (animationCycle >= animationDuration * fractionOfMovement) {
                // setup phase2, i.e. divide blocks
                phase2 = true;

                dividedBlockWidth = gb.getCellWidth() / divider;
                dividedBlockHeight = gb.getCellHeight() / divider;
                dAlpha = 255 / (animationDuration * (1 - fractionOfMovement));
                alpha = 255;

                dividedBlocksX = new float[divider][divider];
                dividedBlocksY = new float[divider][divider];
                dividedBlocksDx = new float[divider][divider];
                dividedBlocksDy = new float[divider][divider];

                for (int x = 0; x < divider; x++) {
                    for (int y = 0; y < divider; y++) {
                        dividedBlocksX[x][y] = pos1X + x * dividedBlockWidth;
                        dividedBlocksY[x][y] = pos1Y + y * dividedBlockHeight;

                        dividedBlocksDx[x][y] = rand.nextFloat();
                        dividedBlocksDy[x][y] = (float) Math.sqrt(2 - Math.pow(dividedBlocksDx[x][y],2.0));
                        if (x<divider/2 ) dividedBlocksDx[x][y] *= -1;
                        if (y<divider/2 ) dividedBlocksDy[x][y] *= -1;
                    }
                }
            }
        }
        else {
            for (int x = 0; x < divider; x++) {
                for (int y = 0; y < divider; y++) {
                    dividedBlocksX[x][y] += dividedBlocksDx[x][y];
                    dividedBlocksY[x][y] += dividedBlocksDy[x][y];
                }
            }
            alpha -= dAlpha;
        }


        animationCycle++;
        if (animationCycle >= animationDuration) return true;
        else return false;
    }
}
