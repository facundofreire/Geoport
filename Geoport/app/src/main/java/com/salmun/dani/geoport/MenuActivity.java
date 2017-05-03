package com.salmun.dani.geoport;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {
    Button btnPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ObtenerReferencias();
        btnPlay.setOnClickListener(btnPlayClick);
    }

    private void ObtenerReferencias() {
        btnPlay = (Button) findViewById(R.id.btnPlay);
    }

    private View.OnClickListener btnPlayClick = new View.OnClickListener() {
        public void onClick(View v) {
            Play();
        }
    };

    private void Play() {
        Intent intent = new Intent(this, BanderasActivity.class);
        startActivity(intent);
    }
}
