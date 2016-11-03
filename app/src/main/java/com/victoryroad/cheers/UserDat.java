package com.victoryroad.cheers;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by bptrent on 11/2/2016.
 */

public class UserDat {
    public String Name;
    public HashMap<String,String> Checkins;
    private static String UserID;

    public UserDat(String userId, String userName) {
        Name = userName;
        UserID = userId;
        Checkins = new HashMap<>();
    }

    public static String getUserID(){
        return UserID;
    }

}
