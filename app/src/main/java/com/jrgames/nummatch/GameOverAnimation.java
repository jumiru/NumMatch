package com.jrgames.nummatch;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import androidx.annotation.Nullable;

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

        dY = gb.getBoardHeight() / dur;
        bottom = (float)gb.game.TOP_BORDER;

        scrollPos = -500;
        scrollY = (int)gb.game.TOP_BORDER+gb.getBoardWidth()/2;

        curtain = new Rect( gb.game.LEFT_BORDER, gb.game.TOP_BORDER,
                gb.game.LEFT_BORDER+gb.getBoardWidth(), gb.game.TOP_BORDER);

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
            if (bottom>=gb.getBoardHeight()+gb.game.TOP_BORDER) {
                curtainPhase = false;
                bottom = gb.getBoardHeight()+gb.getBoardHeight();
            }
        } else {
            scrollPos+=5;
            alphaCounter += alphaSineIncrement;
            alpha = alphaStart+alphaAmplitude*(float) Math.sin((double)alphaCounter);
            if (scrollPos >= gb.getBoardWidth()+gb.game.LEFT_BORDER+gb.game.RIGHT_BORDER) {
                scrollPos = -500;
                scrollY = rand.nextInt(gb.getBoardHeight())+gb.game.TOP_BORDER;
            }
        }

        animationCycle++;
        if (!gb.game.isGameOver()) return true;
        return false;
    };

}
