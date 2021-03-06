package com.example.blooddonationapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonationapp.Model.Queries;
import com.example.blooddonationapp.Utils.FirebaseDatabaseInstance;
import com.example.blooddonationapp.Utils.SharedPreference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    View view;
    Context context;
    FirebaseDatabaseInstance rootRef;
    SharedPreference pref;
    String currentUserId, userType;
    ArrayList<Queries> msgList = new ArrayList<>();

    public MessageAdapter(){

    }
    public MessageAdapter(Context context, ArrayList<Queries> msgList){
        this.context=context;
        this.msgList=msgList;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        rootRef = FirebaseDatabaseInstance.getInstance();
        pref = SharedPreference.getInstance();
        currentUserId = pref.getData(SharedPreference.currentUserId, context);
        userType = pref.getData(SharedPreference.userType, context);

        view = LayoutInflater.from(context).inflate(R.layout.item_msg, parent, false);
        return new MessageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Queries message = msgList.get(position);

        holder.setIsRecyclable(true);
        if(message.getFrom().equals(currentUserId)){
            populateMySide(message, holder);
        }else {
            populateOtherSide(message, holder);
        }
        holder.replyReceiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToReplyPage(message);
            }
        });
        holder.replySender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToReplyPage(message);
            }
        });

    }

    private void goToReplyPage(Queries message) {

        Intent intent = new Intent(context, ReplyActivity.class);
        if(userType.equals("Doctor")||message.getFrom().equals(currentUserId)){
            intent.putExtra("allow_reply", "yes");
        }else {
            intent.putExtra("allow_reply", "no");
        }
        intent.putExtra("msgKey", message.getId());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void populateOtherSide(Queries message, ViewHolder holder) {
        holder.receiverLayout.setVisibility(View.VISIBLE);
        holder.senderLayout.setVisibility(View.GONE);
        holder.rec.setText(message.getText());

        if(message.getUserType().equals("User")){
            fillPublisherName("Users", holder, message);
        }else if(message.getUserType().equals("Doctor")){
            fillPublisherName("Doctors", holder, message);
        }else if(message.getUserType().equals("Blood Bank")){
            fillPublisherName("BloodBanks", holder,message);
        }else {
            fillPublisherName("AmbulanceProvider", holder, message);
        }
    }

    private void fillPublisherName(String key, ViewHolder holder, Queries message) {
        rootRef.getRootRef().child(key).child(message.getFrom()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.recName.setText(snapshot.child("name").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void populateMySide(Queries message, ViewHolder holder) {
        holder.receiverLayout.setVisibility(View.GONE);
        holder.senderLayout.setVisibility(View.VISIBLE);
        holder.sent.setText(message.getText());
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout senderLayout, receiverLayout;
        TextView sent, rec;

        TextView recName;
        TextView replySender, replyReceiver;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            senderLayout = itemView.findViewById(R.id.sender_layout);
            receiverLayout = itemView.findViewById(R.id.rec_layout);

            sent = itemView.findViewById(R.id.sender_text);
            rec = itemView.findViewById(R.id.rec_text);

            recName = itemView.findViewById(R.id.name_of_sender);

            replySender = itemView.findViewById(R.id.reply_sender);
            replyReceiver = itemView.findViewById(R.id.reply_receiver);


        }
    }
}
