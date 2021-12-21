package com.example.muzik;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class HomePageFragmentPagerAdapter extends FragmentPagerAdapter {
    private int tabsNumber;
    private String tabTitles[] = new String[] { "My muzik", "Online"};

    public HomePageFragmentPagerAdapter(@NonNull FragmentManager fm, int behavior, int tabs) {
        super(fm, behavior);
        this.tabsNumber = tabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new PageFragmentOffline();
            case 1:
                return new PageFragmentOnline();
            case 2:
                return new Settings();
            default: return  null;
        }
    }

    @Override
    public int getCount() {
        return tabsNumber;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }

}
