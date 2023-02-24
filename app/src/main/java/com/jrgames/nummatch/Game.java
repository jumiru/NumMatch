package com.jrgames.nummatch;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;


/**
 * Game manages all objects in the game and is responsible for updating all states
 * and renders all objects to the screen
 */
public class Game extends SurfaceView implements SurfaceHolder.Callback  {

    private GameLoop gameLoop;
    private GameBoard gameBoard;

    public final int TOP_BORDER    = 20;
    public final int BOTTOM_BORDER = 400;
    public final int LEFT_BORDER   = 40;
    public final int RIGHT_BORDER  = 40;

    public final int buttonHeight = 150;

    private static int fieldsX       = 9;
    private int fieldsY;

    private int canvasWidth;
    private int canvasHeight;

    private List<Animation> ongoingAnimations = new ArrayList<>(20);
    private Paint buttonPaint;
    private Paint pressedButtonPaint;
    private Paint buttonTextPaint;
    private int buttonTop;
    private int buttonBottom;

    private Rect resetButton;
    private Rect undoButton;
    private Rect addButton;
    private Rect hintButton;

    private Rect buttons[];
    private int resetButtonHighlightCounter;
    private int undoButtonHighlightCounter;
    private int addButtonHighlightCounter;
    private int hintButtonHighlightCounter;
    private final int buttonHighlightDuration = 10;

    private boolean gameOver;
    private boolean gameWon;



    public Game(Context context, SharedPreferences prefs) {
        super(context);

        //getSurfaceHolder and add callback method
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        setFocusable( true );
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        Log.d("Game()", "surfaceCreated()");
        Rect frame = holder.getSurfaceFrame();
        canvasWidth = frame.right - frame.left;
        canvasHeight = frame.bottom - frame.top;

        int boardWidth = canvasWidth - LEFT_BORDER - RIGHT_BORDER;
        int maxBoardHeight = canvasHeight -TOP_BORDER - BOTTOM_BORDER;
        fieldsY = maxBoardHeight/(boardWidth/fieldsX);

        gameBoard = new GameBoard(this, fieldsX, fieldsY, boardWidth );

        buttonPaint = new Paint();
        buttonPaint.setColor(Color.rgb(200,200,200));

        pressedButtonPaint = new Paint();
        pressedButtonPaint.setColor(Color.rgb(100,100,100));

        buttonTextPaint = new Paint();
        buttonTextPaint.setColor(Color.rgb(0,0,250));
        buttonTextPaint.setTextAlign(Paint.Align.CENTER);
        buttonTextPaint.setTextSize(120);

        buttonTop = gameBoard.getBoardHeight()+2*TOP_BORDER;
        buttonBottom = buttonTop+buttonHeight;

        int top  = buttonTop;

        int left = canvasWidth*1/5-buttonHeight/2;
        resetButton = new Rect(left,top,left+buttonHeight,top+buttonHeight);
        left = canvasWidth*2/5-75;
        undoButton = new Rect(left,top,left+150,top+150);
        left = canvasWidth*3/5-75;
        addButton = new Rect(left,top,left+150,top+150);
        left = canvasWidth*4/5-75;
        hintButton = new Rect(left,top,left+150,top+150);

        gameOver = false;
        gameWon = false;

        gameLoop = new GameLoop(this, holder);
        gameLoop.startLoop();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        Log.d("Game()", "surfaceChanged()");
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        Log.d("Game()", "surfaceDestroyed()");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int x = (int)event.getX();
                int y = (int)event.getY();
                if (!gameOver) {
                    gameBoard.onTouchEvent(x-LEFT_BORDER, y-TOP_BORDER);

                    if (undoButton.left <= x && x <= undoButton.right &&
                            undoButton.top <= y && y <= undoButton.bottom) {
                        undoButtonHighlightCounter = buttonHighlightDuration;
                        undoButtonPressed();
                    } else if (addButton.left <= x && x <= addButton.right &&
                            addButton.top <= y && y <= addButton.bottom) {
                        addButtonHighlightCounter = buttonHighlightDuration;
                        addButtonPressed();
                    } else if (hintButton.left <= x && x <= hintButton.right &&
                            hintButton.top <= y && y <= hintButton.bottom) {
                        hintButtonHighlightCounter = buttonHighlightDuration;
                        hintButtonPressed();
                    }

                }
                if (resetButton.left <= x && x <= resetButton.right &&
                        resetButton.top <= y && y <= resetButton.bottom) {
                    resetButtonHighlightCounter = buttonHighlightDuration;
                    resetButtonPressed();
                }
                return true;
        }

        return super.onTouchEvent(event);
    }

    private void hintButtonPressed() {

    }

    private void addButtonPressed() {
        gameBoard.addNewCells();
    }

    private void undoButtonPressed() {
    }

    private void resetButtonPressed() {
        gameOver = false;
        gameWon = false;
        gameBoard.init();
    }


    @Override
    public void draw(Canvas canvas) {

        super.draw(canvas);

        // draw game board
        gameBoard.draw(canvas, LEFT_BORDER, TOP_BORDER);

        // animations
        synchronized (ongoingAnimations) {
            ongoingAnimations.forEach((a) -> {
                a.draw(canvas);
            });
        }
        drawButtons(canvas);

    }

    private void drawButtons(Canvas c) {

        Paint p = buttonPaint;
        if (resetButtonHighlightCounter>0) {
            resetButtonHighlightCounter--;
            p = pressedButtonPaint;
        }
        c.drawRect( resetButton, p);
        c.drawText("\ud83d\udd04", resetButton.left+buttonHeight/2, resetButton.top+115, buttonTextPaint);

        p = buttonPaint;
        if (undoButtonHighlightCounter>0) {
            undoButtonHighlightCounter--;
            p = pressedButtonPaint;
        }
        c.drawRect(undoButton, p);
        c.drawText("\u21BA",undoButton.left+buttonHeight/2, undoButton.top+115, buttonTextPaint );

        p = buttonPaint;
        if (addButtonHighlightCounter>0) {
            addButtonHighlightCounter--;
            p = pressedButtonPaint;
        }
        c.drawRect(addButton, p);
        c.drawText("\u2795",addButton.left+buttonHeight/2, addButton.top+115, buttonTextPaint );

        p = buttonPaint;
        if (hintButtonHighlightCounter>0) {
            hintButtonHighlightCounter--;
            p = pressedButtonPaint;
        }
        c.drawRect(hintButton, p);
        c.drawText("\u261d",hintButton.left+buttonHeight/2, hintButton.top+115, buttonTextPaint );
    }

    public void update() {

        // game updates

        // animation updates
        synchronized (ongoingAnimations) {
            ongoingAnimations.removeIf(a -> a.update());
        }

        // check for empty lines
        gameBoard.checkForEmptyLines();

        // check for game win
        if (!gameOver && gameBoard.isBoardEmpty()) {
            setGameOver(true);
        }
    }

    public void addAnimation(Animation a) {
        synchronized (ongoingAnimations) {
            ongoingAnimations.add(a);
        }
    }

    public void pause() {
        if (gameLoop!=null)
            gameLoop.stopLoop();
    }


    public void setGameOver( boolean win ) {
        gameOver = true;
        gameWon = win;
        addAnimation( new GameOverAnimation(gameBoard, 100, win) );
    }

    public boolean isGameOver() {
        return gameOver;
    }
}
