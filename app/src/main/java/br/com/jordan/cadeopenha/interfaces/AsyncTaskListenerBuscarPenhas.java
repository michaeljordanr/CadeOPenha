package br.com.jordan.cadeopenha.interfaces;


import br.com.jordan.cadeopenha.model.Penhas;

public interface AsyncTaskListenerBuscarPenhas {
    public void onTaskCompleteAutenticarAPI(Penhas result);
}
