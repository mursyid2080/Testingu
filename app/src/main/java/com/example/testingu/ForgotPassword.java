package com.example.testingu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ForgotPassword extends AppCompatActivity {

    private EditText emailEt;
    private Button submitBtn;
    private TextView loginTv;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        getSupportActionBar().hide();

        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();

        emailEt=findViewById(R.id.emailEt);
        submitBtn=findViewById(R.id.saveBtn);
        loginTv=findViewById(R.id.loginText);

        email=emailEt.getText().toString();

        submitBtn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                email=emailEt.getText().toString();
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(ForgotPassword.this, "Please enter your registered email", Toast.LENGTH_SHORT).show();
                    emailEt.setError("Email is required");
                    emailEt.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(ForgotPassword.this, "Please enter valid email", Toast.LENGTH_SHORT).show();
                    emailEt.setError("Email is required");
                    emailEt.requestFocus();
                }
                else{
                    resetPassword(email);
                }

            }
        });

        loginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

    }
    private void resetPassword(String email){
        fAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ForgotPassword.this, "Please check your inbox for password reset link", Toast.LENGTH_SHORT).show();

                    Intent i=new Intent(ForgotPassword.this, Login.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                }
            }
        });
    }
}