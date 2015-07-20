package br.com.jordan.cadeopenha.activity;

import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
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

import org.w3c.dom.Document;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import br.com.jordan.cadeopenha.interfaces.AsyncTaskListenerBuscarPenhas;
import br.com.jordan.cadeopenha.R;
import br.com.jordan.cadeopenha.interfaces.AsyncTaskListenerGetWaypoints;
import br.com.jordan.cadeopenha.model.Penha;
import br.com.jordan.cadeopenha.model.Penhas;
import br.com.jordan.cadeopenha.task.BuscarPenhasTask;
import br.com.jordan.cadeopenha.task.GoogleAddressTask;
import br.com.jordan.cadeopenha.util.GPSTracker;
import br.com.jordan.cadeopenha.util.GoogleDirection;


public class MainActivity extends Activity implements OnMapReadyCallback, AsyncTaskListenerBuscarPenhas, AsyncTaskListenerGetWaypoints, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener {

    private GoogleMap map;


    private GoogleDirection gd;
    private Document mDoc;
    private GPSTracker gps;

    private ArrayList<String> nome = new ArrayList<>();
    private MapFragment mapFragment;
    private MarkerOptions optionsm;

    private Penhas penhasLocalizados = new Penhas();

    LatLng latLngDestino = new LatLng(-23.512102, -46.530783);
    LatLng latLngOrigem = new LatLng(-23.589442, -46.634740);
    LatLng latLngCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.getView().setVisibility(View.VISIBLE);


        gd = new GoogleDirection(this);
        gd.setOnDirectionResponseListener(new GoogleDirection.OnDirectionResponseListener() {
            public void onResponse(String status, Document doc, GoogleDirection gd) {
                mDoc = doc;
                map.addPolyline(gd.getPolyline(doc, 3, Color.BLUE));
                map.addMarker(new MarkerOptions().position(latLngOrigem)
                        .icon(BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_GREEN)));

                map.addMarker(new MarkerOptions().position(latLngDestino)
                        .icon(BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_GREEN)));

            }
        });

        GoogleAddressTask googleAddressTask = new GoogleAddressTask(this, this);
        googleAddressTask.execute(getResources().getStringArray(R.array.waypoints));

        BuscarPenhasTask task = new BuscarPenhasTask(this, this);
        task.execute();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        //map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        map.getUiSettings().setZoomControlsEnabled(true);

        LatLng locationSP = new LatLng(-23.528918, -46.585642);
        CameraPosition cameraPos = new CameraPosition.Builder().target(locationSP).zoom(13).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos));

        gps = new GPSTracker(this);

        if(gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            latLngCurrentLocation = new LatLng(latitude, longitude);

        }else{
            Toast.makeText(this, "GPS desligado", Toast.LENGTH_LONG).show();
        }
    }

    private void markerPenhasOnMap(Penhas penhas, float penhaMaisProximo){
        for (Penha penha : penhas.getListPenha()) {

            optionsm = new MarkerOptions();

            LatLng latLng = new LatLng(penha.getLatitude(), penha.getLongitude());

            optionsm.position(latLng).title("Penha " + penha.getNumero()).snippet(getString(R.string.description_penha))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_penha))
                    //.icon(BitmapDescriptorFactory.defaultMarker())
                    .draggable(false);

            Marker m = map.addMarker(optionsm);

        }
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        CameraPosition cameraPos = new CameraPosition.Builder().target(marker.getPosition()).zoom(20).build();
        CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPos);
        map.animateCamera(update);
        return true;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }


    @Override
    public void onTaskCompleteAutenticarAPI(Penhas result) {
        if (result.getListPenha().size() > 0) {
            penhasLocalizados = result;
            Toast.makeText(this, "Penhas localizados :)", Toast.LENGTH_LONG).show();

            float penhaMaisProximo = 0;
            if(gps.canGetLocation()){
                penhaMaisProximo = getPenhaMaisProximo(penhasLocalizados, latLngCurrentLocation);
            }

            optionsm = new MarkerOptions();
            optionsm.position(latLngCurrentLocation).title(getString(R.string.you_are_here))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.rsz_cool))
                    .draggable(false);

            if(penhaMaisProximo > 0){
                Locale locale = new Locale("pt","BR");
                MessageFormat formatter = new MessageFormat("");
                if(penhaMaisProximo < 1000) {
                    optionsm.snippet(MessageFormat.format("O Penha mais próximo está a {0} metros de você", String.valueOf(penhaMaisProximo)));
                }else{
                    optionsm.snippet(MessageFormat.format("O Penha mais próximo está a {0} kilômetros de você", penhaMaisProximo));
                }
            }

            map.addMarker(optionsm);

            markerPenhasOnMap(penhasLocalizados, penhaMaisProximo);

        }else{
            Toast.makeText(this, "Nenhum penha encontrado :(", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onTaskCompleteGetWaypoints(List<LatLng> result) {
        gd.setLogging(true);
        for(LatLng waypoint : result) {
            gd.request(latLngOrigem, latLngDestino, waypoint, GoogleDirection.MODE_DRIVING);
        }
    }

    private float getPenhaMaisProximo(Penhas penhas, LatLng currentLocation){
        List<Float> distancias = new ArrayList<>();

        Location location = new Location("");
        location.setLatitude(currentLocation.latitude);
        location.setLongitude(currentLocation.longitude);

        for(Penha penha : penhas.getListPenha()) {
            Location penhaLocation = new Location("");
            penhaLocation.setLatitude(penha.getLatitude());
            penhaLocation.setLongitude(penha.getLongitude());
            distancias.add(location.distanceTo(penhaLocation));
        }

        Collections.sort(distancias);
        return distancias.get(0);
    }
}
