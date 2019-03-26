package com.example.bantay.bantay;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AcknowledgeNotif extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acknowledge_notif);

        Button ok = findViewById(R.id.ackbutton);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user != null){
                    finish();
                    startActivity(new Intent(AcknowledgeNotif.this, MainActivity.class));
                }
                else{
                    finish();
                    startActivity(new Intent(AcknowledgeNotif.this, MainActivity.class));
                }
            }
        });
    }
}
