package com.xerocry.planetapp;


import android.graphics.Color;

/**
 * Created by Xerocry on 10.06.2015.
 */
public class SamplePlayer extends Player {

    public String NAME = "Player";
    final int COLOR = Color.GREEN;

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

    }
}
