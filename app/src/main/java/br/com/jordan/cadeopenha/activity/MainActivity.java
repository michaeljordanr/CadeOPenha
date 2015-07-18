package br.com.jordan.cadeopenha.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import br.com.jordan.cadeopenha.interfaces.AsyncTaskListenerBuscarPenhas;
import br.com.jordan.cadeopenha.R;
import br.com.jordan.cadeopenha.model.Penha;
import br.com.jordan.cadeopenha.model.Penhas;
import br.com.jordan.cadeopenha.task.BuscarPenhasTask;


public class MainActivity extends Activity implements OnMapReadyCallback, AsyncTaskListenerBuscarPenhas, GoogleMap.OnMarkerClickListener {

    private GoogleMap map;
    private ArrayList<String> nome = new ArrayList<>();
    private MapFragment mapFragment;
    private MarkerOptions optionsm;

    private Penhas penhasLocalizados;

    LatLng latLngDestino = new LatLng(-23.512102, -46.530783);
    LatLng latLngOrigem = new LatLng(-23.589442, -46.634740);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.getView().setVisibility(View.VISIBLE);

        BuscarPenhasTask task = new BuscarPenhasTask(this, this);
        task.execute();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        //map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        map.getUiSettings().setZoomControlsEnabled(true);

        LatLng locationSP = new LatLng(-23.550520, -46.633309);
        CameraPosition cameraPos = new CameraPosition.Builder().target(locationSP).zoom(20).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
    }


    @Override
    public void onTaskCompleteAutenticarAPI(Penhas result) {
        if (result != null) {
            penhasLocalizados = result;
            Toast.makeText(this, "Penhas localizados :)", Toast.LENGTH_LONG).show();

            markerPenhasOnMap(penhasLocalizados);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(2000);
                }
            });

        }else{
            Toast.makeText(this, "Nenhum penha encontrado :(", Toast.LENGTH_LONG).show();
        }

    }

    private void markerPenhasOnMap(Penhas penhas){
        for (Penha penha : penhas.getListPenha()) {

            optionsm = new MarkerOptions();

            LatLng latLng = new LatLng(penha.getLatitude(), penha.getLongitude());

            optionsm.position(latLng).title("Marcador " + penha.getNumero()).snippet("Número do Penha" + penha.getNumero())
                    //.icon(BitmapDescriptorFactory.fromResource(android.R.drawable.star_on));
                    .icon(BitmapDescriptorFactory.defaultMarker())
                    .draggable(true);

            Marker m = map.addMarker(optionsm);

        }
    }

    private void moverCameraPenhas(Penhas penhas){
        for (Penha penha : penhas.getListPenha()) {
            LatLng latLng = new LatLng(penha.getLatitude(), penha.getLongitude());
            CameraPosition cameraPos = new CameraPosition.Builder().target(latLng).zoom(8).build();
            CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPos);
            map.animateCamera(update);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(2000);
                }
            });
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        CameraPosition cameraPos = new CameraPosition.Builder().target(marker.getPosition()).zoom(20).build();
        CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPos);
        map.animateCamera(update);
        return false;
    }
}
