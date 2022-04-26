package com.example.blooddonationapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.blooddonationapp.StartFragments.BloodBankFragment;
import com.example.blooddonationapp.StartFragments.DoctorFragment;
import com.example.blooddonationapp.StartFragments.DonorFragment;
import com.example.blooddonationapp.StartFragments.PredictionFragment;
import com.example.blooddonationapp.Utils.SharedPreference;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class StartActivity extends AppCompatActivity implements LocationListener {

    SharedPreference pref;
    LocationManager locationManager;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    DonorFragment donorFragment;
    BloodBankFragment bloodBankFragment;
    DoctorFragment doctorFragment;
    PredictionFragment predictionFragment;
    LinearLayout ambulanceCall;
    String userType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        pref = SharedPreference.getInstance();
        ambulanceCall = findViewById(R.id.ambulance_call_layout);

        ambulanceCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this, ViewAllAmbulance.class));
            }
        });
        Log.i("----------------------", pref.getData(SharedPreference.userType, getApplicationContext()));
        setTabLayout();

    }

    private void setTabLayout() {
        donorFragment = new DonorFragment();
        bloodBankFragment = new BloodBankFragment();
        doctorFragment = new DoctorFragment();
        predictionFragment = new PredictionFragment();

        viewPager = findViewById(R.id.viewPagerStart);
        tabLayout = findViewById(R.id.tabs);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(donorFragment, "Donors");
        viewPagerAdapter.addFragment(bloodBankFragment, "Banks");
        viewPagerAdapter.addFragment(doctorFragment, "Queries");
        viewPagerAdapter.addFragment(predictionFragment, "Predict");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).select();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Toast.makeText(this, location.getLatitude()+" "+location.getLongitude(), Toast.LENGTH_LONG).show();
        pref.saveData(SharedPreference.isLocationProvided, "yes", getApplicationContext());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(StartActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(StartActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
            pref.saveData(SharedPreference.permissionOfLocation, "yes", getApplicationContext());
        }

    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, StartActivity.this);
    }
    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        List<Fragment> fragments = new ArrayList<>();
        List<String> fragmentTitle = new ArrayList<>();

        ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentTitle.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitle.get(position);
        }
    }
}