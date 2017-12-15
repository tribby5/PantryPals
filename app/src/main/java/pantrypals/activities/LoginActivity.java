package pantrypals.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.databaes.pantrypals.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


/**
 * A login screen that offers login via email/password.
 * Google log-in also included
 */
public class LoginActivity extends AppCompatActivity{
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final int RC_GOOGLE_SIGN_IN = 100;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private GoogleApiClient googleApiClient;

    //tracks whether there exists users in the system
    private static boolean existUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //TODO remove after testing
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            FirebaseAuth.getInstance().signOut();
        }

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    existUser = true;
                    Log.d("signedIn", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("signedOut", "onAuthStateChanged:signed_out");
                }

            }
        };


        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);


        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });


        regularSignInOnClick();
        googleSignInOnClick();

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            allowAccess();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    private void regularSignInOnClick(){
        //When sign in button is clicked
        Button mEmailSignInButton = (Button) findViewById(R.id.Bsignin);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        //When register button clicked
        Button Bregister = (Button)findViewById(R.id.Bregister);
        Bregister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toggle to true because there now exists users in the system
                LoginActivity.existUser = true;
                Intent register = new Intent(getApplicationContext(), EmailRegistrationActivity.class);
                startActivity(register);
            }
        });
    }

    private void googleSignInOnClick(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, null).addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        final SignInButton googleSignIn= (SignInButton) findViewById(R.id.google_signin);
        googleSignIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptGoogleLogin();
            }
        });
    }



    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        else {

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("attemptedLogin", "signInWithEmail:onComplete:" + task.isSuccessful());

                            if (!task.isSuccessful()) {
                                Log.w("failedLogin", "signInWithEmail:failed", task.getException());
                                Toast.makeText(LoginActivity.this, R.string.auth_failed,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                               allowAccess();
                            }
                        }
                    });
        }

    }

    private void attemptGoogleLogin(){
        Intent googleSignin = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(googleSignin, RC_GOOGLE_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_GOOGLE_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            getUserDataFromGoogle(result);
        }
    }

    private void getUserDataFromGoogle(GoogleSignInResult result){
        if(result.isSuccess()){
            GoogleSignInAccount user = result.getSignInAccount();

            AuthCredential credential = GoogleAuthProvider.getCredential(user.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.userAccounts))
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists()) {
                                                    allowAccess();
                                                }
                                                else{
                                                    Intent account = new Intent(getApplicationContext(), AccountCreationActivity.class);
                                                    startActivity(account);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                Log.d("WORKS", "UID: " + mAuth.getCurrentUser().getUid());
                            }
                            else{
                                Log.d("GOOGLE_AUTH_LOGIN_ERROR", "ERROR IN LOGIN");
                            }
                        }
                    });
        }
        else{
            Log.d("GOOGLE_AUTH_LOGIN_ERROR", "task not successful");
        }
    }

    private void allowAccess(){
        Log.d("WORKS", "ALLOWING ACCESS: " + FirebaseAuth.getInstance().getCurrentUser().getUid());
        Intent mainIntent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(mainIntent);
    }

    public static boolean isEmailValid(String email) {
        //TODO replace logic
        return email.contains("@");
    }

    public static boolean isPasswordValid(String password) {
        //Currently requirement is just 6 characters, may augment later
        return password.length() >= 6;
    }

    /**
     * Create modal which is a form for allowing users to enter their email to reset their password
     */
    public void resetPassword(View v){

        //builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.modal_reset_title));

        //https://stackoverflow.com/questions/18371883/how-to-create-modal-dialog-box-in-android
        LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        final View dialogView = inflater.inflate(R.layout.modal_reset_password, null);
        builder.setView(dialogView);

        final TextView address = (TextView) dialogView.findViewById(R.id.emailReset);

        builder.setPositiveButton(getResources().getString(R.string.reset_password_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendResetEmail(address.getText().toString());
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void sendResetEmail(String address){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(address);
        Toast.makeText(LoginActivity.this, getResources().getString(R.string.reset_email_confirm) + address, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}

