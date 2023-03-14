package com.jrgames.nummatch;

import android.graphics.Canvas;

public class DropLineAnimation extends Animation {
    private int firstEmptyLine;
    private int numEmptyLines;
    private int numLinesToMove;
    private int firstMovementLine;
    private int lastMovementLine;
    private float s;
    private float v;
    private float a;
    private boolean started;

    private int contentToMove[][];

    public DropLineAnimation(GameBoard gb, int dur, int firstEmptyLine, int numEmptyLines) {
        super(gb, dur);
        this.firstEmptyLine = firstEmptyLine;
        this.numEmptyLines = numEmptyLines;


        firstMovementLine = firstEmptyLine+numEmptyLines;

        lastMovementLine = firstMovementLine+1;
        while (lastMovementLine<gb.getHeight()) {
            if ( !gb.isLineEmpty( lastMovementLine)) {
                lastMovementLine++;
            } else {
                break;
            }
        }
        lastMovementLine--;

        numLinesToMove = lastMovementLine-firstMovementLine+1;
        contentToMove = new int[gb.getWidth()][numLinesToMove];

        started = false;




        s = 0;
        v = 0;
        a = (2.0f*numEmptyLines*gb.getCellHeight())/(dur*dur);
    }

    @Override
    public void draw(Canvas c) {
        for ( int y = 0; y < numLinesToMove; y++) {
            float posY = gb.getCanvasTextYPos(y+firstMovementLine) - s;
            for (int x = 0; x < gb.getWidth(); x++) {
                if (contentToMove[x][y] != 0) {
                    c.drawText(Integer.toString(contentToMove[x][y]), gb.getCanvasTextXPos(x), posY, gb.digitPaint);
                }
            }
        }
    }

    @Override
    public boolean update() {

        if (!started) {
            for ( int y = firstMovementLine; y <= lastMovementLine; y++) {
                for( int x = 0; x < gb.getWidth(); x++ ) {
                    contentToMove[x][y-firstMovementLine] = gb.content[x][y];
                    gb.content[x][y] = 0;
                }
            }
            if ( firstEmptyLine != 0 ) {
                if (gb.isLineEmpty(firstEmptyLine-1)) {
                    firstEmptyLine--;
                    numEmptyLines++;
                    a = (2.0f*numEmptyLines*gb.getCellHeight())/(animationDuration*animationDuration);
                }

            }
            started = true;
        }
        s += v;
        v += a;

            animationCycle++;
            if (animationCycle >= animationDuration) {
            for ( int y = 0; y < numLinesToMove; y++ ) {
                for ( int x = 0; x < gb.getWidth(); x++ ) {
                    gb.content[x][y+firstEmptyLine] = contentToMove[x][y];
                }
            }
            gb.game.addAnimation(new RumbleAnimation(gb,60, 20, false));
            return true;
        }
        else return false;
    }
}
