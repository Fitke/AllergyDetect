package com.example.allergydetect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.allergydetect.Helpers.CloudFirestore;
import com.example.allergydetect.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterUser extends AppCompatActivity {

    private Button btnRegister;
    private EditText etRegName, etRegEmail, etRegPassword;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    CloudFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        db = new CloudFirestore(true);

        mAuth = FirebaseAuth.getInstance();

        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        etRegEmail = findViewById(R.id.etRegEmail);
        etRegName = findViewById(R.id.etRegName);
        etRegPassword = findViewById(R.id.etRegPassword);
        progressBar = findViewById(R.id.progressBarRegister);
    }

    private void registerUser(){
        String email = etRegEmail
                .getText()
                .toString()
                .trim();
        String password = etRegPassword
                .getText()
                .toString()
                .trim();
        String name = etRegName
                .getText()
                .toString()
                .trim();

        if(name.isEmpty()){
            etRegName.setError("Name is Required!");
            etRegName.requestFocus();
            return;
        }
        if(password.isEmpty()){
            etRegPassword.setError("Password is Required!");
            etRegPassword.requestFocus();
            return;
        }
        if(email.isEmpty()){
            etRegEmail.setError("Email is Required!");
            etRegEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etRegEmail.setError("Please provide a valid Email");
            etRegEmail.requestFocus();
            return;
        }
        if(password.length() < 6){
            etRegPassword.setError("Minimum password length is 6 characters ");
            etRegPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    User user = new User(mAuth.getCurrentUser().getUid(),
                            name,
                            email);
                    if(db.PostUser(user)){

                        Toast.makeText(RegisterUser.this, "Failed to Register Try again!", Toast.LENGTH_LONG).show();

                    }else {

                        Toast.makeText(RegisterUser.this, "User has been registered", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(RegisterUser.this, LoginUser.class);
                        startActivity(intent);
                    }
                } else {

                    Toast.makeText(RegisterUser.this, "This user is already registered", Toast.LENGTH_LONG).show();

                    Log.w("Register User", "Error adding document", task.getException());

                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

}