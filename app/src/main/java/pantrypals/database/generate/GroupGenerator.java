package pantrypals.database.generate;

import android.util.Log;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import pantrypals.database.query.QueryMaker;
import pantrypals.models.Group;
import pantrypals.models.User;

/**
 * Created by adityasrinivasan on 07/12/17.
 */

public class GroupGenerator {

    private static final String TAG = "GroupGenerator";

    private static Random r = new Random();
    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public static void gen() {
        Log.d(TAG, "Generating groups...");
        final List<String> names = Lists.newArrayList(
                "Dessert Junkies",
                "Lebanese Lovers",
                "Mexican Munchers",
                "Parents United",
                "Fit Freaks",
                "Fine Diners",
                "Boys Who Cook");

        mDatabase.child("/userAccounts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (String gname : names) {
                    Group group = new Group();

                    Map<String, Boolean> members = Maps.newHashMap();
                    double selectivity = r.nextDouble() * 0.5 + 0.25;       // number between 0.25 and 0.75

                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        String userId = childSnapshot.getKey();

                        if (r.nextDouble() > selectivity) {
                            members.put(userId, true);
                        }
                    }

                    group.setName(gname);
                    group.setMembers(members);

                    String gid = UUID.nameUUIDFromBytes(gname.getBytes()).toString().replace("-", "");
                    mDatabase.child("/groups").child(gid).setValue(group);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "The read failed: " + databaseError.getMessage());
            }
        });



        Log.d(TAG, "Done generating groups!");
    }
}
