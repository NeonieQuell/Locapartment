package com.neoniequell.locapartment;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.neoniequell.locapartment.databinding.ActivityCreateNewAccountBinding;

public class ActivityCreateNewAccount extends AppCompatActivity implements
        View.OnClickListener, DialogAlert.OnDialogAlertDismissListener {

    private String mName;
    private String mEmail;
    private String mPassword;

    private ActivityCreateNewAccountBinding mBinding;

    private DialogLoading mDiaLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityCreateNewAccountBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.fabBack.setOnClickListener(this);
        mBinding.btnCreateAccount.setOnClickListener(this);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_back:
                finish();
                break;
            case R.id.btn_create_account:
                if (!editTextsIsEmpty()) {
                    mDiaLoading = new DialogLoading(this);
                    mDiaLoading.show();
                    createAccount();
                }
                break;
        }
    }

    private boolean editTextsIsEmpty() {
        mName = String.valueOf(mBinding.etName.getText()).trim();
        mEmail = String.valueOf(mBinding.etEmail.getText()).trim();
        mPassword = String.valueOf(mBinding.etPassword.getText()).trim();

        if (mName.isEmpty()) {
            showToast("Name cannot be empty");
            mBinding.etName.requestFocus();
            return true;
        } else if (mEmail.isEmpty()) {
            showToast("Email cannot be empty");
            mBinding.etEmail.requestFocus();
            return true;
        } else if (mPassword.isEmpty()) {
            showToast("Password cannot be empty");
            mBinding.etPassword.requestFocus();
            return true;
        } else {
            if (mPassword.length() < 8) {
                showToast("Password must be at least 8 characters");
                mBinding.etPassword.requestFocus();
                return true;
            } else return false;
        }
    }

    private void createAccount() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(mEmail, mPassword)
                .addOnSuccessListener(s -> {
                    DbUsers dbUsers = new DbUsers();
                    ModelUser modelUser = new ModelUser(auth.getUid(), mName, mEmail, "null");

                    dbUsers.createUser(modelUser)
                            .addOnSuccessListener(s1 -> {
                                mDiaLoading.dismiss();
                                clearEditTexts();
                                setDialogAlert();
                            })
                            .addOnFailureListener(e -> {
                                e.printStackTrace();
                                mDiaLoading.dismiss();
                                showToast("Account not created");
                            });
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    mDiaLoading.dismiss();
                });
    }

    private void clearEditTexts() {
        mBinding.etName.setText("");
        mBinding.etEmail.setText("");
        mBinding.etPassword.setText("");
    }

    private void setDialogAlert() {
        DialogAlert diaAlert = new DialogAlert(this, this);
        diaAlert.setContent("Account Created!", "Your account with name "
                + mName + " has been created.");

        diaAlert.delayedShow();
    }

    @Override
    public void onDialogAlertDismiss() {
        FirebaseAuth.getInstance().signOut();
        finish();
    }
}
