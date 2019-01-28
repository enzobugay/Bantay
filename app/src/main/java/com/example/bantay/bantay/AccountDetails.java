package com.example.bantay.bantay;

/*
* THIS IS TO SET AND GET THE DETAILS OF THE ACCOUNT IN REGISTRATION (RegisterActivity)
*
* */
public class AccountDetails {
    public String userFirstName;
    public String userLastName;
    public String userAddress;
    public String userBarangay;
    public String userContactNumber;
    public String userEmail;

    public AccountDetails(){

    }

    public AccountDetails(String userFirstName, String userLastName, String userAddress, String userContactNumber, String userBarangay, String userEmail) {
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userAddress = userAddress;
        this.userBarangay = userBarangay;
        this.userContactNumber = userContactNumber;
        this.userEmail = userEmail;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserBarangay() {
        return userBarangay;
    }

    public void setUserBarangay(String userBarangay) {
        this.userBarangay = userBarangay;
    }

    public String getUserContactNumber() {
        return userContactNumber;
    }

    public void setUserContactNumber(String userContactNumber) {
        this.userContactNumber = userContactNumber;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
