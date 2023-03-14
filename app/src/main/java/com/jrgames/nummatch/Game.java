package com.jrgames.nummatch;


import android.content.Context;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;


/**
 * Game manages all objects in the game and is responsible for updating all states
 * and renders all objects to the screen
 */
public class Game extends SurfaceView implements SurfaceHolder.Callback , DialogInterface.OnClickListener  {

    private final Context mContext;
    private GameLoop gameLoop;
    private GameBoard gameBoard;

    private final boolean usePreload = false;
    private int preload[][] = {
            {0, 0, 0, 3, 0, 0, 0, 0, 5 },
            {3, 5, 0, 0, 0, 0, 0, 0, 0 },
            {0, 0, 0, 0, 0, 0, 0, 0, 0 },
            {0, 0, 0, 0, 0, 0, 0, 0, 0 }
    };

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
    private List<Animation> newAnimations = new ArrayList<>(20);
    private Paint buttonPaint;
    private Paint pressedButtonPaint;
    private Paint buttonTextPaint;
    private int buttonTop;
    private int buttonBottom;

    private Rect resetButton;
    private Rect addButton;
    private Rect hintButton;

    private Rect buttons[];
    private int resetButtonHighlightCounter;
    private int addButtonHighlightCounter;
    private int hintButtonHighlightCounter;
    private final int buttonHighlightDuration = 10;

    private boolean gameOver;
    private boolean gameWon;
    private boolean gameBoardCreated;

    private int numHints;
    private int numAdds;
    private Paint circlePaint;
    private Paint circleTextPaint;
    private boolean hintGiven;

    private float rumbleOffset;
    public void setRumbleOffset(float rumbleOffset) {
        this.rumbleOffset = rumbleOffset;
    }

    private float shakerOffset;
    public void setShakerOffset(float shakerOffset) { this.shakerOffset = shakerOffset;};

    private int gamesWon;
    private int gamesLost;
    private int score;
    private int highScore;

    private Paint scorePaint;

    SharedPreferences prefs;


    public Game(Context context, SharedPreferences prefs) {
        super(context);
        this.mContext = context;
        this.prefs = prefs;

        //getSurfaceHolder and add callback method
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        gameBoardCreated = false;

        this.highScore = prefs.getInt("highscore", 0);
        this.gamesWon  = prefs.getInt("gameswon", 0);
        this.gamesLost = prefs.getInt("gameslost", 0);

        setFocusable( true );
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        Log.d("Game()", "surfaceCreated()");
        if (!gameBoardCreated) {
            gameBoardCreated = true;
            Rect frame = holder.getSurfaceFrame();
            canvasWidth = frame.right - frame.left;
            canvasHeight = frame.bottom - frame.top;

            int boardWidth = canvasWidth - LEFT_BORDER - RIGHT_BORDER;
            int maxBoardHeight = canvasHeight - TOP_BORDER - BOTTOM_BORDER;
            fieldsY = maxBoardHeight / (boardWidth / fieldsX);

            gameBoard = new GameBoard(this, fieldsX, fieldsY, boardWidth);

            buttonPaint = new Paint();
            buttonPaint.setColor(Color.rgb(200, 200, 200));

            pressedButtonPaint = new Paint();
            pressedButtonPaint.setColor(Color.rgb(100, 100, 100));

            buttonTextPaint = new Paint();
            buttonTextPaint.setColor(Color.rgb(0, 0, 250));
            buttonTextPaint.setTextAlign(Paint.Align.CENTER);
            buttonTextPaint.setTextSize(120);

            buttonTop = gameBoard.getPhysicalHeight() + 2 * TOP_BORDER;
            buttonBottom = buttonTop + buttonHeight;

            int top = buttonTop;

            int left = canvasWidth * 1 / 4 - buttonHeight / 2;
            resetButton = new Rect(left, top, left + buttonHeight, top + buttonHeight);
            left = canvasWidth * 2 / 4 - 75;
            addButton = new Rect(left, top, left + 150, top + 150);
            left = canvasWidth * 3 / 4 - 75;
            hintButton = new Rect(left, top, left + 150, top + 150);

            circlePaint = new Paint();
            circlePaint.setColor(Color.rgb(222,0,0));

            circleTextPaint = new Paint();
            circleTextPaint.setTextSize(gameBoard.getCellWidth()/3);
            circleTextPaint.setColor(Color.rgb(222,222,222));

            scorePaint = new Paint();
            scorePaint.setTextSize(50);
            scorePaint.setColor(Color.WHITE);

            init();
        }



        gameLoop = new GameLoop(this, holder);
        gameLoop.startLoop();
    }

