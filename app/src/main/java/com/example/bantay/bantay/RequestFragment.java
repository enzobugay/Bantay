package com.example.bantay.bantay;


import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SearchEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static android.support.v4.content.ContextCompat.checkSelfPermission;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {

    public TextView requestfirstname, requestcontactnumber, requestlocation;

    public EditText requestlandmarks,requestspecific, requestpax;

    public CheckBox requestpwd, requestsenior, requestinfant, requestmedical, requestother;

    public String rfirstname, rfirstnameb, rlastname, requestlastname, rcontactnum, rlocation, rlandmarks, rpax, rpwd, rsenior,
            rinfant, rmedical, rother, rspecific, notvulnerable, address, barangayaddress;

    public String notpriority = "true";
    public String barangaypriority = "false";

    public FirebaseAuth firebaseAuth;
    public FirebaseDatabase firebaseDatabase;

    public Boolean mLocationPermissionsGranted = false;
    public final static int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    public final static int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationManager locationManager;
    String provider;
    double dalat,dalng;

    int count = 0;
    public ProgressDialog progressDialog;

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_request, container, false);
        getLocationPermission();

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
                            setRequestFragment();
                            Toast.makeText(getActivity(), "You sent a rescue request", Toast.LENGTH_SHORT).show();
                        }
                    });
                    alertDialog.show();
                }

            }
        });

        //Refresh get device location address (will go to mapfragment)
        Button refresh = view.findViewById(R.id.requestlocrefresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMapFragment();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RequestFragment requestFragment = (RequestFragment) getChildFragmentManager().findFragmentById(R.id.request);
        loadEntries();
        if(isMapsEnabled()) {
            if (mLocationPermissionsGranted) {
                getDeviceLocation();
            }
        }
    }


    /*
    ------------------------------GPS LOCATION AND VALIDATION METHODS-------------------------------
    */

    public void buildAlertMessageNoGps() {
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager)getActivity().getSystemService(getActivity().LOCATION_SERVICE);

        if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("isMapsEnabled", "GPS VALIDATION IS WORKING!");
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }
    //Get device permission for getDeviceLocation method
    public void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};

        if (checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("getLocationPermission", "FINE_LOCATION IF IS WORKING!");
            mLocationPermissionsGranted = true;
            //initMap();
        } else {
            Log.d("getLocationPermission", "FINE_LOCATION WORKING!");
            requestPermissions(permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionsGranted = false;
        Log.d("onRequestPermissions", "THIS FUNCTION IS CALLED!");
        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mLocationPermissionsGranted = true;
                    Log.d("onRequestPermissions", "PERMISSION GRANTED!");
                    //Initialize our map
                    getDeviceLocation();
                }
            }
        }
    }
    //Get location address
    public void getDeviceLocation(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try{
            if(isMapsEnabled()) {
                if (mLocationPermissionsGranted) {
                    final Task location = fusedLocationProviderClient.getLastLocation();
                    location.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                Location currentLocation = (Location) task.getResult();
                                locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                                provider = locationManager.getBestProvider(new Criteria(), false);
                                final Location location = locationManager.getLastKnownLocation(provider);
                                if(location == null) {
                                    Log.e("ERROR", "Location is null");
                                }
                                dalat = currentLocation.getLatitude();
                                dalng = currentLocation.getLongitude();
                                new RequestFragment.GetAddress().execute(String.format("%.4f,%.4f",dalat,dalng));

                            }
                        }
                    });

                }
            }
        }catch(SecurityException e){
            Log.d("getDeviceLocation", "Device location not working!");
        }
    }
    //Get location address
    private class GetAddress extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {
            try{
                double lat = Double.parseDouble(strings[0].split(",")[0]);
                double lng = Double.parseDouble(strings[0].split(",")[1]);
                String response;
                HttpDataHandler http = new HttpDataHandler();
                String url = String.format("https://us1.locationiq.com/v1/reverse.php?key=0b3a97d7f1f654&lat=%.4f&lon=%.4f&format=json",lat,lng);
                response = http.GetHTTPData(url);
                Log.d("testpandebug,requestbg", response);
                Log.d("testpandebug,requestbg", url);
                return response;
            }
            catch (Exception ex)
            {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject jsonObject = new JSONObject(s);

                //Get full address
                address = jsonObject.get("display_name").toString();
                Log.d("testpandebug,requestpex", address);
                requestlocation = getView().findViewById(R.id.requestlocation);
                requestlocation.setText(address);

                //Get and set barangay
                barangayaddress = jsonObject.getJSONObject("address").get("suburb").toString();
                Log.d("testpandebug,requestpex", barangayaddress);
                if(barangayaddress.toLowerCase().equals("malanday")){
                    barangaypriority = "true";
                }
                Log.d("testpandebug, brgprior", barangaypriority);



            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    /*
    ----------------------------------RESCUE REQUEST METHODS----------------------------------------
    */


    //Get name, contact number, and location of user
    private void loadEntries(){

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        requestfirstname = (TextView)getView().findViewById(R.id.requestfirstname);
        requestcontactnumber = (TextView)getView().findViewById(R.id.requestcontactnumber);

        String path = "/Users/Residents";
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(path); //Residents path

        databaseReference.child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                rfirstnameb = dataSnapshot.child("userFirstName").getValue(String.class);
                requestlastname = dataSnapshot.child("userLastName").getValue(String.class);
                String rcontactnumber = dataSnapshot.child("userContactNumber").getValue(String.class);
                requestfirstname.setText(rfirstnameb + " " + requestlastname);
                requestcontactnumber.setText(rcontactnumber);

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


    //Get request entries of user

        requestlandmarks = getView().findViewById(R.id.requestlandmarks);
        requestpax = getView().findViewById(R.id.requestpax);
        requestpwd = getView().findViewById(R.id.requestpwd);
        requestsenior = getView().findViewById(R.id.requestsenior);
        requestinfant = getView().findViewById(R.id.requestinfant);
        requestmedical = getView().findViewById(R.id.requestmedical);
        requestother = getView().findViewById(R.id.requestother);
        requestspecific = getView().findViewById(R.id.requestspecification);

        //Set initial values of checkboxes
        rpwd = "false";
        rsenior = "false";
        rinfant = "false";
        rmedical = "false";
        rother = "false";
        notvulnerable = "true";

        //Set text to checkboxes
        requestpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(requestpwd.isChecked()){
                    rpwd = "true";
                    notvulnerable = "false";
                }else{
                    rpwd = "false";
                    notvulnerable = "true";
                }
            }
        });

        requestsenior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(requestsenior.isChecked()){
                    rsenior = "true";
                    notvulnerable = "false";
                }else{
                    rsenior = "false";
                    notvulnerable = "true";
                }
            }
        });

        requestinfant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(requestinfant.isChecked()){
                    rinfant = "true";
                    notvulnerable = "false";
                }else{
                    rinfant = "false";
                    notvulnerable = "true";
                }
            }
        });

        requestmedical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(requestmedical.isChecked()){
                    rmedical = "true";
                    notvulnerable = "false";
                }else{
                    rmedical = "false";
                    notvulnerable = "true";
                }
            }
        });

        requestother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(requestother.isChecked()){
                    rother = "true";
                    notvulnerable = "false";
                }else{
                    rother = "false";
                    notvulnerable = "true";
                }
            }
        });

    }


    //Validate entry fields
    private Boolean validate(){
        Boolean result = false;

        rlandmarks = requestlandmarks.getText().toString().trim();
        rpax = requestpax.getText().toString().trim();
        rspecific = requestspecific.getText().toString().trim();
        rfirstname = rfirstnameb;
        rlastname = requestlastname;
        rcontactnum = requestcontactnumber.getText().toString();
        rlocation = requestlocation.getText().toString();


        if (rlandmarks.isEmpty() || rpax.isEmpty()) {
            Log.d("BOOLEAN VALIDATE", "IF WORKING");
            Toast.makeText(getActivity(), "Please fill up required fields", Toast.LENGTH_SHORT).show();
            Log.d("BOOLEAN VALIDATE", "PASSED THROUGH TOAST");
        }
        else if (rmedical.equals("true") && rother.equals("false") && rspecific.isEmpty()){
            Toast.makeText(getActivity(), "Please specify medical conditions", Toast.LENGTH_SHORT).show();
        }
        else if (rother.equals("true") && rmedical.equals("false") && rspecific.isEmpty()){
            Toast.makeText(getActivity(), "Please specify your other vulnerability", Toast.LENGTH_SHORT).show();
        }
        else if (rmedical.equals("true") && rother.equals("true") && rspecific.isEmpty()){
            Toast.makeText(getActivity(), "Please specify medical conditions & other vulnerability", Toast.LENGTH_SHORT).show();
        }
        else if(rlocation.isEmpty()){
            Toast.makeText(getActivity(), "No location provided, please refresh location", Toast.LENGTH_SHORT).show();
        }
        else if(!rlocation.isEmpty() && !rlocation.toLowerCase().contains("marikina")){
            Toast.makeText(getActivity(), "You cannot send a request outside Marikina City!", Toast.LENGTH_SHORT).show();
        }
        else{
            result = true;
        }
        return result;
    }

    //Rescue request details to database
    private void sendRescueRequest(){

        //Set value for notPriority node
        if(notvulnerable.equals("false") || barangaypriority.equals("true")){
            notpriority = "false";
        }
        Log.d("testpandebug, notvul", notvulnerable);
        Log.d("testpandebug, brgprior", barangaypriority);
        Log.d("testpandebug, priority", notpriority);

        String path = "/Rescue Requests/New Rescue Requests"; //Database path
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(path);
        RequestEntries requestEntries = new RequestEntries(rfirstname, rlastname, rcontactnum, rlocation,
                rlandmarks, rpax, rpwd, rsenior, rinfant, rmedical, rother, rspecific);
        databaseReference.child(firebaseAuth.getUid()).setValue(requestEntries);
        //Child and Values not included in RequestEntries.class
        databaseReference.child(firebaseAuth.getUid()).child("requestTimestamp").setValue(ServerValue.TIMESTAMP);
        databaseReference.child(firebaseAuth.getUid()).child("requestDateTimeDeployed").setValue("");
        databaseReference.child(firebaseAuth.getUid()).child("requestDateTimeRescued").setValue("");
        databaseReference.child(firebaseAuth.getUid()).child("urgentFlag").setValue("0");
        databaseReference.child(firebaseAuth.getUid()).child("rescueTeam").setValue("");
        databaseReference.child(firebaseAuth.getUid()).child("notPriority").setValue(notpriority);
        databaseReference.child(firebaseAuth.getUid()).child("receivedTimestamp").setValue("");

    }

    //Go to NewRescueRequest fragment
    public void setRequestFragment(){
        SetRequestFragment setRequestFragment = new SetRequestFragment();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_nav, setRequestFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    //Go to MapFragment fragment
    public void setMapFragment(){
        MapFragment mapFragment = new MapFragment();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_nav, mapFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        /*BottomNavigationView bottomNavigationView = getView().findViewById(R.id.bottom_nav);
        bottomNavigationView.getMenu().findItem(R.id.nav_map).setChecked(true);*/
    }


}
