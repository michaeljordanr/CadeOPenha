package br.com.jordan.cadeopenha.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.jordan.cadeopenha.R;
import br.com.jordan.cadeopenha.interfaces.AsyncTaskListenerBuscarPenhas;
import br.com.jordan.cadeopenha.interfaces.AsyncTaskListenerGetWaypoints;
import br.com.jordan.cadeopenha.model.Penha;
import br.com.jordan.cadeopenha.model.PenhaMaisProximo;
import br.com.jordan.cadeopenha.model.Penhas;
import br.com.jordan.cadeopenha.receiver.RadarPenhaReceiver;
import br.com.jordan.cadeopenha.task.BuscarPenhasTask;
import br.com.jordan.cadeopenha.util.GPSTracker;
import br.com.jordan.cadeopenha.util.GoogleDirection;
import br.com.jordan.cadeopenha.util.PenhaUtil;


public class MainActivity extends Activity implements OnMapReadyCallback, AsyncTaskListenerBuscarPenhas, AsyncTaskListenerGetWaypoints, GoogleMap.OnMarkerClickListener, GoogleMap.OnMyLocationChangeListener {

    private final static int DISTANCIA_LIMITE = 1000;
    private boolean FLAG_ROTA_DESENHADA = false;

    private GoogleMap map;
    private GPSTracker gps;
    //private AdView mAdView;
    private GoogleDirection objGoogleDirection;
    private PenhaUtil penhaUtil = new PenhaUtil();

    Locale locale = new Locale("pt", "BR");

    private MapFragment mapFragment;
    private MarkerOptions optionsm;
    private List<Marker> markersPenha = new ArrayList<>();
    private Marker markerLocale;
    float penhaMaisProximo = 0;
    Circle circleLocale;


    private Penhas penhasLocalizados = new Penhas();
    private BuscarPenhasTask task;

    LatLng latLngRotaPenha = new LatLng(-23.528918, -46.585642);
    LatLng latLngDestino = new LatLng(-23.512102, -46.530783);
    LatLng latLngOrigem = new LatLng(-23.589442, -46.634740);
    LatLng latLngCurrentLocation;

    PendingIntent pi;
    private boolean FLAG_PENHA_RADAR = true;

    private RadarPenhaReceiver receiver;

    private static String URL_PENHA = "http://apibus.smed.xyz/api/shapes/55264";
    private static String URL_VOLTA_PENHA = "http://apibus.smed.xyz/api/shapes/58713";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent alarmIntent = new Intent(this, RadarPenhaReceiver.class);
        pi = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

        setStatusBarColor(findViewById(R.id.statusBarBackground), getResources().getColor(android.R.color.background_dark));

        objGoogleDirection = new GoogleDirection(this);

        //mAdView = (AdView) findViewById(R.id.adView);
        //AdRequest adRequest = new AdRequest.Builder().build();
        //mAdView.loadAd(adRequest);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.getView().setVisibility(View.VISIBLE);

        /*GoogleAddressTask googleAddressTask = new GoogleAddressTask(this, this);
        googleAddressTask.execute(URL_PENHA);

        googleAddressTask = new GoogleAddressTask(this, this);
        googleAddressTask.execute(URL_VOLTA_PENHA);*/

