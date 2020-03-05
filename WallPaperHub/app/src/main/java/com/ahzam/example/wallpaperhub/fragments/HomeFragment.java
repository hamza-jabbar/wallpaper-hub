package com.ahzam.example.wallpaperhub.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.ahzam.example.wallpaperhub.R;
import com.ahzam.example.wallpaperhub.adapters.CategoriesAdapter;
import com.ahzam.example.wallpaperhub.models.Category;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private ProgressBar progressBar;
    private DatabaseReference dbCategories;

    private List<Category> categoryList;
    private RecyclerView rvCategories;

    private CategoriesAdapter categoriesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.fh_catProgress);
        progressBar.setVisibility(View.VISIBLE);

        rvCategories = view.findViewById(R.id.fh_catRecycler);rvCategories.setHasFixedSize(true);
        rvCategories.setLayoutManager(new LinearLayoutManager(getActivity()));

        categoryList = new ArrayList<>();
        categoriesAdapter = new CategoriesAdapter(getActivity(), categoryList);

        rvCategories.setAdapter(categoriesAdapter);

        //  Refer to the categories node in the DB
        dbCategories = FirebaseDatabase.getInstance().getReference("categories");
        dbCategories.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    progressBar.setVisibility(View.GONE);
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String name = ds.getKey();
                        String desc = ds.child("desc").getValue(String.class);
                        String thumb = ds.child("thumbnail").getValue(String.class);

                        Category category = new Category(name, desc, thumb);
                        categoryList.add(category);

                        //  Add the categories inside the RecyclerView
                        categoriesAdapter.notifyDataSetChanged();   //  Reload RecyclerView
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


}
