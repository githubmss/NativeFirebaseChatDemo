package com.mss.myfirebasedemo.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.mss.myfirebasedemo.R;

/**
 * Created by deepakgupta on 15/2/17.
 */

public class SplashActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initUi();
    }

    private void initUi() {
        if (Build.VERSION.SDK_INT >= 23) {
            checkPermission();
        } else {
            startActivity();
        }
    }

    private void startActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        }, 700);
    }


    public void checkPermission() {
        //get all required elements
        int READ_EXTERNALSTORAGE_PERMISSION = ContextCompat.checkSelfPermission(SplashActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int WRITE_EXTERNALSTORAGE_PERMISSION = ContextCompat.checkSelfPermission(SplashActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int ACCESS_CAMERA_PREMISSION = ContextCompat.checkSelfPermission(SplashActivity.this, android.Manifest.permission.CAMERA);

        //Check permissions if permission already granted run if blog if not granted then run else blog
        if (READ_EXTERNALSTORAGE_PERMISSION == PackageManager.PERMISSION_GRANTED && WRITE_EXTERNALSTORAGE_PERMISSION == PackageManager.PERMISSION_GRANTED && ACCESS_CAMERA_PREMISSION == PackageManager.PERMISSION_GRANTED) {
            startActivity();
        } else {
            // Marshmallow+
            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA}, REQUEST_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity();
                } else {
                }
                return;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


}
