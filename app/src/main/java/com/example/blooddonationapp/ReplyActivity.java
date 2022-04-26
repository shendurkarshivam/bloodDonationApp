package com.example.blooddonationapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.blooddonationapp.Utils.FirebaseDatabaseInstance;
import com.example.blooddonationapp.Utils.SharedPreference;

public class ReplyActivity extends AppCompatActivity {

    FirebaseDatabaseInstance rootRef;
    SharedPreference pref;
    String currentUserId, userType;
    String allowReply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        pref = SharedPreference.getInstance();
        rootRef = FirebaseDatabaseInstance.getInstance();

        

        allowReply = getIntent().getStringExtra("allow_reply");

        if(allowReply.equals("yes")){

        }


    }
}