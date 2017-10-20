package com.salmun.dani.geoport;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
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

import java.util.ArrayList;
import java.util.Random;

public class UbicacionActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    LatLng coordenadaCorrecta;
    int puntaje;
    float distanciaUsada = 0;
    final float limiteDistancia = 20000;
    int contIntento = 0;

    String[] vecCiudades;
    String[] vecPaises;
    int[] vecDificultad;
    String[] vecCoordenadas;

    ArrayList<Integer> lstCiudadesRepetidas = new ArrayList<>();

    TextView tvwPuntaje;
    TextView tvwPais;
    Button btnProxActividad;

    boolean cambioCamara;
    boolean fin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        obtenerReferencias();
        cargarVectores();
        if (savedInstanceState != null) {
            puntaje = savedInstanceState.getInt("intPuntaje");
            contIntento = savedInstanceState.getInt("contIntento");
            distanciaUsada = savedInstanceState.getFloat("distanciaUsada");
        } else {
            puntaje = Usuario.leerPuntaje();
        }
        String puntajeAMostrar = String.valueOf(puntaje) + "\nLimite: " + String.valueOf(Math.round(distanciaUsada))
                + "/" + String.valueOf(Math.round(limiteDistancia)) + "km";
        tvwPuntaje.setText(puntajeAMostrar);
        elegirCiudad();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("intPuntaje", puntaje);
        savedInstanceState.putInt("contIntento", contIntento);
        savedInstanceState.putFloat("distanciaUsada", distanciaUsada);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
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

    //MAP
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                getApplicationContext(), R.raw.estilomapa));
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0,0), 1f));
        mMap.setOnMapClickListener(clickMapa);
        mMap.setOnCameraMoveStartedListener(camaraMoviendose);
        mMap.setOnCameraIdleListener(camaraQuieta);
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
            int padding = (int) (getResources().getDisplayMetrics().widthPixels * 0.12);
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

    //CIUDAD
    private void elegirCiudad(){
        int posicion = sacarPosicion();

        lstCiudadesRepetidas.add(posicion);

        String[] args = vecCoordenadas[posicion].split(",");
        coordenadaCorrecta = new LatLng(Double.valueOf(args[0]),
                Double.valueOf(args[1]));
        String strMostrar;
        if (contIntento > 5) {
            strMostrar = vecCiudades[posicion];
        } else {
            strMostrar = vecCiudades[posicion] + ", " + vecPaises[posicion];
        }
        tvwPais.setText(strMostrar);
    }

    private int sacarPosicion(){
        Random r = new Random();
        int posicion;
        if(contIntento > 5){
            if (contIntento > 10){
                do {
                    posicion = r.nextInt(vecCiudades.length);
                } while (lstCiudadesRepetidas.contains(posicion));
            }
            do {
                posicion = r.nextInt(vecCiudades.length);
            } while (vecDificultad[posicion] == 2
                    || lstCiudadesRepetidas.contains(posicion));
        }
        else {
            do {
                posicion = r.nextInt(vecCiudades.length);
            } while (lstCiudadesRepetidas.contains(posicion));
        }
        return posicion;
    }

    //DISTANCIA
    private float calcularDistancia(LatLng posicionUsuario){
        Location markerCorrecto = new Location("");
        markerCorrecto.setLatitude(coordenadaCorrecta.latitude);
        markerCorrecto.setLongitude(coordenadaCorrecta.longitude);
        Location markerUsuario = new Location("");
        markerUsuario.setLatitude(posicionUsuario.latitude);
        markerUsuario.setLongitude(posicionUsuario.longitude);
        return markerCorrecto.distanceTo(markerUsuario);
    }

    //PUNTAJE
    private void calcularPuntaje(float distancia){
        int agregarPuntaje = 4;
        String mensajeDistancia = "";
        if(distancia < 200){
            agregarPuntaje = 20;
            mensajeDistancia = getString(R.string.perfectoUbicacion);
        } else if(distancia < 400){
            agregarPuntaje = 8;
            mensajeDistancia = getString(R.string.bienUbicacion);
        }
        if (!mensajeDistancia.isEmpty()) {
            Toast.makeText(getApplicationContext(),
                    mensajeDistancia + " (" + String.valueOf(Math.round(distancia)) + "km)",
                    Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(), String.valueOf(Math.round(distancia)) + "km",
                    Toast.LENGTH_LONG).show();
        }

        puntaje += agregarPuntaje;

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

    //CIRCULOS
    private void dibujarFiguras(float distancia, LatLng coordenadaUsuario){
        CircleOptions circuloCorrecto = new CircleOptions()
                .radius(distancia * 18)
                .center(coordenadaCorrecta)
                .fillColor(Color.GREEN)
                .strokeWidth(2);

        CircleOptions circuloUsuario = new CircleOptions()
                .radius(distancia * 18)
                .center(coordenadaUsuario)
                .fillColor(Color.RED)
                .strokeWidth(2)
                .visible(true);

        mMap.addCircle(circuloCorrecto);
        mMap.addCircle(circuloUsuario);
    }

    //CAMARA LISTENERS
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
                        if(fin){
                            mMap.setOnMapClickListener(null);
                        }
                    }
                }
            };

    //NUEVA ACTIVIDAD
    private void nuevaActividad(){
        fin = true;
        btnProxActividad.setVisibility(View.VISIBLE);
        tvwPais.setText(R.string.gameOver);
        btnProxActividad.setOnClickListener(clickProxActividad);
    }

    private View.OnClickListener clickProxActividad = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), PuntuacionActivity.class);
            Usuario.escribirPuntaje(puntaje);
            startActivity(intent);
            finish();
        }
    };
}