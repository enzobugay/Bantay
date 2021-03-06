package com.example.bantay.bantay;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AlertNotif3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_notif3);

        Button ok = findViewById(R.id.alert3button);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user != null){

                    SharedPreferences alertflag = getSharedPreferences("AlertFlag", MODE_PRIVATE);
                    SharedPreferences.Editor editor = alertflag.edit();
                    editor.putString("Flag", "true");
                    editor.apply();

                    SharedPreferences ackflag = getSharedPreferences("AckFlag", MODE_PRIVATE);
                    SharedPreferences.Editor editorb = ackflag.edit();
                    editorb.putString("Flag", "false");
                    editorb.apply();

                    finish();
                    startActivity(new Intent(AlertNotif3.this, HomeActivity.class));
                }
                else{
                    finish();
                    startActivity(new Intent(AlertNotif3.this, MainActivity.class));
                }
            }
        });
    }
}
