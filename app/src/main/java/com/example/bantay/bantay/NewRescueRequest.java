package com.example.bantay.bantay;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewRescueRequest extends Fragment {

    public TextView requestfirstname, requestcontactnumber, requestlocation, requestlandmarks,
            requestpax, requestvulnerability, requestspecification, newrequesttv;
    public Button urgent;
    public String urgentflag, notpriority;
    public FirebaseAuth firebaseAuth;
    public FirebaseDatabase firebaseDatabase;
    public Handler handler = new Handler();
    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            urgent.setEnabled(true);
            handler.postDelayed(this, 5000);
        }
    };

    public NewRescueRequest() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_new_rescue_request, container, false);
            urgent = view.findViewById(R.id.urgentbtn);
            newrequesttv = view.findViewById(R.id.newrequesttv);
             urgent.setVisibility(View.VISIBLE);
             urgent.setEnabled(false);
             handler.postDelayed(runnable, 5000);
             urgent.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {

                     //Alert dialog
                     AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                     alertDialog.setCancelable(false);
                     alertDialog.setTitle("Send an urgent notification?");
                     alertDialog.setMessage("An urgent notification of your request will be sent to Marikina City Rescue 161. Send?");
                     alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                             //Do nothing
                         }
                     });
                     alertDialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                             urgentFlag();
                             urgent.setEnabled(false);
                             urgent.setVisibility(View.INVISIBLE);
                             newrequesttv.setText("You sent an urgent notification");
                             newrequesttv.setTextColor(Color.GREEN);
                         }
                     });
                     alertDialog.show();


                 }
             });

        return view;
    }

    @Override
    public void onPause(){
        handler.removeCallbacks(runnable);
        super.onPause();

    }

    @Override
    public void onResume(){
        handler.postDelayed(runnable, 5000);
        super.onResume();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NewRescueRequest newRescueRequest = (NewRescueRequest) getChildFragmentManager().findFragmentById(R.id.newrescuerequest);
        loadEntries();
    }


    private void loadEntries(){

        requestfirstname = getView().findViewById(R.id.newrequestfirstname);
        requestcontactnumber = getView().findViewById(R.id.newrequestcontactnum);
        requestlocation = getView().findViewById(R.id.newrequestlocation);
        requestlandmarks = getView().findViewById(R.id.newrequestlandmarks);
        requestpax = getView().findViewById(R.id.newrequestpax);
        requestvulnerability = getView().findViewById(R.id.newrequestvulnerability);
        requestspecification = getView().findViewById(R.id.newrequestspecification);

        String path = "/Rescue Requests/New Rescue Requests";
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(path);

        databaseReference.child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                RequestEntries requestEntries = dataSnapshot.getValue(RequestEntries.class);
                requestfirstname.setText(requestEntries.getRequestFirstName() + " " + requestEntries.getRequestLastName());
                requestcontactnumber.setText(requestEntries.getRequestContactNumber());
                requestlocation.setText(requestEntries.getRequestLocation());
                requestlandmarks.setText(requestEntries.getRequestLandmarks());
                requestpax.setText(requestEntries.getRequestPax());
                requestspecification.setText(requestEntries.getRequestSpecific());
                urgentflag = dataSnapshot.child("urgentFlag").getValue().toString();
                notpriority = dataSnapshot.child("notPriority").getValue().toString();

                Log.d("urgentbutton", urgentflag);
                if(urgentflag.equals("1")){
                    urgent.setEnabled(false);
                    urgent.setVisibility(View.INVISIBLE);
                    newrequesttv.setText("You sent an urgent notification");
                    newrequesttv.setTextColor(Color.GREEN);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    //Set UrgentFlag value (database child)
    public void urgentFlag(){
        String path = "/Rescue Requests/New Rescue Requests";
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(path);
        databaseReference.child(firebaseAuth.getUid()).child("urgentFlag").setValue("1");
        if(notpriority.equals("true")){
            databaseReference.child(firebaseAuth.getUid()).child("notPriority").setValue("false");
        }
    }



}
