package com.example.muzik;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

public class SearchActivity extends AppCompatActivity {
    RecyclerView listMusic;
    ArrayList<Song> songs;
    MyAdapter adapter;
    InitSongTask search = null;
    int waitingTime = 350;
    CountDownTimer cntr;
    Thread searchThread;
    String newText;
    AtomicReference<String> data;
    EditText searchText;

    private LinearLayout mController;
    private ImageView mControllerThumb;
    private TextView mControllerSongName;
    private TextView mControllerArtistNames;
    private ImageButton mControllerPrevious;
    private ImageButton mControllerPlay;
    private ImageButton mControllerNext;
    private ImageView mLoading;

    private CurrentPlayList playlist;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        Intent intent = getIntent();
        listMusic = findViewById(R.id.list_music);
        songs = new ArrayList<Song>();
        adapter = new MyAdapter(SearchActivity.this, R.layout.list_row, songs, 1);
        listMusic.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        listMusic.setAdapter(adapter);
        searchText = findViewById(R.id.ETKeyWord);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                newText = s.toString();
                if (cntr != null || newText == null || newText.equals("")) {
                    cntr.cancel();
                    if (searchThread != null) searchThread.interrupt();
                }
                (cntr = new waitForInputStop(waitingTime, waitingTime)).start();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        findViewById(R.id.BtnSearchBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        setupVariable();
        setupButtons();
    }

    public void setupVariable() {
        mController = findViewById(R.id.LLController);
        mControllerThumb = findViewById(R.id.IBControllerThumb);
        mControllerSongName = findViewById(R.id.TVControllerSongName);
        mControllerArtistNames = findViewById(R.id.TVControllerArtistNames);
        mControllerPrevious = findViewById(R.id.IBControllerPrevious);
        mControllerPlay = findViewById(R.id.IBControllerPlay);
        mControllerNext = findViewById(R.id.IBControllerNext);
        mLoading = findViewById(R.id.progress_bar);

        playlist = CurrentPlayList.getInstance();

        ((AnimationDrawable) mLoading.getDrawable()).start();
    }

