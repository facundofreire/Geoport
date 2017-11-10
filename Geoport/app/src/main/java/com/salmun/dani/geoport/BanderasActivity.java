package com.salmun.dani.geoport;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class BanderasActivity extends AppCompatActivity {
    String[] vecPaises;
    int[] vecDificultad;

    TextView tvwPais, tvwPaisError, tvwPuntaje, tvwTiempo,
            tvwContCorrectas, tvwContIncorrectas, tvwComboMax;
    ImageView imvCorrecto;
    ProgressBar prbTimer;

    int puntaje, contCombo, contCorrectas, contIncorrectas, comboMax = 0;

    ArrayList<Integer> lstUltimoPais = new ArrayList<>();
    ArrayList<Integer> lstIdImb = new ArrayList<>();
    ArrayList<Integer> lstRepetido;

    CountDownTimer cTimer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banderas);
        obtenerReferenciasYSetearListeners();
        elegirPais();
        empezarTimer();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
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
        tvwPaisError = (TextView) findViewById(R.id.tvwPaisError);
        tvwContCorrectas = (TextView) findViewById(R.id.tvwContCorrectas);
        tvwContIncorrectas = (TextView) findViewById(R.id.tvwContIncorrectas);
        tvwComboMax = (TextView) findViewById(R.id.tvwComboMax);
        prbTimer = (ProgressBar) findViewById(R.id.prbTimer);

        vecPaises = getResources().getStringArray(R.array.paises_array);
        vecDificultad = getResources().getIntArray(R.array.dificultadPaises);

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
            if (view.getId() == lstIdImb.get(0)){
                imvCorrecto.setImageResource(R.drawable.tick);
                contCombo++;
                contCorrectas++;
                puntaje += contCombo / 2 + 1;
                tvwPaisError.setText("");
            }
            else {
                for (int i = 1; i < 4; i++) {
                    if (view.getId() == lstIdImb.get(i)) {
                        String textoPaisError = getString(R.string.paisElegido) + vecPaises[lstRepetido.get(i)];
                        tvwPaisError.setText(textoPaisError);
                        break;
                    }
                }
                imvCorrecto.setImageResource(R.drawable.cross);
                if (contCombo == 0){
                    puntaje -= 1;
                }
                if (contCombo > comboMax){
                    comboMax = contCombo;
                }
                contCombo = 0;
                contIncorrectas++;
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
        final long tiempo = 1000;
        cTimer = new CountDownTimer(tiempo, 1000) {
            @SuppressLint("SetTextI18n")
            public void onTick(long milisegundos) {
                if (milisegundos < 10000){
                    tvwTiempo.setText("0" + String.valueOf(milisegundos / 1000));
                    if (milisegundos < 5000){
                        tvwTiempo.setTextColor(Color.parseColor("#be0000"));
                    }
                }
                else{
                    tvwTiempo.setText(String.valueOf(milisegundos / 1000));
                }
            }
            public void onFinish() {
                nuevaActividad();
            }
        };
        cTimer.start();
        prbTimer.setRotation(-90);
        ObjectAnimator animation = ObjectAnimator.ofInt (prbTimer, "progress", 500, 0);
        animation.setDuration(tiempo);
        animation.setInterpolator (new DecelerateInterpolator());
        animation.start ();
    }

    private void elegirPais(){
        Random r = new Random();
        lstRepetido = new ArrayList<>();

        Collections.shuffle(lstIdImb);

        int dificultad = 1;
        if(contCombo < 6){
            dificultad = 0;
        }
        if(contCombo >= 14){
            dificultad = 2;
        }

        int posicion;
        do {
            posicion = r.nextInt(vecPaises.length);
        } while (lstUltimoPais.contains(posicion) || vecDificultad[posicion] != dificultad);
        lstUltimoPais.add(posicion);

        ImageButton imbTemp = (ImageButton) findViewById(lstIdImb.get(0));
        assert imbTemp != null;
        imbTemp.setImageDrawable(traerImagen(posicion));
        lstRepetido.add(posicion);
        tvwPais.setText(vecPaises[posicion]);
        for (int i = 1; i<4; i++) {
            posicion = r.nextInt(vecPaises.length);
            while (lstRepetido.contains(posicion)){
                posicion = r.nextInt(vecPaises.length);
            }
            lstRepetido.add(posicion);
            imbTemp = (ImageButton) findViewById(lstIdImb.get(i));
            assert imbTemp != null;
            imbTemp.setImageDrawable(traerImagen(posicion));
        }
    }

    @Nullable
    private Drawable traerImagen(int nombreImg){
        try {
            InputStream stream = getAssets().open("flagsTest/" + String.valueOf(nombreImg + 1) + ".png");
            return Drawable.createFromStream(stream, null);
        }
        catch(IOException ex) {
            return null;
        }
    }

    private void nuevaActividad(){
        cTimer.cancel();
        tvwPais.setText(R.string.fin);

        if (contCombo > comboMax){
            comboMax = contCombo;
        }

        for (Integer idImb:lstIdImb) {
            ImageButton imbTemp = (ImageButton) findViewById(idImb);
            assert imbTemp!=null;
            imbTemp.setVisibility(View.INVISIBLE);
        }
        tvwTiempo.setVisibility(View.INVISIBLE);
        tvwPaisError.setVisibility(View.INVISIBLE);

        String textoAMostrar = tvwContCorrectas.getText().toString() + String.valueOf(contCorrectas);
        tvwContCorrectas.setText(textoAMostrar);
        textoAMostrar = tvwContIncorrectas.getText().toString() + String.valueOf(contIncorrectas);
        tvwContIncorrectas.setText(textoAMostrar);
        textoAMostrar = tvwComboMax.getText().toString() + String.valueOf(comboMax);
        tvwComboMax.setText(textoAMostrar);
        tvwContCorrectas.setVisibility(View.VISIBLE);
        tvwContIncorrectas.setVisibility(View.VISIBLE);
        tvwComboMax.setVisibility(View.VISIBLE);

        Button btnProxActividad = (Button) findViewById(R.id.btnProxActividad);
        assert btnProxActividad != null;
        btnProxActividad.setVisibility(View.VISIBLE);
        btnProxActividad.setOnClickListener(clickProxActividad);
    }

    private View.OnClickListener clickProxActividad = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), FormasActivity.class);
            Usuario.escribirPuntaje(puntaje);
            startActivity(intent);
            finish();
        }
    };
}