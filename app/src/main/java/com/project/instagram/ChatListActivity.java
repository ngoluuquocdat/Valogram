package com.project.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.instagram.Adapter.ChatUserAdapter;
import com.project.instagram.Model.Chat;
import com.project.instagram.Model.User;

import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatUserAdapter chatUserAdapter;
    private List<User> mUsers;
    private List<User> chatUserList;

    private List<String> chatUserIDList;

    DatabaseReference reference;
    FirebaseUser firebaseUser;

    EditText search_bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        search_bar = findViewById(R.id.search_bar);

        mUsers = new ArrayList<>();
        chatUserAdapter = new ChatUserAdapter(ChatListActivity.this, mUsers);
        recyclerView.setAdapter(chatUserAdapter);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        chatUserIDList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatUserIDList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    assert chat != null;
                    if (chat.getSender().equals(firebaseUser.getUid())) {
                        if(!chatUserIDList.contains(chat.getReceiver()))
                            chatUserIDList.add(chat.getReceiver());
                    }
                    if (chat.getReceiver().equals(firebaseUser.getUid())) {
                        if(!chatUserIDList.contains(chat.getSender()))
                            chatUserIDList.add(chat.getSender());
                    }
                }
                readChatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void searchUsers(String s){
        if(s.equals("")){
            //mUsers.clear();
            readChatList();
            return;
        }
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
                .startAt(s)
                .endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : datasnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    mUsers.add(user);
                }

                chatUserAdapter = new ChatUserAdapter(ChatListActivity.this, mUsers);
                recyclerView.setAdapter(chatUserAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readChatList() {
        chatUserList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    for (String id : chatUserIDList) {
                        assert user != null;
                        if (user.getId().equals(id)) {
                            chatUserList.add(user);
                        }
                    }
                }
                chatUserAdapter = new ChatUserAdapter(ChatListActivity.this, chatUserList);
                recyclerView.setAdapter(chatUserAdapter);
                //chatUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}