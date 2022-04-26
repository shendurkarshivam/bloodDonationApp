package com.example.blooddonationapp.Utils;

import com.example.blooddonationapp.ChatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Deque;

public class FirebaseDatabaseInstance {
    private static FirebaseDatabaseInstance single_instance = null;

    DatabaseReference rootRef = FirebaseDatabase.getInstance("https://bloodbankapp-8cd3f-default-rtdb.firebaseio.com/").getReference();
    public static FirebaseDatabaseInstance getInstance() {
        if (single_instance == null)
            single_instance = new FirebaseDatabaseInstance();
        return single_instance;
    }
    public DatabaseReference getRootRef(){
        return rootRef.getRef();
    }

    public DatabaseReference getUserRef() {
        return rootRef.child("Users");
    }

    public DatabaseReference getNumbersRef() {
        return rootRef.child("Numbers");
    }

    public DatabaseReference getBankRef() {
        return rootRef.child("BloodBanks");
    }

    public DatabaseReference getDocRef() {
        return rootRef.child("Doctors");
    }

    public DatabaseReference getMessageRef() {
        return rootRef.child("Messages");
    }

    public DatabaseReference getMessageList() {
        return rootRef.child("MessageList");
    }

    public DatabaseReference getAmbRef() {
        return rootRef.child("AmbulanceProvider");
    }

    public DatabaseReference getRequestRef() {
        return rootRef.child("Requests");
    }

    public DatabaseReference getMyTripRef() {
        return rootRef.child("MyTripList");
    }

    public DatabaseReference getTripDetailsRef() {
        return rootRef.child("TripDetails");
    }

    public DatabaseReference getMyRequestRef() {
        return rootRef.child("MyRequests");
    }
}
