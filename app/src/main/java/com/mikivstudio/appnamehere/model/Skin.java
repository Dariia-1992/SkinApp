package com.mikivstudio.appnamehere.model;

/**
 * Created by Dariia Mshanetskaya  on 01.06.2019.
 */
public class Skin {
    private String number;
    private String name;
    private int intName;
    private boolean isNumber;

    public Skin(int number) {
        this.number = String.valueOf(number);
        this.name = String.valueOf(number + 1);
        parseName();
    }

    public Skin(String number, String name) {
        this.number = number;
        this.name = name;
        parseName();
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public int getIntName() {
        return intName;
    }

    public boolean isNumber() {
        return isNumber;
    }

    private void parseName() {
        try {
            intName = Integer.parseInt(name);
            isNumber = true;
        } catch (NumberFormatException e) { }
    }
}
