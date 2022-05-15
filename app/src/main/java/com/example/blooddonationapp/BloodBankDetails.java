package com.example.blooddonationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.blooddonationapp.Model.BloodBank;
import com.example.blooddonationapp.Model.BloodFill;
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
    BloodFilladapter bloodFilladapter;

    LinearLayout forOwner, forUsers;
    RecyclerView bloodFillOwner, bloodFillOther;
    ArrayList<BloodFill> bloodList = new ArrayList<>();

    ImageView menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_bank_details);

        rootRef = FirebaseDatabaseInstance.getInstance();
        pref = SharedPreference.getInstance();
        currentUserId = pref.getData(SharedPreference.currentUserId, getApplicationContext());
        bankId = getIntent().getStringExtra("bankId");
        fields();

        //for Owner
        LinearLayoutManager linearLayoutManagerForOwner = new LinearLayoutManager(getApplicationContext());
        bloodFillOwner.setLayoutManager(linearLayoutManagerForOwner);
        bloodFillOwner.setHasFixedSize(true);

        forOwner.setVisibility(View.VISIBLE);
        populateOwners();

        if(bankId.equals(currentUserId)){
            forOwner.setVisibility(View.VISIBLE);
            populateOwners();
        }else {
            forUsers.setVisibility(View.VISIBLE);
            populateUsers();
        }

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options2[] = {"Requests"};
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                builder.setItems(options2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(BloodBankDetails.this, ViewBloodRequests.class);
                        intent.putExtra("bankId", bankId);
                        if(currentUserId.equals(bankId)){
                            intent.putExtra("owner", "yes");
                        }
                        else {
                            intent.putExtra("owner", "no");
                        }
                        startActivity(intent);
                        //submit.setVisibility(View.VISIBLE);
                    }
                });
                builder.show();
            }
        });



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

    private void    populateOwners() {
        bloodList.clear();
        bloodFilladapter = new BloodFilladapter(getApplicationContext(), bloodList, bankId);
        bloodFillOwner.setAdapter(bloodFilladapter);
        rootRef.getBankRef().child(bankId).child("FacilityDetails").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot snap : snapshot.getChildren()){
                        BloodFill bloodFill = snap.getValue(BloodFill.class);
                        bloodList.add(bloodFill);
                        //bloodFilladapter.notifyDataSetChanged();
                    }bloodFilladapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        rootRef.getBankRef().child(currentUserId).child("details").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //oOther.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fields() {

        menu = findViewById(R.id.menu_blood);

        //checkbox
        plasma = findViewById(R.id.one);
        platelet = findViewById(R.id.two);
        power = findViewById(R.id.three);
        wholeBlood = findViewById(R.id.four);

        //edit texts


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

        bloodFillOwner = findViewById(R.id.blood_fill);

        //button
        edit = findViewById(R.id.edit_button);
    }
}