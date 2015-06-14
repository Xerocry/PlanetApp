package com.xerocry.planetapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class Game extends View {
    private Player p1 = null;
    private Player p2 = null;
    private Player winner = null;

    Planet start = null;
    Planet end = null;

    private static final int NUM_PLANETS = 6;

    private Set<Planet> planets;
    private Set<Fleet> fleets;

    private static Timer clock = null;
    private TimerTask timerTask;


    private int width = getContext().getResources().getDisplayMetrics().widthPixels;
    private int height = getContext().getResources().getDisplayMetrics().heightPixels;

    private boolean setupComplete = false;
    private Random random;
    private GameScreen mActivity;
    public boolean gameOver = false;
    private int frameRate;
    private boolean playing = false;

    public Game(Context context, GameScreen activity, final Player player1, Player player2, int speed) {
        super(context);
//        p1 = player1;
//        p2 = player2;


        winner = null;
        mActivity = activity;
        planets = Collections.synchronizedSet(new HashSet<Planet>());
        ;
        fleets = Collections.synchronizedSet(new HashSet<Fleet>());
        ;
        random = new Random();
        this.frameRate = 5 * (speed + 1);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                float x;
                float y;
                String sDown;
                String sMove;
                String sUp;

                x = event.getX();
                y = event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: // press
                        for (Planet planet : planets) {
                            if ((Math.pow(x - planet.X, 2) +
                                    Math.pow(y - planet.Y, 2) <= planet.RADIUS * planet.RADIUS)&&planet.owner==p1) {
                                start = planet;
                            }
                        }


                        break;
                    case MotionEvent.ACTION_MOVE: // move

                        break;
                    case MotionEvent.ACTION_UP: // unpress
                        for (Planet planet : planets) {
                            if (Math.pow(x - planet.X, 2) +
                                    Math.pow(y - planet.Y, 2) <= planet.RADIUS * planet.RADIUS) {
                                end = planet;
                                if (!planet.equals(start) && start != null) {
                                    start.sendFleet((int) (start.numUnits * 0.5), end);
//                                    startMove((int) (start.numUnits * 0.5), start, end);
                                    start = null;
                                    end = null;
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

    public void play() {
        if (setupComplete != true) {
            return;
        }
        if (p1.getClass().equals(SampleBot.class)) {
        p1.start();
        }
        if (p2.getClass().equals(SampleBot.class)) {
            System.out.println("p2 is a bot! Start!");
        p2.start();
        }
        playing = true;
    }

    public void addFleet(Fleet f) {
        synchronized (fleets) {
            fleets.add(f);
        }
    }

    public Set<Planet> getAllPlanets() {
        synchronized (planets) {
            return planets;
        }
    }

    // Setup View
    public void setup() {
        gameOver = false;
        playing = false;
        p2 = new SampleBot();
        p2.currentGame = this;
        p1 = new SamplePlayer();
        p1.currentGame = this;
        int pxWidth = getWidth();
        int pxHeight = getHeight();
        int dpi = getResources().getDisplayMetrics().densityDpi;

        generateNewMap(p1, p2);

//        clock.schedule(timerTask, 500, 500);

        setupComplete = true;
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
        ;
        fleets = Collections.synchronizedSet(new HashSet<Fleet>());
        ;
        planets.add(new Planet(p1, this)); //Give player 1 a starting planet
        planets.add(new Planet(p2, this)); //Give player 2 a starting planet

        for (int i = 0; i < NUM_PLANETS - 2; i++) //Add the rest of the planets as neutral
        {
            planets.add(new Planet(this));
        }

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

        for (Planet pl : planets) {
            pl.draw(canvas);
        }
        for (Fleet fleet : fleets) {
            fleet.draw(canvas);
        }

        final View parent = this;

        int p1Win = 0;
        int p2Win = 0;

        for (Planet planet : planets) {
            if (planet.owner == p2) {
                p2Win++;
            } else if (planet.owner == p1) p1Win++;
        }
        if (p1Win == 0 && fleets.isEmpty()) {
            winner = p2;
            gameOver = true;
            stop(p1);
            stop(p2);
        } else if (p2Win == 0 && fleets.isEmpty()) {
            winner = p1;
            gameOver = true;
            stop(p1);
            stop(p2);
        }
        if (gameOver) {
            new Thread(new Runnable() {
                public void run() {
                    parent.postDelayed(new Runnable() {
                        public void run() {
                            mActivity.gameOver(winner);
                        }
                    }, 500);
                }
            }).start();
        } else {
            new Thread(new Runnable() {
                public void run() {
                    parent.postDelayed(new Runnable() {
                        public void run() {
                            parent.invalidate();
                            for (Planet pl : planets) {
                                if (pl.numUnits < pl.MAX_NEUTRAL_UNITS && pl.owner == null)
                                    pl.numUnits++;
                                else if (pl.numUnits < pl.MAX_UNITS)
                                    pl.numUnits++;
                                try {
                                    Thread.sleep((long) (pl.PRODUCTION_TIME));
                                } catch (InterruptedException ignored) {
                                }
                            }
                        }
                    }, 500 / frameRate);
                }
            }).start();
        }


    }

    public void stop(Thread thread) {
        thread = null;
        deleteFleet();
    }

    public void deleteFleet() {
        Iterator<Fleet> it = fleets.iterator();
        while (it.hasNext()) {
            Fleet fleet = it.next();
            if (fleet.isArrived()) {
                it.remove();
            }
        }
    }

    public class MyCustomTimer{
        public MyCustomTimer() {
        }
        public void setTimer(Game game, long time) {
            new CountDownTimer(time, 1000) {
                public void onTick(long millisUntilFinished) {
                }
                public void onFinish() {
                    for (Planet pl : planets) {
                        if (pl.numUnits < pl.MAX_NEUTRAL_UNITS && pl.owner == null)
                            pl.numUnits++;
                        else if (pl.numUnits < pl.MAX_UNITS)
                            pl.numUnits++;
                    }
                }
            }.start();
        }
    }

}