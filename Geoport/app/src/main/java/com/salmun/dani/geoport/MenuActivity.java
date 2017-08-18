package com.salmun.dani.geoport;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MenuActivity extends AppCompatActivity {
    Button btnPlay;
    ImageButton imbFacebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ObtenerReferenciasYSetearListeners();
    }

    private void ObtenerReferenciasYSetearListeners() {
        btnPlay = (Button) findViewById(R.id.btnPlay);
        imbFacebook = (ImageButton) findViewById(R.id.btnFacebook);

        btnPlay.setOnClickListener(btnPlayClick);
        imbFacebook.setOnClickListener(clickFacebook);
    }

    private View.OnClickListener clickFacebook = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private View.OnClickListener btnPlayClick = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), BanderasActivity.class);
            startActivity(intent);
            finish();
        }
    };
}
