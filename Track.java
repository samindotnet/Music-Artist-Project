package com.example.musicartistproject;

public class Track {
    private String trackid;
    private String trackname;
    private int trackRating;
    public Track(){

    }

    public Track(String trackid, String trackname, int trackRating) {
        this.trackid = trackid;
        this.trackname = trackname;
        this.trackRating = trackRating;
    }

    public String getTrackid() {
        return trackid;
    }

    public String getTrackname() {
        return trackname;
    }

    public int getTrackRating() {
        return trackRating;
    }
}
