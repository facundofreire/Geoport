package com.salmun.dani.geoport;

import android.support.annotation.Keep;

/**
 * Created by Dani on 08/11/2017.
 */
@Keep
public class Amigo {
    public int puntaje;
    public String nombre;

    @Override
    public String toString(){
        return nombre + "   " + puntaje;
    }
}
