package com.example.bantay.bantay;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

import java.util.List;

//da
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import static android.support.v4.content.ContextCompat.*;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback{

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
// DA
    LocationManager locationManager;
    String provider;
    double dalat,dalng;
    List<Address> addressList;
    //Evacuation center markers
    public Marker SampaguitaGym, BarangkaES, IVCES, KalumpangES, LVictorinoES, MalandayES, MarikinaES, SanRoqueES,
            StoNinoES;

    public MapFragment() {
        // Required empty public constructor
    }

//    DA
//    private void getLocation()
//    {
//        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE)
//    }

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
                                LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM);
                                map.animateCamera(cameraUpdate);
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);

                                //Get location address
                                locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                                provider = locationManager.getBestProvider(new Criteria(), false);

                                final Location location = locationManager.getLastKnownLocation(provider);
                                if(location == null) {
                                    Log.e("ERROR", "Location is null");
                                }
                                dalat = currentLocation.getLatitude();
                                dalng = currentLocation.getLongitude();
                                new GetAddress().execute(String.format("%.4f,%.4f",dalat,dalng));

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

                /* Other geocoder urls (optional)
                String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%.4f,%.4f&sensor=false&key=AIzaSyDvTpjZhv9Qf7Z7xMe1Vb5w08ZOIqtNV7c",lat,lng);
                String url = String.format("http://open.mapquestapi.com/geocoding/v1/reverse?key=2KtQuwfGGdfHxj6ybdgqcC7uFHrgVoJy&location=%.4f,%.4f",lat,lng);
                String url = String.format("https://nominatim.openstreetmap.org/reverse?format=json&lat=%.4f&lon=%.4f",lat,lng);*/

                String url = String.format("https://us1.locationiq.com/v1/reverse.php?key=0b3a97d7f1f654&lat=%.4f&lon=%.4f&format=json",lat,lng);
                response = http.GetHTTPData(url);
                Log.d("testpandebug,doinbg", response);
                Log.d("testpandebug,doinbg", url);
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
                String address = jsonObject.get("display_name").toString();
                Log.d("testpandebug,onpex", address);
                //String address = ((JSONArray)jsonObject.get("results")).getJSONObject(1).get("formatted_address").toString();
                mapAddress = getView().findViewById(R.id.mapAddress);
                mapAddress.setText(address);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}