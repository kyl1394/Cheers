package com.victoryroad.cheers.dataclasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.victoryroad.cheers.R;

/**
 * Created by Squiggs on 12/2/2016.
 */

public class CustomGMapInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private View cardLayout;
    private Context viewContext = null;

    public CustomGMapInfoWindowAdapter(Context context, CheckIn cardData) {
        viewContext = context;

    }

    @Override
    public View getInfoWindow(Marker marker) {

        View layout = LayoutInflater.from(viewContext).inflate(R.layout.fragment_drinkcard, null);
        return layout;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