    private void init() {
        gameOver = false;
        gameWon = false;
        numHints = 3;
        numAdds  = 3;

        score = 0;

        gameBoard.init(4);
        if ( usePreload ) {
            for (int y = 0; y < preload.length; y++) {
                for (int x = 0; x < gameBoard.getWidth(); x++) {
                    gameBoard.content[x][y] = preload[y][x];
                }
            }
        }
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

                    if (y<gameBoard.getPhysicalHeight()) hintGiven = false;

                    gameBoard.onTouchEvent(x-LEFT_BORDER, y-TOP_BORDER);

                    if (addButton.left <= x && x <= addButton.right &&
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
        if (numHints != 0) {

            Cell c1 = new Cell();
            Cell c2 = new Cell();
            boolean hintAvailable = gameBoard.findCombination(c1, c2);

            if (hintAvailable) {
                if (!hintGiven) numHints--;
                hintGiven = true;
                addAnimation(new HighlightTwoCellsAnimation(gameBoard, 180, c1, c2));
            } else {
                addButtonHighlightCounter = buttonHighlightDuration;
                addAnimation( new RumbleAnimation(gameBoard, 60, 30, true));
            }
        }
    }

    private void addButtonPressed() {
        if ( numAdds != 0) {
            numAdds--;
            gameBoard.addNewCells();
        }
    }

    private void resetButtonPressed() {
        if (gameOver) {
            init();
        } else {
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this.mContext);
            //lgAlert.setMessage(resp);
            dlgAlert.setTitle("Do you really want to end the game and restart?");
            dlgAlert.setPositiveButton("YES", this);
            dlgAlert.setNegativeButton("NOPE", this);
            dlgAlert.setCancelable(true);

            dlgAlert.create().show();
        }
    }



    public int getNumHints() {
        return numHints;
    }

    public int getNumAdds() {
        return numAdds;
    }




    @Override
    public void draw(Canvas canvas) {

        super.draw(canvas);

        // draw game board
        gameBoard.draw(canvas, LEFT_BORDER, TOP_BORDER, (int)rumbleOffset, (int)shakerOffset);

        // animations
        synchronized (ongoingAnimations) {
            ongoingAnimations.forEach((a) -> {
                a.draw(canvas);
            });
        }
        drawButtons(canvas);
        drawScore(canvas);

    }

