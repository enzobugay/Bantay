package com.example.bantay.bantay;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private EditText email, password;
    private Button login;
    private TextView forgotpass, signup;
    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    public Handler handler = new Handler();
    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.d("LOGIN DISABLER", "PASSED");
            login.setEnabled(true);
        }
    };

    int attemptCounter = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = (EditText)findViewById(R.id.loginemail);
        password = (EditText)findViewById(R.id.loginpassword);
        login = (Button)findViewById(R.id.loginbutton);
        forgotpass = (TextView)findViewById(R.id.forgotpassword);
        signup = (TextView)findViewById(R.id.signup);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        FirebaseMessaging.getInstance().subscribeToTopic("all");

        progressDialog = new ProgressDialog(this);

        //User no need to re-login, if logged in
       if(user != null){
            finish();
            startActivity(new Intent(MainActivity.this, HomeActivity.class));

        }

        //Button listener
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uemail = email.getText().toString().trim();
                String epassword = password.getText().toString().trim();
                if(uemail.isEmpty() ||(epassword.isEmpty())){
                    Toast.makeText(MainActivity.this, "Enter your email address and password", Toast.LENGTH_LONG).show();
                }
                else{
                    validate(email.getText().toString(), password.getText().toString());
                }

            }
        });


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });

        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ForgotPasswordActivity.class));
            }
        });
    }

    //Login method
    private void validate(final String useremail, String userpassword){

        progressDialog.setMessage("Logging in...");
        progressDialog.show();

        //Don't login rescue users
        if(useremail.lastIndexOf("bantayrescue@gmail.com") >= 0) // String user email that should not login
        {
            progressDialog.dismiss();
            Toast.makeText(MainActivity.this, "Rescue Login Failed", Toast.LENGTH_SHORT).show();
        }
        else //Login non-rescue users
        {
            firebaseAuth.signInWithEmailAndPassword(useremail, userpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){
                        progressDialog.dismiss();
                        checkEmailVerification();
                    }
                    else{
                        attemptCounter--;
                        Log.d("LOGIN COUNTER", String.valueOf(attemptCounter));
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
                        if(attemptCounter % 5 == 0){
                            Log.d("LOGIN COUNTER", "PASSED");
                            login.setEnabled(false);
                            Toast.makeText(MainActivity.this, "You reached 5 attempts, please wait 30 secs to re-login", Toast.LENGTH_LONG).show();
                            handler.postDelayed(runnable, 30000);

                            /*new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d("LOGIN DISABLER", "PASSED");
                                    login.setEnabled(true);
                                }
                            }, 30000);*/
                        }
                    }
                }
            });
        }

    }


    //Check email verification
   private void checkEmailVerification(){

        FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        Boolean emailverified = firebaseUser.isEmailVerified();

        if(emailverified){
            finish();
            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
        }
        else{
            Toast.makeText(this, "Verify your email address to login", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }

    }

}
