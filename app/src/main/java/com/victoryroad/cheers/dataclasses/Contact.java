package com.victoryroad.cheers.dataclasses;

/**
 * Created by Squiggs on 11/29/2016.
 */

public class Contact {
    public String name;
    public String number;
    public static final String SEPARATOR = ": ";

    public Contact() {
        name = "";
        number = "";
    }

    public Contact(String n, String num) {
        name = n;
        number = num;
    }

    public boolean equals(Object o) {
        return o.toString().equalsIgnoreCase(this.toString());
    }

    public String toString() {
        return name + SEPARATOR  + number;
    }

    public String serialize() {
        return toString();
    }

    public static Contact deserialize(String contact) {
        if(contact == null)
            return null;

        String name;
        String num;

        int sepIndex = contact.indexOf(SEPARATOR);

        name = contact.substring(0, sepIndex);
        num = contact.substring(sepIndex + SEPARATOR.length());

        return new Contact(name, num);
    }
}
