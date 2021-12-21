package com.example.muzik;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MediaPlayerNotification {
    public static final String CHANNEL_ID = "MuZik_Channel";
    public static final String ACTION_PREVIOUS = "actionprevious";
    public static final String ACTION_PLAY = "actionplay";
    public static final String ACTION_NEXT = "actionnext";
    public static final String ACTION_STOP = "actionstop";

    public static Notification notification;

    private static Context ctx;
    private static int playbutton;

    public static void createMediaPlayerNotification(Context context, Song song, int playbtn) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ctx = context;
            playbutton = playbtn;

            if (song.getThumbnail() == null) {
                buildNotification(song, BitmapFactory.decodeResource(ctx.getResources(), R.drawable.audio_default));
            }
            else {
                buildNotification(song, song.getThumbnail());
            }
        }
    }

    private static void buildNotification(Song song, Bitmap icon) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(ctx);
        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(ctx, "tag");

        notification = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_music_note_24)
                .setContentTitle(song.getName())
                .setContentText(song.getToStringArtist())
                .setLargeIcon(icon)
                .setOnlyAlertOnce(true)
                .setShowWhen(false)
                .addAction(R.drawable.ic_baseline_skip_previous_24, "Previous", pendingIntentPrevious())
                .addAction(playbutton, "Previous", pendingIntentPlay())
                .addAction(R.drawable.ic_baseline_skip_next_24, "Next", pendingIntentNext())
                .addAction(R.drawable.ic_baseline_stop_24, "Stop", pendingIntentStop())
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        notificationManagerCompat.notify(1, notification);
    }

    private static PendingIntent pendingIntentPrevious() {
        Intent intentPrevious = new Intent(ctx, MediaPlayerNotificationActionReceiver.class)
                .setAction(ACTION_PREVIOUS);
        return PendingIntent.getBroadcast(ctx, 0, intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static PendingIntent pendingIntentPlay() {
        Intent intentPlay = new Intent(ctx, MediaPlayerNotificationActionReceiver.class)
                .setAction(ACTION_PLAY);
        return PendingIntent.getBroadcast(ctx, 0, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static PendingIntent pendingIntentNext() {
        Intent intentNext = new Intent(ctx, MediaPlayerNotificationActionReceiver.class)
                .setAction(ACTION_NEXT);
        return PendingIntent.getBroadcast(ctx, 0, intentNext, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static PendingIntent pendingIntentStop() {
        Intent intentNext = new Intent(ctx, MediaPlayerNotificationActionReceiver.class)
                .setAction(ACTION_STOP);
        return PendingIntent.getBroadcast(ctx, 0, intentNext, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
