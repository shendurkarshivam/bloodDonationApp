package com.example.blooddonationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.blooddonationapp.Utils.SharedPreference;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    SharedPreference pref;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = SharedPreference.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                verifyUserSet();

            }
        }, 1500);

        //goToSignUpPage();
    }

    private void verifyUserSet() {
        if(pref.getData(SharedPreference.signup, getApplicationContext())!=null && pref.getData(SharedPreference.signup, getApplicationContext()).equals("true")
                 ){
            goToMainPage();
        }else {
            goToSignUpPage();
        }
    }

    private void goToMainPage() {
        startActivity(new Intent(MainActivity.this, StartActivity.class));
        finish();
    }

    private void goToSignUpPage() {

        startActivity(new Intent(MainActivity.this, SignUpPage.class));
        finish();
    }


}