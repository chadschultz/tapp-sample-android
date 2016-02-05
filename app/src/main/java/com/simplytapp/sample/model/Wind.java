package com.simplytapp.sample.model;

/**
 * Represents "wind" portion of JSON response
 *
 * Created by Chad Schultz on 2/4/2016.
 */
public class Wind {
    private double speed;
    private double deg;

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getDeg() {
        return deg;
    }

    public void setDeg(double deg) {
        this.deg = deg;
    }
}
