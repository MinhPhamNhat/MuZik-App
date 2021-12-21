package com.example.muzik;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import jp.wasabeef.blurry.Blurry;

import static java.lang.Thread.sleep;

public class PageFragmentAudio1 extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    public static PageFragmentAudio1 fragment;

    CurrentPlayList playList;
    static Song song;
    static ImageButton control, next, previous;
    static ImageView image;
    static TextView songName, artistName;
    SeekBar seek;
    Bitmap bitmap;
    long currentTime;
    boolean isDrag = false;

    View view;
    ViewGroup parent;

    public static PageFragmentAudio1 newInstance(int page) {
        if (fragment == null) {
            fragment = new PageFragmentAudio1();
        }
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_audio, container, false);
        parent = container;

        setupVariable();
        setupButton();

        return view;
    }

    public void setupVariable() {
        seek = view.findViewById(R.id.seek);
        control = view.findViewById(R.id.IBControllerPlay);
        next = view.findViewById(R.id.IBControllerNext);
        previous = view.findViewById(R.id.IBControllerPrevious);
        image = view.findViewById(R.id.IBControllerThumb);
        songName = view.findViewById(R.id.TVControllerSongName);
        artistName = view.findViewById(R.id.TVControllerArtistNames);
        playList = CurrentPlayList.getInstance();
    }

    public void setupButton() {
        control.setOnClickListener(v->{
            if (mMediaService.isStarted()) {
                play();
            }
            else {
                initAudio();
            }
        });

        next.setOnClickListener(v->{
            song = playList.getNextSong(mMediaService.getCurrentSong());
            playList.setIsNeedStarted(true);
            playList.setIsNewSonglist(false);
            initAudio();
            setupView();
        });

        previous.setOnClickListener(v->{
            song = playList.getPreviousSong(mMediaService.getCurrentSong());
            playList.setIsNeedStarted(true);
            playList.setIsNewSonglist(false);
            initAudio();
            setupView();
        });
    }

    public void setupView() {
        songName.setText(song.getName());
        getBitmap(song.getThumbnailLink());
        artistName.setText(song.getToStringArtist());
    }

    public static void setButtonAvailable() {
        if (mMediaService.mp != null) {
            if (mMediaService.mp.isPlaying()) {
                control.setImageResource(R.drawable.ic_baseline_pause_24);
            }else{
                control.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            }
        }
        else control.setImageResource(R.drawable.ic_baseline_play_arrow_24);
    }

    private ServiceConnection connection;
    boolean mBound = false;
    static MediaPlayerService mMediaService;

    @Override
    public void onResume() {
        super.onResume();

        if (mMediaService != null) {
            setupView();
            setButtonAvailable();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MediaPlayerService.MediaPlayerBinder binder = (MediaPlayerService.MediaPlayerBinder) service;
                mMediaService = binder.getService();

                mMediaService.addContext(parent.getContext());
                mBound = true;
                initAudio();

                setupView();
                setButtonAvailable();

                isReceiverRegister();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mBound = false;
            }
        };

        Intent intent =  new Intent(getActivity(), MediaPlayerService.class);
        getActivity().startService(intent);
        getActivity().bindService(intent,connection,getActivity().BIND_AUTO_CREATE);
    }

    @Override
    public void onPause() {
        super.onPause();
        mMediaService.removeContext(parent.getContext());
        parent.getContext().unregisterReceiver(mMediaService.getBroadcastReceiver());
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unbindService(connection);
        mBound = false;
    }

    public void initAudio(){
        isReceiverRegister();

        if (playList.isNewSonglist()) {
            song = playList.getFirstSong();
            playList.setIsNewSonglist(false);
        }
        else {
            if (playList.isNewSong()) {
                song = playList.getLastSong();
                playList.setIsNewSong(false);
            }
        }

        if (!playList.isNeedStarted()) {
            song = mMediaService.getCurrentSong();
        }
        else {
            mMediaService.startMediaPlayer(song);
            playList.setIsNeedStarted(false);
        }

        (new waitForDuration()).start();
        control.setImageResource(R.drawable.ic_baseline_pause_24);


        mMediaService.mp.setOnCompletionListener(mp -> {
            if (mMediaService.isStarted()){
                if (playList.isNextSongExist(song)) {
                    song = playList.getNextSong(song);
                    mMediaService.startMediaPlayer(song);
                    setupView();
                    setButtonAvailable();
                } else {
                    control.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                }
            }
        });

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    seek.setProgress(progress);
                    updateTimeIndicator(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isDrag = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (!mMediaService.mp.isPlaying()) {
                    control.setImageResource(R.drawable.ic_baseline_pause_24);
                    mMediaService.mp.start();
                }
                mMediaService.mp.seekTo(seekBar.getProgress());
                isDrag = false;
            }
        });

        (new beginSeekBarTracking()).start();
    }

    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            currentTime = msg.what;
            if (!isDrag) {
                updateTimeIndicator(currentTime);
                seek.setProgress((int) currentTime);
            }
        }
    };

    public class waitForDuration extends Thread {
        @Override
        public void run() {
            while(mMediaService.duration < 0) {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            seek.setMax((int) mMediaService.duration);
        }
    }

    public class beginSeekBarTracking extends Thread {
        public beginSeekBarTracking() { super("beginSeekBarTraking"); }

        @Override
        public void run() {
            boolean breaker = false;
            while(mMediaService.mp != null){
                try{
                    if (mMediaService.isStarted()) {
                        if (mMediaService.mp.isPlaying()) {
                            breaker = true;
                            Message mess = new Message();
                            mess.what = mMediaService.mp.getCurrentPosition();
                            handler.sendMessage(mess);
                            setButtonAvailable();
                        }
                    }
                    else if (breaker) {
                        try {
                            getActivity().runOnUiThread(() -> {
                                PageFragmentAudio1.this.setupView();
                                PageFragmentAudio1.this.setButtonAvailable();
                                updateTimeIndicator(0);
                                seek.setProgress(0);
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    sleep(1000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void play(){
        if (mMediaService.mp.isPlaying()) {
            control.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            mMediaService.pauseMediaPlayer();
        }else{
            control.setImageResource(R.drawable.ic_baseline_pause_24);
            mMediaService.resumeMediaPlayer();
        }
    }

    public void getBitmap(String link){
        if (song.thumbnail == null) {
            new Thread(() -> {
                URL url = null;
                try {
                    url = new URL(link);
                    bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    sleep(1000);
                } catch (MalformedURLException | InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(() -> {
                    song.thumbnail = bitmap;
                    image.setImageBitmap(song.thumbnail);
//                    setWallpager();
                    mMediaService.showNotification();
                    Blurry.with(parent.getContext()).from(bitmap).into(getActivity().findViewById(R.id.imageBackground));
                });
            }).start();
        } else  {
            image.setImageBitmap(song.thumbnail);
            Blurry.with(parent.getContext()).from(song.thumbnail).into(getActivity().findViewById(R.id.imageBackground));
        }
    }

    public void updateTimeIndicator(long current) {
        ((TextView) view.findViewById(R.id.timeIndicator)).setText(toMinute(current)+" / "+toMinute(mMediaService.duration));
    }

    public String toMinute(long miliseconds) {
        int seconds = (int) ((miliseconds / 1000) +0.5);
        return ((int) (seconds / 60)) + ":" +((seconds % 60) < 10 ? "0"+(seconds % 60) : (seconds % 60));
    }

    public BroadcastReceiver service= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("action");
            Log.e("testt",action);
            if (action.equals("UPDATE_AUDIO")) {
                    song = mMediaService.getCurrentSong();
                    setupView();
                    initAudio();
            }
        }
    };

    public void isReceiverRegister() {
        if (mMediaService != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mMediaService.createChannel();
                parent.getContext().registerReceiver(mMediaService.getBroadcastReceiver(), new IntentFilter("MuZikMediaPlayer"));
                parent.getContext().registerReceiver(service, new IntentFilter("MuZikAudioActivty"));
            }
        }
    }

    public void setWallpager() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int height = metrics.heightPixels;
            int width = metrics.widthPixels;

            WallpaperManager wallpaperManager = WallpaperManager.getInstance(getActivity().getApplicationContext());
            try {
                wallpaperManager.setBitmap(song.thumbnail, null, true, WallpaperManager.FLAG_LOCK);
            } catch (IOException e) {

            }
            wallpaperManager.suggestDesiredDimensions(width, height);
        }
    }
}