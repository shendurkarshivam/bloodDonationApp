package com.example.blooddonationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

public class BloodBankDetails extends AppCompatActivity {
    FirebaseDatabaseInstance rootRef;
    SharedPreference pref;
    CheckBox plasma, platelet, power, wholeBlood;
    EditText ePlasma, ePlatelet, ePower, eWholeBlood, oOther;
    Button edit;
    TextView pl, pla, pow, wh, other;
    TextView number, address;
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
    }

    private void getTheValue(String key, TextView pl) {
        rootRef.getBankRef().child(bankId).child("FacilityDetails").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pl.setText(snapshot.child(key).getValue().toString());
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

        //textview
        pl = findViewById(R.id.o);
        pla = findViewById(R.id.t);
        pow = findViewById(R.id.th);
        wh = findViewById(R.id.f);
        other = findViewById(R.id.five);

        //details fields
        address = findViewById(R.id.address_bb);
        number = findViewById(R.id.number_bb);

        //linearlayout

        forOwner = findViewById(R.id.current);
        forUsers = findViewById(R.id.post);

        //button
        edit = findViewById(R.id.edit_button);
    }
}