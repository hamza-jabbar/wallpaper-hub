package com.ahzam.example.wallpaperhub.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.ahzam.example.wallpaperhub.R;
import com.ahzam.example.wallpaperhub.fragments.FavouritesFragment;
import com.ahzam.example.wallpaperhub.fragments.HomeFragment;
import com.ahzam.example.wallpaperhub.fragments.SettingsFragment;
import com.google.android.gms.ads.MobileAds;

public class HomeActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        MobileAds.initialize(this,
                "ca-app-pub-2307196974068839~5976127200");


        bottomNavigationView = findViewById(R.id.home_BottomMenu);

        //  Implements the interface used below
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        //  Displays the HomeFragment by default
        displayFragment(new HomeFragment());

    }

    private void displayFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_area, fragment)
                .commit();
    }

    //  Called when an item from the navigation menu is selected
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment;
        switch (item.getItemId()) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                break;
            case R.id.nav_fav:
                fragment = new FavouritesFragment();
                break;
            case R.id.nav_settings:
                fragment = new SettingsFragment();
                break;
            default:
                fragment = new HomeFragment();
        }
        displayFragment(fragment);
        return true;
    }
}