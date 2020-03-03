package com.qps.mayd.Controllers;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioButton;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.qps.mayd.Base.BaseActivity;
import com.qps.mayd.R;
import com.qps.mayd.api.UserHelper;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

public class CreateUserActivity extends BaseActivity {
    @BindView(R.id.createuser_activity_coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.createuser_activity_edit_text_firstname)
    TextInputEditText textInputEditTextFirstName;
    @BindView(R.id.createuser_activity_edit_text_lastname)
    TextInputEditText textInputEditTextLastName;
    @BindView(R.id.createuser_activity_datepicker)
    DatePicker datePickerBirthDate;
    @BindView(R.id.createuser_activity_radio_button_male)
    RadioButton radioButtonMale;
    @BindView(R.id.createuser_activity_radio_button_female)
    RadioButton radioButtonFemale;
    @BindView(R.id.createuser_activity_edit_text_phonenumber)
    TextInputEditText textInputEditTextPhoneNumber;




    @Override
    public int getFragmentLayout() {
        return R.layout.activity_createuser;
    }
    private void showSnackBar(CoordinatorLayout coordinatorLayout, String message){
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    @OnClick(R.id.createuser_activity_button_validate)
    public void onClickValidateButton(){
        if(!TextUtils.isEmpty(textInputEditTextFirstName.getText().toString()) && !TextUtils.isEmpty(textInputEditTextLastName.getText().toString()) && !TextUtils.isEmpty(textInputEditTextPhoneNumber.getText().toString())) {
            this.createUserInFirestore();
            this.startMainActivity();
        }
        else{
            showSnackBar(this.coordinatorLayout, "Un champ n'est pas rempli");
        }

    }

    private void startMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    private void createUserInFirestore(){

        FirebaseUser user = this.getCurrentUser();

        if (this.getCurrentUser() != null){

            String urlPicture = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;
            String username = user.getDisplayName();
            String firstName = textInputEditTextFirstName.getText().toString();
            String lastName = textInputEditTextLastName.getText().toString();
            String gender = "homme" ;
            if(radioButtonFemale.isChecked()){gender = "femme";}
            int day = datePickerBirthDate.getDayOfMonth();
            int month = datePickerBirthDate.getMonth();
            int year = datePickerBirthDate.getYear();
            Calendar calendar = Calendar.getInstance();
            calendar.set(year,month,day);
            Date birthDate = calendar.getTime();
            String uid = user.getUid();
            String phoneNumber = (user.getPhoneNumber() != null ? user.getPhoneNumber() : textInputEditTextPhoneNumber.getText().toString());

            UserHelper.createUser(uid, username,firstName,lastName,gender,birthDate,phoneNumber, urlPicture).addOnFailureListener(this.onFailureListener());
        }
    }


}
