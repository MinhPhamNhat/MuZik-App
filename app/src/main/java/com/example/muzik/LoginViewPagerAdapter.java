package com.example.muzik;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class LoginViewPagerAdapter extends LoginPageFragmentPagerAdapter {

    public LoginViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

//    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0)
        {
            fragment = new PageFragmentLogin();
        }
        else if (position == 1)
        {
            fragment = new PageFragmentRegister();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

//    @Override
//    public CharSequence getPageTitle(int position) {
//        String title = null;
//        if (position == 0)
//        {
//            title = "Tab-1";
//        }
//        else if (position == 1)
//        {
//            title = "Tab-2";
//        }
//        else if (position == 2)
//        {
//            title = "Tab-3";
//        }
//        return title;
//    }
}
