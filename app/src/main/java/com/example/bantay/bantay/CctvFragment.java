package com.example.bantay.bantay;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;


import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class CctvFragment extends Fragment {


    WebView webView;
    String httpLiveUrl = "http://121.58.202.110:8080/jpeg?cam=4";

    public CctvFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cctv, container, false);

        SharedPreferences ackflag = this.getActivity().getSharedPreferences("AlertFlag", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = ackflag.edit();
        editor.putString("Flag", "false");
        editor.apply();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CctvFragment cctvFragment = (CctvFragment) getChildFragmentManager().findFragmentById(R.id.cctv);

        if(!Connection()){
            buildDialog().show();
        } else {
            playStream();
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
                    playStream();
                }
            }
        });
        return builder;
    }
    //Play video stream
    public void playStream(){

        webView = getView().findViewById(R.id.cctvfragment);
        webView.loadUrl(httpLiveUrl);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
    }
}


