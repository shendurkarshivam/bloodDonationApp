package com.example.blooddonationapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blooddonationapp.Model.Queries;
import com.example.blooddonationapp.Notifications.Notification;
import com.example.blooddonationapp.Utils.FirebaseDatabaseInstance;
import com.example.blooddonationapp.Utils.SharedPreference;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReplyActivity extends AppCompatActivity {

    FirebaseDatabaseInstance rootRef;
    SharedPreference pref;
    String currentUserId, userType;
    String allowReply, msgKey;
    RecyclerView replyRec;
    TextView namePublisher, messageText;
    ImageButton sendReply;
    EditText inputMessage;

    RelativeLayout sendMessageRelativelayout;
    MessageAdapter messageAdapter;
    Queries query;
    ArrayList<Queries> a = new ArrayList<>();

    private void fields() {
        replyRec = findViewById(R.id.reply_rec);
        namePublisher = findViewById(R.id.name_publisher);
        messageText = findViewById(R.id.msg);
        inputMessage = findViewById(R.id.input_message);
        sendReply = findViewById(R.id.send_message_btn);

        sendMessageRelativelayout = findViewById(R.id.chat_linear_layout);



    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        pref = SharedPreference.getInstance();
        rootRef = FirebaseDatabaseInstance.getInstance();

        currentUserId = pref.getData(SharedPreference.currentUserId, getApplicationContext());
        userType = pref.getData(SharedPreference.userType, getApplicationContext());




        allowReply = getIntent().getStringExtra("allow_reply");
        msgKey = getIntent().getStringExtra("msgKey");

        getPublisherName();


        fields();

        if(allowReply.equals("no")){
            sendMessageRelativelayout.setVisibility(View.GONE);
        }

        sendReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!inputMessage.getText().toString().equals("")){
                    String inp = inputMessage.getText().toString();
                    String replyKey = rootRef.getReplyRef().child(query.getId
                            ()).push().getKey();
                   Queries q = new Queries(query.getFrom(), currentUserId, inp, replyKey, userType, System.currentTimeMillis());
                   rootRef.getReplyRef().child(query.getId()).child(replyKey).setValue(q);
                   inputMessage.setText("");
                   if(userType.equals("Doctor")){
                       if(!currentUserId.equals(query.getFrom())){
                           Notification.sendPersonalNotifiaction(currentUserId, query.getFrom(), inp, "A Query Thread", "query", query.getId());
                       }
                   }else{
                       rootRef.getDocRef().addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot snapshot) {
                               if(snapshot.exists()){
                                   for(DataSnapshot snap : snapshot.getChildren()){
                                       Notification.sendPersonalNotifiaction(currentUserId, snap.getKey(), inp, "A Query Thread", "query", query.getId());
                                   }
                               }
                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError error) {

                           }
                       });
                   }

                }else {
                    Toast.makeText(getApplicationContext(), "Please type something", Toast.LENGTH_LONG).show();
                }
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        replyRec.setHasFixedSize(true);
        replyRec.setLayoutManager(linearLayoutManager);



    }

    private void setRecyclerView() {
        a.clear();
        messageAdapter = new MessageAdapter(getApplicationContext(), a);
        replyRec.setAdapter(messageAdapter);

        rootRef.getReplyRef().child(query.getId()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    Queries l = snapshot.getValue(Queries.class);
                    a.add(l);
                    messageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getPublisherName() {
        rootRef.getMessageRef().child(msgKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                query = snapshot.getValue(Queries.class);

                if(query.getUserType().equals("User")){
                    fillPublisherName("Users");
                    setRecyclerView();
                }else if(query.getUserType().equals("Doctor")){
                    fillPublisherName("Doctors");
                    setRecyclerView();
                }else if(query.getUserType().equals("Blood Bank")){
                    fillPublisherName("BloodBanks");
                    setRecyclerView();
                }else {
                    fillPublisherName("AmbulanceProvider");
                    setRecyclerView();
                }
                messageText.setText(query.getText());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fillPublisherName(String key) {
        rootRef.getRootRef().child(key).child(query.getFrom()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                namePublisher.setText(snapshot.child("name").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}