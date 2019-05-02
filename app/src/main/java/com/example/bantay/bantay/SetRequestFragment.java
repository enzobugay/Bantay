package com.example.bantay.bantay;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;


/**
 * A simple {@link Fragment} subclass.
 */
public class SetRequestFragment extends Fragment {

    public FirebaseDatabase firebaseDatabase;
    public FirebaseAuth firebaseAuth;
    public ProgressDialog progressDialog;

    public SetRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_set_request, container, false);

        SharedPreferences ackflag = this.getActivity().getSharedPreferences("AckFlag", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = ackflag.edit();
        editor.putString("Flag", "false");
        editor.apply();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SetRequestFragment setrequestFragment = (SetRequestFragment) getChildFragmentManager().findFragmentById(R.id.setrequestfragment);

        if(!Connection()){
            buildDialog().show();
        } else {
            setFragment();
        }

    }

    //Check internet connection of device
    public boolean Connection(){

        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            android.net.NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if ((mobile != null && mobile.isConnected()) || (wifi != null && wifi.isConnected()))
                return true;
            else return false;
        } else
            return false;

    }
    //Alert dialog if no internet connection
    public AlertDialog.Builder buildDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle("No Internet Connection");
        builder.setMessage("This feature requires internet connection. Please turn on your internet connection.");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!Connection()){
                    buildDialog().show();
                }
                else{
                    setFragment();
                }
            }
        });
        return builder;
    }

    //Fragment setters
    public void setFragment(){

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        final String newrescuerequest = "/Rescue Requests/New Rescue Requests"; //Database path
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(newrescuerequest);

        databaseReference.child(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    newRescueRequest();
                    progressDialog.dismiss();
                }
                else{
                    setFragmentB();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        String topicuid = firebaseAuth.getUid();
        Log.d("topicuid", topicuid);
        FirebaseMessaging.getInstance().subscribeToTopic(topicuid);
    }

    public void setFragmentB(){

        final String deployedrescuerequest = "/Rescue Requests/Deployed Rescue Requests"; //Database path
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(deployedrescuerequest);

        databaseReference.child(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    deployedRescueRequest();
                    progressDialog.dismiss();
                }
                else{
                    requestFragment();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Go to RequestFragment fragment
    public void requestFragment(){
        RequestFragment requestFragment = new RequestFragment();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_nav, requestFragment);
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    //Go to NewRescueRequest fragment
    public void newRescueRequest(){
        NewRescueRequest newRescueRequest = new NewRescueRequest();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_nav, newRescueRequest);
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    //Go to DeployedRescueRequest fragment
    public void deployedRescueRequest(){
        DeployedRescueRequest deployedRescueRequest = new DeployedRescueRequest();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_nav, deployedRescueRequest);
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
