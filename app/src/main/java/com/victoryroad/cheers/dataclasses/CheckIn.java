package com.victoryroad.cheers.dataclasses;

import android.location.Location;

import com.google.android.gms.games.internal.constants.TimeSpan;

import java.util.ArrayList;

/**
 * Created by krohlfing on 11/3/2016.
 */

public class CheckIn {
    private ArrayList<Comment> Comments;
    private String DrinkKey;
    private Location Location;
    private TimeSpan Time;
    private String Venue;

    public CheckIn(ArrayList<Comment> comments, String drinkKey, Location location, TimeSpan time, String venue) {
        Comments = comments;
        DrinkKey = drinkKey;
        Location = location;
        Time = time;
        Venue = venue;
    }

    public CheckIn(String drinkKey, Location location, TimeSpan time, String venue) {
        DrinkKey = drinkKey;
        Location = location;
        Time = time;
        Venue = venue;
    }

    public CheckIn(String drinkKey, Location location, TimeSpan time) {
        DrinkKey = drinkKey;
        Location = location;
        Time = time;
        Venue = "";
    }
}
