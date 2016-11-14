package com.victoryroad.cheers.dataclasses;

/**
 * Created by krohlfing on 11/3/2016.
 */

public class Comment {
    private String Comment;
    private String UserID;

    public Comment(String message, String uid) {
        Comment = message;
        UserID = uid;
    }
}