        task = new BuscarPenhasTask(this, this);
        task.execute();
    }

    public void onRadar(View view) {
        if (!FLAG_PENHA_RADAR) {
            //receiver = new RadarPenhaReceiver();
            //IntentFilter intentFilter = new IntentFilter();
            //intentFilter.addAction("br.com.jordan.cadeopenha.LIGA_RADAR");
            //registerReceiver(receiver, intentFilter);

            //Intent intent = new Intent("br.com.jordan.cadeopenha.LIGA_RADAR");
            //sendBroadcast(intent);

            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            int interval = 10000;

            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pi);
            Toast.makeText(this, "Radar de Penha ligado!", Toast.LENGTH_SHORT).show();
            FLAG_PENHA_RADAR = true;
        }else{
            //unregisterReceiver(receiver);

            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            manager.cancel(pi);
            Toast.makeText(this, "Radar de Penha desligado!", Toast.LENGTH_SHORT).show();
            FLAG_PENHA_RADAR = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mAdView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mAdView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mAdView.destroy();
        //if(receiver != null) {
        //    unregisterReceiver(receiver);
        //}
    }

    private void setUpMap() {
        map.setMyLocationEnabled(true);
        map.setOnMyLocationChangeListener(this);
        map.getUiSettings().setMyLocationButtonEnabled(false);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        setUpMap();

        CameraPosition cameraPos = new CameraPosition.Builder().target(latLngRotaPenha).zoom(11).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos));

        gps = new GPSTracker(this);

        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            latLngCurrentLocation = new LatLng(latitude, longitude);

        } else {
            Toast.makeText(this, R.string.gps_off, Toast.LENGTH_LONG).show();
        }

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                if (marker.getTitle() == null) {
                    return null;
                } else if (marker.getTitle().equals(getString(R.string.you_are_here))) {
                    View view = getLayoutInflater().inflate(R.layout.info_window_location, null);

                    TextView txtModelo = (TextView) view.findViewById(R.id.txt_location);
                    txtModelo.setText(marker.getSnippet());

                    return view;
                } else {
                    View view = getLayoutInflater().inflate(R.layout.info_window, null);

                    TextView txtModelo = (TextView) view.findViewById(R.id.txt_modelo);
                    txtModelo.setText(marker.getTitle());

                    TextView txtDistancia = (TextView) view.findViewById(R.id.txt_distancia);

                    if (latLngCurrentLocation != null) {
                        Location location = new Location("");
                        location.setLatitude(latLngCurrentLocation.latitude);
                        location.setLongitude(latLngCurrentLocation.longitude);

                        LatLng penha = marker.getPosition();

                        Location penhaLocation = new Location("");
                        penhaLocation.setLatitude(penha.latitude);
                        penhaLocation.setLongitude(penha.longitude);
                        float distancia = location.distanceTo(penhaLocation);

                        if (distancia < 1000) {
                            txtDistancia.setText(String.format(locale, "Você está a %.2f\n metros de distância", distancia));
                        } else {
                            txtDistancia.setText(String.format(locale, "Você está a %.2f\n kilômetros de distância", distancia / 1000));
                        }
                    } else {
                        txtDistancia.setVisibility(View.INVISIBLE);
                    }
                    return view;
                }
            }
        });
    }

    private void markerPenhasOnMap(Penhas penhas) {
        for (Penha penha : penhas.getListPenha()) {

            optionsm = new MarkerOptions();

            LatLng latLng = new LatLng(penha.getLatitude(), penha.getLongitude());

            optionsm.position(latLng).title("Penha " + penha.getNumero())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_penha))
                    .draggable(false);

            markersPenha.add(map.addMarker(optionsm));
        }
    }

    private void markerPenhasOffOnMap(Penhas penhas) {
        for (Penha penha : penhas.getListPenha()) {

            optionsm = new MarkerOptions();

            LatLng latLng = new LatLng(penha.getLatitude(), penha.getLongitude());

            optionsm.position(latLng).title("Penha voltando " + penha.getNumero())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_penha0))
                    .draggable(false);

            markersPenha.add(map.addMarker(optionsm));
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
    public void onTaskCompleteAutenticarAPI(List<Penhas> result) {
        Penhas penhas = result.get(0);
        Penhas penhasOff = result.get(1);

        if (penhas.getListPenha().size() > 0 || penhasOff.getListPenha().size() > 0) {
            penhasLocalizados = penhas;
            Toast.makeText(this, "Penhas localizados :)", Toast.LENGTH_LONG).show();
            markerPenhasOnMap(penhasLocalizados);
            markerPenhasOffOnMap(penhasOff);
            //setMarkerLocale();
        } else {
            Toast.makeText(this, "Nenhum penha encontrado :(", Toast.LENGTH_LONG).show();
        }
    }

    public void setMarkerLocale() {
        if (markerLocale != null) {
            markerLocale.remove();
        }

        if (gps.canGetLocation()) {
            PenhaMaisProximo obj = penhaUtil.getPenhaMaisProximo(penhasLocalizados, latLngCurrentLocation);

            LatLng penhaProx = obj.getPosicao();
            penhaMaisProximo = obj.getDistancia();

            double distancia = objGoogleDirection.distance(latLngCurrentLocation.latitude, latLngCurrentLocation.longitude, penhaProx.latitude, penhaProx.longitude, 'K');
            distancia = distancia * 1000; //metter to km

            //Toast.makeText(this, String.format(locale, "Penha mais proximo esta num raio de: %.2f metros de você", (distancia)), Toast.LENGTH_LONG).show();

            //optionsm = new MarkerOptions();
            //optionsm.position(latLngCurrentLocation).title(getString(R.string.you_are_here))
            //        .icon(BitmapDescriptorFactory.fromResource(R.drawable.rsz_cool))
            //        .draggable(false);

            if (penhaMaisProximo > 0) {

                if (penhaMaisProximo < 1000) {
                    optionsm.snippet(String.format(locale, "O Penha mais próximo está a\n %.2f metros de você", penhaMaisProximo));
                } else {
                    optionsm.snippet(String.format(locale, "O Penha mais próximo está a\n %.2f kilômetros de você", (penhaMaisProximo / 1000)));
                }
            }

            markerLocale = map.addMarker(optionsm);
            drawCircle(latLngCurrentLocation);
        }
    }

    @Override
    public void onTaskCompleteGetWaypoints(List<LatLng> result, int sentido) {
        int color = Color.BLACK;

        if(sentido == 1){
            color = Color.RED;
        }

        LatLng latLngAnteror = result.get(0);
        result.remove(0);
        for (LatLng waypoint : result) {

            PolylineOptions options = new PolylineOptions();
            options.add(latLngAnteror);
            options.add(waypoint);
            options.width(3);
            options.geodesic(true);
            options.color(color);
            Polyline p = map.addPolyline(options);
            latLngAnteror = waypoint;

            if (!FLAG_ROTA_DESENHADA) {
                FLAG_ROTA_DESENHADA = true;
            }
        }

        map.addMarker(new MarkerOptions().position(latLngOrigem)
                .icon(BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_YELLOW)));

        map.addMarker(new MarkerOptions().position(latLngDestino)
                .icon(BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_YELLOW)));
    }

    @Override
    public void onMyLocationChange(Location location) {
        double latOld = 0;
        double longOld = 0;
        if(latLngCurrentLocation != null) {
            latOld = latLngCurrentLocation.latitude;
            longOld = latLngCurrentLocation.longitude;
        }

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        latLngCurrentLocation = new LatLng(latitude, longitude);

        if(latitude != latOld && longitude != longOld) {
            if(markerLocale != null)
                markerLocale.remove();

            //optionsm = new MarkerOptions();
            //optionsm.position(latLngCurrentLocation).title(getString(R.string.you_are_here))
            //        .icon(BitmapDescriptorFactory.fromResource(R.drawable.rsz_cool))
            //        .draggable(false);

            //markerLocale = map.addMarker(optionsm);
            drawCircle(latLngCurrentLocation);
        }
    }


    public void refreshLocation(View view) {
        if(latLngCurrentLocation == null) {
            gps = new GPSTracker(this);
            latLngCurrentLocation = new LatLng(gps.getLatitude(), gps.getLongitude());
        }

        if (gps.canGetLocation() && latLngCurrentLocation != null) {
            CameraPosition cameraPos = new CameraPosition.Builder().target(latLngCurrentLocation).zoom(map.getCameraPosition().zoom).build();
            CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPos);
            map.animateCamera(update);

            //setMarkerLocale();
        } else {
            Toast.makeText(this, getString(R.string.gps_off), Toast.LENGTH_SHORT).show();
        }
    }

    public void refreshPenhaOnMap(View view) {
        /*if (!FLAG_ROTA_DESENHADA) {
            GoogleAddressTask googleAddressTask = new GoogleAddressTask(this, this);
            googleAddressTask.execute();
        }

        for (Marker m : markersPenha) {
            m.remove();
        }*/

        task = new BuscarPenhasTask(this, this);
        task.execute();

        //setMarkerLocale();

        //CameraPosition cameraPos = new CameraPosition.Builder().target(latLngRotaPenha).zoom(11).build();
        //map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
    }

    public void setStatusBarColor(View statusBar, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //status bar height
            int actionBarHeight = getActionBarHeight();
            int statusBarHeight = getStatusBarHeight();
            //action bar height
            statusBar.getLayoutParams().height = actionBarHeight + statusBarHeight;
            statusBar.setBackgroundColor(color);
        }
    }

    public int getActionBarHeight() {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void drawCircle(LatLng point) {
        if(circleLocale != null){
            circleLocale.remove();
        }
        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();

        // Specifying the center of the circle
        circleOptions.center(point);

        // Radius of the circle
        circleOptions.radius(1000);

        // Border color of the circle
        circleOptions.strokeColor(0xff00ffff);

        // Fill color of the circle
        circleOptions.fillColor(0x4000ffff);

        // Border width of the circle
        circleOptions.strokeWidth(1);

        // Adding the circle to the GoogleMap
        circleLocale = map.addCircle(circleOptions);
    }


}
