package com.example.muzik;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;


public class PublicPlaylistAdapter extends RecyclerView.Adapter<PublicPlaylistAdapter.MyHolder> {
    Context context;
    ArrayList<JSONObject> playlists;
    int resource;

    public PublicPlaylistAdapter(@NonNull Context context, int resource, @NonNull ArrayList<JSONObject> playlists){
        this.context = context;
        this.resource = resource;
        this.playlists = playlists;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(this.resource, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        JSONObject playlist = playlists.get(position);

        try {
            holder.playlistName.setText(playlist.getString("PlaylistName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AtomicReference<Bitmap> bitmap = new AtomicReference<>();
        new Thread(() -> {
            try {
                bitmap.set(HttpRequest.getBitmap(playlist.getString("Thumbnail")));
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            ((Activity) this.context).runOnUiThread(() -> {
                holder.playlistImage.setImageBitmap(bitmap.get());
            });
        }).start();

        holder.playlistImage.setOnClickListener(v -> {
            Intent intent = new Intent((Activity)this.context, PlaylistActivity.class);
            intent.putExtra("playlist", playlist.toString());
            intent.putExtra("mode", "1");
            Objects.requireNonNull((Activity)this.context).startActivity(intent);
        });

        AtomicReference<String> data = new AtomicReference<>("");
        holder.playlistBtn.setOnClickListener(v-> {
            new Thread(()->{
                try {
                    data.set(HttpRequest.getPlaylistByID(playlist.getString("PlaylistID"), 999));
                    ArrayList<Song> songs = new ArrayList<>();
                    InitSongTask initSongTask = (InitSongTask) new InitSongTask().execute(data.get());
                    try {
                        songs = initSongTask.getTask();
                        CurrentPlayList currentPlayList = CurrentPlayList.getInstance();
                        currentPlayList.setIsNeedStarted(true);
                        currentPlayList.replacePlaylist(songs);
                        currentPlayList.setIsNewSonglist(true);
                        Intent intent = new Intent(this.context, AudioPlayActivity.class);
                        intent.putExtra("data", playlist.toString());
                        ((Activity) this.context).startActivity(intent);
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }


    public class MyHolder extends RecyclerView.ViewHolder{
        ImageView playlistImage;
        TextView playlistName;
        ImageButton playlistBtn;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            this.playlistName = itemView.findViewById(R.id.playlist_name);
            this.playlistImage = itemView.findViewById(R.id.playlist_image);
            this.playlistBtn = itemView.findViewById(R.id.playlist_playBtn);
        }
    }
}