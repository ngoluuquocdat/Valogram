package com.project.instagram.Fragment;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.instagram.MainActivity;
import com.project.instagram.R;
import com.project.instagram.StartActivity;


public class ProfileFragment extends Fragment {
    ImageView ic_logout;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ic_logout = view.findViewById(R.id.ic_logout);
        ic_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });
        return view;
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

    private void finish() {
        getActivity().finish();
    }
}