package com.salmun.dani.geoport;

import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class FormasActivity extends AppCompatActivity {

    String[] vecPaisesFaciles;
    ArrayList<Integer> lstIdBtn = new ArrayList<>();

    int contCombo = 0;
    int puntaje;
    TextView tvwPuntaje;
    TextView tvwTiempo;


    //TODO:SEGUIR CON ELEGIRPAIS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formas);
        puntaje = getIntent().getIntExtra("intPuntaje", 0);
        String strPuntaje = "x0\n" + String.valueOf(puntaje);
        obtenerReferenciasYSetearListeners();
        tvwPuntaje.setText(strPuntaje);
        elegirPais();
    }

    private void obtenerReferenciasYSetearListeners(){
        Button btn1 = (Button) findViewById(R.id.btn1);
        Button btn2 = (Button) findViewById(R.id.btn2);
        Button btn3 = (Button) findViewById(R.id.btn3);
        Button btn4 = (Button) findViewById(R.id.btn4);
        ImageView imvPais = (ImageView) findViewById(R.id.imvPais);
        tvwPuntaje = (TextView) findViewById(R.id.tvwScore);
        tvwTiempo = (TextView) findViewById(R.id.tvwTimer);

        vecPaisesFaciles = getResources().getStringArray(R.array.paises_array_facil);

        assert btn1 != null;
        assert btn2 != null;
        assert btn3 != null;
        assert btn4 != null;
        btn1.setOnClickListener(onClickListener);
        btn2.setOnClickListener(onClickListener);
        btn3.setOnClickListener(onClickListener);
        btn4.setOnClickListener(onClickListener);
    }

    private void elegirPais(){
        Random r = new Random();
        //GENERAR PAIS Y ASIGNARLO AL IMV

    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };
}
