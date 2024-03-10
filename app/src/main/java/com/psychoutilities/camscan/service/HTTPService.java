package com.psychoutilities.camscan.service;


import static com.psychoutilities.camscan.utils.AppSettings.getPortNumber;
import static com.psychoutilities.camscan.utils.Utility.getLocalIpAddress;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.psychoutilities.camscan.R;
import com.psychoutilities.camscan.activity.ActMain;
import com.psychoutilities.camscan.utils.Utility;
import com.psychoutilities.camscan.webserver.WebServer;

public class HTTPService extends Service {
    private static final int NOTIFICATION_STARTED_ID = 52342;
    private NotificationManager mNotifyMgr = null;
    private WebServer server = null;
private int lastShownNotificationId;
    @Override
    public void onCreate() {
        super.onCreate();
        mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        server = new WebServer(this, mNotifyMgr);
    }

    @Override
    public void onDestroy() {
        if (server != null) {
            server.stopThread();
            server = null;
        }
        mNotifyMgr.cancel(NOTIFICATION_STARTED_ID);
        mNotifyMgr = null;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        server.startThread();
        createAndShowForegroundNotification(this,NOTIFICATION_STARTED_ID);
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static PendingIntent contentIntent(Context context){
        Intent startActivityIntent = new Intent(context, ActMain.class);
        startActivityIntent.setAction("do_something");
        return PendingIntent.getActivity(context,NOTIFICATION_STARTED_ID,startActivityIntent,PendingIntent.FLAG_UPDATE_CURRENT);
    };

    private void createAndShowForegroundNotification(Service yourService, int notificationId) {

        final NotificationCompat.Builder builder = getNotificationBuilder(yourService,
                "com.psychoutilities.camscan.56463", // Channel id
                NotificationManagerCompat.IMPORTANCE_LOW); //Low importance prevent visual appearance for this notification channel on top
                builder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(yourService.getString(R.string.app_name))
                .setContentText("Documents shared at \n http://"+getLocalIpAddress(1) + ":" + getPortNumber(this))
                .setContentIntent(contentIntent(yourService))
                ;

        Notification notification = builder.build();
       // While making notification
       // Intent i = new Intent("do_something");
       // notification.contentIntent = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        yourService.startForeground(notificationId, notification);

        if (notificationId != lastShownNotificationId) {
            // Cancel previous notification
            final NotificationManager nm = (NotificationManager) yourService.getSystemService(Activity.NOTIFICATION_SERVICE);
            nm.cancel(lastShownNotificationId);
        }
        lastShownNotificationId = notificationId;
    }

    public static NotificationCompat.Builder getNotificationBuilder(Context context, String channelId, int importance) {
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            prepareChannel(context, channelId, importance);
            builder = new NotificationCompat.Builder(context, channelId);
        } else {
            builder = new NotificationCompat.Builder(context);
        }
        return builder;
    }

    @TargetApi(26)
    private static void prepareChannel(Context context, String id, int importance) {
        final String appName = context.getString(R.string.app_name);
        String description = context.getString(R.string.noti_content);
        final NotificationManager nm = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);

        if(nm != null) {
            NotificationChannel nChannel = nm.getNotificationChannel(id);

            if (nChannel == null) {
                nChannel = new NotificationChannel(id, appName, importance);
                nChannel.setDescription(description);
                nm.createNotificationChannel(nChannel);
            }
        }
    }
}
