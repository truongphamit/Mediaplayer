package com.pqs.mediaplayer.models;

/**
 * Created by truongpq on 4/18/17.
 */

public class Artist {
    public int albumCount;
    public long id;
    public String name;
    public int songCount;

    public Artist() {}

    public Artist(int albumCount, long id, String name, int songCount) {
        this.albumCount = albumCount;
        this.id = id;
        this.name = name;
        this.songCount = songCount;
    }

    public int getAlbumCount() {
        return albumCount;
    }

    public void setAlbumCount(int albumCount) {
        this.albumCount = albumCount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSongCount() {
        return songCount;
    }

    public void setSongCount(int songCount) {
        this.songCount = songCount;
    }
}
