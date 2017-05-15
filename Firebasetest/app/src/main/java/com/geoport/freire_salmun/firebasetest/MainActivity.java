package com.geoport.freire_salmun.firebasetest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("message");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView tvwBajar = (TextView) findViewById(R.id.tvwBajar);
        final EditText edtBajar = (EditText) findViewById(R.id.edtSubir);
        Button btnSubir = (Button) findViewById(R.id.btnSubir);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String strVar = dataSnapshot.getValue(String.class);
                tvwBajar.setText(strVar);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                tvwBajar.setText("Fallo en la conexión");
            }
        });
        btnSubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    myRef.setValue(edtBajar.getText().toString());
                }catch (Throwable n){}
            }
        });
    }
}
