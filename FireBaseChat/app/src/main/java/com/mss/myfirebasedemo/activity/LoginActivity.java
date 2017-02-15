package com.mss.myfirebasedemo.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
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
import com.mss.myfirebasedemo.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.mss.myfirebasedemo.activity.AppController.mAuth;

public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.edit_email)
    EditText editEmail;
    @Bind(R.id.edit_pass)
    EditText editPass;
    @Bind(R.id.btn_login)
    Button btnSend;
    @Bind(R.id.btn_signUp)
    Button btnSignUp;
    @Bind(R.id.activity_main)
    LinearLayout activityMain;
    @Bind(R.id.btn_forgot)
    Button btnForgot;
    private ViewGroup viewGroup;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initUi();
    }

    private void initUi() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        viewGroup = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d("pic", user.getPhotoUrl() + "");
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
            }
        };

        getSupportActionBar().setTitle("Login");
    }

    @OnClick({R.id.btn_login, R.id.btn_signUp})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                if (validations()) {
                    signIn(editEmail.getText().toString(), editPass.getText().toString());
                }
                break;

            case R.id.btn_signUp:
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void signIn(String email, String password) {
        progressDialog.setMessage("Login...");
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(loginIntent);
                            finish();
                        }
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "failed",
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }

    public boolean validations() {
        if (editEmail.getText().toString().trim().isEmpty()) {
            AppUtils.showErrorOnTop(viewGroup, LoginActivity.this, "Please enter your email.");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(editEmail.getText().toString().trim()).matches()) {
            AppUtils.showErrorOnTop(viewGroup, LoginActivity.this, "Please enter valid email.");
            return false;
        } else if (editPass.getText().toString().trim().isEmpty()) {
            AppUtils.showErrorOnTop(viewGroup, LoginActivity.this, "Please enter your password");
            return false;
        } else {
            return true;
        }
    }

    @OnClick(R.id.btn_forgot)
    public void onClick() {
        editPass.setText("");
        editEmail.setText("");
        startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
    }
}
