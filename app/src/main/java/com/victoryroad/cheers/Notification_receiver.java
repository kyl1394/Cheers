package com.victoryroad.cheers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;

import com.victoryroad.cheers.dataclasses.Settings;

/**
 * Created by krohlfing on 12/5/2016.
 */
public class Notification_receiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent repeating_intent = new Intent(context, RequestUberHomeActivity.class);
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Intent repeating_intent2 = new Intent(context, MainActivity.class);
        repeating_intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = RequestUberHomeActivity.getDismissIntent(100, context);

        Intent callDD = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", Settings.getSettingsFor(context).getContact().number, null));
        PendingIntent callDDIntent = PendingIntent.getActivity(context, 101, callDD, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.beer)
                .setContentTitle("Heading Home?")
                .setContentText("Let's make sure you get there safely.")
                .setVibrate(new long[] {1000, 1000})
                .setAutoCancel(true)
                .setOnlyAlertOnce(true);

        builder.mActions.add(new NotificationCompat.Action(R.drawable.addbeer, "Request Uber", pendingIntent));
        builder.mActions.add(new NotificationCompat.Action(R.drawable.addbeer, "Call Designated Driver", callDDIntent));

        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(100, notification);
    }
}
