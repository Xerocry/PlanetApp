package com.xerocry.planetapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.FrameLayout;

/**
 * Created by Xerocry on 07.06.2015.
 */
public class GameScreen extends Activity {
    private Player player1, player2, winner;
    private Gamev2 gamev2;
    private FrameLayout frameView;
    private Activity mActivity;
    SharedPreferences speedSetting;
    private int speed;

    // Initialize Game Screen
    @Override
    public void onCreate(Bundle savedInstanceState) {
        speedSetting = getSharedPreferences("speed", 0);
        speed = speedSetting.getInt("speed", 1);
        speed = 5;

        // Create Game View & Add Handler to Current Activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        mActivity = this;

        // Grab Score TextView Handle, Create Game Object & Add Game to Frame
        player1 = null;
        player2 = null;
        winner = null;
        gamev2 = new Gamev2(this, this, speed);
        frameView = (FrameLayout) findViewById(R.id.gameFrame);
        frameView.addView(gamev2);
    }

    public void gameOver(Player winner) {
        final CharSequence[] items = {"Play Again", "Go Back"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game Over. Winner : " + winner.getPlayerName());
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    // Play Again
                    case 0:
                        gamev2.setup();
                        gamev2.invalidate();
                        break;

                    // Go Back
                    default:
                        mActivity.finish();
                }
            }
        });

        builder.setCancelable(false);
        builder.create().show();
    }

    public void pauseGame() {

        // Do Nothing if Game Over
        if (gamev2.gameOver) return;

        for (Planet planet : gamev2.getAllPlanets()) {
            planet.onPause();
        }

        final CharSequence[] items = {"Continue", "Start Over", "Go Back"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.paused);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    // New Game (Start Over)
                    case 1:
                        gamev2.setup();
                        gamev2.invalidate();
                        break;

                    // End Game (Go Back)
                    case 2:
                        mActivity.finish();
                        break;

                    // Continue Game
                    default:
                        gamev2.invalidate();
                        for (Planet planet : gamev2.getAllPlanets()) {
                            planet.onResume();
                        }
                }
            }
        });

        builder.setCancelable(false);
        builder.create().show();
    }

    // Hardware Button Presses
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // On Menu or Back Press, Pause Game
        if ((keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_BACK) && event.getRepeatCount() == 0)
            pauseGame();
        return true;
    }

    // Pause Game when Activity Paused
    @Override
    public void onPause() {
        super.onPause();
        pauseGame();
    }

}
