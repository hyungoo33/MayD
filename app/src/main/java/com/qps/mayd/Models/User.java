package com.qps.mayd.Models;

import androidx.annotation.Nullable;

import java.util.Date;

public class User {

    private String uid;
    private String username;
    private String firstName;
    private String lastName;
    private Boolean ghostOn;
    private String gender;
    private Date birthDate;
    private String phoneNumber;
    @Nullable private String urlPicture;

    public User(){}

    public User(String uid, String username,String firstName,String lastName,String gender, Date birthDate,String phoneNumber, String urlPicture){
        this.uid = uid;
        this.username = username;
        this.ghostOn = true;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
        this.urlPicture = urlPicture;
    }


    // ---- GETTERS ----
    public String getUid() { return uid; }
    public String getUsername() { return username; }
    public Boolean getGhostOn() { return ghostOn; }
    public String getFirstName(){ return firstName;}
    public String getLastName(){ return  lastName;}
    public String getGender(){return gender;}
    public Date getBirthDate(){return birthDate;}
    public String getPhoneNumber(){return phoneNumber;}
    public String getUrlPicture() { return urlPicture; }


    // --- SETTERS ---
    public void setUsername(String username) { this.username = username; }
    public void setUid(String uid) { this.uid = uid; }
    public void setGhostOn(Boolean ghost) { this.ghostOn = ghost; }
    public void setFirstName(String firstName){this.firstName = firstName;}
    public void setLastName(String lastName){this.lastName = lastName;}
    public void setGender(String gender){this.gender = gender;}
    public void setBirthDate(Date birthDate){this.birthDate = birthDate;}
    public void setPhoneNumber(String phoneNumber){this.phoneNumber = phoneNumber;}
    public void setUrlPicture(String urlPicture) { this.urlPicture = urlPicture; }
}
