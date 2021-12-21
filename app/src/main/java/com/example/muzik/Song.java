package com.example.muzik;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Song implements Parcelable {
    String name;
    String id;
    ArrayList<String> artistName;
    ArrayList<String> artistId;
    String thumbnailLink;
    Bitmap thumbnail;
    public Song(String id, String name, ArrayList<String> artistName, ArrayList<String> artistId){
        this.id = id;
        this.name = name;
        this.artistName = artistName;
        this.artistId = artistId;
    }

    public Song(JSONObject data) throws JSONException {
        this.name = data.getString("TenBaiHat");
        this.id = data.getString("BaiHatID");
        this.thumbnailLink = data.getString("Thumbnail");
        this.artistName = new ArrayList<String>();
        this.artistId = new ArrayList<String>();
        JSONArray artists = new JSONArray(data.getString("CaSi")) ;
        for (int i = 0 ; i < artists.length(); i++){
            this.artistName.add(artists.getJSONObject(i).getString("TenCaSi"));
            this.artistId.add(artists.getJSONObject(i).getString("CaSiID"));
        }
    }

    protected Song(Parcel in) {
        name = in.readString();
        id = in.readString();
        artistName = in.createStringArrayList();
        artistId = in.createStringArrayList();
        thumbnailLink = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public  ArrayList<String> getArtist() {
        return artistName;
    }

    public  ArrayList<String> getArtistId() {
        return artistId;
    }

    public String getThumbnailLink(){ return this.thumbnailLink; }

    public String getToStringArtist(){
        if (this.artistName.size() >3){
            return "Nhiều ca sĩ";
        }
        String artistsname = "";
        for (String i : this.artistName){
            artistsname += i+", ";
        }
        return artistsname.substring(0,artistsname.length()-2);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
        dest.writeString(thumbnailLink);
        dest.writeSerializable(artistName);
        dest.writeSerializable(artistId);
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }
}
