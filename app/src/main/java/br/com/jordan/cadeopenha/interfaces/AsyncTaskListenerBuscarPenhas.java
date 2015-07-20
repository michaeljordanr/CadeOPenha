package br.com.jordan.cadeopenha.interfaces;


import com.google.android.gms.maps.model.LatLng;

import br.com.jordan.cadeopenha.model.Penhas;

public interface AsyncTaskListenerBuscarPenhas {
    public void onTaskCompleteAutenticarAPI(Penhas result);
}
