package com.example.bantay.bantay;


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

        webView = view.findViewById(R.id.cctvfragment);
        webView.loadUrl(httpLiveUrl);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CctvFragment cctvFragment = (CctvFragment) getChildFragmentManager().findFragmentById(R.id.cctv);

    }
}


