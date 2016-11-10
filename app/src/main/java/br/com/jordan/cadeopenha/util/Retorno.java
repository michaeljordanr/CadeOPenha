package br.com.jordan.cadeopenha.util;

import java.io.Serializable;

/**
 * Created by Michael on 06/11/2016.
 */

public class Retorno implements Serializable {
    private int statusCode;
    private String response;
    private String mensagem;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
