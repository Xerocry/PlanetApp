package com.xerocry.planetapp;


import android.graphics.Color;

/**
 * Created by Xerocry on 10.06.2015.
 */
public class SampleBot extends Player {

    int i;
    final String NAME = "Sample Player";
    final int COLOR = Color.rgb((int) (Math.random() * 255), (int) (Math.random() * 225), (int) (Math.random() * 225));

    @Override
    public int getColor() {
        return COLOR;
    }

    @Override
    public String getPlayerName() {
        return NAME;
    }

    @Override
    public void makeMove() {
        try {
            sleep(100);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("Making move~~" + i);
        i++;

        PlanetInfo source = null;
        double sourceScore = Double.MIN_VALUE;
        for (PlanetInfo p : getMyPlanetInfo()) {
            double score = (double) p.NUM_UNITS;
            if (score > sourceScore) {
                sourceScore = score;
                source = p;
            }
        }

        PlanetInfo dest = null;
        double destScore = Double.MIN_VALUE;
        for (PlanetInfo p : getAllPlanetInfo()) {
            double score = 1.0 / (1 + p.NUM_UNITS);
            if (score > destScore) {
                destScore = score;
                dest = p;
            }
        }

        if (source != null && dest != null) {
            int numShips = Math.round(source.NUM_UNITS / 2);
            sendFleet(source, numShips, dest);
        }
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
