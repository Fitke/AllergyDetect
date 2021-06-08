package com.example.allergydetect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.google.firebase.auth.FirebaseUser;

public class LoginUser extends AppCompatActivity {

    private TextView signUp;
    private EditText etEmail, etPassword;
    private Button login;
    private CheckBox remember;

    private static CloudFirestore db;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        progressBar = findViewById(R.id.progressBarLogin);

        SharedPreferences preferences = getSharedPreferences("checkbox",MODE_PRIVATE);
        String checkbox = preferences.getString("remember","");
        if(checkbox.equals("true")){
            db = new CloudFirestore(false);
            Intent intent = new Intent(LoginUser.this, UserActivity.class);
            startActivity(intent);
        }else if(checkbox.equals("false")){
            Toast.makeText(LoginUser.this, "Please Sign In", Toast.LENGTH_SHORT).show();
        }
        remember = findViewById(R.id.cbRemember);
        remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(buttonView.isChecked()){

                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "true");
                    editor.apply();
                    Toast.makeText(LoginUser.this, "Checked", Toast.LENGTH_SHORT).show();

                }else if(!buttonView.isChecked()){

                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "false");
                    editor.apply();
                    Toast.makeText(LoginUser.this, "Unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        login = findViewById(R.id.btnLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });

        signUp = findViewById(R.id.tvSignUp);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginUser.this, RegisterUser.class);
                startActivity(intent);
            }
        });
    }

    public static CloudFirestore getDb() {
        CloudFirestore DB = db;
        return DB;
    }

    /* @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){

        }else {

            Intent intent = new Intent(LoginUser.this, UserActivity.class);
            startActivity(intent);
            finish();
        }
    }*/

    private void userLogin(){
        String email = etEmail.getText()
                .toString()
                .trim();
        String password = etPassword.getText()
                .toString()
                .trim();
        if(email.isEmpty()){
            etEmail.setError("Email is required!");
            etEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError("Please enter a valid Email");
            etEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }
        if(password.length() < 6){
            etPassword.setError("Minimum length is 6 characters");
            etPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    db = new CloudFirestore(false);
                    if(user.isEmailVerified()) {
                        Intent intent = new Intent(LoginUser.this, UserActivity.class);
                        startActivity(intent);
                    }else {
                        user.sendEmailVerification();
                        Toast.makeText(LoginUser.this, "We've sent you an email for verification", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(LoginUser.this, "failed to login! Email and Password combination is wrong", Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}