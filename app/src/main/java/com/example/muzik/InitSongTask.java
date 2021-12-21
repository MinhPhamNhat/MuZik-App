package com.example.muzik;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class InitSongTask extends AsyncTask<String, Integer, ArrayList<Song>> {
    ArrayList<Song> songs;
    Context ctx;
    boolean cancel;
    public InitSongTask(){
        this.songs = new ArrayList<Song>();
    }
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected ArrayList<Song> doInBackground(String... strings) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(strings[0]);
            JSONArray data = obj.getJSONArray("data");
            this.songs = new ArrayList<Song>();
            for (int i = 0 ;  i < data.length(); i ++){
                if (cancel){
                    return null;
                }
                JSONObject song = data.getJSONObject(i);
                this.songs.add(new Song(song));
                publishProgress((int) ((i / (float) data.length()) * 100));
            }
            boolean checkFinished = false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this.songs;
    }

    @Override
    protected void onPostExecute(ArrayList<Song> songs) {
        super.onPostExecute(songs);
    }

    public ArrayList<Song> getTask() throws ExecutionException, InterruptedException {
        ArrayList<Song> tempSongs = get();
        if(cancel){
            return null;
        }
        else {
            return tempSongs;
        }
    }
}
