package com.example.muzik;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AudioActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.sendBroadcast(new Intent("MuZikAudioActivty")
        .putExtra("action", intent.getAction()));
    }
}
