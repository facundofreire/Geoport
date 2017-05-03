package com.salmun.dani.geoport;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
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
    ArrayList<String> lstRepetidoGlobal = new ArrayList<>();

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

    //ELIMINAR CODIGO TEMPORAL (BTNREINICIAR EN ONCREATE, CONT EN ELEGIRPAIS)
    //CAMBIAR SISTEMA DE PUNTAJE, AGREGAR COMBO TIEMPO Y FUNCION PARA PROXACTIVITY

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
            }
            tvwPuntaje.setText(String.valueOf(puntaje));
            elegirPais();
        }
    };

    private void elegirPais(){
        Random r = new Random();
        ArrayList<String> lstRepetido = new ArrayList<>();
        int[] vecIdImb = new int[4];
        vecIdImb[0] = imbBandera1.getId();
        vecIdImb[1] = imbBandera2.getId();
        vecIdImb[2] = imbBandera3.getId();
        vecIdImb[3] = imbBandera4.getId();
        String packageName = getPackageName();
        Resources resources = getResources();
        int intVecLenght = vecPaises.length;
        int resID;
        for (int i = 4; i>0; i--) {
            String strPais = vecPaises[r.nextInt(intVecLenght)];
            while (lstRepetido.contains(strPais) || lstRepetidoGlobal.contains(strPais)){
                strPais = vecPaises[r.nextInt(intVecLenght)];
            }
            if (lstRepetidoGlobal.size()>11){
                imbBandera1.setEnabled(false);
                imbBandera2.setEnabled(false);
                imbBandera3.setEnabled(false);
                imbBandera4.setEnabled(false);
                if (puntaje >= 10){
                    tvwCorrecto.setText("Ganaste");
                    tvwCorrecto.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                }
                else {
                    tvwCorrecto.setText("Perdiste");
                    tvwCorrecto.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                }
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