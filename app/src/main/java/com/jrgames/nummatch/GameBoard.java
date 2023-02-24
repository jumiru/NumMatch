package com.jrgames.nummatch;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.lang.Math;



public class GameBoard {

    public final Game game;


    private int logSizeX;
    private int logSizeY;
    private int physSizeX;
    private int physSizeY;
    private float cellWidth;
    private float cellHeight;

    private int delimiterWidth = 4;

    public final Paint digitPaint;
    private Paint linePaint;
    private Paint boldLinePaint;
    private Paint fieldPaint;
    public final Paint highlightCellPaint;

    public int content[][];
    private float cellOffsetX;
    private float cellOffsetY;

    private List<Cell> highlightedCells;

    Random rand;



    public GameBoard( Game game, int numPosX, int numPosY, int gameBoardWidth  ) {
        this.game = game;
        logSizeX = numPosX;
        logSizeY = numPosY;
        physSizeX = gameBoardWidth;
        cellWidth  = ((float) physSizeX) / ((float) logSizeX);
        cellHeight = cellWidth;
        physSizeY = (int) (logSizeY*cellHeight);


        cellOffsetX = cellWidth/5.0f;
        cellOffsetY = cellHeight/7.0f;

        linePaint = new Paint();
        linePaint.setColor(Color.rgb(0x00,0x80,0x80));
        linePaint.setStrokeWidth(delimiterWidth);

        boldLinePaint = new Paint();
        boldLinePaint.setColor(Color.rgb(0x00,0xb0,0xb0));
        boldLinePaint.setStrokeWidth(delimiterWidth*2);

        fieldPaint = new Paint();
        fieldPaint.setColor(Color.WHITE);

        digitPaint = new Paint();
        digitPaint.setColor(Color.DKGRAY);
        digitPaint.setTextSize( cellHeight );

        highlightCellPaint = new Paint();
        highlightCellPaint.setColor(Color.rgb(0xa0, 0xa0, 0xff));

        rand = new Random();

        init();
    }

    public int getBoardWidth() {
        return physSizeX;
    }

    public int getBoardHeight() {
        return physSizeY;
    }

    public void init() {
        content = new int[logSizeX][logSizeY];
        for ( int x = 0; x < logSizeX; x++ ) {
            for ( int y = 0; y < logSizeY; y++ ) {
                content[x][y] = 0;
            }
        }

        //TODO: make the initial filling smarter so that the game is theoretically solvable
        for ( int x = 0; x < logSizeX; x++ ) {
            for ( int y = 0; y < 3; y++ ) {
                content[x][y] = rand.nextInt(9)+1;
            }
        }

        highlightedCells = new ArrayList<>(10);

    }

    public void draw(Canvas canvas, int offsetX, int offsetY) {

        // make game board white
        canvas.drawRect((float)offsetX,(float)offsetY,(float) (offsetX+physSizeX), (float) (offsetY+physSizeY), fieldPaint);

        // highlight selected cells
        highlightedCells.forEach((c) -> {
            Rect r = new Rect( (int)(offsetX+c.getX()*cellWidth), (int)(offsetY+c.getY()*cellHeight),
                    (int)(offsetX+(c.getX()+1)*cellWidth), (int)(offsetY+(c.getY()+1)*cellHeight));
            canvas.drawRect(r, highlightCellPaint);
        });

        // draw grid
        for ( int x = 0; x <= logSizeX; x++ ) {
            float xpos = (float) (x*cellWidth+offsetX);
            if ( (x==0) || (x==logSizeX))
                canvas.drawLine( xpos, offsetY, xpos, offsetY+physSizeY, boldLinePaint);
            else
               canvas.drawLine( xpos, offsetY, xpos, offsetY+physSizeY, linePaint);
        }
        for ( int y = 0; y <= logSizeY; y++ ) {
            float ypos = (float) (y*cellHeight+offsetY);
            if ( (y==0) || (y==logSizeY))
                canvas.drawLine( offsetX, ypos, offsetX+physSizeX, ypos, boldLinePaint);
            else
                canvas.drawLine( offsetX, ypos, offsetX+physSizeX, ypos, linePaint);
        }


        // put cell content on top
        for ( int x = 0; x < logSizeX; x++ ) {
            for ( int y = 0; y < logSizeY; y++ ) {
                if (content[x][y]!=0) {
                    canvas.drawText(Integer.toString(content[x][y]), getCanvasTextXPos(x), getCanvasTextYPos(y), digitPaint);
                }
            }
        }
    }

    public void onTouchEvent(int x, int y) {
        int logX = (int)(x/cellWidth);
        int logY = (int)(y/cellHeight);
        Cell c = new Cell(logX, logY);

        if ( logX < logSizeX && logY<logSizeY) {
            synchronized (this) {
                // check if we have a NumMatch!!!
                if (highlightedCells.size()==1) {
                    Cell h = highlightedCells.remove(0);

                    if (match(c,h)) {
                        //  match!!!
                        game.addAnimation(new MergeAnimation(
                                this, 120, c,h,
                                content[c.getX()][c.getY()],
                                content[h.getX()][h.getY()]));
                        content[c.getX()][c.getY()] = 0;
                        content[h.getX()][h.getY()] = 0;
                    }
                } else {
                    if (content[c.getX()][c.getY()]!=0)
                        highlightedCells.add(c);
                }
            }
        }
    }

    public boolean match(Cell c1, Cell c2) {
        int v1 = content[c1.getX()][c1.getY()];
        int v2 = content[c2.getX()][c2.getY()];
        if (c1.equals(c2)) return false;
        if ((v1 != v2) && (v1 + v2 != 10)) return false;
        if (!isDiagonalAndFree(c1,c2) && !isInLineAndFree(c1,c2) && !isInNextLineAndFree(c1,c2)) return false;
        return true;
    }

