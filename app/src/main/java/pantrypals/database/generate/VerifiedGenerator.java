package pantrypals.database.generate;

import android.util.Log;

import com.google.common.collect.Sets;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Set;

import pantrypals.models.User;

/**
 * Created by adityasrinivasan on 08/12/17.
 */

public class VerifiedGenerator implements Generator {

    private static final String TAG = "VerifiedGenerator";
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public void gen() {
        Log.d(TAG, "Generating verified...");

        final Set<String> celebrities = Sets.newHashSet("Gordon Ramsay",
                "Martha Stewart",
                "Bobby Flay",
                "Wolfgang Puck",
                "Action Bronson",
                "Rachael Ray",
                "Mario Batali",
                "Jamie Oliver",
                "Adriano Zumbo",
                "Anthony Bourdain",
                "Barack Obama",
                "Betty Crocker",
                "Snoop Dogg");

        mDatabase.child("/userAccounts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if(celebrities.contains(user.getName())) {
                        user.setVerified(true);
                    } else {
                        user.setVerified(false);
                    }
                    mDatabase.child("/userAccounts").child(userSnapshot.getKey()).setValue(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "The read failed: " + databaseError.getMessage());
            }
        });



        Log.d(TAG, "Done generating verified!");
    }
}
