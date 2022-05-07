package com.example.blooddonationapp.StartFragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.blooddonationapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class PredictionFragment extends Fragment {

    View view;
    EditText input;
    ImageButton send;
    TextView sender;
    TextView bot;
    String[] symptomList = new String[]{
            "itching" ,
            "skin_rash" ,
            "nodal_skin_eruptions" ,
            "continuous_sneezing" ,
            "shivering" ,
            "chills" ,
            "joint_pain" ,
            "stomach_pain" ,
            "acidity" ,
            "ulcers_on_tongue" ,
            "muscle_wasting" ,
            "vomiting" ,
            "burning_micturition" ,
            "spotting_ urination" ,
            "fatigue" ,
            "weight_gain" ,
            "anxiety" ,
            "cold_hands_and_feets" ,
            "mood_swings" ,
            "weight_loss" ,
            "restlessness" ,
            "lethargy" ,
            "patches_in_throat" ,
            "irregular_sugar_level" ,
            "cough" ,
            "high_fever" ,
            "sunken_eyes" ,
            "breathlessness" ,
            "sweating" ,
            "dehydration" ,
            "indigestion" ,
            "headache" ,
            "yellowish_skin",
            "dark_urine" ,
            "nausea" ,
            "loss_of_appetite" ,
            "pain_behind_the_eyes" ,
            "back_pain" ,
            "constipation" ,
            "abdominal_pain" ,
            "diarrhoea" ,
            "mild_fever" ,
            "yellow_urine" ,
            "yellowing_of_eyes" ,
            "acute_liver_failure" ,
            "fluid_overload" ,
            "swelling_of_stomach" ,
            "swelled_lymph_nodes" ,
            "malaise" ,
            "blurred_and_distorted_vision" ,
            "phlegm" ,
            "throat_irritation",
            "redness_of_eyes" ,
            "sinus_pressure" ,
            "runny_nose" ,
            "congestion" ,
            "chest_pain" ,
            "weakness_in_limbs" ,
            "fast_heart_rate" ,
            "pain_during_bowel_movements" ,
            "pain_in_anal_region" ,
            "bloody_stool" ,
            "irritation_in_anus" ,
            "neck_pain" ,
            "dizziness" ,
            "cramps" ,
            "bruising" ,
            "obesity" ,
            "swollen_legs" ,
            "swollen_blood_vessels" ,
            "puffy_face_and_eyes" ,
            "enlarged_thyroid" ,
            "brittle_nails" ,
            "swollen_extremeties" ,
            "excessive_hunger" ,
            "extra_marital_contacts" ,
            "drying_and_tingling_lips",
            "slurred_speech" ,
            "knee_pain" ,
            "hip_joint_pain" ,
            "muscle_weakness" ,
            "stiff_neck" ,
            "swelling_joints",
            "movement_stiffness" ,
            "spinning_movements" ,
            "loss_of_balance" ,
            "unsteadiness" ,
            "weakness_of_one_body_side" ,
            "loss_of_smell" ,
            "bladder_discomfort",
            "foul_smell_of urine" ,
            "continuous_feel_of_urine",
            "passage_of_gases" ,
            "internal_itching" ,
            "toxic_look_(typhos)" ,
            "depression" ,
            "irritability",
            "muscle_pain" ,
            "altered_sensorium",
            "red_spots_over_body",
            "belly_pain" ,
            "abnormal_menstruation",
            "dischromic _patches" ,
            "watering_from_eyes" ,
            "increased_appetite" ,
            "polyuria" ,
            "family_history",
            "mucoid_sputum" ,
            "rusty_sputum" ,
            "lack_of_concentration" ,
            "visual_disturbances" ,
            "receiving_blood_transfusion" ,
            "receiving_unsterile_injections" ,
            "coma" ,
            "stomach_bleeding" ,
            "distention_of_abdomen",
            "history_of_alcohol_consumption",
            "fluid_overload" ,
            "blood_in_sputum" ,
            "prominent_veins_on_calf" ,
            "palpitations" ,
            "painful_walking",
            "pus_filled_pimples",
            "blackheads" ,
            "scurring" ,
            "skin_peeling",
            "silver_like_dusting" ,
            "small_dents_in_nails" ,
            "inflammatory_nails" ,
            "blister" ,
            "red_sore_around_nose",
            "yellow_crust_ooze" ,
            "prognosis"
    };

    ArrayList<String> list = new ArrayList<String>(Arrays.asList(symptomList));
    ArrayList<String> listFinal = new ArrayList<String>();
    RecyclerView rec;
    String edit="";
    ArrayList<String> forStoring = new ArrayList<>();
    String arr[];
    ItemAdapter itemAdapter;
    int replacePosition;
    public PredictionFragment() {
        // Required empty public constructor
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Log.i("beforelast====", arr[arr.length-1]);
            String symptom = intent.getStringExtra("symptom");
            Log.i("replace====", symptom);
            Log.i("beforeEdit====", edit);

            edit="";
            arr[arr.length-1]=symptom;
            for(String s : arr){
                edit=edit+s+" ";
            }


            Log.i("afterEdit====", edit);
            Log.i("afterlast====", arr[arr.length-1]);


            input.setText(edit);

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_prediction, container, false);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-message"));

        input = view.findViewById(R.id.input_message);
        send = view.findViewById(R.id.send_message_btn);

        sender = view.findViewById(R.id.sender_text);
        bot = view.findViewById(R.id.rec_text);

        rec = view.findViewById(R.id.suggest);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        rec.setLayoutManager(linearLayoutManager);

        rec.setHasFixedSize(true);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, ArrayList<String>> h = new HashMap<>();

                HashSet<String> hs = new HashSet<>();

                for(String k : forStoring){
                    hs.add(k);
                }
                ArrayList<String> st = new ArrayList<>();
                for(String z : hs){
                    st.add(z);
                }

                h.put("symptoms", st);

                Gson gson = new Gson();
                String json = gson.toJson(h);
                Log.i("json---", json);



            }
        });

        populateTheList("");

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String ip = input.getText().toString();

                Log.i("called----", ip);
                populateTheList(ip);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return view;
    }

    private void populateTheList(String s) {
        itemAdapter = new ItemAdapter(getContext(), listFinal);

        rec.setAdapter(itemAdapter);
        edit = s;
        listFinal.clear();
        if(edit.length()>1){
            for(String ss : list){
                arr = edit.split(" ");

                Log.i("kl----", String.valueOf(arr.length));
                for(String k : arr){
                    Log.i("k----", k);
                    Log.i("ss----", ss);
                    if(ss.length()>k.length()){
                        if(ss.substring(0, k.length()).toLowerCase().contains(k.toLowerCase())){
                            if(k.length()>1){

                                listFinal.add(ss);

                            }

                        }
                    }else if(ss.length()==k.length()){
                        forStoring.add(k);
                    }

                }

                itemAdapter.notifyDataSetChanged();
            }
        }if(edit.length()==0){
            listFinal.clear();
            itemAdapter.notifyDataSetChanged();
        }

    }
    class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder>{
        View view;
        Context context;
        ArrayList<String> list = new ArrayList<>();

        public ItemAdapter(){

        }public ItemAdapter(Context context, ArrayList<String> list){
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public ItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            view = LayoutInflater.from(context).inflate(R.layout.item_suggest, parent, false);
            return new ItemAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemAdapter.ViewHolder holder, int position) {
            String val = list.get(position);

            holder.suggest.setText(val);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent("custom-message");
                    intent.putExtra("symptom", holder.suggest.getText().toString());
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    forStoring.add(val);
                }
            });
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
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView suggest;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                suggest = itemView.findViewById(R.id.suggest_text);
            }
        }
    }
}