    private boolean isDiagonalAndFree(Cell c1, Cell c2) {

        // check if c1 and c2 lay on a diagonal
        if (Math.abs( c1.getX() - c2.getX()) !=
                Math.abs( c1.getY() - c2.getY())) return false;

        // check if cells in between are empty
        int deltaX = (int)(Math.signum(c2.getX()-c1.getX()));
        int deltaY = (int)(Math.signum(c2.getY()-c1.getY()));

        int x = c1.getX()+deltaX;
        int y = c1.getY()+deltaY;

        while (x<c2.getX()) {
            if (content[x][y]!=0) return false;
            x = x + deltaX;
            y = y + deltaY;
        }
        return true;
    }

    private boolean isInLineAndFree(Cell c1, Cell c2) {
        if (c1.getX()==c2.getX()) {
            int x = c1.getX();
            int start = Math.min(c1.getY(), c2.getY())+1;
            int stop  = Math.max(c1.getY(), c2.getY())-1;
            for ( int y = start; y <= stop; y++ ) {
                if (content[x][y]!=0) return false;
            }
            return true;
        } else if (c1.getY()==c2.getY()) {
            int y = c1.getY();
            int start = Math.min(c1.getX(), c2.getX())+1;
            int stop  = Math.max(c1.getX(), c2.getX())-1;
            for ( int x = start; x <= stop; x++ ) {
                if (content[x][y]!=0) return false;
            }
            return true;
        }
        return false;
    }

    private boolean isInNextLineAndFree(Cell c1, Cell c2) {

        int firstLine = Math.min(c1.getY(), c2.getY());
        int lastLine  = Math.max(c1.getY(), c2.getY());
        if (Math.abs(firstLine-lastLine)!=1) return false;
        int start = c1.getX()+1;
        int stop  = c2.getX()-1;
        if (c2.getY()==firstLine) {
            start = c2.getX()+1;
            stop  = c1.getX()-1;
        }

        for ( int x = start; x < logSizeX; x++ ) if (content[x][firstLine]!=0) return false;
        for ( int x = 0; x <= stop; x++) if (content[x][lastLine]!=0) return false;

        return true;
    }

    public float getCanvasXPos( int x ) {
        return (float) (x*cellWidth+game.LEFT_BORDER);
    }
    public float getCanvasYPos( int y ) {
        return (float) (y*cellHeight+game.TOP_BORDER);
    }

    public float getCanvasTextXPos( int x ) {
        return (float)(game.LEFT_BORDER+x*cellWidth+cellOffsetX);
    }

    public float getCanvasTextYPos( int y ) {
        return (float)(game.TOP_BORDER+(y+1)*cellHeight-cellOffsetY);
    }

    public float getCellWidth() { return cellWidth; }
    public float getCellHeight() { return cellHeight; }

    public void addNewCells() {
        List<Integer> list = new ArrayList<>(20);
        int lastX = 0;
        int lastY = 0;

        for ( int y = 0; y < logSizeY; y++ ) {
            for ( int x = 0; x < logSizeX; x++ ) {
                if (content[x][y]!=0) {
                    lastX=x;
                    lastY=y;
                    list.add(new Integer(content[x][y]));
                }
            }
        }
        Collections.shuffle(list);

        int x = lastX;
        int y = lastY;
        int initialDelay = 0;
        while (!list.isEmpty()) {
            x++;
            if (x >= logSizeX) {
                x = 0;
                y++;
            }
            if (y >= logSizeY) {
                game.setGameOver(false);
                return;
            } else {
                game.addAnimation(new InsertionAnimation(
                        this, 50, initialDelay += 2,
                        new Rect((int) getCanvasXPos(logSizeX), (int) getCanvasYPos(y),
                                (int) (getCanvasXPos(logSizeX) + cellWidth), (int) (getCanvasYPos(y) + cellHeight)),
                        list.get(list.size() - 1),
                        new Cell(x, y)));
                list.remove(list.size() - 1);
            }
        }
    }

    public boolean isLineEmpty(int y) {
        boolean lineEmpty = true;
        for (int x = 0; x < logSizeX-1 && lineEmpty; x++) {
            if (content[x][y] != 0) lineEmpty = false;
        }
        return lineEmpty;
    }

    public int findLastUsedLine() {
        int lastY = -1;

        for ( int y = 0; y < logSizeY; y++ ) {
            if (!isLineEmpty(y)) {
                lastY = y;
            }
        }
        return lastY;
    }


    public void checkForEmptyLines() {
        int lastUsedLine = findLastUsedLine();
        for (int y = 0; y < lastUsedLine; y++) {
            if (isLineEmpty(y)) {
                int firstEmptyLine = y;
                int numEmptyLines = 1;
                while (isLineEmpty(y+1)) {
                    y++;
                    numEmptyLines++;
                }
                removeEmptyLines(firstEmptyLine, numEmptyLines);
            }
        }
    }

    private void removeEmptyLines(int firstEmptyLine, int numEmptyLines) {
        for ( int l = 0; l < logSizeY; l++ ) {
            if ( firstEmptyLine+numEmptyLines+l < logSizeY ) {
                for (int x = 0; x < logSizeX; x++) {
                    content[x][firstEmptyLine + l] = content[x][firstEmptyLine + numEmptyLines + l];
                }
            }
        }
    }

    boolean isBoardEmpty() {
        return (findLastUsedLine()==-1);
    }
}
