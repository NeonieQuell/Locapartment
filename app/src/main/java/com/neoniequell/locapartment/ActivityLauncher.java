package com.neoniequell.locapartment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.neoniequell.locapartment.databinding.ActivityLauncherBinding;

public class ActivityLauncher extends AppCompatActivity implements View.OnClickListener {

    private ActivityLauncherBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkTheme();
        checkLastSignedInAccount();

        mBinding = ActivityLauncherBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.btnLogIn.setOnClickListener(this);
        mBinding.btnCreateNewAccount.setOnClickListener(this);
    }

    private void checkTheme() {
        SharedPreferences sharedPrefs = getSharedPreferences(
                DialogTheme.SHARED_PREFS, MODE_PRIVATE);

        String nightModeValue = sharedPrefs.getString(DialogTheme.NIGHT_MODE,
                "MODE_NIGHT_FOLLOW_SYSTEM");

        switch (nightModeValue) {
            case "MODE_NIGHT_NO":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "MODE_NIGHT_YES":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case "MODE_NIGHT_FOLLOW_SYSTEM":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    private void checkLastSignedInAccount() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            startActivity(new Intent(this, ActivityMain.class));
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_log_in:
                startActivityForResult(new Intent(this, ActivityLogIn.class), 1);
                break;
            case R.id.btn_create_new_account:
                startActivity(new Intent(this, ActivityCreateNewAccount.class));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) finish();
        }
    }
}
