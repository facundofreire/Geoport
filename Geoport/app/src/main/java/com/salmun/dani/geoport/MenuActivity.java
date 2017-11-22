package com.salmun.dani.geoport;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MenuActivity extends AppCompatActivity {
    Button btnPlay, btnRanking, btnIdioma;
    LoginButton loginButton;

    CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    private AccessTokenTracker fbTracker;
    private SharedPreferences sharedPreferences;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_menu);
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (mAuth.getCurrentUser() != null && AccessToken.getCurrentAccessToken() != null) {
            handleFacebookAccessToken(AccessToken.getCurrentAccessToken());
        }

        ObtenerReferenciasYSetearListeners();

        btnIdioma.setText(getString(R.string.idioma));

        Usuario.setGuardoScore(false);

        fbTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken accessToken, AccessToken accessToken2) {
                if (accessToken2 == null) {
                    mAuth.signOut();
                    Usuario.escribirID("");
                    Usuario.escribirNombre("");
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.remove("listaAmigos");
                    editor.apply();
                    Log.e("Auth", "Sign out");
                }
            }
        };
        fbTracker.startTracking();
    }

    private void ObtenerReferenciasYSetearListeners() {
        btnIdioma = (Button) findViewById(R.id.btnIdioma);
        btnRanking = (Button) findViewById(R.id.btnRanking);
        btnPlay = (Button) findViewById(R.id.btnPlay);
        loginButton = (LoginButton) findViewById(R.id.btnLogin);

        btnPlay.setOnClickListener(btnPlayClick);
        btnIdioma.setOnClickListener(clickIdioma);
        btnRanking.setOnClickListener(clickRanking);
        loginButton.setOnClickListener(clickFacebook);
    }

    private View.OnClickListener clickFacebook = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            loginButton.setReadPermissions("user_friends");
            loginButton.setReadPermissions("public_profile");
            callbackManager = CallbackManager.Factory.create();
            loginButton.registerCallback(callbackManager, callback);
        }
    };

    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            handleFacebookAccessToken(loginResult.getAccessToken());
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/me/friends",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            try {
                                JSONArray json = response.getJSONObject().getJSONArray("data");
                                if (json.length()>0) {
                                    String listaAmigos = "";
                                    for (int i = 0; i < json.length(); i++) {
                                        JSONObject amigo = json.getJSONObject(i);
                                        listaAmigos += amigo.getString("id") + "°";
                                        Log.e("JSON", amigo.getString("name"));
                                    }
                                    listaAmigos = listaAmigos.substring(0, listaAmigos.length() - 2);
                                    SharedPreferences.Editor editor=sharedPreferences.edit();
                                    editor.putString("listaAmigos", listaAmigos);
                                    editor.apply();
                                }else{
                                    Log.e("JSON", "No tenes amigos xd");
                                }
                            }catch (JSONException e){
                                Log.e("JSON", "Error al parsear json");
                            }
                        }
                    }
            ).executeAsync();
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/me",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            try {
                                Usuario.escribirNombre(response.getJSONObject().getString("name"));
                            }catch (JSONException e){
                                Log.e("Menu", e.getLocalizedMessage());
                            }
                        }
                    }
            ).executeAsync();
        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onError(FacebookException error) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode ,resultCode, data);
    }

    private void handleFacebookAccessToken(final AccessToken token){
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (mAuth.getCurrentUser() != null) {
                                Usuario.escribirID(token.getUserId());
                                DatabaseReference myRef = database.getReference("users/" + Usuario.leerID());

                                myRef.child("puntaje").addListenerForSingleValueEvent(puntajeListener);
                                myRef.child("nombre").addListenerForSingleValueEvent(nombreListener);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private ValueEventListener puntajeListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (!dataSnapshot.exists()){
                DatabaseReference myRef = database.getReference("users/"
                        + Usuario.leerID() + "/puntaje");
                myRef.setValue(0);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private ValueEventListener nombreListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()){
                Usuario.escribirNombre(dataSnapshot.getValue(String.class));
            }else{
                DatabaseReference myRef = database.getReference("users/"
                        + Usuario.leerID() + "/nombre");
                myRef.setValue(Usuario.leerNombre());
            }
            Log.e("Menu", "Usuario main: " + Usuario.leerNombre());
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private View.OnClickListener btnPlayClick = new View.OnClickListener() {
        public void onClick(View v) {
            fbTracker.stopTracking();
            Intent intent = new Intent(getApplicationContext(), BanderasActivity.class);
            startActivity(intent);
            finish();
        }
    };

    private View.OnClickListener clickIdioma = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (LocaleHelper.getLanguage(getApplicationContext()).equals("en")){
                LocaleHelper.setLocale(getBaseContext(), "es");
            }else{
                LocaleHelper.setLocale(getBaseContext(), "en");
            }
            recreate();
        }
    };

    private View.OnClickListener clickRanking = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), RankingActivity.class);
            startActivity(intent);
        }
    };
}
