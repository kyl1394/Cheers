package com.victoryroad.cheers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.rides.RequestDeeplink;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.SessionConfiguration;
import com.victoryroad.cheers.dataclasses.Settings;

import java.util.Arrays;

public class RequestUberHomeActivity extends AppCompatActivity {

    public static final String NOTIFICATION_ID = "NOTIFICATION_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionConfiguration config = new SessionConfiguration.Builder()
                // mandatory
                .setClientId("jE0s0Ilok1y5WNj6TWA8FDOeoskoLlIe")
                // required for enhanced button features
                .setServerToken("PRlGcle3iECIb4HFnf8KpTtA0RmdPk77F3C52qnr")
                // required for implicit grant authentication
                .setRedirectUri("<REDIRECT_URI>")
                // required scope for Ride Request Widget features
                .setScopes(Arrays.asList(Scope.RIDE_WIDGETS))
                // optional: set Sandbox as operating environment
                .setEnvironment(SessionConfiguration.Environment.SANDBOX)
                .build();

        UberSdk.initialize(config);

        LatLng home = Settings.getSettingsFor(getApplicationContext()).getHomeLocation();

        RideParameters params = new RideParameters.Builder()
                .setPickupToMyLocation()
                .setDropoffLocation(home.latitude, home.longitude, "", "").build();
        RequestDeeplink deeplink = new RequestDeeplink.Builder(getApplicationContext())
                .setSessionConfiguration(config)
                .setRideParameters(params).build();

        deeplink.execute();
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(getIntent().getIntExtra(NOTIFICATION_ID, -1));
        finish(); // since finish() is called in onCreate(), onDestroy() will be called immediately
    }

    public static PendingIntent getDismissIntent(int notificationId, Context context) {
        Intent intent = new Intent(context, RequestUberHomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(NOTIFICATION_ID, notificationId);
        PendingIntent dismissIntent = PendingIntent.getActivity(context, 100, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return dismissIntent;
    }
}
