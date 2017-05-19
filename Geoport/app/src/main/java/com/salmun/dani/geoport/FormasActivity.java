package com.salmun.dani.geoport;

import android.graphics.drawable.Drawable;
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

    String[] vecPaisesFaciles;
    ArrayList<Integer> lstIdBtn = new ArrayList<>();

    int contCombo = 0;
    int puntaje;

    int idCorrecto;

    TextView tvwPuntaje;
    TextView tvwTiempo;
    ImageView imvPais;


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
        imvPais = (ImageView) findViewById(R.id.imvPais);
        tvwPuntaje = (TextView) findViewById(R.id.tvwScore);
        tvwTiempo = (TextView) findViewById(R.id.tvwTimer);

        assert btn1 != null;
        assert btn2 != null;
        assert btn3 != null;
        assert btn4 != null;
        vecPaisesFaciles = getResources().getStringArray(R.array.paises_array_facil);
        lstIdBtn.add(btn1.getId());
        lstIdBtn.add(btn2.getId());
        lstIdBtn.add(btn3.getId());
        lstIdBtn.add(btn4.getId());

        btn1.setOnClickListener(onClickListener);
        btn2.setOnClickListener(onClickListener);
        btn3.setOnClickListener(onClickListener);
        btn4.setOnClickListener(onClickListener);
    }

    private void elegirPais(){
        Random r = new Random();
        String strPaisCorrecto = vecPaisesFaciles[r.nextInt(vecPaisesFaciles.length)];

        assert imvPais != null;
        imvPais.setImageDrawable(traerImagen(strPaisCorrecto));

        Collections.shuffle(lstIdBtn);
        idCorrecto = lstIdBtn.get(0);
        Button btnTemp = (Button) findViewById(idCorrecto);
        assert btnTemp != null;
        btnTemp.setText(strPaisCorrecto);

        ArrayList<String> lstPaisesRepetidos = new ArrayList<>();

        lstPaisesRepetidos.add(strPaisCorrecto);
        for (int i = 1; i < 4; i++){
            String strPaisesBotones = vecPaisesFaciles[r.nextInt(vecPaisesFaciles.length)];
            while(lstPaisesRepetidos.contains(strPaisesBotones)){
                strPaisesBotones = vecPaisesFaciles[r.nextInt(vecPaisesFaciles.length)];
            }
            lstPaisesRepetidos.add(strPaisesBotones);

            btnTemp = (Button) findViewById(lstIdBtn.get(i));
            assert btnTemp != null;
            btnTemp.setText(strPaisesBotones);
        }
    }

    @Nullable
    private Drawable traerImagen(String strPais){
        try {
            InputStream stream = getAssets().open("maps/" + strPais.replace(' ', '_') + ".png");
            return Drawable.createFromStream(stream, null);
        }
        catch(IOException ex) {
            return null;
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (idCorrecto == view.getId()){
                //TEMPORAL
                tvwTiempo.setText("Correcto");
                //TEMPORAL
                contCombo++;
                puntaje += contCombo * 2;
            }else{
                //TEMPORAL
                tvwTiempo.setText("Incorrecto");
                //TEMPORAL
                if (contCombo == 0){
                    puntaje -= 2;
                }
                contCombo = 0;
            }
            String strMostrarComboYPuntaje = "x" + String.valueOf(contCombo) + "\n" + String.valueOf(puntaje);
            tvwPuntaje.setText(strMostrarComboYPuntaje);
        }
    };
}