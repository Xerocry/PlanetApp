package com.xerocry.planetapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class TitleScreen extends Activity {

  SharedPreferences settings;

  // Create Title Screen View
  @Override
  public void onCreate(Bundle savedInstanceState) {

    // Set Theme According to Settings
    settings = getSharedPreferences("settings", 0);
    if(settings.getInt("theme",0) == 1) setTheme(android.R.style.Theme_Holo);

    super.onCreate(savedInstanceState);
    setContentView(R.layout.title_screen);
  }

  // On "Options" View Button Press, Change to Options Screen
  public void options(View view){
    Intent intent = new Intent(this, OptionsScreen.class);
    startActivity(intent);
    this.finish();
  }

  public void startGame(View view){
    Intent intent = new Intent(this, GameScreen.class);
    startActivity(intent);
  }

}
