package com.example.blooddonationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.blooddonationapp.Model.BloodBank;
import com.example.blooddonationapp.Utils.FirebaseDatabaseInstance;
import com.example.blooddonationapp.Utils.SharedPreference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BloodBankDetails extends AppCompatActivity {
    FirebaseDatabaseInstance rootRef;
    SharedPreference pref;
    CheckBox plasma, platelet, power, wholeBlood;
    EditText ePlasma, ePlatelet, ePower, eWholeBlood, oOther;
    Button edit;
    TextView nameOfBB;
    TextView pl, pla, pow, wh, other;
    TextView number, addressOfBB;
    TextView openMap;
    String currentUserId;
    String bankId;

    BloodBank bloodBank;

    LinearLayout forOwner, forUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_bank_details);

        rootRef = FirebaseDatabaseInstance.getInstance();
        pref = SharedPreference.getInstance();
        currentUserId = pref.getData(SharedPreference.currentUserId, getApplicationContext());
        bankId = getIntent().getStringExtra("bankId");
        fields();

        if(bankId.equals(currentUserId)){
            forOwner.setVisibility(View.VISIBLE);
            populateOwners();
        }else {
            forUsers.setVisibility(View.VISIBLE);
            populateUsers();
        }

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(plasma.isChecked()){

                    rootRef.getBankRef().child(currentUserId).child("Facilities").child("Plasma Donation").setValue(true);
                }else {
                    rootRef.getBankRef().child(currentUserId).child("Facilities").child("Plasma Donation").setValue(false);
                }
                if(platelet.isChecked()){

                    rootRef.getBankRef().child(currentUserId).child("Facilities").child("Platelet Donation").setValue(true);
                }else {
                    rootRef.getBankRef().child(currentUserId).child("Facilities").child("Platelet Donation").setValue(false);
                }

                if(power.isChecked()){

                    rootRef.getBankRef().child(currentUserId).child("Facilities").child("Power Red Donation").setValue(true);
                }
                else {
                    rootRef.getBankRef().child(currentUserId).child("Facilities").child("Power Red Donation").setValue(false);
                }
                if(wholeBlood.isChecked()){

                    rootRef.getBankRef().child(currentUserId).child("Facilities").child("Whole Blood Donation").setValue(true);
                }else {
                    rootRef.getBankRef().child(currentUserId).child("Facilities").child("Whole Blood Donation").setValue(false);
                }
                rootRef.getBankRef().child(currentUserId).child("details").setValue(oOther.getText().toString());

                rootRef.getBankRef().child(currentUserId).child("FacilityDetails").child("Plasma Donation").setValue(ePlasma.getText().toString());
                rootRef.getBankRef().child(currentUserId).child("FacilityDetails").child("Platelet Donation").setValue(ePlatelet.getText().toString());
                rootRef.getBankRef().child(currentUserId).child("FacilityDetails").child("Power Red Donation").setValue(ePower.getText().toString());
                rootRef.getBankRef().child(currentUserId).child("FacilityDetails").child("Whole Blood Donation").setValue(eWholeBlood.getText().toString());

                finish();
            }


        });



    }

    private void populateUsers() {
        rootRef.getBankRef().child(bankId).child("Facilities").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("Plasma Donation").getValue().toString().equals("true")){
                        pl.setVisibility(View.VISIBLE);
                        getTheValue("Plasma Donation", pl);
                    }if(snapshot.child("Platelet Donation").getValue().toString().equals("true")){
                        pla.setVisibility(View.VISIBLE);
                        getTheValue("Platelet Donation", pla);
                    }if(snapshot.child("Power Red Donation").getValue().toString().equals("true")){
                        pow.setVisibility(View.VISIBLE);
                        getTheValue("Power Red Donation", pow);
                    }if(snapshot.child("Whole Blood Donation").getValue().toString().equals("true")){
                        wh.setVisibility(View.VISIBLE);
                        getTheValue("Whole Blood Donation", wh);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        rootRef.getBankRef().child(bankId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bloodBank = snapshot.getValue(BloodBank.class);
                nameOfBB.setText(bloodBank.getName());
                number.setText(Html.fromHtml("<b>Number: </b>"+snapshot.child("number").getValue().toString()));
                double lat = Double.parseDouble(bloodBank.getLatitude());
                double longi = Double.parseDouble(bloodBank.getLongitude());

                Geocoder geocoder;
                List<Address> addresses = new ArrayList<>();
                geocoder = new Geocoder(BloodBankDetails.this, Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(lat, longi, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();

                    String totalAddr = address+", "+city+", "+state+", "+country+", "+postalCode;
                    addressOfBB.setText(Html.fromHtml("<b>Address: </b>"+totalAddr));

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        openMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri navigation = Uri.parse("google.navigation:q="+bloodBank.getLatitude()+","+bloodBank.getLongitude()+"");
                Intent navigationIntent = new Intent(Intent.ACTION_VIEW, navigation);
                navigationIntent.setPackage("com.google.android.apps.maps");
                startActivity(navigationIntent);
            }
        });
    }

    private void getTheValue(String key, TextView pl) {
        rootRef.getBankRef().child(bankId).child("FacilityDetails").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.child(key).getValue().toString().equals("")){
                    String k = "<b>"+key+": </b>"+ snapshot.child(key).getValue().toString();
                    pl.setText(Html.fromHtml(k));
                }else {
                    pl.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void populateOwners() {
        rootRef.getBankRef().child(currentUserId).child("Facilities").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String one = snapshot.child("Plasma Donation").getValue().toString();

                    if(snapshot.child("Plasma Donation").getValue().toString().trim().equals("true")){
                        plasma.setChecked(true);
                    }if(snapshot.child("Platelet Donation").getValue().toString().trim().equals("true")){
                        Log.i("one---", one);
                        platelet.setChecked(true);
                    }if(snapshot.child("Power Red Donation").getValue().toString().trim().equals("true")){
                        power.setChecked(true);
                    }if(snapshot.child("Whole Blood Donation").getValue().toString().trim().equals("true")){
                        wholeBlood.setChecked(true);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        rootRef.getBankRef().child(currentUserId).child("FacilityDetails").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    ePlasma.setText(snapshot.child("Plasma Donation").getValue().toString());
                    ePlatelet.setText(snapshot.child("Platelet Donation").getValue().toString());
                    ePower.setText(snapshot.child("Power Red Donation").getValue().toString());
                    eWholeBlood.setText(snapshot.child("Whole Blood Donation").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        rootRef.getBankRef().child(currentUserId).child("details").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                oOther.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fields() {
        //checkbox
        plasma = findViewById(R.id.one);
        platelet = findViewById(R.id.two);
        power = findViewById(R.id.three);
        wholeBlood = findViewById(R.id.four);

        //edit texts
        ePlasma = findViewById(R.id.e_plasma);
        ePlatelet = findViewById(R.id.e_plate);
        ePower = findViewById(R.id.e_power);
        eWholeBlood = findViewById(R.id.e_whole);
        oOther = findViewById(R.id.e_other);


        nameOfBB = findViewById(R.id.name_of_bb);
        //textview
        pl = findViewById(R.id.o);
        pla = findViewById(R.id.t);
        pow = findViewById(R.id.th);
        wh = findViewById(R.id.f);
        other = findViewById(R.id.five);

        //details fields
        addressOfBB = findViewById(R.id.address_bb);
        number = findViewById(R.id.number_bb);

        openMap = findViewById(R.id.open_in_map);
        //linearlayout

        forOwner = findViewById(R.id.current);
        forUsers = findViewById(R.id.post);

        //button
        edit = findViewById(R.id.edit_button);
    }
}