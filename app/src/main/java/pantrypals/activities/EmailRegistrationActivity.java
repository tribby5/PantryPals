package pantrypals.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.databaes.pantrypals.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class EmailRegistrationActivity extends AppCompatActivity {


    private static EmailRegistrationActivity checkLogin;
    private FirebaseAuth mAuth;


    //UI components
    private EditText newUserEmail;
    private EditText newUserPassword;
    private EditText newUserPasswordConfirm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_registration);

        setOnClick();
        checkLogin = this;
        newUserEmail = (EditText)findViewById(R.id.TFemail);
        newUserPassword = (EditText)findViewById(R.id.TFpassword);
        newUserPasswordConfirm = (EditText) findViewById(R.id.TFpasswordConfirm);

        mAuth= FirebaseAuth.getInstance();
    }


    //Allow access to this activity through this method
    public static EmailRegistrationActivity getInstance(){
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


                final String email = newUserEmail.getText().toString();
                final String password = newUserPassword.getText().toString();

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
                Log.d("EMAIL", email);

                if(!cancel) {

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(EmailRegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d("createCredentials", "createUserWithEmail:onComplete:" + task.isSuccessful());

                                    if(task.isSuccessful()) {


                                        //Verification email
                                        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {

                                                            Log.d("EmailSent", "Email sent.");
                                                        }
                                                    }
                                                });
                                       // Toast.makeText(EmailRegistrationActivity.this, getResources().getString(R.string.email_reg_confirmed), Toast.LENGTH_LONG).show();

                                        Intent accountIntent = new Intent(getApplicationContext(), AccountCreationActivity.class);
                                        startActivity(accountIntent);

                                    }
                                }
                            });

                }
            }
        });
    }



    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }




}
