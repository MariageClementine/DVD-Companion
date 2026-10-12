package model;

public enum ActiveTime {

    DAY("Day"),
    NIGHT("Night"),
    BOTH("Both");

    private final String strForDisplay;

    ActiveTime(String strDisplay){
        this.strForDisplay = strDisplay;
    }

    String getStrForDisplay(){
        return this.strForDisplay;
    }
}
