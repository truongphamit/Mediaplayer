package com.pqs.mediaplayer.models;

/**
 * Created by truongpq on 4/18/17.
 */

public class Album {
    public String artistName;
    public long id;
    public int songCount;
    public String title;
    public int year;

    public Album() {}

    public Album(String artistName, long id, int songCount, String title, int year) {
        this.artistName = artistName;
        this.id = id;
        this.songCount = songCount;
        this.title = title;
        this.year = year;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getSongCount() {
        return songCount;
    }

    public void setSongCount(int songCount) {
        this.songCount = songCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
