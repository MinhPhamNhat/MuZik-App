package com.example.muzik;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PageFragmentOnline#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PageFragmentOnline extends Fragment {

    RecyclerView gridPlaylist;
    View onlineFragmentView;
    ArrayList<JSONObject> playlist;
    ProgressBar progressBar;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PageFragmentOnline() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Online.
     */
    // TODO: Rename and change types and number of parameters
    public static PageFragmentOnline newInstance(String param1, String param2) {
        PageFragmentOnline fragment = new PageFragmentOnline();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        onlineFragmentView = inflater.inflate(R.layout.fragment_online, container, false);
        gridPlaylist = onlineFragmentView.findViewById(R.id.grid_playlist);
        progressBar = onlineFragmentView.findViewById(R.id.progress_bar);
        gridPlaylist.setVisibility(View.GONE);
        new Thread(()->{
            try {
                getPlaylistData();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            getActivity().runOnUiThread(()->{
                PublicPlaylistAdapter adapter = new PublicPlaylistAdapter(getContext(), R.layout.playlist_grid, playlist);
                gridPlaylist.setAdapter(adapter);
                gridPlaylist.setLayoutManager(new GridLayoutManager(getContext(), 2));
                progressBar.setVisibility(View.GONE);
                gridPlaylist.setVisibility(View.VISIBLE);

            });
        }).start();




        return onlineFragmentView;
    }


    public void getPlaylistData() throws InterruptedException {
        playlist = new ArrayList<>();
        Thread t = new Thread(() -> {
            try {
                String data = HttpRequest.getPublicPlaylist();
                JSONArray playlistArr = new JSONArray(new JSONObject(data).getString("data"));
                for (int i = 0; i < playlistArr.length(); i++) {
                    playlist.add(playlistArr.getJSONObject(i));
                }
            } catch (IOException | JSONException e) {
                Log.e("TAG", "" + e);
                e.printStackTrace();
            }
        });
        t.start();
        t.join();
    }

}