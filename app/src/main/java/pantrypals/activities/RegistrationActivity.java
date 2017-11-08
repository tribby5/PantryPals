package pantrypals.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.databaes.pantrypals.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegistrationActivity extends AppCompatActivity {

    private static RegistrationActivity checkLogin;
    private FirebaseAuth mAuth;

    //UI components
    private EditText newUserFirstName;
    private EditText newUserLastName;
    private EditText newUserEmail;
    private EditText newUserPassword;
    private EditText newUserPasswordConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setOnClick();
        checkLogin = this;
        newUserFirstName = (EditText)findViewById(R.id.TFnameFirst);
        newUserLastName = (EditText)findViewById(R.id.TFnameLast);
        newUserEmail = (EditText)findViewById(R.id.TFemail);
        newUserPassword = (EditText)findViewById(R.id.TFpassword);
        newUserPasswordConfirm = (EditText) findViewById(R.id.TFpasswordConfirm);

        mAuth= FirebaseAuth.getInstance();
    }

    //Allow access to this activity through this method
    public static RegistrationActivity getInstance(){
        return checkLogin;
    }

    private void setOnClick() {
        Button Bregister = (Button)findViewById(R.id.Bregister);
        Bregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Set default errors
                newUserEmail.setError(null);
                newUserPassword.setError(null);
                newUserPasswordConfirm.setError(null);


                String email = newUserEmail.getText().toString();
                String password = newUserPassword.getText().toString();

                Boolean cancel = false;
                View focusView = null;

                if(!LoginActivity.isEmailValid(email)){
                    newUserEmail.setError(getResources().getString(R.string.error_invalid_email));
                    focusView = newUserEmail;
                    cancel = true;
                }

                if(!LoginActivity.isPasswordValid(password)){
                    newUserPassword.setError(getResources().getString(R.string.error_invalid_password));
                    focusView = newUserPassword;
                    cancel = true;
                }

                if(!password.equals(newUserPasswordConfirm.getText().toString())){
                    newUserPasswordConfirm.setError(getResources().getString(R.string.error_invalid_password_confirm));
                    focusView = newUserPasswordConfirm;
                    cancel = true;
                }

                if(cancel){
                    focusView.requestFocus();
                }


                if(!cancel) {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d("createCredentials", "createUserWithEmail:onComplete:" + task.isSuccessful());

                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(RegistrationActivity.this, R.string.auth_failed,
                                                Toast.LENGTH_LONG).show();
                                    }
                                    else{
                                        // Set DisplayName as name
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        String displayName = newUserFirstName.getText().toString() + " " + newUserLastName.getText().toString();
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(displayName).build();
                                        user.updateProfile(profileUpdates);

                                        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d("EmailSent", "Email sent.");
                                                        }
                                                    }
                                                });
                                        Toast.makeText(RegistrationActivity.this, getResources().getString(R.string.reg_confirmed), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}
