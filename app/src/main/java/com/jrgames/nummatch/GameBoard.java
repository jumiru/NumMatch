package com.jrgames.nummatch;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.webkit.WebHistoryItem;

public class GameBoard {

    private final Paint digitPaint;
    private int logSizeX;
    private int logSizeY;
    private int physSizeX;
    private int physSizeY;
    private float cellWidth;
    private float cellHeight;

    private int delimiterWidth = 4;

    private Paint linePaint;
    private Paint boldLinePaint;
    private Paint fieldPaint;

    private int content[][];
    private float cellOffsetX;
    private float cellOffsetY;


    public GameBoard( int numPosX, int numPosY, int gameBoardWidth, int gameBoardHeight  ) {
        logSizeX = numPosX;
        logSizeY = numPosY;
        physSizeX = gameBoardWidth;
        cellWidth  = ((float) physSizeX) / ((float) logSizeX);
        cellHeight = cellWidth;
        physSizeY = (int) (logSizeY*cellHeight);


        cellOffsetX = cellWidth/5.0f;
        cellOffsetY = cellHeight/7.0f;

        content = new int[numPosX][numPosY];
        for ( int x = 0; x < logSizeX; x++ ) {
            for ( int y = 0; y < logSizeY; y++ ) {
                content[x][y] = 0;
            }
        }
        content[4][9] = 8;
        content[8][5] = 1;
        content[6][1] = 4;

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

    }

    public void draw(Canvas canvas, int offsetX, int offsetY) {
        canvas.drawRect((float)offsetX,(float)offsetY,(float) (offsetX+physSizeX), (float) (offsetY+physSizeY), fieldPaint);
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

        for ( int x = 0; x < logSizeX; x++ ) {
            for ( int y = 0; y < logSizeY; y++ ) {
                if (content[x][y]!=0) {
                    canvas.drawText(Integer.toString(content[x][y]), (float)(offsetX+x*cellWidth+cellOffsetX), (float)(offsetY+(y+1)*cellHeight-cellOffsetY), digitPaint);
                }
            }
        }
    }
}
