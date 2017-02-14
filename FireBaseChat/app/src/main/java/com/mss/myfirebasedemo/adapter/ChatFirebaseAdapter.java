package com.mss.myfirebasedemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.mss.myfirebasedemo.ClickListenerChatFirebase;
import com.mss.myfirebasedemo.R;
import com.mss.myfirebasedemo.model.ChatModel;

/**
 * Created by deepakgupta on 9/2/17.
 */

public class ChatFirebaseAdapter extends FirebaseRecyclerAdapter<ChatModel, ChatFirebaseAdapter.MyChatViewHolder> {

    private static final int RIGHT_MSG = 0;
    private static final int LEFT_MSG = 1;
    private static final int RIGHT_MSG_IMG = 2;
    private static final int LEFT_MSG_IMG = 3;
    private ClickListenerChatFirebase mClickListenerChatFirebase;
    private String nameUser;

    public ChatFirebaseAdapter(DatabaseReference ref, String nameUser, ClickListenerChatFirebase mClickListenerChatFirebase) {
        super(ChatModel.class, R.layout.item_message_left, ChatFirebaseAdapter.MyChatViewHolder.class, ref);
        this.nameUser = nameUser;
        this.mClickListenerChatFirebase = mClickListenerChatFirebase;
    }

    @Override
    public MyChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == RIGHT_MSG) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right, parent, false);
            return new MyChatViewHolder(view);
        } else if (viewType == LEFT_MSG) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left, parent, false);
            return new MyChatViewHolder(view);
        } else if (viewType == RIGHT_MSG_IMG) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right_img, parent, false);
            return new MyChatViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left_img, parent, false);
            return new MyChatViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        ChatModel model = getItem(position);
        if (model.getFromModel().getEmail().equals(nameUser) && model.getFileModel() == null) {
            return RIGHT_MSG;
        } else if (model.getFileModel() != null) {
            if (model.getFileModel().getType().equals("img") && model.getFromModel().getEmail().equals(nameUser)) {
                return RIGHT_MSG_IMG;
            } else {
                return LEFT_MSG_IMG;
            }
        } else {
            return LEFT_MSG;
        }
    }

    @Override
    protected void populateViewHolder(MyChatViewHolder viewHolder, ChatModel model, int position) {
        viewHolder.setTxtMessage(model.getMessage());
        if (model.getFileModel() != null) {
            viewHolder.setPhoto(model.getFileModel().getUrl_file());
        }
    }

    public class MyChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtMessage;
        ImageView imgChatPhoto;

        public MyChatViewHolder(View itemView) {
            super(itemView);
            imgChatPhoto = (ImageView) itemView.findViewById(R.id.img_chat);
            txtMessage = (TextView) itemView.findViewById(R.id.txt_receiver);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            ChatModel model = getItem(position);
            if (model.getFileModel() != null) {
                mClickListenerChatFirebase.clickImageChat(view, position,model.getFileModel().getUrl_file());
            }
        }

        public void setTxtMessage(String message) {
            if (txtMessage == null) return;
            txtMessage.setText(message);
        }

        public void setPhoto(String url) {
            if (imgChatPhoto == null) return;
            Glide.with(imgChatPhoto.getContext()).load(url)
                    .override(100, 100)
                    .fitCenter()
                    .into(imgChatPhoto);
            imgChatPhoto.setOnClickListener(this);
        }
    }

    private CharSequence converteTimestamp(String mileSegundos) {
        return DateUtils.getRelativeTimeSpanString(Long.parseLong(mileSegundos), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
    }

}
