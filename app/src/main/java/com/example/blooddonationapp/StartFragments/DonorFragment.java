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

import com.example.blooddonationapp.Model.User;
import com.example.blooddonationapp.R;
import com.example.blooddonationapp.Utils.FirebaseDatabaseInstance;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DonorFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    View view;
    FirebaseDatabaseInstance rootRef;
    RecyclerView donorList;
    ArrayList<User> list = new ArrayList<>();
    DonorAdapter donorAdapter;


    public DonorFragment() {
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
        view = inflater.inflate(R.layout.fragment_donor, container, false);
        // Inflate the layout for this fragment
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rootRef = FirebaseDatabaseInstance.getInstance();
        donorList = view.findViewById(R.id.donor_list);
        donorList.setHasFixedSize(true);
        donorList.setLayoutManager(linearLayoutManager);

        populateDonorList("");
        return view;
    }

    private void populateDonorList(String s) {
        list.clear();
        donorAdapter = new DonorAdapter(getContext(), list);

        rootRef.getUserRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot snap : snapshot.getChildren()){
                        User user = snap.getValue(User.class);
                        if(user.getName().contains(s) && user.getType().equals("User")){
                            list.add(user);
                            donorAdapter.notifyDataSetChanged();
                        }
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

class DonorAdapter extends RecyclerView.Adapter<DonorAdapter.ViewHolder>{
    Context context;
    ArrayList<User> list = new ArrayList<>();
    public DonorAdapter(Context context, ArrayList<User> list) {
        this.list=list;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_donor, parent, false);
        return new DonorAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = list.get(position);
        holder.donorName.setText(user.getName());
        holder.donorBG.setText(user.getBg());

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