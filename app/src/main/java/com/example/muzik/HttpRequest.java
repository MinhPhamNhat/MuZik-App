package com.example.muzik;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import jp.wasabeef.blurry.Blurry;

public class HttpRequest {
    private final static String HOST= "https://mymuzik123.000webhostapp.com/";
    //    private static String host= "http://10.0.2.2:80/Muzik/";



    public static String getPlaylistByID(String ID, int num) throws  IOException{
        URL url = new URL(HOST+"getPlaylist.php?id="+ID+"&num="+num);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");
        InputStream is = con.getInputStream();
        String output = readInputStreamAsText(is);
        return output;
    }

    public static String getPublicPlaylist() throws IOException {
        URL url = new URL(HOST+"getPublicPlaylist.php");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");
        InputStream is = con.getInputStream();
        String output = readInputStreamAsText(is);
        return output;
    }


    public static Bitmap getBitmap(String link) throws IOException {
        URL url = new URL(link);
        return  BitmapFactory.decodeStream(url.openConnection().getInputStream());
    }

    public static Bitmap getAva(String userId) throws IOException {
        URL url = new URL(HOST+"user_ava/"+userId+".png");
        return  BitmapFactory.decodeStream(url.openConnection().getInputStream());
    }

    public static String getByFilter(String text) throws IOException {
        URL url = new URL(HOST+"getFilter.php?filter="+text);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");
        InputStream is = con.getInputStream();
        String output = readInputStreamAsText(is);
        return output;
    }

    public static String getListUserPlaylistById(String id) throws IOException {
        URL url = new URL(HOST+"getUserPlaylist.php?id="+id);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");
        InputStream is = con.getInputStream();
        String output = readInputStreamAsText(is);
        return output;
    }

    public static  String addPlaylist(String UserID, String PlaylistName) throws IOException {
        URL url = new URL(HOST+"addPlaylist.php?userid="+UserID+"&playlistname="+PlaylistName);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");
        InputStream is = con.getInputStream();
        String output = readInputStreamAsText(is);
        return output;
    }

    public static String editPlaylist(String PlaylistID, String PlaylistName) throws IOException {
        URL url = new URL(HOST+"editPlaylistName.php?playlistid="+PlaylistID+"&playlistname="+PlaylistName);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");
        InputStream is = con.getInputStream();
        String output = readInputStreamAsText(is);
        return output;
    }

    public static String deletePlaylist(String PlaylistID) throws IOException {
        URL url = new URL(HOST+"deletePlaylist.php?playlistid="+PlaylistID);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");
        InputStream is = con.getInputStream();
        String output = readInputStreamAsText(is);
        return output;
    }

    public static  String addSongToPlaylist(String songID, String playlistID) throws IOException {
        URL url = new URL(HOST+"addToPlaylist.php?playlistid="+playlistID+"&songid="+songID);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");
        InputStream is = con.getInputStream();
        String output = readInputStreamAsText(is);
        return output;
    }

    public static String deleteSongFromPlaylist(String songID, String playlistID) throws IOException{

        URL url = new URL(HOST+"deleteSongFromPlaylist.php?playlistid="+playlistID+"&songid="+songID);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");
        InputStream is = con.getInputStream();
        String output = readInputStreamAsText(is);
        return output;
    }

    public static String register(String username, String password, String email, String name) throws IOException {
        URL url = new URL(HOST+"register.php");
        String urlParameters  = "username="+username+"&password="+password+"&email="+email+"&name="+name+"";
        byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setReadTimeout(15000);
        con.setConnectTimeout(10000);
        con.setDoOutput( true );
        con.setRequestMethod( "POST" );
        con.setUseCaches( false );
        try( DataOutputStream wr = new DataOutputStream( con.getOutputStream())) {
            wr.write( postData );
            wr.flush();
        }

        InputStream is = con.getInputStream();
        String output = readInputStreamAsText(is);
        return output;
    }

    public static String login(String username, String password) throws IOException {
        URL url = new URL(HOST+"login.php");
        String urlParameters  = "username="+username+"&password="+password;
        byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
        HttpURLConnection con= (HttpURLConnection) url.openConnection();
        con.setReadTimeout(15000);
        con.setConnectTimeout(10000);
        con.setDoOutput( true );
        con.setRequestMethod( "POST" );
        con.setUseCaches( false );
        try( DataOutputStream wr = new DataOutputStream( con.getOutputStream())) {
            wr.write( postData );
            wr.flush();
        }

        InputStream is = con.getInputStream();
        String output = readInputStreamAsText(is);
        return output;
    }

    private static String readInputStreamAsText(InputStream is) throws IOException {

        char[] buffer = new char[1024];
        StringBuilder builder = new StringBuilder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        int size;
        while ((size = reader.read(buffer)) > 0) {
            builder.append(buffer, 0, size);
        }

        reader.close();
        is.close();

        return builder.toString();
    }

}