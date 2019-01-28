package com.example.bantay.bantay;

/*
    THIS IS TO SET AND GET THE ENTRIES OF USER IN THE RESCUE REQUEST (RequestFragment)
 */

public class RequestEntries {

    public String requestFirstName;
    public String requestLastName;
    public String requestContactNumber;
    public String requestLocation;
    public String requestLandmarks;
    public String requestPax;
    public String requestPwd;
    public String requestSenior;
    public String requestInfant;
    public String requestMedical;
    public String requestOther;
    public String requestSpecific;

    public RequestEntries(String requestFirstName, String requestLastName, String requestContactNumber, String requestLocation, String requestLandmarks, String requestPax, String requestPwd, String requestSenior, String requestInfant, String requestMedical, String requestOther, String requestSpecific) {
        this.requestFirstName = requestFirstName;
        this.requestLastName = requestLastName;
        this.requestContactNumber = requestContactNumber;
        this.requestLocation = requestLocation;
        this.requestLandmarks = requestLandmarks;
        this.requestPax = requestPax;
        this.requestPwd = requestPwd;
        this.requestSenior = requestSenior;
        this.requestInfant = requestInfant;
        this.requestMedical = requestMedical;
        this.requestOther = requestOther;
        this.requestSpecific = requestSpecific;
    }
}
