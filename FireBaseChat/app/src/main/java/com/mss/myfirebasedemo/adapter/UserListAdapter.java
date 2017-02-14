package com.mss.myfirebasedemo.adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mss.myfirebasedemo.R;
import com.mss.myfirebasedemo.activity.ChatMessageActivity;
import com.mss.myfirebasedemo.model.UserModel;

import java.io.IOException;
import java.util.List;

/**
 * Created by deepakgupta on 9/2/17.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    Context mContext;
    List<UserModel> userList;
    DatabaseReference database;

    public UserListAdapter(Context context, List<UserModel> userList) {
        mContext = context;
        this.userList = userList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View contactsView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_user_item, parent, false);
        return new ViewHolder(contactsView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserModel user = userList.get(position);
        if (user.getProfile() != null) {
            if (!user.getProfile().getName().equals("")) {
                holder.txtName.setText(user.getProfile().getName());
            } else {
                holder.txtName.setText(user.getEmail());
            }
        } else {
            holder.txtName.setText(user.getEmail());
            holder.imgUsers.setImageResource(R.drawable.user_icon);
        }


        if (user.getProfile() != null) {
            if (user.getProfile().getProfilePic() != null) {
                Bitmap imgbitmap = null;
                try {
                    imgbitmap = decodeFromFirebaseBase64(user.getProfile().getProfilePic());
                    holder.imgUsers.setImageBitmap(imgbitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                holder.imgUsers.setImageResource(R.drawable.user_icon);
            }
        }


        holder.txtDesc.setText(user.getUid());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtDesc;
        ImageView imgUsers;

        public ViewHolder(View itemView) {
            super(itemView);
            database = FirebaseDatabase.getInstance().getReference();
            txtName = (TextView) itemView.findViewById(R.id.user_title);
            txtDesc = (TextView) itemView.findViewById(R.id.user_description);
            imgUsers = (ImageView) itemView.findViewById(R.id.img_profile);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    UserModel userModel = userList.get(position);
                    Intent update = new Intent(mContext, ChatMessageActivity.class);
                    update.putExtra("update", userModel);
                    mContext.startActivity(update);
                }
            });

        }

    }

    public static Bitmap decodeFromFirebaseBase64(String image) throws IOException {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }
}
