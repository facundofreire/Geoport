package com.salmun.dani.geoport;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class BanderasActivity extends AppCompatActivity {
    String[] vecPaises;
    TextView tvwPais;
    TextView tvwCorrecto;
    TextView tvwPuntaje;
    int idCorrecto;
    int puntaje = 0;
    int contCombo = 0;
    ArrayList<Integer> lstIdImb = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banderas);
        obtenerReferenciasYSetearListeners();
        elegirPais();
        //Codigo temporal
        Button btnReiniciar = (Button) findViewById(R.id.btnReiniciar);
        btnReiniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });
        //Codigo temporal
    }

    //ELIMINAR CODIGO TEMPORAL (BTNREINICIAR EN ONCREATE, CONT EN ELEGIRPAIS)
    //SUBIR DIFICULTAD CUANDO SUBE COMBO
    //CAMBIAR SISTEMA DE PUNTAJE, AGREGAR COMBO, TIEMPO Y FUNCION PARA PROXACTIVITY

    private void obtenerReferenciasYSetearListeners(){
        ImageButton imbBandera1 = (ImageButton) findViewById(R.id.imbBandera1);
        ImageButton imbBandera2 = (ImageButton) findViewById(R.id.imbBandera2);
        ImageButton imbBandera3 = (ImageButton) findViewById(R.id.imbBandera3);
        ImageButton imbBandera4 = (ImageButton) findViewById(R.id.imbBandera4);
        tvwPais = (TextView) findViewById(R.id.tvwPais);
        tvwCorrecto = (TextView) findViewById(R.id.tvwCorrecto);
        tvwPuntaje = (TextView) findViewById(R.id.tvwScore);

        vecPaises = getResources().getStringArray(R.array.paises_array);
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
                tvwCorrecto.setText("Correcto");
                contCombo++;
                puntaje += contCombo / 2 + 1;
            }
            else {
                tvwCorrecto.setText("Incorrecto");
                contCombo = 0;
            }
            tvwPuntaje.setText(String.valueOf(puntaje));
            elegirPais();
        }
    };

    private void elegirPais(){
        Random r = new Random();
        ArrayList<String> lstRepetido = new ArrayList<>();
        int intVecLenght = vecPaises.length;
        Collections.shuffle(lstIdImb);
        String error = "";
        for (int i = 0; i<4; i++) {
            String strPais = vecPaises[r.nextInt(intVecLenght)];
            while (lstRepetido.contains(strPais)){
                strPais = vecPaises[r.nextInt(intVecLenght)];
            }
            lstRepetido.add(strPais);

            ImageButton imbTemp = (ImageButton) findViewById(lstIdImb.get(i));
            imbTemp.setImageDrawable(traerImagen(strPais));
            error += "  " + strPais;
            if (i == 0){
                idCorrecto = imbTemp.getId();
                strPais = strPais.substring(0, 1).toUpperCase() + strPais.substring(1);
                tvwPais.setText(strPais);
            }
        }
        tvwCorrecto.setText(error);
    }

    private Drawable traerImagen(String strPais){
        try {
            InputStream stream = getAssets().open("flags/" + strPais.replace(' ', '_') + ".png");
            Drawable imagen = Drawable.createFromStream(stream, null);
            return imagen;
        }
        catch(IOException ex) {
            return null;
        }
    }
}