package com.victoryroad.cheers.dataclasses;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by bptrent on 11/2/2016.
 */

public class UserDat {
    public String Name;
    public HashMap<String,String> Checkins;
    private String UserID;

    public UserDat(String userId, String userName) {
        Name = userName;
        UserID = userId;
        Checkins = new HashMap<>();
    }

    public String getUserID(){
        return UserID;
    }

}
