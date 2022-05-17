package com.example.blooddonationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.blooddonationapp.Model.TripDetails;
import com.example.blooddonationapp.Utils.DateAndTime;
import com.example.blooddonationapp.Utils.FirebaseDatabaseInstance;
import com.example.blooddonationapp.Utils.SharedPreference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class TripDetailsActivity extends AppCompatActivity {

    String tripId;
    String currentUserId;
    String currentUserType;

    FirebaseDatabaseInstance rootRef;
    SharedPreference pref;

    TripDetails tripDetails;

    //DRIVER DETAILS

    TextView driverName, driverNumber, driverFacilities;

    //CONSUMER DETAILS

    TextView consumerName, consumerPhone, status;

    //TIME DETAILS

    TextView reqTime, accTime, dropTime;

    //ONLY FOR DRIVER

    LinearLayout onlyForDriver;
        // OPEN MAP
        TextView openInMap;
        // STATUS CHANGE
        TextView statusChangeText;
        ImageView statusDrop;
    private String latitude="", longitude="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        rootRef = FirebaseDatabaseInstance.getInstance();
        pref = SharedPreference.getInstance();

        currentUserId = pref.getData(SharedPreference.currentUserId, getApplicationContext());
        currentUserType = pref.getData(SharedPreference.userType, getApplicationContext());

        tripId = getIntent().getStringExtra("tripId");

        fields();
        getTripDetails();

        openInMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri navigation = Uri.parse("google.navigation:q="+latitude+","+longitude+"");
                Intent navigationIntent = new Intent(Intent.ACTION_VIEW, navigation);
                navigationIntent.setPackage("com.google.android.apps.maps");
                startActivity(navigationIntent);
            }
        });


        statusDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options2[] = {"Driver Accepted", "Picked-Up", "Dropped"};
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                builder.setItems(options2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        rootRef.getTripDetailsRef().child(tripDetails.getTripId()).child("tripStatus").setValue(options2[i].toString());
                        //submit.setVisibility(View.VISIBLE);
                    }
                });
                builder.show();
            }
        });

    }

    private void fields() {
        // DRIVER DETAILS
        driverName = findViewById(R.id.driver_name);
        driverNumber = findViewById(R.id.driver_number);
        driverFacilities = findViewById(R.id.driver_facilities);

        // CONSUMER DETAILS
        consumerName = findViewById(R.id.consumer_name);
        consumerPhone = findViewById(R.id.consumer_number);
        status = findViewById(R.id.status);

        //TIME DETAILS
        reqTime = findViewById(R.id.date_time_req);
        accTime = findViewById(R.id.date_time_acc);
        dropTime = findViewById(R.id.date_time_drop);

        // ONLY FOR DRIVER
        onlyForDriver = findViewById(R.id.only_for_driver);
            // OPEN IN MAP
            openInMap = findViewById(R.id.open_in_map);

            // CHANGE STATUS
            statusChangeText = findViewById(R.id.status_change_text);
            statusDrop = findViewById(R.id.status_drop);

    }

    private void getTripDetails() {
        rootRef.getTripDetailsRef().child(tripId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    tripDetails = snapshot.getValue(TripDetails.class);
                    latitude = tripDetails.getConsumerStartLatitude();
                    longitude = tripDetails.getConsumerStartLongitude();
                    status.setText(Html.fromHtml("<b>Status: </b>"+tripDetails.getTripStatus()));
                    statusChangeText.setText(Html.fromHtml("<b>Status: </b>"+tripDetails.getTripStatus()));

                    reqTime.setText(Html.fromHtml("<b>Request At: </b>" + DateAndTime.getDate(tripDetails.getRequestTimeStamp())));
                    accTime.setText(Html.fromHtml("<b>Accepted At: </b>" + DateAndTime.getDate(tripDetails.getAcceptanceTimeStamp())));

                    if(currentUserType.equals("User")){
                        // consumer has opened the app
                        fillDriverDetails();
                        Log.i("----------------------", "124");
                        fillConsumerDetails("Users");
                    }else if(currentUserType.equals("Ambulance Provider")){
                        fillDriverDetails();
                        if(currentUserId.equals(tripDetails.getDriverId())){
                            // driver who is delivering has opened the app
                            if(tripDetails.getConsumerType().equals("User")){
                                fillConsumerDetails("Users");
                                Log.i("----------------------", "132");

                            }else if(tripDetails.getConsumerType().equals("Blood Bank")){
                                Log.i("----------------------", "138");

                                fillConsumerDetails("BloodBanks");
                            }else if(tripDetails.getConsumerType().equals("Doctor")){
                                fillConsumerDetails("Doctors");
                                Log.i("----------------------", "140");

                            }
                            onlyForDriver.setVisibility(View.VISIBLE);

                        }else{
                            // driver has opened the app as consumer
                            //fillDriverDetails();

                            fillConsumerDetails("AmbulanceProvider");
                            Log.i("----------------------", "150");

                        }

                    }else if(currentUserType.equals("Doctor")){
                        // consumer has opened the app
                        fillDriverDetails();
                        fillConsumerDetails("Doctors");
                        Log.i("----------------------", "158");

                    }else {
                        //for blood banks                         // consumer has opened the app
                        fillDriverDetails();
                        fillConsumerDetails("BloodBanks");
                        Log.i("----------------------", "164");

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fillConsumerDetails(String user) {
        rootRef.getRootRef().child(user).child(tripDetails.getConsumerId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                consumerName.setText(Html.fromHtml("<b>Name: </b>"+snapshot.child("name").getValue().toString()));
                consumerPhone.setText(Html.fromHtml("<b>Number: </b>"+snapshot.child("number").getValue().toString()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fillDriverDetails() {
        rootRef.getAmbRef().child(tripDetails.getDriverId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                driverName.setText(Html.fromHtml("<b>Name: </b>"+snapshot.child("name").getValue().toString()));
                driverNumber.setText(Html.fromHtml("<b>Number: </b>"+snapshot.child("number").getValue().toString()));
                driverFacilities.setText(Html.fromHtml("<b>Facilities: </b>"+snapshot.child("ambulanceFacilities").getValue().toString()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}