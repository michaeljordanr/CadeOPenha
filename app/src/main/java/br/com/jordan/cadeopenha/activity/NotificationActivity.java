package br.com.jordan.cadeopenha.activity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import br.com.jordan.cadeopenha.R;

/**
 * Created by techresult on 10/11/2016.
 */

public class NotificationActivity extends Activity {

    private Context context;
    private String texto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getBaseContext();

        texto = getIntent().getStringExtra("text");
        notify("Test", texto);

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
