package com.example.mobilehw2_nivshalom.data;

public class LeaderboardsItem {

    private int score;
    private double lat;
    private double lon;

    public LeaderboardsItem(int score, double lat, double lon) {
        this.score = score;
        this.lat = lat;
        this.lon = lon;
    }

    public int GetScore() { return this.score; }

    public double GetLat() { return this.lat; }

    public double GetLon() { return this.lon; }

    @Override
    public String toString() {
        return "LeaderboardsItem{" +
                "score=" + score +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';
    }

}