    private void drawScore(Canvas canvas) {
        canvas.drawText("score: "+Integer.toString(score), 2*RIGHT_BORDER, resetButton.bottom+100, scorePaint);
        canvas.drawText("high: "+Integer.toString(highScore), canvasWidth/2, resetButton.bottom+100, scorePaint);
        canvas.drawText("won: "+Integer.toString(gamesWon), 2*RIGHT_BORDER, resetButton.bottom+200, scorePaint);
        canvas.drawText("lost: "+Integer.toString(gamesLost), canvasWidth/2, resetButton.bottom+200, scorePaint);
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
        if (addButtonHighlightCounter>0) {
            addButtonHighlightCounter--;
            p = pressedButtonPaint;
        }
        c.drawRect(addButton, p);
        if (getNumAdds()>0) {
            float x = addButton.right-gameBoard.getCellWidth()/5;
            float y = addButton.top+gameBoard.getCellHeight()/5;
            c.drawCircle(x, y , gameBoard.getCellHeight()/5 , circlePaint);
            c.drawText(Integer.toString(getNumAdds()), x-gameBoard.getCellWidth()/9,y+gameBoard.getCellHeight()/9,circleTextPaint);
        }
        c.drawText("\u2795",addButton.left+buttonHeight/2, addButton.top+115, buttonTextPaint );

        p = buttonPaint;
        if (hintButtonHighlightCounter>0) {
            hintButtonHighlightCounter--;
            p = pressedButtonPaint;
        }
        c.drawRect(hintButton, p);
        c.drawText("\u261d",hintButton.left+buttonHeight/2, hintButton.top+115, buttonTextPaint );
        if (getNumHints()>0) {
            float x = hintButton.right-gameBoard.getCellWidth()/5;
            float y = hintButton.top+gameBoard.getCellHeight()/5;
            c.drawCircle(x, y , gameBoard.getCellHeight()/5 , circlePaint);
            c.drawText(Integer.toString(getNumHints()), x-gameBoard.getCellWidth()/9,y+gameBoard.getCellHeight()/9,circleTextPaint);
        }

    }

    public void update() {

        // animation updates
        if ( !newAnimations.isEmpty()) {
            // only one dropLineAnimation at a time
            int idx = 0;
            while ( idx < newAnimations.size() ) {
                Animation a = newAnimations.get(idx);
                if ( !(a instanceof DropLineAnimation) || !dropLineAnimationRunning()) {
                    synchronized (ongoingAnimations) {
                        ongoingAnimations.add(a);
                    }
                    newAnimations.remove(idx);
                } else {
                    idx++;
                }
            }
        }

        synchronized (ongoingAnimations) {
            ongoingAnimations.removeIf(a -> a.update());
        }

        // check for game win
        boolean dropRunning = dropLineAnimationRunning();
        boolean insertionRunning = insertionAnimationRunning();
        boolean mergeRunning = mergeAnimationRunning();
        boolean animationScheduled = isAnimationScheduled();
        boolean relevantAnimationRunning = dropRunning || insertionRunning || mergeRunning || animationScheduled;
        if (!gameOver && !relevantAnimationRunning) {
            if (gameBoard.isBoardEmpty()) {
                setGameOver(true);
            } else if (!gameBoard.hasCombination() && numAdds==0) {
                boolean hc = gameBoard.hasCombination();
                setGameOver(false);
            }
        }
    }

    private boolean isAnimationScheduled() {
        return (newAnimations.size()!=0);
    }

    private boolean insertionAnimationRunning() {
        return !ongoingAnimations.stream().allMatch( a -> !(a instanceof InsertionAnimation));
    }

    private boolean mergeAnimationRunning() {
        return !ongoingAnimations.stream().allMatch( a -> !(a instanceof MergeAnimation));
    }

    private boolean dropLineAnimationRunning() {
        return !ongoingAnimations.stream().allMatch( a -> !(a instanceof DropLineAnimation));
    }

    public void addAnimation(Animation a) {
        synchronized (newAnimations) {
            newAnimations.add(a);
        }
    }

    public void pause() {
        if (gameLoop!=null)
            gameLoop.stopLoop();
    }


    public void setGameOver( boolean win ) {
        gameOver = true;
        gameWon = win;
        if ( win ) {
            if (score > highScore) {
                highScore = score;
            }
            gamesWon++;
        }
        else gamesLost++;
        addAnimation( new GameOverAnimation(gameBoard, 100, win) );

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("highscore", highScore);
        editor.putInt("gameswon", gamesWon);
        editor.putInt("gameslost", gamesLost);
        editor.apply();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void increaseScore(int increment) {
        score += increment;

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which== DialogInterface.BUTTON_POSITIVE) {
            init();
        }
    }
}
