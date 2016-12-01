package com.victoryroad.cheers.dataclasses;

import android.location.Location;

import com.google.android.gms.games.internal.constants.TimeSpan;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by krohlfing on 11/3/2016.
 */

public class CheckIn {
    public ArrayList<Comment> Comments;
    public String DrinkKey;
    public LatLng Location;
    public Date Time;
    public String Venue;
    public ArrayList<String> Categories;
    public String id;
    public String userName;

    public CheckIn(ArrayList<Comment> comments, String drinkKey, LatLng location, Date time, String venue) {
        Comments = comments;
        DrinkKey = drinkKey;
        Location = location;
        Time = time;
        Venue = venue;
    }

    public CheckIn(String drinkKey, LatLng location, Date time, String venue) {
        DrinkKey = drinkKey;
        Location = location;
        Time = time;
        Venue = venue;
    }

    public CheckIn(String drinkKey, LatLng location, Date time) {
        DrinkKey = drinkKey;
        Location = location;
        Time = time;
    }

    public CheckIn() {

    }
}
