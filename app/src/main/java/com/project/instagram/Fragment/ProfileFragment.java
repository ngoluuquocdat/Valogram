package com.project.instagram.Fragment;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.instagram.MainActivity;
import com.project.instagram.R;
import com.project.instagram.StartActivity;


public class ProfileFragment extends Fragment {
    TextView out;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        out = view.findViewById(R.id.out);
        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(getActivity(), StartActivity.class));
                finish();

            }
        });
        return view;
    }

    private void finish() {
        getActivity().finish();
    }
}