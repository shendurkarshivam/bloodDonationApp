package com.example.blooddonationapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonationapp.Model.BloodBank;
import com.example.blooddonationapp.Model.BloodRequests;
import com.example.blooddonationapp.Notifications.Notification;
import com.example.blooddonationapp.Utils.FirebaseDatabaseInstance;
import com.example.blooddonationapp.Utils.SharedPreference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BloodRequestAdapter extends RecyclerView.Adapter<BloodRequestAdapter.ViewHolder> {

    View view;
    FirebaseDatabaseInstance rootRef;
    SharedPreference pref;
    ArrayList<BloodRequests> bloodRequestList = new ArrayList<>();
    Context context;
    String currentUserId;
    int av, de;

    public BloodRequestAdapter(Context context, ArrayList<BloodRequests> bloodRequestList) {
        this.context=context;
        this.bloodRequestList=bloodRequestList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        view = LayoutInflater.from(context).inflate(R.layout.item_blood_requests, parent, false);
        rootRef = FirebaseDatabaseInstance.getInstance();
        pref = SharedPreference.getInstance();

        currentUserId = pref.getData(SharedPreference.currentUserId, context);

        return new BloodRequestAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BloodRequests bloodRequests = bloodRequestList.get(position);

        if(bloodRequests.getBankId().equals(currentUserId)){
            holder.ll.setVisibility(View.GONE);
        }
        if(bloodRequests.getStatus().equals("no") && bloodRequests.getBankId().equals(currentUserId)){
            holder.reqs.setVisibility(View.VISIBLE);
        }
        if(bloodRequests.getRequesterType().equals("User")){
            populateRequester(holder, "Users", bloodRequests);
        }else if(bloodRequests.getRequesterType().equals("Blood Bank")){
            populateRequester(holder, "BloodBanks", bloodRequests);
        }else if(bloodRequests.getRequesterType().equals("Doctor")){
            populateRequester(holder, "Doctors", bloodRequests);
        }else {
            populateRequester(holder, "AmbulanceProvider", bloodRequests);
        }
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootRef.getBankRef().child(bloodRequests.getBankId()).child("Requests").child(bloodRequests.getRequestId()).child("status").setValue("yes");
                rootRef.getBankRef().child(bloodRequests.getBankId()).child("FacilityDetails").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            av = Integer.parseInt(snapshot.child(bloodRequests.getTypeOfDonation()).child(bloodRequests.getBloodGroup()).getValue().toString());
                            rootRef.getBankRef().child(bloodRequests.getBankId()).child("Requests").child(bloodRequests.getRequestId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        de = Integer.parseInt(snapshot.child("quantity").getValue().toString());
                                        int k = av-de;
                                        rootRef.getBankRef().child(bloodRequests.getBankId()).child("FacilityDetails")
                                                .child(bloodRequests.getTypeOfDonation())
                                                .child(bloodRequests.getBloodGroup()).setValue(String.valueOf(k));

                                        holder.reqs.setVisibility(View.GONE);

                                        Notification.sendPersonalNotifiaction(currentUserId, bloodRequests.getRequesterId(), "Your Request for Blood is Accepted", "Blood Request", "blood_req", bloodRequests.getBankId());
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        holder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.reqs.setVisibility(View.GONE);
                rootRef.getBankRef().child(bloodRequests.getBankId()).child("Requests").child(bloodRequests.getRequestId()).child("status").setValue("reject");
            }
        });

        holder.open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!bloodRequests.getBankId().equals(currentUserId)){
                    rootRef.getBankRef().child(bloodRequests.getBankId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            BloodBank b = snapshot.getValue(BloodBank.class);
                            Uri navigation = Uri.parse("google.navigation:q="+b.getLatitude()+","+b.getLongitude()+"");
                            Intent navigationIntent = new Intent(Intent.ACTION_VIEW, navigation);
                            navigationIntent.setPackage("com.google.android.apps.maps");
                            context.startActivity(navigationIntent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }else {

                }

            }
        });

    }

    private void populateRequester(ViewHolder holder, String userType, BloodRequests bloodRequests) {
        rootRef.getRootRef().child(userType).child(bloodRequests.getRequesterId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String name = snapshot.child("name").getValue().toString();
                    holder.reqBy.setText(Html.fromHtml("<b>Request By: </b>"+name));
                    if(bloodRequests.getBloodGroup().equals("aPlus")){
                        po(holder, "A+", bloodRequests.getTypeOfDonation());
                    }else if(bloodRequests.getBloodGroup().equals("aNeg")){
                        po(holder, "A-", bloodRequests.getTypeOfDonation());
                    }else if(bloodRequests.getBloodGroup().equals("bPlus")){
                        po(holder, "B+", bloodRequests.getTypeOfDonation());
                    }else if(bloodRequests.getBloodGroup().equals("bNeg")){
                        po(holder, "B-", bloodRequests.getTypeOfDonation());
                    }else if(bloodRequests.getBloodGroup().equals("oPlus")){
                        po(holder, "AB+", bloodRequests.getTypeOfDonation());
                    }else if(bloodRequests.getBloodGroup().equals("oNeg")){
                        po(holder, "AB-", bloodRequests.getTypeOfDonation());
                    }else if(bloodRequests.getBloodGroup().equals("abPlus")){
                        po(holder, "O+", bloodRequests.getTypeOfDonation());
                    }else {
                        po(holder, "O-", bloodRequests.getTypeOfDonation());
                    }
                    //holder.reqDetail.setText(Html.fromHtml("<b>Request Details: </b>"+bloodRequests.getBloodGroup()+"("+bloodRequests.getTypeOfDonation()+")"));
                    if(bloodRequests.getStatus().equals("no")){
                        holder.reqStatus.setText(Html.fromHtml("<b>Status: </b> Pending"));
                    }else if(bloodRequests.getStatus().equals("yes")){
                        holder.reqStatus.setText(Html.fromHtml("<b>Status: </b> Accepted"));
                    }else{
                        holder.reqStatus.setText(Html.fromHtml("<b>Status: </b> Rejected"));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void po(ViewHolder holder, String s, String typeOfDonation) {
        holder.reqDetail.setText(Html.fromHtml("<b>Request Details: </b>"+s+"("+typeOfDonation+")"));
    }

    @Override
    public int getItemCount() {
        return bloodRequestList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout reqs;
        TextView reqBy, reqDetail, reqStatus;
        Button accept, reject;
        LinearLayout ll;
        TextView open;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            reqBy = itemView.findViewById(R.id.req_by);
            reqDetail = itemView.findViewById(R.id.req_detail);
            reqStatus = itemView.findViewById(R.id.status_blood);

            reqs = itemView.findViewById(R.id.accept_reject);
            accept = itemView.findViewById(R.id.accept);
            reject = itemView.findViewById(R.id.reject);

            ll = itemView.findViewById(R.id.openMap);
            open = itemView.findViewById(R.id.open_in_map);
        }
    }
}
