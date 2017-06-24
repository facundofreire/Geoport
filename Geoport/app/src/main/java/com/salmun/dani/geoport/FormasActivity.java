package com.salmun.dani.geoport;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class FormasActivity extends AppCompatActivity {

    String[] vecPaises;
    int[] vecDificultad;

    ArrayList<Integer> lstIdBtn = new ArrayList<>();
    ArrayList<Integer> lstPaisesRepetidosGlobal = new ArrayList<>();

    int contCombo = 0;
    int puntaje;

    int idCorrecto;

    TextView tvwPuntaje;
    TextView tvwTiempo;
    ImageView imvPais;
    ImageView imvCorrecto;
    Button btnContinuar;

    CountDownTimer cTimer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formas);
        puntaje = getIntent().getIntExtra("intPuntaje", 0);
        obtenerReferenciasYSetearListeners();
        elegirPais();
        empezarTimer();
    }

    private void obtenerReferenciasYSetearListeners(){
        Button btn1 = (Button) findViewById(R.id.btn1);
        Button btn2 = (Button) findViewById(R.id.btn2);
        Button btn3 = (Button) findViewById(R.id.btn3);
        Button btn4 = (Button) findViewById(R.id.btn4);
        imvPais = (ImageView) findViewById(R.id.imvPais);
        imvCorrecto = (ImageView) findViewById(R.id.imvCorrecto);
        tvwPuntaje = (TextView) findViewById(R.id.tvwScore);
        tvwTiempo = (TextView) findViewById(R.id.tvwTimer);
        btnContinuar = (Button) findViewById(R.id.btnContinuar);

        vecPaises = getResources().getStringArray(R.array.paises_array);
        vecDificultad = getResources().getIntArray(R.array.dificultadPaises);

        assert btn1 != null;
        assert btn2 != null;
        assert btn3 != null;
        assert btn4 != null;
        lstIdBtn.add(btn1.getId());
        lstIdBtn.add(btn2.getId());
        lstIdBtn.add(btn3.getId());
        lstIdBtn.add(btn4.getId());

        btn1.setOnClickListener(onClickListener);
        btn2.setOnClickListener(onClickListener);
        btn3.setOnClickListener(onClickListener);
        btn4.setOnClickListener(onClickListener);
    }

    private void empezarTimer(){
        cTimer = new CountDownTimer(1000, 1000) {
            @SuppressLint("SetTextI18n")
            public void onTick(long milisegundos) {
                if (milisegundos < 10000){
                    tvwTiempo.setText("0:0" + milisegundos / 1000);
                    if (milisegundos < 5000){
                        tvwTiempo.setTextColor(Color.parseColor("#be0000"));
                    }
                }
                else{
                    tvwTiempo.setText("0:" + milisegundos / 1000);
                }
            }
            public void onFinish() {
                nuevaActividad();
            }
        };
        cTimer.start();
    }

    private boolean elegirPais(){
        Random r = new Random();
        int dificultad = 1;
        if (contCombo < 6){
            dificultad = 0;
        }else if(contCombo > 10){
            dificultad = 2;
        }
        int posicion = r.nextInt(vecPaises.length);
        if (lstPaisesRepetidosGlobal.size() != vecPaises.length) {
            do {
                posicion = r.nextInt(vecPaises.length);
            }while (lstPaisesRepetidosGlobal.contains(posicion)
                    || vecDificultad[posicion] != dificultad);
        }else{
            nuevaActividad();
            return false;
        }

        assert imvPais != null;
        imvPais.setImageDrawable(traerImagen(posicion));

        Collections.shuffle(lstIdBtn);
        idCorrecto = lstIdBtn.get(0);
        Button btnTemp = (Button) findViewById(idCorrecto);
        assert btnTemp != null;
        btnTemp.setText(vecPaises[posicion]);

        ArrayList<Integer> lstPaisesRepetidos = new ArrayList<>();

        lstPaisesRepetidos.add(posicion);
        lstPaisesRepetidosGlobal.add(posicion);
        for (int i = 1; i < 4; i++){
            posicion = r.nextInt(vecPaises.length);
            while(lstPaisesRepetidos.contains(posicion)){
                posicion = r.nextInt(vecPaises.length);
            }
            lstPaisesRepetidos.add(posicion);

            btnTemp = (Button) findViewById(lstIdBtn.get(i));
            assert btnTemp != null;
            btnTemp.setText(vecPaises[posicion]);
        }
        return true;
    }

    @Nullable
    private Drawable traerImagen(int nombreImg){
        try {
            InputStream stream = getAssets().open("mapsTest/" + String.valueOf(nombreImg + 1) + ".png");
            return Drawable.createFromStream(stream, null);
        }
        catch(IOException ex){
            return null;
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (idCorrecto == view.getId()){
                imvCorrecto.setImageResource(R.drawable.tick);
                contCombo++;
                puntaje += contCombo * 2;
            }else{
                imvCorrecto.setImageResource(R.drawable.cross);
                if (contCombo == 0){
                    puntaje -= 2;
                }
                contCombo = 0;
            }
            imvCorrecto.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    imvCorrecto.setVisibility(View.GONE);
                }
            }, 500);
            String strMostrarComboYPuntaje = "x" + String.valueOf(contCombo) + "\n" + String.valueOf(puntaje);
            tvwPuntaje.setText(strMostrarComboYPuntaje);
            elegirPais();
        }
    };

    private void nuevaActividad(){
        cTimer.cancel();
        imvPais.setImageResource(R.drawable.logo_geoport);
        for (int idBtn:lstIdBtn){
            Button btnTemp = (Button)findViewById(idBtn);
            assert btnTemp != null;
            btnTemp.setEnabled(false);
            btnTemp.setText(R.string.fin);
        }
        tvwTiempo.setVisibility(View.INVISIBLE);
        btnContinuar.setVisibility(View.VISIBLE);
        btnContinuar.setOnClickListener(clickProxActividad);
    }

    private View.OnClickListener clickProxActividad = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), UbicacionActivity.class);
            intent.putExtra("intPuntaje", puntaje);
            startActivity(intent);
            finish();
        }
    };
}