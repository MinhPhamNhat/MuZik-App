package com.example.muzik;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button BtnBack;
    private EditText LgUsername, LgPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        BtnBack = findViewById(R.id.BtnLoginBack);

        BtnBack.setOnClickListener(this);

        ViewPager viewPager = (ViewPager) findViewById(R.id.ViewPagerLogin);
        LoginViewPagerAdapter LoginViewPagerAdapter = new LoginViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(LoginViewPagerAdapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.TL_LoginPage);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onClick(View v) {
        int clickId = v.getId();
        if (clickId == this.BtnBack.getId()){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void checkUsername(){
    }

}
