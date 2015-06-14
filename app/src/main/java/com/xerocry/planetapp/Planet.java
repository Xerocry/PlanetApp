package com.xerocry.planetapp;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by Xerocry on 08.06.2015.
 */
public class Planet {
    public static final int MAX_SIZE = 90;
    public static final int MIN_SIZE = 50;
    public static final int MAX_NEUTRAL_UNITS = 50;
    public static final int MIN_PRODUCE_TIME = 40;
    public static final int MAX_PRODUCE_TIME = 100;
    public static final int MAX_UNITS = 100;

    public int X, Y, RADIUS, PRODUCTION_TIME;
    public int numUnits = 0;
    public Player owner;
    public Game parent;

    Paint color = new Paint();

    public Planet(Player player, Game parent) {
        this.parent = parent;
        owner = player;
        numUnits = 20;
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

    public Planet(Game parent) {

        this.parent = parent;
        this.owner = null;
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

    public void sendFleet(int numSent, Planet target) {
        if (target != this && target != null) {
            if (numSent > numUnits) {
                numSent = numUnits - 1;
            }
            numUnits -= numSent;
            Fleet fleet = new Fleet(this, target, numSent);
            parent.addFleet(fleet);
            FleetProcessor fleetProc = new FleetProcessor(parent, fleet);
            fleetProc.start();
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