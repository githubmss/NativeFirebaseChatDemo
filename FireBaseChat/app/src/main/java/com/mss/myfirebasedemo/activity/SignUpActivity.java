package com.mss.myfirebasedemo.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mss.myfirebasedemo.R;
import com.mss.myfirebasedemo.model.ProfileModel;
import com.mss.myfirebasedemo.model.UserModel;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.mss.myfirebasedemo.activity.AppController.mAuth;

/**
 * Created by deepakgupta on 10/2/17.
 */

public class SignUpActivity extends AppCompatActivity {


    @Bind(R.id.edit_email)
    EditText editEmail;
    @Bind(R.id.edit_pass)
    EditText editPass;
    @Bind(R.id.btn_login)
    Button btnLogin;
    @Bind(R.id.activity_main)
    LinearLayout activityMain;
    private ViewGroup viewGroup;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog progressDialog;
    UserModel user;
    private DatabaseReference database;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        initUi();
    }

    private void initUi() {
        progressDialog = new ProgressDialog(this);
        database = FirebaseDatabase.getInstance().getReference();
        viewGroup = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d("pic", user.getPhotoUrl() + "");
                    finish();
                }
            }
        };
        getSupportActionBar().setTitle("SignUp");
    }

    private void createAccount(String email, String password) {
        progressDialog.setMessage("SignUp...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            user = new UserModel();
                            user.setUid(mAuth.getCurrentUser().getUid());
                            user.setEmail(editEmail.getText().toString());
                            user.setProfile(null);
                            editEmail.setText("");
                            editPass.setText("");
                            database.child("users").child(user.getUid()).setValue(user);
                            finish();
                        }
                        // If sign in fails, display a message to the user. If sign in succeeds
                        if (!task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(SignUpActivity.this, "failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public boolean validations() {
        if (editEmail.getText().toString().trim().isEmpty()) {
            AppUtils.showErrorOnTop(viewGroup, SignUpActivity.this, "Please enter your email.");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(editEmail.getText().toString().trim()).matches()) {
            AppUtils.showErrorOnTop(viewGroup, SignUpActivity.this, "Please enter valid email.");
            return false;
        } else if (editPass.getText().toString().trim().isEmpty()) {
            AppUtils.showErrorOnTop(viewGroup, SignUpActivity.this, "Please enter your password");
            return false;
        } else {
            return true;
        }
    }

    @OnClick(R.id.btn_login)
    public void onClick() {
        if (validations()) {
            createAccount(editEmail.getText().toString(), editPass.getText().toString());
        }
    }
}
