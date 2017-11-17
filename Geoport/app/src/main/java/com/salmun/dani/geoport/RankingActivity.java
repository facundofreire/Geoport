package com.salmun.dani.geoport;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RankingActivity extends AppCompatActivity {

    ArrayList<Amigo> lstAmigos = new ArrayList<>();
    ListView lvwRanking;
    SharedPreferences sharedPreferences;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final String TAG = "RankingActivity";
    int cantAmigos = 9999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        obtenerReferencias();
        Usuario.setGuardoScore(true);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        llenarLista();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    private void obtenerReferencias(){
        lvwRanking = (ListView) findViewById(R.id.lvwRanking);
    }

    private void llenarLista(){
        if (Usuario.leerID().isEmpty()){
            ArrayList<String> lstNombres = new ArrayList<>();
            lstNombres.add("¡Logeate a facebook en el menu principal para ver los puntajes de tus amigos!");
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.cell, lstNombres);
            if (lvwRanking != null){
                lvwRanking.setAdapter(arrayAdapter);
            }
        }else{
            DatabaseReference mDatabase = database.getReference("users");

            String listaAmigos = sharedPreferences.getString("listaAmigos", "");
            if (!listaAmigos.isEmpty()){
                String[] vecAmigos = listaAmigos.split("°");
                cantAmigos = vecAmigos.length;
                for (String id:vecAmigos) {
                    mDatabase.child(id).addListenerForSingleValueEvent(scoreListener);

                    Log.e(TAG, "Id amigo: " + id);
                }
            }else{
                cantAmigos = 0;
            }
            mDatabase.child(Usuario.leerID()).addListenerForSingleValueEvent(scoreListener);
        }
    }

    ValueEventListener scoreListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Amigo oAmigo = dataSnapshot.getValue(Amigo.class);
            lstAmigos.add(oAmigo);
            if (lstAmigos.size() > cantAmigos){
                Collections.sort(lstAmigos, Collections.reverseOrder(new Comparator<Amigo>() {
                    @Override
                    public int compare(Amigo o1, Amigo o2) {
                        Log.e(TAG, "Puntaje: " + String.valueOf(o1.puntaje));
                        return Integer.compare(o1.puntaje, o2.puntaje);
                    }
                }));

                ArrayAdapter<Amigo> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.cell, lstAmigos);
                if (lvwRanking != null){
                    lvwRanking.setAdapter(arrayAdapter);
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w("Firebase", "loadPost:onCancelled", databaseError.toException());
        }
    };
}