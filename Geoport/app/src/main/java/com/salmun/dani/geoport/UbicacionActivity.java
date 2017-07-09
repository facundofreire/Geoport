package com.salmun.dani.geoport;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;

import java.util.Random;

public class UbicacionActivity extends FragmentActivity implements OnMapReadyCallback {

    //TODO: PUNTAJE
    private GoogleMap mMap;

    LatLng coordenadaCorrecta = new LatLng(0, 0);
    int puntaje;
    float distanciaUsada = 0;
    final float limiteDistancia = 40000;
    int contIntento = 0;

    String[] vecCiudades;
    String[] vecPaises;
    int[] vecDificultad;
    String[] vecCoordenadas;

    TextView tvwPuntaje;
    TextView tvwPais;
    Button btnProxActividad;

    boolean cambioCamara;

    private static final String TAG = "UbicacionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        obtenerReferencias();
        cargarVectores();
        puntaje = getIntent().getIntExtra("intPuntaje", 0);
        String puntajeAMostrar = String.valueOf(puntaje) + "\nLimite: " + String.valueOf(Math.round(distanciaUsada))
                + "/" + String.valueOf(Math.round(limiteDistancia)) + "km";
        tvwPuntaje.setText(puntajeAMostrar);
        elegirCiudad();
    }

    private void obtenerReferencias(){
        tvwPuntaje = (TextView) findViewById(R.id.tvwPuntaje);
        tvwPais = (TextView) findViewById(R.id.tvwPais);
        btnProxActividad = (Button) findViewById(R.id.btnContinuar);
    }

    private void cargarVectores(){
        Resources res = getResources();
        vecCiudades = res.getStringArray(R.array.capitales_array);
        vecCoordenadas = res.getStringArray(R.array.coordenadas_array);
        vecDificultad = res.getIntArray(R.array.dificultadPaises);
        vecPaises = res.getStringArray(R.array.paises_array);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                getApplicationContext(), R.raw.estilomapa));
        mMap = googleMap;
        mMap.setOnMapClickListener(clickMapa);
        mMap.setOnCameraMoveStartedListener(camaraMoviendose);
        mMap.setOnCameraIdleListener(camaraQuieta);
    }

    private void elegirCiudad(){
        Random r = new Random();
        int dificultad = 0;
        int posicion;
        do {
            posicion = r.nextInt(vecCiudades.length);
        }while (false);
        String[] args = vecCoordenadas[posicion].split(",");
        coordenadaCorrecta = new LatLng(Double.valueOf(args[0]),
                Double.valueOf(args[1]));
        String strMostrar = vecCiudades[posicion] + ", " + vecPaises[posicion];
        tvwPais.setText(strMostrar);
    }

    public GoogleMap.OnMapClickListener clickMapa = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng coordenadaUsuario) {
            mMap.setOnMapClickListener(null);
            mMap.clear();

            contIntento++;

            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            boundsBuilder.include(coordenadaUsuario);
            boundsBuilder.include(coordenadaCorrecta);
            LatLngBounds bounds = boundsBuilder.build();
            int padding = (int) (getResources().getDisplayMetrics().widthPixels * 0.10);
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));

            float distancia = calcularDistancia(coordenadaUsuario) / 1000;
            dibujarFiguras(distancia, coordenadaUsuario);
            calcularPuntaje(distancia);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0,0), 1f));
                }
            }, 2500);
        }
    };

    private GoogleMap.OnCameraMoveStartedListener camaraMoviendose =
            new GoogleMap.OnCameraMoveStartedListener() {
        @Override
        public void onCameraMoveStarted(int i) {
            if (i == GoogleMap.OnCameraMoveStartedListener
                    .REASON_DEVELOPER_ANIMATION ||
                    i == GoogleMap.OnCameraMoveStartedListener
                            .REASON_API_ANIMATION) {
                mMap.getUiSettings().setScrollGesturesEnabled(false);
                cambioCamara = true;
            }
            else{
                cambioCamara = false;
            }
        }
    };

    private GoogleMap.OnCameraIdleListener camaraQuieta =
            new GoogleMap.OnCameraIdleListener() {
        @Override
        public void onCameraIdle() {
            if (cambioCamara) {
                mMap.setOnMapClickListener(clickMapa);
                mMap.getUiSettings().setScrollGesturesEnabled(true);
            }
        }
    };

    private float calcularDistancia(LatLng posicionUsuario){
        Location markerCorrecto = new Location("");
        markerCorrecto.setLatitude(coordenadaCorrecta.latitude);
        markerCorrecto.setLongitude(coordenadaCorrecta.longitude);
        Location markerUsuario = new Location("");
        markerUsuario.setLatitude(posicionUsuario.latitude);
        markerUsuario.setLongitude(posicionUsuario.longitude);
        return markerCorrecto.distanceTo(markerUsuario);
    }

    private void calcularPuntaje(float distancia){
        Toast.makeText(getApplicationContext(), String.valueOf(distancia),
                Toast.LENGTH_LONG).show();
        distanciaUsada += distancia;
        if (limiteDistancia < distanciaUsada){
            nuevaActividad();
        }else{
            elegirCiudad();
        }
        String puntajeAMostrar = String.valueOf(puntaje) + "\nLimite: " + String.valueOf(Math.round(distanciaUsada))
                + "/" + String.valueOf(Math.round(limiteDistancia)) + "km";
        tvwPuntaje.setText(puntajeAMostrar);
    }

    private void dibujarFiguras(float distancia, LatLng coordenadaUsuario){
        CircleOptions circuloCorrecto = new CircleOptions()
                .radius(distancia * 10)
                .center(coordenadaCorrecta)
                .fillColor(Color.GREEN)
                .strokeWidth(5);

        CircleOptions circuloUsuario = new CircleOptions()
                .radius(distancia * 10)
                .center(coordenadaUsuario)
                .fillColor(Color.RED)
                .strokeWidth(5)
                .visible(true);

        mMap.addCircle(circuloCorrecto);
        mMap.addCircle(circuloUsuario);
    }

    private void nuevaActividad(){
        btnProxActividad.setVisibility(View.VISIBLE);
        tvwPais.setText(R.string.fin);
        mMap.setOnMapClickListener(null);
        btnProxActividad.setOnClickListener(clickProxActividad);
    }

    private View.OnClickListener clickProxActividad = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), FormasActivity.class);
            intent.putExtra("intPuntaje", puntaje);
            startActivity(intent);
            finish();
        }
    };
}