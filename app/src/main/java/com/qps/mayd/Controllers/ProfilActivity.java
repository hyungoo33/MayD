package com.qps.mayd.Controllers;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.qps.mayd.Base.BaseActivity;
import com.qps.mayd.R;
import com.qps.mayd.api.UserHelper;
import com.qps.mayd.Models.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class ProfilActivity extends BaseActivity {

    //FOR DESIGN
    @BindView(R.id.profil_activity_imageview_profile)
    ImageView imageViewProfile;
    @BindView(R.id.profil_activity_text_view_username)
    TextView textViewUsername;
    @BindView(R.id.profil_activity_text_view_email)
    TextView textViewEmail;
    @BindView(R.id.profil_activity_text_view_phonenumber)
    TextView textViewPhoneNumber;
    @BindView(R.id.profil_activity_text_view_gender)
    TextView textViewGender;
    @BindView(R.id.profil_activity_text_view_birthdate)
    TextView textViewBirthDate;
    @BindView(R.id.profil_activity_progress_bar)
    ProgressBar progressBar;


    private boolean updateUsername = false;
    private static final int SIGN_OUT_TASK = 10;
    private static final int DELETE_USER_TASK = 20;
    private static final int UPDATE_USERNAME = 30;

    //image
    private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int RC_IMAGE_PERMS = 100;
    private static final int RC_CHOOSE_PHOTO = 200;
    private Uri uriImageSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.configureToolbar();
        this.updateUIWhenCreating();
    }

    @Override
    public int getFragmentLayout() { return R.layout.activity_profil; }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 2 - Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    public void startModifyUserActivity(){
        Intent intent = new Intent(this,ModifyUserActivity.class);
        startActivity(intent);
    }


    // --------------------
    // ACTIONS
    // --------------------


    @OnClick(R.id.profil_activity_button_modify)
    public void onClickUpdateButton() {this.startModifyUserActivity(); }

    @OnClick(R.id.profil_activity_button_sign_out)
    public void onClickSignOutButton() {this.signOutUserFromFirebase();}

    @OnClick(R.id.profil_activity_button_delete)
    public void onClickDeleteButton() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.popup_message_confirmation_delete_account)
                .setPositiveButton(R.string.popup_message_choice_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteUserFromFirebase();
                    }
                })
                .setNegativeButton(R.string.popup_message_choice_no, null)
                .show();
    }



    // --------------------
    // UI
    // --------------------

    // 1 - Update UI when activity is creating
    private void updateUIWhenCreating(){

        if (this.getCurrentUser() != null){

            //Get picture URL from Firebase
            if (this.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageViewProfile);
            }


            //Get email & username from Firebase
            String email = TextUtils.isEmpty(this.getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) : this.getCurrentUser().getEmail();

            UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User currentUser = documentSnapshot.toObject(User.class);
                    String username = TextUtils.isEmpty(currentUser.getUsername()) ? getString(R.string.info_no_username_found) : currentUser.getUsername();
                    textViewUsername.setText(username);
                    String phoneNumber = TextUtils.isEmpty(currentUser.getPhoneNumber()) ? "aucun contact" : currentUser.getPhoneNumber();
                    textViewPhoneNumber.setText(phoneNumber);
                    String gender = TextUtils.isEmpty(currentUser.getGender()) ? "non répertorié" : currentUser.getGender();
                    textViewGender.setText(gender);
                    SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MM/yyyy");
                    String birthdate = TextUtils.isEmpty(simpleDate.format(currentUser.getBirthDate())) ? "aucun contact" : simpleDate.format(currentUser.getBirthDate());
                    textViewBirthDate.setText(birthdate);
                    if (currentUser.getUrlPicture() != null) {
                        Glide.with(ProfilActivity.this)
                                .load(currentUser.getUrlPicture())
                                .apply(RequestOptions.circleCropTransform())
                                .into(imageViewProfile);
                    }
                }
            });
            //Update views with data
            this.textViewEmail.setText(email);
        }
    }
    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin){
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin){
                    case UPDATE_USERNAME:
                        progressBar.setVisibility(View.INVISIBLE);
                        break;
                    case SIGN_OUT_TASK:
                        finish();
                        break;
                    case DELETE_USER_TASK:
                        finish();
                        break;
                    default:
                        break;
                }
            }
        };
    }


    // --------------------
    // FILE MANAGEMENT
    // --------------------




    // --------------------
    // REST REQUESTS
    // --------------------
    private void signOutUserFromFirebase(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
    }

    private void deleteUserFromFirebase(){
        if (this.getCurrentUser() != null) {
            UserHelper.deleteUser(this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener());

            AuthUI.getInstance()
                    .delete(this)
                    .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(DELETE_USER_TASK));

        }
    }

}