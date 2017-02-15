package com.mss.myfirebasedemo.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mss.myfirebasedemo.ClickListenerChatFirebase;
import com.mss.myfirebasedemo.R;
import com.mss.myfirebasedemo.adapter.ChatFirebaseAdapter;
import com.mss.myfirebasedemo.model.ChatModel;
import com.mss.myfirebasedemo.model.FileModel;
import com.mss.myfirebasedemo.model.FromModel;
import com.mss.myfirebasedemo.model.ToModel;
import com.mss.myfirebasedemo.model.UserModel;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.mss.myfirebasedemo.activity.AppController.mAuth;

/**
 * Created by deepakgupta on 10/2/17.
 */

public class ChatMessageActivity extends AppCompatActivity implements ClickListenerChatFirebase {

    @Bind(R.id.messageRecyclerView)
    RecyclerView messageRecyclerView;
    @Bind(R.id.ibtn_attach)
    ImageView ibtnAttach;
    @Bind(R.id.editTextMessage)
    EditText editTextMessage;
    @Bind(R.id.img_message)
    ImageView ImgMessage;
    @Bind(R.id.contentRoot)
    LinearLayout contentRoot;
    private UserModel user;
    String chatKey, key, tempKey;
    private DatabaseReference database;
    private LinearLayoutManager mLinearLayoutManager;
    private File filePathImageCamera;
    private LinearLayout llTakePic;
    private LinearLayout llChoosePic;
    FromModel from;
    ToModel to;
    FirebaseStorage storage;
    private ViewGroup viewGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        initUi();
    }



    private void initUi() {
        user = (UserModel) getIntent().getSerializableExtra("update");
        chatKey = user.getUid() + "," + mAuth.getCurrentUser().getUid();
        tempKey = mAuth.getCurrentUser().getUid() + "," + user.getUid();
        database = FirebaseDatabase.getInstance().getReference();
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child("Chat").hasChild(chatKey)) {
                    key = chatKey;
                } else if (snapshot.child("Chat").hasChild(tempKey)) {
                    key = tempKey;
                } else {
                    key = chatKey;
                }
                getMessages();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        viewGroup = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
        from = new FromModel();
        to = new ToModel();
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        storage = FirebaseStorage.getInstance();
        populateUi();
    }

    private void populateUi() {
        from.setUid(mAuth.getCurrentUser().getUid());
        from.setEmail(mAuth.getCurrentUser().getEmail());
        to.setUid(user.getUid());
        to.setEmail(user.getEmail());
        if (user.getProfile() != null) {
            if (!user.getProfile().getName().equals("")) {
                getSupportActionBar().setTitle(user.getProfile().getName());
            } else {
                getSupportActionBar().setTitle(to.getEmail());
            }
        } else {
            getSupportActionBar().setTitle(to.getEmail());
        }
    }

    private void sendMessage() {
        ChatModel model = new ChatModel(from, to, null, editTextMessage.getText().toString(), "");
        if (key != null) {
            database.child("Chat").child(key).push().setValue(model);
            editTextMessage.setText("");
        }
    }

    private void getMessages() {
        final ChatFirebaseAdapter firebaseAdapter = new ChatFirebaseAdapter(database.child("Chat").child(key), mAuth.getCurrentUser().getEmail(), this);
        firebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = firebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    messageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });
        messageRecyclerView.setLayoutManager(mLinearLayoutManager);
        messageRecyclerView.setAdapter(firebaseAdapter);
    }

    @Override
    public void clickImageChat(View view, int position, String urlPhotoClick) {
        Intent intent = new Intent(this, FullScreenImageActivity.class);
        intent.putExtra("urlPhotoClick", urlPhotoClick);
        startActivity(intent);
    }

    @OnClick({R.id.img_message, R.id.ibtn_attach})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_message:
                if (validations()) {
                    sendMessage();
                }
                break;
            case R.id.ibtn_attach:
                selectProfile();
                break;
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
                photoCameraIntent();
                dialog.dismiss();
            }
        });
        llChoosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoGalleryIntent();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void photoCameraIntent() {
        String nomeFoto = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
        filePathImageCamera = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), nomeFoto + "camera.jpg");
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        it.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(filePathImageCamera));
        startActivityForResult(it, 1);
    }

    private void photoGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.app_name)), 2);
    }

    public boolean validations() {
        if (editTextMessage.getText().toString().trim().isEmpty()) {
            AppUtils.showErrorOnTop(viewGroup, ChatMessageActivity.this, "Please enter some text.");
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        StorageReference storageRef = storage.getReferenceFromUrl(Constants.URL_STORAGE_REFERENCE).child(Constants.FOLDER_STORAGE_IMG);
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    sendFileFirebase(storageRef, selectedImageUri);
                } else {

                }
            }
        } else if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (filePathImageCamera != null && filePathImageCamera.exists()) {
                    StorageReference imageCameraRef = storageRef.child(filePathImageCamera.getName() + "_camera");
                    sendFileFirebase(imageCameraRef, filePathImageCamera);
                } else {

                }
            }
        }
    }

    private void sendFileFirebase(StorageReference storageReference, final Uri file) {
        if (storageReference != null) {
            final String name = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
            StorageReference imageGalleryRef = storageReference.child(name + "_gallery");
            UploadTask uploadTask = imageGalleryRef.putFile(file);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    FileModel fileModel = new FileModel("img", downloadUrl.toString(), name, "");
                    ChatModel chatModel = new ChatModel(from, to, fileModel, "", Calendar.getInstance().getTime().getTime() + "");
                    if (key != null) {
                        database.child("Chat").child(key).push().setValue(chatModel);
                    }
                }
            });
        }
    }

    private void sendFileFirebase(StorageReference storageReference, final File file) {
        if (storageReference != null) {
            UploadTask uploadTask = storageReference.putFile(Uri.fromFile(file));
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    FileModel fileModel = new FileModel("img", downloadUrl.toString(), file.getName(), file.length() + "");
                    ChatModel chatModel = new ChatModel(from, to, fileModel, "", Calendar.getInstance().getTime().getTime() + "");
                    if (key != null) {
                        database.child("Chat").child(key).push().setValue(chatModel);
                    }
                }
            });
        }
    }

}
