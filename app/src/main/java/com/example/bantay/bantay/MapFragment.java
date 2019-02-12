package com.example.bantay.bantay;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
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
import android.widget.TextView;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    //vars


    GoogleMap map;
    FusedLocationProviderClient fusedLocationProviderClient;
    float DEFAULT_ZOOM = 17f;
    public Boolean mLocationPermissionsGranted = false;
    public final static int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    public final static int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    //Bounds compass(sw, ne)
    public LatLngBounds MARIKINA = new LatLngBounds(new LatLng(14.618055, 121.079299), new LatLng(14.673361, 121.131398));
    public TextView mapAddress;
    Geocoder geocoder;
    List<Address> addressList;
    //Evacuation center markers
    public Marker SampaguitaGym, BarangkaES, IVCES, KalumpangES, LVictorinoES, MalandayES, MarikinaES, SanRoqueES,
            StoNinoES;

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

    @Override
    public void onResume(){
        super.onResume();
        initMap();
    }

    /*public boolean checkMapServices(){
        if(isMapsEnabled()){
            return true;
        }
        return false;
    }*/

    public void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
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

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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


    //Actual map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Google maps style with default landmarks etc..
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_style_json)); //JSON map style in /res/raw
        map = googleMap;
        //Current location of user
        map.setLatLngBoundsForCameraTarget(MARIKINA);
        moveCamera(new LatLng(14.647329, 121.104834), 10f);

        if(isMapsEnabled()) {
            if (mLocationPermissionsGranted) {
                getDeviceLocation();
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    getLocationPermission();
                }
                map.setMyLocationEnabled(true);
                map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        getDeviceLocation();
                        return false;
                    }
                });
            }
        }

        //Evacuation center markers
        map.getUiSettings().setMapToolbarEnabled(false); //To remove "directions" button on click of marker
        SampaguitaGym = map.addMarker(new MarkerOptions().position(new LatLng(14.648983, 121.093749)).title("Sampaguita Gym").snippet("Sampaguita St., Malanday"));
        BarangkaES = map.addMarker(new MarkerOptions().position(new LatLng(14.633379, 121.081927)).title("Barangka Elementary School").snippet("Boni Ave., Barangka"));
        IVCES = map.addMarker(new MarkerOptions().position(new LatLng(14.621713, 121.080356)).title("Industrial Valley Elementary School").snippet("O. De Guzman St., Sitio Olandez IVC"));
        KalumpangES = map.addMarker(new MarkerOptions().position(new LatLng(14.622279, 121.090296)).title("Kalumpang Elementary School").snippet("Kagitingan St., Kalumpang"));
        MalandayES = map.addMarker(new MarkerOptions().position(new LatLng(14.650276, 121.094389)).title("Malanday Elementary School").snippet("Malaya St., Malanday"));
        LVictorinoES = map.addMarker(new MarkerOptions().position(new LatLng(14.635265, 121.090290)).title("Leodegario Victorino Elementary School").snippet("A. Bonifacio Ave., J. dela Peña"));
        MarikinaES = map.addMarker(new MarkerOptions().position(new LatLng(14.631120, 121.097553)).title("Marikina Elementary School").snippet("Munding Ave., Sta. Elena"));
        SanRoqueES = map.addMarker(new MarkerOptions().position(new LatLng(14.622896, 121.097122)).title("San Roque Elementary School").snippet("Abad Santos, San Roque"));
        StoNinoES = map.addMarker(new MarkerOptions().position(new LatLng(14.637888, 121.096434)).title("Sto. Niño Elementary School").snippet("Guerilla St., Sto. Niño"));
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
            if(isMapsEnabled()) {
                if (mLocationPermissionsGranted) {
                    final Task location = fusedLocationProviderClient.getLastLocation();
                    location.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                Location currentLocation = (Location) task.getResult();
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);

                                geocoder = new Geocoder(getActivity(), Locale.getDefault());
                                try {
                                    addressList = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                                    //  String address = addressList.get(0).getAddressLine(0);

                                    String addressline = addressList.get(0).getAddressLine(0);
                                    String thoroughfare = addressList.get(0).getThoroughfare(); //Street
                                    String locality = addressList.get(0).getLocality(); //City or Marikina
                                    String adminarea = addressList.get(0).getAdminArea(); //Capital or Metro Manila
                                    // String fulladdress = thoroughfare+", "+locality+", "+adminarea;
                                    String fulladdress = addressline;
                                    mapAddress = getView().findViewById(R.id.mapAddress);
                                    mapAddress.setText(fulladdress);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });

                }
            }
        }catch(SecurityException e){
            Log.d("getDeviceLocation", "Device location not working!");
        }
    }


}