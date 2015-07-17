package br.com.jordan.cadeopenha.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.ArrayList;

import br.com.jordan.cadeopenha.interfaces.AsyncTaskListenerAutenticarAPI;
import br.com.jordan.cadeopenha.R;
import br.com.jordan.cadeopenha.task.AutenticarAPITask;


public class MainActivity extends Activity implements OnMapReadyCallback, AsyncTaskListenerAutenticarAPI {

    private GoogleMap map;
    private ArrayList<String> nome = new ArrayList<>();
    MapFragment mapFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AutenticarAPITask task = new AutenticarAPITask(this, this);
        task.execute();

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.getView().setVisibility(View.GONE);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        //map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        map.getUiSettings().setZoomControlsEnabled(true);

    }


    @Override
    public void onTaskCompleteAutenticarAPI(String result) {
        if (result.equals("true")) {
            Toast.makeText(this, getString(R.string.auth_success), Toast.LENGTH_LONG).show();
            mapFragment.getView().setVisibility(View.VISIBLE);
        }else{
            Toast.makeText(this, getString(R.string.auth_error), Toast.LENGTH_LONG).show();
        }

    }
}
