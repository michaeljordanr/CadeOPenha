package br.com.jordan.cadeopenha.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;

import br.com.jordan.cadeopenha.R;
import br.com.jordan.cadeopenha.activity.MainActivity;
import br.com.jordan.cadeopenha.activity.Splash;
import br.com.jordan.cadeopenha.interfaces.AsyncTaskListenerBuscarPenhas;
import br.com.jordan.cadeopenha.model.PenhaMaisProximo;
import br.com.jordan.cadeopenha.model.Penhas;
import br.com.jordan.cadeopenha.task.BuscarPenhasFromRadarTask;
import br.com.jordan.cadeopenha.util.GPSTracker;
import br.com.jordan.cadeopenha.util.PenhaUtil;

public class RadarPenhaReceiver extends BroadcastReceiver implements AsyncTaskListenerBuscarPenhas {
    PenhaUtil penhaUtil = new PenhaUtil();
    Penhas penhasLocalizados;
    LatLng latLngCurrentLocation;
    GPSTracker gps;
    Context context;
    Intent intent;
    Locale locale = new Locale("pt", "BR");

    boolean FLAG_NOTIFICATION_MAX = false;

    public RadarPenhaReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;

        gps = new GPSTracker(context);
        latLngCurrentLocation = new LatLng(gps.getLatitude(), gps.getLongitude());

        BuscarPenhasFromRadarTask task = new BuscarPenhasFromRadarTask(context, this);
        task.execute();
    }

    @Override
    public void onTaskCompleteAutenticarAPI(List<Penhas> result) {
        Penhas penhas = result.get(0);

        if (penhas.getListPenha().size() > 0) {
            penhasLocalizados = penhas;
            PenhaMaisProximo objPenhaProx = penhaUtil.getPenhaMaisProximo(penhas, latLngCurrentLocation);

            if(objPenhaProx.getDistancia() <= 1000){
                //Toast.makeText(context, "PENHA PERTO: " + objPenhaProx.getDistancia(), Toast.LENGTH_LONG).show();
                notify("OLHA O PENHHAAAAA", String.format(locale, "Tem um penha em um raio de %.2f metros de voce", objPenhaProx.getDistancia()));
            }else{ }
        } else {
            Toast.makeText(context, "Nenhum penha encontrado :(", Toast.LENGTH_LONG).show();
        }
    }

    public void notify(String titulo, String texto){
        Intent resultIntent = new Intent(context.getApplicationContext(), MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(Splash.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        // Open NotificationView.java Activity
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create Notification using NotificationCompat.Builder
        Notification.Builder builder = new Notification.Builder(
                context)
                // Set Icon
                .setSmallIcon(R.drawable.ic_penha)
                        // Set Ticker Message
                .setTicker("Penha")
                        // Set Title
                .setContentTitle(titulo)
                        // Set Text
                .setContentText(texto)
                        // Add an Action Button below Notification
                //.addAction(R.drawable.ic_launcher, "Action Button", pIntent)
                        // Set PendingIntent into Notification
                .setContentIntent(resultPendingIntent)
                        // Dismiss Notification
                .setAutoCancel(true);

        builder.setSmallIcon(android.R.drawable.stat_sys_warning);

        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notif = builder.build();
        notif.flags = Notification.FLAG_AUTO_CANCEL;
        notif.defaults = Notification.DEFAULT_ALL;

        // Build Notification with Notification Manager
        notificationmanager.notify(0, notif);
    }
}
