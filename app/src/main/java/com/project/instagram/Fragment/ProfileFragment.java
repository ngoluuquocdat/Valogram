package com.project.instagram.Fragment;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.instagram.Adapter.MyPhotoAdapter;
import com.project.instagram.EditProfileActivity;
import com.project.instagram.MainActivity;
import com.project.instagram.Model.Post;
import com.project.instagram.Model.User;
import com.project.instagram.R;
import com.project.instagram.StartActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ProfileFragment extends Fragment {
    ImageView ic_logout, image_profile;
    TextView posts, following, followers, fullname, bio, username;
    Button edit_profile;
    private List<String> mySaves;

    RecyclerView recyclerView_saves;
    MyPhotoAdapter myPhotoAdapter_saves;
    List<Post> postsList_saves;


    RecyclerView recyclerView;
    MyPhotoAdapter myPhotoAdapter;
    List<Post> postsList;

    FirebaseUser firebaseUser;
    String profileid;

    ImageButton my_fotos, my_fotos_saved;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid = preferences.getString("profileid", "none");

        image_profile = view.findViewById(R.id.image_profile);
        posts = view.findViewById(R.id.posts);
        following = view.findViewById(R.id.following);
        followers = view.findViewById(R.id.followers);
        fullname = view.findViewById(R.id.fullname);
        bio = view.findViewById(R.id.bio);
        username = view.findViewById(R.id.username);
        edit_profile = view.findViewById(R.id.edit_profile);
        my_fotos = view.findViewById(R.id.my_fotos);
        my_fotos_saved = view.findViewById(R.id.save_fotos);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 3);
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        postsList = new ArrayList<>();
        myPhotoAdapter = new MyPhotoAdapter(getContext(), postsList);
        recyclerView.setAdapter(myPhotoAdapter);

        recyclerView_saves = view.findViewById(R.id.recycler_view_saved);
        recyclerView_saves.setHasFixedSize(true);
        GridLayoutManager linearLayoutManager_saves = new GridLayoutManager(getContext(), 3);
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView_saves.setLayoutManager(linearLayoutManager_saves);

        postsList_saves = new ArrayList<>();
        myPhotoAdapter_saves = new MyPhotoAdapter(getContext(), postsList_saves);
        recyclerView_saves.setAdapter(myPhotoAdapter_saves);

        recyclerView.setVisibility(View.VISIBLE);
        recyclerView_saves.setVisibility(View.GONE);

        myPhotos();
        userInfo();
        getFollowers();
        getPost();
        mysaves();


        if (profileid.equals(firebaseUser.getUid())){
            edit_profile.setText("Edit Profile");
        } else {
            checkFollow();
            my_fotos_saved.setVisibility(View.GONE);
        }
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn = edit_profile.getText().toString();
                if (btn.equals("Edit Profile")){
                    startActivity(new Intent(getContext(), EditProfileActivity.class));

                } else if (btn.equals("follow")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("follower").child(firebaseUser.getUid()).setValue(true);
                } else if (btn.equals("following")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("follower").child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        my_fotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView_saves.setVisibility(View.GONE);
            }
        });
        my_fotos_saved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.GONE);
                recyclerView_saves.setVisibility(View.VISIBLE);
            }
        });

        ic_logout = view.findViewById(R.id.ic_logout);
        ic_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });
        return view;
    }
    private void userInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null){
                    return ;
                }
                User user = snapshot.getValue(User.class);

                if(!user.getImageurl().equals("default")){
                    Glide.with(getContext()).load(user.getImageurl()).into(image_profile);
                }

                username.setText(user.getUsername());
                fullname.setText((user.getFullname()));
                bio.setText(user.getBio());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void checkFollow(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(profileid).exists()){
                    edit_profile.setText("following");
                } else {
                    edit_profile.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getFollowers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileid).child("follower");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileid).child("following");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void getPost(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileid)){
                        i++;
                    }
                }
                posts.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void myPhotos(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postsList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileid)){
                        postsList.add(post);
                    }
                }
                Collections.reverse(postsList);
//                Log.e("List post size:", postsList.size()+"");
//                for(Post post : postsList){
//                    Log.e("Post Image:", post.getPostimage());
//                }
                myPhotoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void showAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Logging out");
        builder.setMessage("Are you sure?");
        builder.setCancelable(false);
        builder.setPositiveButton(Html.fromHtml("<font color='#777777'>Yes</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                auth.signOut();
                startActivity(new Intent(getActivity(), StartActivity.class));
                finish();
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton(Html.fromHtml("<font color='#222222'>No</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
    private void mysaves(){
         mySaves = new ArrayList<>();
         DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid());
         reference.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                     mySaves.add(dataSnapshot.getKey());
                 }
                 readSaves();
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });

    }
    private void readSaves(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postsList_saves.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);

                    for (String id : mySaves){
                        if (post.getPostid().equals(id)){
                            postsList_saves.add(post);
                        }
                    }
                }
                myPhotoAdapter_saves.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void finish() {
        getActivity().finish();
    }
}