package com.example.muzik;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PageFragmentAudio2 extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";


    public static PageFragmentRegister newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragmentRegister fragment = new PageFragmentRegister();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    RecyclerView playlistInAudioLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audio_2, container, false);
        playlistInAudioLayout = view.findViewById(R.id.playlist_song_list);
        MyAdapter adapter = new MyAdapter(getContext(), R.layout.list_row, CurrentPlayList.getInstance().getSongList(), 1);
        playlistInAudioLayout.setAdapter(adapter);
        playlistInAudioLayout.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        return view;
    }
}