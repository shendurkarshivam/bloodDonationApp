package com.example.blooddonationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.blooddonationapp.Model.TripDetails;
import com.example.blooddonationapp.Utils.FirebaseDatabaseInstance;
import com.example.blooddonationapp.Utils.SharedPreference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AllTripsByMeAsDriver extends AppCompatActivity {

    FirebaseDatabaseInstance rootRef;
    SharedPreference pref;
    String currentUserId;
    RecyclerView rec;

    ArrayList<TripDetails> allTrips = new ArrayList<>();

    AllTripAdapter allTripAdapter;
    String currentUserType;;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_trips);

        rootRef = FirebaseDatabaseInstance.getInstance();
        pref = SharedPreference.getInstance();

        currentUserId = pref.getData(SharedPreference.currentUserId, getApplicationContext());
        currentUserType = pref.getData(SharedPreference.userType, getApplicationContext());

        rec = findViewById(R.id.driver_my_trip);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());

        rec.setHasFixedSize(true);

        rec.setLayoutManager(linearLayoutManager);

        if(currentUserType.equals("Ambulance Provider")){

            populateMyTrips("");

        }
        else {
            populateMyTripsAsPassenger("");
        }



    }

    private void populateMyTripsAsPassenger(String s) {
        allTrips.clear();
        allTripAdapter = new AllTripAdapter(getApplicationContext(), allTrips);
        rec.setAdapter(allTripAdapter);

        rootRef.getMyTripRef().child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot snap: snapshot.getChildren()){
                        String tripString = (snap.getKey());
                        rootRef.getTripDetailsRef().child(tripString).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    allTrips.add(snapshot.getValue(TripDetails.class));
                                    allTripAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void populateMyTrips(String s) {
        allTrips.clear();
        allTripAdapter = new AllTripAdapter(getApplicationContext(), allTrips);
        rec.setAdapter(allTripAdapter);
        rootRef.getAmbRef().child(currentUserId).child("MyTrips").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot snap : snapshot.getChildren()){
                        String tripString = (snap.getKey());
                        rootRef.getTripDetailsRef().child(tripString).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    allTrips.add(snapshot.getValue(TripDetails.class));
                                    allTripAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}