    public void setupButtons() {
        ((ImageButton) findViewById(R.id.IBControllerPlay)).setOnClickListener((View.OnClickListener) v -> {
            if (mBound == true) {
                if (mMediaService.isStarted()) {
                    if (mMediaService.isPaused()) {
                        mMediaService.resumeMediaPlayer();
                        ((ImageButton) findViewById(R.id.IBControllerPlay)).setBackgroundResource(R.drawable.ic_baseline_pause_24);
                    } else {
                        mMediaService.pauseMediaPlayer();
                        ((ImageButton) findViewById(R.id.IBControllerPlay)).setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
                    }
                }
            }
        });

        ((ImageButton) findViewById(R.id.IBControllerPlay)).setOnClickListener((View.OnClickListener) v -> {
            if (mBound == true) {
                if (mMediaService.isStarted()) {
                    if (mMediaService.isPaused()) {
                        mMediaService.resumeMediaPlayer();
                        ((ImageButton) findViewById(R.id.IBControllerPlay)).setImageResource(R.drawable.ic_baseline_pause_24);
                    } else {
                        mMediaService.pauseMediaPlayer();
                        ((ImageButton) findViewById(R.id.IBControllerPlay)).setImageResource(R.drawable.ic_baseline_play_arrow_24);
                    }
                }
            }
        });

        ((ImageButton) findViewById(R.id.IBControllerNext)).setOnClickListener((View.OnClickListener) v -> {
            if (mBound == true) {
                mMediaService.startMediaPlayer((playlist.getNextSong(mMediaService.getCurrentSong())));
                setButtonAvailable();
                setupController();
            }
        });

        ((ImageButton) findViewById(R.id.IBControllerPrevious)).setOnClickListener((View.OnClickListener) v -> {
            if (mBound == true) {
                mMediaService.startMediaPlayer((playlist.getPreviousSong(mMediaService.getCurrentSong())));
                setButtonAvailable();
                setupController();
            }
        });

        mController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchActivity.this, AudioPlayActivity.class));
                playlist.setIsNeedStarted(false);
            }
        });
    }

    public void setButtonAvailable() {
        if (mMediaService.mp != null) {
            if (mMediaService.mp.isPlaying()) {
                mControllerPlay.setImageResource(R.drawable.ic_baseline_pause_24);
            }else{
                mControllerPlay.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            }
        }
        else mControllerPlay.setImageResource(R.drawable.ic_baseline_play_arrow_24);
    }

    public void setupController() {
        if (mMediaService != null) {
            if (mMediaService.isStarted()) {
                mController.setVisibility(View.VISIBLE);
                Song song = mMediaService.getCurrentSong();

                mControllerSongName.setText(song.getName());
                mControllerArtistNames.setText(song.getToStringArtist());
                if (mMediaService.isPaused())
                    mControllerPlay.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                else mControllerPlay.setImageResource(R.drawable.ic_baseline_pause_24);

                setButtonAvailable();

                mControllerThumb.setBackground(new BitmapDrawable(getResources(), song.thumbnail));
            } else mController.setVisibility(View.GONE);
        } else mController.setVisibility(View.GONE);
    }

    private ServiceConnection connection;
    boolean mBound = false;
    MediaPlayerService mMediaService;

    @Override
    protected void onStart() {
        super.onStart();

        Log.e("testt", "hello");
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MediaPlayerService.MediaPlayerBinder binder = (MediaPlayerService.MediaPlayerBinder) service;
                mMediaService = binder.getService();
                mMediaService.addContext(SearchActivity.this);
                mBound = true;
                setupController();

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mMediaService.createChannel();
                    mMediaService.getString().add("Search");
                    registerReceiver(mMediaService.getBroadcastReceiver(), new IntentFilter("MuZikMediaPlayer"));
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mBound = false;
            }
        };

        Intent intent =  new Intent(this, MediaPlayerService.class);
        startService(intent);
        bindService(intent,connection,BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMediaService.removeContext(this);
        mMediaService.getString().remove("Search");
        unregisterReceiver(mMediaService.getBroadcastReceiver());
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        mBound = false;
    }

    public class waitForInputStop extends CountDownTimer {
        public waitForInputStop(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }


        @Override
        public void onFinish() {
            songs = new ArrayList<Song>();
            adapter.setData(songs);
            searchOrInput(true);

            if (!newText.equals("")) {
                if (search != null) {
                    search.cancel = true;
                }
                data = new AtomicReference<>("");

                if (searchThread != null) {
                    searchThread.interrupt();
                }
                (searchThread = new beginSearchThread()).start();
            } else {
                searchOrInput(false);
            }
        }
    }

    public class beginSearchThread extends Thread {

        public beginSearchThread() {
            super("BeginSearchThread");
        }

        @Override
        public void run() {
            try {
                data.set(HttpRequest.getByFilter(newText));


                search = (InitSongTask) new InitSongTask();
                search.execute(data.toString());
                ArrayList<Song> temp = search.getTask();
                if (temp != null) {
                    songs = temp;
                    runOnUiThread(() -> {
                        adapter.setData(songs);
                        if (songs.isEmpty()) {
                            findViewById(R.id.search_layout).setVisibility(View.VISIBLE);
                            findViewById(R.id.list_music).setVisibility(View.GONE);
                            mLoading.setVisibility(View.INVISIBLE);
                            ((TextView) findViewById(R.id.search_text)).setText("Không có bài nhạc nào tên vậy hết");
                        } else {
                            findViewById(R.id.search_layout).setVisibility(View.GONE);
                            findViewById(R.id.list_music).setVisibility(View.VISIBLE);
                        }
                    });
                }
            } catch (IOException | InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public void searchOrInput(boolean search) {
        if (search) {
            findViewById(R.id.search_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.list_music).setVisibility(View.GONE);
            mLoading.setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.search_text)).setText("Đang tìm kiếm...");
        } else {
            findViewById(R.id.search_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.list_music).setVisibility(View.GONE);
            mLoading.setVisibility(View.INVISIBLE);
            ((TextView) findViewById(R.id.search_text)).setText("Nhập từ khóa tìm kiếm");
        }
    }
}