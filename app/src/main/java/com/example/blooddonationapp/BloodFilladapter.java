package com.example.blooddonationapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonationapp.Model.BloodFill;
import com.example.blooddonationapp.Model.BloodGroup;
import com.example.blooddonationapp.Utils.FirebaseDatabaseInstance;
import com.example.blooddonationapp.Utils.SharedPreference;

import java.util.ArrayList;

public class BloodFilladapter extends RecyclerView.Adapter<BloodFilladapter.ViewHolder> {

    FirebaseDatabaseInstance rootRef;
    SharedPreference pref;
    String currentUserId;
    String userType;
    View view;
    Context context;
    String id;
    ArrayList<BloodFill> bloodList = new ArrayList<>();
    ArrayList<BloodGroup> grpList = new ArrayList<>();


    public BloodFilladapter(){

    }public BloodFilladapter(Context context, ArrayList<BloodFill> bloodList, String id){
        this.context=context;
        this.bloodList=bloodList;
        this.id = id;
    }

    @Override
    public int getItemViewType(int position) {
        return (position);
    }

    @Override
    public long getItemId(int position) {
        return (position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        rootRef = FirebaseDatabaseInstance.getInstance();
        pref = SharedPreference.getInstance();
        userType = pref.getData(SharedPreference.userType, context);
        currentUserId = pref.getData(SharedPreference.currentUserId, context);
        view = LayoutInflater.from(context).inflate(R.layout.item_blood_fill, parent, false);
        return new BloodFilladapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BloodFill bloodFill = bloodList.get(position);
        holder.setIsRecyclable(false);
        holder.title.setText(bloodFill.getFacilityType());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        holder.innerRec.setHasFixedSize(true);
        holder.innerRec.setLayoutManager(linearLayoutManager);

        grpList.clear();
        BloodFillAdapterGroup bloodFillAdapterGroup = new BloodFillAdapterGroup(context, grpList, id);

        holder.innerRec.setAdapter(bloodFillAdapterGroup);

        BloodGroup bloodGroup = new BloodGroup("A+", bloodFill.getaPlus(), bloodFill.getFacilityType());
        grpList.add(bloodGroup);
        bloodGroup = new BloodGroup("A-", bloodFill.getaNeg(), bloodFill.getFacilityType());
        grpList.add(bloodGroup);
        bloodGroup = new BloodGroup("B+", bloodFill.getbPlus(), bloodFill.getFacilityType());
        grpList.add(bloodGroup);
        bloodGroup = new BloodGroup("B-", bloodFill.getbNeg(), bloodFill.getFacilityType());
        grpList.add(bloodGroup);
        bloodGroup = new BloodGroup("AB+", bloodFill.getAbPlus(), bloodFill.getFacilityType());
        grpList.add(bloodGroup);
        bloodGroup = new BloodGroup("AB-", bloodFill.getAbNeg(), bloodFill.getFacilityType());
        grpList.add(bloodGroup);
        bloodGroup = new BloodGroup("O+", bloodFill.getoPlus(), bloodFill.getFacilityType());
        grpList.add(bloodGroup);
        bloodGroup = new BloodGroup("O-", bloodFill.getoNeg(), bloodFill.getFacilityType());
        grpList.add(bloodGroup);

        //bloodFillAdapterGroup.notifyDataSetChanged();



    }

    @Override
    public int getItemCount() {
        return bloodList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        RecyclerView innerRec; // same id
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.facility_name);
            innerRec = itemView.findViewById(R.id.inner_rec);
        }
    }
}
