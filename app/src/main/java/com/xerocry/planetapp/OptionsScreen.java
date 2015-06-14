/*
Snake - an Android Game
Copyright 2012 Nick Eyre <nick@nickeyre.com>

Snake is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Snake is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Snake.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.xerocry.planetapp;

import android.app.Activity;
import android.app.backup.BackupManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;

public class OptionsScreen extends Activity {

  SharedPreferences userPreferences;
  SharedPreferences.Editor userPreferencesEditor;
  Spinner themeSpinner;

  @Override
  public void onCreate(Bundle savedInstanceState) {

    // Grab Existing Settings
    // Speed Setting is Stored in a Different File Because It Should Not Be Synced Across Devices
    userPreferences  = getSharedPreferences("settings", 0);
    int theme = userPreferences.getInt("theme",0);

    // Set Dark Theme
    if(theme == 1) setTheme(android.R.style.Theme_Holo);

    super.onCreate(savedInstanceState);
    setContentView(R.layout.options);

  }

  // Back Button in View
  public void back(View view){
    onBackPressed();
  }

  // Go Back to Title Screen
  @Override
  public void onBackPressed(){
    // Go Home & Close Options Screen
    Intent intent = new Intent(this, TitleScreen.class);
    startActivity(intent);
    this.finish();
  }
}
