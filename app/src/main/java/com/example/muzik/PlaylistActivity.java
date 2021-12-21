package com.example.muzik;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

public class PlaylistActivity extends AppCompatActivity {
    TextView PlaylistName;
    JSONObject playlist;
    RecyclerView playlistSongList;
    ArrayList<Song> songs;
    LinearLayout back;
    int mode;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.public_playlist);


        songs = new ArrayList<>();
        back = findViewById(R.id.backBtn);
        PlaylistName = findViewById(R.id.playlist_name);
        Intent intent = getIntent();
        mode = Integer.parseInt(intent.getStringExtra("mode"));
        try {
            playlist = new JSONObject(intent.getStringExtra("playlist"));
            PlaylistName.setText(playlist.getString("PlaylistName"));
            getPlaylistSong();
        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        back.setOnClickListener(v->{
            if (mode == 1)
            finish();
            else{
                startActivity(new Intent(this, MyPlaylistActivity.class));
                finish();
            }
        });
    }

    public void getPlaylistSong() throws JSONException, InterruptedException, ExecutionException {
        AtomicReference<String> data = new AtomicReference<>("");
        Thread t = new Thread(()->{
            try {
                data.set(HttpRequest.getPlaylistByID(playlist.getString("PlaylistID"), 999));
                InitSongTask initSongTask = (InitSongTask) new InitSongTask().execute(data.get());
                songs = initSongTask.getTask();
                runOnUiThread(()->{
                    playlistSongList = findViewById(R.id.playlist_song_list);
                    try {
                        playlistSongList.setAdapter(new MyAdapter(this, R.layout.list_row, songs,mode , playlist.getString("PlaylistID")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    playlistSongList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                });
            } catch (IOException | JSONException | InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        t.start();
    }




}
