package com.example.bookapps.view.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookapps.R;
import com.example.bookapps.view.activity.Login;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SettingFragment extends Fragment implements OnBackPressed {

    private View passwordLayout, emailLayout;
    private Button changeButton, resetButton, changeEmailButton, resetPasswordButton, backButton;
    private EditText newPassword, confirmNewPassword, newEmail;
    private FirebaseUser user ;
    private FirebaseAuth auth;
    private TextView emailTxt;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_setting, container, false);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        passwordLayout = root.findViewById(R.id.resetPasswordLayout);
        emailLayout = root.findViewById(R.id.changeMailLayout);

        changeEmailButton = root.findViewById(R.id.changeMailButton);
        changeButton = root.findViewById(R.id.changeButton);
        resetButton = root.findViewById(R.id.resetButton);
        resetPasswordButton = root.findViewById(R.id.resetPasswrodButton);
        backButton = root.findViewById(R.id.backButton);

        emailTxt = root.findViewById(R.id.emailText);


        newPassword = root.findViewById(R.id.newPassText);
        confirmNewPassword = root.findViewById(R.id.confirmNewPassText);
        newEmail = root.findViewById(R.id.newEmailText);

        emailTxt.setText(""+ user.getEmail());



        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeEmailButton.setVisibility(View.INVISIBLE);
                passwordLayout.setVisibility(View.VISIBLE);
                backButton.setVisibility(View.VISIBLE);

            }
        });

        changeEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPasswordButton.setVisibility(View.INVISIBLE);
                emailLayout.setVisibility(View.VISIBLE);
                backButton.setVisibility(View.VISIBLE);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
                emailLayout.setVisibility(View.INVISIBLE);
                passwordLayout.setVisibility(View.INVISIBLE);
                resetPasswordButton.setVisibility(View.VISIBLE);
                changeEmailButton.setVisibility(View.VISIBLE);
                backButton.setVisibility(View.INVISIBLE);
            }
        });
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newEmail.getText().toString().isEmpty()){
                    newEmail.setError("Please enter your new email!");
                    return;
                }
                user = auth.getCurrentUser();
                user.updateEmail(newEmail.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getActivity(), "Please verify your new email", Toast.LENGTH_SHORT).show();
                        user.sendEmailVerification();
                        emailLayout.setVisibility(View.INVISIBLE);
                        passwordLayout.setVisibility(View.INVISIBLE);
                        resetPasswordButton.setVisibility(View.VISIBLE);
                        changeEmailButton.setVisibility(View.VISIBLE);
                        backButton.setVisibility(View.INVISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newPassword.getText().toString().isEmpty()){
                    newPassword.setError("Password is Required");
                    return;
                }
                if(confirmNewPassword.getText().toString().isEmpty()){
                    confirmNewPassword.setError("Password is Required");
                    return;
                }
                if(!newPassword.getText().toString().equals(confirmNewPassword.getText().toString())){
                    confirmNewPassword.setError("Password Do not Match.");
                    return;
                }

                user.updatePassword(newPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(),"PASSWORD UPDATE NOW YOU CAN LOGIN WITH A NEW PASSWORD", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getActivity(), Login.class));
                        FirebaseAuth.getInstance().signOut();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        return root;
    }

    @Override
    public boolean onBackPressed() {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        int count = manager.getBackStackEntryCount();

        if (count == 0) {
            //action not popBackStack
            return true;
        } else {
            return false;
        }
    }
    private void clear(){
        newPassword.getText().clear();
        confirmNewPassword.getText().clear();
        newEmail.getText().clear();
    }


}