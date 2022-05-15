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
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blooddonationapp.Model.AmbulanceProvider;
import com.example.blooddonationapp.Model.BloodBank;
import com.example.blooddonationapp.Model.BloodFill;
import com.example.blooddonationapp.Model.Doctor;
import com.example.blooddonationapp.Model.LocationOfUser;
import com.example.blooddonationapp.Model.User;
import com.example.blooddonationapp.Utils.FirebaseDatabaseInstance;
import com.example.blooddonationapp.Utils.SharedPreference;
import com.example.blooddonationapp.Utils.ValidateTextFields;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class SignUpPage extends AppCompatActivity {
    private static final int REQUEST_LOCATION = 1;
    ImageView menu;
    EditText number, password, name, email;
    Button beforePass, afterPass, submit;
    TextView bgText;
    LinearLayout passLayout, detailLayout, bankLayout, docLayout, ambLayout;
    LocationManager locationManager;
    String numberString = "", passwordString = "", nameString = "", emailString = "", bgString = "B+", id;
    String latitude="", longitude="";
    FirebaseDatabaseInstance rootRef;
    SharedPreference pref;
    private FirebaseAuth firebaseAuth;

    private String verificationCode;
    private PhoneAuthProvider.ForceResendingToken token;
    String phoneNumberWithoutSpecialChar;

    TextView userText;
    ImageView userMenu;
    String userTextString;

    //blood bank details
    CheckBox one, two, three, four;
    EditText detailOfBank;
    String detailsOfBankText="";
    HashMap<String, Boolean> facilities = new HashMap<>();

    //doc details

    EditText qualification, organization, license, address;
    String qualificationString="",organizationString = "", licenseString = "", addressText="";

    //ambulance provider

    EditText ambulanceNumber, organizationAmbulance, facilitiesAmbulance;
    String ambulanceNumberString="", organizationAmbulanceString="", facilitiesAmbulanceString="";

    FirebaseAuthSettings firebaseAuthSettings;
    Button btnGetLocation;

    LinearLayout donorLayout;
    CheckBox donorBox;

    void showToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            //Getting the code sent by SMS

            String code = phoneAuthCredential.getSmsCode();

            Log.e("onVerification: ", phoneAuthCredential.getProvider());
            if (code != null) {
                password.setText(code);
                verifyVerificationCode(code);
            } else {
                password.setVisibility(View.INVISIBLE);
                signInWithCredential(phoneAuthCredential);
            }

            // Configure faking the auto-retrieval with the whitelisted numbers.

            showToast("Phone verified automatically");
        }


        @Override
        public void onVerificationFailed(FirebaseException e) {
            //loading_bar.setVisibility(View.VISIBLE);
            afterPass.setVisibility(View.INVISIBLE);
            //resend_otp.setVisibility(View.VISIBLE);
            Log.e("PHONEVERIFY", "----------- error : " + e.getMessage());
            showToast("Please check your INTERNET connection and click RESEND OTP\n" + e.getMessage());

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            //super.onCodeSent(s, forceResendingToken);
            Log.e("this", s);
            //storing the verification id that is sent to the user
            afterPass.setVisibility(View.VISIBLE);
            //loading_bar.setVisibility(View.INVISIBLE);
            //resend_otp.setVisibility(View.VISIBLE);

            showToast("Code Sent");
            verificationCode = s;
            token = forceResendingToken;

            switch (phoneNumberWithoutSpecialChar) {
                case "+917777777777":
                    password.setText("654321");
                    verifyVerificationCode("654321");
                    break;
                case "+919876543210":
                case "+19876543210":
                case "+559876543210":
                case "+499876543210":
                    password.setText("123456");
                    verifyVerificationCode("123456");
                    break;
            }
        }
    };

    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, code);
        //signing the user
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //loading_bar.setVisibility(View.VISIBLE);
                            afterPass.setVisibility(View.INVISIBLE);
                            //resend_otp.setVisibility(View.INVISIBLE);


                            //user verifies mobile number we can store it in shared preferences now!!!
                            updateUi();
                            // setUserToSetProfileActivity();
                            //finish();
                        } else {
                            //loading_bar.setVisibility(View.VISIBLE);
                            //resend_otp.setVisibility(View.VISIBLE);

                            showToast("Please check your INTERNET connection and click RESEND OTP\n" + task.getException().getMessage());

                           /* Toast.makeText(PhoneVerify.this, "Please check your INTERNET connection and click RESEND OTP", Toast.LENGTH_SHORT).show();
                            Toast.makeText(PhoneVerify.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();*/
                        }
                    }
                });
    }

    private void updateUi() {
        rootRef.getNumbersRef().child("+91" + numberString).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    goToMainPageOnlyLogin(snapshot.getValue().toString());
                } else {
                    detailLayout.setVisibility(View.VISIBLE);
                    afterPass.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void goToMainPageOnlyLogin(String type) {
        Log.i("usertype----", type);
        pref.saveData(SharedPreference.signup, "true", getApplicationContext());
        pref.saveData(SharedPreference.number, numberString, getApplicationContext());
        pref.saveData(SharedPreference.userType, type, getApplicationContext());
        if(type.equals("Donor")){
            rootRef.getUserRef().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        for(DataSnapshot snap : snapshot.getChildren()){
                            User user = snap.getValue(User.class);
                            if(user.getNumber().equals(numberString)){
                                pref.saveData(SharedPreference.currentUserId, user.getId(), getApplicationContext());
                                showToast("Reached Main Page");
                                startActivity(new Intent(SignUpPage.this, StartActivity.class));
                                finish();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        else if(type.equals("Blood Bank")){
            rootRef.getBankRef().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        for(DataSnapshot snap : snapshot.getChildren()){
                            BloodBank user = snap.getValue(BloodBank.class);
                            if(user.getNumber().equals(numberString)){
                                pref.saveData(SharedPreference.currentUserId, user.getId(), getApplicationContext());
                                showToast("Reached Main Page");
                                startActivity(new Intent(SignUpPage.this, StartActivity.class));
                                finish();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else if(type.equals("Doctor")){
            rootRef.getDocRef().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        for(DataSnapshot snap : snapshot.getChildren()){
                            Doctor user = snap.getValue(Doctor.class);
                            if(user.getNumber().equals(numberString)){
                                pref.saveData(SharedPreference.currentUserId, user.getId(), getApplicationContext());
                                showToast("Reached Main Page");
                                startActivity(new Intent(SignUpPage.this, StartActivity.class));
                                finish();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else {
            Log.i("amb---", "yes");
            rootRef.getAmbRef().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        for(DataSnapshot snap : snapshot.getChildren()){
                            AmbulanceProvider user = snap.getValue(AmbulanceProvider.class);
                            if(user.getNumber().equals(numberString)){
                                pref.saveData(SharedPreference.currentUserId, user.getId(), getApplicationContext());
                                showToast("Reached Main Page");
                                startActivity(new Intent(SignUpPage.this, StartActivity.class));
                                finish();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        /*showToast("Reached Main Page");
        startActivity(new Intent(SignUpPage.this, StartActivity.class));
        finish();*/
    }

    private void goToMainPage() {
        pref.saveData(SharedPreference.signup, "true", getApplicationContext());
        pref.saveData(SharedPreference.number, numberString, getApplicationContext());
        pref.saveData(SharedPreference.currentUserId, id, getApplicationContext());
        pref.saveData(SharedPreference.userType, userTextString, getApplicationContext());
        showToast("Reached Main Page");
        startActivity(new Intent(SignUpPage.this, StartActivity.class));
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        pref = SharedPreference.getInstance();
        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        fields();
        facilities.put("Whole Blood Donation", false);
        facilities.put("Power Red Donation", false);
        facilities.put("Plasma Donation", false);
        facilities.put("Platelet Donation", false);

        ActivityCompat.requestPermissions( this,
                new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        btnGetLocation = findViewById(R.id.btn_loc);
        btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    OnGPS();
                } else {
                    getLocation();
                }
            }
        });


        rootRef = FirebaseDatabaseInstance.getInstance();

        beforePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(number.getText().toString())) {
                    beforePass.setError("Required");
                    beforePass.requestFocus();
                } else if (number.getText().toString().length() < 10) {
                    beforePass.setError("Invalid Number");
                    beforePass.requestFocus();
                } else {

                    numberString = number.getText().toString();
                    phoneNumberWithoutSpecialChar = numberString.replaceAll("[ -()/]", "");
                    sendVerificationCode("+91" + phoneNumberWithoutSpecialChar);

                    passLayout.setVisibility(View.VISIBLE);
                    beforePass.setVisibility(View.GONE);
                }
            }
        });
        afterPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = password.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    password.setError("Enter valid code");
                    password.requestFocus();
                    return;
                }
                //verifying the code entered manually
                verifyVerificationCode(code);
                passwordString = password.getText().toString();

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bgString = bgText.getText().toString();
                emailString = email.getText().toString();
                nameString = name.getText().toString();
                passwordString = password.getText().toString();
                userTextString = userText.getText().toString();


                detailsOfBankText = detailOfBank.getText().toString();

                qualificationString = qualification.getText().toString();
                organizationString = organization.getText().toString();
                licenseString = license.getText().toString();
                addressText = address.getText().toString();

                ambulanceNumberString = ambulanceNumber.getText().toString();
                organizationAmbulanceString = organizationAmbulance.getText().toString();
                facilitiesAmbulanceString = facilitiesAmbulance.getText().toString();

                Log.i("details---", detailsOfBankText);
                Log.i("usertextstring", userTextString);

                if (!numberString.equals("") &&
                        !passwordString.equals("") &&
                        !bgString.equals("") &&
                        !emailString.equals("") &&
                        !nameString.equals("") &&
                        !userTextString.equals("") &&
                        !latitude.equals("") &&
                        !longitude.equals("")) {

                    if (userTextString.equals("User")) {
                        HashMap<String, String> h = new HashMap<>();
                        String isDonor = "no";
                        if(donorBox.isChecked()){
                            isDonor = "yes";
                        }

                        id = rootRef.getUserRef().push().getKey();
                        h.put("id", id);
                        h.put("name", nameString);
                        h.put("number", numberString);
                        h.put("password", passwordString);
                        h.put("email", emailString);
                        h.put("bg", bgString);
                        h.put("type", userTextString);
                        h.put("latitude", latitude);
                        h.put("longitude", longitude);
                        h.put("isDonor", isDonor);

                        Log.i("number---", numberString);

                        rootRef.getUserRef().child(id).setValue(h).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                rootRef.getNumbersRef().child("+91" + numberString).setValue(userTextString);
                                goToMainPage();
                            }
                        });


                    }else if(userTextString.equals("Blood Bank")&&!detailsOfBankText.equals("")){
                        if(one.isChecked()){
                            facilities.put("Whole Blood Donation", true);
                        }
                        if(two.isChecked()){
                            facilities.put("Power Red Donation", true);
                        }
                        if(three.isChecked()){
                            facilities.put("Plasma Donation", true);
                        }
                        if(four.isChecked()){
                            facilities.put("Platelet Donation", true);
                        }
                        HashMap<String, String> h = new HashMap<>();

                        id = rootRef.getBankRef().push().getKey();
                        h.put("id", id);
                        h.put("name", nameString);
                        h.put("number", numberString);
                        h.put("password", passwordString);
                        h.put("email", emailString);
                        h.put("bg", bgString);
                        h.put("type", userTextString);
                        h.put("latitude", latitude);
                        h.put("longitude", longitude);
                        h.put("details", detailsOfBankText);

                        Log.i("number---", numberString);

                        rootRef.getBankRef().child(id).setValue(h).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                rootRef.getNumbersRef().child("+91" + numberString).setValue(userTextString);
                                rootRef.getBankRef().child(id).child("Facilities").setValue(facilities);

                                HashMap<String, String > hk = new HashMap<>();
                                BloodFill bloodFill = new BloodFill("Plasma Donation", "0", "0",
                                        "0", "0",
                                        "0", "0",
                                        "0", "0");
                                rootRef.getBankRef().child(id).child("FacilityDetails").child("Plasma Donation").setValue(bloodFill);

                                bloodFill = new BloodFill("Platelet Donation", "0", "0",
                                        "0", "0",
                                        "0", "0",
                                        "0", "0");
                                rootRef.getBankRef().child(id).child("FacilityDetails").child("Platelet Donation").setValue(bloodFill);

                                bloodFill = new BloodFill("Power Red Donation", "0", "0",
                                        "0", "0",
                                        "0", "0",
                                        "0", "0");
                                rootRef.getBankRef().child(id).child("FacilityDetails").child("Power Red Donation").setValue(bloodFill);

                                bloodFill = new BloodFill("Whole Blood Donation", "0", "0",
                                        "0", "0",
                                        "0", "0",
                                        "0", "0");
                                rootRef.getBankRef().child(id).child("FacilityDetails").child("Whole Blood Donation").setValue(bloodFill);
                                goToMainPage();
                            }
                        });


                    }else if(userTextString.equals("Doctor")
                            &&ValidateTextFields.field(qualification)
                            &&ValidateTextFields.field(organization)
                            &&ValidateTextFields.field(license)
                            &&ValidateTextFields.field(address)){
                        HashMap<String, String> h = new HashMap<>();

                        id = rootRef.getDocRef().push().getKey();
                        h.put("id", id);
                        h.put("name", nameString);
                        h.put("number", numberString);
                        h.put("password", passwordString);
                        h.put("email", emailString);
                        h.put("bg", bgString);
                        h.put("type", userTextString);
                        h.put("latitude", latitude);
                        h.put("longitude", longitude);
                        h.put("specialization", qualificationString );
                        h.put("organization", organizationString);
                        h.put("license", licenseString);
                        h.put("address", addressText );

                        Log.i("number---", numberString);

                        rootRef.getDocRef().child(id).setValue(h).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                rootRef.getNumbersRef().child("+91" + numberString).setValue(userTextString);
                                goToMainPage();
                            }
                        });

                    } else if(userTextString.equals("Ambulance Provider")
                        && ValidateTextFields.field(ambulanceNumber)
                        && ValidateTextFields.field(organizationAmbulance)
                        && ValidateTextFields.field(facilitiesAmbulance)){

                        HashMap<String, String> h = new HashMap<>();

                        id = rootRef.getAmbRef().push().getKey();
                        h.put("id", id);
                        h.put("name", nameString);
                        h.put("number", numberString);
                        h.put("password", passwordString);
                        h.put("email", emailString);
                        h.put("bg", bgString);
                        h.put("type", userTextString);
                        h.put("latitude", latitude);
                        h.put("longitude", longitude);
                        h.put("ambulanceNumber", ambulanceNumberString );
                        h.put("ambulanceOrganization", organizationAmbulanceString);
                        h.put("ambulanceFacilities", facilitiesAmbulanceString);

                        Log.i("number---", numberString);

                        rootRef.getAmbRef().child(id).setValue(h).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                rootRef.getNumbersRef().child("+91" + numberString).setValue(userTextString);
                                goToMainPage();
                            }
                        });


                    }
                    else {
                        Log.i("something is blank---", "blank");
                        ValidateTextFields.field(name);
                        ValidateTextFields.field(email);

                        ValidateTextFields.field(detailOfBank);


                    }
                }else {
                    Log.i("something is blank---", "blank");
                    if(longitude.equals("")||latitude.equals("")){
                        Toast.makeText(getApplicationContext(), "Please Provide Location Access", Toast.LENGTH_LONG).show();
                    }
                    ValidateTextFields.field(name);
                    ValidateTextFields.field(email);
                    ValidateTextFields.field(detailOfBank);
                }

            }
        });



        userMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options2[] = {"User", "Blood Bank", "Doctor", "Ambulance Provider"};
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                builder.setItems(options2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        userTextString = String.valueOf(options2[i]);
                        userText.setText(userTextString);

                        if(userTextString.equals("Blood Bank")){
                            menu.setVisibility(View.GONE);
                            bgText.setVisibility(View.GONE);
                            bankLayout.setVisibility(View.VISIBLE);
                            submit.setVisibility(View.VISIBLE);
                            ambLayout.setVisibility(View.GONE);
                            docLayout.setVisibility(View.GONE);
                            donorLayout.setVisibility(View.GONE);
                        }else if(userTextString.equals("Doctor")){
                            docLayout.setVisibility(View.VISIBLE);
                            ambLayout.setVisibility(View.GONE);
                            bankLayout.setVisibility(View.GONE);
                            donorLayout.setVisibility(View.GONE);
                        }else if(userTextString.equals("Ambulance Provider")){
                            ambLayout.setVisibility(View.VISIBLE);
                            bankLayout.setVisibility(View.GONE);
                            docLayout.setVisibility(View.GONE);
                            donorLayout.setVisibility(View.GONE);
                        }else {
                            donorLayout.setVisibility(View.VISIBLE);
                        }
                        //submit.setVisibility(View.VISIBLE);
                    }
                });
                builder.show();
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        bgString = String.valueOf(options[i]);
                        bgText.setText(bgString);
                        submit.setVisibility(View.VISIBLE);
                    }
                });
                builder.show();
            }
        });

    }


    private void sendVerificationCode(String mobile) {
        Log.e("mobile---", mobile);
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(mobile)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void fields() {

        donorLayout = findViewById(R.id.donor_lay);

        donorBox = findViewById(R.id.donor_box);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthSettings = firebaseAuth.getFirebaseAuthSettings();

        menu = findViewById(R.id.menu);
        userMenu = findViewById(R.id.user_menu);

        number = findViewById(R.id.number);
        password = findViewById(R.id.pass);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);

        beforePass = findViewById(R.id.after_number);
        afterPass = findViewById(R.id.after_pass);
        submit = findViewById(R.id.submit);

        bgText = findViewById(R.id.bg_text);
        userText = findViewById(R.id.user_text);

        //doc fields
        qualification = findViewById(R.id.specialization);
        organization = findViewById(R.id.organization);
        license = findViewById(R.id.license);
        address = findViewById(R.id.address);

        //ambulance_fields
        ambulanceNumber = findViewById(R.id.ambulance_number);
        organizationAmbulance = findViewById(R.id.organization_ambulance);
        facilitiesAmbulance = findViewById(R.id.facilities_ambulance);

        passLayout = findViewById(R.id.pa);
        detailLayout = findViewById(R.id.ll);
        bankLayout = findViewById(R.id.bank_layout);
        docLayout = findViewById(R.id.doc_layout);
        ambLayout = findViewById(R.id.amb_layout);

        //checkbox
        one = findViewById(R.id.one);
        two = findViewById(R.id.two);
        three = findViewById(R.id.three);
        four = findViewById(R.id.four);

        detailOfBank = findViewById(R.id.extra_desc);


    }

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
                SignUpPage.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                SignUpPage.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            Log.i("---locif---", "");
        } else {
            Log.i("----else", "else");
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                double lat = locationGPS.getLatitude();
                double longi = locationGPS.getLongitude();
                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);
                Log.i("---locelse---", "");
                Log.i("-----loc----", latitude+"---"+longitude);

            } else {
                Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();

            }
        }

    }

}