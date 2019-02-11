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

    public RequestEntries(){

    }


    public RequestEntries(String requestFirstName, String requestLastName, String requestContactNumber,
                          String requestLocation, String requestLandmarks, String requestPax, String requestPwd,
                          String requestSenior, String requestInfant, String requestMedical, String requestOther,
                          String requestSpecific) {
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

    public String getRequestFirstName() {
        return requestFirstName;
    }

    public void setRequestFirstName(String requestFirstName) {
        this.requestFirstName = requestFirstName;
    }

    public String getRequestLastName() {
        return requestLastName;
    }

    public void setRequestLastName(String requestLastName) {
        this.requestLastName = requestLastName;
    }

    public String getRequestContactNumber() {
        return requestContactNumber;
    }

    public void setRequestContactNumber(String requestContactNumber) {
        this.requestContactNumber = requestContactNumber;
    }

    public String getRequestLocation() {
        return requestLocation;
    }

    public void setRequestLocation(String requestLocation) {
        this.requestLocation = requestLocation;
    }

    public String getRequestLandmarks() {
        return requestLandmarks;
    }

    public void setRequestLandmarks(String requestLandmarks) {
        this.requestLandmarks = requestLandmarks;
    }

    public String getRequestPax() {
        return requestPax;
    }

    public void setRequestPax(String requestPax) {
        this.requestPax = requestPax;
    }

    public String getRequestPwd() {
        return requestPwd;
    }

    public void setRequestPwd(String requestPwd) {
        this.requestPwd = requestPwd;
    }

    public String getRequestSenior() {
        return requestSenior;
    }

    public void setRequestSenior(String requestSenior) {
        this.requestSenior = requestSenior;
    }

    public String getRequestInfant() {
        return requestInfant;
    }

    public void setRequestInfant(String requestInfant) {
        this.requestInfant = requestInfant;
    }

    public String getRequestMedical() {
        return requestMedical;
    }

    public void setRequestMedical(String requestMedical) {
        this.requestMedical = requestMedical;
    }

    public String getRequestOther() {
        return requestOther;
    }

    public void setRequestOther(String requestOther) {
        this.requestOther = requestOther;
    }

    public String getRequestSpecific() {
        return requestSpecific;
    }

    public void setRequestSpecific(String requestSpecific) {
        this.requestSpecific = requestSpecific;
    }
}
