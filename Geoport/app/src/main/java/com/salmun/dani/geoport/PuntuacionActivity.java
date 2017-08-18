package com.salmun.dani.geoport;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PuntuacionActivity extends AppCompatActivity {

    int puntaje;

    Button btnMenu;
    Button btnContinuar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puntuacion);
        puntaje = Usuario.leerPuntaje();
        obtenerReferenciasYSetearListeners();
    }

    private void obtenerReferenciasYSetearListeners(){
        btnContinuar = (Button) findViewById(R.id.btnContinuar);
        btnMenu = (Button) findViewById(R.id.btnMenu);
        TextView tvwPuntaje = (TextView) findViewById(R.id.tvwPuntajeFinal);
        assert tvwPuntaje != null;
        tvwPuntaje.setText(String.valueOf(puntaje));

        btnContinuar.setOnClickListener(clickContinuar);
        btnMenu.setOnClickListener(clickMenu);
    }

    private View.OnClickListener clickContinuar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), RankingActivity.class);
            Usuario.escribirPuntaje(puntaje);
            startActivity(intent);
        }
    };

    private View.OnClickListener clickMenu = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            llamarDialog();
        }
    };

    private void llamarDialog(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(R.string.confSalir);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                R.string.si,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                        startActivity(intent);
                        finish();
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
