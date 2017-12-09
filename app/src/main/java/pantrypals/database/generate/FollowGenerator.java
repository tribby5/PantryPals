package pantrypals.database.generate;

import android.util.Log;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import pantrypals.models.Group;
import pantrypals.models.User;

/**
 * Created by adityasrinivasan on 07/12/17.
 */

public class FollowGenerator implements Generator {

    private static final String TAG = "FollowGenerator";

    private static Random r = new Random();
    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public void gen() {
        Log.d(TAG, "Generating follows...");

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

        final double celebrityFollowsNormalProbability = 0.1;
        final double celebrityFollowsCelebrityProbability = 0.6;
        final double normalFollowsCelebrityProbability = 0.9;
        final double normalFollowsNormalProbability = 0.3;
        final double followType = 0.5;

        mDatabase.child("/userAccounts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot follower : dataSnapshot.getChildren()) {
                    String followerName = follower.getValue(User.class).getName();

                    for (DataSnapshot followee : dataSnapshot.getChildren()) {
                        if (follower == followee) {
                            continue;
                        }
                        double sample = r.nextDouble();
                        String followeeName = followee.getValue(User.class).getName();
                        if(celebrities.contains(followerName)) {
                            if(celebrities.contains(followeeName)) {
                                if(sample < celebrityFollowsCelebrityProbability) {
                                    String type;
                                    if(r.nextDouble() < followType) {
                                        type = "all";
                                    } else {
                                        type = "relevant";
                                    }
                                    mDatabase.child("/follows").child(follower.getKey()).child(followee.getKey()).setValue(type);
                                }
                            } else {
                                if(sample < celebrityFollowsNormalProbability) {
                                    String type;
                                    if(r.nextDouble() < followType) {
                                        type = "all";
                                    } else {
                                        type = "relevant";
                                    }
                                    mDatabase.child("/follows").child(follower.getKey()).child(followee.getKey()).setValue(type);
                                }
                            }
                        } else {
                            if(celebrities.contains(followeeName)) {
                                if(sample < normalFollowsCelebrityProbability) {
                                    String type;
                                    if(r.nextDouble() < followType) {
                                        type = "all";
                                    } else {
                                        type = "relevant";
                                    }
                                    mDatabase.child("/follows").child(follower.getKey()).child(followee.getKey()).setValue(type);
                                }
                            } else {
                                if(sample < normalFollowsNormalProbability) {
                                    String type;
                                    if(r.nextDouble() < followType) {
                                        type = "all";
                                    } else {
                                        type = "relevant";
                                    }
                                    mDatabase.child("/follows").child(follower.getKey()).child(followee.getKey()).setValue(type);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "The read failed: " + databaseError.getMessage());
            }
        });



        Log.d(TAG, "Done generating follows!");
    }
}
