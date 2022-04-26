package com.example.blooddonationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.blooddonationapp.Model.AmbulanceRequests;
import com.example.blooddonationapp.Utils.FirebaseDatabaseInstance;
import com.example.blooddonationapp.Utils.SharedPreference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CurrentRequests extends AppCompatActivity {
    FirebaseDatabaseInstance rootRef;
    SharedPreference pref;
    RequestAdapter requestAdapter;
    RecyclerView requestView;
    ArrayList<AmbulanceRequests> requestList = new ArrayList<>();

    String userType, currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_requests);
        rootRef = FirebaseDatabaseInstance.getInstance();
        pref = SharedPreference.getInstance();
        requestView = findViewById(R.id.request_rec);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());

        requestView.setHasFixedSize(true);
        requestView.setLayoutManager(linearLayoutManager);

        userType = pref.getData(SharedPreference.userType, getApplicationContext());
        currentUserId = pref.getData(SharedPreference.currentUserId, getApplicationContext());

        if(userType.equals("Ambulance Provider")){
            populateRequests("");
        }else {
            if(userType.equals("User")){
                populateConsumerRequests("", "Users");
            }else if(userType.equals("Doctor")){
                populateConsumerRequests("", "Doctors");
            }else{
                populateConsumerRequests("", "BloodBanks");
            }
        }



    }

    private void populateConsumerRequests(String s, String key) {
        requestList.clear();
        requestAdapter = new RequestAdapter(getApplicationContext(), requestList);

        rootRef.getMyRequestRef().child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot snap : snapshot.getChildren()){
                        rootRef.getRequestRef().child(snap.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    AmbulanceRequests ambulanceRequests = snapshot.getValue(AmbulanceRequests.class);
                                    requestList.add(ambulanceRequests);
                                }
                                requestView.setAdapter(requestAdapter);
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

    private void populateRequests(String s) {
        requestList.clear();
        requestAdapter = new RequestAdapter(getApplicationContext(), requestList);
        rootRef.getRequestRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot snap: snapshot.getChildren()){
                        AmbulanceRequests ambulanceRequests = snap.getValue(AmbulanceRequests.class);
                        requestList.add(ambulanceRequests);
                    }
                    requestView.setAdapter(requestAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}