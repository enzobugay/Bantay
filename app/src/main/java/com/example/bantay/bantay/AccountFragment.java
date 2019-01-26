package com.example.bantay.bantay;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private TextView profilefirstname, profilelastname, profileaddress, profilebarangay, profilenumber, profileemail;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private Firebase UserRootRef;

    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        //Edit Account Button
        Button edit = (Button)view.findViewById(R.id.editaccountbutton);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment();
            }
        });

        //Logout Button
        Button logout = (Button)view.findViewById(R.id.logoutbutton);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                getActivity().onBackPressed();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);

            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Firebase.setAndroidContext(getActivity());
        AccountFragment accountFragment = (AccountFragment) getChildFragmentManager().findFragmentById(R.id.account);
        loadEntries();

    }

    //Load Account Details Method
    private void loadEntries(){

        profilefirstname = (TextView)getView().findViewById(R.id.tvfirstname);
        profilelastname = (TextView)getView().findViewById(R.id.tvlastname);
        profileaddress = (TextView)getView().findViewById(R.id.tvaddress);
        profilebarangay = (TextView)getView().findViewById(R.id.tvbarangay);
        profilenumber = (TextView)getView().findViewById(R.id.tvcontactnumber);
        profileemail = (TextView)getView().findViewById(R.id.tvemail);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReferenceFromUrl("https://bantay-f81c2.firebaseio.com/Users");

        databaseReference.child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AccountDetails accountDetails = dataSnapshot.getValue(AccountDetails.class);
                profilefirstname.setText(accountDetails.getUserFirstName());
                profilelastname.setText(accountDetails.getUserLastName());
                profileaddress.setText(accountDetails.getUserAddress());
                profilebarangay.setText(accountDetails.getUserBarangay());
                profilenumber.setText(accountDetails.getUserContactNumber());
                profileemail.setText(accountDetails.getUserEmail());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    //AccountFragment to EditAccountFragment method
    private void setFragment() {
        EditAccountFragment editAccountFragment = new EditAccountFragment();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_nav, editAccountFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

}
