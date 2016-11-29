package com.victoryroad.cheers.dataclasses;

import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by bptrent on 11/2/2016.
 */

public class UserDat {
    public String Name;
    public HashMap<String,String> Checkins;
//    public Uri ProfilePicUri;
    private String UserID;

    public UserDat(String userId, String userName) {
        Name = userName;
        UserID = userId;
        Checkins = new HashMap<>();
    }

    public UserDat(String userId, String userName, Uri picUri) {
        Name = userName;
        UserID = userId;
        Checkins = new HashMap<>();
//        ProfilePicUri = picUri;
    }

    public String getUserID(){
        return UserID;
    }

}
