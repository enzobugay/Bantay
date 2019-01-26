package com.example.bantay.bantay;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailforgot;
    private Button reset;
    private TextView back;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailforgot = (EditText)findViewById(R.id.forgotemail);
        reset = (Button)findViewById(R.id.forgotreset);
        back = (TextView)findViewById(R.id.forgotback);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        //Reset button
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String useremail = emailforgot.getText().toString().trim();

                if(useremail.equals("")){
                    Toast.makeText(ForgotPasswordActivity.this, "Enter your email address", Toast.LENGTH_LONG).show();
                }
                else{
                    //Reset method
                    progressDialog.setMessage("Please wait...");
                    progressDialog.show();

                    firebaseAuth.sendPasswordResetEmail(useremail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                progressDialog.dismiss();
                                Toast.makeText(ForgotPasswordActivity.this, "Password reset email sent!", Toast.LENGTH_LONG).show();
                                finish();
                                startActivity(new Intent(ForgotPasswordActivity.this, MainActivity.class));
                            }
                            else{
                                progressDialog.dismiss();
                                Toast.makeText(ForgotPasswordActivity.this, "Password reset error!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

        //Back button
        back.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(ForgotPasswordActivity.this, MainActivity.class));
        }
         });

    }
}
