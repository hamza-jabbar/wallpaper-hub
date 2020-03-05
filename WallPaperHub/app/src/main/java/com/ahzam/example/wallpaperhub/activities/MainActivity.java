package com.ahzam.example.wallpaperhub.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ahzam.example.wallpaperhub.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(i);
        finish();

    }
}
