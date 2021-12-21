package com.example.muzik;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ActionMenuView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {
    Context ctx;
    int res;
    ArrayList<Song> songs;
    Bitmap defaultbmp;
    ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
    int mode;
    String playlistId;

    public MyAdapter(Context ctx, int res, ArrayList<Song> songs, int mode){
        this.ctx = ctx;
        this.res = res;
        this.mode = mode;
        this.songs = songs;
        defaultbmp = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.audio_default);
        bitmapArrayList = new ArrayList<>();
        if (bitmapArrayList.size() == 0) {
            for (int i = 0; i < this.songs.size(); i++) {
                bitmapArrayList.add(defaultbmp);
            }
        }
    }


    public MyAdapter(Context ctx, int res, ArrayList<Song> songs, int mode, String playlistId){
        this.ctx = ctx;
        this.res = res;
        this.mode = mode;
        this.playlistId = playlistId;
        this.songs = songs;
        defaultbmp = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.audio_default);
        bitmapArrayList = new ArrayList<>();
        if (bitmapArrayList.size() == 0) {
            for (int i = 0; i < this.songs.size(); i++) {
                bitmapArrayList.add(defaultbmp);
            }
        }
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.ctx).inflate(res, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.pos = position;
        Song song = songs.get(position);
        holder.name.setText(song.getName());
        holder.thumbnail.setImageResource(R.drawable.audio_default);
        holder.artist.setText(song.getToStringArtist());
        holder.view.setOnClickListener(v->{
            Intent intent = new Intent(this.ctx, AudioPlayActivity.class);
            CurrentPlayList currentPlayList = CurrentPlayList.getInstance();
            currentPlayList.setIsNewSong(true);
            currentPlayList.setIsNeedStarted(true);
            currentPlayList.addSong(song);
            this.ctx.startActivity(intent);
        });

        if (bitmapArrayList.get(position).equals(defaultbmp)) {
            new Thread(() -> {
                URL url = null;
                try {
                    Bitmap bitmap;
                    url = new URL(song.getThumbnailLink());
                    bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    ((Activity)this.ctx).runOnUiThread(() -> {
                        if (bitmapArrayList.size() > position) {
                            bitmapArrayList.set(position, bitmap);
                            if (holder.pos == position)
                                holder.thumbnail.setImageBitmap(bitmapArrayList.get(position));
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            holder.thumbnail.setImageBitmap(bitmapArrayList.get(position));
        }



        holder.menuBtn.setOnClickListener(v -> {
            LayoutInflater layoutInflater = (LayoutInflater) this.ctx.getSystemService(LAYOUT_INFLATER_SERVICE);
            final View popupView = layoutInflater.inflate(R.layout.item_menu, null);
            final PopupWindow popupWindow = new PopupWindow(
                    popupView,
                    ActionMenuView.LayoutParams.WRAP_CONTENT,
                    ActionMenuView.LayoutParams.WRAP_CONTENT, true);

            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);


            popupWindow.setBackgroundDrawable(new BitmapDrawable());


            popupWindow.showAsDropDown(v,20, -175);

            ImageButton addToPlaylist = popupView.findViewById(R.id.add_to_playlist);
            ImageButton deleteFromPlaylist = popupView.findViewById(R.id.delete_song);

            if (mode == 1){
                popupView.findViewById(R.id.delete_song).setVisibility(View.GONE);
                addToPlaylist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ctx, AddSongToPlaylistActivity.class);
                        intent.putExtra("songid", song.id);
                        ctx.startActivity(intent);
                    }
                });
            }else if (mode == 2){
                popupView.findViewById(R.id.add_to_playlist).setVisibility(View.GONE);
                deleteFromPlaylist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
                        alert.setTitle("Bạn có muốn xoá bài này khỏi Playlist không ?");

                        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                new Thread(()->{
                                    try {
                                        String data = HttpRequest.deleteSongFromPlaylist(song.id, playlistId);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }).start();

                                songs.remove(position);
                                notifyItemRemoved(position);
                            }
                        });
                        alert.setNegativeButton("Hủy",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                    }
                                });
                        alert.show();
                    }
                });
            }else if (mode == 3){
                popupView.findViewById(R.id.add_to_playlist).setVisibility(View.GONE);
                popupView.findViewById(R.id.delete_song).setVisibility(View.GONE);
            }
        });



    }

    @Override
    public int getItemCount() {
        return this.songs.size();
    }

    public void setData(ArrayList<Song> songs){
        this.songs = new ArrayList<Song>(songs);
        bitmapArrayList = new ArrayList<>();
        if (bitmapArrayList.size() == 0) {
            for (int i = 0; i < this.songs.size(); i++) {
                bitmapArrayList.add(defaultbmp);
            }
        }
        notifyDataSetChanged();
    }


    public class MyHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView artist;
        View view;
        int pos ;
        ImageView thumbnail;
        ImageButton menuBtn;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            this.name = itemView.findViewById(R.id.name);
            this.artist = itemView.findViewById(R.id.artist);
            this.thumbnail = itemView.findViewById(R.id.thumbnail);
            this.menuBtn = itemView.findViewById((R.id.menu_btn));
        }

    }
}
