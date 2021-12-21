package com.example.muzik;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import jp.wasabeef.blurry.Blurry;

import static java.lang.Thread.sleep;

public class MediaPlayerService extends Service {
    private final IBinder binder = new MediaPlayerBinder();
    private static boolean paused = false;
    private static boolean started = false;
    private static Song currentsong;
    private static NotificationManager notificationManager;
    private static ArrayList<Context> ctxlist = new ArrayList<>();
    public static ArrayList<String> string = new ArrayList<>();

    public ArrayList<String> getString() {
        return string;
    }

    public MediaPlayer mp = new MediaPlayer();
    private CurrentPlayList playlist = CurrentPlayList.getInstance();

    long duration = -100000;

    private final BroadcastReceiver broadcastReceiver = getBroadcastReceiver();



    public class MediaPlayerBinder extends Binder {
        MediaPlayerService getService () {
            return MediaPlayerService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    public void startMediaPlayer(Song newSong) {
        currentsong = newSong;
        updateController();
        try {
            mp.reset();
            mp.setDataSource(this, Uri.parse("http://api.mp3.zing.vn/api/streaming/audio/" + currentsong.getId() + "/320"));
            mp.setOnPreparedListener(mp -> {
                mp.start();
                showNotification();
                duration = mp.getDuration();
                started = true;
            });
            mp.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showNotification() {
        MediaPlayerNotification.createMediaPlayerNotification(MediaPlayerService.this, currentsong, R.drawable.ic_baseline_pause_24);
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isPaused() {
        return paused;
    }

    public void resumeMediaPlayer() {
        mp.start();
        MediaPlayerNotification.createMediaPlayerNotification(MediaPlayerService.this, currentsong, R.drawable.ic_baseline_pause_24);
        for (Context ctx: ctxlist)
            ((ImageButton) ((Activity) ctx).findViewById(R.id.IBControllerPlay)).setImageResource(R.drawable.ic_baseline_pause_24);
        paused = false;
    }

    public void pauseMediaPlayer() {
        mp.pause();
        MediaPlayerNotification.createMediaPlayerNotification(MediaPlayerService.this, currentsong, R.drawable.ic_baseline_play_arrow_24);
        for (Context ctx: ctxlist) ((ImageButton) ((Activity) ctx).findViewById(R.id.IBControllerPlay)).setImageResource(R.drawable.ic_baseline_play_arrow_24);
        paused = true;
    }

    public void updateController() {
        Intent intentUpdate = new Intent(this, AudioActionReceiver.class)
                .setAction("UPDATE_AUDIO");
        try {
            (PendingIntent.getBroadcast(this, 0, intentUpdate, PendingIntent.FLAG_UPDATE_CURRENT)).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }

    }

    public Song getCurrentSong() {
        return currentsong;
    }

    public void stopService() {
        mp.reset();
        notificationManager.cancelAll();
        started = false;
        paused = false;
    }

    public BroadcastReceiver getBroadcastReceiver() {
        if (broadcastReceiver == null) {
            return new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getExtras().getString("action");
                    Log.e("testt", action);
                    Log.e("testt", "s: "+string.toString());
                    switch (action) {
                        case MediaPlayerNotification.ACTION_PREVIOUS:
                            startMediaPlayer(playlist.getPreviousSong(currentsong));
                            break;
                        case MediaPlayerNotification.ACTION_PLAY:
                            if (isPaused()) resumeMediaPlayer();
                            else  pauseMediaPlayer();
                            break;
                        case MediaPlayerNotification.ACTION_NEXT:
                            startMediaPlayer(playlist.getNextSong(currentsong));
                            break;
                        case MediaPlayerNotification.ACTION_STOP:
                            stopService();
                            break;
                    }
                }
            };
        }
        return broadcastReceiver;
    }

    public void createChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(MediaPlayerNotification.CHANNEL_ID, "MuZik", NotificationManager.IMPORTANCE_HIGH);

            notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public void removeContext(Context ctx) {
        this.ctxlist.remove(ctx);
        registerReceiver(getBroadcastReceiver(), new IntentFilter("MuZikMediaPlayer"));
    }

    public void addContext(Context ctx) {
        ctxlist.add(ctx);
        try {
            unregisterReceiver(getBroadcastReceiver());
        } catch (IllegalArgumentException e) {}
    }
}
