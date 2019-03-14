package com.example.bantay.bantay;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
public class EditAccountFragment extends Fragment {

    private EditText firstname, lastname, address, contactnumber;
    private String email, fname, lname, addressb, cnumber;
    private Spinner barangay;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    String path = "/Users/Residents";


    public EditAccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_account, container, false);

        //Save button
       Button save = (Button)view.findViewById(R.id.editsave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setCancelable(false);
                    alertDialog.setMessage("Save account details?");
                    alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Do nothing
                        }
                    });
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            updateEntries();
                            Toast.makeText(getActivity(), "Account details saved!", Toast.LENGTH_SHORT).show();
                            setFragment();
                        }
                    });
                    alertDialog.show();
                }
            }
        });
        //Cancel button
      Button cancel = (Button)view.findViewById(R.id.editcancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment();
            }
        });
        //Change password button
        TextView changepassword = (TextView)view.findViewById(R.id.editchangepwordbtn);
          changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment2();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditAccountFragment editAccountFragment = (EditAccountFragment) getChildFragmentManager().findFragmentById(R.id.editaccount);
        loadEntries();
    }



    //Load Account Details Method
    private void loadEntries() {

        firstname = (EditText) getView().findViewById(R.id.editfirstname);
        lastname = (EditText) getView().findViewById(R.id.editlastname);
        address = (EditText) getView().findViewById(R.id.editaddress);
        contactnumber = (EditText) getView().findViewById(R.id.editcontactnumber);
        barangay = (Spinner) getView().findViewById(R.id.editbarangay);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.barangay));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        barangay.setAdapter(arrayAdapter);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(path);

        databaseReference.child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AccountDetails accountDetails = dataSnapshot.getValue(AccountDetails.class);
                firstname.setText(accountDetails.getUserFirstName());
                lastname.setText(accountDetails.getUserLastName());
                address.setText(accountDetails.getUserAddress());
                barangay.setSelected(Boolean.parseBoolean(accountDetails.getUserBarangay()));
                contactnumber.setText(accountDetails.getUserContactNumber());
                email = dataSnapshot.child("userEmail").getValue(String.class);

                //Set the dropdown value to user's barangay
                String compareValue = accountDetails.userBarangay;
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                        (getActivity(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.barangay));
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                barangay.setAdapter(arrayAdapter);
                if (compareValue != null) {
                    int spinnerPosition = arrayAdapter.getPosition(compareValue);
                    barangay.setSelection(spinnerPosition);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    //Save edited account details to database
    private void updateEntries() {

        String ubarangay = barangay.getSelectedItem().toString();
        String uemail = email;

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(path);
        AccountDetails accountDetails;
        accountDetails = new AccountDetails(fname, lname, addressb, cnumber, ubarangay, uemail);
        databaseReference.child(firebaseAuth.getUid()).setValue(accountDetails);

    }

    //Validate all fields
    private boolean validate(){
        Boolean result = false;

        firstname = (EditText) getView().findViewById(R.id.editfirstname);
        lastname = (EditText) getView().findViewById(R.id.editlastname);
        address = (EditText) getView().findViewById(R.id.editaddress);
        contactnumber = (EditText) getView().findViewById(R.id.editcontactnumber);

        fname = firstname.getText().toString().trim();
        lname = lastname.getText().toString().trim();
        addressb = address.getText().toString().trim();
        cnumber = contactnumber.getText().toString().trim();

        if(fname.isEmpty() || lname.isEmpty() || addressb.isEmpty() || cnumber.isEmpty()){
            Toast.makeText(getActivity(), "Please fill up all fields", Toast.LENGTH_SHORT).show();
        }
        else{
            result = true;
        }
        return result;
    }

    //Cancel method
    public void setFragment(){
        AccountFragment accountFragment = new AccountFragment();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_nav, accountFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    //Go to change password method
    public void setFragment2(){
        ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_nav, changePasswordFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
