package com.example.contactapp.ui.main.data;

public class ContactLists {
    private String mFirstName;
    private String mLastName;
    private String mPhoneNumber;



    private String mEmail;

    public ContactLists(){

    }

    public ContactLists(String mFirstName, String mLastName, String mPhoneNumber,String mEmail) {
        this.mFirstName = mFirstName;
        this.mLastName = mLastName;
        this.mPhoneNumber = mPhoneNumber;
        this.mEmail = mEmail;
    }

    public String getmFirstName() {
        return mFirstName;
    }

    public void setmFirstName(String mFirstName) {
        this.mFirstName = mFirstName;
    }

    public String getmLastName() {
        return mLastName;
    }

    public void setmLastName(String mLastName) {
        this.mLastName = mLastName;
    }

    public String getmPhoneNumber() {
        return mPhoneNumber;
    }

    public void setmPhoneNumber(String mPhoneNumber) {
        this.mPhoneNumber = mPhoneNumber;
    }
    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }


}
