package com.qps.mayd.Controllers;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.qps.mayd.Base.BaseActivity;
import com.qps.mayd.Models.User;
import com.qps.mayd.R;
import com.qps.mayd.api.UserHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class ModifyUserActivity extends BaseActivity {

    @BindView(R.id.modifyuser_activity_progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.modifyuser_activity_imageview_profile)
    ImageView imageViewProfile;
    @BindView(R.id.modifyuser_activity_edit_text_username)
    TextInputEditText textInputEditTextUsername;
    @BindView(R.id.modifyuser_activity_radio_button_male)
    RadioButton radioButtonMale;
    @BindView(R.id.modifyuser_activity_radio_button_female)
    RadioButton radioButtonFemale;
    @BindView(R.id.modifyuser_activity_datepicker)
    DatePicker datePickerBirthdate;
    @BindView(R.id.modifyuser_activity_edit_text_phonenumber)
    TextInputEditText textInputEditTextPhoneNumber;





    private static final int UPDATE_INFOS = 40;

    private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int RC_IMAGE_PERMS = 100;
    private static final int RC_CHOOSE_PHOTO = 200;
    private Uri uriImageSelected;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.configureToolbar();
        this.updateUIWhenCreating();
    }


    @Override
    public int getFragmentLayout() {
        return R.layout.activity_modifyuser;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponse(requestCode, resultCode, data);
    }

    @OnClick(R.id.modifyuser_activity_button_update)
    public void onClickUpdateButton(){ updateInfosInFirebase(); }


    @OnClick(R.id.modifyuser_activity_imageview_profile)
    @AfterPermissionGranted(RC_IMAGE_PERMS)
    public void onClickAddFile() {
        if (!EasyPermissions.hasPermissions(this, PERMS)) {
            EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_files_access), RC_IMAGE_PERMS, PERMS);
            return;
        }
        this.chooseImageFromPhone();
    }



    private void chooseImageFromPhone(){
        if (!EasyPermissions.hasPermissions(this, PERMS)) {
            EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_files_access), RC_IMAGE_PERMS, PERMS);
            return;
        }

        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RC_CHOOSE_PHOTO);
    }

    private void handleResponse(int requestCode, int resultCode, Intent data){
        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) {
                this.uriImageSelected = data.getData();
                Glide.with(this)
                        .load(this.uriImageSelected)
                        .apply(RequestOptions.circleCropTransform())
                        .into(this.imageViewProfile);
                UserHelper.updateImage(
                        this.getCurrentUser().getUid(),this.uriImageSelected.toString()).addOnFailureListener(this.onFailureListener());
            } else {
                Toast.makeText(this, getString(R.string.toast_title_no_image_chosen), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void updateUIWhenCreating(){

        if (this.getCurrentUser() != null){

            if (this.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageViewProfile);
            }

            UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User currentUser = documentSnapshot.toObject(User.class);
                    String username = TextUtils.isEmpty(currentUser.getUsername()) ? getString(R.string.info_no_username_found) : currentUser.getUsername();
                    textInputEditTextUsername.setText(username);
                    String phoneNumber = TextUtils.isEmpty(currentUser.getPhoneNumber()) ? "aucun contact" : currentUser.getPhoneNumber();
                    textInputEditTextPhoneNumber.setText(phoneNumber);
                    String gender = TextUtils.isEmpty(currentUser.getGender()) ? "non répertorié" : currentUser.getGender();
                    if(gender.matches("homme")){
                        radioButtonMale.isChecked();
                    }
                    else if(gender.matches("femme")){
                        radioButtonFemale.isChecked();
                    }
                    Date birthDate =  currentUser.getBirthDate();
                    datePickerBirthdate.init(birthDate.getYear(),birthDate.getMonth(),birthDate.getDay(),null);
                    if (currentUser.getUrlPicture() != null) {
                        Glide.with(ModifyUserActivity.this)
                                .load(currentUser.getUrlPicture())
                                .apply(RequestOptions.circleCropTransform())
                                .into(imageViewProfile);
                    }
                }
            });
        }
    }

    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin){
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin){
                    case UPDATE_INFOS:
                        progressBar.setVisibility(View.INVISIBLE);
                        finish();
                        break;
                    default:
                        break;
                }
            }
        };
    }


    private void updateInfosInFirebase(){
        Button update = findViewById(R.id.modifyuser_activity_button_update);



        this.progressBar.setVisibility(View.VISIBLE);
        String username = this.textInputEditTextUsername.getText().toString();
        String phoneNumber = this.textInputEditTextPhoneNumber.getText().toString();
        String gender = "homme" ;
        if(radioButtonFemale.isChecked()){gender = "femme";}
        int day = datePickerBirthdate.getDayOfMonth();
        int month = datePickerBirthdate.getMonth();
        int year = datePickerBirthdate.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,day);
        Date birthDate = calendar.getTime();
        SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MM/yyyy");
        String sBirthDate = TextUtils.isEmpty(simpleDate.format(birthDate)) ? "pas de date" : simpleDate.format(birthDate);

        if (this.getCurrentUser() != null) {
            if (!username.isEmpty() && !username.equals(getString(R.string.info_no_username_found))) {
                UserHelper.updateUsername(username,
                     this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener()).addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted(UPDATE_INFOS));
            }
            if (!phoneNumber.isEmpty()) {
                UserHelper.updatePhoneNumber(this.getCurrentUser().getUid(),
                        phoneNumber).addOnFailureListener(this.onFailureListener()).addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted(UPDATE_INFOS));
            }
            if (!gender.isEmpty()) {
                UserHelper.updateGender(this.getCurrentUser().getUid(),
                        gender).addOnFailureListener(this.onFailureListener()).addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted(UPDATE_INFOS));
            }
            if (!sBirthDate.isEmpty()) {
                UserHelper.updateBirthDate(this.getCurrentUser().getUid(),
                        birthDate).addOnFailureListener(this.onFailureListener()).addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted(UPDATE_INFOS));
            }

        }
        update.setText(R.string.button_update_account);
        this.textInputEditTextUsername.setEnabled(false);

    }

}