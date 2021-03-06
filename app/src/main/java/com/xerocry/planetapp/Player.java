package com.xerocry.planetapp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Xerocry on 07.06.2015.
 */
public abstract class Player extends Thread
{
    public int numFleets = 0;
    static final AtomicLong NEXT_ID = new AtomicLong(0);
    final long id = NEXT_ID.getAndIncrement();
    public abstract int getColor(); //Get the AI's color
    public abstract void makeMove(); //The AI's main brain
    public abstract String getPlayerName(); //Get the AI's name
    public Paint color = new Paint();

    public static Game currentGame;

    @Override
    public void run()
    {
        while (true)
        {
            makeMove();
        }
    }

    public ArrayList<PlanetInfo> getMyPlanetInfo()
    {
        ArrayList<PlanetInfo> myInfo = new ArrayList<>();
        for (Planet p: currentGame.getAllPlanets())
        {
            if (p.owner == this)
            {
                myInfo.add(p.getPlanetInfo());
            }
        }
        return myInfo;
    }

    public ArrayList<PlanetInfo> getAllPlanetInfo()
    {
        ArrayList<PlanetInfo> info = new ArrayList<PlanetInfo>();
        for (Planet p: currentGame.getAllPlanets())
        {
            info.add(p.getPlanetInfo());
        }
        return info;
    }

    private Planet getPlanetFromInfo(PlanetInfo pInfo)
    {
        for (Planet p: currentGame.getAllPlanets())
        {
            if (p.getPlanetInfo().equals(pInfo))
            {
                return p;
            }
        }
        return null;
    }

    public void sendFleet(PlanetInfo originInfo, int numUnits, PlanetInfo targetInfo)
    {
        Planet origin = getPlanetFromInfo(originInfo);
        Planet target = getPlanetFromInfo(targetInfo);
        if (origin != null && target != null)
        {
                origin.sendFleet(numUnits, target, this);
        }
    }

    public String toString()
    {
        return "Player [name=" + getPlayerName() + "]";
    }

}
