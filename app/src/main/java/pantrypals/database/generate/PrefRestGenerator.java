package pantrypals.database.generate;

import android.util.Log;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.Random;
import java.util.Set;

import pantrypals.models.User;

/**
 * Created by adityasrinivasan on 08/12/17.
 */

public class PrefRestGenerator implements Generator {

    private static final String TAG = "PrefRestGenerator";
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private static Random r = new Random();

    public void gen() {
        Log.d(TAG, "Generating preferences/restrictions...");

        mDatabase.child("/userAccounts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    final User user = userSnapshot.getValue(User.class);

                    mDatabase.child("/preferences").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot outerSnapshot) {
                            Map<String, Boolean> prefs = Maps.newHashMap();
                            double selectivity = r.nextDouble() * 0.3 + 0.4;
                            for (DataSnapshot prefSnapshot : outerSnapshot.getChildren()) {
                                if (r.nextDouble() < selectivity) {
                                    prefs.put(prefSnapshot.getKey(), true);
                                }
                            }
                            user.setPreferences(prefs);
                            mDatabase.child("/restrictions").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot outerSnapshot) {
                                    Map<String, Boolean> restricts = Maps.newHashMap();
                                    double selectivity = r.nextDouble() * 0.1 + 0.05;
                                    for (DataSnapshot restrictSnapshot : outerSnapshot.getChildren()) {
                                        if(r.nextDouble() < selectivity) {
                                            restricts.put(restrictSnapshot.getKey(), true);
                                        }
                                    }
                                    user.setRestrictions(restricts);
                                    mDatabase.child("/userAccounts").child(userSnapshot.getKey()).setValue(user);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "The read failed: " + databaseError.getMessage());
            }
        });



        Log.d(TAG, "Done generating preferences/restrictions!");
    }

}
