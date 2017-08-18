package com.salmun.dani.geoport;

/**
 * Created by 45017521 on 4/8/2017.
 */
public class Usuario {
    static private int puntaje;
    static private String fbKey;

    static public int leerPuntaje(){
        return puntaje;
    }

    static public void escribirPuntaje(int puntajeNuevo){
        puntaje = puntajeNuevo;
    }

    static public String leerKey(){
        return fbKey;
    }

    static public void escribirKey(String nuevaKey){
        fbKey = nuevaKey;
    }
}
