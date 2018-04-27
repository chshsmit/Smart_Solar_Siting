package solarsitingucsc.smartsolarsiting.Controller;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import solarsitingucsc.smartsolarsiting.R;

public class LoginActivity extends AppCompatActivity {


    private static final String TAG = "LoginActivity";

    private FirebaseAuth mAuth;
    private EditText mEmailField;
    private EditText mPasswordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Toolbar setup
        Toolbar topToolBar = findViewById(R.id.my_toolbar);
        setSupportActionBar(topToolBar);

        //Views
        mEmailField = (EditText) findViewById(R.id.emailEditText);
        mPasswordField = (EditText) findViewById(R.id.passwordEditText);

        //Firebase
        mAuth = FirebaseAuth.getInstance();

        //On click listeners
        initializeOnCLickListeners();

        signIn("chshsmit@gmail.com", "WhatTheFuck");

    }

    //--------------------------------------------------------------------------------------------
    //Code to set up on click listeners
    //--------------------------------------------------------------------------------------------

    private void initializeOnCLickListeners(){
        //On click listener for signing in
        Button signInButton = findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());

            }
        });


        //On click listener to create account
        Button createAccount = findViewById(R.id.createAccount);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createNewUser = new Intent(getApplication(), EmailSignUpActivity.class);
                startActivity(createNewUser);

            }
        });
    }

    //--------------------------------------------------------------------------------------------
    //Fucntions for signin
    //--------------------------------------------------------------------------------------------

    private void signIn(String email, String passsword) {
        Log.d(TAG, "signIn:" + email);
//        if(!validateForm()){
//            return;
//        }

        //Start sign in with email
        mAuth.signInWithEmailAndPassword(email, passsword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //Sign in success
                    Log.d(TAG, "signInWithEmail:success");
                    changeToHomePage();

                } else {
                    //If sign in fails, display a message to the user
                    Log.w(TAG, "signInWithEmail:FAILED", task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Ensuring all necessary fields are filled out
    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString().trim();
        if(TextUtils.isEmpty(email)){
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString().trim();
        if(TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;


    }

    //--------------------------------------------------------------------------------------------
    //Functions to change activities
    //--------------------------------------------------------------------------------------------

    private void changeToHomePage(){
        Intent intent = new Intent(getApplication(),
                HomePageActivity.class);
        startActivity(intent);
    }

}
