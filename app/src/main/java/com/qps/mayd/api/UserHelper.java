package com.qps.mayd.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.qps.mayd.Models.User;

import java.util.Date;

public class UserHelper {

    private static final String COLLECTION_NAME = "users";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createUser(String uid, String username, String firstName, String lastName,  String gender,Date birthDate, String phoneNumber, String urlPicture) {
        User userToCreate = new User(uid, username,firstName,lastName,gender,birthDate,phoneNumber, urlPicture);
        return UserHelper.getUsersCollection().document(uid).set(userToCreate);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }
    public static Task<DocumentSnapshot> getGhost(String ghostOn){
        return UserHelper.getUsersCollection().document(ghostOn).get();
    }

    // --- UPDATE ---

    public static Task<Void> updateUsername(String username, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("username", username);
    }

    public static Task<Void> updateGhostOn(String uid, Boolean ghostOn) {
        return UserHelper.getUsersCollection().document(uid).update("ghostOn", ghostOn);
    }

    public static Task<Void> updateImage(String uid, String image) {
        return UserHelper.getUsersCollection().document(uid).update("urlPicture", image);
    }
    public static Task<Void> updatePhoneNumber(String uid, String phoneNumber) {
        return UserHelper.getUsersCollection().document(uid).update("phoneNumber", phoneNumber);
    }
    public static Task<Void> updateGender(String uid, String gender) {
        return UserHelper.getUsersCollection().document(uid).update("gender", gender);
    }
    public static Task<Void> updateBirthDate(String uid, Date birthDate) {
        return UserHelper.getUsersCollection().document(uid).update("birthDate", birthDate);
    }


    // --- DELETE ---

    public static Task<Void> deleteUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).delete();
    }

}