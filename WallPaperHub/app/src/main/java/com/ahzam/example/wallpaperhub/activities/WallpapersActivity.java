package com.ahzam.example.wallpaperhub.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.ahzam.example.wallpaperhub.R;
import com.ahzam.example.wallpaperhub.adapters.WallpapersAdapter;
import com.ahzam.example.wallpaperhub.models.Wallpaper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WallpapersActivity extends AppCompatActivity {

    private List<Wallpaper> wallpaperList, wallpaperFavList;
    private RecyclerView rvWallpaper;
    private WallpapersAdapter wallpapersAdapter;

    private DatabaseReference dbWallpapers, dbFavourites;
    private ProgressBar wallProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpapers);

        Intent intent = getIntent();
        final String category = intent.getStringExtra("category");

        Toolbar toolbar = findViewById(R.id.wallpaper_toolbar);
        toolbar.setTitle(category);
        setSupportActionBar(toolbar);

        wallpaperList = new ArrayList<>();
        wallpaperFavList = new ArrayList<>();

        rvWallpaper = findViewById(R.id.wall_wallRecycler);
        rvWallpaper.setHasFixedSize(true);
        rvWallpaper.setLayoutManager(new LinearLayoutManager(this));

        wallpapersAdapter = new WallpapersAdapter(this, wallpaperList);

        rvWallpaper.setAdapter(wallpapersAdapter);

        wallProgress = findViewById(R.id.wall_wallProgress);


        dbWallpapers = FirebaseDatabase.getInstance().getReference("images").child(category);


        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            dbFavourites = FirebaseDatabase.getInstance().getReference("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("favourites")
                    .child(category);
            fetchFavouriteWallpapers(category);
        } else {
            fetchWallpapers(category);
        }

    }

    //  Fetch users favourite wallpapers from category
    private void fetchFavouriteWallpapers(final String category) {
        wallProgress.setVisibility(View.VISIBLE);
        dbFavourites.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                wallProgress.setVisibility(View.GONE);
                if (dataSnapshot.exists()) {
                    for (DataSnapshot wallpaperSnapshot : dataSnapshot.getChildren()) {

                        String id = wallpaperSnapshot.getKey();
                        String title = wallpaperSnapshot.child("title").getValue(String.class);
                        String desc = wallpaperSnapshot.child("desc").getValue(String.class);
                        String url = wallpaperSnapshot.child("url").getValue(String.class);

                        Wallpaper w = new Wallpaper(id, title, desc, url, category);
                        wallpaperFavList.add(w);

                    }
                }
                fetchWallpapers(category);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //  Fetch all the wallpapers of the category
    private void fetchWallpapers(final String category) {
        wallProgress.setVisibility(View.VISIBLE);
        dbWallpapers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                wallProgress.setVisibility(View.GONE);
                if (dataSnapshot.exists()) {
                    for (DataSnapshot wallpaperSnapshot : dataSnapshot.getChildren()) {

                        String id = wallpaperSnapshot.getKey();
                        String title = wallpaperSnapshot.child("title").getValue(String.class);
                        String desc = wallpaperSnapshot.child("desc").getValue(String.class);
                        String url = wallpaperSnapshot.child("url").getValue(String.class);

                        Wallpaper w = new Wallpaper(id, title, desc, url, category);

                        //  Check if wallpaper is favorite
                        if(isFavourite(w)) {
                            w.isFavourite = true;
                        }

                        wallpaperList.add(w);
                    }
                    wallpapersAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private boolean isFavourite(Wallpaper wallpaper) {
        for(Wallpaper f : wallpaperFavList) {
            if(f.id.equals(wallpaper.id)) {
                return true;
            }
        }
        return false;
    }
}
