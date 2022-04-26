package com.example.blooddonationapp.StartFragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.blooddonationapp.AllDoctorList;
import com.example.blooddonationapp.ChatActivity;
import com.example.blooddonationapp.DoctorAdapter;
import com.example.blooddonationapp.MessageAdapter;
import com.example.blooddonationapp.Model.Doctor;
import com.example.blooddonationapp.Model.Queries;
import com.example.blooddonationapp.Model.User;
import com.example.blooddonationapp.R;
import com.example.blooddonationapp.Utils.FirebaseDatabaseInstance;
import com.example.blooddonationapp.Utils.SharedPreference;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DoctorFragment extends Fragment {

    View view;
    FirebaseDatabaseInstance rootRef;
    RecyclerView donorList;
    ArrayList<Doctor> list = new ArrayList<>();
    DoctorAdapter donorAdapter;
    String currentUserId, userType;
    SharedPreference pref;
    TextView getAllDocs;

    EditText inputMessage;
    ImageButton sendMessage;

    ArrayList<Queries> queries = new ArrayList<>();

    MessageAdapter messageAdapter;


    public DoctorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        pref = SharedPreference.getInstance();
        currentUserId = pref.getData(SharedPreference.currentUserId, getContext());
        view = inflater.inflate(R.layout.fragment_doctor, container, false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rootRef = FirebaseDatabaseInstance.getInstance();
        donorList = view.findViewById(R.id.donor_list);
        getAllDocs = view.findViewById(R.id.get_all_docs);
        donorList.setHasFixedSize(true);
        donorList.setLayoutManager(linearLayoutManager);
        userType = pref.getData(SharedPreference.userType, getContext());
        if(!userType.equals("Doctor")){
            //populateDoctorListForUsers("");
        }

        inputMessage = view.findViewById(R.id.input_message);
        sendMessage = view.findViewById(R.id.send_message_btn);

        sendMessage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(!inputMessage.getText().toString().equals("")){
                    String msg = inputMessage.getText().toString();
                    inputMessage.setText("");
                    String id = rootRef.getMessageRef().push().getKey();

                    Queries q1 = new Queries("all", currentUserId, msg, id, userType, System.currentTimeMillis());
                    rootRef.getMessageRef().child(id).setValue(q1);
                }else{

                }

            }
        });

        populateQueries();


        getAllDocs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AllDoctorList.class));
            }
        });
        return view;
    }

    private void populateQueries() {
        queries.clear();
        messageAdapter = new MessageAdapter(getContext(), queries);
        donorList.setAdapter(messageAdapter);
        rootRef.getMessageRef().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    Queries q = snapshot.getValue(Queries.class);
                    queries.add(q);

                    messageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void populateDoctorListForUsers(String s) {
        list.clear();
        donorAdapter = new DoctorAdapter(getContext(), list);

        rootRef.getUserRef().child(currentUserId).child("DoctorChat").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot snap: snapshot.getChildren()){
                        rootRef.getDocRef().child(snap.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    Doctor user = snapshot.getValue(Doctor.class);
                                    list.add(user);
                                    donorAdapter.notifyDataSetChanged();
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
        donorList.setAdapter(donorAdapter);
    }
}
