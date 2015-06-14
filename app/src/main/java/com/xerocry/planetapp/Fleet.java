package com.xerocry.planetapp;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by Xerocry on 08.06.2015.
 */
public class Fleet {
    private Planet owner;
    private Planet destination;
    private boolean arrived = false;

    double X, Y;
    int shipAmount;

    public Planet getOwner() {
        return owner;
    }

    public boolean isArrived() {
        return arrived;
    }

    public void setArrived(boolean arrived) {
        this.arrived = arrived;
    }

    public Fleet(Planet owner, Planet destination, int shipAmount) {
        this.owner = owner;
        this.destination = destination;
        this.arrived = false;
        X = owner.X;
        Y = owner.Y;
        this.shipAmount = shipAmount;
    }

    public void draw(Canvas canvas) {
        Paint color = new Paint();
        color.setStyle(Paint.Style.STROKE);
//        if (owner != null) color.setColor(owner.color);
//        else color.setColor(Color.DKGRAY);
        canvas.drawCircle((float)X, (float)Y, 20, color);

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(30f);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        Rect bounds = new Rect();
        paint.getTextBounds(Integer.toString(shipAmount), 0, Integer.toString(shipAmount).length(), bounds);
        canvas.drawText(Integer.toString(shipAmount), (float) X, (float) Y, paint);

    }

    public Planet getDestination() {
        return destination;
    }
}

