package com.project.instagram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.project.instagram.ChatActivity;
import com.project.instagram.Model.User;
import com.project.instagram.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.ViewHolder>{

    private Context mContext;
    private List<User> mUsers;


    public ChatUserAdapter(Context mContext, List<User> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_chat_item, parent, false);
        return new ChatUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final User user = mUsers.get(position);

        holder.username.setText(user.getUsername());
        holder.fullname.setText(user.getFullname());

        if(!user.getImageurl().equals("default")){
            Glide.with(mContext).load(user.getImageurl()).into(holder.image_profile);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                // gởi đi ID của người nhận tin nhắn
                intent.putExtra("userid", user.getId());
                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public TextView fullname;
        public CircleImageView image_profile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            fullname = itemView.findViewById(R.id.fullname);
            image_profile = itemView.findViewById(R.id.image_profile);
        }
    }
}

