package com.mss.myfirebasedemo.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.mss.myfirebasedemo.R;

public class FullScreenImageActivity extends AppCompatActivity {

    private ImageView mImageView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        initUi();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    private void initUi() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        mImageView = (ImageView) findViewById(R.id.img_pic);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setValues();
    }

    private void setValues() {
        String urlPhotoClick;
        urlPhotoClick = getIntent().getStringExtra("urlPhotoClick");
        Glide.with(this).load(urlPhotoClick).asBitmap().override(640, 640).fitCenter().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onLoadStarted(Drawable placeholder) {
                progressDialog.setMessage("Loading...");
                progressDialog.show();
            }

            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                progressDialog.dismiss();
                mImageView.setImageBitmap(resource);
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                progressDialog.dismiss();
            }
        });
    }

}
