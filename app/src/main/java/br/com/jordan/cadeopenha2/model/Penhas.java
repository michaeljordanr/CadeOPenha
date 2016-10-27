package br.com.jordan.cadeopenha2.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Penhas implements Serializable {

    @SerializedName("hora")
    @Expose
    private String hora;

    @SerializedName("vs")
    @Expose
    private List<Penha> listPenha;

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public List<Penha> getListPenha() {
        return listPenha;
    }

    public void setListPenha(List<Penha> listPenha) {
        this.listPenha = listPenha;
    }

}
