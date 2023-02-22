package com.jrgames.nummatch;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;


/**
 * Game manages all objects in the game and is responsible for updating all states
 * and renders all objects to the screen
 */
public class Game extends SurfaceView implements SurfaceHolder.Callback  {

    private GameLoop gameLoop;
    private GameBoard gameBoard;

    private static int TOP_BORDER    = 20;
    private static int BOTTOM_BORDER = 400;
    private static int LEFT_BORDER   = 40;
    private static int RIGHT_BORDER  = 40;
    private static int fieldsX       = 9;
    private int fieldsY;

    private int canvasWidth;
    private int canvasHeight;

    private List<Animation> ongoingAnimations = new ArrayList<>(20);

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

        gameBoard = new GameBoard(fieldsX, fieldsY, boardWidth,maxBoardHeight);

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
                //gameBoard.onTouchEvent((int)event.getX(), (int)event.getY());
                return true;
        }

        return super.onTouchEvent(event);
    }



    @Override
    public void draw(Canvas canvas) {
        //Log.d("Game()", "draw");
        super.draw(canvas);

        // draw game board
        gameBoard.draw(canvas, LEFT_BORDER, TOP_BORDER);

        // animations

    }

    public void update() {
        //Log.d("Game()", "update");
        // game updates
        if ( ongoingAnimations.isEmpty() ) {
            Animation a = new ExplodeAnimation(4, 4, 100);
            addAnimation(a);
        }
    }

    private void addAnimation(Animation a) {
        ongoingAnimations.add(a);
    }

    public void pause() {
        if (gameLoop!=null)
            gameLoop.stopLoop();
    }


}
