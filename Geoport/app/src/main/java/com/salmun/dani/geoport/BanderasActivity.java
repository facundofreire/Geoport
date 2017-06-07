package com.salmun.dani.geoport;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class BanderasActivity extends AppCompatActivity {
    String[] vecPaises;
    String[] vecPaisesFaciles;
    String[] vecPaisesMedios;
    String[] vecPaisesDificiles;

    TextView tvwPais;
    TextView tvwPuntaje;
    TextView tvwTiempo;
    ImageView imvCorrecto;

    int idCorrecto;
    int puntaje = 0;
    int contCombo = 0;

    ArrayList<String> lstUltimoPais = new ArrayList<>();
    ArrayList<Integer> lstIdImb = new ArrayList<>();

    CountDownTimer cTimer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banderas);
        obtenerReferenciasYSetearListeners();
        elegirPais();
        empezarTimer();
    }

    private void obtenerReferenciasYSetearListeners(){
        ImageButton imbBandera1 = (ImageButton) findViewById(R.id.imbBandera1);
        ImageButton imbBandera2 = (ImageButton) findViewById(R.id.imbBandera2);
        ImageButton imbBandera3 = (ImageButton) findViewById(R.id.imbBandera3);
        ImageButton imbBandera4 = (ImageButton) findViewById(R.id.imbBandera4);
        tvwPais = (TextView) findViewById(R.id.tvwPais);
        tvwPuntaje = (TextView) findViewById(R.id.tvwScore);
        tvwTiempo = (TextView) findViewById(R.id.tvwTimer);
        imvCorrecto = (ImageView) findViewById(R.id.imvCorrecto);

        vecPaises = getResources().getStringArray(R.array.paises_array);
        vecPaisesFaciles = getResources().getStringArray(R.array.paises_array_facil);
        vecPaisesMedios = getResources().getStringArray(R.array.paises_array_media);
        vecPaisesDificiles = getResources().getStringArray(R.array.paises_array_dificil);

        assert imbBandera1 != null;
        assert imbBandera2 != null;
        assert imbBandera3 != null;
        assert imbBandera4 != null;
        lstIdImb.add(imbBandera1.getId());
        lstIdImb.add(imbBandera2.getId());
        lstIdImb.add(imbBandera3.getId());
        lstIdImb.add(imbBandera4.getId());

        imbBandera1.setOnClickListener(clickImg);
        imbBandera2.setOnClickListener(clickImg);
        imbBandera3.setOnClickListener(clickImg);
        imbBandera4.setOnClickListener(clickImg);
    }

    private View.OnClickListener clickImg = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == idCorrecto){
                imvCorrecto.setImageResource(R.drawable.tick);
                contCombo++;
                puntaje += contCombo / 2 + 1;
            }
            else {
                imvCorrecto.setImageResource(R.drawable.cross);
                if (contCombo == 0){
                    puntaje -= 1;
                }
                contCombo = 0;
                lstUltimoPais.remove(lstUltimoPais.size() - 1);
            }
            imvCorrecto.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    imvCorrecto.setVisibility(View.GONE);
                }
            }, 500);
            String mostrarPuntajeYCombo = "x" + String.valueOf(contCombo) + "\n" + String.valueOf(puntaje);
            tvwPuntaje.setText(mostrarPuntajeYCombo);
            elegirPais();
        }
    };

    private void empezarTimer(){
        cTimer = new CountDownTimer(30000, 1000) {
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

    private void elegirPais(){
        Random r = new Random();
        ArrayList<String> lstRepetido = new ArrayList<>();
        int intVecLenght = vecPaises.length;

        Collections.shuffle(lstIdImb);

        String strPais = "";
        ImageButton imbTemp = (ImageButton) findViewById(lstIdImb.get(0));

        do {
            if (contCombo < 6) {
                strPais = vecPaisesFaciles[r.nextInt(vecPaisesFaciles.length)];
            }
            if (contCombo >= 6 && contCombo < 14) {
                strPais = vecPaisesMedios[r.nextInt(vecPaisesMedios.length)];
            }
            if (contCombo >= 14) {
                strPais = vecPaisesDificiles[r.nextInt(vecPaisesDificiles.length)];
            }
        } while (lstUltimoPais.contains(strPais));
        lstUltimoPais.add(strPais);

        assert imbTemp != null;
        imbTemp.setImageDrawable(traerImagen(strPais));
        lstRepetido.add(strPais);
        idCorrecto = imbTemp.getId();
        strPais = strPais.substring(0, 1).toUpperCase() + strPais.substring(1);
        tvwPais.setText(strPais);
        for (int i = 1; i<4; i++) {
            strPais = vecPaises[r.nextInt(intVecLenght)];
            while (lstRepetido.contains(strPais)){
                strPais = vecPaises[r.nextInt(intVecLenght)];
            }
            lstRepetido.add(strPais);
            imbTemp = (ImageButton) findViewById(lstIdImb.get(i));
            assert imbTemp != null;
            imbTemp.setImageDrawable(traerImagen(strPais));
        }
    }

    @Nullable
    private Drawable traerImagen(String strPais){
        try {
            InputStream stream = getAssets().open("flags/" + strPais.replace(' ', '_') + ".png");
            return Drawable.createFromStream(stream, null);
        }
        catch(IOException ex) {
            return null;
        }
    }

    private void nuevaActividad(){
        cTimer.cancel();
        tvwPais.setText("¡Fin!");
        for (Integer idImb:lstIdImb) {
            ImageButton imbTemp = (ImageButton) findViewById(idImb);
            assert imbTemp != null;
            imbTemp.setEnabled(false);
            imbTemp.setImageResource(R.drawable.logo_geoport);
        }
        tvwTiempo.setVisibility(View.INVISIBLE);
        Button btnProxActividad = (Button) findViewById(R.id.btnProxActividad);
        assert btnProxActividad != null;
        btnProxActividad.setVisibility(View.VISIBLE);
        btnProxActividad.setOnClickListener(clickProxActividad);
    }

    private View.OnClickListener clickProxActividad = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), FormasActivity.class);
            intent.putExtra("intPuntaje", puntaje);
            startActivity(intent);
            finish();
        }
    };
}