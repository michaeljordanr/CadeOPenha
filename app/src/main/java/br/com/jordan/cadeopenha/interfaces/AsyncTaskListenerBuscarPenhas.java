package br.com.jordan.cadeopenha.interfaces;


import java.util.List;

import br.com.jordan.cadeopenha.model.Penhas;

public interface AsyncTaskListenerBuscarPenhas {
    public void onTaskCompleteAutenticarAPI(List<Penhas> result);
}
