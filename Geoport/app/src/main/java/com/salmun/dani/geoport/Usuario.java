package com.salmun.dani.geoport;

import org.json.JSONObject;

import java.util.ArrayList;


public class Usuario {
    static private int puntaje;
    static private String id = "";
    static private String nombre = "";

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

    static String leerNombre(){
        return nombre;
    }

    static void escribirNombre(String nombreNuevo){
        nombre = nombreNuevo;
    }
}
