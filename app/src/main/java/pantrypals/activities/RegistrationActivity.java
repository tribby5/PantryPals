package pantrypals.activities;

import android.content.Context;
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
import com.google.common.collect.Maps;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import pantrypals.models.Pantry;
import pantrypals.models.User;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RegistrationActivity extends AppCompatActivity {

    private static RegistrationActivity checkLogin;
    private FirebaseAuth mAuth;

    //UI components
    private EditText newUserFirstName;
    private EditText newUserLastName;
    private EditText newUserEmail;
    private EditText newUserPassword;
    private EditText newUserPasswordConfirm;
    private EditText newUserBio;
    private EditText newUserPreferences;
    private EditText newUserRestrictions;

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
        newUserBio = (EditText) findViewById(R.id.TFbio);
        newUserPreferences = (EditText) findViewById(R.id.TFpreferences);
        newUserRestrictions = (EditText) findViewById(R.id.TFrestrictions);

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
                newUserBio.setError(null);
                newUserPreferences.setError(null);
                newUserRestrictions.setError(null);


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
                            .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d("createCredentials", "createUserWithEmail:onComplete:" + task.isSuccessful());

                                    if(task.isSuccessful()) {

                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        String displayName = newUserFirstName.getText().toString() + " " + newUserLastName.getText().toString();
                                        String bio = newUserBio.getText().toString();
                                        Map<String, Boolean> preferences = extractFields(newUserPreferences.getText().toString());
                                        Map<String, Boolean> restrictions = extractFields(newUserRestrictions.getText().toString());
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(displayName).build();
                                        user.updateProfile(profileUpdates);


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
                                        Toast.makeText(RegistrationActivity.this, getResources().getString(R.string.reg_confirmed), Toast.LENGTH_LONG).show();
                                        writeNewUser(user.getUid(), displayName, email, bio, preferences, restrictions);
//                                      }
                                    }
                                }
                            });
                }
            }
        });
    }

    private Map<String, Boolean> extractFields(String input) {
        Map<String, Boolean> map = Maps.newHashMap();
        for (String s : input.split(",")) {
            String trimmed = s.trim();
            map.put(trimmed, true);
        }
        return map;
    }

    private void writeNewUser(String userId, String name, String email, String bio, Map<String, Boolean> preferences, Map<String, Boolean> restrictions) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setBio(bio);
        user.setPreferences(preferences);
        user.setRestrictions(restrictions);

        //Creates personal pantry and return keys to store it with
        //String pantryKey = createPersonalPantry();
       // user.setPersonalPantry(pantryKey);
        
        FirebaseDatabase.getInstance().getReference().child("userAccounts").child(userId).setValue(user);

    }

    private String createPersonalPantry(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //Create entry for a personal pantry:
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.pantriesData));
        Pantry newPantry = new Pantry();
        HashMap<String, Boolean> ownedBy = new HashMap<>();
        ownedBy.put(uid, true);
        newPantry.setOwnedBy(ownedBy);
        newPantry.setShared(false);
        String key = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.pantriesData)).push().getKey();
        ref.child(key).setValue(newPantry);

        //Returns pantry key
        return key;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
