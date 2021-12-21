package com.example.muzik;

public class Playlist {
    int noSong;
    String name;
    String id;
    String songs;
    String jsonObject;
    public Playlist(int noSong, String name, String id, String songs, String jsonObject) {
        this.name = name;
        this.id = id;
        this.noSong = noSong;
        this.songs = songs;
        this.jsonObject = jsonObject;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getNoSong() {
        return noSong;
    }

    public String getSongs() {
        return songs;
    }

    public String getJsonObject() {
        return jsonObject;
    }
}
