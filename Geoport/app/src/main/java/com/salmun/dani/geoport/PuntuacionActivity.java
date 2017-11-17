package com.salmun.dani.geoport;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            if (mAuth != null) {
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("users/" + Usuario.leerID());
                myRef.child("puntaje").addListenerForSingleValueEvent(postListener);
                Intent intent = new Intent(getApplicationContext(), RankingActivity.class);
                startActivity(intent);
            }
        }
    };

    ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                int puntajeDatabase = dataSnapshot.getValue(int.class);
                if (puntajeDatabase < puntaje) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("users/" + Usuario.leerID());
                    myRef.child("puntaje").setValue(puntaje);
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private View.OnClickListener clickMenu = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(Usuario.isGuardoScore()) {
                nuevaActividad();
            }else{
                llamarDialog();
            }
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
                        nuevaActividad();
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

    private void nuevaActividad(){
        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
        startActivity(intent);
        finish();
    }
}
