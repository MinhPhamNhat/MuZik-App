package com.example.muzik;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class AudioPlayActivity extends AppCompatActivity {

    LinearLayout back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_pages);


        back = findViewById(R.id.BtnAudioBack);

        back.setOnClickListener(v->{
            Log.e("TAG" , "HELLO");
            finish();
        });

        ViewPager viewPager = (ViewPager) findViewById(R.id.ViewPagerAudio);
        AudioViewPagerAdapter AudioViewPagerAdapter = new AudioViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(AudioViewPagerAdapter);

    }
}
