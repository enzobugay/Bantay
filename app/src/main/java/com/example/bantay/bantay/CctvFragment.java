package com.example.bantay.bantay;


import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class CctvFragment extends Fragment {

    String cctv = "http://121.58.202.110:8080/jpeg?cam=4";

    public VideoView cctvfrag;

    public CctvFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_cctv, container, false);

        cctvfrag = rootView.findViewById(R.id.cctvfragment);
        MediaController mediaController = new MediaController(getActivity());
        VideoView videoView = cctvfrag;

        Uri uri = Uri.parse(cctv);
        videoView.setVideoURI(uri);
        videoView.setMediaController(mediaController);
        videoView.start();

        return rootView;
    }




}
