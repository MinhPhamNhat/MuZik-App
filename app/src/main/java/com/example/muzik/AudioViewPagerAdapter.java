package com.example.muzik;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class AudioViewPagerAdapter extends AudioPageFragmentPagerAdapter {

    public AudioViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

//    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0)
        {
            fragment = new PageFragmentAudio1();
        }
        else if (position == 1)
        {
            fragment = new PageFragmentAudio2();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

}
