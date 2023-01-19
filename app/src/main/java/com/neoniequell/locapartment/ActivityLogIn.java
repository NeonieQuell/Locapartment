package com.neoniequell.locapartment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.neoniequell.locapartment.databinding.ActivityLogInBinding;

public class ActivityLogIn extends AppCompatActivity implements View.OnClickListener {

    private static final int RQ_CODE = 1;

    private String mEmail, mPassword;

    private FirebaseAuth mAuth;
    private DbUsers mDbUsers;

    private ActivityLogInBinding mBinding;

    private DialogLoading mDiaLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityLogInBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        mDbUsers = new DbUsers();

        mDiaLoading = new DialogLoading(this);

        mBinding.fabBack.setOnClickListener(this);
        mBinding.btnLogIn.setOnClickListener(this);
        mBinding.btnContinueWithGoogle.setOnClickListener(this);
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
            case R.id.btn_log_in:
                if (!editTextsIsEmpty()) {
                    mDiaLoading.show();
                    continueWithEmailAndPassword();
                }
                break;
            case R.id.btn_continue_with_google:
                mDiaLoading.show();
                continueWithGoogle();
                break;
        }
    }

    private boolean editTextsIsEmpty() {
        mEmail = String.valueOf(mBinding.etEmail.getText()).trim();
        mPassword = String.valueOf(mBinding.etPassword.getText()).trim();

        if (mEmail.isEmpty()) {
            showToast("Email cannot be empty");
            mBinding.etEmail.requestFocus();
            return true;
        } else if (mPassword.isEmpty()) {
            showToast("Password cannot be empty");
            mBinding.etPassword.requestFocus();
            return true;
        } else return false;
    }

    private void continueWithEmailAndPassword() {
        mAuth.signInWithEmailAndPassword(mEmail, mPassword)
                .addOnSuccessListener(s -> {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    assert firebaseUser != null;

                    if (firebaseUser.isEmailVerified()) {
                        mDiaLoading.dismiss();
                        updateUi();
                    } else {
                        firebaseUser.sendEmailVerification()
                                .addOnSuccessListener(s1 -> {
                                    mBinding.etEmail.setText("");
                                    mBinding.etPassword.setText("");

                                    mDiaLoading.dismiss();

                                    String message = "Check your email's inbox or spam folder " +
                                            "for account verification.";

                                    DialogAlert diaAlert = new DialogAlert(this);
                                    diaAlert.setContent("Verify your Account", message);
                                    diaAlert.delayedShow();

                                    mAuth.signOut();
                                })
                                .addOnFailureListener(e -> {
                                    e.printStackTrace();
                                    mDiaLoading.dismiss();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    mDiaLoading.dismiss();
                });
    }

    private void continueWithGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();

        GoogleSignInClient gsc = GoogleSignIn.getClient(this, gso);
        startActivityForResult(gsc.getSignInIntent(), RQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RQ_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                onGoogleSignInSuccess(account);
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void onGoogleSignInSuccess(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(s -> {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    assert firebaseUser != null;

                    ModelUser modelUser = new ModelUser();
                    modelUser.setUid(firebaseUser.getUid());
                    modelUser.setDisplayName(firebaseUser.getDisplayName());
                    modelUser.setEmail(firebaseUser.getEmail());
                    modelUser.setPhotoUrl(String.valueOf(firebaseUser.getPhotoUrl()));

                    mDbUsers.hasUser(modelUser, exist -> {
                        if (exist) {
                            setResult(RESULT_OK);
                            mDiaLoading.dismiss();
                            updateUi();
                        } else {
                            mDbUsers.createUser(modelUser)
                                    .addOnSuccessListener(s1 -> {
                                        setResult(RESULT_OK);
                                        mDiaLoading.dismiss();
                                        updateUi();
                                    })
                                    .addOnFailureListener(e -> {
                                        e.printStackTrace();
                                        mDiaLoading.dismiss();
                                    });
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    mDiaLoading.dismiss();
                });
    }

    private void updateUi() {
        Intent intent = new Intent(this, ActivityMain.class);
        startActivity(intent);
        finish();
    }
}
