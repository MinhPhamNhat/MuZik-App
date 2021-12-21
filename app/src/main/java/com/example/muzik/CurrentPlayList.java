package com.example.muzik;

import java.util.ArrayList;

public class CurrentPlayList {
    private static CurrentPlayList instance;
    private static ArrayList<Song> songlist;
    private static boolean newSonglist = false;
    private static boolean newSong = false;
    private static boolean needStarted = false;

    public static CurrentPlayList getInstance() {
        if (instance == null) {
            instance = new CurrentPlayList();
            songlist = new ArrayList<>();
        }
        return instance;
    }

    public boolean isNewSonglist() {
        return newSonglist;
    }

    public void setIsNewSonglist(boolean bool) {
        newSonglist = bool;
    }

    public boolean isNewSong() {
        return newSong;
    }

    public void setIsNewSong(boolean bool) {
        newSong = bool;
    }

    public ArrayList<Song> getSongList() {
        return songlist;
    }

    public void addSong(Song newSong) {
        for (Song song : songlist) {
            if (song.getId().equals(newSong.getId())) {
                removeSong(song);
                break;
            }
        }
        songlist.add(newSong);
    }

    public boolean isNeedStarted() {
        return needStarted;
    }

    public void  setIsNeedStarted(boolean bool) {
        needStarted = bool;
    }

    public boolean removeSong(Song song) {
        int index = getCurrentSongIndex(song);
        if (index > -1) {
            songlist.remove(index);
        }
        return false;
    }

    public Song getSongAtIndex(int index) {
        if (index >= 0 && index < songlist.size()) {
            return songlist.get(index);
        }
        else return null;
    }

    public Song getFirstSong() {
        if (songlist.size() > 0) {
            return songlist.get(0);
        }
        else return null;
    }

    public Song getLastSong() {
        if (songlist.size() > 0) {
            return songlist.get(songlist.size() - 1);
        }
        else return null;
    }

    public boolean isNextSongExist(Song song) {
        if ((getCurrentSongIndex(song) + 1 ) < songlist.size()) {
            return true;
        }
        else return false;
    }

    public boolean isPreviousSongExist(Song song) {
        if ((getCurrentSongIndex(song) - 1 ) >= 0) {
            return true;
        }
        else return false;
    }

    public Song getNextSong(Song song) {
        if (isNextSongExist(song)) {
            return songlist.get(getCurrentSongIndex(song) + 1);
        }
        else return getFirstSong();
    }

    public Song getPreviousSong(Song song) {
        if (isPreviousSongExist(song)) {
            return songlist.get(getCurrentSongIndex(song) - 1);
        }
        else return getLastSong();
    }

    public int getCurrentSongIndex(Song song) {
        for (int i = 0; i < songlist.size(); i++) {
            if (song.equals(songlist.get(i))) return i;
        }
        return -1;
    }

    public void replacePlaylist(ArrayList<Song> songs) {
        songlist = songs;
    }

    public int size() {
        return songlist.size();
    }
}
