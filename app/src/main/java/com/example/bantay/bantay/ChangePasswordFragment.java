package com.example.bantay.bantay;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePasswordFragment extends Fragment {


    private EditText oldpw, newpw, confirmpw;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private String oldPassword, newPassword, confirmPassword, email;


    public ChangePasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        //Save button
        Button save = (Button)view.findViewById(R.id.editpwsave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setCancelable(false);
                    alertDialog.setMessage("Change Password?");
                    alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Do nothing
                        }
                    });
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            changePassword();
                        }
                    });
                    alertDialog.show();
                }

            }
        });

        //Cancel button
        Button cancel = (Button)view.findViewById(R.id.editpwcancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        return view;
    }

    //Validate all fields
    private boolean validate(){
        Boolean result = false;

        oldpw = getView().findViewById(R.id.oldpw);
        newpw = getView().findViewById(R.id.newpw);
        confirmpw = getView().findViewById(R.id.confirmnewpw);

        oldPassword = oldpw.getText().toString();
        newPassword = newpw.getText().toString();
        confirmPassword = confirmpw.getText().toString();

        if(oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()){
            Toast.makeText(getActivity(), "Please fill up all fields", Toast.LENGTH_SHORT).show();
        }
        else if(!newPassword.equals(confirmPassword)){
            Toast.makeText(getActivity(), "Password does NOT match!", Toast.LENGTH_SHORT).show();
        }
        else if(newPassword.equals(confirmPassword)){
            if(newPassword.length()<8){
                Toast.makeText(getActivity(), "Password must be atleast 8 characters!", Toast.LENGTH_LONG).show();
            }
            else if(!isValidPassword(newPassword)){
                Toast.makeText(getActivity(), "Password must contain a capital letter and a number!", Toast.LENGTH_LONG).show();
            }
            else{
                result = true;
            }
        }
        return result;
    }

    //Change password
    private void changePassword(){

        progressDialog = new ProgressDialog(getActivity());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth = FirebaseAuth.getInstance();

        email = firebaseUser.getEmail(); //get user's email

        AuthCredential authCredential = EmailAuthProvider.getCredential(email, oldPassword);

        progressDialog.setMessage("Changing Password");
        progressDialog.show();
        firebaseUser.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //Check if old password is match
                if(task.isSuccessful()){
                    //Check if new password is not equal to old password
                    if(newPassword.equals(oldPassword)){
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "New password must not be the same as old password!", Toast.LENGTH_LONG).show();
                    }
                    else {
                        //Update password
                        firebaseUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    firebaseAuth.signOut();
                                    //getActivity().onBackPressed();
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    startActivity(intent);
                                    Toast.makeText(getActivity(), "Password successfully changed! Re-login", Toast.LENGTH_LONG).show();
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), "Change password failed. Try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Incorrect old password!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Validate password
    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^.*(?=.*[0-9])(?=.*[A-Z]).*$"; //must have a capital letter and number
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    //Cancel method
    public void cancel(){
        EditAccountFragment editAccountFragment = new EditAccountFragment();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_nav, editAccountFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
