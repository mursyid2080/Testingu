package com.example.testingu;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;

    private String email, name, password;
    EditText emailEt, nameEt, passwordEt;
    Button saveBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize Firebase Auth
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // user use hasmap

        //init db
        fStore=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();

        //connect to widget
        emailEt = findViewById(R.id.emailEt);
        nameEt = findViewById(R.id.nameEt);
        passwordEt = findViewById(R.id.passwordEt);

        saveBtn = findViewById(R.id.saveBtn);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get text from edit text
                email = emailEt.getText().toString();
                name = nameEt.getText().toString();
                password = passwordEt.getText().toString();

                if(!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    //Firestore use hashmap

                    String uid=mAuth.getUid();

                    Map<String, Object> u= new HashMap<>();
                    u.put("fName", name);
                    u.put("fEmail", email);


                    fStore.collection("users").document(uid).set(u);

                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }

                else {
                    //toast
                }



            }
        });


    }


}