package com.salmun.dani.geoport;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MenuActivity extends AppCompatActivity {
    Button btnPlay;
    LoginButton loginButton;

    CallbackManager callbackManager;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_menu);
        mAuth = FirebaseAuth.getInstance();
        ObtenerReferenciasYSetearListeners();
    }

    private void ObtenerReferenciasYSetearListeners() {
        btnPlay = (Button) findViewById(R.id.btnPlay);
        loginButton = (LoginButton) findViewById(R.id.btnLogin);

        btnPlay.setOnClickListener(btnPlayClick);
        loginButton.setOnClickListener(clickFacebook);
    }

    private View.OnClickListener clickFacebook = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            loginButton.setReadPermissions("user_friends");
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
                                    for (int i = 0; i < json.length(); i++) {
                                        JSONObject amigo = json.getJSONObject(i);
                                        Usuario.anadirAmigo(amigo.getString("name"));
                                        Log.e("JSON", amigo.getString("id"));
                                    }
                                }else{
                                    Log.e("JSON", "No tenes amigos xd");
                                }
                            }catch (JSONException e){
                                Log.e("JSON", "Error al parsear json");
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
                                Usuario.escribirID(mAuth.getCurrentUser().getUid());
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference();
                                myRef.child("users").child(Usuario.leerID()).child("fbid").setValue(token.getUserId());
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private View.OnClickListener btnPlayClick = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), BanderasActivity.class);
            startActivity(intent);
            finish();
        }
    };
}
