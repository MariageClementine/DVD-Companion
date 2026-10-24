package io.github.mariageclementine.dvdco.model;

public enum ActiveTime {

    DAY("Day"),
    NIGHT("Night"),
    BOTH("Both");

    private final String strForDisplay;

    ActiveTime(String strDisplay){
        this.strForDisplay = strDisplay;
    }

    public String getStrForDisplay(){
        return this.strForDisplay;
    }
}
