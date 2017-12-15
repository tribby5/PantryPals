package pantrypals.activities;

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
import com.google.common.collect.Maps;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import pantrypals.models.Pantry;
import pantrypals.models.User;

public class AccountCreationActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    //UI components
    private EditText newUserFirstName;
    private EditText newUserLastName;
    private EditText newUserBio;
    private EditText newUserPreferences;
    private EditText newUserRestrictions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation);

        newUserFirstName = (EditText) findViewById(R.id.TFnameFirst);
        newUserLastName = (EditText) findViewById(R.id.TFnameLast);
        newUserBio = (EditText) findViewById(R.id.TFbio);
        newUserPreferences = (EditText) findViewById(R.id.TFpreferences);
        newUserRestrictions = (EditText) findViewById(R.id.TFrestrictions);

        setOnClick();
    }

    private void setOnClick() {
        Button Bregister = (Button) findViewById(R.id.BCreate);
        Bregister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //Set default errors
                newUserBio.setError(null);
                newUserPreferences.setError(null);
                newUserRestrictions.setError(null);

                View focusView = null;

                //Currently not reason to cancel
                if(false){
                    focusView.requestFocus();
                }

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String displayName = newUserFirstName.getText().toString() + " " + newUserLastName.getText().toString();
                String bio = newUserBio.getText().toString();
                Map<String, Boolean> preferences = extractFields(newUserPreferences.getText().toString());
                Map<String, Boolean> restrictions = extractFields(newUserRestrictions.getText().toString());
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(displayName).build();
                user.updateProfile(profileUpdates);


                writeNewUser(user.getUid(), displayName, FirebaseAuth.getInstance().getCurrentUser().getEmail(), bio, preferences, restrictions);

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
        Log.d("WORKS", "FIRING");
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setBio(bio);
        user.setPreferences(preferences);
        user.setRestrictions(restrictions);

        //Creates personal pantry and return keys to store it with
        String pantryKey = createPersonalPantry();
        user.setPersonalPantry(pantryKey);

        FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.userAccounts)).child(userId).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("WORKS", "USER ID " + FirebaseAuth.getInstance().getCurrentUser().getUid());
                Log.d("WORKS", "Successfully registered");
                Intent mainIntent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(mainIntent);
            }
        });

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



}
