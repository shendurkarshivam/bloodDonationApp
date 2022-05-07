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
import com.example.blooddonationapp.Utils.FirebaseDatabaseInstance;
import com.example.blooddonationapp.Utils.SharedPreference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

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
    String currentUserId;
    FirebaseDatabaseInstance rootRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        pref = SharedPreference.getInstance();
        currentUserId = pref.getData(SharedPreference.currentUserId, getApplicationContext());
        rootRef = FirebaseDatabaseInstance.getInstance();
        ambulanceCall = findViewById(R.id.ambulance_call_layout);

        ambulanceCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this, ViewAllAmbulance.class));
            }
        });
        Log.i("----------------------", pref.getData(SharedPreference.userType, getApplicationContext()));
        setTabLayout();
        checkAndGenerateToken();

    }

    private void checkAndGenerateToken() {
        rootRef.getTokenRef().child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            String token=task.getResult();
                            Log.i("token---", token);

                            rootRef.getTokenRef().child(currentUserId).setValue(token);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


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