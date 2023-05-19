package com.tanvir.task;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.file.FileResource;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {
    private Button button;
    private ImageView imageView;
    private boolean isConnected;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();


        button.setOnClickListener(v -> {
            if (isConnected) {
                //time will be change so we get random image or we can use random number
                loadImageFromNetwork("https://picsum.photos/200?random=" + System.currentTimeMillis());
            } else {
                showErrorMessage("No Internet Connection");
            }
        });

    }
    public void init(){
        button = findViewById(R.id.buttonid);
        imageView = findViewById(R.id.imageView);
        getCachedBitmap();
        isConnected = checkNetworkConnectivity();
    }

    private boolean checkNetworkConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void saveImageToCache(Bitmap bitmap) {
        Context context = getApplicationContext();
        File cacheDir = context.getCacheDir();
        File file = new File(cacheDir, "image.png");
        try {
            FileOutputStream fos = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void getCachedBitmap(){
        Context context = getApplicationContext();
        File cacheDir = context.getCacheDir();
        File file = new File(cacheDir, "image.png");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(file), options);
        imageView.setImageBitmap(bitmap);
    }



    private void loadImageFromNetwork(final String imageUrl) {
        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        imageView.setImageBitmap(resource);
                        saveImageToCache(resource);

                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
