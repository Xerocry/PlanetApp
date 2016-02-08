package com.xerocry.planetapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Xerocry on 08.02.2016.
 */
public class Game extends View {
    private Player p1 = null;
    private Player p2 = null;
    private Player winner = null;

    //Be selected from game room size
    private static final int NUM_PLANETS = 6;
    private int frameRate;

    private int width = getContext().getResources().getDisplayMetrics().widthPixels;
    private int height = getContext().getResources().getDisplayMetrics().heightPixels;

    private Set<Planet> planets;
    private Set<Fleet> fleets;

    private boolean setupComplete = false;
    //Flag to start writing score
    public boolean gameOver = false;
    //Flag for checking
    private boolean playing = false;

    public Game(Context context, Player p1, Player p2, int speed) {
        super(context);
        this.p1 = p1;
        this.p2 = p2;
        this.frameRate = 5 * (speed + 1);
        winner = null;
        planets = Collections.synchronizedSet(new HashSet<Planet>());
        fleets = Collections.synchronizedSet(new HashSet<Fleet>());
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Planet start = null;
                Planet end = null;
                float x;
                float y;
                x = event.getX();
                y = event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: // press
                        for (Planet planet : planets) {
                            //if we touch the planet make it start
                            if ((Math.pow(x - planet.X, 2) +
                                    Math.pow(y - planet.Y, 2) <= planet.RADIUS * planet.RADIUS) && planet.owner == p1) {
                                start = planet;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_MOVE: // move

                        break;
                    case MotionEvent.ACTION_UP: // unpress
                        for (Planet planet : planets) {
                            //if we untouch on planet - send fleet to it
                            if (Math.pow(x - planet.X, 2) +
                                    Math.pow(y - planet.Y, 2) <= planet.RADIUS * planet.RADIUS) {
                                end = planet;
                                if (start != null) {
                                    if (planet.getId() != start.getId()) {
                                        start.sendFleet((int) (start.numUnits * 0.5), end, start.owner);
                                        start = null;
                                        end = null;
                                    }
                                }
                            }
                        }

                        break;

                    case MotionEvent.ACTION_CANCEL:
                        break;
                }
                return true;
            }
        });
    }

    public void setup() {
        gameOver = false;
        playing = false;

        //This depends on game mode
        p1 = new SamplePlayer();
        p2 = new SampleBot();

//        p2.currentGamev2 = this;
//        p1.currentGamev2 = this;

        int pxWidth = getWidth();
        int pxHeight = getHeight();
        int dpi = getResources().getDisplayMetrics().densityDpi;

        generateNewMap(p1, p2);

        setupComplete = true;
    }

    public void play() {
        if (setupComplete != true) {
            return;
        }
        if (p1.getClass().equals(SampleBot.class)) {
            p1.start();
        }
        if (p2.getClass().equals(SampleBot.class)) {
            p2.start();
        }
        playing = true;
    }

    //Generate a value of x between rad and WIN_WIDTH - rad
    public int genX(int rad) {
        return (int) (Math.random() * (width - rad * 2) + rad);
    }

    //Generate a value of y between rad and WIN_HEIGHT - rad
    public int genY(int rad) {
        return (int) (Math.random() * (height - rad * 2) + rad);
    }

    public boolean isPlanetOverlapping(int x, int y, int radius) {
        synchronized (planets) {
            for (Planet p : planets) {
                int dx = p.X - x;
                int dy = p.Y - y;
                int minSep = p.RADIUS + radius + 10; //Must be at least ten units apart

                if ((dx * dx + dy * dy <= minSep * minSep) || (x + radius >= getWidth() - 16) || (x - radius <= 16) || (y + radius >= getHeight() - 16)
                        || (y - radius <= 16)) {
                    return true;
                }
            }
            return false;
        }
    }

    private void generateNewMap(Player p1, Player p2) {
        planets = Collections.synchronizedSet(new HashSet<Planet>());
        fleets = Collections.synchronizedSet(new HashSet<Fleet>());
        planets.add(new Planet(p1, this)); //Give player 1 a starting planet
        planets.add(new Planet(p2, this)); //Give player 2 a starting planet

        for (int i = 0; i < NUM_PLANETS - 2; i++) //Add the rest of the planets as neutral
        {
            planets.add(new Planet(this));
        }
//        for (Planet planet : planets) {
//            planet.run();
//        }
        Thread t = new Thread(new Runnable() {
            private long time;

            @Override
            public void run() {
                for (Planet pl : planets) {
                    if (time % (pl.PRODUCTION_TIME * 5) == 0) {
                        if (pl.numUnits < pl.MAX_NEUTRAL_UNITS && pl.owner == null)
                            pl.numUnits++;
                        else if (pl.numUnits < pl.MAX_UNITS)
                            pl.numUnits++;
                    }
                }
                time++;
            }
        });
    }

    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        if (!setupComplete) {
            setup();
            this.invalidate();
            return;
        }
        if (playing != true) {
            play();
        }
        synchronized(planets) {
            for (Planet pl : planets) {
                pl.draw(canvas);
            }
        }
        synchronized(fleets) {
            for (Fleet fleet : fleets) {
                fleet.draw(canvas);
            }
        }

        final View parent = this;

        int p1Win = 0;
        int p2Win = 0;

        for (Planet planet : planets) {
            if (planet.owner == null) {
                continue;
            }
            if (planet.owner.getId() == p2.getId()) {
                p2Win++;
            } else if (planet.owner.getId() == p1.getId()) p1Win++;
        }
        if (p1Win == 0 && p1.numFleets==0) {
            winner = p2;
            gameOver = true;
            stopGame();
        } else if (p2Win == 0 && p2.numFleets==0) {
            winner = p1;
            gameOver = true;
            stopGame();
        }

    }


}
