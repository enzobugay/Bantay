package com.example.bantay.bantay;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private TextView profilefirstname, profileaddress, profilebarangay, profilenumber, profileemail;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    int count = 0;
    public ProgressDialog progressDialog;

    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        //Edit Account Button
        Button edit = view.findViewById(R.id.editaccountbutton);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment();
            }
        });

        //Logout Button
        Button logout = view.findViewById(R.id.logoutbutton);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Alert dialog
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setCancelable(false);
                alertDialog.setMessage("Are you sure you want to logout?");
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing
                    }
                });
                alertDialog.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firebaseAuth.signOut();
                        String topicuid = firebaseAuth.getUid();
                        Log.d("topicuid", topicuid);
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(topicuid);
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                    }
                });
                alertDialog.show();

            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AccountFragment accountFragment = (AccountFragment) getChildFragmentManager().findFragmentById(R.id.account);
        loadEntries();

    }

    //Load Account Details Method
    private void loadEntries(){

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        profilefirstname = (TextView)getView().findViewById(R.id.tvfirstname);
        profileaddress = (TextView)getView().findViewById(R.id.tvaddress);
        profilebarangay = (TextView)getView().findViewById(R.id.tvbarangay);
        profilenumber = (TextView)getView().findViewById(R.id.tvcontactnumber);
        profileemail = (TextView)getView().findViewById(R.id.tvemail);

        String path = "/Users/Residents";
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(path);

        databaseReference.child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AccountDetails accountDetails = dataSnapshot.getValue(AccountDetails.class);
                profilefirstname.setText(accountDetails.getUserFirstName() + " " + accountDetails.getUserLastName());
                profileaddress.setText(accountDetails.getUserAddress());
                profilebarangay.setText(accountDetails.getUserBarangay());
                profilenumber.setText(accountDetails.getUserContactNumber());
                profileemail.setText(accountDetails.getUserEmail());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        databaseReference.child(firebaseAuth.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                count++;

                if(count >= dataSnapshot.getChildrenCount()){
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //AccountFragment to EditAccountFragment method
    private void setFragment() {
        EditAccountFragment editAccountFragment = new EditAccountFragment();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_nav, editAccountFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

}
