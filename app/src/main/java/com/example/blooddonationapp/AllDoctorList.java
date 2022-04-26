package com.example.blooddonationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.blooddonationapp.Model.Doctor;
import com.example.blooddonationapp.Utils.FirebaseDatabaseInstance;
import com.example.blooddonationapp.Utils.SharedPreference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AllDoctorList extends AppCompatActivity {
    FirebaseDatabaseInstance rootRef;
    RecyclerView donorList;
    ArrayList<Doctor> list = new ArrayList<>();
    DoctorAdapter donorAdapter;
    String currentUserId;
    SharedPreference pref;
    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_doctor_list);
        pref = SharedPreference.getInstance();
        currentUserId = pref.getData(SharedPreference.currentUserId, getApplicationContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rootRef = FirebaseDatabaseInstance.getInstance();
        donorList = findViewById(R.id.donor_list);
        donorList.setHasFixedSize(true);
        donorList.setLayoutManager(linearLayoutManager);

        backButton = findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        populateDonorList("");


    }
    private void populateDonorList(String s) {
        list.clear();
        donorAdapter = new DoctorAdapter(getApplicationContext(), list);

        rootRef.getDocRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot snap : snapshot.getChildren()){
                        Doctor user = snap.getValue(Doctor.class);
                        if(user.getName().contains(s)
                                && !user.getId().equals(currentUserId)){
                            list.add(user);
                            donorAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        donorList.setAdapter(donorAdapter);
    }
}