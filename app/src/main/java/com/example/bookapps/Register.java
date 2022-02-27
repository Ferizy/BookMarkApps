package com.example.bookapps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {
    EditText registerFullName,registerEmail,registerPassword,registerConfirm;
    Button createAccountButton,backToLogin;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerFullName = findViewById(R.id.registerFullName);
        registerEmail = findViewById(R.id.registerEmail);
        registerPassword = findViewById(R.id.registerPassword);
        registerConfirm = findViewById(R.id.registerConfirm);
        createAccountButton = findViewById(R.id.createAccountButton);
        backToLogin = findViewById(R.id.backToLoginButton);

        firebaseAuth = FirebaseAuth.getInstance();

        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = registerFullName.getText().toString();
                String email = registerEmail.getText().toString();
                String password = registerPassword.getText().toString();
                String confirmPassword = registerConfirm.getText().toString();

                if(fullName.isEmpty()){
                    registerFullName.setError("Full Name is Required");
                    return;
                }
                if(email.isEmpty()){
                    registerEmail.setError("Email is Required");
                    return;
                }
                if(password.isEmpty()){
                    registerPassword.setError("Password is Required");
                    return;
                }
                if(confirmPassword.isEmpty()){
                    registerConfirm.setError("Confirm Password is Required");
                    return;
                }
                if(!password.equals(confirmPassword)){
                    registerConfirm.setError("Password Do not Match.");
                    return;
                }

                Toast.makeText(Register.this,"SUCCESSFULLY CREATE ACCOUNT", Toast.LENGTH_LONG).show();

                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        startActivity(new Intent(getApplicationContext(), Login.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this,e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

        });

    }
}