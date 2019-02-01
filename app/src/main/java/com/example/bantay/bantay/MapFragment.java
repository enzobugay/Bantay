package com.example.bantay.bantay;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {


    GoogleMap map;
    FusedLocationProviderClient fusedLocationProviderClient;
    float DEFAULT_ZOOM = 17f;
    public Boolean mLocationPermissionsGranted = false;
    public final static int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    //Bounds compass(sw, ne)
    public LatLngBounds MARIKINA = new LatLngBounds(new LatLng(14.618055, 121.079299), new LatLng(14.673361, 121.131398));
    //Evacuation center markers
    public Marker SampaguitaGym;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        getLocationPermission();
        return view;
    }

    //Get device permission for getDeviceLocation method
    public void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("getLocationPermission", "FINE_LOCATION IF IS WORKING!");

            if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d("getLocationPermission", "COARSE_LOCATION IF IS WORKING!");
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                Log.d("getLocationPermission", "COARSE_LOCATION WORKING!");
                requestPermissions(permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
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
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d("onRequestPermissions", "PERMISSION FAILED!");
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                    Log.d("onRequestPermissions", "PERMISSION GRANTED!");
                    //Initialize our map
                    initMap();
                }
            }
        }
    }

    //Initiate map
    public void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    //Show map in fragment
   /* @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }*/

    //Actual map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Google maps style with default landmarks etc..
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_style_json)); //JSON map style in /res/raw
        map = googleMap;
        //Current location of user
        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                getLocationPermission();
            }
            map.setMyLocationEnabled(true);
        }

        //Evacuation center markers
        map.getUiSettings().setMapToolbarEnabled(false); //To remove "directions" button on click of marker
        SampaguitaGym = map.addMarker(new MarkerOptions().position(new LatLng(14.648983, 121.093749)).title("Sampaguita Gym"));
    }
    //Map camera method
    public void moveCamera(LatLng latLng, float zoom){

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        //Map boundaries
        map.setLatLngBoundsForCameraTarget(MARIKINA);
        //Camera zoom boundary
        map.setMinZoomPreference(13f);
    }

    //Get device location method
    public void getDeviceLocation(){

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try{
            if(mLocationPermissionsGranted){
                final Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful() && task.getResult() != null){
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
                        }
                        else{
                            moveCamera(new LatLng(14.647329, 121.104834), 10f);
                            Toast.makeText(getActivity(), "Can't get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        }catch(SecurityException e){
            Log.d("getDeviceLocation", "Device location not working!");
        }
    }


}

