package com.example.bantay.bantay;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.messaging.FirebaseMessaging;


public class HomeActivity extends AppCompatActivity {


    //fragments
    private BottomNavigationView bottom_nav;
    private FrameLayout frameLayout;
    private MapFragment mapFragment;
    private CctvFragment cctvFragment;
    private SetRequestFragment setRequestFragment;
    //private ReportFragment reportFragment;
    private AccountFragment accountFragment;

    //google play servies check
    private static final String TAG = "HomeActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences alertflag = getSharedPreferences("AlertFlag", MODE_PRIVATE);
        String flag = alertflag.getString("Flag", "");

        SharedPreferences ackflag = getSharedPreferences("AckFlag", MODE_PRIVATE);
        String flagb = ackflag.getString("Flag", "");

        //fragment tabs
        frameLayout = (FrameLayout)findViewById(R.id.frame_nav);
        bottom_nav = (BottomNavigationView)findViewById(R.id.bottom_nav);

        mapFragment = new MapFragment();
        cctvFragment = new CctvFragment();
        setRequestFragment = new SetRequestFragment();
       // reportFragment = new ReportFragment();
        accountFragment = new AccountFragment();

        //default fragment
        if(flag.equals("true")){
            setFragment(cctvFragment);
        }
        else if(flagb.equals("true")){
            setFragment(setRequestFragment);
        }else {
            setFragment(mapFragment);
        }
        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_nav);
        BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);


        bottom_nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_nav);
                BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);

                switch (item.getItemId()){

                    case R.id.nav_map:
                        setFragment(mapFragment);
                        return true;
                    case R.id.nav_cctv:
                        setFragment(cctvFragment);
                        return true;
                    case R.id.nav_request:
                        setFragment(setRequestFragment);
                        return true;
                   /* case R.id.nav_report:
                        setFragment(reportFragment);
                        return true;*/
                    case R.id.nav_account:
                        setFragment(accountFragment);
                        return true;
                        default:
                            return false;
                }

            }
        });
    }

    public void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_nav, fragment);
        fragmentTransaction.commit();


    }

    //google play services check
    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(HomeActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //
            Log.d(TAG, "isServicesOK: Google play services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "isServicesOK: An error occured but can be fixed");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(HomeActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
