package com.example.blooddonationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.blooddonationapp.Model.Queries;
import com.example.blooddonationapp.Utils.FirebaseDatabaseInstance;
import com.example.blooddonationapp.Utils.SharedPreference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

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

    Queries query;

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


    }

    private void getPublisherName() {
        rootRef.getMessageRef().child(msgKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                query = snapshot.getValue(Queries.class);

                if(query.getUserType().equals("User")){
                    fillPublisherName("Users");
                }else if(query.getUserType().equals("Doctor")){
                    fillPublisherName("Doctors");
                }else if(query.getUserType().equals("Blood Bank")){
                    fillPublisherName("BloodBanks");
                }else {
                    fillPublisherName("AmbulanceProvider");
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