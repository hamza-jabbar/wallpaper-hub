package com.ahzam.example.wallpaperhub.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ahzam.example.wallpaperhub.R;
import com.ahzam.example.wallpaperhub.activities.WallpapersActivity;
import com.ahzam.example.wallpaperhub.models.Category;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {

    private Context mCtx;
    private List<Category> categoryList;

    private InterstitialAd mInterstitialAd;


    public CategoriesAdapter(Context mCtx, List<Category> categoryList) {
        this.mCtx = mCtx;
        this.categoryList = categoryList;

        mInterstitialAd = new InterstitialAd(mCtx);
        //  mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");              My ID
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");      //  Test ID
        //  Load the ad
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


    }

    @NonNull
    @Override
    public CategoriesAdapter.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesAdapter.CategoryViewHolder holder, int position) {
        Category cat = categoryList.get(position);
        holder.tvCatName.setText(cat.name);
        Glide.with(mCtx)
                .load(cat.thumb)
                .into(holder.ivCatThumbnail);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }


    class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvCatName;
        ImageView ivCatThumbnail;

        public CategoryViewHolder(View itemView) {
            super(itemView);

            tvCatName = itemView.findViewById(R.id.catItem_tvCatName);
            ivCatThumbnail = itemView.findViewById(R.id.catItem_ivCategory);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            //  When user clicks on category, the ad is loaded
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                Toast.makeText(mCtx, "Ad not loaded", Toast.LENGTH_LONG).show();
            }


            int p = getAdapterPosition();
            Category c = categoryList.get(p);

            Intent intent = new Intent(mCtx, WallpapersActivity.class);
            intent.putExtra("category", c.name);
            mCtx.startActivity(intent);

        }
    }
}
