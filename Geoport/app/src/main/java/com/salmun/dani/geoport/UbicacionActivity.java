package com.salmun.dani.geoport;

import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class UbicacionActivity extends FragmentActivity implements OnMapReadyCallback {

    //TODO: ZOOM MAXIMO, QUE FUNCIONE CON UNA CIUDAD, PUNTAJE, COMBO, TEXTVIEWS
    private GoogleMap mMap;
    LatLng posicionCorrecta = new LatLng(0, 0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(clickMapa);
    }

    public GoogleMap.OnMapClickListener clickMapa = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng posicionUsuario) {
            mMap.clear();

            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            boundsBuilder.include(posicionUsuario);
            boundsBuilder.include(posicionCorrecta);
            LatLngBounds bounds = boundsBuilder.build();
            int padding = (int) (getResources().getDisplayMetrics().widthPixels * 0.10);
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));

            float distancia = calcularDistancia(posicionUsuario);
            dibujarFiguras(distancia, posicionUsuario);
            calcularPuntaje(distancia);
        }
    };

    private float calcularDistancia(LatLng posicionUsuario){
        Location markerCorrecto = new Location("");
        markerCorrecto.setLatitude(posicionCorrecta.latitude);
        markerCorrecto.setLongitude(posicionCorrecta.longitude);
        Location markerUsuario = new Location("");
        markerUsuario.setLatitude(posicionUsuario.latitude);
        markerUsuario.setLongitude(posicionUsuario.longitude);
        return markerCorrecto.distanceTo(markerUsuario);
    }

    private void calcularPuntaje(float distancia){
        Toast.makeText(getApplicationContext(), String.valueOf(distancia/1000),
                Toast.LENGTH_LONG).show();
    }

    private void dibujarFiguras(float distancia, LatLng posicionUsuario){
        CircleOptions circuloCorrecto = new CircleOptions()
                .radius(distancia/100)
                .center(posicionCorrecta)
                .fillColor(Color.RED)
                .strokeWidth(5);

        CircleOptions circuloUsuario = new CircleOptions()
                .radius(distancia/100)
                .center(posicionUsuario)
                .fillColor(Color.GREEN)
                .strokeWidth(5);

        mMap.addCircle(circuloCorrecto);
        mMap.addCircle(circuloUsuario);
    }
}
