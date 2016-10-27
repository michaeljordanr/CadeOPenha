package br.com.jordan.cadeopenha2.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Michael on 18/07/2015.
 */
public class Penha implements Serializable {

    @SerializedName("p")
    @Expose
    private int numero;

    @SerializedName("py")
    @Expose
    private double latitude;

    @SerializedName("px")
    @Expose
    private double longitude;

    private double distancia;

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDistancia() {
        return distancia;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }
}
