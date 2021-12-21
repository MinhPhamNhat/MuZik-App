package com.example.muzik;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public class MediaPlayerServiceConnection implements ServiceConnection {
    private MediaPlayerService mMediaService;
    private boolean serviceAvailable = false;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        serviceAvailable = true;
        mMediaService = ((MediaPlayerService.MediaPlayerBinder) service).getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        serviceAvailable = false;
    }
}
