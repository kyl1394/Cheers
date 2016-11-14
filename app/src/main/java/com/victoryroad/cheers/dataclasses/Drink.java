package com.victoryroad.cheers.dataclasses;

/**
 * Created by krohlfing on 11/3/2016.
 */

public class Drink implements Comparable<String> {
    private String Name;
    private String key;

    public String getName() {
        return Name;
    }

    public String getKey() {
        return key;
    }

    public Drink(String name, String drinkKey) {
        Name = name;
        key = drinkKey;
    }

    public Drink() {
        Name = "";
        key = "";
    }

    @Override
    public int hashCode() {
        return Name.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof String && o.equals(Name);
    }

    @Override
    public int compareTo(String o) {
        return o.compareTo(Name);
    }
}
