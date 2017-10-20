package com.salmun.dani.geoport;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by 45017521 on 4/8/2017.
 */
public class Usuario {
    static private int puntaje;
    static private String id;
    static private ArrayList<String> lstAmigos = new ArrayList<>();

    static int leerPuntaje(){
        return puntaje;
    }

    static void escribirPuntaje(int puntajeNuevo){
        puntaje = puntajeNuevo;
    }

    static String leerID(){
        return id;
    }

    static void escribirID(String nuevaID){
        id = nuevaID;
    }

    static void anadirAmigo(String idAmigo){
        lstAmigos.add(idAmigo);
    }
}
