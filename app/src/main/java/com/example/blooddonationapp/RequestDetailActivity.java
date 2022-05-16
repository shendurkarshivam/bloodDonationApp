package com.example.blooddonationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blooddonationapp.Model.AmbulanceProvider;
import com.example.blooddonationapp.Model.AmbulanceRequests;
import com.example.blooddonationapp.Model.BloodBank;
import com.example.blooddonationapp.Model.Doctor;
import com.example.blooddonationapp.Model.TripDetails;
import com.example.blooddonationapp.Model.User;
import com.example.blooddonationapp.Notifications.Notification;
import com.example.blooddonationapp.Utils.DateAndTime;
import com.example.blooddonationapp.Utils.FirebaseDatabaseInstance;
import com.example.blooddonationapp.Utils.SharedPreference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RequestDetailActivity extends AppCompatActivity {

    String reqId;
    SharedPreference pref;
    FirebaseDatabaseInstance rootRef;
    String latitude="", longitude="";
    double lat, longi;
    AmbulanceRequests ambulanceRequests;
    TextView requestBy, requestDetails, addressField, dateTime;
    Button accept;
    String reqType;
    BloodBank bloodBank;
    User user;
    Doctor doctor;
    AmbulanceProvider ambulanceProvider;
    String number="";
    String personId;
    TextView openInMap;
    String currentUserId;
    TripDetails tripDetails;
    String driverLatitude="", driverLongitude="";
    long requestTimestamp;
    LocationManager locationManager;


    String currentUserType;


    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(
                RequestDetailActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                RequestDetailActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            Log.i("---locif---", "");
        } else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                double lat = locationGPS.getLatitude();
                double longi = locationGPS.getLongitude();
                driverLatitude = String.valueOf(lat);
                driverLongitude = String.valueOf(longi);
                Log.i("---locelse---", "");
                Log.i("-----loc----", latitude+"---"+longitude);

            } else {
                Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();

            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detail);

        reqId = getIntent().getStringExtra("reqId");

        pref = SharedPreference.getInstance();
        rootRef = FirebaseDatabaseInstance.getInstance();
        currentUserId = pref.getData(SharedPreference.currentUserId, getApplicationContext());
        fields();

        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);



        currentUserType = pref.getData(SharedPreference.userType, getApplicationContext());


        if(!currentUserType.equals("Ambulance Provider")){
            accept.setVisibility(View.GONE);
        }

        rootRef.getTripDetailsRef().child(reqId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    accept.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!personId.equals(currentUserId)){
                    long timeStamp = System.currentTimeMillis();

                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        OnGPS();
                    } else {
                        getLocation();
                    }

                    tripDetails = new TripDetails(reqId, personId,
                            currentUserId, latitude, longitude,
                            driverLatitude, driverLongitude, reqType,
                            "", "",
                            "DriverAccepted", timeStamp, requestTimestamp, ambulanceRequests.getDetails());

                    rootRef.getTripDetailsRef().child(reqId).setValue(tripDetails); // trip details node me pura storing
                    rootRef.getMyTripRef().child(personId).child(reqId).setValue(""); // my trip refs me consumer id k aage trip ki id lagana
                    rootRef.getAmbRef().child(currentUserId).child("MyTrips").child(reqId).setValue(""); // ambulance driver k node me uske trips add krna
                    rootRef.getMyRequestRef().child(personId).child(reqId).removeValue(); // my request ref me se person k aaage se uski request hatana after acceptance
                    rootRef.getRequestRef().child(reqId).removeValue(); // all requests me se puri request delete krna

                    Notification.sendPersonalNotifiaction(currentUserId, personId, "Your Ambulance Request is Accepted", "Ambulance Request", "amb_acc", reqId);

                    Intent intent = new Intent(RequestDetailActivity.this, TripDetailsActivity.class);
                    intent.putExtra("tripId", reqId);
                    startActivity(intent);
                    finish();

                }else {
                    Toast.makeText(getApplicationContext(), "Invalid Action", Toast.LENGTH_LONG).show();
                }

            }
        });

        openInMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri navigation = Uri.parse("google.navigation:q="+latitude+","+longitude+"");
                Intent navigationIntent = new Intent(Intent.ACTION_VIEW, navigation);
                navigationIntent.setPackage("com.google.android.apps.maps");
                startActivity(navigationIntent);
            }
        });
        rootRef.getRequestRef().child(reqId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    ambulanceRequests = snapshot.getValue(AmbulanceRequests.class);
                    lat = Double.parseDouble(ambulanceRequests.getLatitude());
                    longi = Double.parseDouble(ambulanceRequests.getLongitude());
                    latitude = ambulanceRequests.getLatitude();
                    longitude = ambulanceRequests.getLongitude();
                    reqType = ambulanceRequests.getUserType();
                    personId = ambulanceRequests.getPersonId();
                    Log.i("req----", reqType);
                    requestDetails.setText(ambulanceRequests.getDetails());
                    requestTimestamp = ambulanceRequests.getTimestamp();
                    loadGeoAddress();

                    String date = DateAndTime.getDate(ambulanceRequests.getTimestamp());
                    dateTime.setText(Html.fromHtml("<b>Requested At: </b>"+date));

                    if(reqType.equals("Blood Bank")){
                        rootRef.getBankRef().child(personId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    bloodBank = snapshot.getValue(BloodBank.class);
                                    //writeName(bloodBank.getName(), holder);
                                    requestBy.setText(bloodBank.getName());
                                    number = bloodBank.getNumber();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }else if(reqType.equals("Ambulance Provider")){
                        rootRef.getAmbRef().child(personId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    Log.i("ambu----", "reached");
                                    ambulanceProvider = snapshot.getValue(AmbulanceProvider.class);
                                    requestBy.setText(ambulanceProvider.getName());
                                    //writeName(ambulanceProvider.getName(), holder);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }else if(reqType.equals("User")){
                        rootRef.getUserRef().child(personId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    user = snapshot.getValue(User.class);
                                    requestBy.setText(user.getName());
                                    //writeName(user.getName(), holder);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }else{
                        rootRef.getDocRef().child(personId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    doctor = snapshot.getValue(Doctor.class);
                                    requestBy.setText(doctor.getName());
                                    //writeName(doctor.getName(), holder);
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

    private void loadGeoAddress() {
        Geocoder geocoder;
        List<Address> addresses = new ArrayList<>();
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, longi, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();

            String totalAddr = address+", "+city+", "+state+", "+country+", "+postalCode;
            addressField.setText(totalAddr);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void fields() {
        requestBy = findViewById(R.id.req_from);
        requestDetails = findViewById(R.id.req_detail);
        addressField = findViewById(R.id.req_addr);
        accept = findViewById(R.id.accept);
        openInMap = findViewById(R.id.open_in_map);
        dateTime = findViewById(R.id.date_time);
    }
}