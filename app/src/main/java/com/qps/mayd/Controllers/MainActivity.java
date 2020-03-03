package com.qps.mayd.Controllers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.qps.mayd.Base.BaseActivity;
import com.qps.mayd.R;
import com.qps.mayd.api.UserHelper;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!this.isCurrentUserLogged()){
            finish();
        }
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_main;
    }

    public void onClickButton(View v){
        int buttonTag = Integer.valueOf(v.getTag().toString());
        switch (buttonTag){
            case 10:
                Intent prIntent = new Intent(MainActivity.this, ProfilActivity.class);
                this.startActivity(prIntent);
                break;
            case 20:
                Intent grIntent = new Intent(MainActivity.this, GroupsActivity.class);
                this.startActivity(grIntent);
                break;
            case 30:
                Intent caIntent = new Intent(MainActivity.this, CarteActivity.class);
                this.startActivity(caIntent);
                break;
            case 40:
                break;
            case 50:Intent geIntent = new Intent(MainActivity.this, GestesActivity.class);
                this.startActivity(geIntent);
                break;
            case 60:
                break;
            case 70:Intent reIntent = new Intent(MainActivity.this, ReglagesActivity.class);
                this.startActivity(reIntent);
                break;
            case 80:
                break;
            case 90:
                break;

        }

    }

    /*private void updateUserGhost(){
        if(this.getCurrentUser() != null){
            UserHelper.updateGhostOn(this.getCurrentUser().getUid(),
                    !this.getCurrentUser().getGhostOn());
        }


    }*/
}
