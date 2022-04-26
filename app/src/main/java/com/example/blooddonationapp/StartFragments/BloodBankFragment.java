package com.example.blooddonationapp.StartFragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.blooddonationapp.BloodBankDetails;
import com.example.blooddonationapp.Model.BloodBank;
import com.example.blooddonationapp.Model.User;
import com.example.blooddonationapp.R;
import com.example.blooddonationapp.Utils.FirebaseDatabaseInstance;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BloodBankFragment extends Fragment {

    View view;
    FirebaseDatabaseInstance rootRef;
    RecyclerView donorList;
    ArrayList<BloodBank> list = new ArrayList<>();
    BloodBankAdapter bloodBankAdapter;
    public BloodBankFragment() {
        // Required empty public constructor
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



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_blood_bank, container, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rootRef = FirebaseDatabaseInstance.getInstance();
        donorList = view.findViewById(R.id.bank_list);
        donorList.setHasFixedSize(true);
        donorList.setLayoutManager(linearLayoutManager);

        populateDonorList("");



        return view;
    }

    private void populateDonorList(String s) {
        list.clear();
        bloodBankAdapter = new BloodBankAdapter(getContext(), list);

        rootRef.getBankRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot snap : snapshot.getChildren()){
                        BloodBank user = snap.getValue(BloodBank.class);
                        if(user.getName().contains(s) && user.getType().equals("Blood Bank")){
                            list.add(user);
                            bloodBankAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        donorList.setAdapter(bloodBankAdapter);
    }
}
class BloodBankAdapter extends RecyclerView.Adapter<BloodBankAdapter.ViewHolder>{
    Context context;
    ArrayList<BloodBank> list = new ArrayList<>();
    public BloodBankAdapter(Context context, ArrayList<BloodBank> list) {
        this.list=list;
        this.context=context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_bank, parent, false);
        return new BloodBankAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BloodBank user = list.get(position);
        holder.donorName.setText(user.getName());
        holder.donorBG.setText(user.getDetails());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+user.getNumber()));
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
                //context.startActivity(callIntent);
            }
        });
        holder.email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{ user.getEmail()});
                email.putExtra(Intent.EXTRA_SUBJECT, "Urgent Requirement | MedHelp | ");
                email.putExtra(Intent.EXTRA_TEXT, "");

//need this to prompts email client only
                email.setType("message/rfc822");

                context.startActivity(Intent.createChooser(email, "Choose an Email client :"));
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BloodBankDetails.class);
                intent.putExtra("bankId", user.getId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView donorName, donorBG;
        ImageView call, email;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            donorName = itemView.findViewById(R.id.donor_name);
            donorBG = itemView.findViewById(R.id.donor_bg);
            call = itemView.findViewById(R.id.call);
            email = itemView.findViewById(R.id.email_contact);
        }
    }
}