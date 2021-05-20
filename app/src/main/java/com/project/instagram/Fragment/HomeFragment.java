package com.project.instagram.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.instagram.Adapter.PostAdapter;
import com.project.instagram.Model.Post;
import com.project.instagram.Model.User;
import com.project.instagram.R;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    TextView reminder;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postLists;
    private User user;

    private List<String> followingList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        reminder = view.findViewById(R.id.reminder);

        recyclerView = view.findViewById(R.id.rv_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postLists = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postLists);
        recyclerView.setAdapter(postAdapter);

        checkFollowing();

        return view;
    }

    private void checkFollowing()
    {
        followingList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                followingList.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    followingList.add(snapshot.getKey());
                }
                readPost();;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private  void readPost()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postLists.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    Post post = snapshot.getValue(Post.class);
                    for(String id: followingList)
                    {
                        if(post.getPublisher().equals(id))
                        {
                            postLists.add(post);
                        }
                    }
                }
                if(postLists.size() == 0){
                    reminder.setVisibility(View.VISIBLE);
                } else {
                    reminder.setVisibility(View.GONE);
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}