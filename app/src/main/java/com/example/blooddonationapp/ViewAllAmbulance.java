package com.example.blooddonationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blooddonationapp.Model.AmbulanceProvider;
import com.example.blooddonationapp.Model.AmbulanceRequests;
import com.example.blooddonationapp.Notifications.Notification;
import com.example.blooddonationapp.Utils.FirebaseDatabaseInstance;
import com.example.blooddonationapp.Utils.SharedPreference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewAllAmbulance extends AppCompatActivity {
    LinearLayout requestAmb;
    RecyclerView allAmbList;
    ImageView menu;
    FirebaseDatabaseInstance rootRef;
    SharedPreference pref;
    AmbulanceProvider ambulanceProvider;
    ArrayList<AmbulanceProvider> list = new ArrayList<>();
    ShowAllAmbulanceAdapter showAllAmbulanceAdapter;
    LocationManager locationManager;

    String currentUserId, userType;
    private static final int REQUEST_LOCATION = 1;

    String latitude="", longitude="";
    String text = "";


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
                ViewAllAmbulance.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                ViewAllAmbulance.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            Log.i("---locif---", "");
        } else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                double lat = locationGPS.getLatitude();
                double longi = locationGPS.getLongitude();
                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);
                Log.i("---locelse---", "");
                Log.i("-----loc----", latitude+"---"+longitude);
                String reqId = rootRef.getRequestRef().push().getKey();
                long timestamp = System.currentTimeMillis();

                AmbulanceRequests ambulanceRequests =
                        new AmbulanceRequests(reqId, currentUserId, latitude, longitude, text, userType, timestamp);

                rootRef.getRequestRef().child(reqId).setValue(ambulanceRequests).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        rootRef.getMyRequestRef().child(currentUserId).child(reqId).setValue("");
                        Toast.makeText(getApplicationContext(), "Request is Created Successfully", Toast.LENGTH_LONG).show();

                        rootRef.getAmbRef().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    for(DataSnapshot snap : snapshot.getChildren()){
                                        Notification.sendPersonalNotifiaction(currentUserId, snap.getKey(), text,"Ambulance Request", "amb_req", reqId);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

                /*Uri navigation = Uri.parse("google.navigation:q="+latitude+","+longitude+"");
                Intent navigationIntent = new Intent(Intent.ACTION_VIEW, navigation);
                navigationIntent.setPackage("com.google.android.apps.maps");
                startActivity(navigationIntent);*/
            } else {
                Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();

            }
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_ambulance);

        rootRef = FirebaseDatabaseInstance.getInstance();
        pref = SharedPreference.getInstance();

        currentUserId = pref.getData(SharedPreference.currentUserId, getApplicationContext());
        userType = pref.getData(SharedPreference.userType, getApplicationContext());

        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ActivityCompat.requestPermissions( this,
                new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        fields();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());

        allAmbList.setHasFixedSize(true);

        allAmbList.setLayoutManager(linearLayoutManager);

        populateAmbulanceProviders("");

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userType.equals("Ambulance Provider")){
                    CharSequence options2[] = {"Current Requests", "My Trips"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                    builder.setItems(options2, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(String.valueOf(options2[i]).equals("Current Requests")){
                                startActivity(new Intent(ViewAllAmbulance.this, CurrentRequests.class));
                            }
                            else {
                                Intent intent = new Intent(ViewAllAmbulance.this, AllTripsByMeAsDriver.class);
                                intent.putExtra("id", currentUserId);
                                startActivity(intent);
                            }
                            //submit.setVisibility(View.VISIBLE);
                        }
                    });
                    builder.show();
                }else {
                    CharSequence options1[] = {"My Requests", "My Trips"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                    builder.setItems(options1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            //submit.setVisibility(View.VISIBLE);
                            if(String.valueOf(options1[i]).equals("My Requests")){
                                startActivity(new Intent(ViewAllAmbulance.this, CurrentRequests.class));

                            }else{
                                Intent intent = new Intent(ViewAllAmbulance.this, AllTripsByMeAsDriver.class);
                                intent.putExtra("id", currentUserId);
                                startActivity(intent);
                            }
                        }
                    });
                    builder.show();
                }

            }
        });
        requestAmb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewAllAmbulance.this);
                /*builder.setTitle("Enter new Topic");*/

                LayoutInflater inflater = (ViewAllAmbulance.this).getLayoutInflater();
                View v = inflater.inflate(R.layout.fragment_details, null);
                //final String maaa=topic.getText().toString();
                //  v.setLayoutParams(new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT));
                builder.setView(v);
                final AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();

                final EditText detail = v.findViewById(R.id.details);
                Button detail_cancel = v.findViewById(R.id.details_cancel);
                Button detail_publish = v.findViewById(R.id.details_publish);

                detail_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        detail.setText("");
                        alertDialog.dismiss();
                    }
                });
                detail_publish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        text = detail.getText().toString();
                        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            OnGPS();
                        } else {
                            getLocation();
                        }
                        alertDialog.dismiss();

                    }
                });



            }
        });
    }

    private void populateAmbulanceProviders(String s) {
        list.clear();
        showAllAmbulanceAdapter = new ShowAllAmbulanceAdapter(getApplicationContext(), list);

        rootRef.getAmbRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for(DataSnapshot snap: snapshot.getChildren()){
                        ambulanceProvider = snap.getValue(AmbulanceProvider.class);
                        list.add(ambulanceProvider);
                    }
                    allAmbList.setAdapter(showAllAmbulanceAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void fields() {
        requestAmb = findViewById(R.id.request_amb);
        allAmbList = findViewById(R.id.all_amb_list);
        menu = findViewById(R.id.menu);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the phone call

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}

class ShowAllAmbulanceAdapter extends RecyclerView.Adapter<ShowAllAmbulanceAdapter.ViewHolder> {
    View view;
    Context context;
    FirebaseDatabaseInstance rootRef;
    SharedPreference pref;
    String currentUserId;

    ArrayList<AmbulanceProvider> list = new ArrayList<>();

    public ShowAllAmbulanceAdapter(Context context, ArrayList<AmbulanceProvider> list) {
        this.list=list;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        rootRef = FirebaseDatabaseInstance.getInstance();
        pref = SharedPreference.getInstance();
        currentUserId = pref.getData(SharedPreference.currentUserId, context);

        view = LayoutInflater.from(context).inflate(R.layout.item_donor, parent, false);
        return new ShowAllAmbulanceAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AmbulanceProvider ambulanceProvider = list.get(position);

        holder.name.setText(ambulanceProvider.getName());
        holder.facilities.setText(ambulanceProvider.getAmbulanceFacilities());

        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+ambulanceProvider.getNumber()));
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.CALL_PHONE},
                            1);

                    // MY_PERMISSIONS_REQUEST_CALL_PHONE is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                } else {
                    //You already have permission
                    try {
                        context.startActivity(callIntent);
                    } catch(SecurityException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name, facilities;
        ImageView call, mail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.donor_name);
            facilities = itemView.findViewById(R.id.donor_bg);

            call = itemView.findViewById(R.id.call);
            mail = itemView.findViewById(R.id.email_contact);
        }
    }
}
