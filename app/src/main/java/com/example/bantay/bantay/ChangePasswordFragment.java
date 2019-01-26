package com.example.bantay.bantay;


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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePasswordFragment extends Fragment {


    private EditText newpw, confirmpw;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        Button save = (Button)view.findViewById(R.id.editpwsave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();

            }
        });


        Button cancel = (Button)view.findViewById(R.id.editpwcancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment();
            }
        });
        return view;
    }

    private void changePassword(){

        newpw = (EditText) getView().findViewById(R.id.newpw);
        confirmpw = (EditText) getView().findViewById(R.id.confirmnewpw);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth = FirebaseAuth.getInstance();

        String newPassword = newpw.getText().toString();
        String confirmPassword = confirmpw.getText().toString();

        if(newPassword.equals(confirmPassword)) {
            firebaseUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        firebaseAuth.signOut();
                        getActivity().onBackPressed();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(getActivity(), "Password successfully changed! Re-login", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getActivity(), "Change password failed. Try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            Toast.makeText(getActivity(), "Password does NOT match!", Toast.LENGTH_SHORT).show();
        }
    }

    //Cancel method
    public void setFragment(){
        EditAccountFragment editAccountFragment = new EditAccountFragment();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_nav, editAccountFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
