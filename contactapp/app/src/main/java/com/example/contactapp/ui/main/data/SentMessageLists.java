package com.example.contactapp.ui.main.data;

public class SentMessageLists {


    private String contactFullName;
    private String sentOtp;
    private String otpTime;

    public String getContactFullName() {
        return contactFullName;
    }

    public void setContactFullName(String contactFullName) {
        this.contactFullName = contactFullName;
    }

    public String getSentOtp() {
        return sentOtp;
    }

    public void setSentOtp(String sentOtp) {
        this.sentOtp = sentOtp;
    }

    public String getOtpTime() {
        return otpTime;
    }

    public void setOtpTime(String otpTime) {
        this.otpTime = otpTime;
    }

    public SentMessageLists(){

    }

    public SentMessageLists(String contactFullName, String sentOtp, String otpTime) {
        this.contactFullName = contactFullName;
        this.sentOtp = sentOtp;
        this.otpTime = otpTime;
    }




}
