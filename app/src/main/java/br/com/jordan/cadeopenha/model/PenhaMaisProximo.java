package br.com.jordan.cadeopenha.model;

import com.google.android.gms.maps.model.LatLng;

public class PenhaMaisProximo {

    private float distancia;
    private LatLng posicao;

    public float getDistancia() {
        return distancia;
    }

    public void setDistancia(float distancia) {
        this.distancia = distancia;
    }

    public LatLng getPosicao() {
        return posicao;
    }

    public void setPosicao(LatLng posicao) {
        this.posicao = posicao;
    }
}
