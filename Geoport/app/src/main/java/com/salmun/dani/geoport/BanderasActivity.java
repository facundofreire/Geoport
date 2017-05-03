package com.salmun.dani.geoport;

import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

public class BanderasActivity extends AppCompatActivity {
    String[] vecPaises;
    TextView tvwPais;
    TextView tvwCorrecto;
    TextView tvwPuntaje;
    ImageButton imbBandera1;
    ImageButton imbBandera2;
    ImageButton imbBandera3;
    ImageButton imbBandera4;
    int idCorrecto;
    int puntaje = 0;
    List lstRepetidoGlobal = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banderas);
        obtenerReferenciasYSetearListeners();
        elegirPais();
        Button btnReiniciar = (Button) findViewById(R.id.btnReiniciar);
        btnReiniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });
    }

    private void obtenerReferenciasYSetearListeners(){
        imbBandera1 = (ImageButton) findViewById(R.id.imbBandera1);
        imbBandera2 = (ImageButton) findViewById(R.id.imbBandera2);
        imbBandera3 = (ImageButton) findViewById(R.id.imbBandera3);
        imbBandera4 = (ImageButton) findViewById(R.id.imbBandera4);
        tvwPais = (TextView) findViewById(R.id.tvwPais);
        tvwCorrecto = (TextView) findViewById(R.id.tvwCorrecto);
        tvwPuntaje = (TextView) findViewById(R.id.tvwScore);
        vecPaises = getResources().getStringArray(R.array.paises_array);
        imbBandera1.setOnClickListener(clickImg);
        imbBandera2.setOnClickListener(clickImg);
        imbBandera3.setOnClickListener(clickImg);
        imbBandera4.setOnClickListener(clickImg);
    }

    private View.OnClickListener clickImg = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == idCorrecto){
                tvwCorrecto.setText("Correcto");
                puntaje++;
            }
            else {
                tvwCorrecto.setText("Incorrecto");
                puntaje--;
            }
            tvwPuntaje.setText(String.valueOf(puntaje));
            elegirPais();
        }
    };

    private void elegirPais(){
        Random r = new Random();
        List lstRepetido = new ArrayList();
        int[] vecIdImb = new int[4];
        vecIdImb[0] = imbBandera1.getId();
        vecIdImb[1] = imbBandera2.getId();
        vecIdImb[2] = imbBandera3.getId();
        vecIdImb[3] = imbBandera4.getId();
        String packageName = getPackageName();
        Resources resources = getResources();
        int vecLenght = vecPaises.length;
        int resID;
        for (int i = 4; i>0; i--) {
            String strPais = vecPaises[r.nextInt(vecLenght)];
            int cont = 0;
            while (lstRepetido.contains(strPais) || lstRepetidoGlobal.contains(strPais)){
                cont++;
                strPais = vecPaises[r.nextInt(vecLenght)];
            }
            if (cont >= 10){
                imbBandera1.setEnabled(false);
                imbBandera2.setEnabled(false);
                imbBandera3.setEnabled(false);
                imbBandera4.setEnabled(false);
                //PUNTAJE
            }
            lstRepetido.add(strPais);
            resID = resources.getIdentifier(strPais, "drawable", packageName);
            int intRandom = r.nextInt(4);
            while (vecIdImb[intRandom] == 0){
                intRandom = r.nextInt(4);
            }
            ImageButton imbTemp = (ImageButton) findViewById(vecIdImb[intRandom]);
            vecIdImb[intRandom] = 0;
            imbTemp.setImageResource(resID);
            if (i == 4){
                idCorrecto = imbTemp.getId();
                tvwPais.setText(strPais);
                lstRepetidoGlobal.add(strPais);
            }
        }
    }
}