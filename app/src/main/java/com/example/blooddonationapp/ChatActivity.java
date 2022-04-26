package com.example.blooddonationapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.blooddonationapp.Model.Doctor;
import com.example.blooddonationapp.Model.Queries;
import com.example.blooddonationapp.Model.User;
import com.example.blooddonationapp.Utils.FirebaseDatabaseInstance;
import com.example.blooddonationapp.Utils.SharedPreference;
import com.example.blooddonationapp.Utils.ValidateTextFields;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    FirebaseDatabaseInstance rootRef;
    SharedPreference pref;
    ImageView back;
    TextView name;
    EditText write;
    ImageButton send;
    String currentUserId, userType;
    String rec;
    Doctor doc;
    User user;
    RecyclerView msgRec;
    ArrayList<Queries> msgList = new ArrayList<Queries>();

    //msg fields
    String to, from, text="", id;
    Queries message;
    MessageAdapter messageAdapter;

    @Override
    protected void onStart() {
        super.onStart();
        loadMsgs();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        rootRef = FirebaseDatabaseInstance.getInstance();
        pref = SharedPreference.getInstance();
        currentUserId = pref.getData(SharedPreference.currentUserId, getApplicationContext());
        userType = pref.getData(SharedPreference.userType, getApplicationContext());
        fields();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        msgRec.setHasFixedSize(true);
        msgRec.setLayoutManager(linearLayoutManager);


        rec = getIntent().getStringExtra("rec");
        if(userType.equals("User")){
            getDocName();

        }else {
            getPersonName();
        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text = write.getText().toString();
                Log.i("clicked----", text);
                if(!text.equals("")){
                    id = rootRef.getMessageRef().push().getKey();
                    to = rec;
                    from = currentUserId;
                    Log.i("----ii---", from);
                    message = new Queries(to, from, text, id, 88980);
                    rootRef.getMessageRef().child(id).setValue(message);
                    rootRef.getMessageList().child(from).child(to).child(id).setValue("");
                    rootRef.getMessageList().child(to).child(from).child(id).setValue("");
                    write.setText("");
                }else{
                    ValidateTextFields.field(write);
                }
            }
        });
    }

    private void getPersonName() {
        rootRef.getUserRef().child(rec).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    user = snapshot.getValue(User.class);
                    name.setText(user.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadMsgs() {
        msgList.clear();
        messageAdapter = new MessageAdapter(getApplicationContext(), msgList);
        Log.i("----current", currentUserId);
        Log.i("----rec", rec);
        rootRef.getMessageList().child(currentUserId).child(rec).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot snap: snapshot.getChildren()){
                        Log.i("----snap", snap.getKey());
                        rootRef.getMessageRef().child(snap.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    Queries m = snapshot.getValue(Queries.class);
                                    msgList.add(m);
                                    messageAdapter.notifyDataSetChanged();
                                }
                                msgRec.setAdapter(messageAdapter);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    };

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getDocName() {
        rootRef.getDocRef().child(rec).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.exists()){
                   doc = snapshot.getValue(Doctor.class);
                   name.setText(doc.getName()+", "+doc.getSpecialization());
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fields() {
        back = findViewById(R.id.back_chat);
        name = findViewById(R.id.chat_name);
        write = findViewById(R.id.input_message);
        send = findViewById(R.id.send_message_btn);
        msgRec = findViewById(R.id.msg_list);
    }
}