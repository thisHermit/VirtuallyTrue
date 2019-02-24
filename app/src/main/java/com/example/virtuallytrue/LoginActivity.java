package com.example.virtuallytrue;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private Button Loginbutton;
    private EditText Useremail, UserPassword;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;
    private Button loginButton;
    //private CallbackManager mCallbackManager;
    //public static final String TAG = "FACELOG";
    private GoogleApiClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private ImageView google_signin;
    private static final String TAG = "Login_Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        google_signin = findViewById(R.id.google);
        Useremail = findViewById(R.id.login_email);
        UserPassword = findViewById(R.id.login_password);
        Loginbutton = (Button) findViewById(R.id.login_button);
        loadingbar = new ProgressDialog(this);


        Loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allowingusertologin();
//                sendusertomainactivity();
            }
        });

// Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("914707288449-87e4007f5csd236c6rpc34tnveb0q5ts.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = new GoogleApiClient.Builder(this).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Toast.makeText(LoginActivity.this, "Connection to Google SignIn Failed  ", Toast.LENGTH_SHORT).show();
            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        google_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            loadingbar.setTitle("Google Sign In");
            loadingbar.setMessage("Please wait while we are processing your request...");
            loadingbar.setCanceledOnTouchOutside(true);
            loadingbar.show();

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                Toast.makeText(this, "Please wait while we are getting your authentication result", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Can't get your authentication result.", Toast.LENGTH_SHORT).show();
                loadingbar.dismiss();

            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            sendusertomainactivity();
                        } else {
                            String message = task.getException().toString();
                            Toast.makeText(LoginActivity.this, "Not Authenticated. Try again. Error:" + message, Toast.LENGTH_SHORT).show();
                            sendusertologinactivity();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                        loadingbar.dismiss();


                        // ...
                    }
                });
    }

    private void sendusertologinactivity() {
        Intent mainIntent = new Intent(LoginActivity.this, LoginActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }


    private void allowingusertologin() {
        String email = Useremail.getText().toString();
        String password = UserPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please type your email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please type your password", Toast.LENGTH_SHORT).show();
        } else {
            loadingbar.setTitle("Login");
            loadingbar.setMessage("Please wait while we are processing your request...");
            loadingbar.setCanceledOnTouchOutside(true);
            loadingbar.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                sendusertomainactivity();
                                Toast.makeText(LoginActivity.this, "You are Logged in Successfully.", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Error occured:" + message, Toast.LENGTH_SHORT).show();
                            }
                            loadingbar.dismiss();
                        }
                    });
        }
    }


    private void sendusertoregisteractivity() {
    }
    private void sendusertomainactivity() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
