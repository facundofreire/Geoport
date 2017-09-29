package com.salmun.dani.geoport;

import android.animation.ObjectAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;

public class test extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_prog);
        ProgressBar pgBar = (ProgressBar) findViewById(R.id.progressBar);
        pgBar.setRotation(-90);
        ObjectAnimator animation = ObjectAnimator.ofInt (pgBar, "progress", 500, 0); // see this max value coming back here, we animale towards that value
        animation.setDuration (5000); //in milliseconds
        animation.setInterpolator (new DecelerateInterpolator());
        animation.start ();
    }
}
