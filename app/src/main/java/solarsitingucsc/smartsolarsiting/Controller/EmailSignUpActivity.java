package solarsitingucsc.smartsolarsiting.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import solarsitingucsc.smartsolarsiting.Model.User;
import solarsitingucsc.smartsolarsiting.R;


public class EmailSignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText mName;
    private EditText mEmailField;
    private EditText mPasswordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_sign_up);
        mEmailField = (EditText) findViewById(R.id.InputEmail);
        mPasswordField = (EditText) findViewById(R.id.InputPassword);
        mName = (EditText) findViewById(R.id.InputName);
        mAuth = FirebaseAuth.getInstance();

        //Toolbar setup
        Toolbar topToolBar = findViewById(R.id.my_toolbar);
        setSupportActionBar(topToolBar);

        Button createAccount = (Button) findViewById(R.id.SignUp);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString(), mName.getText().toString());
            }
        });
    }

    private void createAccount(final String email, String password, final String name) {
        if (!validateForm()) {
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            new User(user.getUid(), name, email);
                        }
                        sendEmailVerification();
                        // Sign in success, update UI with the signed-in user's information

                    } else {
                        Toast.makeText(EmailSignUpActivity.this,
                                "Authentication failed. " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    private void sendEmailVerification() {
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(EmailSignUpActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplication(), HomePageActivity
                                    .class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(EmailSignUpActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }
    }


    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String name = mName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }
        return valid;
    }
}
