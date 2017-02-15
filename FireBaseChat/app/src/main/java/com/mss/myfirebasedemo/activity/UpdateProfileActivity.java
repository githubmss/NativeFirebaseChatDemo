package com.mss.myfirebasedemo.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mss.myfirebasedemo.R;
import com.mss.myfirebasedemo.model.ProfileModel;
import com.mss.myfirebasedemo.model.UserModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.mss.myfirebasedemo.activity.AppController.mAuth;

/**
 * Created by deepakgupta on 14/2/17.
 */

public class UpdateProfileActivity extends AppCompatActivity {

    @Bind(R.id.btn_update)
    Button btnUpdate;
    @Bind(R.id.edit_name)
    EditText editName;
    @Bind(R.id.edit_desc)
    EditText editDesc;
    @Bind(R.id.img_user)
    ImageView imgUser;
    @Bind(R.id.txt_email)
    TextView txtEmail;
    private DatabaseReference database;
    private ViewGroup viewGroup;
    private LinearLayout llTakePic;
    private LinearLayout llChoosePic;
    private String imageEncoded;

    boolean imgCheck = false;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        ButterKnife.bind(this);
        initUi();
    }

    private void initUi() {
        viewGroup = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
        progressDialog = new ProgressDialog(this);
        database = FirebaseDatabase.getInstance().getReference();
        getSupportActionBar().setTitle("Update Profile");
        progressDialog.setMessage("getting profile...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child("users").child(mAuth.getCurrentUser().getUid()).hasChild("profile")) {
                    database.child("users").child(mAuth.getCurrentUser().getUid()).child("profile").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            progressDialog.dismiss();
                            ProfileModel profile = snapshot.getValue(ProfileModel.class);
                            editName.setText(profile.getName());
                            editDesc.setText(profile.getDesc());
                            txtEmail.setText(mAuth.getCurrentUser().getEmail());
                            if (profile.getProfilePic() != null) {
                                try {
                                    Bitmap imgbitmap = decodeFromFirebaseBase64(profile.getProfilePic());
                                    imgUser.setImageBitmap(imgbitmap);
                                    if (!imgCheck) {
                                        encodeBitmapAndSaveToFirebase(imgbitmap);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            progressDialog.dismiss();
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Log.d("no", "not exists");
                    txtEmail.setText(mAuth.getCurrentUser().getEmail());
                    editName.setText("");
                    editDesc.setText("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });


    }

    public static Bitmap decodeFromFirebaseBase64(String image) throws IOException {
        byte[] decodedByteArray = Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

    @OnClick({R.id.btn_update, R.id.img_user})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_update:
                if (validations()) {
                    if (mAuth.getCurrentUser().getUid() != null) {
                        UserModel user = new UserModel();
                        ProfileModel profileModel = new ProfileModel();
                        profileModel.setName(editName.getText().toString());
                        profileModel.setDesc(editDesc.getText().toString());
                        profileModel.setProfilePic(imageEncoded);
                        user.setProfile(profileModel);
                        user.setEmail(mAuth.getCurrentUser().getEmail());
                        user.setUid(mAuth.getCurrentUser().getUid());
                        database.child("users").child(mAuth.getCurrentUser().getUid()).setValue(user);
                        Toast.makeText(getApplicationContext(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                break;
            case R.id.img_user:
                selectProfile();
                break;
        }
    }


    public boolean validations() {
        if (editName.getText().toString().trim().isEmpty()) {
            AppUtils.showErrorOnTop(viewGroup, UpdateProfileActivity.this, "Please enter your Name.");
            return false;
        } else {
            return true;
        }
    }

    private void selectProfile() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_select_picture);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.CENTER;
        wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        llTakePic = (LinearLayout) dialog.findViewById(R.id.ll_take_pic);
        llChoosePic = (LinearLayout) dialog.findViewById(R.id.ll_choose_pic);
        llTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 2);
                dialog.dismiss();
            }
        });

        llChoosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryImageIntent, 3);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 2) {
                try {
                    Bitmap imageBitmap = new UserPicture(data.getData(), getContentResolver()).getBitmap();
                    Log.d("bitmp", imageBitmap.toString());
                    imgUser.setImageBitmap(imageBitmap);
                    imgCheck = true;
                    encodeBitmapAndSaveToFirebase(imageBitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 3) {
                try {
                    Bitmap imageBitmap = new UserPicture(data.getData(), getContentResolver()).getBitmap();
                    Log.d("bitmp", imageBitmap.toString());
                    imgCheck = true;
                    imgUser.setImageBitmap(imageBitmap);
                    encodeBitmapAndSaveToFirebase(imageBitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }
}
