package pantrypals.auth;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.databaes.pantrypals.R;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountCreationFragment extends Fragment {

    private FirebaseAuth mAuth;

    //UI components
    private EditText newUserFirstName;
    private EditText newUserLastName;
    private EditText newUserBio;
    private EditText newUserPreferences;
    private EditText newUserRestrictions;


    public AccountCreationFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_account_creation, container, false);

        newUserFirstName = (EditText) view.findViewById(R.id.TFnameFirst);
        newUserLastName = (EditText) view.findViewById(R.id.TFnameLast);
        newUserBio = (EditText) view.findViewById(R.id.TFbio);
        newUserPreferences = (EditText) view.findViewById(R.id.TFpreferences);
        newUserRestrictions = (EditText) view.findViewById(R.id.TFrestrictions);

        setOnClick(view);

        return view;
    }

    private void setOnClick(View view) {
        Button Bregister = (Button) view.findViewById(R.id.Bregister);
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
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setBio(bio);
        user.setPreferences(preferences);
        user.setRestrictions(restrictions);

        //Creates personal pantry and return keys to store it with
        String pantryKey = createPersonalPantry();
        user.setPersonalPantry(pantryKey);

        FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.userAccounts)).child(userId).setValue(user);

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
