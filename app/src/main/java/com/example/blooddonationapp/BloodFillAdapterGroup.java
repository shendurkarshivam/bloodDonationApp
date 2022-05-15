package com.example.blooddonationapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonationapp.Model.BloodGroup;
import com.example.blooddonationapp.Model.BloodRequests;
import com.example.blooddonationapp.Notifications.Notification;
import com.example.blooddonationapp.Utils.FirebaseDatabaseInstance;
import com.example.blooddonationapp.Utils.SharedPreference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BloodFillAdapterGroup extends RecyclerView.Adapter<BloodFillAdapterGroup.ViewHolder> {

    Context context;
    ArrayList<BloodGroup> grpList = new ArrayList<>();
    View view;
    FirebaseDatabaseInstance rootRef;
    SharedPreference pref;
    String currentUserId;
    String userType;
    String id;
    public BloodFillAdapterGroup(Context context, ArrayList<BloodGroup> grpList, String id) {
        this.grpList = grpList;
        this.context = context;
        this.id = id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        pref = SharedPreference.getInstance();
        currentUserId = pref.getData(SharedPreference.currentUserId, context);
        userType = pref.getData(SharedPreference.userType, context);

        rootRef = FirebaseDatabaseInstance.getInstance();

        view = LayoutInflater.from(context).inflate(R.layout.item_blood_group, parent, false);
        return new BloodFillAdapterGroup.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if(currentUserId.equals(id)){
            holder.user.setVisibility(View.GONE);
        }else {
            holder.owner.setVisibility(View.GONE);
        }
        BloodGroup bloodGroup = grpList.get(position);

        holder.setIsRecyclable(false);
        holder.type.setText(bloodGroup.getBloodGroup());
        holder.quantity.setText(bloodGroup.getQuantity());

        holder.quantityU.setText("0");

        holder.decreU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Integer.parseInt(holder.quantityU.getText().toString())!=0){
                    int currr = Integer.parseInt(holder.quantityU.getText().toString());
                    --currr;
                    //bloodGroup.setQuantity(String.valueOf(currr));
                    holder.quantityU.setText(String.valueOf(currr));
                }
            }
        });

        holder.increU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int av = Integer.parseInt(holder.quantity.getText().toString());
                int dem = Integer.parseInt(holder.quantityU.getText().toString());
                if(av<=dem){
                    Toast.makeText(context, "Not available in this Quantity", Toast.LENGTH_LONG).show();
                }else {
                    int currr = Integer.parseInt(holder.quantityU.getText().toString());
                    ++currr;
                    //bloodGroup.setQuantity(String.valueOf(currr));
                    holder.quantityU.setText(String.valueOf(currr));
                }
            }
        });
        holder.incre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Integer.parseInt(bloodGroup.getQuantity())!=10){
                    int currr = Integer.parseInt(bloodGroup.getQuantity());
                    ++currr;
                    bloodGroup.setQuantity(String.valueOf(currr));
                    holder.quantity.setText(String.valueOf(currr));
                }
            }
        });
        holder.decre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Integer.parseInt(bloodGroup.getQuantity())!=0){
                    int currr = Integer.parseInt(bloodGroup.getQuantity());
                    --currr;
                    bloodGroup.setQuantity(String.valueOf(currr));
                    holder.quantity.setText(String.valueOf(currr));
                }

            }
        });

        holder.butUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bloodGroup.getBloodGroup().equals("A+")){
                    createRequest("aPlus", bloodGroup.getType(), holder.quantityU.getText().toString());
                    //updateBG("aPlus", bloodGroup.getType(), holder.quantity.getText().toString());
                }
                if(bloodGroup.getBloodGroup().equals("A-")){
                    createRequest("aNeg", bloodGroup.getType(), holder.quantityU.getText().toString());
                }
                if(bloodGroup.getBloodGroup().equals("B+")){
                    createRequest("bPlus", bloodGroup.getType(), holder.quantityU.getText().toString());
                }
                if(bloodGroup.getBloodGroup().equals("B-")){
                    createRequest("bNeg", bloodGroup.getType(), holder.quantityU.getText().toString());
                }
                if(bloodGroup.getBloodGroup().equals("AB+")){
                    createRequest("abPlus", bloodGroup.getType(), holder.quantityU.getText().toString());
                }
                if(bloodGroup.getBloodGroup().equals("AB-")){
                    createRequest("abNeg", bloodGroup.getType(), holder.quantityU.getText().toString());
                }
                if(bloodGroup.getBloodGroup().equals("O+")){
                    createRequest("oPlus", bloodGroup.getType(), holder.quantityU.getText().toString());
                }
                if(bloodGroup.getBloodGroup().equals("O-")){
                    createRequest("oNeg", bloodGroup.getType(), holder.quantityU.getText().toString());
                }

            }
        });
        holder.but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bloodGroup.getBloodGroup().equals("A+")){
                    updateBG("aPlus", bloodGroup.getType(), holder.quantity.getText().toString());
                }
                if(bloodGroup.getBloodGroup().equals("A-")){
                    updateBG("aNeg", bloodGroup.getType(), holder.quantity.getText().toString());
                }
                if(bloodGroup.getBloodGroup().equals("B+")){
                    updateBG("bPlus", bloodGroup.getType(), holder.quantity.getText().toString());
                }
                if(bloodGroup.getBloodGroup().equals("B-")){
                    updateBG("bNeg", bloodGroup.getType(), holder.quantity.getText().toString());
                }
                if(bloodGroup.getBloodGroup().equals("AB+")){
                    updateBG("abPlus", bloodGroup.getType(), holder.quantity.getText().toString());
                }
                if(bloodGroup.getBloodGroup().equals("AB-")){
                    updateBG("abNeg", bloodGroup.getType(), holder.quantity.getText().toString());
                }
                if(bloodGroup.getBloodGroup().equals("O+")){
                    updateBG("oPlus", bloodGroup.getType(), holder.quantity.getText().toString());
                }
                if(bloodGroup.getBloodGroup().equals("O-")){
                    updateBG("oNeg", bloodGroup.getType(), holder.quantity.getText().toString());
                }

            }
        });

    }

    private void createRequest(String group, String type, String quant) {
        String reqId = rootRef.getAmbRef().child("Requests").push().getKey();
        BloodRequests bloodRequests = new BloodRequests(id, currentUserId, reqId, type, group, quant, System.currentTimeMillis(), "no", userType);
        rootRef.getBankRef().child(id).child("Requests").child(reqId).setValue(bloodRequests).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(context, "Request is successfully Created", Toast.LENGTH_LONG).show();
                Notification.sendPersonalNotifiaction(currentUserId, id, group+"("+type+")"+": "+quant+" pints", "Blood Request", "blood_req", id);
            }
        });
    }

    private void updateBG(String bloodType, String typeOfDonation, String quant) {
        rootRef.getBankRef().child(currentUserId).child("FacilityDetails").child(typeOfDonation).child(bloodType).setValue(quant);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return grpList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView type, quantity, quantityU;
        ImageView incre, decre;
        TextView but, butUser;
        LinearLayout user, owner;


        ImageView increU, decreU;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            incre = itemView.findViewById(R.id.incre);
            decre = itemView.findViewById(R.id.decre);
            type = itemView.findViewById(R.id.blood_type);
            quantity = itemView.findViewById(R.id.quantity);
            but = itemView.findViewById(R.id.upddate_but);

            increU = itemView.findViewById(R.id.increU);
            decreU = itemView.findViewById(R.id.decreU);
            quantityU = itemView.findViewById(R.id.quantityU);

            butUser = itemView.findViewById(R.id.update_user);

            user = itemView.findViewById(R.id.user);
            owner = itemView.findViewById(R.id.owner);


        }
    }
}
