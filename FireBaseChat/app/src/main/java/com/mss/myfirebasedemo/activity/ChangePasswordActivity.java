package com.mss.myfirebasedemo.activity;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.mss.myfirebasedemo.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.mss.myfirebasedemo.activity.AppController.mAuth;


/**
 * Created by deepakgupta on 8/2/17.
 */

public class ChangePasswordActivity extends AppCompatActivity {

    @Bind(R.id.btn_submit)
    Button btnSubmit;
    @Bind(R.id.edit_new_pass)
    EditText editNewPass;
    private ViewGroup viewGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        initUi();
    }

    private void initUi() {
        viewGroup = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
        getSupportActionBar().setTitle("Change Password");
    }

    public boolean validations() {
        if (editNewPass.getText().toString().trim().isEmpty()) {
            AppUtils.showErrorOnTop(viewGroup, ChangePasswordActivity.this, "Please enter your new Password.");
            return false;
        } else if (editNewPass.getText().toString().trim().length() < 6) {
            AppUtils.showErrorOnTop(viewGroup, ChangePasswordActivity.this, "Password length should be greater than 5.");
            return false;
        } else {
            return true;
        }
    }

    private void updatePassword() {
        mAuth.getCurrentUser().updatePassword(editNewPass.getText().toString()).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                                          @Override
                                          public void onComplete(@NonNull Task<Void> task) {
                                              if (task.isSuccessful()) {
                                                  mAuth.signOut();
                                                  Intent loginIntent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                                                  loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                  startActivity(loginIntent);
                                                  finish();
                                                  Log.d("new pass", "User password updated.");
                                              }
                                          }
                                      }
                );
    }

    @OnClick(R.id.btn_submit)
    public void onClick() {
        if (validations()) {
            AuthCredential credential = EmailAuthProvider
                    .getCredential(mAuth.getCurrentUser().getEmail(), "123456");
            // Prompt the user to re-provide their sign-in credentials
            mAuth.getCurrentUser().reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            updatePassword();
                        }
                    });
        }
    }
}
