package com.ahzam.example.wallpaperhub.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class FavouritesFragment extends Fragment {

    private List<Wallpaper> favWallpaperList;
    private RecyclerView rvFavourites;
    private ProgressBar pbFavourites;

    private WallpapersAdapter wallpapersAdapter;

    private DatabaseReference dbFavouriteWallpapers;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourites, container, false);
    }

    //  Users can view this screen when they are logged in
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        favWallpaperList = new ArrayList<>();
        rvFavourites = view.findViewById(R.id.ff_favRecycler);
        pbFavourites = view.findViewById(R.id.ff_favProgress);
        wallpapersAdapter = new WallpapersAdapter(getActivity(), favWallpaperList);

        rvFavourites.setHasFixedSize(true);
        rvFavourites.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvFavourites.setAdapter(wallpapersAdapter);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            //  User is not logged in --> View login Screen in Settings
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_area, new SettingsFragment()).commit();

            return;
        }

        dbFavouriteWallpapers = FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("favourites");

        pbFavourites.setVisibility(View.VISIBLE);

        dbFavouriteWallpapers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                pbFavourites.setVisibility(View.GONE);

                //  read all favourite nodes ie. children
                for (DataSnapshot category : dataSnapshot.getChildren()) {

                    //  read all wallpaper nodes inside the specified category in the above loop
                    for (DataSnapshot wallpaperSnapshot : category.getChildren()) {

                        String id = wallpaperSnapshot.getKey();
                        String title = wallpaperSnapshot.child("title").getValue(String.class);
                        String desc = wallpaperSnapshot.child("desc").getValue(String.class);
                        String url = wallpaperSnapshot.child("url").getValue(String.class);

                        Wallpaper w = new Wallpaper(id, title, desc, url, category.getKey());
                        w.isFavourite = true;

                        favWallpaperList.add(w);
                    }
                }
                wallpapersAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}
