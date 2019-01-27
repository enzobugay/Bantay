package com.example.bantay.bantay;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText firstname, lastname, address, contactnumber, password, confirmpassword;
    public EditText emailadd;
    private Button create;
    private Spinner barangay;
    private TextView login;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;


    public String fname, lname, addressb, cnumber, ubarangay, email, pword, cpword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setRegister();



        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);


        //Register
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()){

                    String useremail = emailadd.getText().toString().trim();
                    String userpassword = password.getText().toString().trim();
                    progressDialog.setMessage("Creating account...");
                    progressDialog.show();

                    firebaseAuth.createUserWithEmailAndPassword(useremail, userpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                sendEmailVerification();
                            }
                            else {
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Account creation failed", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

        //Login button
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }
        });


    }

    private void setRegister(){

        firstname = (EditText)findViewById(R.id.createfirstname);
        lastname = (EditText)findViewById(R.id.createlastname);
        address = (EditText)findViewById(R.id.createhomeaddress);
        contactnumber = (EditText)findViewById(R.id.createcontactnumber);
        emailadd = (EditText)findViewById(R.id.createemail);
        password = (EditText)findViewById(R.id.createpassword);
        confirmpassword = (EditText)findViewById(R.id.createconfirmpassword);
        create = (Button)findViewById(R.id.createcreate);
        login = (TextView)findViewById(R.id.createlogin);

        //Barangay dropdown
        barangay = (Spinner)findViewById(R.id.createbarangay);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (RegisterActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.barangay));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        barangay.setAdapter(arrayAdapter);

        //String Text = barangay.getSelectedItem().toString();


    }

    //Validate if required fields are filled
    private Boolean validate(){
        Boolean result = false;

        fname = firstname.getText().toString();
        lname = lastname.getText().toString();
        addressb = address.getText().toString();
        cnumber = contactnumber.getText().toString();
        email = emailadd.getText().toString();
        pword = password.getText().toString();
        cpword = confirmpassword.getText().toString();
        ubarangay = barangay.getSelectedItem().toString();


        if (fname.isEmpty() || lname.isEmpty() || addressb.isEmpty() || cnumber.isEmpty() || email.isEmpty() || pword.isEmpty() || cpword.isEmpty()) {
            Toast.makeText(this, "Please fill up all required fields", Toast.LENGTH_SHORT).show();
        }
        else if (!pword.equals(cpword)){
            Toast.makeText(this, "Password does NOT match!", Toast.LENGTH_SHORT).show();
        }
        else {
            result = true;
        }
        return result;

    }

    //Email verification
    private void sendEmailVerification(){

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        sendUserData();
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Succesfully registered. Verification email sent!", Toast.LENGTH_LONG).show();
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    }
                    else{
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Verification email NOT sent!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    //Account details to database
    private void sendUserData(){
        String path = "/Users/Residents"; //Database path
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(path);
        AccountDetails accountDetails = new AccountDetails(fname, lname, addressb, cnumber, ubarangay, email);
        databaseReference.child(firebaseAuth.getUid()).setValue(accountDetails);
    }

}
