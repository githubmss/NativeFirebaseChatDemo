package com.mss.myfirebasedemo.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mss.myfirebasedemo.R;
import com.mss.myfirebasedemo.adapter.UserListAdapter;
import com.mss.myfirebasedemo.model.UserModel;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.mss.myfirebasedemo.activity.AppController.mAuth;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.rv_userList)
    RecyclerView rvUserList;
    UserListAdapter adapter;
    DatabaseReference database;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        database = FirebaseDatabase.getInstance().getReference();
        rvUserList.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        initUi();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void initUi() {
        progressDialog = new ProgressDialog(this);
        getSupportActionBar().setTitle("Home");
        progressDialog.setMessage("Getting Users...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        database.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<UserModel> userModelsList = new ArrayList<UserModel>();

                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    UserModel user = noteDataSnapshot.getValue(UserModel.class);
                    if (!user.getUid().equals(mAuth.getCurrentUser().getUid())) {
                        userModelsList.add(user);
                    }
                }
                if (userModelsList.size() > 0) {
                    adapter = new UserListAdapter(MainActivity.this, userModelsList);
                    rvUserList.setAdapter(adapter);
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, UpdateProfileActivity.class));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.signout) {
            mAuth.signOut();
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
        } else if (itemId == R.id.changepass) {
            startActivity(new Intent(MainActivity.this, ChangePasswordActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
