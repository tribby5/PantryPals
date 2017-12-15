package pantrypals.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.databaes.pantrypals.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.Maps;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wefika.flowlayout.FlowLayout;

import java.util.HashMap;
import java.util.Map;

import pantrypals.models.Pantry;
import pantrypals.models.User;

public class AccountCreationActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAnalytics mFirebaseAnalytics;
    private static final int GALLERY_INTENT = 2;
    //UI components
    private EditText newUserFirstName;
    private EditText newUserLastName;
    private EditText newUserPreferences;
    private EditText newUserRestrictions;
    private EditText newUserBio;
    private FlowLayout newUserPreferencesFlowLayout;
    private FlowLayout newUserRestrictionsFlowLayout;
    private Button newUserAvatarButton;
    private Map<String, Boolean> preferences = Maps.newHashMap();
    private Map<String, Boolean> restrictions = Maps.newHashMap();
    private String avatar;


    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    private StorageReference mStorage;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        newUserFirstName = (EditText) findViewById(R.id.TFnameFirst);
        newUserLastName = (EditText) findViewById(R.id.TFnameLast);
        newUserBio = (EditText) findViewById(R.id.TFbio);
        newUserPreferencesFlowLayout = (FlowLayout) findViewById(R.id.reg_prefs_flow_layout);
        newUserRestrictionsFlowLayout = (FlowLayout) findViewById(R.id.reg_restrictions_flow_layout);
        newUserAvatarButton = (Button) findViewById(R.id.uploadAvatarButton);
        mStorage = FirebaseStorage.getInstance().getReference();
        mProgressDialog = new ProgressDialog(this);
        newUserAvatarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });


        addPrefs();
        addRestrictions();


        setOnClick();
    }

    private void setOnClick() {
        Button Bregister = (Button) findViewById(R.id.BCreate);
        Bregister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //Set default errors
                newUserBio.setError(null);

                View focusView = null;

                //Currently not reason to cancel
                if(false){
                    focusView.requestFocus();
                }

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String displayName = newUserFirstName.getText().toString() + " " + newUserLastName.getText().toString();
                String bio = newUserBio.getText().toString();
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(displayName).build();
                user.updateProfile(profileUpdates);

                writeNewUser(user.getUid(), displayName, FirebaseAuth.getInstance().getCurrentUser().getEmail(), bio, preferences, restrictions, avatar);

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



    private void writeNewUser(String userId, String name, String email, String bio, Map<String, Boolean> preferences, Map<String, Boolean> restrictions, String avatar) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setBio(bio);
        user.setPreferences(preferences);
        user.setRestrictions(restrictions);
        user.setAvatar(avatar);

        //Creates personal pantry and return keys to store it with
        String pantryKey = createPersonalPantry();
        user.setPersonalPantry(pantryKey);

        FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.userAccounts)).child(userId).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("WORKS", "USER ID " + FirebaseAuth.getInstance().getCurrentUser().getUid());
                Log.d("WORKS", "Successfully registered");
                log(FirebaseAnalytics.Event.SIGN_UP, FirebaseAuth.getInstance().getCurrentUser().getUid());
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

    private void addPrefs() {
        mRef.child("preferences").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot prefSnapshot : dataSnapshot.getChildren()) {
                    final String pref = prefSnapshot.getKey();
                    final TextView prefText = new TextView(AccountCreationActivity.this);
                    prefText.setText(pref);
                    prefText.setTextSize(14);
                    prefText.setPadding(25, 15, 25, 15);
                    prefText.setTextColor(getResources().getColor(R.color.colorGrayText));
                    prefText.setBackground(getResources().getDrawable(R.drawable.rounded_corner_gray_bg));

                    FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(10,10,10,10);

                    prefText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(preferences.containsKey(pref)) {
                                preferences.remove(pref);
                                prefText.setTextColor(getResources().getColor(R.color.colorGrayText));
                                prefText.setBackground(getResources().getDrawable(R.drawable.rounded_corner_gray_bg));
                            } else {
                                preferences.put(pref, true);
                                prefText.setTextColor(getResources().getColor(R.color.colorPurpleFade));
                                prefText.setBackground(getResources().getDrawable(R.drawable.rounded_corner_purple_fade));
                            }
                        }
                    });

                    prefText.setLayoutParams(params);
                    newUserPreferencesFlowLayout.addView(prefText);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addRestrictions() {
        mRef.child("restrictions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot prefSnapshot : dataSnapshot.getChildren()) {
                    final String pref = prefSnapshot.getKey();
                    final TextView prefText = new TextView(AccountCreationActivity.this);
                    prefText.setText(pref);
                    prefText.setTextSize(14);
                    prefText.setPadding(25, 15, 25, 15);
                    prefText.setTextColor(getResources().getColor(R.color.colorGrayText));
                    prefText.setBackground(getResources().getDrawable(R.drawable.rounded_corner_gray_bg));

                    FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(10,10,10,10);

                    prefText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(restrictions.containsKey(pref)) {
                                restrictions.remove(pref);
                                prefText.setTextColor(getResources().getColor(R.color.colorGrayText));
                                prefText.setBackground(getResources().getDrawable(R.drawable.rounded_corner_gray_bg));
                            } else {
                                restrictions.put(pref, true);
                                prefText.setTextColor(getResources().getColor(R.color.colorPurpleFade));
                                prefText.setBackground(getResources().getDrawable(R.drawable.rounded_corner_purple_fade));
                            }
                        }
                    });

                    prefText.setLayoutParams(params);
                    newUserRestrictionsFlowLayout.addView(prefText);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    private void changeUploadButtonText() {
        newUserAvatarButton.setText("AVATAR UPLOADED");
    }

    private void log(String eventType, String value) {
        Bundle bundle = new Bundle();
        bundle.putString(eventType, value);
        mFirebaseAnalytics.logEvent("HomeFragment", bundle);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            mProgressDialog.setMessage("Uploading image...");
            mProgressDialog.show();
            Uri uri = data.getData();
            StorageReference filepath = mStorage.child("RecipeImages").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    avatar = taskSnapshot.getDownloadUrl().toString();
                    toastMessage("Image upload succeeded.");
                    mProgressDialog.dismiss();

                    // change the button text to uploaded
                    changeUploadButtonText();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    toastMessage("Image upload failed. Please try again.");
                    mProgressDialog.dismiss();
                }
            });
        }
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
