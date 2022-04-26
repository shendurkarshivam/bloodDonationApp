package com.example.blooddonationapp;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonationapp.Model.TripDetails;
import com.example.blooddonationapp.Utils.DateAndTime;
import com.example.blooddonationapp.Utils.FirebaseDatabaseInstance;
import com.example.blooddonationapp.Utils.SharedPreference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class AllTripAdapter extends RecyclerView.Adapter<AllTripAdapter.ViewHolder> {
    View view;
    Context context;
    ArrayList<TripDetails> tripDetails = new ArrayList<>();

    FirebaseDatabaseInstance rootRef;
    SharedPreference pref;
    String providerId, consumerId, consumerType;

    public AllTripAdapter(Context context, ArrayList<TripDetails> allTrips) {
        this.context = context;
        this.tripDetails = allTrips;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        pref = SharedPreference.getInstance();
        rootRef = FirebaseDatabaseInstance.getInstance();

        view = LayoutInflater.from(context).inflate(R.layout.item_trip_detail, parent, false);
        return new AllTripAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TripDetails trip = tripDetails.get(position);

        consumerId = trip.getConsumerId();
        providerId = trip.getDriverId();
        consumerType = trip.getConsumerType();

        fillDateAndTime(holder, trip);

        if(consumerType.equals("User")){
            fillConsumerDetails(holder, trip.getConsumerId(), "Users");
        }else if(consumerType.equals("Ambulance Provider")){
            fillConsumerDetails(holder, trip.getConsumerId(), "AmbulanceProvider");
        }else if(consumerType.equals("Doctor")){
            fillConsumerDetails(holder, trip.getConsumerId(), "Doctors");
        }else {
            fillConsumerDetails(holder, trip.getConsumerId(), "BloodBanks");
        }

        fillContractorDetails(holder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TripDetailsActivity.class);
                intent.putExtra("tripId", trip.getTripId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }

    private void fillDateAndTime(ViewHolder holder, TripDetails trip) {
        String dateTime="";
        dateTime+=DateAndTime.getDate(trip.getRequestTimeStamp());
        holder.dateTime.setText(dateTime);
    }

    private void fillContractorDetails(ViewHolder holder) {
        rootRef.getAmbRef().child(providerId).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = "<b>Provided By: </b>"+snapshot.getValue().toString();
                holder.doneBy.setText(Html.fromHtml(name));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fillConsumerDetails(ViewHolder holder, String consumerId, String key) {
        rootRef.getRootRef().child(key).child(consumerId).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = "<b>Requested By: </b>" + snapshot.getValue().toString();
                holder.from.setText(Html.fromHtml(name));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return tripDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView from, doneBy, dateTime;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            from = itemView.findViewById(R.id.from);
            doneBy = itemView.findViewById(R.id.done_by);
            dateTime = itemView.findViewById(R.id.date_time);
        }
    }
}
