package com.xerocry.planetapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by Xerocry on 07.06.2015.
 */
public class GameScreen extends Activity{
    private Player player1, player2, winner;
    private Game game;
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
        game = new Game(this,this,player1,player2,speed);
        frameView = (FrameLayout) findViewById(R.id.gameFrame);
        frameView.addView(game);
    }

    public void gameOver(Player winner){
        final CharSequence[] items = {"Play Again","Go Back"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.gameOver+winner.getPlayerName());
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                switch(item){
                    // Play Again
                    case 0:
                        game.setup();
                        game.invalidate();
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

    public void pauseGame(){

        // Do Nothing if Game Over
        if(game.gameOver) return;

        final CharSequence[] items = {"Continue","Start Over","Go Back"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.paused);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                switch(item){
                    // New Game (Start Over)
                    case 1:
                        game.setup();
                        game.invalidate();
                        break;

                    // End Game (Go Back)
                    case 2:
                        mActivity.finish();
                        break;

                    // Continue Game
                    default:
                        game.invalidate();
                }
            }
        });

        builder.setCancelable(false);
        builder.create().show();
    }

    // Hardware Button Presses
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        // On Menu or Back Press, Pause Game
        if ((keyCode == KeyEvent.KEYCODE_MENU || keyCode ==  KeyEvent.KEYCODE_BACK) && event.getRepeatCount() == 0)
            pauseGame();
        return true;
    }

    // Pause Game when Activity Paused
    @Override
    public void onPause(){
        super.onPause();
        pauseGame();
    }

}
