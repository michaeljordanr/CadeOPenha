package br.com.jordan.cadeopenha2.interfaces;


import java.util.List;

import br.com.jordan.cadeopenha2.model.Penhas;

public interface AsyncTaskListenerBuscarPenhas {
    public void onTaskCompleteAutenticarAPI(List<Penhas> result);
}
