package com.xerocry.planetapp;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Xerocry on 08.06.2015.
 */
public class Planet implements Runnable{

    //Every planet has uniq id
    static final AtomicLong NEXT_ID = new AtomicLong(0);
    final long id = NEXT_ID.getAndIncrement();
    android.os.Handler customHandler = new android.os.Handler();
    //These par-res must be in settings
    public static final int MAX_SIZE = 90;
    public static final int MIN_SIZE = 50;
    public static final int MAX_NEUTRAL_UNITS = 50;
    public static final int MIN_PRODUCE_TIME = 40;
    public static final int MAX_PRODUCE_TIME = 200;
    public static final int MAX_UNITS = 100;

    public int X, Y, RADIUS, PRODUCTION_TIME;
    public int numUnits = 0;

    public Player owner = null;
    //? Do I need it?
    public Gamev2 parent;

    Paint color = new Paint();

    //What is it?
    private Object mPauseLock = new Object();
    private boolean mPaused = false;
    private boolean mFinished = false;

    public Planet(Player player, Gamev2 parent) {
        this.parent = parent;
        owner = player;
        numUnits = 50;
        RADIUS = MAX_SIZE;
        PRODUCTION_TIME = MIN_PRODUCE_TIME;

        //Try to find a safe landing zone for our planet
        int x = parent.genX(RADIUS);
        int y = parent.genY(RADIUS);

        while (parent.isPlanetOverlapping(x, y, RADIUS)) {
            x = parent.genX(RADIUS);
            y = parent.genY(RADIUS);
        }

        X = x;
        Y = y;

        color = new Paint();
        color.setColor(owner.getColor());
    }

    public Planet(Gamev2 parent) {

        this.parent = parent;
        numUnits = (int) (Math.random() * MAX_NEUTRAL_UNITS);
        RADIUS = (int) (Math.random() * (MAX_SIZE - MIN_SIZE) + MIN_SIZE);
        PRODUCTION_TIME = (int) ((1 - ((double) RADIUS - MIN_SIZE) / (MAX_SIZE - MIN_SIZE)) *
                (MAX_PRODUCE_TIME - MIN_PRODUCE_TIME) + MIN_PRODUCE_TIME);

        int x = parent.genX(RADIUS);
        int y = parent.genY(RADIUS);

        while (parent.isPlanetOverlapping(x, y, RADIUS)) {
            x = parent.genX(RADIUS);
            y = parent.genY(RADIUS);
        }
        X = x;
        Y = y;
        color = new Paint();
        color.setColor(Color.DKGRAY);
    }

    public long getId() {
        return id;
    }

    public void draw(Canvas canvas) {
        if (owner != null) color.setColor(owner.getColor());
        else color.setColor(Color.DKGRAY);
        canvas.drawCircle(X, Y, RADIUS, color);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(30f);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        Rect bounds = new Rect();
        paint.getTextBounds(Integer.toString(numUnits), 0, Integer.toString(numUnits).length(), bounds);
        canvas.drawText(Integer.toString(numUnits), X, Y, paint);
    }

    @Override
    public void run() {
            customHandler.postDelayed(updateTimerThread, 0);

    }

    public void onPause() {
        synchronized (mPauseLock) {
            mPaused = true;
        }
    }

    public void onResume() {
        synchronized (mPauseLock) {
            mPaused = false;
            mPauseLock.notifyAll();
        }
    }

    private Runnable updateTimerThread = new Runnable()
    {
        public void run()
        {
            if (numUnits < MAX_NEUTRAL_UNITS && owner == null)
                numUnits++;
            else if (numUnits < MAX_UNITS)
                numUnits++;
            customHandler.postDelayed(this, PRODUCTION_TIME*5);
        }
    };

    public void sendFleet(int numSent, Planet target, Player owner) {
        if (target != this && target != null) {
            if (numSent > numUnits) {
                numSent = numUnits - 1;
            }
            numUnits -= numSent;
            Fleet fleet = new Fleet(parent, this, target, owner, numSent);
            parent.addFleet(fleet);
            owner.numFleets++;
            fleet.start();
        }
    }

    public PlanetInfo getPlanetInfo() {
        return new PlanetInfo(owner, numUnits, X, Y, RADIUS, PRODUCTION_TIME);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}


//Class for easy info about planet
class PlanetInfo {
    public final int NUM_UNITS, X, Y, RADIUS, PRODUCTION_TIME;
    public final Player OWNER;

    public PlanetInfo(Player ownerIn, int numUnitsIn, int xIn, int yIn, int radiusIn, int production) {
        OWNER = ownerIn;
        NUM_UNITS = numUnitsIn;
        X = xIn;
        Y = yIn;
        RADIUS = radiusIn;
        PRODUCTION_TIME = production;
    }

    //Getters
    public Player getOwner() {
        return OWNER;
    }

    public int getNumUnits() {
        return NUM_UNITS;
    }

    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }

    public int getRadius() {
        return RADIUS;
    }

    public int getProductionTime() {
        return PRODUCTION_TIME;
    }

    public double getProductionRate() {
        return 1.0 / PRODUCTION_TIME;
    }

    //Test to see if this PlanetInfo is the same as some other PlanetInfo
    public boolean equals(PlanetInfo other) {
        return (OWNER == other.OWNER &&
                NUM_UNITS == other.NUM_UNITS &&
                X == other.X &&
                Y == other.Y &&
                RADIUS == other.RADIUS);
    }
}