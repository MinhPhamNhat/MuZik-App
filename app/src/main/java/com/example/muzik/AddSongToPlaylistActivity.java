package com.example.muzik;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class AddSongToPlaylistActivity extends AppCompatActivity {
    ArrayList<Playlist> playlists;
    SharedPreferences sharedPreferences;
    ListView listPlaylist;
    TextView message;
    String userId;
    LinearLayout back, addPlaylist;
    ProgressBar progressBar;
    String songId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_to_add_layout);

        Intent intent = getIntent();
        songId = intent.getStringExtra("songid");

        setUpVariable();
        setButton();

        if (sharedPreferences.contains("userid")) {
            userId = sharedPreferences.getString("userid", null);
            setList();
        }else{
            addPlaylist.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            listPlaylist.setVisibility(View.GONE);
            message.setVisibility(View.VISIBLE);
        }
    }


    public void setButton() {
        back.setOnClickListener(v -> {
            finish();
        });

        addPlaylist.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert);

            final View customLayout = getLayoutInflater().inflate(R.layout.add_playlist_modal, null);
            builder.setView(customLayout);
            builder.setTitle("Nh???p t??n Playlist");

            builder.setPositiveButton("L??u", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditText playListName = (EditText) customLayout.findViewById(R.id.input_text);
                    new Thread(() -> {
                        try {
                            HttpRequest.addPlaylist(userId, playListName.getText().toString());
                            runOnUiThread(() -> {
                                progressBar.setVisibility(View.VISIBLE);
                                listPlaylist.setVisibility(View.GONE);
                                setList();
                                Toast.makeText(AddSongToPlaylistActivity.this, "Th??m Playlist th??nh c??ng", Toast.LENGTH_LONG).show();
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            });

            builder.setNegativeButton("Hu???", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
        });

    }

    public void setUpVariable() {
        message = findViewById(R.id.mess);
        listPlaylist = findViewById(R.id.list_my_playlist);
        sharedPreferences = getSharedPreferences("SESSION", MODE_PRIVATE);
        back = findViewById(R.id.backPlaylistBtn);
        progressBar = findViewById(R.id.progress_bar);
        addPlaylist = findViewById(R.id.add_playlist);
    }

    public void setPlaylist(String data) throws JSONException {
        playlists = new ArrayList<>();
        JSONObject dataObj = new JSONObject(data);
        JSONArray playlistArr = new JSONArray(dataObj.getString("data"));
        for (int i = 0; i < playlistArr.length(); i++) {
            JSONObject obj = new JSONObject(playlistArr.get(i).toString());
            JSONArray songs = new JSONArray(obj.getString("items"));
            playlists.add(new Playlist(songs.length(), obj.get("PlaylistName").toString(), obj.get("PlaylistID").toString(), songs.toString(), obj.toString()));
        }
    }

    public void setList() {
        new Thread(() -> {
            String data = "";
            try {
                data = HttpRequest.getListUserPlaylistById(this.userId);
                Log.e("TAG", data);
                setPlaylist(data);
                runOnUiThread(() -> {
                    AddSongToPlaylistActivity.MyListViewAdapter adapter = new MyListViewAdapter(this, R.layout.playlist_row, playlists);
                    listPlaylist.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
                    listPlaylist.setVisibility(View.VISIBLE);
                });
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    class MyListViewAdapter extends ArrayAdapter {
        Context ctx;
        int res;
        ArrayList<Playlist> playlists;

        public MyListViewAdapter(@NonNull Context ctx, int res, @NonNull ArrayList<Playlist> playlists) {
            super(ctx, res, playlists);
            this.res = res;
            this.ctx = ctx;
            this.playlists = playlists;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            convertView = LayoutInflater.from(this.ctx).inflate(this.res, null);
            Playlist playlist = playlists.get(position);

            TextView playlistName = convertView.findViewById(R.id.my_playlist_name);
            TextView playlistSubText = convertView.findViewById(R.id.no_my_playlist_song);
            ImageButton deletePlaylist = convertView.findViewById(R.id.delete_playlist);
            ImageButton editPlaylist = convertView.findViewById(R.id.edit_playlist);

            deletePlaylist.setVisibility(View.GONE);
            editPlaylist.setVisibility(View.GONE);

            playlistName.setText(playlist.getName());
            playlistSubText.setText("S??? b??i h??t : " + playlist.getNoSong());

            convertView.setOnClickListener(v -> {
                progressBar.setVisibility(View.VISIBLE);
                listPlaylist.setVisibility(View.GONE);
                Thread t = new Thread(()->{
                    try {
                        HttpRequest.addSongToPlaylist(songId, playlist.id);
                        progressBar.setVisibility(View.GONE);
                        runOnUiThread(()->{
                            Toast.makeText(ctx, "Th??m b??i hat th??nh c??ng", Toast.LENGTH_LONG).show();
                            finish();
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                t.start();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });


            return convertView;
        }

    }
}