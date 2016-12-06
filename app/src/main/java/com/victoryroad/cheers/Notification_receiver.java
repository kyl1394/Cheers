package com.victoryroad.cheers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;

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
        PendingIntent callDDIntent = PendingIntent.getActivity(context, 101, repeating_intent2, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.beer)
                .setContentTitle("Heading Home?")
                .setContentText("Let's make sure you get there safely.")
                .setVibrate(new long[] {1000, 1000})
                .setAutoCancel(true);

        builder.mActions.add(new NotificationCompat.Action(R.drawable.addbeer, "Request Uber", pendingIntent));
        builder.mActions.add(new NotificationCompat.Action(R.drawable.addbeer, "Call Designated Driver", callDDIntent));
        notificationManager.notify(100, builder.build());
    }
}
