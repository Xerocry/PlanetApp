package com.xerocry.planetapp;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by Xerocry on 08.06.2015.
 */
public class Fleet extends Thread {
    private Game game;
    private Player owner;
    private Planet start;
    private Planet destination;

    private boolean arrived = false;
    double X, Y;
    int shipAmount;
    private static int delta = 1;

    private Object mPauseLock = new Object();
    private boolean mPaused = false;
    private boolean mFinished = false;

    @Override
    public void run() {
        while (Math.pow(destination.X - X, 2) +
                Math.pow(destination.Y - Y, 2) >= Math.pow(destination.RADIUS, 2)) {
//            while (!mFinished) {
                double cosin = Math.abs((destination.Y - Y) /
                        (Math.sqrt(Math.pow((destination.X - X), 2) +
                                Math.pow((destination.Y - Y), 2))));
                double yDelta = delta * cosin, xDelta = Math.sqrt(Math.pow(delta, 2) - Math.pow(yDelta, 2));
                if (X > destination.X)
                    X = (X - xDelta);
                else if (X < destination.X)
                    X = (X + xDelta);
                if (Y > destination.Y)
                    Y = (Y - yDelta);
                else if (Y < destination.Y)
                    Y = (Y + yDelta);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) {
                }
//                synchronized (mPauseLock) {
//                    while (mPaused) {
//                        try {
//                            mPauseLock.wait();
//                        } catch (InterruptedException e) {
//                        }
//                    }
//                }
//            }

        }
        if (destination.owner != null) {
            if (destination.owner.getId() == getOwner().getId())
                destination.numUnits += shipAmount;
            else if (destination.numUnits <= shipAmount) {
                shipAmount = shipAmount - destination.numUnits;
                destination.numUnits = 0;
                destination.numUnits += shipAmount;
                destination.owner = getOwner();
            } else {
                destination.numUnits -= shipAmount;
            }
        } else if (destination.numUnits <= shipAmount) {
            shipAmount = shipAmount - destination.numUnits;
            destination.numUnits = 0;
            destination.numUnits += shipAmount;
            destination.owner = getOwner();
        } else {
            destination.numUnits -= shipAmount;
        }
        setArrived(true);
        owner.numFleets--;

        game.stop(this);
    }

    public Planet getStart() {
        return start;
    }

    public Player getOwner() {
        return owner;
    }

    public Planet getDestination() {
        return destination;
    }

    public boolean isArrived() {
        return arrived;
    }

    public void setArrived(boolean arrived) {
        this.arrived = arrived;
    }

    public Fleet(Game game, Planet start, Planet destination, Player owner, int shipAmount) {
        this.game = game;
        this.owner = owner;
        this.start = start;
        this.destination = destination;
        this.arrived = false;
        X = start.X;
        Y = start.Y;
        this.shipAmount = shipAmount;
    }

    public void draw(Canvas canvas) {
        Paint color = new Paint();
        color.setStyle(Paint.Style.FILL);
//        color.setStyle(Paint.Style.STROKE);
        System.out.println(owner.getPlayerName());
//        if (owner != null) color = owner.color;
        if (owner != null) color.setColor(owner.getColor());
        canvas.drawCircle((float) X, (float) Y, 30, color);

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(30f);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        Rect bounds = new Rect();
        paint.getTextBounds(Integer.toString(shipAmount), 0, Integer.toString(shipAmount).length(), bounds);
        canvas.drawText(Integer.toString(shipAmount), (float) X, (float) Y, paint);
    }
}

