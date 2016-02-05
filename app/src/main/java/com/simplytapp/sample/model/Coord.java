package com.simplytapp.sample.model;

/**
 * Represents "coord" portion of JSON response
 *
 * Created by Chad Schultz on 2/4/2016.
 */
public class Coord {
    private double lon;
    private double lat;

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
}
