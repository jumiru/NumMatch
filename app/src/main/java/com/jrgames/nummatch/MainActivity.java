package com.jrgames.nummatch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.util.Log;


/**
 * MainActivity is the main entry point to my game
 */
public class MainActivity extends AppCompatActivity {

    Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity()", "onCreate()!!!!");
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        // set content view to game so that objects of the game can be rendered to the screen
        SharedPreferences p = getPreferences(MODE_PRIVATE);
        game = new Game(this, p);

        setContentView(game);
    }

    @Override
    protected void onStop() {
        Log.d("MainActivity()", "onStop()");
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.d("MainActivity()", "onPause()");
        game.pause();
        super.onPause();
    }

}
