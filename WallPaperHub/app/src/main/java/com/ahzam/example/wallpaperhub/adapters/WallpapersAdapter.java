package com.ahzam.example.wallpaperhub.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ahzam.example.wallpaperhub.R;
import com.ahzam.example.wallpaperhub.models.Wallpaper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


public class WallpapersAdapter extends RecyclerView.Adapter<WallpapersAdapter.WallpaperViewHolder> {

    private Context mCtx;
    private List<Wallpaper> wallpaperList;

    public WallpapersAdapter(Context mCtx, List<Wallpaper> wallPaperList) {
        this.mCtx = mCtx;
        this.wallpaperList = wallPaperList;
    }

    @NonNull
    @Override
    public WallpaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.wallpaper_item, parent, false);
        return new WallpaperViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WallpaperViewHolder holder, int position) {
        Wallpaper wall = wallpaperList.get(position);
        holder.tvWallName.setText(wall.title);
        Glide.with(mCtx)
                .load(wall.url)
                .into(holder.ivWallThumbnail);

        if (wall.isFavourite) {
            holder.cbFavourites.setChecked(true);
        }
    }

    @Override
    public int getItemCount() {
        return wallpaperList.size();
    }

    class WallpaperViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

        TextView tvWallName;
        ImageView ivWallThumbnail;

        CheckBox cbFavourites;
        ImageButton ibShare, ibDownload;

        public WallpaperViewHolder(View itemView) {
            super(itemView);

            tvWallName = itemView.findViewById(R.id.wallItem_tvWallName);
            ivWallThumbnail = itemView.findViewById(R.id.wallItem_ivWallpaper);

            cbFavourites = itemView.findViewById(R.id.wallItem_cbFavourite);
            ibDownload = itemView.findViewById(R.id.wallItem_ibDownload);
            ibShare = itemView.findViewById(R.id.wallItem_ibShare);

            cbFavourites.setOnCheckedChangeListener(this);
            ibDownload.setOnClickListener(this);
            ibShare.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.wallItem_ibShare:
                    shareWallpaper(wallpaperList.get(getAdapterPosition()));

                    break;
                case R.id.wallItem_ibDownload:
                    downloadWallpaper(wallpaperList.get(getAdapterPosition()));
                    break;
            }
        }

        private void shareWallpaper(Wallpaper wallpaper) {
            ((Activity) mCtx).findViewById(R.id.wall_wallProgress).setVisibility(View.VISIBLE);

            Glide.with(mCtx)
                    .asBitmap()
                    .load(wallpaper.url)
                    .into(new SimpleTarget<Bitmap>() {
                        //  Method will be called when the resource is ready
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            //  Get URI of the object
                            ((Activity) mCtx).findViewById(R.id.wall_wallProgress).setVisibility(View.GONE);

                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("image/*");
                            intent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(resource));

                            //  When user clicks on image, options to share image are shown
                            mCtx.startActivity(Intent.createChooser(intent, "" + R.string.app_name));

                        }
                    });
        }

        private Uri getLocalBitmapUri(Bitmap bitmap) {
            Uri bitmapUri = null;

            try {
                File file = new File(mCtx.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "wallpaper_hub_" + System.currentTimeMillis() + ".png");
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.close();
                bitmapUri = Uri.fromFile(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmapUri;
        }

        //  Method to download the wallpaper
        private void downloadWallpaper(final Wallpaper wallpaper) {
            ((Activity) mCtx).findViewById(R.id.wall_wallProgress).setVisibility(View.VISIBLE);

            Glide.with(mCtx)
                    .asBitmap()
                    .load(wallpaper.url)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override       //  Method will be called when the resource is ready
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            //  Get URI of the object
                            ((Activity) mCtx).findViewById(R.id.wall_wallProgress).setVisibility(View.GONE);

                            Intent intent = new Intent(Intent.ACTION_VIEW);

                            //  Save resource in External Storage
                            Uri uri = saveWallpaperAndGetUri(resource, wallpaper.id);

                            if (uri != null) {
                                intent.setDataAndType(uri, "image/*");

                                //  When user clicks on image, options to share image are shown
                                mCtx.startActivity(Intent.createChooser(intent, "" + R.string.app_name));
                            }

                        }
                    });

        }

        //  Returns the Uri of the wallpaper
        private Uri saveWallpaperAndGetUri(Bitmap bitmap, String id) {
            //  Check permission at runtime
            if (ContextCompat.checkSelfPermission(mCtx,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                //  If permission was already asked or not
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mCtx,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //  Open settings screen, from where the user can grant the permissions
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

                    Uri uri = Uri.fromParts("package", mCtx.getPackageName(), null);

                    intent.setData(uri);
                    mCtx.startActivity(intent);

                } else {

                    //  Ask for the permission
                    ActivityCompat.requestPermissions((Activity) mCtx,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            100);

                }
                //  If permission not granted, stop further execution
                return null;
            }

            //  Create a folder to where the wallpapers will be saved
            File folder = new File(Environment.getExternalStorageDirectory().toString()
                    + "/wallpaper_hub");
            folder.mkdirs();

            //  File to store the wallpaper
            File file = new File(folder, id + ".jpg");

            try {
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();

                return Uri.fromFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        //  When the checked state is changed
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            //  Check user login status
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast.makeText(mCtx, "Please login", Toast.LENGTH_LONG).show();
                compoundButton.setChecked(false);
                return;
            }

            //  Get selected Wallpaper
            int position = getAdapterPosition();
            Wallpaper w = wallpaperList.get(position);

            //  DB reference
            DatabaseReference dbFavourites = FirebaseDatabase.getInstance().getReference("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("favourites")
                    .child(w.category);

            if (b) {
                //  Save wallpaper to favourites list
                dbFavourites.child(w.id).setValue(w);
            } else {
                //  Remove from the list
                dbFavourites.child(w.id).setValue(null);
            }
        }
    }
}
