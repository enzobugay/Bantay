package com.example.bantay.bantay;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {

    public TextView requestfirstname, requestlastname, requestcontactnumber, requestlocation;

    public EditText requestlandmarks,requestspecific, requestpax;

    public CheckBox requestpwd, requestsenior, requestinfant, requestmedical, requestother;

    public String rfirstname, rlastname, rcontactnum, rlocation, rlandmarks, rpax, rpwd, rsenior,
            rinfant, rmedical, rother, rspecific, rdatetime;

    public FirebaseAuth firebaseAuth;
    public FirebaseDatabase firebaseDatabase;

    Geocoder geocoder;
    List<Address> addressList;

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_request, container, false);

        //Send Request button
        Button request = (Button)view.findViewById(R.id.requestbutton);
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    //Alert dialog
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setCancelable(false);
                    alertDialog.setTitle("Send Rescue Request?");
                    alertDialog.setMessage("Your rescue request will be sent to Marikina City Rescue 161. Send?");
                    alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Do nothing
                        }
                    });
                    alertDialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendRescueRequest();
                            Toast.makeText(getActivity(), "You sent a rescue request", Toast.LENGTH_SHORT).show();
                        }
                    });
                    alertDialog.show();
                }

            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RequestFragment requestFragment = (RequestFragment) getChildFragmentManager().findFragmentById(R.id.request);
        loadEntries();
    }

    //Get name, contact number, and location of user
    private void loadEntries(){

        requestfirstname = (TextView)getView().findViewById(R.id.requestfirstname);
        requestlastname = (TextView)getView().findViewById(R.id.requestlastname);
        requestcontactnumber = (TextView)getView().findViewById(R.id.requestcontactnumber);
        requestlocation = (TextView)getView().findViewById(R.id.requestlocation);


        String path = "/Users/Residents";
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(path); //Residents path

        databaseReference.child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String rfirstname = dataSnapshot.child("userFirstName").getValue(String.class);
                String rlastname = dataSnapshot.child("userLastName").getValue(String.class);
                String rcontactnumber = dataSnapshot.child("userContactNumber").getValue(String.class);

                requestfirstname.setText(rfirstname);
                requestlastname.setText(rlastname);
                requestcontactnumber.setText(rcontactnumber);
                requestlocation.setText("TEST");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    //Get request entries of user

        requestlandmarks = getView().findViewById(R.id.requestlandmarks);
        requestpax = getView().findViewById(R.id.requestpax);
        requestpwd = getView().findViewById(R.id.requestpwd);
        requestsenior = getView().findViewById(R.id.requestsenior);
        requestinfant = getView().findViewById(R.id.requestinfant);
        requestmedical = getView().findViewById(R.id.requestmedical);
        requestother = getView().findViewById(R.id.requestother);
        requestspecific = getView().findViewById(R.id.requestspecification);

        //Date and time
        SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("MM-dd-yyyy' 'hh:mm:ss a"); //format (https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html)
        rdatetime = ISO_8601_FORMAT.format(new Date());

        //Set initial values of checkboxes
        rpwd = "false";
        rsenior = "false";
        rinfant = "false";
        rmedical = "false";
        rother = "false";

        //Set text to checkboxes
        requestpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(requestpwd.isChecked()){
                    rpwd = "true";
                }else{
                    rpwd = "false";
                }
            }
        });

        requestsenior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(requestsenior.isChecked()){
                    rsenior = "true";
                }else{
                    rsenior = "false";
                }
            }
        });

        requestinfant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(requestinfant.isChecked()){
                    rinfant = "true";
                }else{
                    rinfant = "false";
                }
            }
        });

        requestmedical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(requestmedical.isChecked()){
                    rmedical = "true";
                }else{
                    rmedical = "false";
                }
            }
        });

        requestother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(requestother.isChecked()){
                    rother = "true";
                }else{
                    rother = "false";
                }
            }
        });

    }


    //Validate entry fields
    private Boolean validate(){
        Boolean result = false;

        rlandmarks = requestlandmarks.getText().toString();
        rpax = requestpax.getText().toString();
        rspecific = requestspecific.getText().toString();
        rfirstname = requestfirstname.getText().toString();
        rlastname = requestlastname.getText().toString();
        rcontactnum = requestcontactnumber.getText().toString();
        rlocation = requestlocation.getText().toString();


        if (rlandmarks.isEmpty() || rpax.isEmpty()) {
            Log.d("BOOLEAN VALIDATE", "IF WORKING");
            Toast.makeText(getActivity(), "Please fill up required fields", Toast.LENGTH_SHORT).show();
            Log.d("BOOLEAN VALIDATE", "PASSED THROUGH TOAST");
        }
        else if (requestmedical.isChecked() && rspecific.isEmpty()){
            Toast.makeText(getActivity(), "Please specify medical conditions", Toast.LENGTH_SHORT).show();
        }
        else if (requestother.isChecked() && rspecific.isEmpty()){
            Toast.makeText(getActivity(), "Please specify your other vulnerability", Toast.LENGTH_SHORT).show();
        }
        else if (requestother.isChecked() && requestmedical.isChecked() && rspecific.isEmpty()){
            Toast.makeText(getActivity(), "Please specify medical conditions & other vulnerability", Toast.LENGTH_SHORT).show();
        }
        else{
            result = true;
        }
        return result;
    }

    //Rescue request details to database
    private void sendRescueRequest(){
        String path = "/Rescue Requests/New Rescue Requests"; //Database path
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(path);
        RequestEntries requestEntries = new RequestEntries(rfirstname, rlastname, rcontactnum, rlocation,
                rlandmarks, rpax, rpwd, rsenior, rinfant, rmedical, rother, rspecific);
        databaseReference.child(firebaseAuth.getUid()).setValue(requestEntries);
        databaseReference.child(firebaseAuth.getUid()).child("requestDateTime").setValue(rdatetime);
        databaseReference.child(firebaseAuth.getUid()).child("UrgentFlag").setValue("0");

    }
}
