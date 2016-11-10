package br.com.jordan.cadeopenha.interfaces;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public interface AsyncTaskListenerGetWaypoints {
    public void onTaskCompleteGetWaypoints(List<LatLng> result, int sentido);
}
