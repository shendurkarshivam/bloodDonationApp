package com.example.blooddonationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.blooddonationapp.Model.BloodRequests;
import com.example.blooddonationapp.Utils.FirebaseDatabaseInstance;
import com.example.blooddonationapp.Utils.SharedPreference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewBloodRequests extends AppCompatActivity {

    RecyclerView bloodReq;
    FirebaseDatabaseInstance rootRef;
    SharedPreference pref;
    String bankId, ownerOrNot;
    String currentUserId;
    BloodRequestAdapter bloodRequestAdapter;
    ArrayList<BloodRequests> bloodRequestList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_blood_requests);

        bankId = getIntent().getStringExtra("bankId");
        ownerOrNot = getIntent().getStringExtra("owner");

        rootRef = FirebaseDatabaseInstance.getInstance();
        pref = SharedPreference.getInstance();
        currentUserId = pref.getData(SharedPreference.currentUserId, getApplicationContext());

        bloodReq = findViewById(R.id.req_blood);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());

        bloodReq.setHasFixedSize(true);

        bloodReq.setLayoutManager(linearLayoutManager);

        bloodRequestList.clear();
        bloodRequestAdapter = new BloodRequestAdapter(getApplicationContext(), bloodRequestList);
        bloodReq.setAdapter(bloodRequestAdapter);

        if(ownerOrNot.equals("yes")){
            populateOwner();
        }else {
            populateUser();
        }


    }

    private void populateUser() {
        rootRef.getBankRef().child(bankId).child("Requests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot snap : snapshot.getChildren()){
                        BloodRequests bloodRequests = snap.getValue(BloodRequests.class);
                        if(bloodRequests.getRequesterId().equals(currentUserId)){
                            bloodRequestList.add(bloodRequests);
                            bloodRequestAdapter.notifyDataSetChanged();
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void populateOwner() {
        rootRef.getBankRef().child(bankId).child("Requests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot snap : snapshot.getChildren()){
                        BloodRequests bloodRequests = snap.getValue(BloodRequests.class);
                        bloodRequestList.add(bloodRequests);
                        bloodRequestAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}