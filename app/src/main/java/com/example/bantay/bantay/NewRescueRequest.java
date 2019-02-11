package com.example.bantay.bantay;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewRescueRequest extends Fragment {

    public TextView requestfirstname, requestcontactnumber, requestlocation, requestlandmarks,
            requestpax, requestvulnerability, requestspecification;
    public Button urgent;
    public FirebaseAuth firebaseAuth;
    public FirebaseDatabase firebaseDatabase;

    public NewRescueRequest() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_new_rescue_request, container, false);

        return view;
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

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
