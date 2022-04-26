package com.example.blooddonationapp;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonationapp.Model.AmbulanceProvider;
import com.example.blooddonationapp.Model.AmbulanceRequests;
import com.example.blooddonationapp.Model.BloodBank;
import com.example.blooddonationapp.Model.Doctor;
import com.example.blooddonationapp.Model.User;
import com.example.blooddonationapp.Utils.DateAndTime;
import com.example.blooddonationapp.Utils.FirebaseDatabaseInstance;
import com.example.blooddonationapp.Utils.SharedPreference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder>{

    View view;
    Context context;
    ArrayList<AmbulanceRequests> requestList = new ArrayList<>();
    FirebaseDatabaseInstance rootRef;
    SharedPreference pref;
    User user;
    Doctor doctor;
    BloodBank bloodBank;
    AmbulanceProvider ambulanceProvider;

    public RequestAdapter(Context context, ArrayList<AmbulanceRequests> requestList) {
        this.context=context;
        this.requestList=requestList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        rootRef = FirebaseDatabaseInstance.getInstance();
        pref = SharedPreference.getInstance();

        view = LayoutInflater.from(context).inflate(R.layout.item_request, parent, false);
        return new RequestAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AmbulanceRequests ambulanceRequests = requestList.get(position);
        String userType = ambulanceRequests.getUserType();
        String fromId = ambulanceRequests.getPersonId();

        holder.requestDetails.setText(ambulanceRequests.getDetails());
        holder.dateTime.setText(DateAndTime.getDate(ambulanceRequests.getTimestamp()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openRequestPage(ambulanceRequests.getReqId());
            }
        });

        if(userType.equals("User")){
            rootRef.getUserRef().child(fromId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        user = snapshot.getValue(User.class);
                        writeName(user.getName(), holder);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else if(userType.equals("Blood Bank")){
            rootRef.getBankRef().child(fromId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        bloodBank = snapshot.getValue(BloodBank.class);
                        writeName(bloodBank.getName(), holder);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else if(userType.equals("Doctor")){
            rootRef.getDocRef().child(fromId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        doctor = snapshot.getValue(Doctor.class);
                        writeName(doctor.getName(), holder);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else {
            rootRef.getAmbRef().child(fromId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        ambulanceProvider = snapshot.getValue(AmbulanceProvider.class);
                        writeName(ambulanceProvider.getName(), holder);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }

    private void openRequestPage(String reqId) {
        Intent intent = new Intent(context, RequestDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("reqId", reqId);
        context.startActivity(intent);
    }

    private void writeName(String name, ViewHolder holder) {
        String one = "<b>Request from: </b>"+name;
        holder.requestFrom.setText(Html.fromHtml(one));
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView requestFrom, requestDetails, dateTime;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            requestFrom = itemView.findViewById(R.id.request_from);
            requestDetails = itemView.findViewById(R.id.details_of_req);
            dateTime = itemView.findViewById(R.id.date_time);
        }
    }
}
