package com.example.blooddonationapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonationapp.Model.Doctor;

import java.util.ArrayList;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.ViewHolder>{
    Context context;
    ArrayList<Doctor> list = new ArrayList<>();
    public DoctorAdapter(Context context, ArrayList<Doctor> list) {
        this.list=list;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_doctor, parent, false);
        return new DoctorAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Doctor user = list.get(position);
        holder.donorName.setText(user.getName());
        Log.i("specialization---", user.getSpecialization());
        holder.donorBG.setText(user.getSpecialization());
        holder.email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChatPage(user);
            }
        });

    }

    private void openChatPage(Doctor user) {
        String docId = user.getId();
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("rec", docId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
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
            email = itemView.findViewById(R.id.email_contact);
        }
    }
}
