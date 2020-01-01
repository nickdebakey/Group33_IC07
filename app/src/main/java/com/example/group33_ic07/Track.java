package com.example.group33_ic07;

public class Track {
    String name;
    String album;
    String artist;
    String updated;
    String url;

    public Track(String name, String album, String artist, String updated, String url) {
        this.name = name;
        this.album = album;
        this.artist = artist;
        this.updated = updated;
        this.url = url;
    }

    @Override
    public String toString() {
        return "Track{" +
                "name='" + name + '\'' +
                ", album='" + album + '\'' +
                ", artist='" + artist + '\'' +
                ", updated='" + updated + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
