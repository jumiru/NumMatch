package com.jrgames.nummatch;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.Random;

public class GameOverAnimation extends Animation {

    private boolean curtainPhase;
    private Rect curtain;
    private Paint curtainPaint;
    private float dY;
    private float bottom;

    private int scrollPos;
    private Paint textPaint;
    private float alpha;
    private float alphaSineIncrement;
    private float alphaStart;
    private float alphaCounter;
    private float alphaAmplitude;

    Random rand;
    private int scrollY;
    private String scrollText;
    boolean gameWon;


    public GameOverAnimation(GameBoard gb, int dur, boolean gameWon ) {
        super(gb,dur);
        curtainPhase = true;
        curtainPaint = new Paint();
        curtainPaint.setColor(Color.rgb(30,30,30));

        alphaCounter = 0;
        alpha = 0;
        alphaSineIncrement = 0.05f;
        alphaStart = 100;
        alphaAmplitude = 70;
        curtainPaint.setAlpha( (int)alphaStart );
        textPaint = new Paint();
        textPaint.setTextSize(gb.getCellHeight());
        textPaint.setFakeBoldText(true);
        if (gameWon) {
            textPaint.setColor(Color.rgb(22, 255, 88));
            scrollText = "WON!!!!!!!";
        } else {
            textPaint.setColor(Color.rgb(222, 44, 22));
            scrollText = "Game Over !!!";
        }

        dY = (float)gb.getPhysicalHeight() / dur;
        bottom = (float)gb.game.TOP_BORDER;

        scrollPos = -500;
        scrollY = (int)gb.game.TOP_BORDER+gb.getPhysicalWidth()/2;

        curtain = new Rect( gb.game.LEFT_BORDER, gb.game.TOP_BORDER,
                gb.game.LEFT_BORDER+gb.getPhysicalWidth(), gb.game.TOP_BORDER);

        this.gameWon = gameWon;

        rand = new Random();
    }

    @Override
    public void draw(Canvas c) {
        c.drawRect(curtain, curtainPaint);
        if ( !curtainPhase ) {
            c.drawText(scrollText, scrollPos, scrollY , textPaint);
            curtainPaint.setAlpha((int)alpha);
        }
    };


    @Override
    public boolean update() {
        if (curtainPhase) {
            bottom += dY;
            curtain.bottom = (int)bottom;
            if (bottom>=gb.getPhysicalHeight()+gb.game.TOP_BORDER) {
                curtainPhase = false;
                bottom = gb.getPhysicalHeight()+gb.getPhysicalHeight();
            }
        } else {
            scrollPos+=5;
            alphaCounter += alphaSineIncrement;
            alpha = alphaStart+alphaAmplitude*(float) Math.sin((double)alphaCounter);
            if (scrollPos >= gb.getPhysicalWidth()+gb.game.LEFT_BORDER+gb.game.RIGHT_BORDER) {
                scrollPos = -500;
                scrollY = rand.nextInt(gb.getPhysicalHeight())+gb.game.TOP_BORDER;
            }
            if (gameWon && (animationCycle % 60) == 0) {
                int x = rand.nextInt(gb.getWidth());
                int y = rand.nextInt(gb.getHeight());
                int v = gb.content[x][y];
                Cell c = new Cell(x,y);
                gb.game.addAnimation(new MergeAnimation(gb, 240, 10,  c, c, v, v));
            }
        }

        animationCycle++;
        if (!gb.game.isGameOver()) return true;
        return false;
    };

}
