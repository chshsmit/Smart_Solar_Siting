package solarsitingucsc.smartsolarsiting.Controller;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.util.Arrays;
import java.util.Collection;

import solarsitingucsc.smartsolarsiting.Model.User;
import solarsitingucsc.smartsolarsiting.R;

public class LoginActivity extends AppCompatActivity {


    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth mAuth;
//    private static final DataBaseAPI databaseAPI = DataBaseAPI.getDataBase();
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        /////////FACEBOOK LOGIN/////////
        final Activity activity = this;
        setUpFaceBook();

        /////////GOOGLE LOGIN/////////
        setUpGoogle();

        /////////BUTTON LISTENERS/////////
        FloatingActionButton facebookLogin = findViewById(R.id.FacebookFloatingButton);
        facebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collection<String> permission = Arrays.asList("public_profile", "user_friends");
                LoginManager.getInstance().logInWithReadPermissions(activity, permission);
            }
        });

        FloatingActionButton googleLogin = findViewById(R.id.GoogleFloatingButton);
        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        Button emailLogin = findViewById(R.id.loginButton);
        emailLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText email = findViewById(R.id.emailInput);
                EditText password = findViewById(R.id.passwordIn);
                signInEmail(email.getText().toString(), password.getText().toString());
            }
        });

        Button createAccount = findViewById(R.id.createAccount);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createNewUser = new Intent(getApplication(), EmailSignUpActivity.class);
                startActivity(createNewUser);

            }
        });
    }

    private void setUpFaceBook(){
        AppEventsLogger.activateApp(getApplication());
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        signInFacebook(loginResult.getAccessToken());
                    }
                    @Override
                    public void onCancel() {
                        //TODO Deal with this
                    }
                    @Override
                    public void onError(FacebookException exception) {
                        exception.printStackTrace();
                    }
                });
    }

    private void setUpGoogle(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }


    public void signInEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            onSuccessfulSignUp();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this,
                                    "Authentication failed. " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signInFacebook(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            onSuccessfulSignUp();
                        } else {
                            Toast.makeText(LoginActivity.this,
                                    "Authentication failed.. " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void singInGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            onSuccessfulSignUp();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this,
                                    "Authentication failed. . " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void onSuccessfulSignUp(){
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            Intent intent = new Intent(getApplication(), MainActivity.class);
                            if (!snapshot.child(user.getUid()).exists()) {
                                new User(user.getUid(), user.getDisplayName(), user.getEmail());
                            }
                            startActivity(intent);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 64206)
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                singInGoogle(account);
            } catch (ApiException ignored) {

            }
        }
    }
//
//    private static final String TAG = "LoginActivity";
//
//    private FirebaseAuth mAuth;
//    private EditText mEmailField;
//    private EditText mPasswordField;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mAuth = FirebaseAuth.getInstance();
//
//        if(mAuth.getCurrentUser() == null) {
//
//            setContentView(R.layout.activity_login);
//
//            //Toolbar setup
//            Toolbar topToolBar = findViewById(R.id.my_toolbar);
//            setSupportActionBar(topToolBar);
//
//            //Views
//            mEmailField = (EditText) findViewById(R.id.emailEditText);
//            mPasswordField = (EditText) findViewById(R.id.passwordEditText);
//        }else
//            changeToHomePage();
//    }
//
//    //--------------------------------------------------------------------------------------------
//    //Code to set up on click listeners
//    //--------------------------------------------------------------------------------------------
//
//    private void initializeOnCLickListeners() {
//        //On click listener for signing in
//        Button signInButton = findViewById(R.id.signInButton);
//        signInButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
//
//            }
//        });
//
//
//        //On click listener to create account
//        Button createAccount = findViewById(R.id.createAccount);
//        createAccount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent createNewUser = new Intent(getApplication(), EmailSignUpActivity.class);
//                startActivity(createNewUser);
//
//            }
//        });
//    }
//
//    //--------------------------------------------------------------------------------------------
//    //Fucntions for signin
//    //--------------------------------------------------------------------------------------------
//
//    private void signIn(String email, String passsword) {
//        Log.d(TAG, "signIn:" + email);
//        if (!validateForm()) {
//            return;
//        }
//
//        //Start sign in with email
//        mAuth.signInWithEmailAndPassword(email, passsword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if (task.isSuccessful()) {
//                    //Sign in success
//                    Log.d(TAG, "signInWithEmail:success");
//                    changeToHomePage();
//
//                } else {
//                    //If sign in fails, display a message to the user
//                    Log.w(TAG, "signInWithEmail:FAILED", task.getException());
//                    Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
//
//    //Ensuring all necessary fields are filled out
//    private boolean validateForm() {
//        boolean valid = true;
//
//        String email = mEmailField.getText().toString().trim();
//        if (TextUtils.isEmpty(email)) {
//            mEmailField.setError("Required.");
//            valid = false;
//        } else {
//            mEmailField.setError(null);
//        }
//
//        String password = mPasswordField.getText().toString().trim();
//        if (TextUtils.isEmpty(password)) {
//            mPasswordField.setError("Required.");
//            valid = false;
//        } else {
//            mPasswordField.setError(null);
//        }
//
//        return valid;
//
//
//    }
//
//    //--------------------------------------------------------------------------------------------
//    //Functions to change activities
//    //--------------------------------------------------------------------------------------------
//
//    private void changeToHomePage() {
//        Intent intent = new Intent(getApplication(),
//                HomePageActivity.class);
//        startActivity(intent);
//    }

}
