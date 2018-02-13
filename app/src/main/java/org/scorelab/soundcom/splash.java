package org.scorelab.soundcom;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;

import org.scorelab.soundcom.activities.requestPermissions;

public class splash extends AppCompatActivity {

    // Splash screen timer
    private static int TIME_OUT = 4000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        new Handler().postDelayed(new Runnable() {

            /*
			 * Showing splash screen with a timer.
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start app main activity
                Intent i = new Intent(splash.this, requestPermissions.class);
                startActivity(i);

                // Close activity
                finish();
            }
        }, TIME_OUT);
    }
}
