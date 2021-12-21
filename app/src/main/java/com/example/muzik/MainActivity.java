package com.example.muzik;

import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private NavigationView mNavigationView;
    private ViewPager mPager;
    private TabLayout mTabLayout;
    private HomePageFragmentPagerAdapter mAdapter;
    private ActionBar mToolBar;
    private DrawerLayout mDrawerLayout;
    private ServiceConnection connection;
    private final int MY_REQUEST_CODE = 1;

    boolean mBound = false;
    MediaPlayerService mMediaService;
    SharedPreferences sharedPreferences;
    TextView navUsername, navEmail;
    ImageView navAvatar;

    private LinearLayout mController;
    private ImageView mControllerThumb;
    private TextView mControllerSongName;
    private TextView mControllerArtistNames;
    private ImageButton mControllerPrevious;
    private ImageButton mControllerPlay;
    private ImageButton mControllerNext;

    private CurrentPlayList playlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.TBMain));

        setupVariable();
        setupToolBar();
        setupButtons();
        setupViewPagerAndTabLayout();
        setupSharedPreference();
    }

    public void setupVariable() {
        mToolBar = getSupportActionBar();
        mDrawerLayout = findViewById(R.id.drawer);
        mPager = findViewById(R.id.viewpager);
        mTabLayout = findViewById(R.id.tablayout);

        mNavigationView = findViewById(R.id.nav_view);
        View headerView = mNavigationView.getHeaderView(0);
        navUsername = headerView.findViewById(R.id.nav_username);
        navEmail = headerView.findViewById(R.id.nav_email);
        navAvatar = headerView.findViewById(R.id.navAva);

        sharedPreferences = getSharedPreferences("SESSION", MODE_PRIVATE);

        mController = findViewById(R.id.LLController);
        mControllerThumb = findViewById(R.id.IBControllerThumb);
        mControllerSongName = findViewById(R.id.TVControllerSongName);
        mControllerArtistNames = findViewById(R.id.TVControllerArtistNames);
        mControllerPrevious = findViewById(R.id.IBControllerPrevious);
        mControllerPlay = findViewById(R.id.IBControllerPlay);
        mControllerNext = findViewById(R.id.IBControllerNext);

        playlist = CurrentPlayList.getInstance();
    }

    public void setupViewPagerAndTabLayout() {
        mAdapter = new HomePageFragmentPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, mTabLayout.getTabCount());
        mPager.setAdapter(mAdapter);

        mTabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mPager));
        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mPager.setCurrentItem(1);
    }

    public void setupToolBar() {
        mToolBar.setDisplayShowTitleEnabled(false);
    }

    public void setupButtons() {
        mNavigationView.setNavigationItemSelectedListener(this);

        ((ImageButton) findViewById(R.id.BtnNavDrawer)).setOnClickListener((View.OnClickListener) v -> mDrawerLayout.openDrawer(GravityCompat.START));

        ((ImageButton) findViewById(R.id.BtnSearch)).setOnClickListener((View.OnClickListener) v -> {
            startActivity(new Intent(MainActivity.this, SearchActivity.class));
        });

        ((ImageButton) findViewById(R.id.IBControllerPlay)).setOnClickListener((View.OnClickListener) v -> {
            if (mBound == true) {
                if (mMediaService.isStarted()) {
                    if (mMediaService.isPaused()) {
                        mMediaService.resumeMediaPlayer();
                        ((ImageButton) findViewById(R.id.IBControllerPlay)).setImageResource(R.drawable.ic_baseline_pause_24);
                    } else {
                        mMediaService.pauseMediaPlayer();
                        ((ImageButton) findViewById(R.id.IBControllerPlay)).setImageResource(R.drawable.ic_baseline_play_arrow_24);
                    }
                }
            }
        });

        ((ImageButton) findViewById(R.id.IBControllerNext)).setOnClickListener((View.OnClickListener) v -> {
            if (mBound == true) {
                mMediaService.startMediaPlayer((playlist.getNextSong(mMediaService.getCurrentSong())));
                setButtonAvailable();
                setupController();
            }
        });

        ((ImageButton) findViewById(R.id.IBControllerPrevious)).setOnClickListener((View.OnClickListener) v -> {
            if (mBound == true) {
                mMediaService.startMediaPlayer((playlist.getPreviousSong(mMediaService.getCurrentSong())));
                setButtonAvailable();
                setupController();
            }
        });

        mController.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AudioPlayActivity.class));
            playlist.setIsNeedStarted(false);
        });
    }

    public void setupController() {
        if (mMediaService != null) {
            if (mMediaService.isStarted()) {
                mController.setVisibility(View.VISIBLE);
                Song song = mMediaService.getCurrentSong();

                mControllerSongName.setText(song.getName());
                mControllerArtistNames.setText(song.getToStringArtist());
                if (mMediaService.isPaused())
                    mControllerPlay.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                else mControllerPlay.setImageResource(R.drawable.ic_baseline_pause_24);

                setButtonAvailable();

                mControllerThumb.setBackground(new BitmapDrawable(getResources(), song.thumbnail));
            } else mController.setVisibility(View.GONE);
        } else mController.setVisibility(View.GONE);
    }

    public void setButtonAvailable() {
        if (mMediaService.mp != null) {
            if (mMediaService.mp.isPlaying()) {
                mControllerPlay.setImageResource(R.drawable.ic_baseline_pause_24);
            }else{
                mControllerPlay.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            }
        }
        else mControllerPlay.setImageResource(R.drawable.ic_baseline_play_arrow_24);
    }

    public void setupSharedPreference() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (sharedPreferences.contains("name") && sharedPreferences.contains("email") && sharedPreferences.contains("avatar")){
            setLogoutMenu();
            navUsername.setText(sharedPreferences.getString("name", null));
            navEmail.setText(sharedPreferences.getString("email", null));
            navAvatar.setImageBitmap(StringToBitMap(sharedPreferences.getString("avatar", null)));
        }else
            setLoginMenu();
    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        if(item.getItemId() == R.id.menu_login){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            startActivityForResult(intent, MY_REQUEST_CODE);
        }

        if(item.getItemId() == R.id.menu_logout){
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Bạn có muốn đăng xuất không ?");

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        sharedPreferences.edit().clear().commit();
                        navUsername.setText("UnknownUser");
                        navAvatar.setImageResource(R.drawable.default_user);
                        navEmail.setText("");
                        setLoginMenu();
                    }
                });
                alert.setNegativeButton("Hủy",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        });
                alert.show();
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MY_REQUEST_CODE) {
                Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MediaPlayerService.MediaPlayerBinder binder = (MediaPlayerService.MediaPlayerBinder) service;
                mMediaService = binder.getService();

                mMediaService.addContext(MainActivity.this);

                mBound = true;
                setupController();

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mMediaService.createChannel();
                    mMediaService.getString().add("Main");
                    registerReceiver(mMediaService.getBroadcastReceiver(), new IntentFilter("MuZikMediaPlayer"));
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mBound = false;
            }
        };

        Intent intent =  new Intent(this, MediaPlayerService.class);
        startService(intent);
        bindService(intent,connection,BIND_AUTO_CREATE);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mMediaService.removeContext(this);
        mMediaService.getString().remove("Main");
        unregisterReceiver(mMediaService.getBroadcastReceiver());
    }


    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        mBound = false;
    }

    public void setLogoutMenu(){
        mNavigationView.getMenu().clear();
        mNavigationView.inflateMenu(R.menu.logout_menu);
    }

    public void setLoginMenu(){
        mNavigationView.getMenu().clear();
        mNavigationView.inflateMenu(R.menu.login_menu);
    }

}